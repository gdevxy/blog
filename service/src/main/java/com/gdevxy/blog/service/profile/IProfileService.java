package com.gdevxy.blog.service.profile;

import com.gdevxy.blog.model.Profile;
import io.smallrye.mutiny.Uni;

public interface IProfileService {

	Uni<Profile> findProfile();

}
