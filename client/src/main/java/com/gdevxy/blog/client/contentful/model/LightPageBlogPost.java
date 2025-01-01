package com.gdevxy.blog.client.contentful.model;

import java.time.ZonedDateTime;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@ToString
public class LightPageBlogPost {

	private String slug;
	private String title;
	private String shortDescription;
	private ZonedDateTime publishedDate;

}
