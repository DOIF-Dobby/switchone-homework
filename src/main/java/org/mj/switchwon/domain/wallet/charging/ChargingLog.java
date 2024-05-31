package org.mj.switchwon.domain.wallet.charging;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.mj.switchwon.domain.wallet.Point;
import org.mj.switchwon.domain.wallet.Wallet;
import org.mj.switchwon.framework.jpa.BaseEntity;

/**
 * 지갑 충전 이력을 나타내는 Entity
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChargingLog extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "charging_log_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wallet_id")
	private Wallet wallet;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "charging_amount", nullable = false))
	private Point chargingAmount;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "balance", nullable = false))
	private Point balance;

	public ChargingLog(Wallet wallet, Point chargingAmount, Point balance) {
		this.wallet = wallet;
		this.chargingAmount = chargingAmount;
		this.balance = balance;
	}
}
