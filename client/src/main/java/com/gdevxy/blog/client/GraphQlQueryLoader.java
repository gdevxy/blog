package com.gdevxy.blog.client;

import java.nio.charset.StandardCharsets;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.cache.CacheResult;
import lombok.SneakyThrows;

@ApplicationScoped
public class GraphQlQueryLoader {

	@SneakyThrows
	@CacheResult(cacheName = "graphql-query")
	public String loadQuery(String queryName) {

		try(var is = this.getClass().getResourceAsStream("/graphql/%s.graphql".formatted(queryName))) {
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

}
