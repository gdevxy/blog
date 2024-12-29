package com.gdevxy.blog.component.cookie;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import jakarta.ws.rs.core.NewCookie;

import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Cookies {

	private final String BLOG_POST_RATING_COOKIE_NAME = "gdevxy-bpr-%s";

	public Optional<Cookie> findBlogPostRatingCookie(HttpServerRequest req, String id) {

		return req.cookies(BLOG_POST_RATING_COOKIE_NAME.formatted(id)).stream().findAny();
	}

	public NewCookie createBlogPostRatingCookie(String id, String uuid) {

		return new NewCookie.Builder(BLOG_POST_RATING_COOKIE_NAME.formatted(id))
			.secure(true)
			.httpOnly(true)
			.sameSite(NewCookie.SameSite.STRICT)
			.maxAge(Integer.MAX_VALUE)
			.expiry(Date.from(Instant.now().plusSeconds(Integer.MAX_VALUE)))
			.value(uuid)
			.path("/")
			.build();
	}

	public NewCookie deleteBlogPostRatingCookie(String id) {

		return new NewCookie.Builder(BLOG_POST_RATING_COOKIE_NAME.formatted(id))
			.secure(true)
			.httpOnly(true)
			.sameSite(NewCookie.SameSite.STRICT)
			.maxAge(Integer.MAX_VALUE)
			.expiry(Date.from(Instant.now().plusSeconds(Integer.MAX_VALUE)))
			.value("goodbye")
			.path("/")
			.build();
	}

}
