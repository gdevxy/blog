package com.gdevxy.blog.client.contentful.model.content;

import lombok.ToString;
import org.eclipse.microprofile.graphql.Type;

@ToString
@Type("heading-1")
public class Heading1 extends Content {

	@Override
	public Node getNode() {
		return Node.HEADING_1;
	}

}
