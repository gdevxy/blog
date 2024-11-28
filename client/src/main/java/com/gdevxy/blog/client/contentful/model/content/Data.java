package com.gdevxy.blog.client.contentful.model.content;

import com.gdevxy.blog.client.contentful.model.Target;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@ToString
public class Data {

	private String uri;
	private Target target;

}
