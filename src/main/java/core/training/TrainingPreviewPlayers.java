package core.training;

import core.db.DBManager;
import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchStatistics;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.util.HODateTime;
import module.lineup.Lineup;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Training preview of players for the week
 */


public class TrainingPreviewPlayers implements Refreshable {

    private static TrainingPreviewPlayers m_clInstance;

    private final HashMap<Player, TrainingPreviewPlayer> players = new HashMap<>();
    private int nextWeekTraining = -1;
    private boolean isFutureMatchInit =false;
    private WeeklyTrainingType weekTrainTyp = null;
    private List<MatchStatistics> lMatchStats = null;
    private List<Lineup> lineups = null;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Create TrainingPreviewPlayers object
     * Add to refresh
     */
    public TrainingPreviewPlayers() {
        RefreshManager.instance().registerRefreshable(this);
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns a singleton TrainingPreviewPlayers object
     *
     * @return instance of TrainingPreviewPlayers
     */
    public static TrainingPreviewPlayers instance() {
        if (m_clInstance == null) {
            m_clInstance = new TrainingPreviewPlayers();
        }
        return m_clInstance;
    }

    /**
     * get training preview of a player
     *
     * @param player Player
     * @return TrainingPreviewPlayer
     */
    public TrainingPreviewPlayer getTrainPreviewPlayer(@Nullable Player player) {
        if ( player != null) {
            if (players.get(player) == null) {
                calculateWeeklyTrainingForPlayer(player);
            }
            return players.get(player);
        }
        return null;
    }

    /**
     * reinit object and clean database
     */
    public void reInit() {
        refresh();
    }

    /**
     * refresh object
     */
    public void refresh() {
        players.clear();
        if (lMatchStats != null)
            lMatchStats.clear();
        if (lineups != null)
            lineups.clear();
        nextWeekTraining = -1;
        weekTrainTyp = null;
        isFutureMatchInit = false;
    }

    /**
     * get next training
     *
     * @return     training id
     */
    public int getNextWeekTraining() {
        if (nextWeekTraining == -1) {
            int nextWeekSaison = HOVerwaltung.instance().getModel().getBasics().getSeason();
            int nextWeekWeek = HOVerwaltung.instance().getModel().getBasics().getSpieltag();
            if (nextWeekWeek == 16) {
                nextWeekWeek = 1;
                nextWeekSaison++;
            } else {
                nextWeekWeek++;
            }
            var hattrickDate = HODateTime.fromHTWeek(new HODateTime.HTWeek(nextWeekSaison, nextWeekWeek));
            var nextTraining = DBManager.instance().getFuturTraining(HODateTime.toDbTimestamp(hattrickDate));
            if ( nextTraining != null) {
                nextWeekTraining = nextTraining.getTrainingType();
            }
        }
        return nextWeekTraining;
    }

    /**
     * calculate training preview of a player
     *
     * @param player:   player
     */
    private void calculateWeeklyTrainingForPlayer(Player player) {

        final int playerID = player.getPlayerId();
        int fullTrain = 0;
        int partialTrain = 0;
        boolean fullFuturTrain = false;
        boolean partialFuturTrain = false;
        int iStamina = 0;
        boolean bEstimedStamina = false;

        // create a copy of the list to prevent concurrent exception on refresh (#1430)
        var matchStatistics = new ArrayList<>(getMatchesForTraining());
        for ( var ms : matchStatistics){
            if ( weekTrainTyp != null ) {
                if (weekTrainTyp.getTrainingSkillPositions() != null) {
                    fullTrain += ms.getTrainMinutesPlayedInPositions(playerID, weekTrainTyp.getTrainingSkillPositions());
                    if (fullTrain > 90)
                        fullTrain = 90;
                }
                if (weekTrainTyp.getTrainingSkillPartlyTrainingPositions() != null) {
                    partialTrain += ms.getTrainMinutesPlayedInPositions(playerID, weekTrainTyp.getTrainingSkillPartlyTrainingPositions());
                    if (partialTrain > 90)
                        partialTrain = 90;
                }
            }
            // If player receive training, don't display stamina icon
            if (fullTrain == 0 && partialTrain == 0) {
                iStamina += ms.getStaminaMinutesPlayedInPositions(playerID);
                if (iStamina > 90)
                    iStamina = 90;
            }
        }

        for ( var lineup: lineups){
            var roleId = lineup.getPositionByPlayerId(playerID);
            if (roleId != null) {
                if ( weekTrainTyp != null) {
                    if (weekTrainTyp.getTrainingSkillPositions() != null) {
                        for (int k = 0; k < weekTrainTyp.getTrainingSkillPositions().length; k++) {
                            if (roleId.getId() == weekTrainTyp.getTrainingSkillPositions()[k]) {
                                fullFuturTrain = true;
                                break;
                            }
                        }
                    }
                    if (!fullFuturTrain && weekTrainTyp.getTrainingSkillPartlyTrainingPositions() != null) {
                        for (int k = 0; k < weekTrainTyp.getTrainingSkillPartlyTrainingPositions().length; k++) {
                            if (roleId.getId() == weekTrainTyp.getTrainingSkillPartlyTrainingPositions()[k]) {
                                partialFuturTrain = true;
                                break;
                            }
                        }
                    }
                }
                // If player receive training, don't display stamina icon
                if (fullTrain == 0 && partialTrain == 0 && !fullFuturTrain && !partialFuturTrain && 
                        roleId.getId() < IMatchRoleID.substGK1) {
                    bEstimedStamina = true;
                }
            }
        }

        players.put(player,new TrainingPreviewPlayer(fullTrain, partialTrain, 
                fullFuturTrain, partialFuturTrain,
                iStamina, bEstimedStamina));
    }

    /**
     * get the matches concerning by the training week
     */
    private List<MatchStatistics> getMatchesForTraining() {

        if (!isFutureMatchInit) {
            var lastTraining = TrainingManager.instance().getNextWeekTraining();

            this.lMatchStats = new ArrayList<>();
            this.lineups = new ArrayList<>();
            this.isFutureMatchInit = true;

            if (lastTraining != null) {
                weekTrainTyp = WeeklyTrainingType.instance(lastTraining.getTrainingType());
                for (var matchInfo : lastTraining.getMatches()) {
                    if (matchInfo.getMatchStatus() == MatchKurzInfo.FINISHED) {
                        var mlt = matchInfo.getMatchdetails().getOwnTeamLineup();
                        if ( mlt != null ) {
                            lMatchStats.add(new MatchStatistics(matchInfo, mlt));
                        }
                    } else if (matchInfo.getMatchStatus() == MatchKurzInfo.UPCOMING) {
                        var teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
                        var team = DBManager.instance().loadMatchLineupTeam(matchInfo.getMatchType().getId(), matchInfo.getMatchID(), teamId);
                        if (team != null)
                            lineups.add(team.getLineup());
                    }
                }
            }
        }
        return lMatchStats;
    }
}
