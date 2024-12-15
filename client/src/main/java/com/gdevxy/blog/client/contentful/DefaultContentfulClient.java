package com.gdevxy.blog.client.contentful;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.client.GraphQlQueryLoader;
import com.gdevxy.blog.client.contentful.model.ComponentRichImage;
import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.PageBlogPostCollection;
import com.gdevxy.blog.client.contentful.model.Pagination;
import com.gdevxy.blog.client.contentful.model.RecentPageBlogPostCollection;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class DefaultContentfulClient extends ContentfulClientSupport implements ContentfulClient {

	private final GraphQlQueryLoader queryLoader;

	public DefaultContentfulClient(@GraphQLClient("contentful") DynamicGraphQLClient client, GraphQlQueryLoader queryLoader) {
		super(client);
		this.queryLoader = queryLoader;
	}

	@Override
	public Optional<PageBlogPost> findBlogPost(String slug) {

		return findBlogPost(slug, null);
	}

	@Override
	public Optional<PageBlogPost> findBlogPost(String slug, String previewToken) {

		var response = executeQuery(() -> queryLoader.loadQuery("find-blog-post"), Map.of("slug", slug), previewToken);

		return response.getObject(PageBlogPostCollection.class, "pageBlogPostCollection").getItems().stream().findAny();
	}

	@Override
	public PageBlogPostCollection findBlogPosts(Pagination pagination, Set<String> tags) {

		return findBlogPosts(pagination, tags, null);
	}

	@Override
	public PageBlogPostCollection findBlogPosts(Pagination pagination, Set<String> tags, String previewToken) {

		var params =  Map.of("limit", pagination.getPageSize(), "skip", pagination.getOffset(), "tags", tags);
		var response = executeQuery(() -> queryLoader.loadQuery("find-blog-posts"), params, previewToken);

		return response.getObject(PageBlogPostCollection.class, "pageBlogPostCollection");
	}

	@Override
	public RecentPageBlogPostCollection findRecentBlogPosts() {

		return findRecentBlogPosts(null);
	}

	@Override
	public RecentPageBlogPostCollection findRecentBlogPosts(String previewToken) {

		var response = executeQuery(() -> queryLoader.loadQuery("find-recent-blog-posts"), previewToken);

		return response.getObject(RecentPageBlogPostCollection.class, "pageBlogPostCollection");
	}

	@Override
	public Optional<ComponentRichImage> findImage(String id) {

		return findImage(id, null);
	}

	@Override
	public Optional<ComponentRichImage> findImage(String id, String previewToken) {

		var response = executeQuery(() -> queryLoader.loadQuery("find-image"), Map.of("id", id), previewToken);

		return Optional.ofNullable(response.getObject(ComponentRichImage.class, "componentRichImage"));
	}

}
