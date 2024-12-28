package com.gdevxy.blog.client.contentful;

import java.util.Optional;
import java.util.Set;

import com.gdevxy.blog.client.contentful.model.ComponentRichImage;
import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.PageBlogPostCollection;
import com.gdevxy.blog.client.contentful.model.Pagination;
import com.gdevxy.blog.client.contentful.model.RecentPageBlogPostCollection;
import io.smallrye.mutiny.Uni;

public interface ContentfulClient {

	Uni<PageBlogPost> findBlogPost(String slug);

	Uni<PageBlogPost> findBlogPost(String slug, String previewToken);

	Uni<PageBlogPostCollection> findBlogPosts(Pagination pagination, Set<String> tags);

	Uni<PageBlogPostCollection> findBlogPosts(Pagination pagination, Set<String> tags, String previewToken);

	Uni<RecentPageBlogPostCollection> findRecentBlogPosts();

	Uni<RecentPageBlogPostCollection> findRecentBlogPosts(String previewToken);

	Uni<ComponentRichImage> findImage(String id);

	Uni<ComponentRichImage> findImage(String id, String previewToken);

}
