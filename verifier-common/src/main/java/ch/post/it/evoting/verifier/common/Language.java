package ch.post.it.evoting.verifier.common;

import java.util.Locale;

public enum Language {

    DE(Locale.GERMAN), FR(Locale.FRENCH);

    private Locale locale;

    Language(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }
}
