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
import lombok.SneakyThrows;

@PreviewContentful
@ApplicationScoped
public class PreviewContentfulClient extends ContentfulClientSupport implements ContentfulClient {

	private final GraphQlQueryLoader queryLoader;

	public PreviewContentfulClient(@GraphQLClient("contentful-preview") DynamicGraphQLClient client, GraphQlQueryLoader queryLoader) {
		super(client);
		this.queryLoader = queryLoader;
	}

	@Override
	public Optional<PageBlogPost> findBlogPost(String slug) {

		var params = Map.<String, Object>of("preview", true, "slug", slug);
		var response = executeQuery(() -> queryLoader.loadQuery("find-blog-post"), params);

		return response.getObject(PageBlogPostCollection.class, "pageBlogPostCollection").getItems().stream().findAny();
	}

	@SneakyThrows
	public PageBlogPostCollection findBlogPosts(Pagination pagination, Set<String> tags) {

		throw new UnsupportedOperationException();
	}

	@Override
	public RecentPageBlogPostCollection findRecentBlogPosts() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<ComponentRichImage> findImage(String id) {

		var params = Map.<String, Object>of("preview", true, "id", id);
		var response = executeQuery(() -> queryLoader.loadQuery("find-image"), params);

		return Optional.ofNullable(response.getObject(ComponentRichImage.class, "componentRichImage"));
	}

}
