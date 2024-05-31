package org.mj.switchwon.domain.wallet;

import java.io.Serial;

import org.mj.switchwon.framework.exception.BusinessException;

/**
 * 잔액 관련 Exception
 */
public class BalanceException extends BusinessException {
	@Serial
	private static final long serialVersionUID = 3356234038924702938L;

	public BalanceException(String message) {
		super(message);
	}
}
