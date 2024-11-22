package com.gdevxy.blog.client.reflection;

import java.time.ZonedDateTime;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(targets={ ZonedDateTime.class })
public class ThirdPartyReflectionConfiguration {
}