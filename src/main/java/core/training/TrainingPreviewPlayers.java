package core.training;

import core.db.DBManager;
import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchLineupTeam;
import core.model.match.MatchStatistics;
import core.model.match.MatchType;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.util.HOLogger;
import module.lineup.LineupPosition;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Training preview of players for the week
 *
 * @author yaute
 */


public class TrainingPreviewPlayers implements Refreshable {

    private static TrainingPreviewPlayers m_clInstance;

    private HashMap<Player, TrainingPreviewPlayer> players = new HashMap<Player, TrainingPreviewPlayer>();
    private int nextWeekTraining = -1;
    private boolean isFuturMatchInit =false;
    private WeeklyTrainingType weekTrainTyp = null;
    private List<MatchStatistics> lMatchStats = null;
    private List<LineupPosition> lLinueupPos = null;

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
     * @param player
     * @return
     */
    public TrainingPreviewPlayer getTrainPreviewPlayer(Player player) {

        if (players.get(player) == null) {
            calculateWeeklyTrainingForPlayer(player);
        }
        return players.get(player);
    }

    /**
     * reinit object and clean database
     */
    public void reInit() {
        refresh();
        DBManager.instance().removeMatchOrder();
    }

    /**
     * refresh object
     */
    public void refresh() {
        if (players != null)
            players.clear();
        if (lMatchStats != null)
            lMatchStats.clear();
        if (lLinueupPos != null)
            lLinueupPos.clear();
        nextWeekTraining = -1;
        weekTrainTyp = null;
        isFuturMatchInit = false;
    }

