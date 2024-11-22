package com.gdevxy.blog.client.contentful.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@ToString
public abstract class ContentfulCollection {

	private Long total;
	private Long limit;
	private Long skip;

}
