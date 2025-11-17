package com.gdevxy.blog.service.blogpost.converter;

import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.SeoFields;
import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.BlogPostTag;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostConverter {

	private final ImageConverter imageConverter;

	public BlogPost convert(PageBlogPost p, Long rating) {

		return BlogPost.builder()
			.id(p.getSys().getId())
			.slug(p.getSlug())
			.title(p.getTitle())
			.description(p.getShortDescription())
			.publishedDate(p.getPublishedDate())
			.rating(NumberFormat.getCompactNumberInstance().format(rating))
			.image(Optional.ofNullable(p.getFeaturedImage()).map(imageConverter).orElse(null))
			.seo(toSeo(p.getSeoFields()).orElse(null))
			.tags(p.getTags().stream().map(BlogPostTag::new).sorted(Comparator.comparing(BlogPostTag::value)).toList())
			.build();
	}

	private static Optional<BlogPost.Seo> toSeo(@Nullable SeoFields seoFields) {

		return Optional.ofNullable(seoFields).map(f -> {

			var noFollow = f.getNofollow() ? "nofollow" : "follow";
			var noIndex = f.getNoindex() ? "noindex" : "index";

			return BlogPost.Seo.builder()
					.title(f.getPageTitle())
					.description(f.getPageDescription())
					.robotHint(String.join(",", noFollow, noIndex))
					.build();
		});
	}

}
