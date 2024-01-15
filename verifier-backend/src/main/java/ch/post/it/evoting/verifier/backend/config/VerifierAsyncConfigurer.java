/*
 * (c) Copyright 2024 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.backend.config;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class VerifierAsyncConfigurer implements AsyncConfigurer {

	private static final int CORE_POOL_SIZE = 10;
	private static final int MAX_POOL_SIZE = 100;

	private final AsyncExceptionHandler asyncExceptionHandler;

	@Lazy
	VerifierAsyncConfigurer(final AsyncExceptionHandler asyncExceptionHandler) {
		this.asyncExceptionHandler = asyncExceptionHandler;
	}

	@Override
	public Executor getAsyncExecutor() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(CORE_POOL_SIZE);
		executor.setMaxPoolSize(MAX_POOL_SIZE);
		executor.initialize();

		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return asyncExceptionHandler;
	}

}
