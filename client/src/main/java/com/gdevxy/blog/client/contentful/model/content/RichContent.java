package com.gdevxy.blog.client.contentful.model.content;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RichContent {

	private String nodeType;
	private String value;
	private Data data;
	@Builder.Default
	private Set<Mark> marks = Set.of();
	@Builder.Default
	private List<RichContent> content = List.of();

}
