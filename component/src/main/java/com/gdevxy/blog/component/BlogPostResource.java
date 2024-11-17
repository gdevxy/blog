package com.gdevxy.blog.component;

import io.quarkus.qute.Engine;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
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
	public TemplateInstance blogPost(@Valid @Size(max = 25) @PathParam("id") String id, @DefaultValue("false") @QueryParam("preview") Boolean preview) {

		return engine.getTemplate("%s/%s".formatted(BlogPostResource.class.getSimpleName(), id)).instance();
	}

}
