package com.gdevxy.blog.service.contentful.blogpost;

import com.gdevxy.blog.client.contentful.ContentfulClient;
import com.gdevxy.blog.client.contentful.DefaultContentful;
import com.gdevxy.blog.client.contentful.PreviewContentful;
import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.service.contentful.ContentfulServiceSupport;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BlogPostService extends ContentfulServiceSupport {

	private final BlogPostConverter blogPostConverter;

	public BlogPostService(@DefaultContentful ContentfulClient contentfulClient, @PreviewContentful ContentfulClient previewClient,
						   BlogPostConverter blogPostConverter) {
		super(contentfulClient, previewClient);
		this.blogPostConverter = blogPostConverter;
	}

	public Optional<BlogPost> findBlogPost(Boolean preview, String slug) {

		var client = client(preview);

		return client.findBlogPost(slug).map(p -> blogPostConverter.convert(client, p));
	}

	public List<BlogPost> findBlogPosts(Boolean preview) {

		return client(preview).findBlogPosts().getItems().stream().map(blogPostConverter::convert).toList();
	}

}
