package com.gdevxy.blog.client.contentful.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FeaturedImage {

	private String title;
	private String description;
	private String url;
	private Integer width;
	private Integer height;
	private String contentType;

}
