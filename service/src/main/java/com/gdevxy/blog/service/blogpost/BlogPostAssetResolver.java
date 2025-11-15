package com.gdevxy.blog.service.blogpost;

import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.content.RichContent;
import com.gdevxy.blog.model.Image;
import com.gdevxy.blog.service.contentful.ContentfulAssetService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostAssetResolver {

	private final ContentfulAssetService contentfulAssetService;

	public Uni<Map<String, Image>> resolve(PageBlogPost content, @Nullable String previewToken) {

		return Multi.createFrom().iterable(content.getContent().getJson().getContent())
				.flatMap(c -> Multi.createFrom().uni(resolveImage(c, previewToken)))
				.collect()
				.asMap(Image::getId, i -> i, (i1, _) -> i1);
	}

	private Uni<Image> resolveImage(RichContent content, @Nullable String previewToken) {

		if (!"embedded-entry-block".equals(content.getNodeType())) {
			return Uni.createFrom().nullItem();
		}

		return contentfulAssetService.findImage(content.getData().getTarget().getSys().getId(), previewToken);
	}

}
