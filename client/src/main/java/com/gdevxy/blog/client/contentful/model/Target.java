package com.gdevxy.blog.client.contentful.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Target {

	private Sys sys;

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	public static class Sys {

		private String id;
		private String type;
		private String linkType;

	}

}
