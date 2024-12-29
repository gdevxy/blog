package com.gdevxy.blog.component;

import java.util.Map;
import java.util.Optional;
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
import jakarta.ws.rs.core.Response;

import com.gdevxy.blog.component.cookie.Cookies;
import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.BlogPostRateReq;
import com.gdevxy.blog.model.Image;
import com.gdevxy.blog.model.contentful.Node;
import com.gdevxy.blog.service.contentful.ContentfulAssetService;
import com.gdevxy.blog.service.contentful.blogpost.BlogPostService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
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

		var blogPost = blogPostService.findBlogPost(previewToken, slug);
		var images = blogPost.onItem().transformToMulti(p -> Multi.createFrom().iterable(p.getBlocks()))
			.filter(b -> b.getNode() == Node.EMBEDDED_ENTRY)
			.map(BlogPost.ContentBlock::getValue)
			.onItem().transformToUniAndConcatenate(id -> contentfulAssetService.findImage(id, previewToken))
			.collect()
			.with(Collectors.toUnmodifiableMap(Image::getId, i -> i));

		var thumbsUpEnabled = blogPost.map(p -> Cookies.findBlogPostRatingCookie(req, p.getId())).map(Optional::isPresent);

		return Uni.combine().all().unis(blogPost, images, thumbsUpEnabled)
			.with(Templates::blogPost);
	}

	@POST
	@Path("/{id}/thumbs-up")
	public Uni<Response> thumbsUp(@Valid @Size(max = 22) @PathParam("id") String key, @Valid BlogPostRateReq req) {

		return blogPostService.thumbsUp(key, req.captcha())
			.map(uuid -> Response.noContent()
				.cookie(Cookies.createBlogPostRatingCookie(key, uuid))
				.build());
	}

	@POST
	@Path("/{id}/thumbs-down")
	public Uni<Response> thumbsDown(HttpServerRequest req, @Valid @Size(max = 22) @PathParam("id") String key) {

		return Cookies.findBlogPostRatingCookie(req, key)
			.map(c -> blogPostService.thumbsDown(c.getValue()).map(v -> Response.ok().cookie(Cookies.deleteBlogPostRatingCookie(key)).build()))
			.orElseGet(() -> Uni.createFrom().item(Response.noContent().build()));
	}

	@CheckedTemplate
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Templates {

		public static native TemplateInstance blogPost(BlogPost blogPost, Map<String, Image> images, Boolean thumbsUpEnabled);

	}

}
