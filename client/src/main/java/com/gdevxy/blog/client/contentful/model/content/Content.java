package com.gdevxy.blog.client.contentful.model.content;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RegisterForReflection(
	targets = {
		Document.class,
		EmbeddedEntryBlock.class,
		Heading1.class,
		Heading2.class,
		Heading3.class,
		Heading4.class,
		Heading5.class,
		Heading6.class,
		HyperLink.class,
		ListItem.class,
		Paragraph.class,
		OrderedList.class,
		Text.class,
		UnorderedList.class
	}
)
public abstract class Content implements IContent {

	private List<IContent> content;

	public List<IContent> getContent() {

		if (content == null) {
			return List.of();
		}

		return content;
	}

}