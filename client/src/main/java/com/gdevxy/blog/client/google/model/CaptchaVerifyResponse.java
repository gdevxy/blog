package com.gdevxy.blog.client.google.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@ToString
public class CaptchaVerifyResponse {

	@Builder.Default
	private final Boolean success = false;
	@JsonProperty("error-codes")
	private final List<String> errorCodes;
	private final String hostname;

}
