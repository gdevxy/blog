package com.gdevxy.blog.component.api.v1.dto;

import com.gdevxy.blog.model.BlogPostTag;
import com.gdevxy.blog.model.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostSummaryDto {
    private String id;
    private String slug;
    private String title;
    private String description;
    private ZonedDateTime publishedDate;
    private Image image;
    private Set<BlogPostTag> tags;
}
