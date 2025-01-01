package com.gdevxy.blog.client.contentful;

import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.client.GraphQlQueryLoader;
import com.gdevxy.blog.client.contentful.model.ComponentRichImage;
import com.gdevxy.blog.client.contentful.model.PageBlogPost;
import com.gdevxy.blog.client.contentful.model.PageBlogPostCollection;
import com.gdevxy.blog.client.contentful.model.Page;
import com.gdevxy.blog.client.contentful.model.LightPageBlogPostCollection;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
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
	public Uni<PageBlogPost> findBlogPost(String slug) {

		return findBlogPost(slug, null);
	}

	@Override
	public Uni<PageBlogPost> findBlogPost(String slug, String previewToken) {

		return queryLoader.loadQuery("find-blog-post")
			.flatMap(query -> executeQueryAsync(query, Map.of("slug", slug), previewToken))
			.onItem().transformToMulti(res -> Multi.createFrom().iterable(res.getObject(PageBlogPostCollection.class, "pageBlogPostCollection").getItems()))
			.toUni();
	}

	@Override
	public Uni<PageBlogPostCollection> findBlogPosts(Page pagination, Set<String> tags) {

		return findBlogPosts(pagination, tags, null);
	}

	@Override
	public Uni<PageBlogPostCollection> findBlogPosts(Page pagination, Set<String> tags, String previewToken) {

		var params =  Map.of("limit", pagination.getPageSize(), "skip", pagination.getOffset(), "tags", tags);

		return queryLoader.loadQuery("find-blog-posts")
			.flatMap(query -> executeQueryAsync(query, params, previewToken))
			.map(res -> res.getObject(PageBlogPostCollection.class, "pageBlogPostCollection"));
	}

	@Override
	public Uni<LightPageBlogPostCollection> findLightBlogPosts(Page pagination) {

		return findLightBlogPosts(pagination, null);
	}

	@Override
	public Uni<LightPageBlogPostCollection> findLightBlogPosts(Page pagination, String previewToken) {

		var params = Map.<String, Object>of("limit", pagination.getPageSize(), "skip", pagination.getOffset());

		return queryLoader.loadQuery("find-light-blog-posts")
			.flatMap(query -> executeQueryAsync(query, params, previewToken))
			.map(res -> res.getObject(LightPageBlogPostCollection.class, "pageBlogPostCollection"));
	}

	@Override
	public Uni<ComponentRichImage> findImage(String id) {

		return findImage(id, null);
	}

	@Override
	public Uni<ComponentRichImage> findImage(String id, String previewToken) {

		return queryLoader.loadQuery("find-image")
			.flatMap(query -> executeQueryAsync(query, Map.of("id", id), previewToken))
			.map(res -> res.getObject(ComponentRichImage.class, "componentRichImage"));
	}

}
