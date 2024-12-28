package com.gdevxy.blog.component;

import java.util.List;
import java.util.Set;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.RecentBlogPost;
import com.gdevxy.blog.service.contentful.ContentfulModelService;
import com.gdevxy.blog.service.contentful.blogpost.BlogPostService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Path("/")
@RequiredArgsConstructor
public class HomeResource {

	private final BlogPostService blogPostService;
	private final ContentfulModelService contentfulModelService;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Uni<TemplateInstance> home(@QueryParam("previewToken") String previewToken, @QueryParam("tags") Set<String> filterTags) {

		var recentBlogPosts = blogPostService.findRecentBlogPosts(previewToken).collect().asList();
		var blogPosts = blogPostService.findBlogPosts(previewToken, filterTags).collect().asList();
		var tags = contentfulModelService.findBlogPostTags();

		return Uni.combine().all().unis(recentBlogPosts, blogPosts, tags)
			.with((rbp, bp, t) -> Templates.home(rbp, bp).data("tags", t));
	}

	@GET
	@Path("/blog-posts-fragment")
	@Produces(MediaType.TEXT_HTML)
	public Uni<TemplateInstance> blogPosts(@QueryParam("previewToken") String previewToken, @QueryParam("tags") Set<String> tags) {

		return blogPostService.findBlogPosts(previewToken, tags).collect().asList().map(Templates::home$blog_posts);
	}

	@CheckedTemplate
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Templates {

		public static native TemplateInstance home$blog_posts(List<BlogPost> blogPosts);

		public static native TemplateInstance home(List<RecentBlogPost> recentBlogPosts, List<BlogPost> blogPosts);

	}

}
