// %3625019770:hoplugins.teamAnalyzer.report%
package module.teamanalyzer.report;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.util.PlayerNameFixupUtils;
import core.playername.*;
import core.prediction.engine.TeamData;
import core.specialevents.SpecialEventsPredictionManager;
import module.lineup.Lineup;
import module.teamanalyzer.SystemManager;
import module.teamanalyzer.manager.PlayerDataManager;
import module.teamanalyzer.manager.TeamLineupBuilder;
import module.teamanalyzer.vo.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;


/**
 * The main report containing all the data
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class TeamReport {

    private final int teamId;
    private TeamLineup adjustedRatingsLineup;
    private final TeamLineup averageRatingslineup;
    private final List<MatchDetail> matchDetails = new ArrayList<>();
    private final List<SquadInfo> squadInfo = new ArrayList<>();
    private SpecialEventsPredictionManager specialEventsPredictionManager;

    /**
     * handle match selection
     */
    private int selection=0;


    //~ Instance fields ----------------------------------------------------------------------------

    /** Map of SpotReport */
    private final Map<Integer,SpotReport> spotReports = new LinkedHashMap<>();

    /** Match Ratings */
    private final MatchRating rating  = new MatchRating();

    /** Average stars */
    private double averageStars = 0d;

    /** Number of matches considered */
    private int matchNumber = 0;

    private final NameFormat nameFormat;
    private final PlayerNameFormatter playerNameFormatter;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TeamReport object including all filtered matches of the team
     *
     * @param matchDetails list of matches of the team report
     */
    public TeamReport(int teamId, List<MatchDetail> matchDetails, NameFormat nameFormat) {
        this.teamId = teamId;
        this.nameFormat = nameFormat;

        // TODO: that is not correct here
        final var players = matchDetails.stream()
            .flatMap( md -> md.getPerformances().stream())
            .map(pp -> Pair.of(pp.getPlayerId(), PlayerNameFixupUtils.splitName(pp.getPlayerName())))
            .map(p -> new PlayerName() {
                @Override
                public int getPlayerId() {
                    return p.getKey();
                }

                @Override
                public String getFirstName() {
                    return p.getValue().firstName();
                }

                @Override
                public String getNickName() {
                    return p.getValue().nickName();
                }

                @Override
                public String getLastName() {
                    return p.getValue().lastName();
                }
            }).toList();
        playerNameFormatter = new PlayerNameFormatter(players);

        var squadInfos = loadSquadInfo(teamId);
        for (MatchDetail m:matchDetails ) {
            // find latest squad info in training week after match
            var squadInfo = squadInfos.stream().filter(i -> i.getFetchDate().isAfter(m.getMatch().getMatchDate()) &&
                    i.getFetchDate().isBefore(m.getMatch().getMatchDate().plusDaysAtSameLocalTime(7))).min(Comparator.comparing(SquadInfo::getFetchDate)).orElse(null);
            // don't use info in other matches
            if ( squadInfo != null ) squadInfos.remove(squadInfo);
            addMatch(m, squadInfo,  SystemManager.isShowUnavailable.isSet(), playerNameFormatter, nameFormat);
        }
        this.averageRatingslineup = new TeamLineupBuilder(this)
                .setName(TranslationFacility.tr("Durchschnitt")).build();

        if ( HOVerwaltung.instance().getModel().getBasics().isNationalTeam()){
            this.averageRatingslineup.setNtTeamDetails(DBManager.instance().loadNtTeamDetails(this.teamId, null));
        }
    }

    private List<SquadInfo> loadSquadInfo(int teamId) {
        return DBManager.instance().loadSquadInfo(teamId);
    }

    /**
     * TeamReport of one single match (used internally only)
     *
     * @param matchDetail The match of the report is stored in averageRatingslineup
     */
    private TeamReport(int teamId, MatchDetail matchDetail, SquadInfo squadInfo, NameFormat nameFormat) {
        this.teamId = teamId;
        this.nameFormat = nameFormat;

        // TODO: that is not correct here
        final var players = matchDetail.getPerformances().stream()
            .map(pp -> Pair.of(pp.getPlayerId(), PlayerNameFixupUtils.splitName(pp.getPlayerName())))
            .map(p -> new PlayerName() {
                @Override
                public int getPlayerId() {
                    return p.getKey();
                }

                @Override
                public String getFirstName() {
                    return p.getValue().firstName();
                }

                @Override
                public String getNickName() {
                    return p.getValue().nickName();
                }

                @Override
                public String getLastName() {
                    return p.getValue().lastName();
                }
            }).toList();
        playerNameFormatter = new PlayerNameFormatter(players);

        addMatch(matchDetail, squadInfo , SystemManager.isShowUnavailable.isSet(), playerNameFormatter, nameFormat);
        this.averageRatingslineup = new TeamLineupBuilder(this).setMatchDetail(matchDetail).setSquadInfo(squadInfo).build();
        if (HOVerwaltung.instance().getModel().getBasics().isNationalTeam()) {
            this.averageRatingslineup.setNtTeamDetails(DBManager.instance().loadNtTeamDetails(this.teamId, matchDetail.getMatch().getMatchDate().toDbTimestamp()));
        }
    }

    /**
     * Number of existing lineups in the team report
     *
     * @return number of matches plus the average lineup and adjusted lineup if it exists
     */
    public int size() {
        int ret = this.matchDetails.size() + 1;
        if (this.adjustedRatingsLineup != null) ret++;
        return ret;
    }

    /**
     * Get a lineup from the team report
     *
     * @param selection index of the required lineup
     * @return selected team lineup
     */
    public TeamLineup getTeamMatchReport(int selection)
    {
        if (this.matchDetails.isEmpty())return null;
        if ( selection == 0 ){
            return this.averageRatingslineup;
        }
        else {
            int offset = 1;
            if (this.adjustedRatingsLineup != null) {
                if (selection == 1) {
                    return this.adjustedRatingsLineup;
                }
                offset = 2;
            }
            int matchNumber = selection - offset;
            // create a team report of one single match
            TeamReport selectedMatchReport = new TeamReport(this.teamId, matchDetails.get(matchNumber), squadInfo.get(matchNumber), nameFormat);
            return selectedMatchReport.getTeamMatchReport(0);
        }
    }

    /**
     * Set adjusted Lineup
     *
     * @param newRatings the team data copied to the new adjusted lineup
     */
    public void adjustRatingsLineup(TeamData newRatings) {
        if (adjustedRatingsLineup != null) {
            adjustedRatingsLineup.setTeamData(newRatings);
        } else if (selection == 0) {
            adjustedRatingsLineup = new TeamLineupBuilder(this)
                    .setTeamData(newRatings)
                    .build();
        } else {
            MatchDetail matchDetail = getTeamMatchReport(selection).getMatchDetail();
            adjustedRatingsLineup = new TeamLineupBuilder(new TeamReport(this.teamId, matchDetail, null, nameFormat))
                    .setTeamData(newRatings)
                    .setMatchType(matchDetail.getMatch().getMatchType())
                    .setName(TranslationFacility.tr("ls.teamanalyzer.Adjusted"))
                    .build();
        }
        selectLineup(1); // select the adjusted lineup
    }

    //~ Methods ------------------------------------------------------------------------------------
    public MatchRating getRating() {
        return rating;
    }

    public SpecialEventsPredictionManager getSpecialEventsPredictionManager() {
        return specialEventsPredictionManager;
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
    public void addMatch(MatchDetail matchDetail, SquadInfo squad, boolean showUnavailable, PlayerNameFormatter playerNameFormatter, NameFormat nameFormat) {
        this.matchDetails.add(matchDetail);
        this.squadInfo.add(squad);

        for (PlayerPerformance playerPerformance : matchDetail.getPerformances()) {
            addPerformance(playerPerformance, showUnavailable, playerNameFormatter, nameFormat);
        }

        addRating(matchDetail.getRating());
        addStars(matchDetail.getStars());
        addSpecialEvents(matchDetail);
        matchNumber++;
    }

    private void addSpecialEvents(MatchDetail matchDetail)
    {
        Lineup lineup = HOVerwaltung.instance().getModel().getCurrentLineup();

        if ( this.specialEventsPredictionManager == null){
            this.specialEventsPredictionManager = new SpecialEventsPredictionManager();
        }
        this.specialEventsPredictionManager.analyzeLineup(lineup, matchDetail);
    }

    /**
     * Add a performance to the correct SpotReport
     */
    private void addPerformance(PlayerPerformance pp, boolean showUnavailable, PlayerNameFormatter playerNameFormatter, NameFormat nameFormat) {
        if ((!showUnavailable) && (pp.getStatus() != PlayerDataManager.AVAILABLE)) {
            return;
        }

        SpotReport spotReport = getSpotReport(pp.getRoleId());

        if (spotReport == null) {
            spotReport = new SpotReport(pp, playerNameFormatter, nameFormat);
            spotReports.put(pp.getRoleId(), spotReport);
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
     * @return the new average number
     */
    private double updateAverage(double oldValue, double newValue) {
        return ((oldValue * matchNumber) + newValue) / (matchNumber + 1);
    }

    public TeamLineup selectLineup(int i) {
        this.selection=i;
        return getTeamMatchReport(i);
    }

    public TeamLineup getSelectedLineup() {
        return getTeamMatchReport(this.selection);
    }

    public int getSelection(){
        return this.selection;
    }

    public void  setSelection(int selection){
        this.selection = selection;
    }

    public boolean isEmpty() {
        return this.matchDetails.isEmpty();
    }
}
