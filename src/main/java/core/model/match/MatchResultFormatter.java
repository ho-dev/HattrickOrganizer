package core.model.match;

import core.model.TranslationFacility;

/**
 * Formats match scores for display, including the localized separator and result extension.
 */
public final class MatchResultFormatter {

    private MatchResultFormatter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String TRANSLATION_KEY_SEPARATOR = "ls.match.result.separation";

    private static String getSeparator() {
        return " " + TranslationFacility.tr(TRANSLATION_KEY_SEPARATOR) + " ";
    }

    public static String format(int homeGoals, int awayGoals, String resultExtensionAbbreviation) {
        if (homeGoals < 0 || awayGoals < 0)
            return "  " + getSeparator();

        final StringBuilder buffer = new StringBuilder();
        if (homeGoals < 10) {
            buffer.append(" ");
        }
        buffer.append(homeGoals);
        buffer.append(getSeparator());
        buffer.append(awayGoals);
        if (!resultExtensionAbbreviation.isEmpty()) {
            buffer.append(" ").append(resultExtensionAbbreviation);
        }
        return buffer.toString();
    }
}
