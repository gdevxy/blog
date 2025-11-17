package com.gdevxy.blog.service.hmac;

import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.SneakyThrows;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ApplicationScoped
public class HmacService {

	private static final String ALGORITHM = "HmacSHA256";

	private final SecretKeySpec secretKey;

	public HmacService(HmacConfiguration config) {
		this.secretKey = new SecretKeySpec(config.secret().getBytes(StandardCharsets.UTF_8), ALGORITHM);
	}

	@SneakyThrows
	public String generate(String src) {

		var mac = Mac.getInstance(ALGORITHM);
		mac.init(secretKey);

		var hmac = mac.doFinal(src.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(hmac);
	}

	/**
	 * Configuration mapping for HMAC settings.
	 */
	@ConfigMapping(prefix = "hmac")
	public interface HmacConfiguration {

		/**
		 * The HMAC secret key.
		 *
		 * @return the secret key
		 */
		String secret();

	}

}
