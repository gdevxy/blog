package com.gdevxy.blog.client.contentful;

import com.gdevxy.blog.client.GraphQlQueryLoader;
import com.gdevxy.blog.client.contentful.model.ComponentRichImage;
import com.gdevxy.blog.client.contentful.model.FeaturedImage;
import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.PageBlogPostCollection;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.Optional;

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

		return asClass(response, PageBlogPostCollection.class).getItems().stream().findAny();
	}

	@SneakyThrows
	public PageBlogPostCollection findBlogPosts() {

		var params = Map.<String, Object>of("preview", true);
		var response = executeQuery(() -> queryLoader.loadQuery("find-blog-posts"), params);

		return asClass(response, PageBlogPostCollection.class);
	}

	@Override
	public Optional<ComponentRichImage> findImage(String id) {

		var params = Map.<String, Object>of("preview", true, "id", id);
		var response = executeQuery(() -> queryLoader.loadQuery("find-image"), params);

		return Optional.ofNullable(asClass(response, ComponentRichImage.class));
	}

}
