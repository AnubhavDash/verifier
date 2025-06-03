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
package ch.post.it.evoting.verifier.backend.tools;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap.toImmutableMap;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;
import ch.post.it.evoting.verifier.backend.Language;

public class TranslationHelper {

	private static final MessageFormat formatter = new MessageFormat("");

	private TranslationHelper() {
		//only static usage
	}

	public static ImmutableMap<Language, String> getFromResourceBundle(final String resourceBundleName, final String key) {
		return Arrays.stream(Language.values())
				.collect(toImmutableMap(lang -> lang, lang -> getFromResourceBundle(resourceBundleName, key, lang.getLocale())));
	}

	public static ImmutableMap<Language, String> getFromResourceBundle(final String resourceBundleName, final String key, final String... args) {
		return Arrays.stream(Language.values())
				.collect(toImmutableMap(
						lang -> lang,
						lang -> {
							synchronized (formatter) {
								formatter.applyPattern(getFromResourceBundle(resourceBundleName, key, lang.getLocale()));
								return formatter.format(args);
							}
						}
				));
	}

	public static String getFromResourceBundle(final String resourceBundleName, final String key, final Locale locale) {
		return ResourceBundle.getBundle(resourceBundleName, locale).getString(key);
	}

	public static String getFromResourceBundle(final String resourceBundleName, final String key, final Locale locale, final String... args) {
		return MessageFormat.format(ResourceBundle.getBundle(resourceBundleName, locale).getString(key), (Object[]) args);
	}

}
