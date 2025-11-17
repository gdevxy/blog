package com.gdevxy.blog.service.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaginationContext {

	public static Long DEFAULT_PAGE_OFFSET = 0L;
	public static Long DEFAULT_PAGE_SIZE = 100L;

	private static final ThreadLocal<PaginationContext> context = new ThreadLocal<>();

	@Builder.Default
	private final Long offset =  DEFAULT_PAGE_OFFSET;
	@Builder.Default
	private final Long size = DEFAULT_PAGE_SIZE;

	public static void set(PaginationContext pagination) {
		context.set(pagination);
	}

	public static void clear() {
		context.remove();
	}

	public static PaginationContext getInstance() {
		var pagination = context.get();
		if (pagination != null) {
			return pagination;
		}
		return PaginationContext.builder().build();
	}

}
