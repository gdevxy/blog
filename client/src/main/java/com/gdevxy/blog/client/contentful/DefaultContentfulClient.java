package com.gdevxy.blog.client.contentful;

import java.util.Map;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.client.GraphQlQueryLoader;
import com.gdevxy.blog.client.contentful.model.ComponentRichImage;
import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.PageBlogPostCollection;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DefaultContentful
@ApplicationScoped
public class DefaultContentfulClient extends ContentfulClientSupport implements ContentfulClient {

	private final GraphQlQueryLoader queryLoader;

	public DefaultContentfulClient(@GraphQLClient("contentful") DynamicGraphQLClient client, GraphQlQueryLoader queryLoader) {
		super(client);
		this.queryLoader = queryLoader;
	}

	@Override
	public Optional<PageBlogPost> findBlogPost(String slug) {

		var response = executeQuery(() -> queryLoader.loadQuery("find-blog-post"), Map.of("slug", slug));

		return asClass(response, PageBlogPostCollection.class).getItems().stream().findAny();
	}

	@Override
	public PageBlogPostCollection findBlogPosts() {

		var response = executeQuery(() -> queryLoader.loadQuery("find-blog-posts"));

		return asClass(response, PageBlogPostCollection.class);
	}

	@Override
	public Optional<ComponentRichImage> findImage(String id) {

		var response = executeQuery(() -> queryLoader.loadQuery("find-image"), Map.of("id", id));

		return Optional.ofNullable(asClass(response, ComponentRichImage.class));
	}

}
