package com.gdevxy.blog.client.contentful.model.content;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.graphql.Type;

@Getter
@Setter
@ToString
@Type("hyper-link")
public class HyperLink extends Content {

	private Data data;

	@Override
	public Node getNode() {
		return Node.HYPERLINK;
	}

	@Getter
	@Setter
	@ToString
	public static class Data {

		private String uri;
	}

}
