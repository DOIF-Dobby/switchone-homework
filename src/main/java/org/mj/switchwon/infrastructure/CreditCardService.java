package org.mj.switchwon.infrastructure;

import java.math.BigDecimal;

/**
 * 신용카드 결제를 담당하는 Interface
 */
public interface CreditCardService {

	boolean approvalPay(BigDecimal paymentAmount, String cardNumber, String expiryDate, String cvv);
}
