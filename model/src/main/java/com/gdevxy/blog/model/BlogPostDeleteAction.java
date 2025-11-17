package com.gdevxy.blog.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class BlogPostDeleteAction implements CaptchaProtectedAction {

	@NotEmpty
	private final String captcha;
	@NotEmpty
	private final String action;

	@Override
	public String captcha() {
		return captcha;
	}

	@Override
	public String action() {
		return action;
	}

}
