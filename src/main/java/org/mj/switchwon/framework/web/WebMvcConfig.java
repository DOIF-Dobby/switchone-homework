package org.mj.switchwon.framework.web;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

/**
 * Spring Web 설정 관련 Config
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig {

	@Bean
	public ObjectMapper apiObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();

		// 특정 필드가 http 요청으로 넘어왔지만 class field에는 없을 경우 실패하게 할지 정하는 옵션
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// http 요청의 특정 필드가 enum과 맵핑되는데, 해당 값이 enum으로 컨버팅 할 수 없으면 NULL 값 처리할 지 여부
		objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

		// objectMapper가 LocalDate, LocalDateTime 타입의 property를 serialize, deserialize 할 때 필요한 옵션
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormat));
		javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormat));
		javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(datetimeFormat));
		javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(datetimeFormat));
		objectMapper.registerModule(javaTimeModule);

		return objectMapper;
	}

	/**
	 * LocalDate 타입을 Serialize
	 */
	static class LocalDateSerializer extends JsonSerializer<LocalDate> {
		private final DateTimeFormatter dateFormat;

		public LocalDateSerializer(DateTimeFormatter dateFormat) {
			this.dateFormat = dateFormat;
		}

		@Override
		public void serialize(LocalDate value,
							  JsonGenerator generator,
							  SerializerProvider serializers) throws IOException {
			generator.writeString(value.format(dateFormat));
		}
	}

	/**
	 * LocalDate 타입을 Deserialize
	 */
	static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
		private final DateTimeFormatter dateFormat;

		public LocalDateDeserializer(DateTimeFormatter dateFormat) {
			this.dateFormat = dateFormat;
		}

		@Override
		public LocalDate deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
			return LocalDate.parse(parser.getValueAsString(), dateFormat);
		}
	}

	/**
	 * LocalDateTime 타입을 Serialize
	 */
	static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
		private final DateTimeFormatter datetimeFormat;

		public LocalDateTimeSerializer(DateTimeFormatter datetimeFormat) {
			this.datetimeFormat = datetimeFormat;
		}

		@Override
		public void serialize(LocalDateTime value,
							  JsonGenerator generator,
							  SerializerProvider serializers) throws IOException {
			generator.writeString(value.format(datetimeFormat));
		}
	}

	/**
	 * LocalDateTime 타입을 Deserialize
	 */
	static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
		private final DateTimeFormatter datetimeFormat;

		public LocalDateTimeDeserializer(DateTimeFormatter datetimeFormat) {
			this.datetimeFormat = datetimeFormat;
		}

		@Override
		public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
			return LocalDateTime.parse(parser.getValueAsString(), datetimeFormat);
		}
	}
}
