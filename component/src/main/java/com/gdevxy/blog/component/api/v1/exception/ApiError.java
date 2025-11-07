package com.gdevxy.blog.component.api.v1.exception;

import jakarta.ws.rs.core.Response;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
@ToString
public class ApiError {

	private Integer statusCode;
	private String type;
	private String message;
	@Builder.Default
	private Instant timestamp = Instant.now();

	public static ApiError of(Response.Status status, String type, String message) {
		return ApiError.builder().statusCode(status.getStatusCode()).type(type).message(message).build();
	}

}
