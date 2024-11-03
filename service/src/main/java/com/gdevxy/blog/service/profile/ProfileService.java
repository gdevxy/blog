package com.gdevxy.blog.service.profile;

import com.gdevxy.blog.client.gravatar.GravatarClient;
import com.gdevxy.blog.model.Profile;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ProfileService {

    private static final String GDEVXY_GRAVATAR_PROFILE_ID = "29cef4c32ea83b2f916f735017dbdfe4dfc9a91922ea15c62a787e9938baee4d";

    private final GravatarClient gravatarClient;
    private final ProfileConverter profileConverter;

    public ProfileService(@RestClient GravatarClient gravatarClient, ProfileConverter profileConverter) {
        this.gravatarClient = gravatarClient;
        this.profileConverter = profileConverter;
    }

    @CacheResult(cacheName = "profile")
    public Profile findProfile() {

        var profile = gravatarClient.findById(GDEVXY_GRAVATAR_PROFILE_ID);

        return profileConverter.apply(profile);
    }

}
