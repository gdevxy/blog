package com.gdevxy.blog.component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.BlogPostTag;
import com.gdevxy.blog.model.RecentBlogPost;
import com.gdevxy.blog.service.contentful.blogpost.IBlogPostService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Blocking
@Path("/")
@RequiredArgsConstructor
public class HomeResource {

	private final IBlogPostService blogPostService;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance home(@QueryParam("tags") Set<BlogPostTag> tags) {

		var recentBlogPosts = blogPostService.findRecentBlogPosts();
		var blogPosts = blogPostService.findBlogPosts(tags);

		return Templates.home(recentBlogPosts, blogPosts).data("filters", Arrays.asList(BlogPostTag.values()));
	}

	@GET
	@Path("/blog-posts-fragment")
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance blogPosts(@QueryParam("tags") Set<BlogPostTag> tags) {

		return Templates.home$blog_posts(blogPostService.findBlogPosts(tags));
	}

	@CheckedTemplate
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Templates {

		public static native TemplateInstance home$blog_posts(List<BlogPost> blogPosts);

		public static native TemplateInstance home(List<RecentBlogPost> recentBlogPosts, List<BlogPost> blogPosts);

	}

}
