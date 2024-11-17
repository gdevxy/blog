package com.gdevxy.blog.service.contentful;

import com.gdevxy.blog.client.contentful.ContentfulClient;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class ContentfulServiceSupport {

	private ContentfulClient contentfulClient;
	private ContentfulClient contentfulPreviewClient;

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
