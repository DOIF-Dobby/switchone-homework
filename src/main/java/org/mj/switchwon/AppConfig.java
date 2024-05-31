package org.mj.switchwon;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.mj.switchwon.domain.exchange.ExchangeRateFindService;
import org.mj.switchwon.domain.exchange.SimpleExchangeFindService;
import org.mj.switchwon.domain.payment.fee.CalculateFeeService;
import org.mj.switchwon.domain.payment.fee.FixedRateFeeService;
import org.mj.switchwon.infrastructure.CreditCardService;
import org.mj.switchwon.infrastructure.SimpleCreditCardService;

/**
 * 전반적인 App 설정
 */
@Configuration
public class AppConfig {

	/**
	 * 수수료 계산 서비스 설정
	 *
	 * @return CalculateFeeService
	 */
	@Bean
	public CalculateFeeService calculateFeeService() {
		return new FixedRateFeeService();
	}

	/**
	 * 환율 조회 서비스 설정
	 *
	 * @return ExchangeRateFindService
	 */
	@Bean
	public ExchangeRateFindService exchangeRateFindService() {
		return new SimpleExchangeFindService();
	}

	/**
	 * 신용카드 결제 서비스 설정
	 *
	 * @return CreditCardService
	 */
	@Bean
	public CreditCardService creditCardService() {
		return new SimpleCreditCardService();
	}
}
