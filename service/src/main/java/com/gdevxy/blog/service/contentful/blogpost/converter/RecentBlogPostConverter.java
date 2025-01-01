package com.gdevxy.blog.service.contentful.blogpost.converter;

import java.util.function.Function;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.client.contentful.model.LightPageBlogPost;
import com.gdevxy.blog.model.RecentBlogPost;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@RequiredArgsConstructor
public class RecentBlogPostConverter implements Function<LightPageBlogPost, RecentBlogPost> {

	@ConfigProperty(name = "application.base-uri")
	public String baseUri;

	@Override
	public RecentBlogPost apply(LightPageBlogPost p) {

		return RecentBlogPost.builder()
			.slug(p.getSlug())
			.title(p.getTitle())
			.url("%s/blog-posts/%s".formatted(baseUri, p.getSlug()))
			.publishedDate(p.getPublishedDate())
			.build();
	}

}
