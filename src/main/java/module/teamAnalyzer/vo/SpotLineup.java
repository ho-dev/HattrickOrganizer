// %1647522239:hoplugins.teamAnalyzer.vo%
package module.teamAnalyzer.vo;

import module.teamAnalyzer.report.Report;
import module.teamAnalyzer.report.SpotReport;
import module.teamAnalyzer.report.TacticReport;

import java.util.List;


/**
 * Class that holds the real or calculated info for a position on the field (spot)
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class SpotLineup extends Report {
    //~ Instance fields ----------------------------------------------------------------------------

    /** List of tactics used by all who played in this spot */
    private List<TacticReport> tactics;

    /** Name of the Player */
    private String name;

    /** Status of the player */
    private int status;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SpotLineup object.
     */
    public SpotLineup() {
    }

    /**
     * Creates a new SpotLineup object.
     *
     * @param posReport the Spot Report from what the object must be created!!!
     */
    public SpotLineup(SpotReport posReport) {
        setRating(posReport.getRating());
        setAppearance(posReport.getAppearance());
    }

    //~ Methods ------------------------------------------------------------------------------------
    public void setName(String string) {
        name = string;
    }

    public String getName() {
        return name;
    }

    public void setStatus(int i) {
        status = i;
    }

    public int getStatus() {
        return status;
    }

    public void setTactics(List<TacticReport> list) {
        tactics = list;
    }

    public List<TacticReport> getTactics() {
        return tactics;
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
	public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("PositionLineup[");
        buffer.append("tactics = " + tactics);
        buffer.append(", name = " + name);
        buffer.append("]");

        return buffer.toString();
    }
}
