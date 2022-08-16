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
package ch.post.it.evoting.verifier.backend.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.Language;
import ch.post.it.evoting.verifier.backend.Status;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Verification {
	private String id;
	private int verificationId;
	private String block;
	private String name;
	private Category category;
	private Map<Language, String> description;
	private Status status;
	private Map<Language, String> message;
	private Set<String> verifierEvents;
	private List<String> errorStack;
}
