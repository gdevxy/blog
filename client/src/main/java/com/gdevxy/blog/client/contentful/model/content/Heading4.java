package com.gdevxy.blog.client.contentful.model.content;

import lombok.ToString;
import org.eclipse.microprofile.graphql.Type;

@ToString
@Type("heading-4")
public class Heading4 extends Content {

	@Override
	public Node getNode() {
		return Node.HEADING_4;
	}

}
