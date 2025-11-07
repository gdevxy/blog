package com.gdevxy.blog.component.api.v1.dto;

import com.gdevxy.blog.model.BlogPostComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostCommentsResponseDto {
    private List<BlogPostComment> comments;
    private int totalCount;
}
