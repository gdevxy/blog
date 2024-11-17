package com.gdevxy.blog.model.contentful;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Node {

	DOCUMENT("document"),
	PARAGRAPH("paragraph"),
	HEADING_1("heading-1"),
	HEADING_2("heading-2"),
	HEADING_3("heading-3"),
	HEADING_4("heading-4"),
	HEADING_5("heading-5"),
	HEADING_6("heading-6"),
	OL_LIST("ordered-list"),
	UL_LIST("unordered-list"),
	LIST_ITEM("list-item"),
	HR ("hr"),
	QUOTE("blockquote"),
	EMBEDDED_ENTRY("embedded-entry-block"),
	EMBEDDED_ASSET("embedded-asset-block"),
	EMBEDDED_RESOURCE("embedded-resource-block"),
	TEXT("text"),
	TABLE("table"),
	TABLE_ROW("table-row"),
	TABLE_CELL("table-cell"),
	TABLE_HEADER_CELL("table-header-cell");

	private final String code;

	private static final Map<String, Node> LOOKUP = Stream.of(Node.values())
			.collect(Collectors.toUnmodifiableMap(Node::getCode, t -> t));

	public static Node of(String code) {

		return Optional.ofNullable(LOOKUP.get(code)).orElseThrow();
	}

}
