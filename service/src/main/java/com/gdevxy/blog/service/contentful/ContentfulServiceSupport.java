package com.gdevxy.blog.service.contentful;

import com.gdevxy.blog.client.contentful.ContentfulClient;

public abstract class ContentfulServiceSupport {

	private final ContentfulClient contentfulClient;
	private final ContentfulClient contentfulPreviewClient;

	protected ContentfulServiceSupport(ContentfulClient contentfulClient, ContentfulClient previewClient) {
		this.contentfulClient = contentfulClient;
		this.contentfulPreviewClient = previewClient;
	}

	protected ContentfulClient client(Boolean preview) {

		if (preview) {
			return contentfulPreviewClient;
		}

		return contentfulClient;
	}

}
