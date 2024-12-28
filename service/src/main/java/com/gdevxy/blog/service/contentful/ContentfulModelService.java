package com.gdevxy.blog.service.contentful;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.client.contentful.ContentfulCMAClient;
import com.gdevxy.blog.client.contentful.model.PageBlogModel;
import com.gdevxy.blog.model.BlogPostTag;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ContentfulModelService {

	@RestClient
	ContentfulCMAClient client;

	@CacheResult(cacheName = "blog-post-tags")
	public Uni<List<BlogPostTag>> findBlogPostTags() {

		return client.findPageBlogModel()
			.onItem().transformToMulti(m -> Multi.createFrom().iterable(m.getFields()))
			.filter(f -> f.getId().equals("tags"))
			.toUni()
			.map(PageBlogModel.Field::getItems)
			.onItem().transformToMulti(i -> Multi.createFrom().iterable(i.getValidations()))
			.onItem().transformToIterable(PageBlogModel.Field.Items.Validation::getIn)
			.map(BlogPostTag::new)
			.collect()
			.asList();
	}

}
