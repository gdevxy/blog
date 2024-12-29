package com.gdevxy.blog.dao.blogpost;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.dao.DaoSupport;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostRatingDao extends DaoSupport {

	private final PgPool sql;

	public Uni<Long> findRating(Integer id) {

		return as(sql.preparedQuery("""
				select 
					count(*) as rating
				from blog_post_rating
				where blog_post_id = $1
				""")
			.execute(Tuple.of(id)), r -> r.getLong("rating"));
	}

	public Uni<String> thumbsUp(Integer blogId) {

		var uuid = UUID.randomUUID().toString();

		return sql.preparedQuery("""
			insert into blog_post_rating (blog_post_id, uuid) values ($1, $2)
			""").execute(Tuple.of(blogId, uuid)).map(i -> uuid);
	}

	public Uni<Void> thumbsDown(String uuid) {

		return sql.preparedQuery("""
			delete from blog_post_rating where uuid = $1
			""").execute(Tuple.of(uuid)).replaceWithVoid();
	}

}
