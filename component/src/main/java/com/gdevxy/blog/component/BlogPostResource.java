package com.gdevxy.blog.component;

import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import com.gdevxy.blog.component.cookie.Cookies;
import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.CaptchaProtectedAction;
import com.gdevxy.blog.model.Image;
import com.gdevxy.blog.model.contentful.Node;
import com.gdevxy.blog.service.contentful.ContentfulAssetService;
import com.gdevxy.blog.service.contentful.blogpost.BlogPostService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Path("/blog-posts")
@RequiredArgsConstructor
public class BlogPostResource {

	private final BlogPostService blogPostService;
	private final ContentfulAssetService contentfulAssetService;

	@GET
	@Path("/{slug}")
	@Produces(MediaType.TEXT_HTML)
	public Uni<TemplateInstance> blogPost(HttpServerRequest req, @Valid @Size(max = 255) @PathParam("slug") String slug,
		@QueryParam("previewToken") String previewToken) {

		var userId = Cookies.findSessionCookie(req).map(Cookie::getValue).orElseThrow();
		var blogPost = blogPostService.findBlogPost(userId, previewToken, slug);
		var images = blogPost.onItem().transformToMulti(p -> Multi.createFrom().iterable(p.getBlocks()))
			.filter(b -> b.getNode() == Node.EMBEDDED_ENTRY)
			.map(BlogPost.ContentBlock::getValue)
			.onItem().transformToUniAndConcatenate(id -> contentfulAssetService.findImage(id, previewToken))
			.collect()
			.with(Collectors.toUnmodifiableMap(Image::getId, i -> i));

		return Uni.combine().all().unis(blogPost, images).with(Templates::blogPost);
	}

	@POST
	@Path("/{id}/thumbs-up")
	public Uni<Void> thumbsUp(HttpServerRequest req, @Valid @Size(max = 22) @PathParam("id") String key, @Valid CaptchaProtectedAction action) {

		var userId = Cookies.findSessionCookie(req).map(Cookie::getValue).orElseThrow();

		return blogPostService.thumbsUp(userId, key, action);
	}

	@POST
	@Path("/{id}/thumbs-down")
	public Uni<Void> thumbsDown(HttpServerRequest req, @Valid @Size(max = 22) @PathParam("id") String key) {

		return Cookies.findSessionCookie(req)
			.map(Cookie::getValue)
			.map(userId -> blogPostService.thumbsDown(userId, key))
			.orElseThrow();
	}

	@CheckedTemplate
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Templates {

		public static native TemplateInstance blogPost(BlogPost blogPost, Map<String, Image> images);

	}

}
