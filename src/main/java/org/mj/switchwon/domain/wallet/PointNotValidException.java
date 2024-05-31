package org.mj.switchwon.domain.wallet;

import java.io.Serial;

import org.mj.switchwon.framework.exception.BusinessException;

/**
 * 포인트 관련 Exception
 */
public class PointNotValidException extends BusinessException {
	@Serial
	private static final long serialVersionUID = -166107850918367966L;

	public PointNotValidException(String message) {
		super(message);
	}
}
