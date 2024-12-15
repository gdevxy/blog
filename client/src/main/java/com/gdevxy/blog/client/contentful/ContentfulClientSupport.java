package com.gdevxy.blog.client.contentful;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import jakarta.annotation.Nullable;
import jakarta.ws.rs.core.HttpHeaders;

import io.smallrye.graphql.client.Response;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClientBuilder;
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

	@SneakyThrows
	Response executeQuery(Supplier<String> querySupplier, @Nullable String previewToken) {

		return executeQuery(querySupplier, Map.of(), previewToken);
	}

	@SneakyThrows
	Response executeQuery(Supplier<String> querySupplier, Map<String, Object> params, @Nullable String previewToken) {

		if (previewToken == null) {
			return executeQuery(client, querySupplier, params);
		}

		try (var client = previewClient.header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(previewToken)).build()) {
			return executeQuery(client, querySupplier, extendWithPreviewMode(params));
		}
	}

	private Response executeQuery(DynamicGraphQLClient client, Supplier<String> querySupplier, Map<String, Object> params) throws ExecutionException, InterruptedException {

		var response = client.executeSync(querySupplier.get(), params);

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
