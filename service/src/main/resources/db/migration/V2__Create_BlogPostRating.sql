create table blog_post_rating
(
	blog_post_id integer     not null,
	user_id      varchar(36) not null,
	foreign key (blog_post_id) references blog_post (id) on delete cascade
);

create unique index idx_blog_post_rating_blog_post_id_user_id on blog_post_rating (blog_post_id, user_id);
