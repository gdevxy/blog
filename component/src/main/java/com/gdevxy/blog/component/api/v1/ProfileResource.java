package com.gdevxy.blog.component.api.v1;

import com.gdevxy.blog.model.Profile;
import com.gdevxy.blog.service.profile.IProfileService;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/api/v1/profile")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class ProfileResource {

	private final IProfileService profileService;

	@GET
	public Uni<Profile> getProfile() {

		return profileService.findProfile();
	}

}
