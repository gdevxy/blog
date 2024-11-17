package com.gdevxy.blog.service.contentful.image;

import com.gdevxy.blog.client.contentful.ContentfulClient;
import com.gdevxy.blog.client.contentful.DefaultContentful;
import com.gdevxy.blog.client.contentful.PreviewContentful;
import com.gdevxy.blog.client.contentful.model.FeaturedImage;
import com.gdevxy.blog.model.Image;
import com.gdevxy.blog.service.contentful.ContentfulServiceSupport;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class ImageService extends ContentfulServiceSupport {

	private final ImageConverter imageConverter;

	public ImageService(@DefaultContentful ContentfulClient contentfulClient,
						@PreviewContentful ContentfulClient previewClient,
						ImageConverter imageConverter) {
		super(contentfulClient, previewClient);
		this.imageConverter = imageConverter;
	}

	public Optional<Image> findImage(Boolean preview, String id) {

		return client(preview).findImage(id).map(imageConverter);
	}

}
