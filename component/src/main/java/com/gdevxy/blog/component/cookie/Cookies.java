package com.gdevxy.blog.component.cookie;

import java.util.Optional;
import java.util.UUID;

import io.vertx.core.http.Cookie;
import io.vertx.core.http.CookieSameSite;
import io.vertx.core.http.HttpServerRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Cookies {

	private final String SESSION_COOKIE_NAME = "gdevxy-session";

	public Optional<Cookie> findSessionCookie(HttpServerRequest req) {

		return req.cookies(SESSION_COOKIE_NAME).stream().findAny();
	}

	public Cookie createSessionCookie() {

		var cookie = Cookie.cookie(SESSION_COOKIE_NAME, UUID.randomUUID().toString());
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setSameSite(CookieSameSite.STRICT);
		cookie.setMaxAge(Integer.MAX_VALUE);
		cookie.setPath("/");
		return cookie;
	}

}
