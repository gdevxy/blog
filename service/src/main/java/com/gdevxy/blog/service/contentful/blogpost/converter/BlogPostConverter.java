package com.gdevxy.blog.service.contentful.blogpost.converter;

import com.gdevxy.blog.client.contentful.ContentfulClient;
import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.SeoFields;
import com.gdevxy.blog.client.contentful.model.content.Data;
import com.gdevxy.blog.client.contentful.model.content.Mark;
import com.gdevxy.blog.client.contentful.model.content.RichContent;
import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.Image;
import com.gdevxy.blog.model.contentful.Node;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostConverter {

	private final ImageConverter imageConverter;
	private final RichImageConverter richImageConverter;

	public BlogPost convert(PageBlogPost p) {

		return convert(null, p);
	}

	public BlogPost convert(@Nullable ContentfulClient client, PageBlogPost p) {

		return BlogPost.builder()
				.slug(p.getSlug())
				.title(p.getTitle())
				.description(p.getShortDescription())
				.publishedDate(p.getPublishedDate())
				.image(Optional.ofNullable(p.getFeaturedImage()).map(imageConverter).orElse(null))
				.seo(toSeo(p.getSeoFields()).orElse(null))
				.blocks(toContentBlocks(client, p.getContent().getJson().getContent()))
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

	private List<BlogPost.ContentBlock> toContentBlocks(@Nullable ContentfulClient client, List<RichContent> contents) {

		return contents.stream()
				.map(content -> toContent(client, content))
				.toList();
	}

	private BlogPost.ContentBlock toContent(@Nullable ContentfulClient client, RichContent content) {

		var nodeType = Node.of(content.getNodeType());

		return BlogPost.ContentBlock.builder()
				.node(nodeType)
				.value(toValue(nodeType, content))
				.marks(content.getMarks().stream().map(Mark::getType).map(com.gdevxy.blog.model.contentful.Mark::of).collect(Collectors.toUnmodifiableSet()))
				.blocks(toContentBlocks(client, content.getContent()))
				.image(toImage(client, nodeType, content.getData()).orElse(null))
				.build();
	}

	private String toValue(Node node, RichContent content) {

		return switch(node) {
			case HYPERLINK -> content.getData().getUri();
			default -> content.getValue();
		};
	}

	private Optional<Image> toImage(@Nullable ContentfulClient client, Node nodeType, Data embeddedEntry) {

		if (nodeType != Node.EMBEDDED_ENTRY || client == null) {
			return Optional.empty();
		}

		return client.findImage(embeddedEntry.getTarget().getSys().getId()).map(richImageConverter);
	}

}
