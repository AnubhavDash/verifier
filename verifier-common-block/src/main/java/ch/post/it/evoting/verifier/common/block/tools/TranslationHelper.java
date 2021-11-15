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
package ch.post.it.evoting.verifier.common.block.tools;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import ch.post.it.evoting.verifier.common.Language;

public class TranslationHelper {

	private static final MessageFormat formatter = new MessageFormat("");

	private TranslationHelper() {
		//only static usage
	}

	public static Map<Language, String> getFromResourceBundle(String resourceBundleName, String key) {
		Map<Language, String> result = new EnumMap<>(Language.class);
		Arrays.stream(Language.values()).forEach(lang -> result.put(lang, getFromResourceBundle(resourceBundleName, key, lang.getLocale())));
		return result;
	}

	public static Map<Language, String> getFromResourceBundle(String resourceBundleName, String key, String... args) {
		Map<Language, String> result = new EnumMap<>(Language.class);
		Arrays.stream(Language.values())
				.forEach(lang -> {
					formatter.applyPattern(getFromResourceBundle(resourceBundleName, key, lang.getLocale()));
					result.put(lang, formatter.format(args));
				});
		return result;
	}

	public static String getFromResourceBundle(String resourceBundleName, String key, Locale locale) {
		return ResourceBundle.getBundle(resourceBundleName, locale).getString(key);
	}

	public static String getFromResourceBundle(String resourceBundleName, String key, Locale locale, String... args) {
		return MessageFormat.format(ResourceBundle.getBundle(resourceBundleName, locale).getString(key), (Object[]) args);
	}

	public static Map<Language, String> getSameMessageMultiLanguage(String message) {
		return Arrays.stream(Language.values()).map(l -> new AbstractMap.SimpleEntry<>(l, message == null ? "" : message))
				.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
	}
}
