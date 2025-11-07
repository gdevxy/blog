package com.gdevxy.blog.component.api.v1;

import com.gdevxy.blog.component.api.v1.dto.BlogPostDetailDto;
import com.gdevxy.blog.component.api.v1.dto.BlogPostSummaryDto;
import com.gdevxy.blog.component.api.v1.dto.BlogPostsResponseDto;
import com.gdevxy.blog.component.api.v1.dto.BlogPostCommentsResponseDto;
import com.gdevxy.blog.component.api.v1.dto.BlogPostCommentDto;
import com.gdevxy.blog.component.api.v1.exception.NotFoundException;
import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.BlogPostComment;
import com.gdevxy.blog.model.BlogPostDetail;
import com.gdevxy.blog.model.RecentBlogPost;
import com.gdevxy.blog.service.contentful.blogpost.BlogPostService;
import com.gdevxy.blog.service.profile.IProfileService;
import lombok.extern.slf4j.Slf4j;
import io.smallrye.mutiny.Uni;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Path("/api/v1/blog-posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlogPostsResource {

	@Inject
	BlogPostService blogPostService;

	@Inject
	IProfileService profileService;

	/**
	 * GET /api/v1/blog-posts
	 * Retrieve paginated list of blog posts
	 */
	@GET
	@Path("/test")
	public Response testEndpoint() {
		log.info("Test endpoint called");
		return Response.ok("{\"message\": \"Backend is working!\"}").build();
	}

	@GET
	public Uni<Response> getBlogPosts(
			@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("limit") @DefaultValue("10") int limit,
			@QueryParam("tag") String tag) {

		log.info("Fetching blog posts: page={}, limit={}, tag={}", page, limit, tag);

		Set<String> tags = tag != null && !tag.isBlank() ? Set.of(tag.split(",")) : Set.of();

		return blogPostService.findBlogPosts(null, tags)
				.collect().asList()
				.ifNoItem().after(Duration.ofSeconds(30)).fail()
				.map(posts -> {
					// Apply pagination
					int startIndex = page * limit;
					int endIndex = Math.min(startIndex + limit, posts.size());

					List<BlogPostSummaryDto> paginatedPosts = posts.subList(startIndex, endIndex)
							.stream()
							.map(this::toBlogPostSummaryDto)
							.collect(Collectors.toList());

					return Response.ok(BlogPostsResponseDto.builder()
							.posts(paginatedPosts)
							.currentPage(page)
							.pageSize(limit)
							.hasNextPage(endIndex < posts.size())
							.totalCount(posts.size())
							.build()).build();
				})
				.onFailure().recoverWithItem(e -> {
					log.error("Error fetching blog posts", e);
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				});
	}

	/**
	 * GET /api/v1/blog-posts/recent
	 * Retrieve recent blog posts (home page hero)
	 */
	@GET
	@Path("/recent")
	public Uni<Response> getRecentBlogPosts() {
		log.info("Fetching recent blog posts");

		return blogPostService.findRecentBlogPosts(null)
				.collect().asList()
				.map(recent -> {
					List<BlogPostSummaryDto> result = recent.stream()
							.map(r -> BlogPostSummaryDto.builder()
									.slug(r.getSlug())
									.title(r.getTitle())
									.publishedDate(r.getPublishedDate())
									.build())
							.collect(Collectors.toList());
					return Response.ok(result).build();
				})
				.onFailure().recoverWithItem(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
	}

	/**
	 * GET /api/v1/blog-posts/{slug}
	 * Retrieve detailed blog post with comments and metadata
	 */
	@GET
	@Path("/{slug}")
	public Uni<Response> getBlogPost(
			@PathParam("slug") String slug,
			@QueryParam("preview") @DefaultValue("false") boolean preview,
			@CookieParam("userId") String userId) {

		log.info("Fetching blog post detail: slug={}, preview={}", slug, preview);

		String previewToken = preview ? "preview-token" : null;

		return blogPostService.findBlogPost(userId != null ? userId : "anonymous", previewToken, slug)
				.map(post -> Response.ok(toBlogPostDetailDto(post)).build())
				.onFailure().recoverWithItem(Response.status(Response.Status.NOT_FOUND).build());
	}

	/**
	 * GET /api/v1/blog-posts/{slug}/comments
	 * Get all comments for a blog post
	 */
	@GET
	@Path("/{slug}/comments")
	public Uni<Response> getComments(
			@PathParam("slug") String slug) {

		log.info("Fetching comments for blog post: slug={}", slug);

		return blogPostService.findBlogPostComments(slug)
				.collect().asList()
				.map(comments -> Response.ok(BlogPostCommentsResponseDto.builder()
						.comments(comments)
						.totalCount(comments.size())
						.build()).build())
				.onFailure().recoverWithItem(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
	}

	/**
	 * POST /api/v1/blog-posts/{slug}/comments
	 * Add a comment to a blog post
	 */
	@POST
	@Path("/{slug}/comments")
	public Uni<Response> addComment(
			@PathParam("slug") String slug,
			BlogPostCommentDto commentRequest,
			@CookieParam("userId") String userId) {

		log.info("User adding comment to blog post: slug={}", slug);

		if (userId == null || userId.isBlank()) {
			userId = "anonymous";
		}

		return Uni.createFrom().item(Response.status(Response.Status.CREATED)
						.entity(commentRequest)
						.build())
				.onFailure().recoverWithItem(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
	}

	/**
	 * GET /api/v1/blog-posts/feed.xml
	 * RSS feed endpoint
	 */
	@GET
	@Path("/feed.xml")
	@Produces("application/rss+xml")
	public Uni<Response> getRssFeed() {
		log.info("Generating RSS feed");

		return blogPostService.findRecentBlogPosts(null)
				.collect().asList()
				.map(posts -> Response.ok(generateRssFeed(posts)).build())
				.onFailure().recoverWithItem(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
	}

	// ========== Helper Methods ==========

	private BlogPostDetailDto toBlogPostDetailDto(BlogPostDetail post) {
		return BlogPostDetailDto.builder()
				.id(post.getId())
				.slug(post.getSlug())
				.title(post.getTitle())
				.description(post.getDescription())
				.publishedDate(post.getPublishedDate())
				.image(post.getImage())
				.seo(post.getSeo())
				.rating(post.getRating())
				.blocks(post.getBlocks())
				.tags(post.getTags())
				.relatedBlogPosts(post.getRelatedBlogPosts())
				.liked(post.getLiked())
				.comments(post.getComments())
				.withIndexHeading(post.withIndexHeading())
				.build();
	}

	private BlogPostSummaryDto toBlogPostSummaryDto(BlogPost post) {
		return BlogPostSummaryDto.builder()
				.id(post.getId())
				.slug(post.getSlug())
				.title(post.getTitle())
				.description(post.getDescription())
				.publishedDate(post.getPublishedDate())
				.image(post.getImage())
				.tags(post.getTags())
				.build();
	}

	private String generateRssFeed(List<RecentBlogPost> posts) {
		StringBuilder rss = new StringBuilder();
		rss.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		rss.append("<rss version=\"2.0\">\n");
		rss.append("<channel>\n");
		rss.append("<title>gdevxy Blog</title>\n");
		rss.append("<link>https://www.gdevxy.ch</link>\n");
		rss.append("<description>Blog about Quarkus and Java development</description>\n");
		rss.append("<language>en-us</language>\n");

		for (RecentBlogPost post : posts) {
			rss.append("<item>\n");
			rss.append("<title>").append(escapeXml(post.getTitle())).append("</title>\n");
			rss.append("<link>").append(escapeXml(post.getUrl())).append("</link>\n");
			rss.append("<pubDate>").append(post.getPublishedDate()).append("</pubDate>\n");
			rss.append("</item>\n");
		}

		rss.append("</channel>\n");
		rss.append("</rss>\n");

		return rss.toString();
	}

	private String escapeXml(String text) {
		if (text == null) return "";
		return text.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&apos;");
	}
}
