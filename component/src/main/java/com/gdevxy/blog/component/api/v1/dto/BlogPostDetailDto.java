package com.gdevxy.blog.component.api.v1.dto;

import com.gdevxy.blog.model.BlogPostComment;
import com.gdevxy.blog.model.BlogPostDetail;
import com.gdevxy.blog.model.BlogPostTag;
import com.gdevxy.blog.model.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostDetailDto {
    private String id;
    private String slug;
    private String title;
    private String description;
    private ZonedDateTime publishedDate;
    private Image image;
    private BlogPostDetail.Seo seo;
    private String rating;
    private List<BlogPostDetail.ContentBlock> blocks;
    private Set<BlogPostTag> tags;
    private List<BlogPostDetail.RelatedBlogPost> relatedBlogPosts;
    private Boolean liked;
    private List<BlogPostComment> comments;
    private boolean withIndexHeading;
}
