package com.gdevxy.blog.client.contentful;

import java.util.Set;

import com.gdevxy.blog.client.contentful.model.ComponentRichImage;
import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.PageBlogPostCollection;
import com.gdevxy.blog.client.contentful.model.Page;
import com.gdevxy.blog.client.contentful.model.LightPageBlogPostCollection;
import io.smallrye.mutiny.Uni;

public interface ContentfulClient {

	Uni<PageBlogPost> findBlogPost(String slug);

	Uni<PageBlogPost> findBlogPost(String slug, String previewToken);

	Uni<PageBlogPostCollection> findBlogPosts(Page pagination, Set<String> tags);

	Uni<PageBlogPostCollection> findBlogPosts(Page pagination, Set<String> tags, String previewToken);

	Uni<LightPageBlogPostCollection> findLightBlogPosts(Page pagination);

	Uni<LightPageBlogPostCollection> findLightBlogPosts(Page pagination, String previewToken);

	Uni<ComponentRichImage> findImage(String id);

	Uni<ComponentRichImage> findImage(String id, String previewToken);

}
