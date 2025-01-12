package com.gdevxy.blog.model;

import jakarta.validation.constraints.NotEmpty;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class BlogPostCommentAction implements CaptchaProtectedAction {

	@NotEmpty
	private final String captcha;
	@NotEmpty
	private final String action;

	private final String author;
	private final String comment;

	@Override
	public String captcha() {
		return captcha;
	}

	@Override
	public String action() {
		return action;
	}
}
