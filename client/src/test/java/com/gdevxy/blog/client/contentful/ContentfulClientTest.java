package com.gdevxy.blog.client.contentful;

import com.gdevxy.blog.client.contentful.model.ComponentRichImage;
import com.gdevxy.blog.client.contentful.model.FeaturedImage;
import com.gdevxy.blog.client.contentful.model.Page;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkiverse.wiremock.devservice.ConnectWireMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Set;

@QuarkusTest
@ConnectWireMock
class ContentfulClientTest {

	private WireMock wiremock;

	@Inject
	ContentfulClient client;

	@Test
	void findBlogPost() {
		// given
		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PUBLISHED"))
				.withRequestBody(containing("query findBlogPost($limit: Int = 1, $slug: String!, $locale: String = \\\"en\\\", $preview: Boolean = false)"))
				.willReturn(ok().withBodyFile("find-blog-posts.json")));

		// when
		var actual = client.findBlogPost("slug").await().atMost(Duration.ofSeconds(1));

		// then
		assertThat(actual).isNotNull();
	}

	@Test
	void findBlogPost_NotFound() {
		// given
		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PUBLISHED"))
				.withRequestBody(containing("query findBlogPost($limit: Int = 1, $slug: String!, $locale: String = \\\"en\\\", $preview: Boolean = false)"))
				.willReturn(ok().withBodyFile("find-blog-posts-not-found.json")));

		// when
		var actual = client.findBlogPost("slug").await().atMost(Duration.ofSeconds(1));

		// then
		assertThat(actual).isNull();
	}

	@Test
	void findBlogPosts() {
		// given
		var tags = Set.of("tag");
		var pagination = Page.builder().build();

		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PUBLISHED"))
				.withRequestBody(containing("query findBlogPosts($limit: Int = 100, $skip: Int = 0, $locale: String = \\\"en\\\", $tags: [String] = [], $preview: Boolean = false)")
						.and(containing("\"tags\":[\"tag\"]")))
				.willReturn(ok().withBodyFile("find-blog-posts.json")));

		// when
		var actual = client.findBlogPosts(pagination, tags).await().atMost(Duration.ofSeconds(1));

		// then
		assertThat(actual.getItems()).isNotEmpty();
	}

	@Test
	void findLightBlogPosts() {
		// given
		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PUBLISHED"))
			.withRequestBody(containing("query findLightBlogPosts($limit: Int = 100, $skip: Int = 0, $locale: String = \\\"en\\\", $preview: Boolean = false)"))
			.willReturn(ok().withBodyFile("find-recent-blog-posts.json")));

		// when
		var actual = client.findLightBlogPosts(Page.builder().pageSize(5L).build()).await().atMost(Duration.ofSeconds(1));

		// then
		assertThat(actual.getItems()).isNotEmpty();
	}

	@Test
	void findImage() {
		// given
		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PUBLISHED"))
				.withRequestBody(containing("query findImage($id: String!, $preview: Boolean = false)"))
				.willReturn(ok().withBodyFile("find-image.json")));

		// when
		var actual = client.findImage("id").await().atMost(Duration.ofSeconds(1));

		// then
		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(ComponentRichImage.builder()
				.fullWidth(true)
				.image(FeaturedImage.builder()
					.title("Instant Access to Information (Augment Reality)")
					.width(868)
					.height(590)
					.url(
						"https://images.ctfassets.net/r9z7bjp5iedy/7CxNr9Arb299R7KjO103Bt/1d6bd9a06cb4d287ac30b5a626f8ad25/tobias-CyX3ZAti5DA-unsplash__1___1_.jpg")
					.build())
				.build());
	}

	@Test
	void findImage_NotFound() {
		// given
		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PUBLISHED"))
				.withRequestBody(containing("query findImage($id: String!, $preview: Boolean = false)"))
				.willReturn(ok().withBodyFile("find-image-not-found.json")));

		// when
		var actual = client.findImage("id").await().atMost(Duration.ofSeconds(1));

		// then
		assertThat(actual).isNull();
	}

}
