create table blog_post_comment
(
	id           integer primary key generated always as identity,
	blog_post_id integer                     not null,
	user_id      varchar(36)                 not null,
	author       varchar(25),
	comment      varchar(400)                not null,
	created_at   timestamp with time zone not null,
	foreign key (blog_post_id) references blog_post (id) on delete cascade
);

create index idx_blog_post_comment_blog_post_id on blog_post_comment (blog_post_id);
