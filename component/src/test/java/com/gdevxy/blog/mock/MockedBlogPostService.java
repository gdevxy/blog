package com.gdevxy.blog.mock;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.BlogPostTag;
import com.gdevxy.blog.model.RecentBlogPost;
import com.gdevxy.blog.mother.BlogPostMother;
import com.gdevxy.blog.mother.RecentBlogPostMother;
import com.gdevxy.blog.service.contentful.blogpost.IBlogPostService;
import io.quarkus.test.Mock;

@Mock
@ApplicationScoped
public class MockedBlogPostService implements IBlogPostService {

	@Override
	public Optional<BlogPost> findBlogPost(Boolean preview, String slug) {
		return Optional.of(BlogPostMother.basic().build());
	}

	@Override
	public List<BlogPost> findBlogPosts(Set<BlogPostTag> tags) {
		return List.of(BlogPostMother.basic().build());
	}

	@Override
	public List<RecentBlogPost> findRecentBlogPosts() {
		return List.of(RecentBlogPostMother.basic().build());
	}

}
