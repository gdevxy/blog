package com.gdevxy.blog.service.profile;

import com.gdevxy.blog.client.gravatar.model.Profile;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.function.Function;

@ApplicationScoped
public class ProfileConverter implements Function<Profile, com.gdevxy.blog.model.Profile> {

	@Override
	public com.gdevxy.blog.model.Profile apply(Profile profile) {

		return com.gdevxy.blog.model.Profile.builder()
				.displayName(profile.getDisplayName())
				.email(profile.getContactInfo().getEmail())
				.profileUrl(profile.getProfileUrl())
				.avatarUrl("https://gravatar.com/avatar/%s?s=200".formatted(profile.getHash()))
				.location(profile.getLocation())
				.description(profile.getDescription())
				.jobTitle(profile.getJobTitle())
				.company(profile.getCompany())
				.accounts(profile.getAccounts().stream().map(ProfileConverter::toProfileAccount).toList())
				.build();
	}

	private static com.gdevxy.blog.model.Profile.Account toProfileAccount(Profile.Account account) {

		return com.gdevxy.blog.model.Profile.Account.builder()
				.icon(account.getIcon())
				.url(account.getUrl())
				.type(account.getType().name())
				.build();
	}

}
