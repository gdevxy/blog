package com.gdevxy.blog.service.contentful.image;

import com.gdevxy.blog.client.contentful.model.FeaturedImage;
import com.gdevxy.blog.model.Image;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.function.Function;

@ApplicationScoped
public class ImageConverter implements Function<FeaturedImage, Image> {

	@Override
	public Image apply(FeaturedImage fi) {

		return Image.builder()
				.url(fi.getUrl())
				.width(fi.getWidth())
				.height(fi.getHeight())
				.contentType(fi.getContentType())
				.build();
	}

}
