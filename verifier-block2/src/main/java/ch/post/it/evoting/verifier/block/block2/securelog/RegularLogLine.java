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

public class RegularLogLine implements LogLine {
	final String message;
	final RegularLogLineMetadata metadata;

	public RegularLogLine(final String message, final RegularLogLineMetadata metadata) {
		this.message = checkNotNull(message);
		this.metadata = checkNotNull(metadata);
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public RegularLogLineMetadata getMetadata() {
		return this.metadata;
	}

	@Override
	public LogLineType type() {
		return LogLineType.REGULAR;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		RegularLogLine that = (RegularLogLine) o;
		return Objects.equals(message, that.message) && Objects.equals(metadata, that.metadata);
	}

	@Override
	public int hashCode() {
		return Objects.hash(message, metadata);
	}

	@Override
	public String toString() {
		return "RegularLogLine{" +
				"message='" + message + '\'' +
				", metadata=" + metadata +
				'}';
	}
}
