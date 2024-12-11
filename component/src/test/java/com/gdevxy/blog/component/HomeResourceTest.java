package com.gdevxy.blog.component;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URL;
import java.util.List;
import java.util.Set;

import com.gdevxy.blog.mother.BlogPostMother;
import com.gdevxy.blog.mother.RecentBlogPostMother;
import com.gdevxy.blog.service.contentful.blogpost.IBlogPostService;
import com.microsoft.playwright.BrowserContext;
import io.quarkiverse.playwright.InjectPlaywright;
import io.quarkiverse.playwright.WithPlaywright;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

@QuarkusTest
@WithPlaywright
public class HomeResourceTest {

	@InjectPlaywright
	BrowserContext context;

	@InjectMock
	IBlogPostService blogPostService;

	@TestHTTPResource("/")
	URL home;

	@Test
	void home() {
		// given
		var page = context.newPage();

		when(blogPostService.findRecentBlogPosts()).thenReturn(List.of(RecentBlogPostMother.basic().build()));
		when(blogPostService.findBlogPosts(Set.of())).thenReturn(List.of(BlogPostMother.basic().build()));

		// when
		var actual = page.navigate(home.toString());

		// then
		page.waitForLoadState();

		assertThat(actual.status()).isEqualTo(HttpStatus.SC_OK);
		assertThat(page.title()).isEqualTo("gdevxy: home page");
		assertThat(page.locator(".card-link-hidden").getAttribute("href")).isEqualTo("blog-posts/slug");
	}

}
