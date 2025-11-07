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
public class ProfileDto {
    private String displayName;
    private String email;
    private String profileUrl;
    private String avatarUrl;
    private String location;
    private String description;
    private String jobTitle;
    private String company;
    @Builder.Default
    private List<AccountDto> accounts = List.of();

    private String getJob() {
        return String.join(", ", jobTitle, company);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountDto {
        private String icon;
        private String url;
        private String type;
    }
}
