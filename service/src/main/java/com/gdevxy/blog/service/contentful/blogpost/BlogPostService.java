package com.gdevxy.blog.service.contentful.blogpost;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.gdevxy.blog.client.contentful.ContentfulClient;
import com.gdevxy.blog.client.contentful.model.Pagination;
import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.RecentBlogPost;
import com.gdevxy.blog.service.contentful.blogpost.converter.BlogPostConverter;
import com.gdevxy.blog.service.contentful.blogpost.converter.RecentBlogPostConverter;

@ApplicationScoped
public class BlogPostService {

	@Inject
	ContentfulClient contentfulClient;
	@Inject
	BlogPostConverter blogPostConverter;
	@Inject
	RecentBlogPostConverter recentBlogPostConverter;

	public Optional<BlogPost> findBlogPost(@Nullable String previewToken, String slug) {

		return contentfulClient.findBlogPost(slug, previewToken).map(p -> blogPostConverter.convertWithContent(p, previewToken));
	}

	public List<BlogPost> findBlogPosts(@Nullable String previewToken, Set<String> tags) {

		var pagination = Pagination.builder().build();

		return contentfulClient.findBlogPosts(pagination, tags, previewToken)
			.getItems()
			.stream()
			.map(p -> blogPostConverter.convert(p, previewToken))
			.toList();
	}

	public List<RecentBlogPost> findRecentBlogPosts(@Nullable String previewToken) {

		return contentfulClient.findRecentBlogPosts(previewToken).getItems().stream().map(recentBlogPostConverter).toList();
	}

}
