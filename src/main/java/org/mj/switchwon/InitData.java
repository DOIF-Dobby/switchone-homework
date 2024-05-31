package org.mj.switchwon;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.mj.switchwon.domain.user.User;
import org.mj.switchwon.domain.wallet.Currency;
import org.mj.switchwon.domain.wallet.Point;
import org.mj.switchwon.domain.wallet.Wallet;

/**
 * "local" 프로필로 실행했을 때 특정 데이터 삽입을 위한 클래스
 */
@Profile("local")
@Component
@RequiredArgsConstructor
@Transactional
public class InitData implements CommandLineRunner {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void run(String... args) throws Exception {
		User user1 = new User("user1", "사용자1");

		Wallet wallet1 = new Wallet(user1, Currency.KRW);
		wallet1.addBalance(Point.of(70000));

		Wallet wallet2 = new Wallet(user1, Currency.USD);
		wallet2.addBalance(Point.of(150));

		em.persist(user1);
		em.persist(wallet1);
		em.persist(wallet2);
	}
}
