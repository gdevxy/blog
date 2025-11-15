package com.gdevxy.blog.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
@ToString
public class BlogPost {

	private final String id;
	private final String slug;
	private final String title;
	private final String description;
	private final ZonedDateTime publishedDate;
	private final Image image;
	private final Seo seo;
	private final String rating;
	@Builder.Default
	private final List<BlogPostTag> tags = List.of();
	@Builder.Default
	private final Boolean liked = false;

	@Getter
	@Builder
	@ToString
	public static class Seo {

		private final String title;
		private final String description;
		private final String robotHint;

	}

}
