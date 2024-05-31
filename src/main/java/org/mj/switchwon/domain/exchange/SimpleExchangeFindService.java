package org.mj.switchwon.domain.exchange;

import static org.mj.switchwon.domain.wallet.Currency.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.mj.switchwon.domain.wallet.Currency;

/**
 * 간단하게 고정 환율을 조회하는 Service
 */
public class SimpleExchangeFindService implements ExchangeRateFindService {

	private final List<SimpleExchange> simpleExchanges = List.of(
		new SimpleExchange(USD, KRW, new BigDecimal("1377.00")),
		new SimpleExchange(KRW, USD, BigDecimal.ONE.divide(new BigDecimal("1377.00"), 10, RoundingMode.HALF_UP))
	);

	/**
	 * 환율 조회
	 *
	 * @param from from 통화 타입
	 * @param to   to 통화 타입
	 * @return 환율
	 */
	@Override
	public Optional<BigDecimal> getExchangeRate(Currency from, Currency to) {
		return simpleExchanges
			.stream()
			.filter((exchange) -> exchange.getFrom() == from && exchange.getTo() == to)
			.map((SimpleExchange::getRate))
			.findAny();
	}

	@Getter
	@AllArgsConstructor
	public static class SimpleExchange {
		private Currency from;
		private Currency to;
		private BigDecimal rate;
	}
}
