create table blog_post_comment_reply
(
	id                   integer primary key generated always as identity,
	blog_post_comment_id integer                  not null,
	user_id              varchar(36)              not null,
	author               varchar(25),
	comment              varchar(2000)            not null,
	created_at           timestamp with time zone not null,
	foreign key (blog_post_comment_id) references blog_post_comment (id) on delete cascade
);

create index idx_blog_post_comment_reply_blog_post_comment_id on blog_post_comment_reply (blog_post_comment_id);
