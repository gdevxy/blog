package com.gdevxy.blog.service.contentful.blogpost.converter;

import java.util.function.Function;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.client.contentful.model.ComponentRichImage;
import com.gdevxy.blog.model.Image;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class RichImageConverter implements Function<ComponentRichImage, Image> {

	private final ImageConverter imageConverter;

	@Override
	public Image apply(ComponentRichImage i) {

		return imageConverter.apply(i.getImage()).toBuilder()
			.fullWidth(i.getFullWidth())
			.build();
	}

}
