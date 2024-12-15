package com.gdevxy.blog.client.contentful.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@ToString
public class PageBlogPost {

	private Sys sys;
	private String slug;
	private String title;
	private String shortDescription;
	private ZonedDateTime publishedDate;
	private FeaturedImage featuredImage;
	private SeoFields seoFields;
	private JsonContent content;
	private Set<String> tags;

}
