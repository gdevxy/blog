package com.gdevxy.blog.service.contentful;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.gdevxy.blog.client.contentful.ContentfulClient;
import com.gdevxy.blog.model.Image;
import com.gdevxy.blog.service.contentful.blogpost.converter.RichImageConverter;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class ContentfulAssetService {

	@Inject
	ContentfulClient contentfulClient;
	@Inject
	RichImageConverter richImageConverter;

	public Uni<Image> findImage(String id, @Nullable String previewToken) {

		return contentfulClient.findImage(id, previewToken).map(richImageConverter);
	}

}
