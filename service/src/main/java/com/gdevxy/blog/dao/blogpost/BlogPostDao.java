package com.gdevxy.blog.dao.blogpost;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.dao.DaoSupport;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostDao extends DaoSupport {

	private final PgPool sql;

	public Uni<BlogPostEntity> findByKey(String key) {

		return as(sql.preparedQuery("""
				select 
					id,
					key,
					rating
				from blog_post where key = $1
				""")
			.execute(Tuple.of(key)), BlogPostDao::toBlogPostEntity);
	}

	public Uni<Void> rate(String key) {

		return sql.preparedQuery("""
			update blog_post set rating = coalesce(rating, 0) + 1 where key = $1
			""").execute(Tuple.of(key)).replaceWithVoid();
	}

	private static BlogPostEntity toBlogPostEntity(Row row) {

		return BlogPostEntity.builder()
			.id(row.getInteger("id"))
			.key(row.getString("key"))
			.rating(row.getInteger("rating"))
			.build();
	}

}
