package com.gdevxy.blog.service.contentful.blogpost.converter;

import java.util.function.Function;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.client.contentful.model.FeaturedImage;
import com.gdevxy.blog.model.Image;

@ApplicationScoped
public class ImageConverter implements Function<FeaturedImage, Image> {

	@Override
	public Image apply(FeaturedImage image) {

		return Image.builder()
			.title(image.getTitle())
			.description(image.getDescription())
			.url(image.getUrl())
			.width(image.getWidth())
			.height(image.getHeight())
			.contentType(image.getContentType())
			.build();
	}

}
