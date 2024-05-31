package org.mj.switchwon.framework.jpa;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 관련 Config
 */
@EnableJpaAuditing
@Configuration
public class JapConfig {

	/**
	 * createdBy, lastModifiedBy 자동 주입
	 *
	 * @return 사용자명 Provider
	 */
	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.of("Switchwon");

	}
}
