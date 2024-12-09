package com.gdevxy.blog.mother;

import com.gdevxy.blog.model.Image;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImageMother {

	public Image.ImageBuilder _404() {

		return Image.builder().title("image-title")
			.description("image-description")
			.contentType("image/jpeg")
			.url("app/404.jpeg")
			.width(512)
			.height(512)
			.fullWidth(true);
	}

}
