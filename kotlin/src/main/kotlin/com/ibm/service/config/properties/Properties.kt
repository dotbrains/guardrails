package com.dotbrains.service.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Configuration
@ConfigurationProperties(prefix = "application")
@Validated
class Properties {
	val webSecurity: WebSecurity = WebSecurity()
}
