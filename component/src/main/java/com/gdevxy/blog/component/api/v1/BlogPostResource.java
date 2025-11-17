package com.gdevxy.blog.component.api.v1;

import com.gdevxy.blog.model.*;
import com.gdevxy.blog.service.blogpost.BlogPostService;
import com.gdevxy.blog.service.rss.RssFeederService;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

@Slf4j
@Path("/api/v1/blog-posts")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class BlogPostResource {

	private final BlogPostService blogPostService;
	private final RssFeederService rssFeederService;

	@GET
	public Uni<Page<BlogPost>> findBlogPosts(@QueryParam("previewToken") String previewToken,
			@QueryParam("tags") Set<String> tags) {

		return blogPostService.findBlogPosts(previewToken, tags);
	}

	@GET
	@Path("/{slug}")
	public Uni<BlogPostDetail> findBlogPost(@PathParam("slug") String slug,
			@QueryParam("previewToken") String previewToken) {

		return blogPostService.findBlogPost(previewToken, slug);
	}

	@GET
	@Path("/{id}/comments")
	public Uni<List<BlogPostComment>> findComments(@PathParam("id") String contentfulId) {

		return blogPostService.findBlogPostComments(contentfulId).collect().asList();
	}

	@POST
	@Path("/{id}/comments")
	public Uni<Void> addComment(@PathParam("id") String contentfulId, @Valid BlogPostCommentAction commentAction) {

		return blogPostService.saveComment(contentfulId, commentAction);
	}

	@POST
	@Path("/{id}/comments/{commentId}/reply")
	public Uni<Void> saveCommentReply(@PathParam("id") String contentfulId, @PathParam("commentId") Integer commentId,
			@Valid BlogPostCommentAction replyAction) {

		return blogPostService.saveCommentReply(contentfulId, commentId, replyAction);
	}

	@POST
	@Path("/{id}/rating/thumbs-up")
	public Uni<Void> thumbsUp(@PathParam("id") String contentfulId, @Valid LikeAction likeAction) {

		return blogPostService.thumbsUp(contentfulId, likeAction);
	}

	@POST
	@Path("/{id}/rating/thumbs-down")
	public Uni<Void> thumbsDown(@PathParam("id") String contentfulId) {

		return blogPostService.thumbsDown(contentfulId);
	}

	@GET
	@Path("/feed.xml")
	@Produces("application/rss+xml")
	public Uni<RssFeed> getRssFeed() {

		return rssFeederService.findRssFeed();
	}

}
