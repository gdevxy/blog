package com.gdevxy.blog.client.contentful;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import com.gdevxy.blog.client.contentful.model.PageBlogModel;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "contentful-cma")
public interface ContentfulCMAClient {

	@GET
	@Retry(maxRetries = 2)
	@Path("/content_types/pageBlogPost")
	PageBlogModel findPageBlogModel();

}
