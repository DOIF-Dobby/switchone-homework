package org.mj.switchwon.api;

import static org.mj.switchwon.api.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

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

import org.mj.switchwon.application.user.UserBalanceResponse;
import org.mj.switchwon.application.user.UserFindService;
import org.mj.switchwon.domain.wallet.Currency;

@DisplayName("사용자 관련 API Controller 테스트")
@WebMvcTest(UserApiController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class UserApiControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	UserFindService userFindService;

	@Test
	@DisplayName("GET /api/payment/balance/{userId}")
	void getUserBalance() throws Exception {
		// given
		UserBalanceResponse response =
			new UserBalanceResponse("user1", BigDecimal.valueOf(150.00), Currency.USD);

		given(userFindService.getUserBalance(any(String.class), any(Currency.class)))
			.willReturn(response);

		// when
		ResultActions result = mockMvc.perform(
			get("/api/payment/balance/{userId}", "user1")
				.contentType(MediaType.APPLICATION_JSON)
				.param("currency", Currency.USD.toString())
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8));

		// then
		result.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(response)))
			.andDo(print())
			.andDo(document("user-balance",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(
					parameterWithName("userId").description("사용자 ID")
				),
				queryParameters(
					parameterWithName("currency").description("통화 타입")
				),
				responseFields(
					fieldWithPath("userId").type(STRING).description("사용자 ID"),
					fieldWithPath("balance").type(NUMBER).description("잔액"),
					fieldWithPath("currency").type(STRING).description("통화 타입")
				)
			));
	}
}