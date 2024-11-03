package com.gdevxy.blog.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.StringJoiner;

@Getter
@Builder
@ToString
public class Profile {

    private final String displayName;
    private final String email;
    private final String profileUrl;
    private final String avatarUrl;
    private final String location;
    private final String description;
    private final String jobTitle;
    private final String company;
    @Builder.Default
    private final List<Account> accounts = List.of();

    public String getJob() {
        return String.join(", ", jobTitle, company);
    }

    @Getter
    @Builder
    @ToString
    public static class Account {

        private final String icon;
        private final String url;
        private final String type;

    }

}
