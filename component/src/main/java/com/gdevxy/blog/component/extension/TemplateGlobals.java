package com.gdevxy.blog.component.extension;

import io.quarkus.qute.TemplateGlobal;
import io.quarkus.runtime.configuration.ConfigUtils;
import org.eclipse.microprofile.config.ConfigProvider;

@TemplateGlobal
public class TemplateGlobals {

	public static final Boolean runningInProduction = ConfigUtils.isProfileActive("prod");

	public static final String baseUri = ConfigProvider.getConfig().getValue("application.base-uri", String.class);

}
