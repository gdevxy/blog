package com.gdevxy.blog.client.contentful;

import com.gdevxy.blog.client.contentful.model.ComponentRichImage;
import com.gdevxy.blog.client.contentful.model.FeaturedImage;
import com.gdevxy.blog.client.contentful.model.Pagination;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkiverse.wiremock.devservice.ConnectWireMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

@QuarkusTest
@ConnectWireMock
class ContentfulClientTest {

	private WireMock wiremock;

	@Inject
	private ContentfulClient client;

	@Test
	void findBlogPost() {
		// given
		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PUBLISHED"))
				.withRequestBody(containing("query findBlogPost($limit: Int = 1, $slug: String!, $locale: String = \\\"en\\\", $preview: Boolean = false)"))
				.willReturn(ok().withBodyFile("find-blog-posts.json")));

		// when
		var actual = client.findBlogPost("slug");

		// then
		assertThat(actual).isPresent();
	}

	@Test
	void findBlogPost_NotFound() {
		// given
		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PUBLISHED"))
				.withRequestBody(containing("query findBlogPost($limit: Int = 1, $slug: String!, $locale: String = \\\"en\\\", $preview: Boolean = false)"))
				.willReturn(ok().withBodyFile("find-blog-posts-not-found.json")));

		// when
		var actual = client.findBlogPost("slug");

		// then
		assertThat(actual).isEmpty();
	}

	@Test
	void findBlogPosts() {
		// given
		var tags = Set.of("tag");
		var pagination = Pagination.builder().build();

		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PUBLISHED"))
				.withRequestBody(containing("query findBlogPosts($limit: Int = 100, $skip: Int = 0, $locale: String = \\\"en\\\", $tags: [String] = [], $preview: Boolean = false)")
						.and(containing("\"tags\":[\"tag\"]")))
				.willReturn(ok().withBodyFile("find-blog-posts.json")));

		// when
		var actual = client.findBlogPosts(pagination, tags);

		// then
		assertThat(actual.getItems()).isNotEmpty();
	}

	@Test
	void findRecentBlogPosts() {
		// given
		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PUBLISHED"))
			.withRequestBody(containing("query findRecentBlogPosts($limit: Int = 5, $skip: Int = 0, $locale: String = \\\"en\\\", $preview: Boolean = false)"))
			.willReturn(ok().withBodyFile("find-recent-blog-posts.json")));

		// when
		var actual = client.findRecentBlogPosts();

		// then
		assertThat(actual.getItems()).isNotEmpty();
	}

	@Test
	void findImage() {
		// given
		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PUBLISHED"))
				.withRequestBody(containing("query findImage($id: String!)"))
				.willReturn(ok().withBodyFile("find-image.json")));

		// when
		var actual = client.findImage("id");

		// then
		assertThat(actual).isPresent()
			.get()
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
				.withRequestBody(containing("query findImage($id: String!)"))
				.willReturn(ok().withBodyFile("find-image-not-found.json")));

		// when
		var actual = client.findImage("id");

		// then
		assertThat(actual).isEmpty();
	}

}
