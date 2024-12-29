package com.gdevxy.blog.client.google;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import com.gdevxy.blog.client.google.model.CaptchaVerifyResponse;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "google")
public interface GoogleClient {

	@POST
	@Path("/recaptcha/api/siteverify")
	Uni<CaptchaVerifyResponse> verifyCaptcha(@QueryParam("secret") String secret, @QueryParam("response") String response);

}
