package org.mj.switchwon.framework.exception;

import java.io.Serial;

/**
 * 비즈니스 관련 Exception
 */
public class BusinessException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = -461451588489989247L;

	public BusinessException(String message) {
		super(message);
	}
}
