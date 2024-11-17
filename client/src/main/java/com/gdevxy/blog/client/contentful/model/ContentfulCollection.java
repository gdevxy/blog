package com.gdevxy.blog.client.contentful.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class ContentfulCollection {

	private Long total;
	private Long limit;
	private Long skip;

}
