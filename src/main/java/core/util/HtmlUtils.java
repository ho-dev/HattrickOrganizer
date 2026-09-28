package core.util;

import org.apache.commons.text.StringEscapeUtils;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public final class HtmlUtils {

    private HtmlUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String stringToHtml(String plainText) {
        if (isEmpty(plainText)) {
            return null;
        }
        String htmlEscaped = StringEscapeUtils.escapeHtml4(plainText);
        return "<html>" + htmlEscaped.replaceAll("\\R", "<br>") + "</html>";
    }
}
