// %2128164761:hoplugins.teamAnalyzer.report%
package module.teamAnalyzer.report;

import module.teamAnalyzer.vo.PlayerPerformance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Report of all players played in a certain position on a spot on the field
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class PositionReport extends Report {
    //~ Instance fields ----------------------------------------------------------------------------

    /** List of tactics used offensive, defensive etc */
    private Map<String,TacticReport> tacticReports = new HashMap<String,TacticReport>();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new PositionReport object.
     *
     * @param pp PlayerPerformance
     */
    public PositionReport(PlayerPerformance pp) {
        super(pp);
    }

    //~ Methods ------------------------------------------------------------------------------------
    public Collection<TacticReport> getTacticReports() {
        return tacticReports.values();
    }

    /**
     * Add a performance to the report, and updated the tactic list
     *
     * @param pp
     */
    @Override
	public void addPerformance(PlayerPerformance pp) {
        super.addPerformance(pp);
        updateTacticDetails(pp);
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
	public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("PositionReport[");
        buffer.append(super.toString());
        buffer.append("]");

        return buffer.toString();
    }

    /**
     * Update the tactic detail list with the new performance Gets the tactic report for the tactic
     * position (offensive mid, def midfielder etc), and add the new performance
     *
     * @param pp
     */
    private void updateTacticDetails(PlayerPerformance pp) {
        TacticReport report = (TacticReport) tacticReports.get("" + pp.getPosition());

        if (report == null) {
            report = new TacticReport(pp);
            tacticReports.put("" + pp.getPosition(), report);
        }

        report.addPerformance(pp);
    }
}
