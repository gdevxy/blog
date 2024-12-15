package com.gdevxy.blog.client.contentful.model.content;

import java.util.List;

import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;

@JsonbTypeInfo(key = "nodeType", value = {
	@JsonbSubtype(alias = "document", type = Document.class),
	@JsonbSubtype(alias = "embedded-entry-block", type = EmbeddedEntryBlock.class),
	@JsonbSubtype(alias = "heading-1", type = Heading1.class),
	@JsonbSubtype(alias = "heading-2", type = Heading2.class),
	@JsonbSubtype(alias = "heading-3", type = Heading3.class),
	@JsonbSubtype(alias = "heading-4", type = Heading4.class),
	@JsonbSubtype(alias = "heading-5", type = Heading5.class),
	@JsonbSubtype(alias = "heading-6", type = Heading6.class),
	@JsonbSubtype(alias = "hyperlink", type = HyperLink.class),
	@JsonbSubtype(alias = "list-item", type = ListItem.class),
	@JsonbSubtype(alias = "paragraph", type = Paragraph.class),
	@JsonbSubtype(alias = "ordered-list", type = OrderedList.class),
	@JsonbSubtype(alias = "text", type = Text.class),
	@JsonbSubtype(alias = "unordered-list", type = UnorderedList.class)
})
public interface IContent {

	Node getNode();
	List<IContent> getContent();

}