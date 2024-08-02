package com.dotbrains.service.config

import com.dotbrains.service.exceptions.CustomAsyncExceptionHandler
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableAsync
class AsyncConfig : AsyncConfigurer {
	override fun getAsyncExecutor(): Executor? {
		val executor = ThreadPoolTaskExecutor()
		executor.maxPoolSize = 100
		executor.corePoolSize = 75
		executor.queueCapacity = 75
		executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
		executor.setThreadNamePrefix("Executor-")
		executor.initialize()
		return executor
	}

	override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler? {
		return CustomAsyncExceptionHandler()
	}
}
