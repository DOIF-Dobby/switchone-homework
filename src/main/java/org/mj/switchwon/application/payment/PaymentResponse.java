package org.mj.switchwon.application.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.mj.switchwon.domain.payment.PaymentStatus;
import org.mj.switchwon.domain.wallet.Currency;

/**
 * 결제 승인 요청 결과 응답 DTO
 */
public record PaymentResponse(Long paymentId, PaymentStatus status, BigDecimal amountTotal, Currency currency,
							  LocalDateTime timestamp) {
}
