// %1534136644:hoplugins.teamAnalyzer.vo%
package module.teamAnalyzer.vo;

import java.util.ArrayList;
import java.util.List;


/**
 * Match Detail containing data about the players that played in that game
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class MatchDetail {
    //~ Instance fields ----------------------------------------------------------------------------

    /** ArrayList of Player performance for this game */
    private List<PlayerPerformance> playerPerf = new ArrayList<>();

    private int setPiecesTaker = -1;

    /** Match to which the details are reffered */
    private Match match;

    /** Rating on the 7 areas of the pitch */
    private MatchRating rating = new MatchRating();

    /** Starting Lineup */
    private String formation;

    /** Total number of stars */
    private double stars;

    /** Tactic used in the game */
    private int tacticCode;

    /** Tactic LEvel achieved in the game */
    private int tacticLevel;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new MatchDetail object.
     *
     * @param aMatch match for which the details are
     */
    public MatchDetail(Match aMatch) {
        match = aMatch;
    }

    //~ Methods ------------------------------------------------------------------------------------
    public final List<PlayerPerformance> getPerformances() {
        return playerPerf;
    }

    public void setFormation(String i) {
        formation = i;
    }

    public String getFormation() {
        return formation;
    }

    public Match getMatch() {
        return match;
    }

    public void setRating(MatchRating rating) {
        this.rating = rating;
    }

    public MatchRating getRating() {
        return rating;
    }

    public void setStars(double i) {
        stars = i;
    }

    public double getStars() {
        return stars;
    }

    public void setTacticCode(int i) {
        tacticCode = i;
    }

    public int getTacticCode() {
        return tacticCode;
    }

    public void setTacticLevel(int i) {
        tacticLevel = i;
    }

    public int getTacticLevel() {
        return tacticLevel;
    }

    /**
     * Add a player performance to the List
     *
     * @param pp PlayerPerformance of a player in the game
     */
    public void addMatchLineupPlayer(PlayerPerformance pp) {
        playerPerf.add(pp);
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
	public String toString() {

        return "MatchDetail[" +
                "playerPerf = " + playerPerf +
                ", match = " + match +
                ", rating = " + rating +
                "]";
    }

    public double getRatingIndirectSetPiecesDef() {
        return this.rating.getIndirectSetPiecesDef();
    }

    public double getRatingIndirectSetPiecesAtt() {
        return this.rating.getIndirectSetPiecesAtt();
    }

    public int getSetPiecesTaker() {
        return setPiecesTaker;
    }

    public void setSetPiecesTaker(int setPiecesTaker) {
        this.setPiecesTaker = setPiecesTaker;
    }
}
