package com.gdevxy.blog.client.contentful;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nullable;
import jakarta.ws.rs.core.HttpHeaders;

import io.smallrye.graphql.client.Response;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClientBuilder;
import io.smallrye.mutiny.Uni;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
abstract class ContentfulClientSupport {

	private DynamicGraphQLClient client;
	private DynamicGraphQLClientBuilder previewClient;

	public ContentfulClientSupport(DynamicGraphQLClient client) {
		this.client = client;
		this.previewClient = DynamicGraphQLClientBuilder.newBuilder().configKey("contentful");
	}

	Uni<Response> executeQueryAsync(String query, @Nullable String previewToken) {

		return executeQueryAsync(query, Map.of(), previewToken);
	}

	@SneakyThrows
	Uni<Response> executeQueryAsync(String query, Map<String, Object> params, @Nullable String previewToken) {

		if (previewToken == null) {
			return client.executeAsync(query, params)
				.map(this::throwErrorOnGraphqlException);
		}

		try (var client = previewClient.header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(previewToken)).build()) {
			return client.executeAsync(query, extendWithPreviewMode(params))
				.map(this::throwErrorOnGraphqlException);
		}
	}

	private Response throwErrorOnGraphqlException(Response response) {

		if (response.hasError()) {
			log.warn("Contentful query failed: [{}]", response.getErrors());
			throw new ContentfulQueryException();
		}
		return response;
	}

	private Map<String, Object> extendWithPreviewMode(Map<String, Object> params) {

		var p = new HashMap<>(params);
		p.put("preview", true);
		return p;
	}

}
