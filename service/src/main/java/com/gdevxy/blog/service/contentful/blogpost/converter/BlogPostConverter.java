package com.gdevxy.blog.service.contentful.blogpost.converter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.client.contentful.ContentfulClient;
import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.SeoFields;
import com.gdevxy.blog.client.contentful.model.content.EmbeddedEntryBlock;
import com.gdevxy.blog.client.contentful.model.content.HyperLink;
import com.gdevxy.blog.client.contentful.model.content.IContent;
import com.gdevxy.blog.client.contentful.model.content.Text;
import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.BlogPostTag;
import com.gdevxy.blog.model.Image;
import com.gdevxy.blog.model.contentful.Mark;
import com.gdevxy.blog.model.contentful.Node;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostConverter {

	private final ContentfulClient contentfulClient;
	private final ImageConverter imageConverter;
	private final RichImageConverter richImageConverter;

	public BlogPost convert(PageBlogPost p, @Nullable String previewToken) {

		return convert(false, p, previewToken);
	}

	public BlogPost convertWithContent(PageBlogPost p, @Nullable String previewToken) {

		return convert(true, p, previewToken);
	}

	private  BlogPost convert(boolean withContent, PageBlogPost p, @Nullable String previewToken) {

		return BlogPost.builder()
			.slug(p.getSlug())
			.title(p.getTitle())
			.description(p.getShortDescription())
			.publishedDate(p.getPublishedDate())
			.image(Optional.ofNullable(p.getFeaturedImage()).map(imageConverter).orElse(null))
			.seo(toSeo(p.getSeoFields()).orElse(null))
			.blocks(withContent ? toContentBlocks(p.getContent().getJson().getContent(), previewToken) : List.of())
			.tags(p.getTags().stream().map(BlogPostTag::of).collect(Collectors.toUnmodifiableSet()))
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

	private List<BlogPost.ContentBlock> toContentBlocks(List<IContent> contents, @Nullable String previewToken) {

		return contents.stream()
				.map(content -> toContent(content, previewToken))
				.toList();
	}

	private BlogPost.ContentBlock toContent(IContent content, @Nullable String previewToken) {

		return BlogPost.ContentBlock.builder()
				.node(Node.valueOf(content.getNode().name()))
				.value(toValue(content))
				.marks(toMarks(content))
				.blocks(toContentBlocks(content.getContent(), previewToken))
				.image(toImage(content).orElse(null))
				.build();
	}

	private Set<Mark> toMarks(IContent content) {

		return switch(content) {
			case Text t -> t.getMarks().stream().map(Mark::of).collect(Collectors.toUnmodifiableSet());
			default -> Set.of();
		};
	}

	private String toValue(IContent content) {

		return switch(content) {
			case HyperLink l -> l.getData().getUri();
			case Text t -> t.getValue();
			default -> null;
		};
	}

	private Optional<Image> toImage(IContent content) {

		return switch(content) {
			case EmbeddedEntryBlock b -> contentfulClient.findImage(b.getData().getTarget().getSys().getId()).map(richImageConverter);
			default -> Optional.empty();
		};
	}

}
