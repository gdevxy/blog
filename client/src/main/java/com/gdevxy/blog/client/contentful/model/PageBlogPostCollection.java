package com.gdevxy.blog.client.contentful.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageBlogPostCollection extends ContentfulCollection {

	@Builder.Default
	private List<PageBlogPost> items = List.of();

}
