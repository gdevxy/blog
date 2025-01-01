package com.gdevxy.blog.service.contentful.blogpost;

import java.util.Set;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import com.gdevxy.blog.client.contentful.ContentfulClient;
import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.Page;
import com.gdevxy.blog.dao.blogpost.BlogPostDao;
import com.gdevxy.blog.dao.blogpost.BlogPostEntity;
import com.gdevxy.blog.dao.blogpost.BlogPostRatingDao;
import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.CaptchaProtectedAction;
import com.gdevxy.blog.model.RecentBlogPost;
import com.gdevxy.blog.service.captcha.CaptchaService;
import com.gdevxy.blog.service.contentful.blogpost.converter.BlogPostConverter;
import com.gdevxy.blog.service.contentful.blogpost.converter.RecentBlogPostConverter;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class BlogPostService {

	@Inject
	BlogPostDao blogPostDao;
	@Inject
	BlogPostRatingDao blogPostRatingDao;
	@Inject
	ContentfulClient contentfulClient;
	@Inject
	CaptchaService captchaService;
	@Inject
	BlogPostConverter blogPostConverter;
	@Inject
	RecentBlogPostConverter recentBlogPostConverter;

	public Uni<BlogPost> findBlogPost(String userId, @Nullable String previewToken, String slug) {

		return contentfulClient.findBlogPost(slug, previewToken)
			.onItem().ifNull().failWith(new NotFoundException("BlogPost [%s] not found".formatted(slug)))
			.onItem().transformToUni(p -> {
				var liked = blogPostRatingDao.liked(p.getSys().getId(), userId);
				var builder = toBlogPostBuilder(p);
				return Uni.combine().all().unis(builder, liked).with((b, l) -> b.liked(l).build());
			});
	}

	public Multi<BlogPost> findBlogPosts(@Nullable String previewToken, Set<String> tags) {

		var pagination = Page.builder().build();

		return contentfulClient.findBlogPosts(pagination, tags, previewToken)
			.onItem().transformToMulti(p -> Multi.createFrom().iterable(p.getItems()))
			.onItem().transformToUniAndConcatenate(this::toBlogPostBuilder)
			.map(BlogPost.BlogPostBuilder::build);
	}

	public Multi<RecentBlogPost> findRecentBlogPosts(@Nullable String previewToken) {

		return contentfulClient.findLightBlogPosts(Page.builder().pageSize(5L).build(), previewToken)
			.onItem().transformToMulti(p -> Multi.createFrom().iterable(p.getItems()))
			.map(recentBlogPostConverter);
	}

	public Uni<Void> thumbsUp(String userId, String key, CaptchaProtectedAction action) {

		return captchaService.verify(action)
			.flatMap(i -> blogPostDao.findByKey(key))
			.flatMap(e -> blogPostRatingDao.thumbsUp(e.getId(), userId));
	}

	public Uni<Void> thumbsDown(String userId, String key) {

		return blogPostDao.findByKey(key)
			.flatMap(b -> blogPostRatingDao.thumbsDown(b.getId(), userId));
	}

	private Uni<BlogPost.BlogPostBuilder> toBlogPostBuilder(PageBlogPost p) {

		return blogPostDao.findByKey(p.getSys().getId())
			.onFailure(NotFoundException.class).recoverWithUni(() -> blogPostDao.save(BlogPostEntity.builder().key(p.getSys().getId()).build()))
			.flatMap(bp -> blogPostRatingDao.countRating(bp.getId()))
			.onFailure(NotFoundException.class).recoverWithItem(0L)
			.map(rating -> blogPostConverter.convert(p, rating));
	}

}
