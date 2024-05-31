package org.mj.switchwon.api;

import static org.mj.switchwon.api.ApiDocumentUtils.*;
import static org.mj.switchwon.domain.payment.PaymentStatus.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.mj.switchwon.application.payment.PaymentEstimatePayload;
import org.mj.switchwon.application.payment.PaymentEstimateResponse;
import org.mj.switchwon.application.payment.PaymentPayload;
import org.mj.switchwon.application.payment.PaymentResponse;
import org.mj.switchwon.application.payment.PaymentService;
import org.mj.switchwon.domain.wallet.Currency;

@DisplayName("결제 관련 API Controller 테스트")
@WebMvcTest(PaymentApiController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class PaymentApiControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	PaymentService paymentService;

	@Test
	@DisplayName("POST /api/payment/estimate")
	void estimatePayment() throws Exception {
		// given
		PaymentEstimatePayload payload =
			new PaymentEstimatePayload(BigDecimal.valueOf(150.00), Currency.USD, "M101", "user1");

		PaymentEstimateResponse response =
			new PaymentEstimateResponse(BigDecimal.valueOf(154.50), BigDecimal.valueOf(4.50), Currency.USD);

		given(paymentService.estimatePayment(any(PaymentEstimatePayload.class)))
			.willReturn(response);

		// when
		ResultActions result = mockMvc.perform(
			post("/api/payment/estimate")
				.content(objectMapper.writeValueAsBytes(payload))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8));

		// then
		result.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(response)))
			.andDo(print())
			.andDo(document("payment-estimate",
				getDocumentRequest(),
				getDocumentResponse(),
				requestFields(
					fieldWithPath("amount").type(NUMBER).description("지불 금액"),
					fieldWithPath("currency").type(STRING).description("통화 타입"),
					fieldWithPath("merchantId").type(STRING).description("상점 ID"),
					fieldWithPath("userId").type(STRING).description("사용자 ID")
				),
				responseFields(
					fieldWithPath("estimatedTotal").type(NUMBER).description("결제 예상 금액"),
					fieldWithPath("fees").type(NUMBER).description("수수료"),
					fieldWithPath("currency").type(STRING).description("통화 타입")
				)
			));
	}

	@Test
	@DisplayName("POST /api/payment/approval")
	void approvalPayment() throws Exception {
		// given
		PaymentPayload.PaymentDetailPayload paymentDetailPayload =
			new PaymentPayload.PaymentDetailPayload("1234-5678-9123-4567", "12/24", "123");

		PaymentPayload payload = new PaymentPayload(
			"user1",
			BigDecimal.valueOf(150.00),
			Currency.USD,
			"M101",
			"creditCard",
			paymentDetailPayload
		);

		PaymentResponse response =
			new PaymentResponse(1L, APPROVED, BigDecimal.valueOf(154.50), Currency.USD, LocalDateTime.now());

		given(paymentService.approvalPayment(any(PaymentPayload.class)))
			.willReturn(response);

		// when
		ResultActions result = mockMvc.perform(
			post("/api/payment/approval")
				.content(objectMapper.writeValueAsBytes(payload))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8));

		// then
		result.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(response)))
			.andDo(print())
			.andDo(document("payment-approval",
				getDocumentRequest(),
				getDocumentResponse(),
				requestFields(
					fieldWithPath("userId").type(STRING).description("사용자 ID"),
					fieldWithPath("amount").type(NUMBER).description("지불 금액"),
					fieldWithPath("currency").type(STRING).description("통화 타입"),
					fieldWithPath("merchantId").type(STRING).description("상점 ID"),
					fieldWithPath("paymentMethod").type(STRING).description("결제 수단"),
					fieldWithPath("paymentDetails.cardNumber").type(STRING).description("카드 번호"),
					fieldWithPath("paymentDetails.expiryDate").type(STRING).description("카드 만료 일자"),
					fieldWithPath("paymentDetails.cvv").type(STRING).description("카드 CVV")
				),
				responseFields(
					fieldWithPath("paymentId").type(NUMBER).description("결제 ID"),
					fieldWithPath("status").type(STRING).description("결제 상태"),
					fieldWithPath("amountTotal").type(NUMBER).description("총 지불 금액"),
					fieldWithPath("currency").type(STRING).description("통화 타입"),
					fieldWithPath("timestamp").type(STRING).description("결제 일시")
				)
			));
	}
}