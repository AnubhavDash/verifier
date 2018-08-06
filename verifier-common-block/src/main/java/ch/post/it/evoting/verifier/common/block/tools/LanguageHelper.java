package ch.post.it.evoting.verifier.common.block.tools;

import ch.post.it.evoting.verifier.common.Language;

import java.text.MessageFormat;
import java.util.*;

public class LanguageHelper {
    private LanguageHelper() {
        //only static usage
    }

    public static Map<Language, String> getFromResourceBundle(String resourceBundleName, String key) {
        HashMap<Language, String> result = new HashMap<>();
        Arrays.stream(Language.values()).forEach(lang -> result.put(lang, getFromResourceBundle(resourceBundleName, key, lang.getLocale())));
        return result;
    }

    public static Map<Language, String> getFromResourceBundle(String resourceBundleName, String key, String args) {
        HashMap<Language, String> result = new HashMap<>();
        MessageFormat formatter = new MessageFormat("");
        Object[] messageArguments = (Object[])args.split("/");

        Arrays.stream(Language.values())
                .forEach(lang -> {
                    formatter.applyPattern(getFromResourceBundle(resourceBundleName, key, lang.getLocale()));
                    result.put(lang, formatter.format(messageArguments) );
                });
        return result;

    }

    public static String getFromResourceBundle(String resourceBundleName, String key, Locale locale) {
        return ResourceBundle.getBundle(resourceBundleName, locale).getString(key);
    }
}
