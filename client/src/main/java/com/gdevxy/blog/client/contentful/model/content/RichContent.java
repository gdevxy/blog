package com.gdevxy.blog.client.contentful.model.content;

import lombok.*;

import java.util.List;
import java.util.Optional;
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
	private Set<Mark> marks;
	private List<RichContent> content = List.of();

	public Set<Mark> getMarks() {
		return Optional.ofNullable(marks).orElse(Set.of());
	}

	public List<RichContent> getContent() {
		return Optional.ofNullable(content).orElse(List.of());
	}

}
