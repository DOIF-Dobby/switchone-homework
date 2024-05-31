package org.mj.switchwon.domain.wallet.charging;

import org.mj.switchwon.domain.wallet.Point;
import org.mj.switchwon.domain.wallet.Wallet;

/**
 * 지갑에 포인트 충전 시 발생하는 Event
 */
public record ChargingEvent(Wallet wallet, Point chargingAmount, Point balance) {
}
