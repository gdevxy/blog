package com.gdevxy.blog.service.profile;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.gdevxy.blog.client.gravatar.GravatarClient;
import com.gdevxy.blog.model.Profile;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ProfileService implements IProfileService {

	private static final String GDEVXY_GRAVATAR_PROFILE_ID = "29cef4c32ea83b2f916f735017dbdfe4dfc9a91922ea15c62a787e9938baee4d";

	@RestClient
	GravatarClient gravatarClient;
	@Inject
	ProfileConverter profileConverter;

	@Override
	@CacheResult(cacheName = "profile")
	public Uni<Profile> findProfile() {

		return gravatarClient.findById(GDEVXY_GRAVATAR_PROFILE_ID).map(profileConverter);
	}

}
