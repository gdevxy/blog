package com.gdevxy.blog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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

	@Size(max = 25)
	private final String author;

	@NotBlank
	@Size(max = 2000)
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
