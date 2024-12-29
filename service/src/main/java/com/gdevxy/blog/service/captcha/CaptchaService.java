package com.gdevxy.blog.service.captcha;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ForbiddenException;

import com.gdevxy.blog.client.google.GoogleClient;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class CaptchaService {

	@RestClient
	GoogleClient googleClient;

	@ConfigProperty(name = "google.captcha.secret")
	String secret;

	public Uni<Void> verify(String captcha) {

		return googleClient.verifyCaptcha(secret, captcha)
			.invoke(Unchecked.consumer(res -> {
				if (!res.getSuccess()){
					log.info("Captcha validation failed {}", res);
					throw new ForbiddenException();
				}
			}))
			.replaceWithVoid();
	}

}
