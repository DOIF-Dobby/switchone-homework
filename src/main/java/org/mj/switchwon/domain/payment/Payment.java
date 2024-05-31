package org.mj.switchwon.domain.payment;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.mj.switchwon.domain.user.User;
import org.mj.switchwon.domain.wallet.Currency;
import org.mj.switchwon.domain.wallet.Point;
import org.mj.switchwon.framework.jpa.BaseEntity;

/**
 * 결제 정보 Entity
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "amount", nullable = false))
	private Point amount;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "fees", nullable = false))
	private Point fees;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "amount_total", nullable = false))
	private Point amountTotal;

	@Enumerated(EnumType.STRING)
	@Column(name = "currency", nullable = false)
	private Currency currency;

	@Column(name = "merchant_id", nullable = false)
	private String merchantId;

	@Column(name = "payment_method", nullable = false)
	private String paymentMethod;

	@Column(name = "status", nullable = false)
	private PaymentStatus status;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "cardNumber", column = @Column(name = "card_number", nullable = false)),
		@AttributeOverride(name = "expiryDate", column = @Column(name = "expiry_date", nullable = false)),
		@AttributeOverride(name = "cvv", column = @Column(name = "cvv", nullable = false))
	})
	private PaymentDetail paymentDetail;

	public Payment(User user, Point amount, Point fees, Currency currency,
				   String merchantId, String paymentMethod, PaymentDetail paymentDetail) {
		this.user = user;
		this.amount = amount;
		this.fees = fees;
		this.amountTotal = amount.add(fees);
		this.currency = currency;
		this.merchantId = merchantId;
		this.paymentMethod = paymentMethod;
		this.paymentDetail = paymentDetail;
		this.status = PaymentStatus.APPROVED;
	}
}
