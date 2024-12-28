package com.gdevxy.blog.dao.blogpostcomment;

import jakarta.enterprise.context.ApplicationScoped;

import io.vertx.mutiny.pgclient.PgPool;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostCommentDao {

	private final PgPool sql;

}
