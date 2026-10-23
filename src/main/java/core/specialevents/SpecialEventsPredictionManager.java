package core.specialevents;

import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.model.util.PlayerNameFixupUtils;
import core.util.HOLogger;
import module.lineup.Lineup;
import module.opponentspy.OppPlayerSkillEstimator;
import module.opponentspy.OpponentPlayer;
import module.opponentspy.opponentteam.PlayedPosition;
import module.teamanalyzer.manager.PlayerDataManager;
import module.teamanalyzer.vo.MatchDetail;
import module.teamanalyzer.vo.PlayerInfo;
import module.teamanalyzer.vo.PlayerPerformance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class SpecialEventsPredictionManager {

    private static final OppPlayerSkillEstimator OPP_PLAYER_SKILL_ESTIMATOR = new OppPlayerSkillEstimator();

    static ArrayList<ISpecialEventPredictionAnalyzer> analyzers;
    private Lineup lineup = null;
    private Lineup opponentLineup = null;
    private final HashMap<Integer, Player> playerInLineup = new HashMap<>();
    private final HashMap<Integer, Player> opponentPlayerInLineup = new HashMap<>();

    public static class Analyse {
        private List<SpecialEventsPrediction> specialEventsPredictions;
        private final Lineup lineup;
        private final Lineup opponentLineup;
        private final HashMap<Integer, Player> playerInLineup;
        private final HashMap<Integer, Player> opponentPlayerInLineup;
        private Double opponentRatingIndirectSetPiecesDef;
        private Double ratingIndirectSetPiecesAtt;

        public Analyse(Lineup lineup, Lineup oppLineup, HashMap<Integer, Player> player, HashMap<Integer, Player> oppPlayer) {
            this.lineup = lineup;
            opponentLineup = oppLineup;
            playerInLineup = player;
            opponentPlayerInLineup = oppPlayer;
        }

        public void analyzeLineup() {
            if ( this.lineup == null ) return;
            this.specialEventsPredictions = new ArrayList<>();
            for (ISpecialEventPredictionAnalyzer analyzer : analyzers) {
                for (MatchRoleID position : lineup.getFieldPositions()) {
                    if (position.getPlayerId() == 0) continue;
                    analyzer.analyzePosition(this, position);
                }
            }
        }

        public void addSpecialEventPrediction(SpecialEventsPrediction se){
            this.specialEventsPredictions.add(se);
        }

        public Lineup getLineup() {
            return lineup;
        }

        public Player getPlayer(int playerId) {
            return this.playerInLineup.get(playerId);
        }

        public Player getPlayerByPosition(int pos) {
            MatchRoleID mid = this.lineup.getPositionById(pos);
            if ( mid == null )return  null;
            return getPlayer(mid.getPlayerId());
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
            if ( this.opponentLineup == null) return null;
            MatchRoleID mid = this.opponentLineup.getPositionById(pos);
            if ( mid == null) return null;
            return getOpponentPlayer(mid.getPlayerId());
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
            Player scorer = getPlayer(mid.getPlayerId());
            int opponentGoalkeeperSkill = 0;
            Player keeper = getOpponentPlayerByPosition(IMatchRoleID.keeper);
            if (keeper != null) opponentGoalkeeperSkill = keeper.getGoalkeeperSkill();
            return SpecialEventsPrediction.createIfInRange(position,type,
                    1, 10-skillbonus, -10-skillbonus,
                    scorer.getScoringSkill() -  opponentGoalkeeperSkill);
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
            Player scorer = getOpponentPlayer(mid.getPlayerId());
            int goalkeeperSkill = 0;
            Player keeper = getPlayerByPosition(IMatchRoleID.keeper);
            if (keeper != null) goalkeeperSkill = keeper.getGoalkeeperSkill();
            return SpecialEventsPrediction.createIfInRange(position,type,
                    1, 10-skillbonus, -10-skillbonus,
                    scorer.getScoringSkill() -  goalkeeperSkill);
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
        if ( this.lineup == null) return;
        HOModel model = HOVerwaltung.instance().getModel();
        for (MatchRoleID matchRoleID : this.lineup.getFieldPositions()) {
            if (matchRoleID.getPlayerId() == 0) continue;
            if (!this.playerInLineup.containsKey(matchRoleID.getPlayerId())) {
                Player player = model.getCurrentPlayer(matchRoleID.getPlayerId());
                if (player != null) {
                    this.playerInLineup.put(player.getPlayerId(), player);
                }
            }
        }
    }

    public void setOpponentLineup(MatchDetail opponentMatch) {

//        HOLogger.instance().debug(getClass(), opponentMatch.getMatch().getMatchDate().toString());

        this.opponentLineup = new Lineup();
        this.opponentLineup.setKicker(opponentMatch.getSetPiecesTaker());
        for (PlayerPerformance playerPerformance : opponentMatch.getPerformances()) {
            if (playerPerformance.getStatus() <= PlayerDataManager.AVAILABLE) {     // if status is UNKNOWN user has to download players info
                this.opponentLineup.setPosition(playerPerformance.getMatchLineupPosition());
                // playerPerformance -> Player
                OpponentPlayer opponentPlayer = (OpponentPlayer) this.opponentPlayerInLineup.get(playerPerformance.getPlayerId());
                if (opponentPlayer == null) {
                    PlayerInfo latestPlayerInfo = PlayerDataManager.getLatestPlayerInfo(playerPerformance.getPlayerId());
                    opponentPlayer = createOpponentPlayer(playerPerformance, latestPlayerInfo);
                    this.opponentPlayerInLineup.put(playerPerformance.getPlayerId(), opponentPlayer);
                }

                int positionId = playerPerformance.getPosition();
                byte tacticId = playerPerformance.getBehaviour();
                var matchtype = opponentMatch.getMatch().getMatchType();
                double ratingStart = playerPerformance.getRating();
                double ratingEnd = playerPerformance.getRatingEnd();
                PlayedPosition pos = new PlayedPosition(positionId, tacticId, matchtype, ratingStart, ratingEnd);
                opponentPlayer.addPlayedPosition(pos);
            } else {
                // PLayer SOLD, SUSPENDED or INJURED
                OpponentPlayer player = (OpponentPlayer) opponentPlayerInLineup.get(playerPerformance.getPlayerId());
                if (player != null) {
                    opponentPlayerInLineup.remove(player.getPlayerId());
                }
            }
        }
    }

    private static OpponentPlayer createOpponentPlayer(PlayerPerformance playerPerformance, PlayerInfo playerInfo) {
        final int age = playerInfo.getAge();
        final var wage = playerInfo.getSalary();
        final int tsi = playerInfo.getTsi();
        final double form = playerInfo.getForm();
        final double stamina = playerInfo.getStamina();
        final int spec = playerInfo.getSpecialEvent();
        final int role = playerPerformance.getMatchLineupPosition().getPosition();
        OpponentPlayer opponentPlayer = OPP_PLAYER_SKILL_ESTIMATOR.calcPlayer(age, wage, tsi, form, stamina, spec, role, -1);
        opponentPlayer.setPlayerId(playerPerformance.getPlayerId());
        Optional.ofNullable(playerPerformance.getPlayer()).ifPresentOrElse(player -> {
                opponentPlayer.setFirstName(player.getFirstName());
                opponentPlayer.setLastName(player.getLastName());
                opponentPlayer.setNickName(player.getNickName());
            },
            () -> {
                final var playerName = PlayerNameFixupUtils.splitName(playerPerformance.getPlayerName());
                opponentPlayer.setFirstName(playerName.firstName());
                opponentPlayer.setLastName(playerName.lastName());
                opponentPlayer.setNickName(playerName.nickName());
            }
        );
        opponentPlayer.setHomeGrown(playerInfo.isMotherClubBonus());
        opponentPlayer.setLoyalty(playerInfo.getLoyalty());
        opponentPlayer.setExperience(playerInfo.getExperience());
        opponentPlayer.setAge(age);
        opponentPlayer.setWage(wage);
        opponentPlayer.setTsi(tsi);
        opponentPlayer.setForm((int) form);
        opponentPlayer.setSpecialty(spec);
        return opponentPlayer;
    }

    public Player getPlayer(IMatchRoleID id) {
        MatchRoleID mid = (MatchRoleID) id;
        var p = getPlayer(mid.getPlayerId());
        if (p == null) {
            HOLogger.instance().debug(SpecialEventsPredictionManager.class, "mid.getPlayerId() %d not found".formatted(mid.getPlayerId()));
        }
        return p;
    }

    public Player getPlayer(int id) {
        return this.playerInLineup.get(id);
    }

    public Player getOpponentPlayer(IMatchRoleID id) {
        MatchRoleID mid = (MatchRoleID) id;
        var p = getOpponentPlayer(mid.getPlayerId());;
        if (p == null) {
            HOLogger.instance().debug(SpecialEventsPredictionManager.class, "mid.getOpponentPlayer() %d not found".formatted(mid.getPlayerId()));
        }
        return p;
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
        var teamEvents = getTeamEvents();
        if ( teamEvents != null) {
            for (SpecialEventsPrediction se : getTeamEvents()) {
                if (se.getChanceCreationProbability() > 0) {
                    ret += se.getGoalProbability();
                }
            }
        }
        var opponentEvents = getOpponentEvents();
        if ( opponentEvents != null) {
            for (SpecialEventsPrediction se : getOpponentEvents()) {
                if (se.getChanceCreationProbability() < 0) {
                    ret -= se.getGoalProbability();
                }
            }
        }
        return ret;
    }

    public double getOpponentResultScores() {
        double ret = 0;
        var teamEvents = getTeamEvents();
        if ( teamEvents != null) {
            for (SpecialEventsPrediction se : getTeamEvents()) {
                if (se.getChanceCreationProbability() < 0) {
                    ret -= se.getGoalProbability();
                }
            }
        }
        var opponentEvents = getOpponentEvents();
        if ( opponentEvents != null) {
            for (SpecialEventsPrediction se : getOpponentEvents()) {
                if (se.getChanceCreationProbability() > 0) {
                    ret += se.getGoalProbability();
                }
            }
        }
        return ret;
    }

}
