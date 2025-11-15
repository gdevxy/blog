package com.gdevxy.blog.service.blogpost.converter;

import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.PageBlogPostCollection;
import com.gdevxy.blog.client.contentful.model.SeoFields;
import com.gdevxy.blog.client.contentful.model.content.Mark;
import com.gdevxy.blog.client.contentful.model.content.RichContent;
import com.gdevxy.blog.model.BlogPostComment;
import com.gdevxy.blog.model.BlogPostDetail;
import com.gdevxy.blog.model.BlogPostTag;
import com.gdevxy.blog.model.Image;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostDetailConverter {

	private final ImageConverter imageConverter;

	public BlogPostDetail convert(PageBlogPost p, Boolean liked, List<BlogPostComment> comments, Map<String, Image> images) {

		return BlogPostDetail.builder()
			.id(p.getSys().getId())
			.slug(p.getSlug())
			.title(p.getTitle())
			.description(p.getShortDescription())
			.publishedDate(p.getPublishedDate())
			.image(Optional.ofNullable(p.getFeaturedImage()).map(imageConverter).orElse(null))
			.seo(toSeo(p.getSeoFields()).orElse(null))
			.blocks(toContentBlocks(p.getContent().getJson().getContent(), images))
			.tags(p.getTags().stream().map(BlogPostTag::new).collect(Collectors.toUnmodifiableSet()))
			.comments(comments)
			.liked(liked)
			.relatedBlogPosts(Optional.ofNullable(p.getRelatedBlogPostsCollection())
				.map(PageBlogPostCollection::getItems)
				.stream()
				.flatMap(List::stream)
				.map(this::toRelatedBlogPost)
				.toList())
			.build();
	}

	private BlogPostDetail.RelatedBlogPost toRelatedBlogPost(PageBlogPost p) {

		return BlogPostDetail.RelatedBlogPost.builder()
			.slug(p.getSlug())
			.title(p.getTitle())
			.description(p.getShortDescription())
			.build();
	}

	private static Optional<BlogPostDetail.Seo> toSeo(@Nullable SeoFields seoFields) {

		return Optional.ofNullable(seoFields).map(f -> {

			var noFollow = f.getNofollow() ? "nofollow" : "follow";
			var noIndex = f.getNoindex() ? "noindex" : "index";

			return BlogPostDetail.Seo.builder()
					.title(f.getPageTitle())
					.description(f.getPageDescription())
					.robotHint(String.join(",", noFollow, noIndex))
					.build();
		});
	}

	private List<BlogPostDetail.ContentBlock> toContentBlocks(List<RichContent> contents, Map<String, Image> images) {

		return contents.stream()
				.map(c -> toContent(c, images))
				.toList();
	}

	private BlogPostDetail.ContentBlock toContent(RichContent content, Map<String, Image> images) {

		var marks = content.getMarks()
				.stream()
				.map(Mark::getType)
				.collect(Collectors.toUnmodifiableSet());

		var value = toValue(content);
		return BlogPostDetail.ContentBlock.builder()
				.node(content.getNodeType())
				.value(value)
				.image(toImage(value, images))
				.marks(marks)
				.blocks(toContentBlocks(content.getContent(), images))
				.build();
	}

	private String toValue(RichContent content) {

		return switch(content.getNodeType()) {
			case "embedded-entry-block" -> content.getData().getTarget().getSys().getId();
			case "hyperlink" -> content.getData().getUri();
			default -> content.getValue();
		};
	}

	private Image toImage(@Nullable String assetId, Map<String, Image> images) {

		if (assetId == null) {
			return null;
		}

		return images.get(assetId);
	}

}
