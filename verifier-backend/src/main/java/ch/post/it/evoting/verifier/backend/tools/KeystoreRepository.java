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

package ch.post.it.evoting.verifier.backend.tools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class KeystoreRepository {

	private final String keystoreLocation;
	private final String keystorePasswordLocation;
	public KeystoreRepository(
			@Value("${direct.trust.keystore.location}")
			String keystoreLocation,
			@Value("${direct.trust.keystore.password.location}")
			String keystorePasswordLocation) {
		this.keystoreLocation = keystoreLocation;
		this.keystorePasswordLocation = keystorePasswordLocation;
	}

	public InputStream getKeyStore() throws IOException {
		return Files.newInputStream(Paths.get(keystoreLocation));
	}

	public char[] getKeystorePassword() throws IOException {
		return Files.readString(Paths.get(keystorePasswordLocation)).toCharArray();
	}
}