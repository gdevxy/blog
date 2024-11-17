package com.gdevxy.blog.client;

import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class GraphQlQueryLoader {

	@SneakyThrows
	@CacheResult(cacheName = "graphql-query")
	public String loadQuery(String queryName) {

		var uri = Thread.currentThread().getContextClassLoader().getResource("graphql/%s.graphql".formatted(queryName)).toURI();
		return Files.readString(Path.of(uri), StandardCharsets.UTF_8);
	}

}
