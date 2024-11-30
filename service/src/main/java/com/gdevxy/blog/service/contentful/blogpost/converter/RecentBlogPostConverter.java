package com.gdevxy.blog.service.contentful.blogpost.converter;

import java.util.function.Function;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.client.contentful.model.RecentPageBlogPost;
import com.gdevxy.blog.model.RecentBlogPost;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@RequiredArgsConstructor
public class RecentBlogPostConverter implements Function<RecentPageBlogPost, RecentBlogPost> {

	@ConfigProperty(name = "application.base-uri")
	public String baseUri;

	@Override
	public RecentBlogPost apply(RecentPageBlogPost p) {

		return RecentBlogPost.builder()
			.slug(p.getSlug())
			.title(p.getTitle())
			.url("%s/blog-posts/%s".formatted(baseUri, p.getSlug()))
			.publishedDate(p.getPublishedDate())
			.build();
	}

}
