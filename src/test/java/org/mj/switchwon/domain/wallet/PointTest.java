package org.mj.switchwon.domain.wallet;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("포인트 테스트")
class PointTest {

	@Test
	@DisplayName("포인트 동등성 테스트")
	void testEquals() {
		// given
		Point point1 = new Point(new BigDecimal(100));
		Point point2 = Point.of(100);
		Point point3 = Point.of(100.00);
		Point point4 = Point.of("100");
		Point point5 = Point.of(BigDecimal.valueOf(100));

		// when

		// then
		assertThat(point1).isEqualTo(point2);
		assertThat(point1).isEqualTo(point3);
		assertThat(point1).isEqualTo(point4);
		assertThat(point1).isEqualTo(point5);
		assertThat(point1.hashCode()).isEqualTo(point2.hashCode());
	}

	@Test
	@DisplayName("포인트 toString 테스트")
	void testToString() {
		// given
		Point point1 = Point.of("100.52");
		Point point2 = Point.of("100");

		// when

		// then
		assertThat(point1.toString()).isEqualTo("100.52");
		assertThat(point2.toString()).isEqualTo("100");
	}

	@Test
	@DisplayName("포인트 compareTo 테스트")
	void testCompareTo() throws Exception {
		// given
		List<Point> list = List.of(
			Point.of("300"),
			Point.of("500"),
			Point.of("100")
		);

		// when
		List<Point> sortedList = list
			.stream()
			.sorted()
			.toList();

		// then
		assertThat(sortedList).extracting("value")
			.containsExactly(BigDecimal.valueOf(100), BigDecimal.valueOf(300), BigDecimal.valueOf(500));
	}

	@Test
	@DisplayName("포인트 더하기 테스트")
	void add() {
		// given
		Point point1 = Point.of("100");
		Point point2 = Point.of("200");

		// when
		Point addedPoint = point1.add(point2);

		// then
		assertThat(addedPoint).isEqualTo(Point.of("300"));
	}

	@Test
	@DisplayName("포인트 빼기 테스트")
	void subtract() {
		// given
		Point point1 = Point.of("500");
		Point point2 = Point.of("200");

		// when
		Point subtractedPoint = point1.subtract(point2);

		// then
		assertThat(subtractedPoint).isEqualTo(Point.of("300"));
	}

	@Test
	@DisplayName("포인트 곱하기 테스트")
	void multiply() throws Exception {
		// given
		Point point1 = Point.of("500");
		Point point2 = Point.of("3");

		// when
		Point multiplyValue1 = point1.multiply(point2);
		Point multiplyValue2 = point1.multiply(4);

		// then
		assertThat(multiplyValue1).isEqualTo(Point.of("1500"));
		assertThat(multiplyValue2).isEqualTo(Point.of("2000"));
	}

	@Test
	@DisplayName("포인트 나누기 테스트")
	void divide() throws Exception {
		// given
		Point point1 = Point.of("500");
		Point point2 = Point.of("5");

		// when
		Point divideValue1 = point1.divide(point2);
		Point divideValue2 = point1.divide(3);

		// then
		assertThat(divideValue1).isEqualTo(Point.of("100"));
		assertThat(divideValue2).isEqualTo(Point.of("166.67"));
	}

	@Test
	@DisplayName("포인트 같은 금액 테스트")
	void eq() {
		// given
		Point point1 = Point.of("500");
		Point point2 = Point.of("500");
		Point point3 = Point.of("600");
		Point point4 = Point.of("600.00");

		// when

		// then
		assertThat(point1.eq(point2)).isTrue();
		assertThat(point1.eq(point3)).isFalse();
		assertThat(point3.eq(point4)).isTrue();
	}

	@Test
	@DisplayName("포인트 더 큰지 테스트")
	void gt() {
		// given
		Point point1 = Point.of("600");
		Point point2 = Point.of("500");
		Point point3 = Point.of("600");

		// when

		// then
		assertThat(point1.gt(point2)).isTrue();
		assertThat(point1.gt(point3)).isFalse();
	}

	@Test
	@DisplayName("포인트 더 작은지 테스트")
	void lt() {
		// given
		Point point1 = Point.of("400");
		Point point2 = Point.of("500");
		Point point3 = Point.of("400");

		// when

		// then
		assertThat(point1.lt(point2)).isTrue();
		assertThat(point1.lt(point3)).isFalse();
	}

	@Test
	@DisplayName("포인트 크거나 같은지 테스트")
	void goe() {
		// given
		Point point1 = Point.of("400");
		Point point2 = Point.of("400");
		Point point3 = Point.of("500");

		// when

		// then
		assertThat(point1.goe(point2)).isTrue();
		assertThat(point1.goe(point3)).isFalse();
	}

	@Test
	@DisplayName("포인트 작거나 같은지 테스트")
	void loe() {
		// given
		Point point1 = Point.of("400");
		Point point2 = Point.of("400");
		Point point3 = Point.of("300");

		// when

		// then
		assertThat(point1.loe(point2)).isTrue();
		assertThat(point1.loe(point3)).isFalse();
	}

	@Test
	@DisplayName("포인트 소수점 버리기 테스트")
	void truncateDecimal() {
		// given
		Point point = Point.of("400.12345");

		// when
		Point truncatedDecimal = point.truncateDecimal();

		// then
		assertThat(truncatedDecimal).isEqualTo(Point.of("400"));
	}

	@Test
	@DisplayName("포인트 소수점 둘째 자리까지 버리기 테스트")
	void truncateToTwoDecimal() {
		// given
		Point point = Point.of("400.12345");

		// when
		Point truncatedToTwoDecimal = point.truncateToTwoDecimal();

		// then
		assertThat(truncatedToTwoDecimal).isEqualTo(Point.of("400.12"));
	}

}