package org.mj.switchwon.application.user;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

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

@SpringBootTest
@Transactional
@DisplayName("사용자 조회 서비스 테스트")
class UserFindServiceTest {

	@Autowired
	UserFindService userFindService;

	@Autowired
	EntityManager em;

	@BeforeEach
	void setUp() {
		User user = new User("user1", "사용자1");
		em.persist(user);

		Wallet usdWallet = new Wallet(user, Currency.USD);
		usdWallet.addBalance(Point.of(100));
		em.persist(usdWallet);

		Wallet krwWallet = new Wallet(user, Currency.KRW);
		krwWallet.addBalance(Point.of(10000));
		em.persist(krwWallet);
	}

	@Test
	@DisplayName("사용자 잔액 조회 테스트")
	void getUserBalance() {
		// given

		// when
		UserBalanceResponse usdResult = userFindService.getUserBalance("user1", Currency.USD);
		UserBalanceResponse krwResult = userFindService.getUserBalance("user1", Currency.KRW);

		// then
		assertThat(usdResult.userId()).isEqualTo("user1");
		assertThat(usdResult.balance()).isEqualTo(BigDecimal.valueOf(107.26)); // 100 + (1/1377 * 10000)
		assertThat(usdResult.currency()).isEqualTo(Currency.USD);

		assertThat(krwResult.userId()).isEqualTo("user1");
		assertThat(krwResult.balance()).isEqualTo(BigDecimal.valueOf(147700)); // (100 * 1377) + 10000
		assertThat(krwResult.currency()).isEqualTo(Currency.KRW);
	}
}