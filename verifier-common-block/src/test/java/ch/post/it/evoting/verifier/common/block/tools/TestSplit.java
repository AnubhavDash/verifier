package ch.post.it.evoting.verifier.common.block.tools;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestSplit {

    @Test
    public void testSplit(){
        String[] results = splitStreetHousenumber("2bis, Chemin du lac", true);
        Assert.assertEquals("2bis", results[0]);
        Assert.assertEquals("Chemin du lac", results[1]);
    }

    private String[] splitStreetHousenumber(String combined, boolean logging) {

        if (combined == null) {
            return new String[]{"", ""};
        } else if (isNumeric(combined)) {
            return new String[]{"", combined};
        }

        List<Rule> rules = new ArrayList<>();
        rules.add(new Rule("Rule_1&2",  "^(\\d+(?:[a-zA-Z]{0,2}|bis|ter|quater)),(.*)$",    Arrays.asList(Selector.A1, Selector.A2),10));
        rules.add(new Rule("Rule_3",    "^(\\D[^\\d\\s]+\\D)$",                             Arrays.asList(Selector.A1, Selector.NOTHING),10));
        rules.add(new Rule("Rule_4",    "^(\\D.*)\\s+(\\d+([- a-zA-Z]{1,3}\\d+))$",         Arrays.asList(Selector.A1, Selector.A2), 17));
        rules.add(new Rule("Rule_5",    "^([a-zA-Z\\.]+)(\\d+)$",                           Arrays.asList(Selector.A1, Selector.A2), 10));
        rules.add(new Rule("Rule_6&7&8","^(.*)\\s(\\d+\\s*(?:bis|ter|[a-zA-Z]{1,2}))$",     Arrays.asList(Selector.A1, Selector.A2), 10));
        rules.add(new Rule("Rule_9",    "^([a-zA-Z]+)(\\d+(?:[a-zA-Z]{0,2}))$",             Arrays.asList(Selector.A1, Selector.A2), 10));
        rules.add(new Rule("Rule_10",   "^(\\D+)(\\d+.*[\\.|\\/].*)$",                      Arrays.asList(Selector.A1, Selector.A2), 718));
        rules.add(new Rule("Rule_11",   "^(.*)(?:Nr\\.|no\\.|No\\.)(.*)$",                  Arrays.asList(Selector.A1, Selector.A2), 720));
        rules.add(new Rule("Rule_12",   "^(.+)(?:Nr|No)(\\W{0,3}\\d+.*)$",                  Arrays.asList(Selector.A1, Selector.A2), 715));
        rules.add(new Rule("Rule_13",   "^(\\D+) ((?:bis|ter|[a-zA-Z]{1,2}))$",             Arrays.asList(Selector.A1, Selector.A2), 10));
        rules.add(new Rule("Rule_14",   "^(\\D.*) ([a-zA-Z]\\d+)$",                         Arrays.asList(Selector.A1, Selector.A2), 10));
        rules.add(new Rule("Rule_15",   "^(\\D[^ ]* [^ ]* [^ ]*\\D)$",                      Arrays.asList(Selector.A1, Selector.NOTHING), 5));
        rules.add(new Rule("Rule_19",   "^(\\d+ .*) (\\d*)$",                               Arrays.asList(Selector.A1, Selector.A2), 12));
        rules.add(new Rule("Rule_21",   "^(\\d+ (?:[a-zA-Z]{1,2}|ter|bis))(.*)$",           Arrays.asList(Selector.A2, Selector.A1_NOSPACE), 0));
        rules.add(new Rule("Rule_22",   "^(.*(?:Postfach|Casella Postale|Case Postale|C\\.P\\.|C\\. P\\.|PF\\.|CP\\.|P\\.B\\.|P\\.O\\.|Box|\\WCP|\\WPO|\\WPF|\\WPB|P\\.F\\.|P\\. F\\.|P\\. B\\.|PB\\.|P\\. O\\.|PO\\.|C\\.\\/P.|CP|C\\/P|Potfach|Casela Postale|Case Postal|Casella Postal|Casela Postal|PO Box|P\\.O\\. Box|P\\.O Box|P\\.O\\.Box|P\\. O\\. Box|P\\. O Box|PO\\. Box|PO\\.Box|POSTFACH).*)$",
                                                                                                         Arrays.asList(Selector.A1, Selector.NOTHING), 720));
        rules.add(new Rule("Rule_50",   "^([a-zA-Z \\D]+)(\\d+)$",                          Arrays.asList(Selector.A1, Selector.A2), 5));
        rules.add(new Rule("Rule_51",   "^(\\D* +)(\\d+,.*)$",                              Arrays.asList(Selector.A1, Selector.A2), 5));
        rules.add(new Rule("Rule_52",   "^([a-zA-Z]+) +no *(\\d+)$",                        Arrays.asList(Selector.A1, Selector.A2), 7));
        rules.add(new Rule("Rule_53",   "^(\\D.*) +(\\d+)$",                                Arrays.asList(Selector.A1, Selector.A2), 100));
        rules.add(new Rule("Rule_54",   "^(\\D.*) +(\\d+[- ]{1,3}\\d+)$",                   Arrays.asList(Selector.A1, Selector.A2), 101));
        rules.add(new Rule("Rule_55",   "^(\\d+\\s)+(([a-zA-Z]{3,})(.*))$",                 Arrays.asList(Selector.A2, Selector.A1), 8));


        combined = combined.trim();
        List<Candidate> candidateList = new ArrayList<>();

        for (Rule rule : rules) {
            Pattern pattern = Pattern.compile(rule.p);
            Matcher matcher = pattern.matcher(combined);

            if (matcher.matches()){
                String[] results = rule.selectors.stream().map(selector -> selector.apply(matcher)).toArray(String[]::new);
                candidateList.add(new Candidate(rule, results));
                if (logging) {
                    System.out.println(combined + " ---" + rule.rulename + "---> " + results[0] + "¦" + results[1]);
                }
            }
        }

        if (candidateList.isEmpty()) {
            if (logging) {
                System.out.println(combined + " ---> NO MATCH!");
            }
            return new String[]{combined, ""};
        } else if (candidateList.size() == 1) {
            return candidateList.get(0).results;
        } else {
            Candidate bestCandidate = candidateList.stream()
                    .max(Comparator.comparing(Candidate::getPriority))
                    .orElseThrow(NoSuchElementException::new);
            if (logging) {
                System.out.println(">>> Winner: " + bestCandidate.rule.rulename);
            }
            return bestCandidate.results;
        }

    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer i = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private class Rule {
        String rulename;
        String p;
        List<Selector> selectors;
        int prio;

        Rule(String rulename, String p, List<Selector> selectors, int prio) {
            this.rulename = rulename;
            this.p = p;
            this.selectors = selectors;
            this.prio = prio;
        }
    }

    private class Candidate {
        Rule rule;
        String[] results;

        Candidate(Rule rule, String[] results) {
            this.rule = rule;
            this.results = results;
        }

        int getPriority() {
            return rule.prio;
        }
    }

    enum Selector {
        A1 {
            @Override
            public String apply(Matcher matcher) {
                return matcher.group(1).trim();
            }
        },
        A2 {
            @Override
            public String apply(Matcher matcher) {
                return matcher.group(2).trim();
            }
        },
        A1_NOSPACE {
            @Override
            public String apply(Matcher matcher) {
                return matcher.group(1).replace(" ", "");
            }
        },
        NOTHING {
            @Override
            public String apply(Matcher matcher) {
                return "";
            }
        };
        abstract String apply(Matcher matcher);
    }


}
