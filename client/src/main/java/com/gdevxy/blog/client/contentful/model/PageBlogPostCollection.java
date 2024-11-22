package com.gdevxy.blog.client.contentful.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@ToString
public class PageBlogPostCollection extends ContentfulCollection {

	private List<PageBlogPost> items;

	public List<PageBlogPost> getItems() {
		return Optional.ofNullable(items).orElse(List.of());
	}

}
