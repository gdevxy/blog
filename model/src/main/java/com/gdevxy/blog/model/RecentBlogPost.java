package com.gdevxy.blog.model;

import java.time.ZonedDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class RecentBlogPost {

	private final String slug;
	private final String title;
	private final String url;
	private final ZonedDateTime publishedDate;

}
