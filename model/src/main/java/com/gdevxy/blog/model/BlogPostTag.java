package com.gdevxy.blog.model;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BlogPostTag {

	ABOUT_ME("About Me"),
	AGILE("Agile"),
	AI("AI"),
	ARCHITECTURE("Architecture"),
	COMMUNITY("Community"),
	CONTENTFUL("Contentful"),
	DEBUGGING("Debugging"),
	GARBAGE_COLLECTOR("Garbage Collector"),
	JAVA("Java"),
	JAVA_21("Java 21"),
	JAVA_23("Java 23"),
	JAVA_24("Java 24"),
	MAVEN("Maven"),
	OCI("OCI"),
	PERFORMANCE("Performance"),
	PROJECT ("Project"),
	QUARKUS("Quarkus"),
	REACTIVE_PROGRAMMING("Reactive Programming"),
	SPRING("Spring"),
	SPRING_WEBFLUX("Spring Webflux"),
	STORY("Story"),
	TECHNICAL("Technical"),
	TESTING("Testing");

	private final String code;

	private static final Map<String, BlogPostTag> LOOKUP = Stream.of(BlogPostTag.values())
			.collect(Collectors.toUnmodifiableMap(BlogPostTag::getCode, t -> t));

	public static BlogPostTag of(String code) {

		return Optional.ofNullable(LOOKUP.get(code)).orElseThrow();
	}

}
