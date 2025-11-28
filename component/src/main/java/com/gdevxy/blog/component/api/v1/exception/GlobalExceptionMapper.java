package com.gdevxy.blog.component.api.v1.exception;

import com.gdevxy.blog.model.UnauthorizedException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
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

		return switch(exception) {
			case ConstraintViolationException ex -> toValidationErrorResponse(ex);
			case NotFoundException ex -> toResponse(Response.Status.NOT_FOUND, ex);
			case UnauthorizedException ex -> toResponse(Response.Status.UNAUTHORIZED, ex);
			default -> {
				log.error("Internal Server Error: {}", exception.getMessage(), exception);
				yield toResponse(Response.Status.INTERNAL_SERVER_ERROR, exception);
			}
		};
	}

	private Response toResponse(Response.Status status, Exception ex) {

		return Response.status(status)
				.type(MediaType.APPLICATION_JSON)
				.entity(ApiError.of(status, status.name(), ex.getMessage()))
				.build();
	}

	private Response toValidationErrorResponse(ConstraintViolationException e) {

		var message = e.getConstraintViolations()
				.stream()
				.map(ConstraintViolation::getMessage)
				.reduce((a, b) -> a + "; " + b)
				.orElse("Validation failed");

		return Response.status(Response.Status.BAD_REQUEST)
				.type(MediaType.APPLICATION_JSON)
				.entity(ApiError.of(Response.Status.BAD_REQUEST, "BAD_REQUEST", message))
				.build();
	}

}
