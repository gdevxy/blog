package com.gdevxy.blog.component.extension;

import io.quarkus.qute.TemplateExtension;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TemplateExtensions {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' h:mma");

	@TemplateExtension
	public static String formatted(ZonedDateTime dateTime) {
		return FORMATTER.format(dateTime);
	}

}