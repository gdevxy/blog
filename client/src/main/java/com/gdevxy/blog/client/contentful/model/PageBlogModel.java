package com.gdevxy.blog.client.contentful.model;

import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@ToString
public class PageBlogModel {

	@Builder.Default
	private final List<Field> fields = List.of();

	@Getter
	@Builder
	@Jacksonized
	@ToString
	public static class Field {

		private final String id;
		private final Items items;

		@Getter
		@Builder
		@Jacksonized
		@ToString
		public static class Items {

			private final List<Validation> validations;

			@Getter
			@Builder
			@Jacksonized
			@ToString
			public static class Validation {

				@Builder.Default
				private List<String> in = List.of();

			}

		}

	}

}