    /**
     * get next training
     *
     * @return:     training id
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
            nextWeekTraining = DBManager.instance().getFuturTraining(nextWeekSaison, nextWeekWeek);
        }

        return nextWeekTraining;
    }

    /**
     * calculate training preview of a player
     *
     * @param player:   player
     */
    private void calculateWeeklyTrainingForPlayer(Player player) {

        final int playerID = player.getSpielerID();
        int fullTrain = 0;
        int partialTrain = 0;
        boolean fullFuturTrain = false;
        boolean partialFuturTrain = false;
        int iStamina = 0;
        boolean bEstimedStamina = false;

        getMatchesForTraining();

        //for (int i = 0; i < lMatchStats.size(); i++) {
        for ( var ms : lMatchStats){

            if (weekTrainTyp.getPrimaryTrainingSkillPositions() != null) {
                fullTrain += ms.getTrainMinutesPlayedInPositions(playerID, weekTrainTyp.getPrimaryTrainingSkillPositions());
                if (fullTrain > 90)
                    fullTrain = 90;
            }
            if (weekTrainTyp.getPrimaryTrainingSkillSecondaryTrainingPositions() != null) {
                partialTrain += ms.getTrainMinutesPlayedInPositions(playerID, weekTrainTyp.getPrimaryTrainingSkillSecondaryTrainingPositions());
                if (partialTrain > 90)
                    partialTrain = 90;
            }
            // If player receive training, don't display stamina icon
            if (fullTrain == 0 && partialTrain == 0) {
                iStamina += ms.getStaminaMinutesPlayedInPositions(playerID);
                if (iStamina > 90)
                    iStamina = 90;
            }
        }

        //for (int i = 0; i < lLinueupPos.size(); i++) {
        for ( var pos: lLinueupPos ){
            MatchRoleID roleId = pos.getPositionBySpielerId(playerID);

            if (roleId != null) {
                if (weekTrainTyp.getPrimaryTrainingSkillPositions() != null) {
                    for (int k = 0; k < weekTrainTyp.getPrimaryTrainingSkillPositions().length; k++) {
                        if (roleId.getId() == weekTrainTyp.getPrimaryTrainingSkillPositions()[k]) {
                            fullFuturTrain = true;
                            break;
                        }
                    }
                }
                if (!fullFuturTrain && weekTrainTyp.getPrimaryTrainingSkillSecondaryTrainingPositions() != null) {
                    for (int k = 0; k < weekTrainTyp.getPrimaryTrainingSkillSecondaryTrainingPositions().length; k++) {
                        if (roleId.getId() == weekTrainTyp.getPrimaryTrainingSkillSecondaryTrainingPositions()[k]) {
                            partialFuturTrain = true;
                            break;
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
     * get the matchs concerning by the training week
     */
    private void getMatchesForTraining() {

        if (!isFuturMatchInit) {
            var lastTraining = TrainingManager.instance().getLastTrainingWeek();


            //final int lastHrfId = DBManager.instance().getLatestHrfId();
            //List<MatchObj> matches = new ArrayList<>();
            lMatchStats = new ArrayList<>();
            lLinueupPos = new ArrayList<>();
            isFuturMatchInit = true;


            if (lastTraining != null) {
                /*
                Timestamp previousTrainingDate = DBManager.instance()
                        .getXtraDaten(lastHrfId)
                        .getTrainingDate();

                int nextWeekTrain = getNextWeekTraining();

                if ((nextWeekTrain > -1) && (previousTrainingDate != null)){
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(previousTrainingDate);
                    weekTrainTyp = WeeklyTrainingType.instance(nextWeekTrain);
                    int myID = HOVerwaltung.instance().getModel().getBasics().getTeamId();

                    try {
                        final ResultSet matchRS = DBManager.instance().getAdapter().executeQuery(createQuery(cal));

                        if (matchRS == null) {
                            return;
                        }

                        while (matchRS.next()) {

                            matches.add(new MatchObj(matchRS.getInt("MATCHID"),
                                    matchRS.getInt("MATCHTYP"),
                                    matchRS.getInt("STATUS")));
                        }

                        matchRS.close();
                    } catch (Exception e1) {
                        HOLogger.instance().log(getClass(), e1);
                    }
                    for (int i = 0; i < matches.size(); i++) {

                        final MatchObj matchInfo = (matches.get(i));
*/


                weekTrainTyp = WeeklyTrainingType.instance(lastTraining.getTrainingType());
                for (var matchInfo : lastTraining.getMatches()) {
                    if (matchInfo.getMatchStatus() == MatchKurzInfo.FINISHED) {
                        //Get the MatchLineup by id
                        //MatchLineupTeam mlt = DBManager.instance().getMatchLineupTeam(matchInfo.getMatchID(), MatchKurzInfo.user_team_id);
                        var mlt = matchInfo.getMatchdetails().getTeamLineup();
                        lMatchStats.add(new MatchStatistics(matchInfo, mlt));
                    } else if (matchInfo.getMatchStatus() == MatchKurzInfo.UPCOMING) {
                        LineupPosition lineuppos = DBManager.instance().getMatchOrder(matchInfo.getMatchID(), matchInfo.getMatchTyp());
                        if (lineuppos != null)
                            lLinueupPos.add(lineuppos);
                    }
                }
            }
        }
    }
    
    /**
     * create request for getting matchs
     *
     * @param calendar:     training week date
     * @return
     */
    private String createQuery(Calendar calendar) {
        final Timestamp ts = new Timestamp(calendar.getTimeInMillis());
        final Calendar old = (Calendar) calendar.clone();

        // set time one week back
        old.add(Calendar.WEEK_OF_YEAR, -1);

        final Timestamp ots = new Timestamp(old.getTimeInMillis());
        final int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
        final String sdbquery = "SELECT MATCHID,MATCHTYP,STATUS FROM MATCHESKURZINFO WHERE " + "( HEIMID=" + teamId
                + " OR GASTID=" + teamId + " ) " + "AND MatchDate BETWEEN '"
                + ots.toString() + "' AND '" + ts.toString() + "' "
                + " AND (MatchTyp=" + MatchType.QUALIFICATION.getId()
                + " OR MatchTyp=" + MatchType.LEAGUE.getId()
                + " OR MatchTyp=" + MatchType.CUP.getId()
                + " OR MatchTyp=" + MatchType.FRIENDLYNORMAL.getId()
                + " OR MatchTyp=" + MatchType.FRIENDLYCUPRULES.getId()
                + " OR MatchTyp=" + MatchType.INTFRIENDLYCUPRULES.getId()
                + " OR MatchTyp=" + MatchType.INTFRIENDLYNORMAL.getId()
                + " OR MatchTyp=" + MatchType.EMERALDCUP.getId()
                + " OR MatchTyp=" + MatchType.RUBYCUP.getId()
                + " OR MatchTyp=" + MatchType.SAPPHIRECUP.getId()
                + " OR MatchTyp=" + MatchType.CONSOLANTECUP.getId() + " )"
                + " AND (STATUS=" + MatchKurzInfo.FINISHED + " OR STATUS=" + MatchKurzInfo.UPCOMING + ")"
                + " ORDER BY MatchDate DESC";
        return sdbquery;
    }

    /**
     * match object object
     */
    private static class MatchObj {

        private int matchid;
        private int matchtyp;
        private int status;

        //~ Constructors -------------------------------------------------------------------------------

        /**
         * create MatchObj object
         *
         * @param matchid:  match id
         * @param matchtyp: match type
         * @param status:   status of match (finish/upcoming)
         */
        public MatchObj(int matchid, int matchtyp, int status) {
            this.matchid = matchid;
            this.matchtyp = matchtyp;
            this.status = status;
        }

        //~ Methods ------------------------------------------------------------------------------------

        /**
         * get match id
         *
         * @return  match id
         */
        public int getMatchid() {
            return matchid;
        }

        /**
         * get match type
         *
         * @return  match type
         */
        public int getMatchtyp() {
            return matchtyp;
        }

        /**
         * get match status
         *
         * @return  match status
         */
        public int getStatus() {
            return status;
        }
    }
}
