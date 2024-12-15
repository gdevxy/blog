package com.gdevxy.blog.client.contentful.model.content;

import lombok.ToString;
import org.eclipse.microprofile.graphql.Type;

@ToString
@Type("paragraph")
public class Paragraph extends Content {

	@Override
	public Node getNode() {
		return Node.PARAGRAPH;
	}
}
