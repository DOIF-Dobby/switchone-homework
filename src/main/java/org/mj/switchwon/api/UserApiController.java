package org.mj.switchwon.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mj.switchwon.application.user.UserBalanceResponse;
import org.mj.switchwon.application.user.UserFindService;
import org.mj.switchwon.domain.wallet.Currency;

/**
 * 사용자 관련 API Controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class UserApiController {

	private final UserFindService userFindService;

	/**
	 * 사용자 잔액 조회 API
	 *
	 * @param userId   사용자 ID
	 * @param currency 요청 통화 타입
	 * @return 잔액 조회 결과
	 */
	@GetMapping("/balance/{userId}")
	public UserBalanceResponse getUserBalance(@PathVariable String userId,
											  @RequestParam(required = false, defaultValue = "USD") Currency currency) {
		return userFindService.getUserBalance(userId, currency);
	}
}
