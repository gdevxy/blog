package com.gdevxy.blog.client.contentful;

import io.smallrye.graphql.client.Response;
import io.smallrye.graphql.client.core.Document;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
abstract class ContentfulClientSupport {

	private DynamicGraphQLClient client;

	@SneakyThrows
	Response executeQuery(Supplier<String> querySupplier) {

		return executeQuery(querySupplier, Map.of());
	}

	@SneakyThrows
	Response executeQuery(Supplier<String> querySupplier, Map<String, Object> params) {

		var response = client.executeSync(querySupplier.get(), params);

		if (response.hasError()) {
			log.warn("Contentful query failed: [{}]", response.getErrors());
			throw new ContentfulQueryException();
		}

		return response;
	}

}
