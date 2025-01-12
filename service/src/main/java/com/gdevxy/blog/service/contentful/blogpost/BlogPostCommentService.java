package com.gdevxy.blog.service.contentful.blogpost;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.dao.blogpost.BlogPostCommentDao;
import com.gdevxy.blog.dao.blogpost.BlogPostCommentReplyDao;
import com.gdevxy.blog.dao.blogpost.model.BlogPostCommentEntity;
import com.gdevxy.blog.dao.blogpost.model.BlogPostCommentReplyEntity;
import com.gdevxy.blog.model.BlogPostComment;
import com.gdevxy.blog.model.BlogPostCommentAction;
import com.gdevxy.blog.model.LikeAction;
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

	public Multi<BlogPostComment> find(String blogPostKey) {

		return blogPostCommentDao.find(blogPostKey)
			.onItem().transformToMultiAndConcatenate(p -> blogPostCommentReplyDao.find(p.getId())
				.map(this::toBlogPostComment)
				.collect()
				.asList()
				.map(replies -> toBlogPostComment(p, replies))
				.toMulti())
			.log();
	}

	public Uni<Void> saveBlogPostComment(String userId, Integer blogPostId, BlogPostCommentAction action) {

		return captchaService.verify(action)
			.map(v -> BlogPostCommentEntity.builder().userId(userId).blogPostId(blogPostId).author(action.getAuthor()).comment(action.getComment()).build())
			.flatMap(blogPostCommentDao::save)
			.replaceWithVoid();
	}

	public Uni<Void> saveBlogPostCommentReply(String userId, Integer blogPostCommentId, BlogPostCommentAction creation, LikeAction action) {

		return captchaService.verify(action)
			.map(v -> BlogPostCommentReplyEntity.builder().userId(userId).blogPostCommentId(blogPostCommentId).author(creation.getAuthor()).comment(creation.getComment()).build())
			.flatMap(blogPostCommentReplyDao::save)
			.replaceWithVoid();
	}

	private BlogPostComment toBlogPostComment(BlogPostCommentEntity e, List<BlogPostComment> replies) {

		return BlogPostComment.builder()
			.id(e.getId())
			.author(e.getAuthor())
			.createdAt(e.getCreatedAt())
			.comment(e.getComment())
			.replies(replies)
			.build();
	}

	private BlogPostComment toBlogPostComment(BlogPostCommentReplyEntity e) {

		return BlogPostComment.builder()
			.id(e.getId())
			.author(e.getAuthor())
			.createdAt(e.getCreatedAt())
			.comment(e.getComment())
			.build();
	}

}
