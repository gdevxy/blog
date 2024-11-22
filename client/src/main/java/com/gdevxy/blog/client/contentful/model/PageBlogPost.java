package com.gdevxy.blog.client.contentful.model;

import com.gdevxy.blog.client.contentful.model.content.DynamicContent;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

import java.time.ZonedDateTime;

import jakarta.json.bind.annotation.JsonbDateFormat;

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
	private DynamicContent content;

}
