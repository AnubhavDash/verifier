/*
 * Copyright 2022 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.backend.config;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncExceptionHandler.class);

	@Override
	public void handleUncaughtException(final Throwable throwable, final Method method, final Object... objects) {
		LOGGER.error("Exception message - {}", throwable.getMessage());
		LOGGER.error("Method name - {}", method.getName());
		for (final Object param : objects) {
			LOGGER.error("Parameter value - {}", param);
		}
	}

}
