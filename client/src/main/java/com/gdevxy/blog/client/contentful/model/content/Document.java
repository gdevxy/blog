package com.gdevxy.blog.client.contentful.model.content;

import lombok.ToString;
import org.eclipse.microprofile.graphql.Type;

@ToString
@Type("document")
public class Document extends Content {

	@Override
	public Node getNode() {
		return Node.DOCUMENT;
	}

}
