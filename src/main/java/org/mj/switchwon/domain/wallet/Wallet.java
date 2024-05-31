package org.mj.switchwon.domain.wallet;

import java.util.Objects;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.mj.switchwon.domain.user.User;
import org.mj.switchwon.domain.wallet.charging.ChargingEvent;
import org.mj.switchwon.framework.event.EventPublisher;
import org.mj.switchwon.framework.jpa.BaseEntity;

/**
 * 지갑 Entity
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_wallet_01", columnNames = {"user_id", "currency"})
	}
)
public class Wallet extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "wallet_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "currency", nullable = false)
	private Currency currency;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "balance", nullable = false))
	private Point balance;

	public Wallet(User user, Currency currency) {
		this.user = user;
		this.currency = currency;
		this.balance = Point.ZERO;

		user.getWallets().add(this);
	}

	/**
	 * 잔액 조회
	 * 잔액 조회 시, 통화 타입에 맞게 소수점은 버린다.
	 *
	 * @return 포인트
	 */
	public Point getBalance() {
		return currency.truncatePointDecimal(balance);
	}

	/**
	 * 잔액 증가
	 *
	 * @param point 포인트
	 * @return 증가된 포인트
	 */
	public Point addBalance(Point point) {
		this.balance = balance.add(point);

		// 충전 이벤트 발행
		EventPublisher.publishEvent(new ChargingEvent(this, point, balance));

		return balance;
	}

	/**
	 * 잔액을 사용한다.
	 *
	 * @param point 사용할 포인트
	 * @return 사용된 잔액
	 */
	public Point useBalance(Point point) {
		// 사용하려는 금액이 0이하면 그대로 Zero를 반환한다.
		if (point.lt(Point.ZERO)) {
			return Point.ZERO;
		}

		// 현재 잔액에서 사용할 포인트를 사용하고 남은 잔액
		Point remainPoint = balance.subtract(point);

		// 사용할 금액
		// 남은 잔액이 0 이상이면 현재 지갑에서 모두 처리할 수 있는 금액이므로 "사용할 포인트"를 그대로 사용 하도록 한다.
		// 그렇지 않으면 사용할 포인트가 현재 지갑에서 처리할 수 없는 큰 금액이므로 "지갑 잔액"을 모두 사용 하도록 한다.
		Point toUseAmount = remainPoint.goe(Point.ZERO) ? point : balance;

		// 사용할 금액은 현재 잔액을 초과할 수 없다.
		if (toUseAmount.gt(balance)) {
			throw new PointNotValidException("Point cannot be less than zero.");
		}

		// 현재 잔액에서 사용할 금액을 빼서 잔액으로 설정한다.
		this.balance = balance.subtract(toUseAmount);
		// 사용된 금액
		return toUseAmount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
