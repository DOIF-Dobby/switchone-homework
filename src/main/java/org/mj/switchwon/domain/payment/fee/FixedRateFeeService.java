package org.mj.switchwon.domain.payment.fee;

import org.mj.switchwon.domain.wallet.Point;
import org.mj.switchwon.domain.wallet.PointNotValidException;

/**
 * 고정 수수료율로 수수료를 계산하는 Service
 */
public class FixedRateFeeService implements CalculateFeeService {

	@Override
	public Point calculateFee(Point point) {
		if (point == null) {
			throw new PointNotValidException("Point is required");
		}

		final int fixedRate = 3;
		return point.multiply(fixedRate).divide(100);
	}
}
