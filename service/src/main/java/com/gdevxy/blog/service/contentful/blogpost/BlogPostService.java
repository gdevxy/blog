package com.gdevxy.blog.service.contentful.blogpost;

import java.util.Set;
import java.util.function.Function;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import com.gdevxy.blog.client.contentful.ContentfulClient;
import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.Pagination;
import com.gdevxy.blog.dao.blogpost.BlogPostDao;
import com.gdevxy.blog.dao.blogpost.BlogPostEntity;
import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.RecentBlogPost;
import com.gdevxy.blog.service.contentful.blogpost.converter.BlogPostConverter;
import com.gdevxy.blog.service.contentful.blogpost.converter.RecentBlogPostConverter;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class BlogPostService {

	@Inject
	BlogPostDao blogPostDao;
	@Inject
	ContentfulClient contentfulClient;
	@Inject
	BlogPostConverter blogPostConverter;
	@Inject
	RecentBlogPostConverter recentBlogPostConverter;

	public Uni<BlogPost> findBlogPost(@Nullable String previewToken, String slug) {

		return contentfulClient.findBlogPost(slug, previewToken)
			.onItem().ifNull().failWith(new NotFoundException("BlogPost [%s] not found".formatted(slug)))
			.onItem().transformToUni(toBlogPost());
	}

	public Multi<BlogPost> findBlogPosts(@Nullable String previewToken, Set<String> tags) {

		var pagination = Pagination.builder().build();

		return contentfulClient.findBlogPosts(pagination, tags, previewToken)
			.onItem().transformToMulti(p -> Multi.createFrom().iterable(p.getItems()))
			.onItem().transformToUniAndConcatenate(toBlogPost());
	}

	public Multi<RecentBlogPost> findRecentBlogPosts(@Nullable String previewToken) {

		return contentfulClient.findRecentBlogPosts(previewToken)
			.onItem().transformToMulti(p -> Multi.createFrom().iterable(p.getItems()))
			.map(recentBlogPostConverter);
	}

	public Uni<Void> rate(String id) {

		return blogPostDao.rate(id);
	}

	private Function<PageBlogPost, Uni<? extends BlogPost>> toBlogPost() {

		return p -> blogPostDao.findByKey(p.getSys().getId())
			.map(BlogPostEntity::getRating)
			.onFailure(NotFoundException.class).recoverWithItem(0)
			.map(rating -> blogPostConverter.convert(p, rating));
	}

}
