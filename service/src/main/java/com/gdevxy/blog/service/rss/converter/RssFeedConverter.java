package com.gdevxy.blog.service.rss.converter;

import java.util.List;
import java.util.function.Function;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.client.contentful.model.LightPageBlogPost;
import com.gdevxy.blog.model.RssFeed;

@ApplicationScoped
public class RssFeedConverter implements Function<List<LightPageBlogPost>, RssFeed> {

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
			.link("https://www.gdevxy.ch/blog-posts/%s".formatted(p.getSlug()))
			.pubDate(p.getPublishedDate())
			.build();
	}

}
