package org.mj.switchwon.application.payment;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import org.mj.switchwon.domain.payment.PaymentStatus;
import org.mj.switchwon.domain.user.User;
import org.mj.switchwon.domain.user.UserRepository;
import org.mj.switchwon.domain.wallet.BalanceException;
import org.mj.switchwon.domain.wallet.Currency;
import org.mj.switchwon.domain.wallet.Point;
import org.mj.switchwon.domain.wallet.Wallet;
import org.mj.switchwon.domain.wallet.WalletRepository;
import org.mj.switchwon.domain.wallet.charging.ChargingLog;
import org.mj.switchwon.domain.wallet.charging.ChargingLogRepository;
import org.mj.switchwon.domain.wallet.using.UsingLog;
import org.mj.switchwon.domain.wallet.using.UsingLogRepository;

@SpringBootTest
@Transactional
@DisplayName("결제 서비스 테스트")
class PaymentServiceTest {

	@Autowired
	PaymentService paymentService;

	@Autowired
	EntityManager em;

	@Autowired
	WalletRepository walletRepository;

	@Autowired
	UsingLogRepository usingLogRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ChargingLogRepository chargingLogRepository;

	@BeforeEach
	void setUp() {
		User user = new User("user1", "사용자1");
		em.persist(user);

		Wallet usdWallet = new Wallet(user, Currency.USD);
		usdWallet.addBalance(Point.of(100));
		em.persist(usdWallet);
	}

	@Test
	@DisplayName("결제 예상 결과 조회 테스트")
	void estimatePayment() {
		// given
		PaymentEstimatePayload payload =
			new PaymentEstimatePayload(BigDecimal.valueOf(150.00), Currency.USD, "M101", "user1");

		// when
		PaymentEstimateResponse result = paymentService.estimatePayment(payload);

		// then
		assertThat(result.estimatedTotal().compareTo(BigDecimal.valueOf(154.50)) == 0).isTrue();
		assertThat(result.fees().compareTo(BigDecimal.valueOf(4.50)) == 0).isTrue();
		assertThat(result.currency()).isEqualTo(Currency.USD);
	}

	@Test
	@DisplayName("결제 승인 요청 테스트 (잔액이 충분한 경우)")
	void approvalPayment1() {
		// given
		PaymentPayload.PaymentDetailPayload paymentDetailPayload =
			new PaymentPayload.PaymentDetailPayload("1234-5678-9123-4567", "12/24", "123");

		PaymentPayload paymentPayload = new PaymentPayload(
			"user1",
			BigDecimal.valueOf(90),
			Currency.USD,
			"M101",
			"creditCard",
			paymentDetailPayload
		);

		// when
		PaymentResponse paymentResponse = paymentService.approvalPayment(paymentPayload);

		// then
		assertThat(paymentResponse.status()).isEqualTo(PaymentStatus.APPROVED);
		assertThat(paymentResponse.currency()).isEqualTo(Currency.USD);
		assertThat(paymentResponse.amountTotal().compareTo(BigDecimal.valueOf(92.70)) == 0).isTrue();
		assertThat(paymentResponse.timestamp().toLocalDate()).isEqualTo(LocalDate.now());

		User user = userRepository.findById("user1").get();
		Wallet wallet = walletRepository.findByUserAndCurrency(user, Currency.USD);

		// USD 지갑의 잔액은 100 - 92.7 = 7.3
		assertThat(wallet.getBalance()).isEqualTo(Point.of("7.3"));

		// 사용 로그가 쌓임
		List<UsingLog> results = usingLogRepository.findAll();
		assertThat(results).hasSize(1);

		UsingLog usingLog = results.get(0);
		assertThat(usingLog.getUsingAmount()).isEqualTo(Point.of("92.7"));
		assertThat(usingLog.getBalance()).isEqualTo(Point.of("7.3"));
		assertThat(usingLog.getPayment().getId()).isEqualTo(paymentResponse.paymentId());
		assertThat(usingLog.getWallet()).isEqualTo(wallet);
	}

