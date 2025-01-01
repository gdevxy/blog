package com.gdevxy.blog.client.contentful.model;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@ToString
public class LightPageBlogPostCollection extends ContentfulCollection {

	private List<LightPageBlogPost> items;

	public List<LightPageBlogPost> getItems() {
		return Optional.ofNullable(items).orElse(List.of());
	}

}
