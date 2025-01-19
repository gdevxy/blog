package com.gdevxy.blog.dao.blogpost;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.dao.DaoSupport;
import com.gdevxy.blog.dao.blogpost.model.BlogPostEntity;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostDao extends DaoSupport {

	private final PgPool sql;

	public Uni<BlogPostEntity> findByKey(String key) {

		return asUni(sql.preparedQuery("""
				select 
					id,
					key
				from blog_post
				where key = $1
				""")
			.execute(Tuple.of(key)), BlogPostDao::toBlogPostEntity);
	}

	public Uni<BlogPostEntity> save(BlogPostEntity entity) {

		return asUni(sql.preparedQuery("""
				insert into blog_post(
					key
				) values (
					$1
				) returning *
				""")
			.execute(Tuple.of(entity.getKey())), row -> entity.toBuilder().id(row.getInteger("id")).build());
	}

	private static BlogPostEntity toBlogPostEntity(Row row) {

		return BlogPostEntity.builder()
			.id(row.getInteger("id"))
			.key(row.getString("key"))
			.build();
	}

}
