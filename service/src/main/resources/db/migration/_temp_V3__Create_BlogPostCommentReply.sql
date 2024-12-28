create table blog_post_comment_reply
(
	id        bigint      not null,
	author    varchar(25),
	comment   varchar(400) not null,
	foreign key (id) references blog_post_comment(id) on delete cascade
);
