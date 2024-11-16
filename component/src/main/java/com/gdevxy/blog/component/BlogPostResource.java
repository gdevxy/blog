package com.gdevxy.blog.component;

import io.quarkus.qute.Engine;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

@Blocking
@Path("/blog-posts")
@RequiredArgsConstructor
public class BlogPostResource {

	private final Engine engine;

	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance blog(@Valid @Size(max = 25) @PathParam("id") String id) {

		return engine.getTemplate("%s/%s".formatted(BlogPostResource.class.getSimpleName(), id)).instance();
	}

}
