package com.gdevxy.blog.client.contentful.model.content;

import lombok.ToString;
import org.eclipse.microprofile.graphql.Type;

@ToString
@Type("list-item")
public class ListItem extends Content {

	@Override
	public Node getNode() {
		return Node.LIST_ITEM;
	}

}
