package com.gdevxy.blog.client.contentful;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.util.List;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import com.gdevxy.blog.client.contentful.model.PageBlogModel;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkiverse.wiremock.devservice.ConnectWireMock;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

@QuarkusTest
@ConnectWireMock
class ContentfulCMAClientTest {

	private WireMock wiremock;

	@RestClient
	ContentfulCMAClient client;

	@Test
	void findPageBlogModel() {
		// given
		wiremock.register(get(urlPathEqualTo("/content_types/pageBlogPost")).withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer CMA_TOKEN"))
			.willReturn(ok().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withBodyFile("find-page-blog-model.json")));

		// when
		var actual = client.findPageBlogModel().await().atMost(Duration.ofSeconds(1));

		// then
		assertThat(actual.getFields()).usingRecursiveFieldByFieldElementComparator()
			.contains(PageBlogModel.Field.builder()
				.id("tags")
				.items(PageBlogModel.Field.Items.builder()
					.validations(List.of(PageBlogModel.Field.Items.Validation.builder().in(List.of("About Me", "Agile")).build()))
					.build())
				.build());
	}

}
