package com.dotbrains.service.config.properties

class WebSecurity {
	private val corsAllowedOrigins: String? = null

	fun getCorsAllowedOrigins(): List<String> {
		return listOf(
			*corsAllowedOrigins!!
				.split(",".toRegex()).dropLastWhile { it.isEmpty() }
				.toTypedArray()
		)
	}
}
