package core.specialevents;

import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.match.MatchType;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.util.HOLogger;
import module.lineup.Lineup;
import module.opponentspy.OppPlayerSkillEstimator;
import module.opponentspy.OpponentPlayer;
import module.opponentspy.OpponentTeam.PlayedPosition;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.vo.MatchDetail;
import module.teamAnalyzer.vo.PlayerInfo;
import module.teamAnalyzer.vo.PlayerPerformance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import static java.lang.Math.min;

public class SpecialEventsPredictionManager {

    static private OppPlayerSkillEstimator oppPlayerSkillEstimator = new OppPlayerSkillEstimator();
    static ArrayList<ISpecialEventPredictionAnalyzer> analyzers;
    private Lineup lineup = null;
    private Lineup opponentLineup = null;
    private HashMap<Integer, Player> playerInLineup = new HashMap<Integer, Player>();
    private HashMap<Integer, Player> opponentPlayerInLineup = new HashMap<Integer, Player>();

    public class Analyse {
        private List<SpecialEventsPrediction> specialEventsPredictions;
        private Lineup lineup = null;
        private Lineup opponentLineup = null;
        private HashMap<Integer, Player> playerInLineup;
        private HashMap<Integer, Player> opponentPlayerInLineup;
        private Double opponentRatingIndirectSetPiecesDef;
        private Double ratingIndirectSetPiecesAtt;

        public Analyse(Lineup lineup, Lineup oppLineup, HashMap<Integer, Player> player, HashMap<Integer, Player> oppPlayer) {
            this.lineup = lineup;
            opponentLineup = oppLineup;
            playerInLineup = player;
            opponentPlayerInLineup = oppPlayer;
        }

        public void analyzeLineup() {
            this.specialEventsPredictions = new ArrayList<>();
            for (ISpecialEventPredictionAnalyzer analyzer : analyzers) {
                for (IMatchRoleID position : lineup.getFieldPositions()) {
                    MatchRoleID mid = (MatchRoleID) position;
                    if (mid.getSpielerId() == 0) continue;
                    analyzer.analyzePosition(this, mid);
                }
            }
        }

        public void addSpecialEventPrediction(SpecialEventsPrediction se){
            this.specialEventsPredictions.add(se);
        }

        public Lineup getLineup() {
            return lineup;
        }

        public Lineup getOpponentLineup() {
            return opponentLineup;
        }

        public Player getPlayer(int playerId) {
            return this.playerInLineup.get(playerId);
        }

        public Player getPlayerByPosition(int pos) {
            return getPlayer(this.lineup.getPositionById(pos).getSpielerId());
        }

        public MatchRoleID getPosition(int pos) {
            return this.lineup.getPositionById(pos);
        }

        public Player getOpponentPlayer(int id) {
            return this.opponentPlayerInLineup.get(id);
        }

        public List<SpecialEventsPrediction> getEvents() {
            return this.specialEventsPredictions;
        }

        public Player getOpponentPlayerByPosition(int pos) {
            return getOpponentPlayer(this.opponentLineup.getPositionById(pos).getSpielerId());
        }

        public MatchRoleID getOpponentPosition(int pos) {
            return this.opponentLineup.getPositionById(pos);
        }

        public double getGoalProbability(IMatchRoleID scorer) {
            return getGoalProbability(scorer, 0);
        }

        public double getGoalProbability(IMatchRoleID scorer, double skillbonus) {
            SpecialEventsPrediction dummy = getScorerSpecialEvent(scorer,
                    ISpecialEventPredictionAnalyzer.SpecialEventType.QUICK_SCORES,
                    skillbonus);
            if (dummy != null) return dummy.getChanceCreationProbability();
            return 0;
        }

        public SpecialEventsPrediction getScorerSpecialEvent(
                IMatchRoleID position,
                ISpecialEventPredictionAnalyzer.SpecialEventType type,
                double skillbonus                // to make it easier to score (for wingers etc.)
        ){
            // Calc goalProbability - compare score skill with opponent goalkeeper skill
            MatchRoleID mid = (MatchRoleID) position;
            Player scorer = getPlayer(mid.getSpielerId());;
            int opponentGoalkeeperSkill = 0;
            Player keeper = getOpponentPlayerByPosition(IMatchRoleID.keeper);
            if (keeper != null) opponentGoalkeeperSkill = keeper.getGKskill();
            return SpecialEventsPrediction.createIfInRange(position,type,
                    1, 10-skillbonus, -10-skillbonus,
                    scorer.getSCskill() -  opponentGoalkeeperSkill);
        }

        public double getOpponentGoalProbability(IMatchRoleID scorer) {
            return getOpponentGoalProbability(scorer, 0);
        }

        public double getOpponentGoalProbability(IMatchRoleID scorer, double skillbonus) {
            SpecialEventsPrediction dummy = getOpponentScorerSpecialEvent(scorer,
                    ISpecialEventPredictionAnalyzer.SpecialEventType.QUICK_SCORES,
                    skillbonus);
            if (dummy != null) return dummy.getChanceCreationProbability();
            return 0;
        }

