package com.gdevxy.blog.service.captcha;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;

import com.gdevxy.blog.client.google.GoogleClient;
import com.gdevxy.blog.client.google.model.CaptchaVerifyResponse;
import com.gdevxy.blog.model.CaptchaProtectedAction;
import com.gdevxy.blog.model.LikeAction;
import io.smallrye.config.ConfigMapping;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class CaptchaService {

	@RestClient
	GoogleClient googleClient;

	@Inject
	CaptchaServiceConfiguration configuration;

	public Uni<Void> verify(CaptchaProtectedAction action) {

		return googleClient.verifyCaptcha(configuration.secret(), action.captcha())
			.invoke(Unchecked.consumer(res -> {
				if (!isValidResponse(res, action.action())){
					log.info("Captcha validation failed [action: {}, response: {}]", action.action(), res);
					throw new ForbiddenException();
				}
			}))
			.replaceWithVoid();
	}

	/**
	 * @see <a href="https://developers.google.com/recaptcha/docs/v3"/>
	 */
	private boolean isValidResponse(CaptchaVerifyResponse response, String action) {

		if (!response.getSuccess()) {
			return false;
		}

		if (!response.getAction().equals(action)) {
			return false;
		}

		// [0.0 - 1.0] 1.0 being the most confident
		if (response.getScore() < 0.6) {
			return false;
		}

		return response.getHostname().endsWith(configuration.hostname());
	}

	/**
	 * Configuration mapping for Google reCAPTCHA settings.
	 */
	@ConfigMapping(prefix = "google.captcha")
	public interface CaptchaServiceConfiguration {

		/**
		 * The reCAPTCHA secret key.
		 *
		 * @return the secret key
		 */
		String secret();

		/**
		 * The hostname for reCAPTCHA verification.
		 *
		 * @return the hostname
		 */
		String hostname();

	}

}
