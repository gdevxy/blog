package com.gdevxy.blog.service;

import com.gdevxy.blog.model.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import lombok.experimental.UtilityClass;

import java.util.function.Predicate;

@UtilityClass
public class MutinyUtils {

	public <T> Uni<T> unauthorizedOrContinue(T value, Predicate<T> ifFailed) {

		if (ifFailed.test(value)) {
			return Uni.createFrom().failure(new UnauthorizedException());
		}

		return Uni.createFrom().item(value);
	}

}
