// %2614020934:hoplugins.teamAnalyzer.report%
package module.teamanalyzer.report;

import core.playername.NameFormat;
import core.playername.PlayerNameFormatter;
import module.teamanalyzer.vo.PlayerAppearance;
import module.teamanalyzer.vo.PlayerPerformance;

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
    private final Map<Integer, PlayerAppearance> players = new HashMap<>();

    /** Map of PositionReport based on the effective position on the field */
    private Map<String,PositionReport> positionReports = new HashMap<>();

    private final PlayerNameFormatter playerNameFormatter;
    private final NameFormat nameFormat;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SpotReport object.
     *
     * @param pp PlayerPerformance
     */
    public SpotReport(PlayerPerformance pp, PlayerNameFormatter playerNameFormatter, NameFormat nameFormat) {
        super(pp);
        this.playerNameFormatter = playerNameFormatter;
        this.nameFormat = nameFormat;
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
        PlayerAppearance playerApperance = players.get(pp.getPlayerId());

        if (playerApperance == null) {
            playerApperance = new PlayerAppearance();
            playerApperance.setPlayerId(pp.getPlayerId());
            playerApperance.setName(playerNameFormatter.format(pp.getPlayerId(), nameFormat));

            playerApperance.setStatus(pp.getStatus());
            players.put(pp.getPlayerId(), playerApperance);
        }

        playerApperance.addApperarence();
    }

    /**
     * Update positionReport based on the new player performance
     *
     * @param pp
     */
    private void updatePositionDetails(PlayerPerformance pp) {
        PositionReport report = (PositionReport) positionReports.get("" + pp.getRoleId());

        if (report == null) {
            report = new PositionReport(pp);
            positionReports.put("" + pp.getRoleId(), report);
        }

        report.addPerformance(pp);
    }
}
