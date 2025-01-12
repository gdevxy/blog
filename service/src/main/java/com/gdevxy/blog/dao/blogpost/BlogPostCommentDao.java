package com.gdevxy.blog.dao.blogpost;

import java.time.ZoneOffset;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.dao.DaoSupport;
import com.gdevxy.blog.dao.blogpost.model.BlogPostCommentEntity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostCommentDao extends DaoSupport {

	private final PgPool sql;

	public Uni<Long> count(Integer blogPostId) {

		return as(sql.preparedQuery("""
				select
					count(*) as nb_comments
				from blog_post_comment
				where blog_post_id = $1
				""")
			.execute(Tuple.of(blogPostId)), r -> r.getLong("nb_comments"));
	}

	public Multi<BlogPostCommentEntity> find(String blogPostKey) {

		return sql.preparedQuery("""
				select
					pc.id,
					pc.blog_post_id,
					pc.user_id,
					pc.author,
					pc.comment,
					pc.created_at
				from blog_post_comment pc
				inner join blog_post p on p.id = pc.blog_post_id
				where p.key = $1
				order by pc.created_at desc
				""")
			.execute(Tuple.of(blogPostKey))
			.onItem().transformToMulti(RowSet::toMulti)
			.map(this::toBlogPostCommentEntity);
	}

	public Uni<BlogPostCommentEntity> save(BlogPostCommentEntity e) {

		return as(sql.preparedQuery("""
				insert into blog_post_comment (
					blog_post_id,
					user_id,
					author,
					comment,
					created_at
				) values ($1, $2, $3, $4, $5) returning *
				""")
			.execute(Tuple.of(e.getBlogPostId(), e.getUserId(), e.getAuthor(), e.getComment(), toOffsetDateTimeUTC(e.getCreatedAt()))), r -> e.toBuilder().id(r.getInteger("id")).build());
	}

	private BlogPostCommentEntity toBlogPostCommentEntity(Row row) {

		return BlogPostCommentEntity.builder()
			.id(row.getInteger("id"))
			.blogPostId(row.getInteger("blog_post_id"))
			.userId(row.getString("user_id"))
			.author(asString(row, "author").orElse("Anonymous"))
			.comment(row.getString("comment"))
			.createdAt(asRequiredInstant(row,"created_at"))
			.build();
	}

}
