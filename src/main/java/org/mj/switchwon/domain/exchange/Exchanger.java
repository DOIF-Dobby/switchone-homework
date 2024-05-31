package org.mj.switchwon.domain.exchange;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import org.mj.switchwon.domain.wallet.Currency;
import org.mj.switchwon.domain.wallet.Point;

/**
 * 환전을 담당하는 Service
 */
@Component
@RequiredArgsConstructor
public class Exchanger {

	private final ExchangeRateFindService exchangeRateFindService;

	public Point exchange(Point point, Currency from, Currency to) {
		if (from == to) {
			return new Point(point.getValue());
		}

		BigDecimal exchangeRate = exchangeRateFindService.getExchangeRate(from, to)
			.orElseThrow(() -> new ExchangeRateNotFoundException(from, to));

		return point.multiply(exchangeRate);
	}
}
