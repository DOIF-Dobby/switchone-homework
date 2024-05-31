package org.mj.switchwon.domain.payment.fee;

import org.mj.switchwon.domain.wallet.Point;

/**
 * 결제 수수료 계산을 담당하는 Interface
 */
public interface CalculateFeeService {
	Point calculateFee(Point point);
}
