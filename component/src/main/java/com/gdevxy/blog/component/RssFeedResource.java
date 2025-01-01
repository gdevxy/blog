package com.gdevxy.blog.component;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.gdevxy.blog.model.RssFeed;
import com.gdevxy.blog.service.rss.RssFeederService;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;

@Path("/rss")
@RequiredArgsConstructor
public class RssFeedResource {

	@Inject
	RssFeederService rssFeederService;

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Uni<RssFeed> rssFeed() {

		return rssFeederService.findRssFeed();
	}

}