        public SpecialEventsPrediction getOpponentScorerSpecialEvent(
                IMatchRoleID position,
                ISpecialEventPredictionAnalyzer.SpecialEventType type,
                double skillbonus                // to make it easier to score (for wingers etc.)
        ){
            // Calc goalProbability - compare score skill with opponent goalkeeper skill
            MatchRoleID mid = (MatchRoleID) position;
            Player scorer = getOpponentPlayer(mid.getSpielerId());;
            int goalkeeperSkill = 0;
            Player keeper = getPlayerByPosition(IMatchRoleID.keeper);
            if (keeper != null) goalkeeperSkill = keeper.getGKskill();
            return SpecialEventsPrediction.createIfInRange(position,type,
                    1, 10-skillbonus, -10-skillbonus,
                    scorer.getSCskill() -  goalkeeperSkill);
        }

        public double getOpponentRatingIndirectSetPiecesDef() {
            if ( opponentRatingIndirectSetPiecesDef ==null){
                this.opponentRatingIndirectSetPiecesDef = this.opponentLineup.getRatingIndirectSetPiecesDef();
            }
            return opponentRatingIndirectSetPiecesDef;
        }

        public void setOpponentRatingIndirectSetPiecesDef(double ratingIndirectSetPiecesDef) {
            this.opponentRatingIndirectSetPiecesDef = ratingIndirectSetPiecesDef;
        }

        public Double getRatingIndirectSetPiecesAtt() {
            if ( this.ratingIndirectSetPiecesAtt == null){
                this.ratingIndirectSetPiecesAtt = this.lineup.getRatingIndirectSetPiecesAtt();
            }
            return ratingIndirectSetPiecesAtt;
        }

        public void setRatingIndirectSetPiecesAtt(double ratingIndirectSetPiecesAtt) {
            this.ratingIndirectSetPiecesAtt = ratingIndirectSetPiecesAtt;
        }
    }

    private Analyse teamAnalyse;
    private Analyse opponentAnalyse;

    private void initAnalyzers() {
        analyzers = new ArrayList<>();
        analyzers.add(new ExperienceEventPredictionAnalyzer());
        analyzers.add(new UnpredictableEventPredictionAnalyzer());
        analyzers.add(new WingerEventPredictionAnalyzer());
        analyzers.add(new PowerfulEventPredictionAnalyzer());
        analyzers.add(new QuickEventPredictionAnalyzer());
        analyzers.add(new TechnicalEventPredictionAnalyzer());
        analyzers.add(new CornerEventPredictionAnalyzer());
        analyzers.add(new StaminaEventPredictionAnalyzer());
    }

    public SpecialEventsPredictionManager() {
        initAnalyzers();
    }

    public void analyzeLineup(Lineup lineup, MatchDetail opponentMatch) {
        setLineup(lineup);
        setOpponentLineup(opponentMatch);
        this.teamAnalyse = new Analyse(this.lineup, opponentLineup, playerInLineup, opponentPlayerInLineup);
        this.teamAnalyse.analyzeLineup();
        this.opponentAnalyse = new Analyse(opponentLineup, this.lineup, opponentPlayerInLineup, playerInLineup);
        this.opponentAnalyse.setOpponentRatingIndirectSetPiecesDef(opponentMatch.getRatingIndirectSetPiecesDef());
        this.opponentAnalyse.setRatingIndirectSetPiecesAtt(opponentMatch.getRatingIndirectSetPiecesAtt());
        this.opponentAnalyse.analyzeLineup();
    }

    public void setLineup(Lineup m_cLineup) {
        this.lineup = m_cLineup;
        HOModel model = HOVerwaltung.instance().getModel();
        for (IMatchRoleID matchRoleID : this.lineup.getFieldPositions()) {
            MatchRoleID mid = (MatchRoleID) matchRoleID;
            if (mid.getSpielerId() == 0) continue;
            if (this.playerInLineup.containsKey(mid.getSpielerId()) == false) {
                Player player = model.getSpieler(mid.getSpielerId());
                if (player != null) {
                    this.playerInLineup.put(player.getSpielerID(), player);
                }
            }
        }
    }

