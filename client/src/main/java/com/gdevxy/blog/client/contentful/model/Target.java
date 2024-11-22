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
public class Target {

	private Sys sys;

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@RegisterForReflection
	@ToString
	public static class Sys {

		private String id;
		private String type;
		private String linkType;

	}

}
