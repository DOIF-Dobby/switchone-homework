package org.mj.switchwon.api;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mj.switchwon.application.payment.PaymentEstimatePayload;
import org.mj.switchwon.application.payment.PaymentEstimateResponse;
import org.mj.switchwon.application.payment.PaymentPayload;
import org.mj.switchwon.application.payment.PaymentResponse;
import org.mj.switchwon.application.payment.PaymentService;

/**
 * 결제 관련 API Controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentApiController {

	private final PaymentService paymentService;

	/**
	 * 결제 예상 결과 조회 API
	 *
	 * @param payload 결제 예상 결과 조회 Payload
	 * @return 결제 예상 결과
	 */
	@PostMapping("/estimate")
	public PaymentEstimateResponse estimatePayment(@Valid @RequestBody PaymentEstimatePayload payload) {
		return paymentService.estimatePayment(payload);
	}

	/**
	 * 결제 승인 요청 API
	 *
	 * @param payload 결제 승인 요청 Payload
	 * @return 결제 승인 요청 결과
	 */
	@PostMapping("/approval")
	public PaymentResponse approvalPayment(@Valid @RequestBody PaymentPayload payload) {
		return paymentService.approvalPayment(payload);
	}
}
