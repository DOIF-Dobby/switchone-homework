package org.mj.switchwon.domain.user;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.springframework.data.domain.Persistable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.mj.switchwon.domain.exchange.Exchanger;
import org.mj.switchwon.domain.wallet.BalanceException;
import org.mj.switchwon.domain.wallet.Currency;
import org.mj.switchwon.domain.wallet.Point;
import org.mj.switchwon.domain.wallet.Wallet;
import org.mj.switchwon.framework.jpa.BaseEntity;

/**
 * 사용자 정보 Entity
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity implements Persistable<String> {

	@Id
	@Column(name = "user_id")
	private String id;

	@Column(name = "user_name", nullable = false)
	private String name;

	@OneToMany(mappedBy = "user")
	private List<Wallet> wallets = new ArrayList<>();

	public User(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * 지갑들을 순회하면서 지갑의 잔액으로 요청금액을 처리한다.
	 *
	 * @param currency  통화 타입
	 * @param amount    요청 금액
	 * @param exchanger 환전기
	 * @return 사용된 지갑과 포인트 정보
	 */
	public Map<Wallet, Point> useWalletsBalance(Currency currency, Point amount, Exchanger exchanger) {
		// 사용된 지갑과 포인트 정보
		Map<Wallet, Point> usedWalletInfo = new HashMap<>();

		// 사용자의 지갑 순서를 정렬한다. (결제 요청한 통화 타입이 먼저 정렬되도록)
		List<Wallet> sortedWallets = wallets
			.stream()
			.sorted(Comparator.comparing(wallet -> wallet.getCurrency() == currency ? 0 : 1))
			.toList();

		// 남아 있는 결제 요청 금액.
		// 지갑에서 잔액을 사용하면 이 금액은 점점 줄어든다.
		Point remainAmount = amount;
		// 전 지갑 통화 타입. 처음에는 요청 타입과 동일하다
		Currency previousCurrency = currency;

		for (Wallet wallet : sortedWallets) {
			// 통화 타입이 같으면 남은 결제 요청 금액을 그대로 사용하고, 다르면 지갑의 통화 타입으로 환전하여 계산한다.
			remainAmount = exchanger.exchange(remainAmount, previousCurrency, wallet.getCurrency());

			// 금액을 사용하면 실제 사용된 금액을 반환한다.
			// 200달러를 사용한다고 했을 때, 지갑 잔액이 300달러가 남았다면 200달러를 반환한다.
			// 200달러를 사용한다고 했을 때, 지갑 잔액이 150달러가 남았다면 150달러를 반환한다.
			Point usedBalance = wallet.useBalance(remainAmount);

			// 남은 결제 요청 금액을 업데이트 한다.
			remainAmount = remainAmount.subtract(usedBalance);

			// 반환할 Map에 저장
			usedWalletInfo.put(wallet, usedBalance);

			// 결제 요청 금액을 모두 처리 했으면 다음 지갑을 볼 필요 없이 결제 처리를 할 수 있다.
			if (remainAmount.eq(Point.ZERO)) {
				break;
			}

			previousCurrency = wallet.getCurrency();
		}

		// 혹시라도 남은 결제 요청금액을 다 처리하지 못했다면 Exception
		if (remainAmount.gt(Point.ZERO)) {
			throw new BalanceException("Insufficient balance");
		}

		return usedWalletInfo;
	}

	/**
	 * 통화 타입에 맞는 지갑을 찾아서 포인트를 충전한다.
	 *
	 * @param currency 통화 타입
	 * @param amount   충전할 금액
	 */
	public void chargingWalletBalance(Currency currency, Point amount) {
		// 통화 타입에 맞는 지갑 조회
		Wallet currencyWallet = wallets
			.stream()
			.filter(wallet -> wallet.getCurrency() == currency)
			.findAny()
			.orElseThrow(() -> new BalanceException("Please create a wallet first. Currency: " + currency));

		// 부족한 금액 충전
		currencyWallet.addBalance(amount);

	}

	@Override
	public boolean isNew() {
		return getCreatedBy() == null;
	}
}
