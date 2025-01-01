package com.gdevxy.blog.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class RssDateTimeFormatter extends XmlAdapter<String, ZonedDateTime> {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy");

	@Override
	public ZonedDateTime unmarshal(String v) {
		return ZonedDateTime.parse(v, FORMATTER);
	}

	@Override
	public String marshal(ZonedDateTime v) {
		return v.format(FORMATTER);
	}

}
