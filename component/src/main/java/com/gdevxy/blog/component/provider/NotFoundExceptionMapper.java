package com.gdevxy.blog.component.provider;

import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import io.quarkus.qute.Template;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

	@Inject
	Template notFound;

	@Override
	public Response toResponse(NotFoundException exception) {

		return Response.ok(notFound.render()).type(MediaType.TEXT_HTML).build();
	}

}