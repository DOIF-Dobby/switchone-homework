package org.mj.switchwon.domain.wallet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 지갑의 포인트를 나타내는 값 타입
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point implements Comparable<Point> {

	@Column
	private BigDecimal value;

	public Point(BigDecimal value) {
		if (value == null) {
			throw new PointNotValidException("Point value is required");
		}

		this.value = value;
	}

	public static Point of(BigDecimal value) {
		return new Point(value);
	}

	public static Point of(String value) {
		return new Point(new BigDecimal(value));
	}

	public static Point of(double value) {
		return new Point(new BigDecimal(value));
	}

	public static Point of(int value) {
		return new Point(new BigDecimal(value));
	}

	public static Point ZERO = new Point(BigDecimal.ZERO);

	/**
	 * 포인트 더하기
	 *
	 * @param point 포인트
	 * @return 결과 포인트
	 */
	public Point add(Point point) {
		if (point == null) {
			throw new PointNotValidException("Point is required");
		}

		return new Point(value.add(point.value));
	}

	/**
	 * 포인트 빼기
	 *
	 * @param point 포인트
	 * @return 결과 포인트
	 */
	public Point subtract(Point point) {
		if (point == null) {
			throw new PointNotValidException("Point is required");
		}

		return new Point(value.subtract(point.value));
	}

	/**
	 * 포인트 곱하기
	 *
	 * @param point 포인트
	 * @return 결과 포인트
	 */
	public Point multiply(Point point) {
		if (point == null) {
			throw new PointNotValidException("Point is required");
		}

		return new Point(value.multiply(point.value).setScale(2, RoundingMode.HALF_UP));
	}

	/**
	 * 포인트 곱하기
	 *
	 * @param multiplyValue 곱할 정수
	 * @return 결과 포인트
	 */
	public Point multiply(int multiplyValue) {
		return new Point(value.multiply(new BigDecimal(multiplyValue).setScale(2, RoundingMode.HALF_UP)));
	}

	/**
	 * 포인트 곱하기
	 *
	 * @param multiplyValue 곱할 BigDecimal
	 * @return 결과 포인트
	 */
	public Point multiply(BigDecimal multiplyValue) {
		if (multiplyValue == null) {
			throw new PointNotValidException("multiplyValue is required");
		}

		return new Point(value.multiply(multiplyValue).setScale(2, RoundingMode.HALF_UP));
	}

	/**
	 * 포인트 나누기
	 * 소수점 둘째 자리에서 반올림
	 *
	 * @param point 포인트
	 * @return 결과 포인트
	 */
	public Point divide(Point point) {
		if (point == null) {
			throw new PointNotValidException("Point is required");
		}

		return new Point(value.divide(point.value, 2, RoundingMode.HALF_UP));
	}

	/**
	 * 포인트 나누기
	 * 소수점 둘째 자리에서 반올림
	 *
	 * @param divideValue 나눌 정수
	 * @return 결과 포인트
	 */
	public Point divide(int divideValue) {
		return new Point(value.divide(new BigDecimal(divideValue), 2, RoundingMode.HALF_UP));
	}

	/**
	 * 포인트 나누기
	 * 소수점 둘째 자리에서 반올림
	 *
	 * @param divideValue BigDecimal
	 * @return 결과 포인트
	 */
	public Point divide(BigDecimal divideValue) {
		if (divideValue == null) {
			throw new PointNotValidException("divideValue is required");
		}

		return new Point(value.divide(divideValue, 2, RoundingMode.HALF_UP));
	}

	/**
	 * 포인트 같은지 비교
	 *
	 * @param point 포인트
	 * @return 비교 결과
	 */
	public boolean eq(Point point) {
		if (point == null) {
			return false;
		}
		return this.equals(point);
	}

	/**
	 * 포인트 큰지 비교
	 *
	 * @param point 포인트
	 * @return 비교 결과
	 */
	public boolean gt(Point point) {
		if (point == null) {
			return false;
		}
		return value.compareTo(point.value) > 0;
	}

	/**
	 * 포인트 작은지 비교
	 *
	 * @param point 포인트
	 * @return 비교 결과
	 */
	public boolean lt(Point point) {
		if (point == null) {
			return false;
		}

		return value.compareTo(point.value) < 0;
	}

	/**
	 * 포인트 크거나 같은지 비교
	 *
	 * @param point 포인트
	 * @return 비교 결과
	 */
	public boolean goe(Point point) {
		return eq(point) || gt(point);
	}

	/**
	 * 포인트 작거나 같은지 비교
	 *
	 * @param point 포인트
	 * @return 비교 결과
	 */
	public boolean loe(Point point) {
		return eq(point) || lt(point);
	}

	/**
	 * 소수점 버림
	 *
	 * @return 소수점 버린 포인트
	 */
	Point truncateDecimal() {
		return new Point(value.setScale(0, RoundingMode.DOWN));
	}

	/**
	 * 소수점 둘째 자리까지 버림
	 *
	 * @return 소수점 버린 포인트
	 */
	Point truncateToTwoDecimal() {
		return new Point(value.setScale(2, RoundingMode.DOWN));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		Point point = (Point) obj;

		if (value == null) {
			return point.value == null;
		}

		return value.compareTo(point.value) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public int compareTo(Point point) {
		return value.compareTo(point.value);
	}
}
