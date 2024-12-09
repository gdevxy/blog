package com.gdevxy.blog.mother;

import com.gdevxy.blog.model.BlogPost;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SeoMother {

	public BlogPost.Seo.SeoBuilder basic() {

		return BlogPost.Seo.builder().title("seo-title").description("seo-description").robotHint("robot-hint");
	}

}
