package com.gdevxy.blog.client.contentful.model.content;

import lombok.ToString;
import org.eclipse.microprofile.graphql.Type;

@ToString
@Type("heading-2")
public class Heading2 extends Content {

	@Override
	public Node getNode() {
		return Node.HEADING_2;
	}

}
