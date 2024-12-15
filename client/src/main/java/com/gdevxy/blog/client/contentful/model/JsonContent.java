package com.gdevxy.blog.client.contentful.model;

import com.gdevxy.blog.client.contentful.model.content.Document;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@ToString
public class JsonContent {

	private Document json;

}
