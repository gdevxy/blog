package com.gdevxy.blog.client.contentful.model.content;

import lombok.ToString;
import org.eclipse.microprofile.graphql.Type;

@ToString
@Type("ordered-list")
public class OrderedList extends Content {

	@Override
	public Node getNode() {
		return Node.ORDERED_LIST;
	}

}
