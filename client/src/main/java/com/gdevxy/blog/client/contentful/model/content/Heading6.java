package com.gdevxy.blog.client.contentful.model.content;

import lombok.ToString;
import org.eclipse.microprofile.graphql.Type;

@ToString
@Type("heading-6")
public class Heading6 extends Content {

	@Override
	public Node getNode() {
		return Node.HEADING_6;
	}

}
