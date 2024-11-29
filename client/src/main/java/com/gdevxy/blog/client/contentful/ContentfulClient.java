package com.gdevxy.blog.client.contentful;

import com.gdevxy.blog.client.contentful.model.ComponentRichImage;
import com.gdevxy.blog.client.contentful.model.FeaturedImage;
import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.PageBlogPostCollection;

import java.util.Optional;

public interface ContentfulClient {

	Optional<PageBlogPost> findBlogPost(String slug);

	PageBlogPostCollection findBlogPosts();

	Optional<ComponentRichImage> findImage(String id);

}
