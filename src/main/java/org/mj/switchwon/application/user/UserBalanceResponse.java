package org.mj.switchwon.application.user;

import java.math.BigDecimal;

import org.mj.switchwon.domain.wallet.Currency;

/**
 * 사용자 잔액 조회 응답 DTO
 */
public record UserBalanceResponse(String userId, BigDecimal balance, Currency currency) {
}
