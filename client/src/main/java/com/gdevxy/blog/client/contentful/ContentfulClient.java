package com.gdevxy.blog.client.contentful;

import com.gdevxy.blog.client.contentful.model.ComponentRichImage;
import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.PageBlogPostCollection;
import com.gdevxy.blog.client.contentful.model.Pagination;
import com.gdevxy.blog.client.contentful.model.RecentPageBlogPostCollection;

import java.util.Optional;
import java.util.Set;

public interface ContentfulClient {

	Optional<PageBlogPost> findBlogPost(String slug);

	PageBlogPostCollection findBlogPosts(Pagination pagination, Set<String> tags);

	RecentPageBlogPostCollection findRecentBlogPosts();

	Optional<ComponentRichImage> findImage(String id);

}
