package com.dotbrains.service.config

import com.dotbrains.service.config.properties.Properties
import com.dotbrains.service.config.properties.WebSecurity
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter
import org.springframework.web.cors.CorsConfiguration
import java.util.*

@Configuration
@EnableWebSecurity
class WebSecurityConfig(properties: Properties) {

	private val webSecurity: WebSecurity = properties.webSecurity

	@Bean
	@Throws(Exception::class)
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		http
			.csrf()
			.disable()
			.cors()
			.configurationSource { _: HttpServletRequest? ->
				val configuration = CorsConfiguration()
				configuration.allowedOrigins = webSecurity.getCorsAllowedOrigins()
				configuration.allowedMethods = mutableListOf("GET", "POST", "HEAD", "DELETE")
				configuration.allowedHeaders = listOf("*")
				configuration
			}
			.and()
			.headers()
			.frameOptions { obj: HeadersConfigurer<HttpSecurity>.FrameOptionsConfig -> obj.sameOrigin() }
			.xssProtection { xss: HeadersConfigurer<HttpSecurity>.XXssConfig ->
				xss.headerValue(
					XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK
				)
			}

		return http.build()
	}
}
