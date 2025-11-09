package com.gdevxy.blog.service.contentful.blogpost.converter;

import com.gdevxy.blog.client.contentful.model.Page;
import com.gdevxy.blog.service.common.PaginationContext;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ContentfulPages {

	public Page of() {

		var ctx = PaginationContext.getInstance();

		return Page.builder()
				.offset(ctx.getOffset())
				.pageSize(ctx.getSize())
				.build();
	}

}
