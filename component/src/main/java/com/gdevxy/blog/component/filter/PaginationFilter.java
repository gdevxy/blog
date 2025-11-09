package com.gdevxy.blog.component.filter;

import com.gdevxy.blog.service.common.PaginationContext;
import io.quarkus.vertx.web.RouteFilter;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("unused")
public class PaginationFilter {

	private static final Long MAX_SIZE = 1000L;

	@RouteFilter(101)
	public void extractPaginationHeaders(RoutingContext ctx) {

		var pageOffset = parseHeader(ctx, "X-Page-Offset", PaginationContext.DEFAULT_PAGE_OFFSET);
		var pageSize = parseHeader(ctx, "X-Page-Size", PaginationContext.DEFAULT_PAGE_SIZE);

		// clamp page size to max allowed
		if (pageSize > MAX_SIZE) {
			pageSize = MAX_SIZE;
		}

		// validate offset
		if (pageOffset < 0) {
			pageOffset = PaginationContext.DEFAULT_PAGE_OFFSET;
		}

		var pagination = PaginationContext.builder().offset(pageOffset).size(pageSize).build();
		PaginationContext.set(pagination);

		try {
			ctx.next();
		} finally {
			PaginationContext.clear();
		}
	}

	private Long parseHeader(RoutingContext ctx, String headerName, Long defaultValue) {
		var headerValue = ctx.request().getHeader(headerName);
		if (headerValue == null || headerValue.isBlank()) {
			return defaultValue;
		}
		try {
			return Long.parseLong(headerValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
