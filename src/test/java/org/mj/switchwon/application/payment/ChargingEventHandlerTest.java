package org.mj.switchwon.application.payment;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import org.mj.switchwon.domain.user.User;
import org.mj.switchwon.domain.wallet.Currency;
import org.mj.switchwon.domain.wallet.Point;
import org.mj.switchwon.domain.wallet.Wallet;
import org.mj.switchwon.domain.wallet.WalletRepository;
import org.mj.switchwon.domain.wallet.charging.ChargingLog;
import org.mj.switchwon.domain.wallet.charging.ChargingLogRepository;

@SpringBootTest
@Transactional
@DisplayName("지갑 충전 이벤트 테스트")
class ChargingEventHandlerTest {

	@Autowired
	EntityManager em;

	@Autowired
	WalletRepository walletRepository;

	@Autowired
	ChargingLogRepository chargingLogRepository;

	@BeforeEach
	void setUp() {
		User user = new User("user1", "사용자1");
		em.persist(user);

		Wallet wallet = new Wallet(user, Currency.USD);
		em.persist(wallet);
	}

	@Test
	@DisplayName("지갑 잔액 증액 시, 충전 로그 쌓는 이벤트 핸들러 테스트")
	void handle() throws Exception {
		// given
		Wallet wallet = walletRepository.findAll().get(0);

		// when
		wallet.addBalance(Point.of(1000));

		// then
		List<ChargingLog> results = chargingLogRepository.findAll();
		assertThat(results).hasSize(1);

		ChargingLog chargingLog = results.get(0);
		assertThat(chargingLog.getWallet()).isEqualTo(wallet);
		assertThat(chargingLog.getChargingAmount()).isEqualTo(Point.of(1000));
		assertThat(chargingLog.getBalance()).isEqualTo(Point.of(1000));
	}
}