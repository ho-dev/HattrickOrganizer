// %3324148068:hoplugins.teamAnalyzer.report%
package module.teamAnalyzer.report;

import module.teamAnalyzer.vo.PlayerPerformance;


/**
 * A Report class used for collect data on a specific spot on the field
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class Report {
    //~ Instance fields ----------------------------------------------------------------------------

    /** The rating achieved */
    private double rating;

    /** Number of appearance */
    private int appearance;

    /** The player id */
    private int playerId;

    /** The position who has the player that played in the spot, defender or extra midfielder */
    private int position;

    /** The spot place on the lineup */
    private int spot;
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new Report object.
     *
     * @param pp PlayerPerformance for which the report has to be built
     */
    public Report(PlayerPerformance pp) {
        this.spot = pp.getRoleId();
        this.position = pp.getRoleId();
        this.playerId = pp.getSpielerId();
    }

    /**
     * Creates a new Report object.
     */
    public Report() {
    }

    //~ Methods ------------------------------------------------------------------------------------
    public void setAppearance(int i) {
        appearance = i;
    }

    public int getAppearance() {
        return appearance;
    }

    public void setPlayerId(int i) {
        playerId = i;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPosition(int i) {
        position = i;
    }

    public int getPosition() {
        return position;
    }

    public void setRating(double d) {
        rating = d;
    }

    public double getRating() {
        return rating;
    }

    public void setSpot(int i) {
        spot = i;
    }

    public int getSpot() {
        return spot;
    }

    /**
     * Add another performance to the Report, updating appearance and average rating the rest has
     * to be updated in child classes
     *
     * @param pp
     */
    public void addPerformance(PlayerPerformance pp) {
        appearance++;
        rating = ((rating * (appearance - 1)) + pp.getRating()) / appearance;
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
	public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("numberAppearance = " + appearance);
        buffer.append(", averageRating = " + rating);
        buffer.append(", spot = " + spot);
        buffer.append(", position = " + position);

        return buffer.toString();
    }
}
