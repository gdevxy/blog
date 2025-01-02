package com.gdevxy.blog.service.rss.converter;

import java.util.List;
import java.util.function.Function;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.gdevxy.blog.client.contentful.model.LightPageBlogPost;
import com.gdevxy.blog.model.RssFeed;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@ApplicationScoped
public class RssFeedConverter implements Function<List<LightPageBlogPost>, RssFeed> {

	@Inject
	@ConfigProperty(name = "application.base-uri")
	String baseUri;

	@Override
	public RssFeed apply(List<LightPageBlogPost> p) {

		return RssFeed.builder()
			.channel(RssFeed.Channel.builder()
				.items(p.stream().map(this::toItem).toList())
				.build())
			.build();
	}

	private RssFeed.Item toItem(LightPageBlogPost p) {

		return RssFeed.Item.builder()
			.title(p.getTitle())
			.description(p.getShortDescription())
			.link("%s/blog-posts/%s".formatted(baseUri, p.getSlug()))
			.pubDate(p.getPublishedDate())
			.build();
	}

}
