package com.gdevxy.blog.service.contentful;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.client.contentful.ContentfulCMAClient;
import com.gdevxy.blog.client.contentful.model.PageBlogModel;
import com.gdevxy.blog.model.BlogPostTag;
import io.quarkus.cache.CacheResult;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ContentfulModelService {

	@RestClient
	ContentfulCMAClient client;

	@CacheResult(cacheName = "blog-post-tags")
	public List<BlogPostTag> findBlogPostTags() {

		return client.findPageBlogModel()
			.getFields()
			.stream()
			.filter(f -> f.getId().equals("tags"))
			.findAny()
			.map(PageBlogModel.Field::getItems)
			.map(PageBlogModel.Field.Items::getValidations)
			.map(List::getFirst)
			.map(PageBlogModel.Field.Items.Validation::getIn)
			.orElse(List.of())
			.stream()
			.map(BlogPostTag::new)
			.toList();
	}

}
