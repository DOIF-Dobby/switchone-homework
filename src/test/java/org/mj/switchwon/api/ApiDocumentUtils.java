package org.mj.switchwon.api;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.UriModifyingOperationPreprocessor;

public class ApiDocumentUtils {
	private static final String SCHEME = "http";
	private static final String HOST = "localhost";
	private static final Integer PORT = 8080;

	public static OperationRequestPreprocessor getDocumentRequest() {
		UriModifyingOperationPreprocessor preprocessor = modifyUris()
			.scheme(SCHEME)
			.host(HOST)
			.port(PORT);

		return preprocessRequest(preprocessor, prettyPrint());
	}

	public static OperationResponsePreprocessor getDocumentResponse() {
		return preprocessResponse(prettyPrint());   // 문서의 response를 예쁘게 출력하기 위해 사용
	}
}
