// %3414899912:hoplugins.teamAnalyzer.vo%
package module.teamAnalyzer.vo;

import core.model.match.MatchType;
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

    // average, adjusted, opponent team name
    private String name;
    // adjusted values
    private Integer adjustedTacticCode;
    private Integer adjustedTacticLevel;

    private MatchDetail matchDetail;

    /**
     * Rating of the team on the field
     */
    private MatchRating rating;

    /**
     * Array of the 11 SpotLineup object representing the single spot.
     * Changed to a HashMap with roleID (from HO) as key...
     */

    private HashMap<Integer, SpotLineup> spotLineups = new HashMap<Integer, SpotLineup>();
    //private SpotLineup[] spotLineups = new SpotLineup[11];

    /**
     * Number of stars
     */
    private double stars;

    private SpecialEventsPredictionManager specialEventsPrediction;

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns the SpotLineup for the spot
     *
     * @param spot desired spot
     * @return a spot lineup
     */
    public final SpotLineup getSpotLineup(int spot) {
        return spotLineups.get(spot);
    }

    public HashMap<Integer, SpotLineup> getSpotLineups() {
        return spotLineups;
    }

    public void setSpotLineups(HashMap<Integer, SpotLineup> in) {
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
     * @param spot   spot to be filled with the object
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

    public MatchDetail getMatchDetail() {
        return matchDetail;
    }

    public void setMatchDetail(MatchDetail matchDetail) {
        this.matchDetail = matchDetail;
    }

    public String getName() {
        if (this.matchDetail != null) {
            Match match = this.matchDetail.getMatchDetail();
            if (match.isHome()) {
                return match.getAwayTeam();
            }
            return "* " + match.getHomeTeam();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MatchType getMatchType() {
        if (this.matchDetail != null) {
            return this.matchDetail.getMatchDetail().getMatchType();
        }
        return MatchType.NONE;
    }

    public String getResult() {
        if (this.matchDetail != null) {
            Match match = this.matchDetail.getMatchDetail();
            if (match.isHome()) {
                return match.getHomeGoals() + "-" + match.getAwayGoals();
            }
            return match.getAwayGoals() + "-" + match.getHomeGoals();
        }
        return "---";
    }

    public int getWeek() {
        if (this.matchDetail != null) {
            return this.matchDetail.getMatchDetail().getWeek();
        }
        return -1;
    }

    public int getSeason() {
        if (this.matchDetail != null) {
            return this.matchDetail.getMatchDetail().getSeason();
        }
        return -1;
    }

    public int getTacticCode() {
        if ( this.adjustedTacticCode != null){
            return this.adjustedTacticCode;
        }
        if (this.matchDetail != null) {
            return this.matchDetail.getTacticCode();
        }
        return -1;
    }

    public int getTacticLevel() {
        if ( this.adjustedTacticLevel != null){
            return this.adjustedTacticLevel;
        }
        if (this.matchDetail != null) {
            return this.matchDetail.getTacticLevel();
        }
        return -1;
    }

    public String getFormation() {
        if (this.matchDetail != null) {
            return this.matchDetail.getFormation();
        }
        return "---";
    }

    public boolean isHomeMatch() {
        if (this.matchDetail != null) {
            return this.matchDetail.getMatchDetail().isHome();
        }
        return false;
    }

    public void setAdjustedTacticCode(Integer adjustedTacticCode) {
        this.adjustedTacticCode = adjustedTacticCode;
    }

    public void setAdjustedTacticLevel(Integer adjustedTacticLevel) {
        this.adjustedTacticLevel = adjustedTacticLevel;
    }
}
