package org.mj.switchwon.framework.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * 이벤트를 쉽게 발행할 수 있게 하는 유틸성 클래스
 */
@Component
public class EventPublisher implements ApplicationEventPublisherAware {
	private static ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		EventPublisher.applicationEventPublisher = applicationEventPublisher;
	}

	public static void publishEvent(Object event) {
		if (applicationEventPublisher != null) {
			applicationEventPublisher.publishEvent(event);
		}
	}
}