	@Test
	@DisplayName("결제 승인 요청 테스트 (잔액이 충분하지 않은 경우)")
	void approvalPayment2() throws Exception {
		// given
		PaymentPayload.PaymentDetailPayload paymentDetailPayload =
			new PaymentPayload.PaymentDetailPayload("1234-5678-9123-4567", "12/24", "123");

		PaymentPayload paymentPayload = new PaymentPayload(
			"user1",
			BigDecimal.valueOf(200),
			Currency.USD,
			"M101",
			"creditCard",
			paymentDetailPayload
		);

		// when
		PaymentResponse paymentResponse = paymentService.approvalPayment(paymentPayload);

		// then
		assertThat(paymentResponse.status()).isEqualTo(PaymentStatus.APPROVED);
		assertThat(paymentResponse.currency()).isEqualTo(Currency.USD);
		assertThat(paymentResponse.amountTotal().compareTo(BigDecimal.valueOf(206.00)) == 0).isTrue();
		assertThat(paymentResponse.timestamp().toLocalDate()).isEqualTo(LocalDate.now());

		User user = userRepository.findById("user1").get();
		Wallet wallet = walletRepository.findByUserAndCurrency(user, Currency.USD);

		assertThat(wallet.getBalance()).isEqualTo(Point.of(1));

		// 사용 로그가 쌓임
		List<UsingLog> usingLogs = usingLogRepository.findAll();
		assertThat(usingLogs).hasSize(1);

		UsingLog usingLog = usingLogs.get(0);
		assertThat(usingLog.getUsingAmount()).isEqualTo(Point.of("206.00"));
		assertThat(usingLog.getBalance()).isEqualTo(Point.of("1"));
		assertThat(usingLog.getPayment().getId()).isEqualTo(paymentResponse.paymentId());
		assertThat(usingLog.getWallet()).isEqualTo(wallet);

		// 충전 로그가 쌓임
		List<ChargingLog> chargingLogs = chargingLogRepository.findAll()
			.stream()
			.sorted(Comparator.comparing(ChargingLog::getId))
			.toList();

		assertThat(chargingLogs).hasSize(2);

		ChargingLog chargingLog = chargingLogs.get(1);
		assertThat(chargingLog.getChargingAmount()).isEqualTo(Point.of("107.00"));
		assertThat(chargingLog.getBalance()).isEqualTo(Point.of("207.00"));
	}

	@Test
	@DisplayName("결제 승인 요청 테스트 (다른 지갑에서 환전하여 결제)")
	void approvalPayment3() throws Exception {
		// given
		User user = userRepository.findById("user1").get();
		Wallet krwWallet = new Wallet(user, Currency.KRW);
		krwWallet.addBalance(Point.of(50000));
		em.persist(krwWallet);

		PaymentPayload.PaymentDetailPayload paymentDetailPayload =
			new PaymentPayload.PaymentDetailPayload("1234-5678-9123-4567", "12/24", "123");

		PaymentPayload paymentPayload = new PaymentPayload(
			"user1",
			BigDecimal.valueOf(120),
			Currency.USD,
			"M101",
			"creditCard",
			paymentDetailPayload
		);

		// when
		PaymentResponse paymentResponse = paymentService.approvalPayment(paymentPayload);

		// then
		assertThat(paymentResponse.status()).isEqualTo(PaymentStatus.APPROVED);
		assertThat(paymentResponse.currency()).isEqualTo(Currency.USD);
		assertThat(paymentResponse.amountTotal().compareTo(BigDecimal.valueOf(123.60)) == 0).isTrue();
		assertThat(paymentResponse.timestamp().toLocalDate()).isEqualTo(LocalDate.now());

		Wallet usdWallet = walletRepository.findByUserAndCurrency(user, Currency.USD);
		assertThat(usdWallet.getBalance()).isEqualTo(Point.ZERO);
		assertThat(krwWallet.getBalance()).isEqualTo(Point.of("17502"));

		// 사용 로그가 쌓임
		List<UsingLog> usingLogs = usingLogRepository.findAll()
			.stream()
			.sorted(Comparator.comparing(usingLog -> usingLog.getWallet().getCurrency() == Currency.USD ? 0 : 1))
			.toList();

		assertThat(usingLogs).hasSize(2);

		UsingLog usingLog1 = usingLogs.get(0);
		assertThat(usingLog1.getUsingAmount()).isEqualTo(Point.of("100"));
		assertThat(usingLog1.getBalance()).isEqualTo(Point.of("0"));
		assertThat(usingLog1.getPayment().getId()).isEqualTo(paymentResponse.paymentId());
		assertThat(usingLog1.getWallet()).isEqualTo(usdWallet);

		UsingLog usingLog2 = usingLogs.get(1);
		assertThat(usingLog2.getUsingAmount()).isEqualTo(Point.of("32497.20"));
		assertThat(usingLog2.getBalance()).isEqualTo(Point.of("17502"));
		assertThat(usingLog2.getPayment().getId()).isEqualTo(paymentResponse.paymentId());
		assertThat(usingLog2.getWallet()).isEqualTo(krwWallet);
	}

	@Test
	@DisplayName("결제 승인 요청 테스트 (통화 타입에 해당하는 지갑이 없을 때)")
	void approvalPayment4() throws Exception {
		// given
		PaymentPayload.PaymentDetailPayload paymentDetailPayload =
			new PaymentPayload.PaymentDetailPayload("1234-5678-9123-4567", "12/24", "123");

		PaymentPayload paymentPayload = new PaymentPayload(
			"user1",
			BigDecimal.valueOf(500000),
			Currency.KRW,
			"M101",
			"creditCard",
			paymentDetailPayload
		);

		// when
		BalanceException exception =
			assertThrows(BalanceException.class, () -> paymentService.approvalPayment(paymentPayload));

		// then
		assertThat(exception.getMessage()).isEqualTo("Please create a wallet first. Currency: KRW");
	}
}