package org.mj.switchwon.application.payment;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.mj.switchwon.domain.wallet.Currency;

/**
 * 결제 예상 결과 조회 요청 DTO
 */
public record PaymentEstimatePayload(@NotNull BigDecimal amount,
									 @NotNull Currency currency,
									 @NotBlank String merchantId,
									 @NotBlank String userId) {
}
