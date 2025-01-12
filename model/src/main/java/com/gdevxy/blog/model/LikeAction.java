package com.gdevxy.blog.model;

import jakarta.validation.constraints.NotEmpty;

public record LikeAction(@NotEmpty String captcha, @NotEmpty String action) implements CaptchaProtectedAction {
}
