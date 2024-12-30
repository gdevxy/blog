package com.gdevxy.blog.dao.blogpost;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import com.gdevxy.blog.dao.DaoSupport;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostRatingDao extends DaoSupport {

	private final PgPool sql;

	public Uni<Boolean> liked(String key, String userId) {

		return sql.preparedQuery("""
				select
					1
				from blog_post_rating bpr
				inner join blog_post bp on bp.id = bpr.blog_post_id
				where bp.key = $1 and bpr.user_id = $2
				""")
			.execute(Tuple.of(key, userId))
			.onItem().transform(t -> t.rowCount() > 0);
	}

	public Uni<Long> countRating(Integer id) {

		return as(sql.preparedQuery("""
				select 
					count(*) as rating
				from blog_post_rating
				where blog_post_id = $1
				""")
			.execute(Tuple.of(id)), r -> r.getLong("rating"));
	}

	public Uni<Void> thumbsUp(Integer blogId, String userId) {

		return sql.preparedQuery("""
			insert into blog_post_rating (blog_post_id, user_id) values ($1, $2)
			""").execute(Tuple.of(blogId, userId)).replaceWithVoid();
	}

	public Uni<Void> thumbsDown(Integer blogId, String userId) {

		return sql.preparedQuery("""
			delete from blog_post_rating where blog_post_id = $1 and user_id = $2
			""").execute(Tuple.of(blogId, userId)).replaceWithVoid();
	}

}
