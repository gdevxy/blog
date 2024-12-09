package com.gdevxy.blog.component.extension;

import io.quarkus.qute.TemplateGlobal;
import io.quarkus.runtime.configuration.ConfigUtils;

@TemplateGlobal
public class TemplateGlobals {

	public static final Boolean runningInProduction = ConfigUtils.isProfileActive("prod");

}