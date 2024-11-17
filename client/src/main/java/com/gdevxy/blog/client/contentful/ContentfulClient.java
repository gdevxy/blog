package com.gdevxy.blog.client.contentful;

import com.gdevxy.blog.client.contentful.model.FeaturedImage;
import com.gdevxy.blog.client.contentful.model.PageBlogPostCollection;

import java.util.Optional;

public interface ContentfulClient {

	PageBlogPostCollection findBlogPosts();

	Optional<FeaturedImage> findImage(String id);

}
