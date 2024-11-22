package com.gdevxy.blog.client.contentful.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@ToString
public class FeaturedImage {

	private String title;
	private String description;
	private String url;
	private Integer width;
	private Integer height;
	private String contentType;

}
