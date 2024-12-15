package com.gdevxy.blog.client.contentful.model.content;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.graphql.Type;

@Getter
@Setter
@ToString
@Type("text")
public class Text extends Content {

	private String value;
	private Set<String> marks;

	@Override
	public Node getNode() {
		return Node.TEXT;
	}

	public Set<String> getMarks() {

		if (marks == null) {
			return Set.of();
		}

		return marks;
	}

}
