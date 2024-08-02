package com.dotbrains.service.exceptions

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import java.lang.reflect.Method

class CustomAsyncExceptionHandler : AsyncUncaughtExceptionHandler {
	override fun handleUncaughtException(ex: Throwable, method: Method, vararg params: Any) {
		logger.error(
			"Unexpected asynchronous exception at : {}.{}",
			method.declaringClass.name,
			method.name
		)
		for (param in params) {
			logger.error("Parameter value - {}", param)
		}
		logger.error("Exception message - {}", ex.message)
	}

	companion object {
		private val logger = LoggerFactory.getLogger(
			CustomAsyncExceptionHandler::class.java
		)
	}
}
