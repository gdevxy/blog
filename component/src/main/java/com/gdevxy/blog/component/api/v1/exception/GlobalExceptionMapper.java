package com.gdevxy.blog.component.api.v1.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {
		log.error("API Error: ", exception);

		if (exception instanceof ConstraintViolationException) {
			return handleValidationException((ConstraintViolationException) exception);
		}

		if (exception instanceof NotFoundException) {

			return Response.status(Response.Status.NOT_FOUND)
					.type(MediaType.APPLICATION_JSON)
					.entity(ApiError.of(Response.Status.NOT_FOUND, "NOT_FOUND", exception.getMessage()))
					.build();
		}

		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.type(MediaType.APPLICATION_JSON)
				.entity(ApiError.of(Response.Status.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", exception.getMessage()))
				.build();
	}

	private Response handleValidationException(ConstraintViolationException e) {

		var message = e.getConstraintViolations()
				.stream()
				.map(ConstraintViolation::getMessage)
				.reduce((a, b) -> a + "; " + b)
				.orElse("Validation failed");

		return Response.status(Response.Status.BAD_REQUEST)
				.type(MediaType.APPLICATION_JSON)
				.entity(ApiError.of(Response.Status.BAD_REQUEST, "VALIDATION_ERROR", message))
				.build();
	}

}
