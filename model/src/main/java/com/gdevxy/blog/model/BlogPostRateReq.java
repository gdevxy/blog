package com.gdevxy.blog.model;

import jakarta.validation.constraints.NotEmpty;

public record BlogPostRateReq(@NotEmpty String captcha) {
}
