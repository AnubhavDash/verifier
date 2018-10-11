package ch.post.it.evoting.verifier.common.block.tools;

import ch.post.it.evoting.verifier.common.Language;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TranslationHelper {

    private static final MessageFormat formatter = new MessageFormat("");

    private TranslationHelper() {
        //only static usage
    }

    public static Map<Language, String> getFromResourceBundle(String resourceBundleName, String key) {
        HashMap<Language, String> result = new HashMap<>();
        Arrays.stream(Language.values()).forEach(lang -> result.put(lang, getFromResourceBundle(resourceBundleName, key, lang.getLocale())));
        return result;
    }

    public static Map<Language, String> getFromResourceBundle(String resourceBundleName, String key, String... args) {
        HashMap<Language, String> result = new HashMap<>();
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
        return formatter.format(ResourceBundle.getBundle(resourceBundleName, locale).getString(key), (Object[]) args);
    }

    public static Map<Language, String> getSameMessageMultiLanguage(String message) {
        return Arrays.stream(Language.values()).map(l -> new AbstractMap.SimpleEntry<>(l, message == null ? "" : message)).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }
}
