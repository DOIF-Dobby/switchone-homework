package org.mj.switchwon.domain.payment.fee;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mj.switchwon.domain.wallet.Point;

@DisplayName("고정 정률 수수료 계산 서비스 테스트")
class FixedRateFeeServiceTest {

	CalculateFeeService calculateFeeService = new FixedRateFeeService();

	@Test
	@DisplayName("고정 정률 수수료 계산")
	void calculateFee() throws Exception {
		// given
		Point point1 = Point.of("150");
		Point point2 = Point.of("180");

		// when
		Point fee1 = calculateFeeService.calculateFee(point1);
		Point fee2 = calculateFeeService.calculateFee(point2);

		// then
		assertThat(fee1).isEqualTo(Point.of("4.50"));
		assertThat(fee2).isEqualTo(Point.of("5.40"));
	}

}