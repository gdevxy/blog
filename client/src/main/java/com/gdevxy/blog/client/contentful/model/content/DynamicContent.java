package com.gdevxy.blog.client.contentful.model.content;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@ToString
public class DynamicContent {

	private RichContent json;

}
