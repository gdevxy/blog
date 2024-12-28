package com.gdevxy.blog.dao.blogpostcomment;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class BlogPostCommentEntity {

	private final Integer id;
	private final String comment;

}
