package org.mj.switchwon.domain.exchange;

import static org.assertj.core.api.Assertions.*;
import static org.mj.switchwon.domain.wallet.Currency.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mj.switchwon.domain.wallet.Point;

@DisplayName("환전 테스트")
class ExchangerTest {

	ExchangeRateFindService exchangeRateFindService = new SimpleExchangeFindService();
	Exchanger exchanger = new Exchanger(exchangeRateFindService);

	@Test
	@DisplayName("달러 -> 원화 환전 테스트")
	void exchangeUsdToKrw() throws Exception {
		// given
		Point point1 = Point.of("1");
		Point point2 = Point.of("1.25");
		Point point3 = Point.of("0.34");

		// when
		Point exchangedPoint1 = exchanger.exchange(point1, USD, KRW);
		Point exchangedPoint2 = exchanger.exchange(point2, USD, KRW);
		Point exchangedPoint3 = exchanger.exchange(point3, USD, KRW);

		// then
		assertThat(exchangedPoint1).isEqualTo(Point.of("1377.00"));
		assertThat(exchangedPoint2).isEqualTo(Point.of("1721.25"));
		assertThat(exchangedPoint3).isEqualTo(Point.of("468.18"));
	}

	@Test
	@DisplayName("원화 -> 달러 환전 테스트")
	void exchangeKrwToUsd() throws Exception {
		// given
		Point point1 = Point.of("1377.00");
		Point point2 = Point.of("1000");
		Point point3 = Point.of("35445");

		// when
		Point exchangedPoint1 = exchanger.exchange(point1, KRW, USD);
		Point exchangedPoint2 = exchanger.exchange(point2, KRW, USD);
		Point exchangedPoint3 = exchanger.exchange(point3, KRW, USD);

		// then
		assertThat(exchangedPoint1).isEqualTo(Point.of("1.00"));
		assertThat(exchangedPoint2).isEqualTo(Point.of("0.73"));
		assertThat(exchangedPoint3).isEqualTo(Point.of("25.74"));
	}

}