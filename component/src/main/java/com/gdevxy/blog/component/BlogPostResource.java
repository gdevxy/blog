package com.gdevxy.blog.component;

import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.service.contentful.blogpost.BlogPostService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.Engine;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Blocking
@Path("/blog-posts")
@RequiredArgsConstructor
public class BlogPostResource {

	private final Engine engine;
	private final BlogPostService blogPostService;

	@GET
	@Path("/{slug}")
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance blogPost(@Valid @Size(max = 255) @PathParam("slug") String slug, @DefaultValue("false") @QueryParam("preview") Boolean preview) {

		return blogPostService.findBlogPost(preview, slug)
				.map(Templates::blogPost)
				.orElseGet(() -> engine.getTemplate("notFound.html").instance());
	}

	@CheckedTemplate
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Templates {

		public static native TemplateInstance blogPost(BlogPost blogPost);

	}

}
