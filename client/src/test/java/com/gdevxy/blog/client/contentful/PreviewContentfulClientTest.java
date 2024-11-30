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
import static org.assertj.core.api.Assertions.*;

import java.util.Set;

@QuarkusTest
@ConnectWireMock
class PreviewContentfulClientTest {

	private WireMock wiremock;

	@Inject
	@PreviewContentful
	private ContentfulClient client;

	@Test
	void findBlogPost() {
		// given
		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PREVIEW"))
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
		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PREVIEW"))
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
		var pagination = Pagination.builder().build();

		// when
		var thrown = catchThrowable(() -> client.findBlogPosts(pagination, Set.of()));

		// then
		assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void findRecentBlogPosts() {
		// when
		var thrown = catchThrowable(() -> client.findRecentBlogPosts());

		// then
		assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void findImage() {
		// given
		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PREVIEW"))
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
		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PREVIEW"))
				.withRequestBody(containing("query findImage($id: String!)"))
				.willReturn(ok().withBodyFile("find-image-not-found.json")));

		// when
		var actual = client.findImage("id");

		// then
		assertThat(actual).isEmpty();
	}

}
