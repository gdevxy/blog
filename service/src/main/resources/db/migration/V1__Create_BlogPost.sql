create table blog_post
(
	id     integer     primary key generated always as identity,
	key    varchar(22) not null
);

create unique index idx_blog_post_key on blog_post (key);
