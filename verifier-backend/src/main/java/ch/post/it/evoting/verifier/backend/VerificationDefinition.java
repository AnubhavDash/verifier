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
package ch.post.it.evoting.verifier.backend;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet;

public class VerificationDefinition {

	private final Set<String> verifierEvents = new HashSet<>();

	private String id;
	private String block;
	private String name;
	private Category category;
	private ImmutableMap<Language, String> description;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(final String block) {
		this.block = block;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(final Category category) {
		this.category = category;
	}

	public void addVerifierEvent(final String event) {
		this.verifierEvents.add(event);
	}

	public ImmutableSet<String> getVerifierEvents() {
		return ImmutableSet.from(this.verifierEvents);
	}

	public ImmutableMap<Language, String> getDescription() {
		return description;
	}

	public void setDescription(final ImmutableMap<Language, String> description) {
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
		return id.equals(that.id) && block.equals(that.block) && Objects.equals(name, that.name)
				&& category == that.category && Objects.equals(verifierEvents, that.verifierEvents) && Objects.equals(
				description, that.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, block, name, category, verifierEvents, description);
	}
}
