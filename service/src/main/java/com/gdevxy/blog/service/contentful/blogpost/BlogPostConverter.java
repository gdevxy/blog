package com.gdevxy.blog.service.contentful.blogpost;

import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.SeoFields;
import com.gdevxy.blog.client.contentful.model.content.Mark;
import com.gdevxy.blog.client.contentful.model.content.RichContent;
import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.contentful.Node;
import com.gdevxy.blog.service.contentful.image.ImageConverter;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostConverter implements Function<PageBlogPost, BlogPost> {

	private final ImageConverter imageConverter;

	@Override
	public BlogPost apply(PageBlogPost p) {

		return BlogPost.builder()
				.slug(p.getSlug())
				.title(p.getTitle())
				.description(p.getShortDescription())
				.publishedDate(p.getPublishedDate())
				.image(Optional.ofNullable(p.getFeaturedImage()).map(imageConverter).orElse(null))
				.seo(toSeo(p.getSeoFields()).orElse(null))
				.blocks(toContentBlocks(p.getContent().getJson().getContent()))
				.build();
	}

	private static Optional<BlogPost.Seo> toSeo(@Nullable SeoFields seoFields) {

		return Optional.ofNullable(seoFields).map(f -> {

			var noFollow = f.getNofollow() ? "nofollow" : null;
			var noIndex = f.getNoindex() ? "noindex" : null;
			var robotHint = Stream.of(noFollow, noIndex).filter(Objects::nonNull).collect(Collectors.joining(", "));

			return BlogPost.Seo.builder()
					.title(f.getPageTitle())
					.description(f.getPageDescription())
					.robotHint(robotHint)
					.build();
		});
	}

	private static List<BlogPost.ContentBlock> toContentBlocks(List<RichContent> contents) {

		return contents.stream()
				.map(BlogPostConverter::toContent)
				.toList();
	}

	private static BlogPost.ContentBlock toContent(RichContent content) {

		return BlogPost.ContentBlock.builder()
				.type(Node.of(content.getNodeType()))
				.value(content.getValue())
				.marks(content.getMarks().stream().map(Mark::getType).map(com.gdevxy.blog.model.contentful.Mark::of).collect(Collectors.toUnmodifiableSet()))
				.blocks(toContentBlocks(content.getContent()))
				.build();
	}

}
