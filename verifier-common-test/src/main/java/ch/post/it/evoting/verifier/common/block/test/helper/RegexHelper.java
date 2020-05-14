package ch.post.it.evoting.verifier.common.block.test.helper;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.regex.Pattern;

public class RegexHelper {
    public static Matcher<String> regexMatcher(final String regex) {
        return new TypeSafeMatcher<>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("a string matching ")
                        .appendValue(regex);
            }

            @Override
            protected boolean matchesSafely(final String item) {
                return Pattern.compile(regex).matcher(item).find();
            }
        };
    }
}
