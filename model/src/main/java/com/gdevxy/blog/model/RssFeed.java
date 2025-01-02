package com.gdevxy.blog.model;

import java.time.ZonedDateTime;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@XmlRootElement(name = "rss")
@XmlAccessorType(XmlAccessType.FIELD)
public class RssFeed {

	@XmlAttribute
	@Builder.Default
	private String version = "2.0";
	@XmlElement
	private Channel channel;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@RegisterForReflection
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Channel {

		@XmlElement
		@Builder.Default
		private String title = "gdevxy";
		@XmlElement
		@Builder.Default
		private String link = "https://www.gdevxy.ch";
		@XmlElement
		@Builder.Default
		private String description = "gdevxy technical blog";
		@XmlElement
		@Builder.Default
		private String language = "en";
		@XmlElement(name = "category")
		@Builder.Default
		private List<String> categories = List.of("java", "quarkus", "spring", "software-development", "programming");
		@Builder.Default
		@XmlElement(name = "item")
		private List<Item> items =List.of();
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@RegisterForReflection
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Item {

		@XmlElement
		private String title;
		@XmlElement
		private String link;
		@XmlElement
		private String description;
		@XmlElement
		@XmlJavaTypeAdapter(RssDateTimeFormatter.class)
		private ZonedDateTime pubDate;

	}

}
