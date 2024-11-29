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
public class ComponentRichImage {

	private FeaturedImage image;
	private Boolean fullWidth;

}