create table blog_post_comment
(
	id           bigint       primary key generated always as identity,
	blog_post_id integer      not null,
	author       varchar(25),
	comment      varchar(400) not null,
	foreign key (blog_post_id) references blog_post (id) on delete cascade
);
