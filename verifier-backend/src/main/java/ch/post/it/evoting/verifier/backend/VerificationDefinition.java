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
package ch.post.it.evoting.verifier.backend;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class VerificationDefinition {

	private final Set<String> verifierEvents = new HashSet<>();

	private String id;
	private String block;
	private String name;
	private Category category;
	private Map<Language, String> description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void addVerifierEvent(String event) {
		this.verifierEvents.add(event);
	}

	public Set<String> getVerifierEvents() {
		return this.verifierEvents;
	}

	public Map<Language, String> getDescription() {
		return description;
	}

	public void setDescription(Map<Language, String> description) {
		this.description = description;
	}

	public String computeUniqueKey() {
		return String.format("%s-%s", this.getBlock(), this.getId());
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final VerificationDefinition that = (VerificationDefinition) o;
		return id == that.id && block.equals(that.block) && Objects.equals(name, that.name)
				&& category == that.category && Objects.equals(verifierEvents, that.verifierEvents) && Objects.equals(
				description, that.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, block, name, category, verifierEvents, description);
	}
}
