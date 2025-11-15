package com.gdevxy.blog.component.api.v1;

import com.gdevxy.blog.model.BlogPostCommentAction;
import com.gdevxy.blog.model.BlogPostDeleteAction;
import com.gdevxy.blog.service.blogpost.BlogPostCommentService;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/api/v1/comments")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class CommentResource {

	private final BlogPostCommentService blogPostCommentService;

	@PUT
	@Path("/{id}")
	public Uni<Void> updateComment(@PathParam("id") Integer commentId, @Valid BlogPostCommentAction commentAction) {

		return blogPostCommentService.updateBlogPostComment(commentId, commentAction);
	}

	@DELETE
	@Path("/{id}")
	public Uni<Void> deleteComment(@PathParam("id") Integer commentId, @Valid BlogPostDeleteAction deleteAction) {

		return blogPostCommentService.deleteBlogPostComment(commentId, deleteAction);
	}

	@PUT
	@Path("/reply/{id}")
	public Uni<Void> updateCommentReply(@PathParam("id") Integer replyId, @Valid BlogPostCommentAction commentAction) {

		return blogPostCommentService.updateBlogPostCommentReply(replyId, commentAction);
	}

	@DELETE
	@Path("/reply/{id}")
	public Uni<Void> deleteCommentReply(@PathParam("id") Integer replyId, @Valid BlogPostDeleteAction deleteAction) {

		return blogPostCommentService.deleteBlogPostCommentReply(replyId, deleteAction);
	}

}
