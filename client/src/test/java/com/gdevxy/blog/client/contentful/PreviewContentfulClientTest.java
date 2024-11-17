package com.gdevxy.blog.client.contentful;

import com.gdevxy.blog.client.contentful.model.FeaturedImage;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkiverse.wiremock.devservice.ConnectWireMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@ConnectWireMock
class PreviewContentfulClientTest {

	private WireMock wiremock;

	@Inject
	@PreviewContentful
	private ContentfulClient client;

	@Test
	void findBlogPosts() {
		// given
		wiremock.register(post(urlPathEqualTo("/")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer PREVIEW"))
				.withRequestBody(containing("query findBlogPosts($limit: Int = 100, $skip: Int = 0, $locale: String = \\\"en\\\", $preview: Boolean = false)"))
				.willReturn(ok().withBodyFile("find-blog-posts.json")));

		// when
		var actual = client.findBlogPosts();

		// then
		assertThat(actual.getItems()).isNotEmpty();
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
		assertThat(actual).isPresent().get().usingRecursiveComparison().isEqualTo(FeaturedImage.builder()
				.title("Instant Access to Information (Augment Reality)")
				.width(868)
				.height(590)
				.url("https://images.ctfassets.net/r9z7bjp5iedy/7CxNr9Arb299R7KjO103Bt/1d6bd9a06cb4d287ac30b5a626f8ad25/tobias-CyX3ZAti5DA-unsplash__1___1_.jpg")
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
