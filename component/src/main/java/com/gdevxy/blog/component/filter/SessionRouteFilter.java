package com.gdevxy.blog.component.filter;

import com.gdevxy.blog.component.cookie.Cookies;
import io.quarkus.vertx.web.RouteFilter;
import io.vertx.ext.web.RoutingContext;

@SuppressWarnings("unused")
public class SessionRouteFilter {

	@RouteFilter(100)
	public void sessionRouteFilter(RoutingContext  ctx) {

		var session = Cookies.findSessionCookie(ctx.request());
		if (session.isEmpty()) {
			ctx.response().addCookie(Cookies.createSessionCookie());
		}

		ctx.next();
	}

}
