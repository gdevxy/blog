package com.gdevxy.blog.client.contentful.model;

import lombok.*;

import java.util.Optional;

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
	private Boolean nofollow;
	private Boolean noindex;

	public Boolean getNofollow() {
		return Optional.ofNullable(nofollow).orElse(false);
	}

	public Boolean getNoindex() {
		return Optional.ofNullable(noindex).orElse(false);
	}

}
