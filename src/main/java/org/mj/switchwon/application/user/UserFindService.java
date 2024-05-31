package org.mj.switchwon.application.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.mj.switchwon.domain.exchange.Exchanger;
import org.mj.switchwon.domain.user.User;
import org.mj.switchwon.domain.user.UserRepository;
import org.mj.switchwon.domain.wallet.Currency;
import org.mj.switchwon.domain.wallet.Point;

/**
 * 사용자 관련 조회 Service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFindService {

	private final UserRepository userRepository;
	private final Exchanger exchanger;

	/**
	 * 사용자 잔액 조회 API
	 *
	 * @param userId   사용자 ID
	 * @param currency 요청 통화 타입
	 * @return 잔액 조회 결과
	 */
	public UserBalanceResponse getUserBalance(String userId, Currency currency) {
		// 사용자 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException(userId));

		// 사용자가 가진 지갑의 잔액들의 총합을 구함. 총 합을 구할 때, 클라이언트가 요청한 통화 타입으로 변환함.
		Point totalPoint = user.getWallets()
			.stream()
			.map(wallet -> exchanger.exchange(wallet.getBalance(), wallet.getCurrency(), currency))
			.reduce(Point.ZERO, Point::add);

		// 통화 타입에 따른 소수점이 제거된 포인트
		Point truncatePoint = currency.truncatePointDecimal(totalPoint);

		return new UserBalanceResponse(
			user.getId(),
			truncatePoint.getValue(),
			currency
		);
	}
}
