package com.gdevxy.blog.dao.blogpost.model;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@ToString
public class BlogPostCommentEntity {

	private final Integer id;
	private final Integer blogPostId;
	private final String userId;
	private final String author;
	private final String comment;
	@Builder.Default
	private final Instant createdAt = Instant.now();

}
