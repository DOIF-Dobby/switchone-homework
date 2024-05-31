package org.mj.switchwon.application.payment;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.mj.switchwon.application.user.UserBalanceResponse;
import org.mj.switchwon.application.user.UserFindService;
import org.mj.switchwon.application.user.UserNotFoundException;
import org.mj.switchwon.domain.exchange.Exchanger;
import org.mj.switchwon.domain.payment.Payment;
import org.mj.switchwon.domain.payment.PaymentDetail;
import org.mj.switchwon.domain.payment.PaymentRepository;
import org.mj.switchwon.domain.payment.fee.CalculateFeeService;
import org.mj.switchwon.domain.user.User;
import org.mj.switchwon.domain.user.UserRepository;
import org.mj.switchwon.domain.wallet.Currency;
import org.mj.switchwon.domain.wallet.Point;
import org.mj.switchwon.domain.wallet.Wallet;
import org.mj.switchwon.domain.wallet.using.UsingLog;
import org.mj.switchwon.domain.wallet.using.UsingLogRepository;
import org.mj.switchwon.infrastructure.CreditCardService;

/**
 * 결제 관련 Service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final CalculateFeeService calculateFeeService;
	private final UserRepository userRepository;
	private final UserFindService userFindService;
	private final Exchanger exchanger;
	private final CreditCardService creditCardService;
	private final UsingLogRepository usingLogRepository;

	/**
	 * 결제 예상 결과 조회
	 *
	 * @param payload 결제 예상 결과 조회 Payload
	 * @return 결제 예상 결과
	 */
	public PaymentEstimateResponse estimatePayment(PaymentEstimatePayload payload) {
		Currency currency = payload.currency();
		Point amount = Point.of(payload.amount());
		Point fees = calculateFeeService.calculateFee(amount);
		Point estimatedTotal = amount.add(fees);

		Point truncateFees = currency.truncatePointDecimal(fees);
		Point truncateTotal = currency.truncatePointDecimal(estimatedTotal);

		return new PaymentEstimateResponse(truncateTotal.getValue(), truncateFees.getValue(), currency);
	}

	/**
	 * 결제 승인 요청
	 *
	 * @param payload 결제 승인 요청 Payload
	 * @return 결제 승인 요청 결과
	 */
	public PaymentResponse approvalPayment(PaymentPayload payload) {
		String userId = payload.userId();
		Point amount = Point.of(payload.amount());
		Currency currency = payload.currency();
		String merchantId = payload.merchantId();
		String paymentMethod = payload.paymentMethod();
		PaymentPayload.PaymentDetailPayload paymentDetailPayload = payload.paymentDetails();

		PaymentDetail paymentDetail = new PaymentDetail(
			paymentDetailPayload.cardNumber(),
			paymentDetailPayload.expiryDate(),
			paymentDetailPayload.cvv()
		);

		// 사용자 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException(userId));

		// 사용자가 가진 포인트 총 잔액 조회
		UserBalanceResponse userBalance = userFindService.getUserBalance(user.getId(), currency);
		Point balanceTotal = Point.of(userBalance.balance());

		// 수수료
		Point fees = calculateFeeService.calculateFee(amount);
		// 총 지불 금액
		Point amountTotal = amount.add(fees);

		// 결제 정보 저장
		Payment payment = new Payment(user, amount, fees, currency, merchantId, paymentMethod, paymentDetail);

		// 사용자가 가진 포인트 잔액이 "총 지불 금액 이상"이라면 그냥 결제 할 수 있다.
		if (balanceTotal.goe(amountTotal)) {
			// 사용자의 지갑에서 잔액을 사용하여 결제 한다.
			useUserBalance(user, currency, amountTotal, payment);

		} else { // 사용자가 가진 포인트 잔액이 "총 지불 금액 미만"이라면 부족분을 충전하여 결제한다.
			// 부족한 금액
			Point insufficientAmount = amountTotal.subtract(balanceTotal).add(currency.getDefaultChargingPoint());

			// 신용 카드를 이용하여 부족한 금액 결제
			boolean isSuccess = creditCardService.approvalPay(
				insufficientAmount.getValue(),
				paymentDetail.getCardNumber(),
				paymentDetail.getExpiryDate(),
				paymentDetail.getCvv()
			);

			// 결제 실패 시, Exception
			if (!isSuccess) {
				throw new PaymentException("Payment is failed");
			}

			// 통화 타입에 맞는 지갑을 찾아서 부족한 금액을 포인트로 충전한다.
			user.chargingWalletBalance(currency, insufficientAmount);

			// 사용자의 지갑에서 잔액을 사용하여 결제 한다.
			useUserBalance(user, currency, amountTotal, payment);
		}

		// 결제 정보 저장
		Payment savedPayment = paymentRepository.save(payment);

		return new PaymentResponse(
			savedPayment.getId(),
			savedPayment.getStatus(),
			currency.truncatePointDecimal(amountTotal).getValue(),
			savedPayment.getCurrency(),
			savedPayment.getCreatedDate()
		);
	}

	/**
	 * 사용자 지갑에서 잔액을 사용하여 결제 하고 사용 이력을 쌓는다.
	 *
	 * @param user        사용자
	 * @param currency    통화 타입
	 * @param amountTotal 결제할 금액
	 * @param payment     결제 정보
	 */
	private void useUserBalance(User user, Currency currency, Point amountTotal, Payment payment) {
		// 사용자의 지갑에서 잔액을 사용하여 결제 한다.
		Map<Wallet, Point> usedWalletsBalance = user.useWalletsBalance(currency, amountTotal, exchanger);

		// 사용된 지갑과 포인트를 사용 이력으로 저장
		for (Wallet wallet : usedWalletsBalance.keySet()) {
			Point point = usedWalletsBalance.get(wallet);
			UsingLog usingLog = new UsingLog(wallet, payment, point, wallet.getBalance());
			usingLogRepository.save(usingLog);
		}
	}
}
