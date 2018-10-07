// %2614020934:hoplugins.teamAnalyzer.report%
package module.teamAnalyzer.report;

import module.teamAnalyzer.vo.PlayerAppearance;
import module.teamAnalyzer.vo.PlayerPerformance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Report of all players played in a certain spot on the field
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class SpotReport extends Report {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Map of players that played in this spot */
    private Map<String,PlayerAppearance> players = new HashMap<String,PlayerAppearance>();

    /** Map of PositionReport based on the effective position on the field */
    private Map<String,PositionReport> positionReports = new HashMap<String,PositionReport>();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SpotReport object.
     *
     * @param pp
     */
    public SpotReport(PlayerPerformance pp) {
        super(pp);
    }

    //~ Methods ------------------------------------------------------------------------------------
    public Collection<PlayerAppearance> getPlayerAppearance() {
        return players.values();
    }

    public Collection<PositionReport> getPositionReports() {
        return positionReports.values();
    }

    /**
     * Add a performance to the spot and updates the position based map
     *
     * @param pp
     */
    @Override
	public void addPerformance(PlayerPerformance pp) {
        super.addPerformance(pp);
        updatePositionDetails(pp);
        updatePlayerAppearance(pp);
    }

    /**
     * Update player appearance of 1
     *
     * @param pp
     */
    private void updatePlayerAppearance(PlayerPerformance pp) {
        String name = "" + pp.getSpielerName();

        name = name.replaceAll(" ", "");

        PlayerAppearance playerApperance = (PlayerAppearance) players.get(name);

        if (playerApperance == null) {
            playerApperance = new PlayerAppearance();
            playerApperance.setPlayerId(pp.getSpielerId());
            playerApperance.setName(pp.getSpielerName());

            playerApperance.setStatus(pp.getStatus());
            players.put(name, playerApperance);
        }

        playerApperance.addApperarence();
    }

    /**
     * Update positionReport based on the new player performance
     *
     * @param pp
     */
    private void updatePositionDetails(PlayerPerformance pp) {
        PositionReport report = (PositionReport) positionReports.get("" + pp.getPositionCode());

        if (report == null) {
            report = new PositionReport(pp);
            positionReports.put("" + pp.getPositionCode(), report);
        }

        report.addPerformance(pp);
    }
}
