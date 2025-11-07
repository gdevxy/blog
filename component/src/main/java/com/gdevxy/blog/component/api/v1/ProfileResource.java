package com.gdevxy.blog.component.api.v1;

import com.gdevxy.blog.component.api.v1.dto.ProfileDto;
import com.gdevxy.blog.model.Profile;
import com.gdevxy.blog.service.profile.IProfileService;
import lombok.extern.slf4j.Slf4j;
import io.smallrye.mutiny.Uni;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.stream.Collectors;

@Slf4j
@Path("/api/v1/profile")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProfileResource {

    @Inject
    IProfileService profileService;

    /**
     * GET /api/v1/profile
     * Retrieve user profile information (for About page)
     */
    @GET
    public Uni<ProfileDto> getProfile() {
        log.info("Fetching user profile");

        return profileService.findProfile()
            .map(this::toProfileDto)
            .onFailure().invoke(e -> log.error("Error fetching profile", e));
    }

    // ========== Helper Methods ==========

    private ProfileDto toProfileDto(Profile profile) {
        return ProfileDto.builder()
            .displayName(profile.getDisplayName())
            .email(profile.getEmail())
            .profileUrl(profile.getProfileUrl())
            .avatarUrl(profile.getAvatarUrl())
            .location(profile.getLocation())
            .description(profile.getDescription())
            .jobTitle(profile.getJobTitle())
            .company(profile.getCompany())
            .accounts(profile.getAccounts().stream()
                .map(account -> ProfileDto.AccountDto.builder()
                    .icon(account.getIcon())
                    .url(account.getUrl())
                    .type(account.getType())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
