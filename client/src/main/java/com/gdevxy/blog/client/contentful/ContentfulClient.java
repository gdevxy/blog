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

	Optional<PageBlogPost> findBlogPost(String slug, String previewToken);

	PageBlogPostCollection findBlogPosts(Pagination pagination, Set<String> tags);

	PageBlogPostCollection findBlogPosts(Pagination pagination, Set<String> tags, String previewToken);

	RecentPageBlogPostCollection findRecentBlogPosts();

	RecentPageBlogPostCollection findRecentBlogPosts(String previewToken);

	Optional<ComponentRichImage> findImage(String id);

	Optional<ComponentRichImage> findImage(String id, String previewToken);

}
