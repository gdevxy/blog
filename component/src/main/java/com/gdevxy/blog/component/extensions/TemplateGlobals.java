package com.gdevxy.blog.component.extensions;

import io.quarkus.qute.TemplateGlobal;
import io.quarkus.runtime.configuration.ConfigUtils;

@TemplateGlobal
public class TemplateGlobals {

	public static final Boolean runningInProduction = ConfigUtils.isProfileActive("prod");

}