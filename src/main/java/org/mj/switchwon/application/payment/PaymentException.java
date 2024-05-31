package org.mj.switchwon.application.payment;

import java.io.Serial;

import org.mj.switchwon.framework.exception.BusinessException;

/**
 * 결제 시 발생하는 Exception
 */
public class PaymentException extends BusinessException {
	@Serial
	private static final long serialVersionUID = -4417388756369434094L;

	public PaymentException(String message) {
		super(message);
	}
}
