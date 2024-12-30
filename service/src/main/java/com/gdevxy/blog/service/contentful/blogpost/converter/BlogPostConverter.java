package com.gdevxy.blog.service.contentful.blogpost.converter;

import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.PageBlogPostCollection;
import com.gdevxy.blog.client.contentful.model.SeoFields;
import com.gdevxy.blog.client.contentful.model.content.Mark;
import com.gdevxy.blog.client.contentful.model.content.RichContent;
import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.BlogPostTag;
import com.gdevxy.blog.model.contentful.Node;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostConverter {

	private final ImageConverter imageConverter;

	public BlogPost.BlogPostBuilder convert(PageBlogPost p, Long rating) {

		return BlogPost.builder()
			.id(p.getSys().getId())
			.slug(p.getSlug())
			.title(p.getTitle())
			.description(p.getShortDescription())
			.publishedDate(p.getPublishedDate())
			.rating(NumberFormat.getCompactNumberInstance().format(rating))
			.image(Optional.ofNullable(p.getFeaturedImage()).map(imageConverter).orElse(null))
			.seo(toSeo(p.getSeoFields()).orElse(null))
			.blocks(toContentBlocks(p.getContent().getJson().getContent()))
			.tags(p.getTags().stream().map(BlogPostTag::new).collect(Collectors.toUnmodifiableSet()))
			.relatedBlogPosts(Optional.ofNullable(p.getRelatedBlogPostsCollection())
				.map(PageBlogPostCollection::getItems)
				.stream()
				.flatMap(List::stream)
				.map(this::toRelatedBlogPost)
				.toList());
	}

	private BlogPost.RelatedBlogPost toRelatedBlogPost(PageBlogPost p) {

		return BlogPost.RelatedBlogPost.builder()
			.slug(p.getSlug())
			.title(p.getTitle())
			.description(p.getShortDescription())
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

	private List<BlogPost.ContentBlock> toContentBlocks(List<RichContent> contents) {

		return contents.stream()
				.map(this::toContent)
				.toList();
	}

	private BlogPost.ContentBlock toContent(RichContent content) {

		var nodeType = Node.of(content.getNodeType());

		return BlogPost.ContentBlock.builder()
				.node(nodeType)
				.value(toValue(nodeType, content))
				.marks(content.getMarks().stream().map(Mark::getType).map(com.gdevxy.blog.model.contentful.Mark::of).collect(Collectors.toUnmodifiableSet()))
				.blocks(toContentBlocks(content.getContent()))
				.build();
	}

	private String toValue(Node node, RichContent content) {

		return switch(node) {
			case EMBEDDED_ENTRY -> content.getData().getTarget().getSys().getId();
			case HYPERLINK -> content.getData().getUri();
			default -> content.getValue();
		};
	}

}
