package org.mj.switchwon.application.payment;

import java.math.BigDecimal;

import org.mj.switchwon.domain.wallet.Currency;

/**
 * 결제 예상 결과 조회 응답 DTO
 */
public record PaymentEstimateResponse(BigDecimal estimatedTotal, BigDecimal fees, Currency currency) {
}
