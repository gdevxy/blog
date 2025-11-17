package com.gdevxy.blog.component.filter;

import com.gdevxy.blog.service.cookie.SessionService;
import io.quarkus.vertx.web.RouteFilter;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SessionRouteFilter {

	private final SessionService sessionCookieService;

	@RouteFilter(100)
	public void sessionRouteFilter(RoutingContext ctx) {

		var cookie = ctx.request().getCookie(SessionService.SESSION_COOKIE_NAME);
		if (cookie == null || !sessionCookieService.isValid(cookie)) {
			cookie = sessionCookieService.create();
			ctx.response().addCookie(cookie);
		}

		ctx.vertx()
			.getOrCreateContext()
			.putLocal(SessionService.SESSION_COOKIE_NAME, sessionCookieService.extractUserId(cookie));

		ctx.next();
	}

}
