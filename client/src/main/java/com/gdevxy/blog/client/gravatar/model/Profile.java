package com.gdevxy.blog.client.gravatar.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@ToString
public class Profile {

    private final String hash;
    @JsonProperty("display_name")
    private final String displayName;
    @JsonProperty("profile_url")
    private final String profileUrl;
    @JsonProperty("avatar_url")
    private final String avatarUrl;
    private final String location;
    private final String description;
    @JsonProperty("job_title")
    private final String jobTitle;
    private final String company;
    @Builder.Default
    @JsonProperty("verified_accounts")
    private final List<Account> accounts = List.of();
    @JsonProperty("contact_info")
    private final ContactInfo contactInfo;

    @Getter
    @Builder
    @Jacksonized
    @ToString
    public static class Account {

        @JsonProperty("service_type")
        private final Type type;
        @JsonProperty("service_icon")
        private final String icon;
        private final String url;

        public enum Type {

            GITHUB,
            LINKEDIN,
            @JsonEnumDefaultValue
            UNSUPPORTED

        }

    }

    @Getter
    @Builder
    @Jacksonized
    @ToString
    public static class ContactInfo {

        private final String email;

    }

}
