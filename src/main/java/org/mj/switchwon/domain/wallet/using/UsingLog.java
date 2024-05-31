package org.mj.switchwon.domain.wallet.using;

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

import org.mj.switchwon.domain.payment.Payment;
import org.mj.switchwon.domain.wallet.Point;
import org.mj.switchwon.domain.wallet.Wallet;

/**
 * 지갑 사용 이력을 나타내는 Entity
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsingLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "using_log_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wallet_id")
	private Wallet wallet;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id")
	private Payment payment;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "using_amount", nullable = false))
	private Point usingAmount;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "balance", nullable = false))
	private Point balance;

	public UsingLog(Wallet wallet, Payment payment, Point usingAmount, Point balance) {
		this.wallet = wallet;
		this.payment = payment;
		this.usingAmount = usingAmount;
		this.balance = balance;
	}
}
