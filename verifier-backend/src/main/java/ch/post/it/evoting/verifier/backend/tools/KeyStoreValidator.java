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

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Arrays;
import java.util.function.BooleanSupplier;

import ch.post.it.evoting.cryptoprimitives.domain.signature.Alias;

/**
 * Validates that the keystore contains only the certificates of other authorities.
 */
public final class KeyStoreValidator {

	private KeyStoreValidator() {
		// utility class
	}

	public static boolean validateKeyStore(KeyStore keyStore) {
		BooleanSupplier signerCertificatesPresent = () -> Arrays.stream(Alias.values())
				.allMatch(alias -> {
					try {
						return keyStore.getCertificate(alias.get()) != null;
					} catch (KeyStoreException e) {
						return false;
					}
				});

		return signerCertificatesPresent.getAsBoolean();
	}
}