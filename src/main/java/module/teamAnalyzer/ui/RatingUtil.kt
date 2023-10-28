// %1126721046323:hoplugins.commons.utils%
package module.teamAnalyzer.ui;

import core.constants.player.PlayerAbility;
import core.model.HOVerwaltung;

import java.util.List;
import java.util.StringTokenizer;


/**
 * Utility for Rating Formatting
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public final class RatingUtil {
    /**
     * Private default constuctor to prevent class instantiation.
     */
    private RatingUtil() {
    }

    /**
     * Utility Method that returns a string representation of the rating
     *
     * @param rating int value of the rating [0..80]
     * @param showNumber is the numerical representation being shown
     * @param showText is the textual representation being shown
     * @return String with rating as configured
     */
    public static String getRating(int rating, boolean showNumber,
        boolean showText) {
        if (rating == 0) {
            return "";
        }

        final String value = PlayerAbility.getNameForSkill((rating-1)/4d+1, showNumber, true);
        String level = value;
        String subLevel = "";
        final StringTokenizer st = new StringTokenizer(value, "(");
     
        if (value.contains("(") && value.contains(")")) {
	        level = st.nextToken().trim();
	        if (rating > 80) // divine(very high)
	        {
	        	// we have divine(+1)(very high) for instance
	        	// add the 1 to level, and remove the (+1) part.
	        	level += "(" + st.nextToken().trim();
	        }
	        StringTokenizer st2 = new StringTokenizer(st.nextToken(), ")");
	        subLevel = st2.nextToken();
        }

        if (subLevel.contains(HOVerwaltung.instance().getLanguageString("veryhigh"))) {
            level = level + "++";
        }
        else if (subLevel.contains(HOVerwaltung.instance().getLanguageString("high"))) {
            level = level + "+";
        }
        else if (subLevel.contains(HOVerwaltung.instance().getLanguageString("verylow"))) {
            level = level + "--";
        }
        else if (subLevel.contains(HOVerwaltung.instance().getLanguageString("low"))) {
            level = level + "-";
        }

        if (!showText) {
            level = "";
        }

        if (showNumber) {
        	if (value.contains("(") && value.contains(")")) {
	        	StringTokenizer st2 = new StringTokenizer(st.nextToken(), ")");
	
	            final String number = st2.nextToken();
	
	            if (level.length() > 0) {
	                level = level + " (" + number + ")";
	            }
	            else {
	                level = number;
	            }
        	} else {
        		level = level + " (0)";
        	}
        }

        return level;
    }

    /**
     * Utility Method that returns a double representation of the rating
     *
     * @param desc String representation of the rating
     * @param isNumeric Indicator if the string includes the numerical value
     * @param isDescription Indicator if the string includes the description
     * @param skills List of skills
     *
     * @return ouble with raing
     */
    public static double getRating(String desc, boolean isNumeric,
        boolean isDescription, List<String> skills) {
        if (isNumeric && !isDescription) {
            return Double.parseDouble(desc);
        }

        if (isNumeric) {
            final StringTokenizer st = new StringTokenizer(desc, "(");

            st.nextToken();

            String s = st.nextToken();

            s = s.substring(0, s.length() - 1);

            return Double.parseDouble(s);
        }
        
        desc.trim();
     
        double extra = 0.5;

        String valueStr = desc;
              	
        if (valueStr.contains("(")) {
        	// We have things like divine(+1)-
        	// Add the 1 to extra, and remove this part.
        	String add = valueStr.substring(valueStr.indexOf("(") + 2 , valueStr.indexOf(")"));
        	extra += Integer.parseInt(add);
        	valueStr = valueStr.substring(0,  valueStr.indexOf("(")) + valueStr.substring(valueStr.indexOf(")") + 1);
        }
        
        
        if (valueStr.contains("++")) {
            extra += 0.7;
            valueStr = valueStr.substring(0, valueStr.indexOf("++"));
        }
        else if (valueStr.contains("+")) {
            extra += 0.6;
            valueStr = valueStr.substring(0, valueStr.indexOf("+"));
        }
        else if (valueStr.contains("--")) {
            extra += 0.3;
            valueStr = valueStr.substring(0, valueStr.indexOf("--"));
        }
        else if (valueStr.contains("-")) {
            extra += 0.4;
            valueStr = valueStr.substring(0, valueStr.indexOf("-"));
        }
        else {
            // Unable to determine value
            return 0.0;
        }
        
        final int value = skills.indexOf(valueStr);

        return extra + value;
    }
    public static int getIntValue4Rating(double rating) {
        return (int) (((rating - 1) * 4d) + 1);
    }
    public static double getDoubleValue4Rating(int rating) {
        return (int) (((double) (rating - 1) / 4d) + 1);
    }

    public static int getSubLevel(int rating) {
        return (rating - 1) % 4;
    }
}
