package com.gdevxy.blog.component.api.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostsResponseDto {
    private List<BlogPostSummaryDto> posts;
    private int currentPage;
    private int pageSize;
    private boolean hasNextPage;
    private long totalCount;
}
