package org.mj.switchwon.application.payment;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import org.mj.switchwon.domain.wallet.charging.ChargingEvent;
import org.mj.switchwon.domain.wallet.charging.ChargingLog;
import org.mj.switchwon.domain.wallet.charging.ChargingLogRepository;

/**
 * 지갑에 포인트 충전 시 발생하는 이벤트 핸들러
 */
@Component
@RequiredArgsConstructor
public class ChargingEventHandler {

	private final ChargingLogRepository chargingLogRepository;

	/**
	 * 지갑에 포인트 충전 시 발생하는 이벤트 핸들링하는 메서드
	 *
	 * @param event 이벤트
	 */
	@EventListener(ChargingEvent.class)
	public void handle(ChargingEvent event) {
		// CHARGING_LOG 테이블에 충전 이력을 쌓는다.
		ChargingLog chargingLog = new ChargingLog(event.wallet(), event.chargingAmount(), event.balance());
		chargingLogRepository.save(chargingLog);
	}
}
