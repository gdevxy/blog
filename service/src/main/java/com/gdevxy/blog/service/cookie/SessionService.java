package com.gdevxy.blog.service.cookie;

import com.gdevxy.blog.service.hmac.HmacService;
import io.vertx.core.Vertx;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.CookieSameSite;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class SessionService {

	public static final String SESSION_COOKIE_NAME = "gdevxy-session";

	private final HmacService hmacService;
	private final Instance<RoutingContext> rci;

	public Cookie create() {

		var userId = UUID.randomUUID().toString();
		var sessionId = "%s:%s".formatted(userId, hmacService.generate(userId));

		var cookie = Cookie.cookie(SESSION_COOKIE_NAME, sessionId);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setSameSite(CookieSameSite.STRICT);
		cookie.setMaxAge(Integer.MAX_VALUE);
		cookie.setPath("/");
		return cookie;
	}

	public Optional<String> requestUserId() {

		return Optional.ofNullable(rci.get())
				.map(RoutingContext::vertx)
				.map(Vertx::getOrCreateContext)
				.map(c -> c.getLocal(SessionService.SESSION_COOKIE_NAME));
	}

	public String extractUserId(Cookie cookie) {

		var content = cookie.getValue().split(":");
		if (content.length != 2) {
			throw new RuntimeException("Invalid cookie value: " + cookie.getValue());
		}

		return content[0];
	}

	public Boolean isValid(Cookie cookie) {

		var content = cookie.getValue().split(":");
		if (content.length != 2) {
			return false;
		}

		return hmacService.generate(content[0]).equals(content[1]);
	}

}
