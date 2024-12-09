package com.gdevxy.blog.service.contentful.blogpost;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.BlogPostTag;
import com.gdevxy.blog.model.RecentBlogPost;

public interface IBlogPostService {

	Optional<BlogPost> findBlogPost(Boolean preview, String slug);

	List<BlogPost> findBlogPosts(Set<BlogPostTag> tags);

	List<RecentBlogPost> findRecentBlogPosts();

}
