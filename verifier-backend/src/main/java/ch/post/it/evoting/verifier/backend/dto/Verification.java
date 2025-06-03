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
package ch.post.it.evoting.verifier.backend.dto;


import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.Language;
import ch.post.it.evoting.verifier.backend.Status;

public class Verification {

	private String id;
	private String verificationId;
	private String block;
	private String name;
	private Category category;
	private ImmutableMap<Language, String> description;
	private Status status;
	private ImmutableMap<Language, String> message;
	private ImmutableSet<String> verifierEvents;
	private ImmutableList<String> errorStack;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getVerificationId() {
		return verificationId;
	}

	public void setVerificationId(final String verificationId) {
		this.verificationId = verificationId;
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

	public ImmutableMap<Language, String> getDescription() {
		return description;
	}

	public void setDescription(final ImmutableMap<Language, String> description) {
		this.description = description;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public ImmutableMap<Language, String> getMessage() {
		return message;
	}

	public void setMessage(final ImmutableMap<Language, String> message) {
		this.message = message;
	}

	public ImmutableSet<String> getVerifierEvents() {
		return verifierEvents;
	}

	public void setVerifierEvents(final ImmutableSet<String> verifierEvents) {
		this.verifierEvents = verifierEvents;
	}

	public ImmutableList<String> getErrorStack() {
		return errorStack;
	}

	public void setErrorStack(final ImmutableList<String> errorStack) {
		this.errorStack = errorStack;
	}
}
