package com.gdevxy.blog.component;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.gdevxy.blog.model.Profile;
import com.gdevxy.blog.service.profile.IProfileService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Path("/about")
@RequiredArgsConstructor
public class AboutResource {

	private final IProfileService profileService;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Uni<TemplateInstance> contact() {

		return profileService.findProfile().map(Templates::about);
	}

	@CheckedTemplate
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Templates {

		public static native TemplateInstance about(Profile profile);

	}

}
