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
package ch.post.it.evoting.verifier.backend.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.Language;
import ch.post.it.evoting.verifier.backend.Status;

public class Verification {

	private String id;
	private String verificationId;
	private String block;
	private String name;
	private Category category;
	private Map<Language, String> description;
	private Status status;
	private Map<Language, String> message;
	private Set<String> verifierEvents;
	private List<String> errorStack;

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

	public Map<Language, String> getDescription() {
		return description;
	}

	public void setDescription(final Map<Language, String> description) {
		this.description = description;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public Map<Language, String> getMessage() {
		return message;
	}

	public void setMessage(final Map<Language, String> message) {
		this.message = message;
	}

	public Set<String> getVerifierEvents() {
		return verifierEvents;
	}

	public void setVerifierEvents(final Set<String> verifierEvents) {
		this.verifierEvents = verifierEvents;
	}

	public List<String> getErrorStack() {
		return errorStack;
	}

	public void setErrorStack(final List<String> errorStack) {
		this.errorStack = errorStack;
	}
}
