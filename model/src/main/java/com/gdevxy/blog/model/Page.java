package com.gdevxy.blog.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class Page<T> {

	@Builder.Default
	private final List<T> elements = List.of();
	private final Long offset;
	private final Long pageSize;
	private final Long totalCount;

	public boolean hasNextPage() {
		return totalCount < offset + pageSize;
	}

}
