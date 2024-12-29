package com.gdevxy.blog.dao.blogpost;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@ToString
public class BlogPostEntity {

	private final Integer id;
	private final String key;

}
