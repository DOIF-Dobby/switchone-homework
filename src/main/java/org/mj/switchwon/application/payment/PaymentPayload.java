package org.mj.switchwon.application.payment;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.mj.switchwon.domain.wallet.Currency;

/**
 * 결제 승인 요청 DTO
 */
public record PaymentPayload(@NotBlank String userId,
							 @NotNull BigDecimal amount,
							 @NotNull Currency currency,
							 @NotBlank String merchantId,
							 @NotBlank String paymentMethod,
							 @Valid @NotNull PaymentDetailPayload paymentDetails) {

	public record PaymentDetailPayload(@NotBlank String cardNumber,
									   @NotBlank String expiryDate,
									   @NotBlank String cvv) {

	}
}
