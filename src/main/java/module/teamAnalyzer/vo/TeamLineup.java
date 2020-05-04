// %3414899912:hoplugins.teamAnalyzer.vo%
package module.teamAnalyzer.vo;

import core.specialevents.SpecialEventsPredictionManager;

import java.util.Arrays;
import java.util.HashMap;


/**
 * Object that holds the Lineup for a certain formation, real or calculated
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class TeamLineup {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Rating of the team on the field */
    private MatchRating rating;

    /** Array of the 11 SpotLineup object representing the single spot.
     *  Changed to a HashMap with roleID (from HO) as key... */

    private HashMap<Integer, SpotLineup> spotLineups = new HashMap<Integer, SpotLineup>();
    //private SpotLineup[] spotLineups = new SpotLineup[11];

    /** Number of stars */
    private double stars;

    private SpecialEventsPredictionManager specialEventsPrediction;

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns the SpotLineup for the spot
     *
     * @param spot desired spot
     *
     * @return a spot lineup
     */
    public final SpotLineup getSpotLineup(int spot) {
        return spotLineups.get(spot);
    }

    public HashMap<Integer, SpotLineup> getSpotLineups(){
        return spotLineups;
    }

    public void setSpotLineups(HashMap<Integer, SpotLineup> in){
        spotLineups = in;
    }

    public void setRating(MatchRating rating) {
        this.rating = rating;
    }

    public MatchRating getRating() {
        return rating;
    }

    /**
     * Sets the spot place with the passes SpotLineup
     *
     * @param detail SpotLineup object
     * @param spot spot to be filled with the object
     */
    public void setSpotLineup(SpotLineup detail, int spot) {
        spotLineups.put(spot, detail);
    }

    public void setStars(double d) {
        stars = d;
    }

    public double getStars() {
        return stars;
    }

    /**
     * toString methode: creates a String representation of the object
     * Maybe not pretty after HashMap change
     *
     * @return the String representation
     */
    @SuppressWarnings("unchecked")
	@Override
	public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("TeamLineup[");

        if (spotLineups == null) {
            buffer.append("spotLineups = " + "null");
        } else {
            buffer.append("spotLineups = " + Arrays.asList(spotLineups).toString());
        }

        buffer.append("]");

        return buffer.toString();
    }

    public void setSpecialEventsPrediction(SpecialEventsPredictionManager specialEventsPredictionManager) {
        this.specialEventsPrediction = specialEventsPredictionManager;
    }
    public SpecialEventsPredictionManager getSpecialEventsPrediction() {
        return specialEventsPrediction;
    }

}
