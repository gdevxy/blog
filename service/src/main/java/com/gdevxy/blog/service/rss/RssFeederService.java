package com.gdevxy.blog.service.rss;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.gdevxy.blog.client.contentful.ContentfulClient;
import com.gdevxy.blog.client.contentful.model.Page;
import com.gdevxy.blog.model.RssFeed;
import com.gdevxy.blog.service.rss.converter.RssFeedConverter;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class RssFeederService {

	@Inject
	ContentfulClient contentfulClient;
	@Inject
	RssFeedConverter rssFeedConverter;

	@CacheResult(cacheName = "rss-feed")
	public Uni<RssFeed> findRssFeed() {

		return Multi.createBy().repeating()
			.uni(AtomicInteger::new, page -> contentfulClient.findLightBlogPosts(Page.builder().offset(100L * page.getAndIncrement()).build()))
			.until(l -> l.getTotal() <= 0)
			.onItem().transformToMultiAndConcatenate(l -> Multi.createFrom().iterable(l.getItems()))
			.collect()
			.asList()
			.map(rssFeedConverter);
	}

}
