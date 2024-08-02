package com.dotbrains.service.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import java.io.IOException

@Configuration
class SslConfig {
	@Value("\${server.ssl.trust-store}")
	private val trustStoreResource: Resource? = null

	@Value("\${server.ssl.trust-store-password}")
	private val trustStorePassword: String? = null

	@Value("\${server.ssl.key-store}")
	private val keyStoreResource: Resource? = null

	@Value("\${server.ssl.key-store-password}")
	private val keyStorePassword: String? = null

	@Bean
	@Throws(IOException::class)
	fun setUpTrustStoreForApplication() {
		val trustStoreFilePath = trustStoreResource!!.file.absolutePath
		logger.info("TrustStore location is {}", trustStoreFilePath)
		System.setProperty("javax.net.ssl.trustStore", trustStoreFilePath)
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword)
	}

	@Bean
	@Throws(IOException::class)
	fun setUpKeyStoreForApplication() {
		val keyStoreFilePath = keyStoreResource!!.file.absolutePath
		logger.info("KeyStore location is {}", keyStoreFilePath)
		System.setProperty("javax.net.ssl.keyStore", keyStoreFilePath)
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword)
	}

	companion object {
		private val logger = LoggerFactory.getLogger(SslConfig::class.java)
	}
}
