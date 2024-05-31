package org.mj.switchwon.domain.wallet;

import org.springframework.data.jpa.repository.JpaRepository;

import org.mj.switchwon.domain.user.User;

/**
 * 지갑 Repository
 */
public interface WalletRepository extends JpaRepository<Wallet, Long> {

	Wallet findByUserAndCurrency(User user, Currency currency);
}
