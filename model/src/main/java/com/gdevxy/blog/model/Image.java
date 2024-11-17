package com.gdevxy.blog.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Image {

	private final String title;
	private final String description;
	private final String url;
	private final Integer width;
	private final Integer height;
	private final String contentType;

}
