package com.gdevxy.blog.service.blogpost;

import com.gdevxy.blog.client.contentful.ContentfulClient;
import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.dao.blogpost.BlogPostDao;
import com.gdevxy.blog.dao.blogpost.BlogPostRatingDao;
import com.gdevxy.blog.dao.blogpost.model.BlogPostEntity;
import com.gdevxy.blog.model.*;
import com.gdevxy.blog.service.captcha.CaptchaService;
import com.gdevxy.blog.service.blogpost.converter.BlogPostConverter;
import com.gdevxy.blog.service.blogpost.converter.BlogPostDetailConverter;
import com.gdevxy.blog.service.blogpost.converter.ContentfulPages;
import com.gdevxy.blog.service.cookie.SessionService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostService {

	private final BlogPostDao blogPostDao;
	private final BlogPostRatingDao blogPostRatingDao;

	private final ContentfulClient contentfulClient;

	private final BlogPostCommentService blogPostCommentService;
	private final CaptchaService captchaService;
	private final SessionService sessionService;

	private final BlogPostConverter blogPostConverter;
	private final BlogPostDetailConverter blogPostDetailConverter;
	private final BlogPostAssetResolver blogPostAssetResolver;

	public Uni<BlogPostDetail> findBlogPost(@Nullable String previewToken, String slug) {

		return contentfulClient.findBlogPost(slug, previewToken)
			.onItem().ifNull().failWith(new NotFoundException("BlogPost [%s] not found".formatted(slug)))
			.onItem().transformToUni(p -> {
				var liked = blogPostRatingDao.liked(p.getSys().getId(), sessionService.requestUserId().orElseThrow());
				var comments = blogPostCommentService.find(p.getSys().getId()).collect().asList();
				var assets = blogPostAssetResolver.resolve(p, previewToken);
				return Uni.combine().all().unis(liked, comments, assets).with((l, c, a) -> blogPostDetailConverter.convert(p, l, c, a));
			});
	}

	public Uni<Page<BlogPost>> findBlogPosts(@Nullable String previewToken, Set<String> tags) {

		return contentfulClient.findBlogPosts(ContentfulPages.of(), tags, previewToken)
				.flatMap(p -> Multi.createFrom().iterable(p.getItems())
						.onItem().transformToUniAndConcatenate(this::toBlogPost)
						.collect()
						.asList()
						.map(posts -> Page.<BlogPost>builder()
								.elements(posts)
								.offset(p.getSkip())
								.pageSize(p.getLimit())
								.totalCount(p.getTotal())
								.build())
				);
	}

	public Multi<BlogPostComment> findBlogPostComments(String key) {

		return blogPostCommentService.find(key);
	}

	public Uni<Void> saveComment(String key, BlogPostCommentAction action) {

		return blogPostDao.findByKey(key)
			.flatMap(p -> blogPostCommentService.saveBlogPostComment(p.getId(), action));
	}

	public Uni<Void> saveCommentReply(String key, Integer id, BlogPostCommentAction action) {

		return blogPostCommentService.find(key, id)
			.flatMap(p -> blogPostCommentService.saveBlogPostCommentReply(p.getId(), action));
	}

	public Uni<Void> thumbsUp(String key, LikeAction action) {

		return captchaService.verify(action)
			.flatMap(i -> blogPostDao.findByKey(key))
			.flatMap(e -> blogPostRatingDao.thumbsUp(e.getId(), sessionService.requestUserId().orElseThrow()));
	}

	public Uni<Void> thumbsDown(String key) {

		return blogPostDao.findByKey(key)
			.flatMap(b -> blogPostRatingDao.thumbsDown(b.getId(), sessionService.requestUserId().orElseThrow()));
	}

	private Uni<BlogPost> toBlogPost(PageBlogPost p) {

		return blogPostDao.findByKey(p.getSys().getId())
			.onFailure(NotFoundException.class).recoverWithUni(() -> blogPostDao.save(BlogPostEntity.builder().key(p.getSys().getId()).build()))
			.flatMap(bp -> blogPostRatingDao.countRating(bp.getId()))
			.onFailure(NotFoundException.class).recoverWithItem(0L)
			.map(rating -> blogPostConverter.convert(p, rating));
	}

}
