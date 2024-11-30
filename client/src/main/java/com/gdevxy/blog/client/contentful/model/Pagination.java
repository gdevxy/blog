package com.gdevxy.blog.client.contentful.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Pagination {

	@Builder.Default
	private final Long pageSize = 100L;
	@Builder.Default
	private final Long offset = 0L;

}
