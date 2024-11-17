package com.gdevxy.blog.model.contentful;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Mark {

	BOLD("bold"),
	ITALIC("italic"),
	UNDERLINE("underline"),
	CODE("code"),
	SUPERSCRIPT("superscript"),
	SUBSCRIPT("subscript");

	private final String code;

	private static final Map<String, Mark> LOOKUP = Stream.of(Mark.values())
			.collect(Collectors.toUnmodifiableMap(Mark::getCode, t -> t));

	public static Mark of(String code) {

		return Optional.ofNullable(LOOKUP.get(code)).orElseThrow();
	}
	
}
