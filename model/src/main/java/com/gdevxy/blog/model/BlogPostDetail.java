package com.gdevxy.blog.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.gdevxy.blog.model.contentful.Mark;
import com.gdevxy.blog.model.contentful.Node;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

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
	private final List<BlogPostComment> comments;

	public boolean withIndexHeading() {
		return blocks.stream().map(ContentBlock::getNode).anyMatch(Node::indexHeading);
	}

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

		private final Node node;
		private final String value;
		private final TextMark marks;
		@Builder.Default
		private final List<ContentBlock> blocks = List.of();

		public String toHtmlIdentifier() {
			return value.replaceAll("\\s","-").toLowerCase();
		}

		@Getter
		@Builder
		@ToString
		@RegisterForReflection
		public static class TextMark {

			private final Set<Mark> marks;

			public Boolean code() {
				return marks.contains(Mark.CODE);
			}

			public String styling() {
				return marks.stream().map(Mark::getStyling).filter(Objects::nonNull).collect(Collectors.joining(" "));
			}

		}

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
