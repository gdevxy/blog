package com.gdevxy.blog.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@ToString
public class Image {

	private final String id;
	private final String title;
	private final String description;
	private final String url;
	private final Integer width;
	private final Integer height;
	private final String contentType;
	private final Boolean fullWidth;

}
