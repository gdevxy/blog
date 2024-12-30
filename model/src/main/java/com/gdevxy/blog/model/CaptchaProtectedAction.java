package com.gdevxy.blog.model;

import jakarta.validation.constraints.NotEmpty;

public record CaptchaProtectedAction(@NotEmpty String captcha, @NotEmpty String action) {
}
