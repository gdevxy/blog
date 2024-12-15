package com.gdevxy.blog.client.contentful.model.content;

import lombok.ToString;
import org.eclipse.microprofile.graphql.Type;

@ToString
@Type("unordered-list")
public class UnorderedList extends Content {

	@Override
	public Node getNode() {
		return Node.UNORDERED_LIST;
	}

}