    public void setOpponentLineup(MatchDetail opponentMatch) {

        HOLogger.instance().debug(getClass(), opponentMatch.getMatchDetail().getMatchDate().toString());

        this.opponentLineup = new Lineup();
        this.opponentLineup.setKicker(opponentMatch.getSetPiecesTaker());
        for (PlayerPerformance playerPerformance : opponentMatch.getPerformances()) {
            if (playerPerformance.getStatus() <= PlayerDataManager.AVAILABLE) {     // if status is UNKNOWN user has to download players info
                this.opponentLineup.setPosition(playerPerformance.getMatchRoleID());
                // playerPerformance -> Player
                OpponentPlayer player = (OpponentPlayer) this.opponentPlayerInLineup.get(playerPerformance.getSpielerId());
                if (player == null) {
                    PlayerInfo latestPlayerInfo = PlayerDataManager.getLatestPlayerInfo(playerPerformance.getSpielerId());
                    int age = latestPlayerInfo.getAge();
                    int wage = latestPlayerInfo.getSalary();
                    int tsi = latestPlayerInfo.getTSI();
                    double form = latestPlayerInfo.getForm();
                    double stamina = latestPlayerInfo.getStamina();
                    int spec = latestPlayerInfo.getSpecialEvent();
                    int role = playerPerformance.getMatchRoleID().getPosition();
                    player = oppPlayerSkillEstimator.calcPlayer(age, wage, tsi, form, stamina, spec, role, -1);
                    player.setSpielerID(playerPerformance.getSpielerId());
                    player.setName(playerPerformance.getSpielerName());
                    player.setHomeGrown(latestPlayerInfo.getMotherClubBonus());
                    player.setLoyalty(latestPlayerInfo.getLoyalty());
                    player.setErfahrung(latestPlayerInfo.getExperience());
                    player.setAlter(age);
                    player.setGehalt(wage);
                    player.setTSI(tsi);
                    player.setForm((int) form);
                    player.setPlayerSpecialty(spec);

                    String pInfo = String.format(
                            "Name=%s, Age=%d, TSI=%d, Wage=%d, Form=%d, Stamina=%d, Experience=%d, GK=%d, DEF=%d, WI=%d, PM=%d , PS=%d, SC=%d, SP=%d, Status=%s",
                            player.getName(),
                            player.getAlter(),
                            player.getTSI(),
                            player.getGehalt(),
                            player.getForm(),
                            player.getKondition(),
                            player.getErfahrung(),
                            player.getGKskill(),
                            player.getDEFskill(),
                            player.getWIskill(),
                            player.getPMskill(),
                            player.getPSskill(),
                            player.getSCskill(),
                            player.getSPskill(),
                            playerPerformance.getStatusAsText()
                    );
                    HOLogger.instance().debug(getClass(), pInfo);

                    this.opponentPlayerInLineup.put(playerPerformance.getSpielerId(), player);
                }

                int positionId = playerPerformance.getPosition();
                byte tacticId = playerPerformance.getTaktik();
                MatchType matchtype = opponentMatch.getMatchDetail().getMatchType();
                double ratingStart = playerPerformance.getRating();
                double ratingEnd = playerPerformance.getRatingEnd();
                PlayedPosition pos = new PlayedPosition(positionId, tacticId, matchtype, ratingStart, ratingEnd);
                player.addPlayedPosition(pos);
                //m_cOppPlayerSkillEstimator.CalculateSkillsForPlayer(player);
            } else {
                // PLayer SOLD, SUSPENDED or INJURED
                OpponentPlayer player = (OpponentPlayer) opponentPlayerInLineup.get(playerPerformance.getSpielerId());
                if (player != null) {
                    opponentPlayerInLineup.remove(player);
                }
            }
        }
    }

    public Player getPlayer(IMatchRoleID id) {
        MatchRoleID mid = (MatchRoleID) id;
        return getPlayer(mid.getSpielerId());
    }

    public Player getPlayer(int id) {
        return this.playerInLineup.get(id);
    }

    public Player getOpponentPlayer(IMatchRoleID id) {
        MatchRoleID mid = (MatchRoleID) id;
        return getOpponentPlayer(mid.getSpielerId());
    }

    public Player getOpponentPlayer(int id) {
        return this.opponentPlayerInLineup.get(id);
    }

    public List<SpecialEventsPrediction> getTeamEvents() {
        if (this.teamAnalyse != null) {
            return this.teamAnalyse.getEvents();
        }
        return null;
    }

    public List<SpecialEventsPrediction> getOpponentEvents() {
        if (this.opponentAnalyse != null) {
            return this.opponentAnalyse.getEvents();
        }
        return null;
    }

    // TODO: each special event type does not happen more than once
    //  => aggregate sums of each type and handle probability sums > 1
    public double getResultScores() {
        double ret = 0;
        for (SpecialEventsPrediction se : getTeamEvents()) {
            if (se.getChanceCreationProbability() > 0) {
                ret += se.getGoalProbability();
            }
        }
        for (SpecialEventsPrediction se : getOpponentEvents()) {
            if (se.getChanceCreationProbability() < 0) {
                ret -= se.getGoalProbability();
            }
        }
        return ret;
    }

    public double getOpponentResultScores() {
        double ret = 0;
        for (SpecialEventsPrediction se : getTeamEvents()) {
            if (se.getChanceCreationProbability() < 0) {
                ret -= se.getGoalProbability();
            }
        }
        for (SpecialEventsPrediction se : getOpponentEvents()) {
            if (se.getChanceCreationProbability() > 0) {
                ret += se.getGoalProbability();
            }
        }
        return ret;
    }

}