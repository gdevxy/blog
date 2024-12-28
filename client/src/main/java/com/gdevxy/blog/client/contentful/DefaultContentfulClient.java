package com.gdevxy.blog.client.contentful;

import java.util.Map;
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
	public Uni<PageBlogPostCollection> findBlogPosts(Pagination pagination, Set<String> tags) {

		return findBlogPosts(pagination, tags, null);
	}

	@Override
	public Uni<PageBlogPostCollection> findBlogPosts(Pagination pagination, Set<String> tags, String previewToken) {

		var params =  Map.of("limit", pagination.getPageSize(), "skip", pagination.getOffset(), "tags", tags);

		return queryLoader.loadQuery("find-blog-posts")
			.flatMap(query -> executeQueryAsync(query, params, previewToken))
			.map(res -> res.getObject(PageBlogPostCollection.class, "pageBlogPostCollection"));
	}

	@Override
	public Uni<RecentPageBlogPostCollection> findRecentBlogPosts() {

		return findRecentBlogPosts(null);
	}

	@Override
	public Uni<RecentPageBlogPostCollection> findRecentBlogPosts(String previewToken) {

		return queryLoader.loadQuery("find-recent-blog-posts")
			.flatMap(query -> executeQueryAsync(query, previewToken))
			.map(res -> res.getObject(RecentPageBlogPostCollection.class, "pageBlogPostCollection"));
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
