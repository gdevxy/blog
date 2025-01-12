package com.gdevxy.blog.service.contentful.blogpost;

import java.util.Set;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import com.gdevxy.blog.client.contentful.ContentfulClient;
import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.Page;
import com.gdevxy.blog.dao.blogpost.BlogPostDao;
import com.gdevxy.blog.dao.blogpost.model.BlogPostEntity;
import com.gdevxy.blog.dao.blogpost.BlogPostRatingDao;
import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.BlogPostComment;
import com.gdevxy.blog.model.BlogPostCommentAction;
import com.gdevxy.blog.model.BlogPostDetail;
import com.gdevxy.blog.model.LikeAction;
import com.gdevxy.blog.model.RecentBlogPost;
import com.gdevxy.blog.service.captcha.CaptchaService;
import com.gdevxy.blog.service.contentful.blogpost.converter.BlogPostConverter;
import com.gdevxy.blog.service.contentful.blogpost.converter.BlogPostDetailConverter;
import com.gdevxy.blog.service.contentful.blogpost.converter.RecentBlogPostConverter;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostService {

	private final BlogPostDao blogPostDao;
	private final BlogPostRatingDao blogPostRatingDao;

	private final ContentfulClient contentfulClient;

	private final BlogPostCommentService blogPostCommentService;
	private final CaptchaService captchaService;

	private final BlogPostConverter blogPostConverter;
	private final BlogPostDetailConverter blogPostDetailConverter;
	private final RecentBlogPostConverter recentBlogPostConverter;

	public Uni<BlogPostDetail> findBlogPost(String userId, @Nullable String previewToken, String slug) {

		return contentfulClient.findBlogPost(slug, previewToken)
			.onItem().ifNull().failWith(new NotFoundException("BlogPost [%s] not found".formatted(slug)))
			.onItem().transformToUni(p -> {
				var liked = blogPostRatingDao.liked(p.getSys().getId(), userId);
				var comments = blogPostCommentService.find(p.getSys().getId()).collect().asList();
				return Uni.combine().all().unis(liked, comments).with((l, c) -> blogPostDetailConverter.convert(p, l, c));
			});
	}

	public Multi<BlogPost> findBlogPosts(@Nullable String previewToken, Set<String> tags) {

		var pagination = Page.builder().build();

		return contentfulClient.findBlogPosts(pagination, tags, previewToken)
			.onItem().transformToMulti(p -> Multi.createFrom().iterable(p.getItems()))
			.onItem().transformToUniAndConcatenate(this::toBlogPost);
	}

	public Multi<RecentBlogPost> findRecentBlogPosts(@Nullable String previewToken) {

		return contentfulClient.findLightBlogPosts(Page.builder().pageSize(5L).build(), previewToken)
			.onItem().transformToMulti(p -> Multi.createFrom().iterable(p.getItems()))
			.map(recentBlogPostConverter);
	}

	public Multi<BlogPostComment> findBlogPostComments(String key) {

		return blogPostCommentService.find(key);
	}

	public Uni<Void> saveComment(String userId, String key, BlogPostCommentAction action) {

		return blogPostDao.findByKey(key)
			.flatMap(p -> blogPostCommentService.saveBlogPostComment(userId, p.getId(), action));
	}

	public Uni<Void> thumbsUp(String userId, String key, LikeAction action) {

		return captchaService.verify(action)
			.flatMap(i -> blogPostDao.findByKey(key))
			.flatMap(e -> blogPostRatingDao.thumbsUp(e.getId(), userId));
	}

	public Uni<Void> thumbsDown(String userId, String key) {

		return blogPostDao.findByKey(key)
			.flatMap(b -> blogPostRatingDao.thumbsDown(b.getId(), userId));
	}

	private Uni<BlogPost> toBlogPost(PageBlogPost p) {

		return blogPostDao.findByKey(p.getSys().getId())
			.onFailure(NotFoundException.class).recoverWithUni(() -> blogPostDao.save(BlogPostEntity.builder().key(p.getSys().getId()).build()))
			.flatMap(bp -> blogPostRatingDao.countRating(bp.getId()))
			.onFailure(NotFoundException.class).recoverWithItem(0L)
			.map(rating -> blogPostConverter.convert(p, rating));
	}

}
