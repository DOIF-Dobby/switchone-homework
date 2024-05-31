package org.mj.switchwon.domain.exchange;

import java.math.BigDecimal;
import java.util.Optional;

import org.mj.switchwon.domain.wallet.Currency;

/**
 * 환율 조회를 담당하는 Interface
 */
public interface ExchangeRateFindService {

	/**
	 * 환율 조회
	 *
	 * @param from from 통화 타입
	 * @param to   to 통화 타입
	 * @return 환율
	 */
	Optional<BigDecimal> getExchangeRate(Currency from, Currency to);
}
