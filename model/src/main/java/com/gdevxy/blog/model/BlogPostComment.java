package com.gdevxy.blog.model;

import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class BlogPostComment {

	private final Integer id;
	private final String author;
	private final String comment;
	private final Instant createdAt;
	@Builder.Default
	private final List<BlogPostComment> replies = List.of();

}
