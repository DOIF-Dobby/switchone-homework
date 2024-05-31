package org.mj.switchwon.domain.exchange;

import java.io.Serial;

import org.mj.switchwon.domain.wallet.Currency;
import org.mj.switchwon.framework.exception.BusinessException;

/**
 * 환율을 찾을 수 없을 때 발생하는 Exception
 */
public class ExchangeRateNotFoundException extends BusinessException {

	@Serial
	private static final long serialVersionUID = 8274656432147371051L;

	public ExchangeRateNotFoundException(Currency from, Currency to) {
		super(String.format("Exchange rate not found for currency %s->%s", from, to));
	}
}
