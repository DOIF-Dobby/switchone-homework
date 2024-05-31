package org.mj.switchwon.domain.wallet;

import java.util.function.Function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 통화 타입
 */
@RequiredArgsConstructor
public enum Currency {
	KRW(Point::truncateDecimal, Point.of(1000)),
	USD(Point::truncateToTwoDecimal, Point.of(1));

	private final Function<Point, Point> truncatePointFunction;

	@Getter
	private final Point defaultChargingPoint;

	/**
	 * 포인트를 받아서 통화 타입에 맞게 소수점 처리를 하는 메서드.
	 * KRW -> 소수점 제거
	 * USD -> 소수점 둘째 자리까지 남김
	 *
	 * @param point 포인트
	 * @return 소수점 처리된 포인트
	 */
	public Point truncatePointDecimal(Point point) {
		return truncatePointFunction.apply(point);
	}
}
