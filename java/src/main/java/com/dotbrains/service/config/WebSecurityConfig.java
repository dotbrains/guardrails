package com.dotbrains.service.config;

import java.util.Arrays;
import java.util.Collections;

import com.dotbrains.service.config.properties.Properties;
import com.dotbrains.service.config.properties.WebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	private final WebSecurity webSecurity;

	public WebSecurityConfig(Properties properties) {
		this.webSecurity = properties.getWebSecurity();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf()
			.disable()
			.cors()
			.configurationSource(corsConfigurationSource())
			.and()
			.headers()
			.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
			.xssProtection((HeadersConfigurer<HttpSecurity>.XXssConfig xss) -> xss.xssProtectionEnabled(true));

		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(webSecurity.getCorsAllowedOrigins());
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "HEAD", "PUT", "DELETE"));
		configuration.setAllowedHeaders(Collections.singletonList("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
