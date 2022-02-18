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
package ch.post.it.evoting.verifier.block.block2.securelog;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

public class CheckpointLogLine implements LogLine {
	private final String message;
	private final CheckpointMetadata metadata;

	public CheckpointLogLine(final String message, final CheckpointMetadata metadata) {
		checkNotNull(message);
		checkNotNull(metadata);
		this.message = message;
		this.metadata = metadata;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public CheckpointMetadata getMetadata() {
		return this.metadata;
	}

	@Override
	public LogLineType type() {
		return LogLineType.CHECKPOINT;
	}

	@Override
	public String toString() {
		return "CheckpointLogLine{" +
				"message='" + message + '\'' +
				", metadata=" + metadata +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CheckpointLogLine that = (CheckpointLogLine) o;
		return Objects.equals(message, that.message) && Objects.equals(metadata, that.metadata);
	}

	@Override
	public int hashCode() {
		return Objects.hash(message, metadata);
	}
}
