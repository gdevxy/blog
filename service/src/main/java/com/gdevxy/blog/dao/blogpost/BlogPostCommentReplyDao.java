package com.gdevxy.blog.dao.blogpost;

import java.time.ZoneOffset;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.dao.DaoSupport;
import com.gdevxy.blog.dao.blogpost.model.BlogPostCommentReplyEntity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostCommentReplyDao extends DaoSupport {

	private final PgPool sql;

	public Multi<BlogPostCommentReplyEntity> find(Integer blogPostCommentId) {

		return sql.preparedQuery("""
				select
					id,
					blog_post_comment_id,
					user_id,
					author,
					comment,
					created_at
				from blog_post_comment_reply
				where blog_post_comment_id = $1
				order by created_at desc
				""")
			.execute(Tuple.of(blogPostCommentId))
			.onItem().transformToMulti(RowSet::toMulti)
			.map(this::toBlogPostCommentReplyEntity);
	}

	public Uni<BlogPostCommentReplyEntity> save(BlogPostCommentReplyEntity e) {

		return as(sql.preparedQuery("""
				insert into blog_post_comment_reply (
					blog_post_comment_id,
					user_id,
					author,
					comment,
					created_at
				) values ($1, $2, $3, $4)
				""")
			.execute(Tuple.of(e.getBlogPostCommentId(), e.getUserId(), e.getAuthor(), e.getComment(), toOffsetDateTimeUTC(e.getCreatedAt()))), r -> e.toBuilder().blogPostCommentId(r.getInteger("id")).build());
	}

	private BlogPostCommentReplyEntity toBlogPostCommentReplyEntity(Row row) {

		return BlogPostCommentReplyEntity.builder()
			.id(row.getInteger("id"))
			.blogPostCommentId(row.getInteger("blog_post_comment_id"))
			.userId(row.getString("user_id"))
			.author(asString(row, "author").orElse("Anonymous"))
			.comment(row.getString("comment"))
			.createdAt(asRequiredInstant(row,"created_at"))
			.build();
	}

}
