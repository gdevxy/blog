package com.gdevxy.blog.client;

import java.nio.charset.StandardCharsets;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import lombok.SneakyThrows;

@ApplicationScoped
public class GraphQlQueryLoader {

	@Inject
	Vertx vertx;

	@SneakyThrows
	@CacheResult(cacheName = "graphql-query")
	public Uni<String> loadQuery(String queryName) {

		return Uni.createFrom().future(vertx.fileSystem().readFile("graphql/%s.graphql".formatted(queryName)).toCompletionStage().toCompletableFuture())
			.map(b -> b.toString(StandardCharsets.UTF_8));
	}

}
