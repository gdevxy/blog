package com.gdevxy.blog.dao.blogpost;

import com.gdevxy.blog.dao.DaoSupport;
import com.gdevxy.blog.dao.blogpost.model.BlogPostCommentEntity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BlogPostCommentDao extends DaoSupport {

	private final Pool sql;

	public Uni<Long> count(Integer blogPostId) {

		return asUni(sql.preparedQuery("""
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

	public Uni<BlogPostCommentEntity> find(String blogPostKey, Integer id) {

		return asUni(sql.preparedQuery("""
				select
					pc.id,
					pc.blog_post_id,
					pc.user_id,
					pc.author,
					pc.comment,
					pc.created_at
				from blog_post_comment pc
				inner join blog_post p on p.id = pc.blog_post_id
				where p.key = $1 and pc.id = $2
				order by pc.created_at desc
				""")
			.execute(Tuple.of(blogPostKey, id)), this::toBlogPostCommentEntity);
	}

	public Uni<BlogPostCommentEntity> save(BlogPostCommentEntity e) {

		return asUni(sql.preparedQuery("""
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

	public Uni<Boolean> update(Integer id, String userId, String author, String comment) {

		return sql.preparedQuery("""
				update blog_post_comment set
					author = $1,
					comment = $2
				where
					id = $3
					and user_id = $4
				""")
			.execute(Tuple.of(author, comment, id, userId))
			.onItem().transform(rowSet -> rowSet.rowCount() > 0);
	}

	public Uni<Boolean> delete(Integer id, String userId) {

		return sql.preparedQuery("""
				delete from blog_post_comment
				where
					id = $1
					and user_id = $2
				""")
			.execute(Tuple.of(id, userId))
			.onItem().transform(rowSet -> rowSet.rowCount() > 0);
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
