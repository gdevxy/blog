package com.gdevxy.blog.service.blogpost;

import java.util.List;

import com.gdevxy.blog.model.BlogPostDeleteAction;
import com.gdevxy.blog.service.MutinyUtils;
import com.gdevxy.blog.service.cookie.SessionService;
import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.dao.blogpost.BlogPostCommentDao;
import com.gdevxy.blog.dao.blogpost.BlogPostCommentReplyDao;
import com.gdevxy.blog.dao.blogpost.model.BlogPostCommentEntity;
import com.gdevxy.blog.dao.blogpost.model.BlogPostCommentReplyEntity;
import com.gdevxy.blog.model.BlogPostComment;
import com.gdevxy.blog.model.BlogPostCommentAction;
import com.gdevxy.blog.service.Strings;
import com.gdevxy.blog.service.captcha.CaptchaService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostCommentService {

	private final BlogPostCommentDao blogPostCommentDao;
	private final BlogPostCommentReplyDao blogPostCommentReplyDao;
	private final CaptchaService captchaService;
	private final SessionService sessionService;

	public Uni<BlogPostComment> find(String blogPostKey, Integer id) {

		return blogPostCommentDao.find(blogPostKey, id)
				.flatMap(p -> blogPostCommentReplyDao.find(p.getId())
						.map(this::toBlogPostComment)
						.collect()
						.asList()
						.map(replies -> toBlogPostComment(p, replies)));
	}

	public Multi<BlogPostComment> find(String blogPostKey) {

		return blogPostCommentDao.find(blogPostKey)
				.onItem().transformToMultiAndConcatenate(p -> blogPostCommentReplyDao.find(p.getId())
						.map(this::toBlogPostComment)
						.collect()
						.asList()
						.map(replies -> toBlogPostComment(p, replies))
						.toMulti());
	}

	public Uni<Void> saveBlogPostComment(Integer blogPostId, BlogPostCommentAction action) {

		return captchaService.verify(action)
				.map(v -> BlogPostCommentEntity.builder()
						.userId(sessionService.requestUserId().orElseThrow())
						.blogPostId(blogPostId)
						.author(Strings.blankToNull(action.getAuthor()))
						.comment(action.getComment())
						.build())
				.flatMap(blogPostCommentDao::save)
				.replaceWithVoid();
	}

	public Uni<Void> updateBlogPostComment(Integer commentId, BlogPostCommentAction action) {

		return captchaService.verify(action)
				.flatMap(_ -> blogPostCommentDao.update(commentId, sessionService.requestUserId()
						.orElseThrow(), Strings.blankToNull(action.getAuthor()), action.getComment()))
				.flatMap(success -> MutinyUtils.unauthorizedOrContinue(success, s -> !s))
				.replaceWithVoid();
	}

	public Uni<Void> deleteBlogPostComment(Integer commentId, BlogPostDeleteAction action) {

		return captchaService.verify(action)
				.flatMap(_ -> blogPostCommentDao.delete(commentId, sessionService.requestUserId().orElseThrow()))
				.flatMap(success -> MutinyUtils.unauthorizedOrContinue(success, s -> !s))
				.replaceWithVoid();
	}

	public Uni<Void> saveBlogPostCommentReply(Integer blogPostCommentId, BlogPostCommentAction action) {

		return captchaService.verify(action)
				.map(v -> BlogPostCommentReplyEntity.builder()
						.userId(sessionService.requestUserId().orElseThrow())
						.blogPostCommentId(blogPostCommentId)
						.author(Strings.blankToNull(action.getAuthor()))
						.comment(action.getComment())
						.build())
				.flatMap(blogPostCommentReplyDao::save)
				.replaceWithVoid();
	}

	public Uni<Void> updateBlogPostCommentReply(Integer replyId, BlogPostCommentAction action) {

		return captchaService.verify(action)
				.flatMap(_ -> blogPostCommentReplyDao.update(replyId, sessionService.requestUserId()
						.orElseThrow(), Strings.blankToNull(action.getAuthor()), action.getComment()))
				.flatMap(success -> MutinyUtils.unauthorizedOrContinue(success, s -> !s))
				.replaceWithVoid();
	}

	public Uni<Void> deleteBlogPostCommentReply(Integer replyId, BlogPostDeleteAction action) {


		return captchaService.verify(action)
				.flatMap(_ -> blogPostCommentReplyDao.delete(replyId, sessionService.requestUserId().orElseThrow()))
				.flatMap(success -> MutinyUtils.unauthorizedOrContinue(success, s -> !s))
				.replaceWithVoid();
	}

	private BlogPostComment toBlogPostComment(BlogPostCommentEntity e, List<BlogPostComment> replies) {

		return BlogPostComment.builder()
				.id(e.getId())
				.author(e.getAuthor())
				.createdAt(e.getCreatedAt())
				.comment(e.getComment())
				.replies(replies)
				.modifiable(isCommentModifiable(e.getUserId()))
				.build();
	}

	private BlogPostComment toBlogPostComment(BlogPostCommentReplyEntity e) {

		return BlogPostComment.builder()
				.id(e.getId())
				.author(e.getAuthor())
				.createdAt(e.getCreatedAt())
				.comment(e.getComment())
				.modifiable(isCommentModifiable(e.getUserId()))
				.build();
	}

	private Boolean isCommentModifiable(String commentUserId) {

		return sessionService.requestUserId().map(s -> s.equals(commentUserId)).orElse(false);
	}

}
