package com.gdevxy.blog.client.contentful.model.content;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.graphql.Type;

@Getter
@Setter
@ToString
@Type("embedded-entry-block")
public class EmbeddedEntryBlock extends Content {

	private Data data;

	@Override
	public Node getNode() {
		return Node.EMBEDDED_ENTRY_BLOCK;
	}

	@Getter
	@Setter
	@ToString
	public static class Data {

		private Target target;

		@Getter
		@Setter
		@ToString
		public static class Target {

			private Sys sys;

			@Getter
			@Setter
			@ToString
			public static class Sys {

				private String id;
				private String type;
				private String linkType;

			}
		}

	}

}
