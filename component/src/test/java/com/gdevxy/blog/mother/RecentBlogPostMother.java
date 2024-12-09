package com.gdevxy.blog.mother;

import java.time.ZonedDateTime;

import com.gdevxy.blog.model.RecentBlogPost;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RecentBlogPostMother {

	public RecentBlogPost.RecentBlogPostBuilder basic() {

		return RecentBlogPost.builder()
			.title("recent-title")
			.slug("recent-slug")
			.url("recent-url")
			.publishedDate(ZonedDateTime.now());
	}

}
