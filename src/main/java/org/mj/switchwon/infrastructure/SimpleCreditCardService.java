package org.mj.switchwon.infrastructure;

import java.math.BigDecimal;

public class SimpleCreditCardService implements CreditCardService {

	@Override
	public boolean approvalPay(BigDecimal paymentAmount, String cardNumber, String expiryDate, String cvv) {
		// 결제 성공하면 true, 아니면 false
		return true;
	}
}
