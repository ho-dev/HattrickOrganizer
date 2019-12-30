// %3625019770:hoplugins.teamAnalyzer.report%
package module.teamAnalyzer.report;

import core.model.HOVerwaltung;
import core.specialevents.SpecialEventsPredictionManager;
import module.lineup.Lineup;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.vo.MatchDetail;
import module.teamAnalyzer.vo.MatchRating;
import module.teamAnalyzer.vo.PlayerPerformance;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * The main report containing all the data
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class TeamReport {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Map of SpotReport */
    private Map<Integer,SpotReport> spotReports;

    /** Match Ratings */
    private MatchRating rating;

    /** Average stars */
    private double averageStars;

    /** Number of matches considered */
    private int matchNumber;
    private SpecialEventsPredictionManager m_cSpecialEventsPredictionManager;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TeamReport object.
     */
    public TeamReport() {
        spotReports = new LinkedHashMap<Integer,SpotReport>();
        rating = new MatchRating();
        matchNumber = 0;
        averageStars = 0d;
    }

    //~ Methods ------------------------------------------------------------------------------------
    public MatchRating getRating() {
        return rating;
    }

    /**
     * Returns the spot report for the specified spot field
     *
     * @param spot the spot number we want
     *
     * @return SpotReport
     */
    public SpotReport getSpotReport(int spot) {
        return spotReports.get(spot);
    }

    public double getStars() {
        return averageStars;
    }

    /**
     * Add a match to the report
     *
     * @param matchDetail Match to be analyzed
     * @param showUnavailable consider also unavailable or not
     */
    public void addMatch(MatchDetail matchDetail, boolean showUnavailable) {
        for (Iterator<PlayerPerformance> iter = matchDetail.getPerformances().iterator(); iter.hasNext();) {
            addPerformance( iter.next(), showUnavailable);
        }

        addRating(matchDetail.getRating());
        addStars(matchDetail.getStars());
        addSpecialEvents(matchDetail);
        matchNumber++;
    }

    private void addSpecialEvents(MatchDetail matchDetail)
    {
        Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();

        if ( this.m_cSpecialEventsPredictionManager == null){
            this.m_cSpecialEventsPredictionManager = new SpecialEventsPredictionManager();
        }
        this.m_cSpecialEventsPredictionManager.analyzeLineup(lineup, matchDetail);
    }

    /**
     * Add a performance to the correct SpotReport
     *
     * @param pp
     * @param showUnavailable
     */
    private void addPerformance(PlayerPerformance pp, boolean showUnavailable) {
        if ((!showUnavailable) && (pp.getStatus() != PlayerDataManager.AVAILABLE)) {
            return;
        }

        SpotReport spotReport = getSpotReport(pp.getId());

        if (spotReport == null) {
            spotReport = new SpotReport(pp);
            spotReports.put(pp.getId(), spotReport);
        }

        spotReport.addPerformance(pp);
    }

    /**
     * Updated the ratings
     *
     * @param aRating new match ratings
     */
    private void addRating(MatchRating aRating) {
        rating.setMidfield(updateAverage(rating.getMidfield(), aRating.getMidfield()));
        rating.setLeftDefense(updateAverage(rating.getLeftDefense(), aRating.getLeftDefense()));
        rating.setCentralDefense(updateAverage(rating.getCentralDefense(),
                                               aRating.getCentralDefense()));
        rating.setRightDefense(updateAverage(rating.getRightDefense(), aRating.getRightDefense()));
        rating.setLeftAttack(updateAverage(rating.getLeftAttack(), aRating.getLeftAttack()));
        rating.setCentralAttack(updateAverage(rating.getCentralAttack(), aRating.getCentralAttack()));
        rating.setRightAttack(updateAverage(rating.getRightAttack(), aRating.getRightAttack()));
        rating.setHatStats(updateAverage(rating.getHatStats(), aRating.getHatStats()));
        rating.setLoddarStats(updateAverage(rating.getLoddarStats(), aRating.getLoddarStats()));
    }

    /**
     * Updates the average stars
     *
     * @param stars new game stars
     */
    private void addStars(double stars) {
        averageStars = updateAverage(averageStars, stars);
    }

    /**
     * Generic calculate average method
     *
     * @param oldValue
     * @param newValue
     *
     * @return the new average number
     */
    private double updateAverage(double oldValue, double newValue) {
        double rat = ((oldValue * matchNumber) + newValue) / (matchNumber + 1);

        return rat;
    }
}
