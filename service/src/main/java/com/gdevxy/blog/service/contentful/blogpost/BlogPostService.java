package com.gdevxy.blog.service.contentful.blogpost;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.client.contentful.ContentfulClient;
import com.gdevxy.blog.client.contentful.DefaultContentful;
import com.gdevxy.blog.client.contentful.PreviewContentful;
import com.gdevxy.blog.client.contentful.model.Pagination;
import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.BlogPostTag;
import com.gdevxy.blog.model.RecentBlogPost;
import com.gdevxy.blog.service.contentful.ContentfulServiceSupport;
import com.gdevxy.blog.service.contentful.blogpost.converter.BlogPostConverter;
import com.gdevxy.blog.service.contentful.blogpost.converter.RecentBlogPostConverter;

@ApplicationScoped
public class BlogPostService extends ContentfulServiceSupport {

	private final BlogPostConverter blogPostConverter;
	private final RecentBlogPostConverter recentBlogPostConverter;

	public BlogPostService(@DefaultContentful ContentfulClient contentfulClient, @PreviewContentful ContentfulClient previewClient,
		BlogPostConverter blogPostConverter, RecentBlogPostConverter recentBlogPostConverter) {
		super(contentfulClient, previewClient);
		this.blogPostConverter = blogPostConverter;
		this.recentBlogPostConverter = recentBlogPostConverter;
	}

	public Optional<BlogPost> findBlogPost(Boolean preview, String slug) {

		var client = client(preview);

		return client.findBlogPost(slug).map(p -> blogPostConverter.convert(client, p));
	}

	public List<BlogPost> findBlogPosts(Set<BlogPostTag> tags) {

		var pagination = Pagination.builder().build();
		var contentfulBlogPostTags = tags.stream().map(BlogPostTag::getCode).collect(Collectors.toUnmodifiableSet());

		return client(false).findBlogPosts(pagination, contentfulBlogPostTags).getItems().stream().map(blogPostConverter::convert).toList();
	}

	public List<RecentBlogPost> findRecentBlogPosts() {

		return client(false).findRecentBlogPosts().getItems().stream().map(recentBlogPostConverter).toList();
	}

}
