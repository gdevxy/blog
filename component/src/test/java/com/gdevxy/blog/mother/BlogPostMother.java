package com.gdevxy.blog.mother;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.BlogPostTag;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BlogPostMother {

	public BlogPost.BlogPostBuilder basic() {

		return BlogPost.builder().title("title")
			.description("description")
			.slug("slug")
			.publishedDate(ZonedDateTime.now())
			.tags(List.of(new BlogPostTag("Architecture"), new BlogPostTag("Agile")))
			.image(ImageMother._404().build())
			.seo(SeoMother.basic().build());
	}

}
