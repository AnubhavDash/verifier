/*
 * Copyright 2021 Post CH Ltd
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
package ch.post.it.evoting.verifier.common;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class VerificationDefinition {
	private int id;
	private int blockId;
	private String name;
	private Category category;
	private boolean deactivated;
	private EnumSet<VerificationTrait> verificationTraits = EnumSet.noneOf(VerificationTrait.class);
	private Map<Language, String> description;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBlockId() {
		return blockId;
	}

	public void setBlockId(int blockId) {
		this.blockId = blockId;
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

	public boolean isDeactivated() {
		return deactivated;
	}

	public void setDeactivated(boolean deactivated) {
		this.deactivated = deactivated;
	}

	public boolean containsVerificationTrait(VerificationTrait trait) {
		return this.verificationTraits.contains(trait);
	}

	public boolean containsAnyVerificationTrait(Set<VerificationTrait> traits) {
		return !this.verificationTraits.isEmpty() && this.verificationTraits.stream().anyMatch(t -> traits.contains(t));
	}

	public void addVerificationTrait(VerificationTrait trait) {
		this.verificationTraits.add(trait);
	}

	public void removeVerificationTrait(VerificationTrait trait) {
		this.verificationTraits.remove(trait);
	}

	public EnumSet<VerificationTrait> getVerificationTraits() {
		return this.verificationTraits;
	}

	public Map<Language, String> getDescription() {
		return description;
	}

	public void setDescription(Map<Language, String> description) {
		this.description = description;
	}

	public String computeUniqueKey() {
		return String.format("%02d-%02d", this.getBlockId(), this.getId());
	}
}
