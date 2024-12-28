package com.gdevxy.blog.dao.blogpost;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class BlogPostEntity {

	private final Integer id;
	private final String key;
	private final Integer rating;

}
