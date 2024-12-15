package com.gdevxy.blog.component;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.service.contentful.blogpost.BlogPostService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Blocking
@Path("/blog-posts")
@RequiredArgsConstructor
public class BlogPostResource {

	private final BlogPostService blogPostService;

	@GET
	@Path("/{slug}")
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance blogPost(@Valid @Size(max = 255) @PathParam("slug") String slug, @QueryParam("previewToken") String previewToken) {

		return blogPostService.findBlogPost(previewToken, slug)
			.map(Templates::blogPost)
			.orElseThrow(() -> new NotFoundException("BlogPost [%s] not found".formatted(slug)));
	}

	@CheckedTemplate
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Templates {

		public static native TemplateInstance blogPost(BlogPost blogPost);

	}

}
