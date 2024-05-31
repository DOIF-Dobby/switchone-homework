package org.mj.switchwon.application.user;

import java.io.Serial;

import org.mj.switchwon.framework.exception.BusinessException;

/**
 * 사용자를 찾을 수 없을 때 발생하는 Exception
 */
public class UserNotFoundException extends BusinessException {
	@Serial
	private static final long serialVersionUID = 4172717110119352640L;

	public UserNotFoundException(String userId) {
		super(String.format("User with id [%s] not found", userId));
	}
}
