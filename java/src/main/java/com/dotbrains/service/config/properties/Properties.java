package com.dotbrains.service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "application")
@Validated
public class Properties {
	private final WebSecurity webSecurity;

	public Properties() {
		webSecurity = new WebSecurity();
	}

	public WebSecurity getWebSecurity() {
		return webSecurity;
	}
}
