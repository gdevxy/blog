package com.gdevxy.blog.component;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Blocking
@Path("/blog")
@RequiredArgsConstructor
public class BlogResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance blog() {

		return Templates.blog();
	}

	@CheckedTemplate
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Templates {

		public static native TemplateInstance blog();

	}

}
