package com.gdevxy.blog.client.contentful.model;

import com.gdevxy.blog.client.contentful.model.content.DynamicContent;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageBlogPost {

	private Sys sys;
	private String slug;
	private String title;
	private String shortDescription;
	private ZonedDateTime publishedDate;
	private FeaturedImage featuredImage;
	private SeoFields seoFields;
	private DynamicContent content;

}
