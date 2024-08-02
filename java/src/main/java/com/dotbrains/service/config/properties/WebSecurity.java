package com.dotbrains.service.config.properties;

import java.util.Arrays;
import java.util.List;

public class WebSecurity {
	private String corsAllowedOrigins;

	public List<String> getCorsAllowedOrigins() {
		return Arrays.asList(corsAllowedOrigins.split(","));
	}
}
