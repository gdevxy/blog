package com.gdevxy.blog.component.api.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostCommentDto {
    private Integer id;
    private String author;
    private String comment;
    private Instant createdAt;
    @Builder.Default
    private List<BlogPostCommentDto> replies = List.of();
}
