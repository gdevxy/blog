package com.gdevxy.blog.component.extensions;

import io.quarkus.qute.TemplateExtension;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@TemplateExtension
public class TemplateExtensions {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	public static String formatted(ZonedDateTime dateTime) {
		return FORMATTER.format(dateTime);
	}

}