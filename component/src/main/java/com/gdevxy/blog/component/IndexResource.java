package com.gdevxy.blog.component;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

@Blocking
@Path("/")
@RequiredArgsConstructor
public class IndexResource {

	private final Template base;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance base() {

		return base.instance().data("body", BlogResource.Templates.blog());
	}

}
