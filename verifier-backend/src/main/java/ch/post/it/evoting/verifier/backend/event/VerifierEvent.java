/*
 * (c) Copyright 2025 Swiss Post Ltd.
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
package ch.post.it.evoting.verifier.backend.event;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.ApplicationEvent;

/**
 * A Verifier specific event that will be received by the verifications.
 */
public abstract class VerifierEvent extends ApplicationEvent {

	private final String inputDirectory;

	protected VerifierEvent(final Object source, final String inputDirectory) {
		super(source);
		this.inputDirectory = inputDirectory;
	}

	public Path getInputDirectoryPath() {
		return Paths.get(this.inputDirectory);
	}

	public abstract String getType();

}
