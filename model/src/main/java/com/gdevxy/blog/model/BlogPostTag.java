package com.gdevxy.blog.model;

public record BlogPostTag(String value, String code) {

	public BlogPostTag(String value) {
		this(value, value.replaceAll("\\s", ""));
	}

}
