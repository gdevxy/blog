package com.gdevxy.blog.client.gravatar;

import com.gdevxy.blog.client.gravatar.model.Profile;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "gravatar")
public interface GravatarClient {

    @GET
    @Retry(maxRetries = 2)
    @Path("/v3/profiles/{id}")
    Profile findById(@PathParam("id") String id);

    @GET
    @Retry(maxRetries = 2)
    @Path("/avatar/{id}")
    String findAvatarUrl(@PathParam("id") String id);

}
