package com.gdevxy.blog.service.profile;

import jakarta.enterprise.context.ApplicationScoped;

import com.gdevxy.blog.client.gravatar.GravatarClient;
import com.gdevxy.blog.model.Profile;
import io.quarkus.cache.CacheResult;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ProfileService implements IProfileService {

	private static final String GDEVXY_GRAVATAR_PROFILE_ID = "29cef4c32ea83b2f916f735017dbdfe4dfc9a91922ea15c62a787e9938baee4d";

	private final GravatarClient gravatarClient;
	private final ProfileConverter profileConverter;

	public ProfileService(@RestClient GravatarClient gravatarClient, ProfileConverter profileConverter) {
		this.gravatarClient = gravatarClient;
		this.profileConverter = profileConverter;
	}

	@Override
	@CacheResult(cacheName = "profile")
	public Profile findProfile() {

		var profile = gravatarClient.findById(GDEVXY_GRAVATAR_PROFILE_ID);

		return profileConverter.apply(profile);
	}

}
