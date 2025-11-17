package com.gdevxy.blog.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Builder
@ToString
public class BlogPostDetail {

	private final String id;
	private final String slug;
	private final String title;
	private final String description;
	private final ZonedDateTime publishedDate;
	private final Image image;
	private final Seo seo;
	private final String rating;
	@Builder.Default
	private final List<ContentBlock> blocks = List.of();
	@Builder.Default
	private final Set<BlogPostTag> tags = Set.of();
	@Builder.Default
	private final List<RelatedBlogPost> relatedBlogPosts = List.of();
	@Builder.Default
	private final Boolean liked = false;
	@Builder.Default
	private final List<BlogPostComment> comments = List.of();

	@Getter
	@Builder
	@ToString
	public static class Seo {

		private final String title;
		private final String description;
		private final String robotHint;

	}

	@Getter
	@Builder
	@ToString
	public static class ContentBlock {

		private final String node;
		private final String value;
		private final Image image;
		@Builder.Default
		private final Set<String> marks = Set.of();
		@Builder.Default
		private final List<ContentBlock> blocks = List.of();

	}

	@Getter
	@Builder
	@ToString
	public static class RelatedBlogPost {

		private final String slug;
		private final String title;
		private final String description;

	}

}
