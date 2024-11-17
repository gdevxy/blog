package com.gdevxy.blog.client.contentful.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SeoFields {

	private Sys sys;
	private String pageTitle;
	private String pageDescription;
	@Builder.Default
	private Boolean nofollow = false;
	@Builder.Default
	private Boolean noindex = false;

}
