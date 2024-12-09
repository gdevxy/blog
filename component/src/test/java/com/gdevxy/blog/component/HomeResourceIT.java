package com.gdevxy.blog.component;

import static org.assertj.core.api.Assertions.*;

import java.net.URL;

import com.microsoft.playwright.BrowserContext;
import io.quarkiverse.playwright.InjectPlaywright;
import io.quarkiverse.playwright.WithPlaywright;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

@WithPlaywright
@QuarkusIntegrationTest
public class HomeResourceIT {

	@InjectPlaywright
	BrowserContext context;

	@TestHTTPResource("/")
	URL home;

	@Test
	void home() {
		// given
		var page = context.newPage();

		// when
		var actual = page.navigate(home.toString());

		// then
		page.waitForLoadState();

		assertThat(actual.status()).isEqualTo(HttpStatus.SC_OK);
		assertThat(page.title()).isEqualTo("gdevxy: home page");
	}

}
