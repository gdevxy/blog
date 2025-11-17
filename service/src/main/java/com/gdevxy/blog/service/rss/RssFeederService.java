package com.gdevxy.blog.service.rss;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.RssFeed;
import com.gdevxy.blog.service.blogpost.BlogPostService;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class RssFeederService {

	private final BlogPostService blogPostService;

	@CacheResult(cacheName = "rss-feed")
	public Uni<RssFeed> findRssFeed() {

		return blogPostService.findBlogPosts(null, Set.of())
			.map(page -> RssFeed.builder()
				.channel(RssFeed.Channel.builder()
					.title("gdevxy Blog")
					.link("/")
					.description("Blog about Quarkus and Java development")
					.language("en-us")
					.items(page.getElements().stream()
						.map(this::toBlogPostItem)
						.collect(Collectors.toList()))
					.build())
				.build());
	}

	private RssFeed.Item toBlogPostItem(BlogPost post) {
		return RssFeed.Item.builder()
			.title(post.getTitle())
			.link("/blog/" + post.getSlug())
			.description(post.getDescription())
			.pubDate(post.getPublishedDate())
			.build();
	}

}
