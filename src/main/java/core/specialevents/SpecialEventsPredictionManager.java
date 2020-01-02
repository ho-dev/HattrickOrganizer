package core.specialevents;

import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.match.MatchType;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import module.lineup.Lineup;
import module.opponentspy.OppPlayerSkillEstimator;
import module.opponentspy.OpponentPlayer;
import module.opponentspy.OpponentTeam.PlayedPosition;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.vo.MatchDetail;
import module.teamAnalyzer.vo.PlayerInfo;
import module.teamAnalyzer.vo.PlayerPerformance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

public class SpecialEventsPredictionManager {

    static private OppPlayerSkillEstimator oppPlayerSkillEstimator = new OppPlayerSkillEstimator();
    static Vector<ISpecialEventPredictionAnalyzer> analyzers;
    private Lineup lineup =null;
    private Lineup opponentLineup = null;
    private HashMap<Integer, Player> playerInLineup = new HashMap<Integer, Player>();
    private HashMap<Integer, Player> opponentPlayerInLineup = new HashMap<Integer, Player>();

    public class Analyse
    {
        private List<SpecialEventsPrediction> specialEventsPredictions;
        private Lineup lineup =null;
        private Lineup opponentLineup = null;
        private HashMap<Integer, Player> playerInLineup;
        private HashMap<Integer, Player> opponentPlayerInLineup;

        public Analyse(Lineup lineup, Lineup oppLineup, HashMap<Integer, Player> player, HashMap<Integer, Player> oppPlayer){
            this.lineup = lineup;
            opponentLineup = oppLineup;
            playerInLineup = player;
            opponentPlayerInLineup = oppPlayer;
        }

        public void analyzeLineup() {
            this.specialEventsPredictions = new Vector<SpecialEventsPrediction>();
            for (ISpecialEventPredictionAnalyzer analyzer : analyzers) {
                for (IMatchRoleID position : lineup.getFieldPositions()) {
                    MatchRoleID mid = (MatchRoleID) position;
                    if (mid.getSpielerId() == 0) continue;
                    this.specialEventsPredictions.addAll(analyzer.analyzePosition(this, mid));
                }
            }
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
            return this.lineup.getPlayerByPositionID(pos);
        }

        public MatchRoleID getPosition(int pos) {
            return this.lineup.getPositionById(pos);
        }

        public Player getOpponentPlayer(int id){
            return this.opponentPlayerInLineup.get(id);
        }

        public List<SpecialEventsPrediction> getEvents() {
            return this.specialEventsPredictions;
        }

        public Player getOpponentPlayerByPosition(int pos) {
            return this.opponentLineup.getPlayerByPositionID(pos);
        }

        public MatchRoleID getOpponentPosition(int pos) {
            return this.opponentLineup.getPositionById(pos);
        }
    }

    private Analyse teamAnalyse;
    private Analyse opponentAnalyse;

    private void  initAnalyzers()
    {
        analyzers = new Vector<ISpecialEventPredictionAnalyzer>();
        analyzers.add(new ExperienceEventPredictionAnalyzer());
        analyzers.add(new UnpredictableEventPredictionAnalyzer());
        analyzers.add(new WingerEventPredictionAnalyzer());
        analyzers.add(new PowerfulForwardEventPredictionAnalyzer());
        analyzers.add(new SittingMidfielderEventPredictionAnalyzer());
        analyzers.add(new QuickEventPredictionAnalyzer());
    }

    public SpecialEventsPredictionManager()
    {
        initAnalyzers();
    }

    public void analyzeLineup(Lineup lineup, MatchDetail opponentMatch) {
        setLineup(lineup);
        setOpponentLineup(opponentMatch);
        this.teamAnalyse = new Analyse(this.lineup, opponentLineup, playerInLineup, opponentPlayerInLineup);
        this.teamAnalyse.analyzeLineup();
        this.opponentAnalyse = new Analyse(opponentLineup, this.lineup, opponentPlayerInLineup, playerInLineup);
        this.opponentAnalyse.analyzeLineup();
    }

    public void setLineup(Lineup m_cLineup) {
        this.lineup = m_cLineup;
        HOModel model = HOVerwaltung.instance().getModel();
        for (IMatchRoleID matchRoleID : this.lineup.getFieldPositions())
        {
            MatchRoleID mid = (MatchRoleID) matchRoleID;
            if ( mid.getSpielerId() == 0 ) continue;
            if ( this.playerInLineup.containsKey(mid.getSpielerId()) == false){
                Player player = model.getSpieler(mid.getSpielerId());
                if ( player != null){
                    this.playerInLineup.put(player.getSpielerID(), player);
                }
            }
        }
    }

    public void setOpponentLineup(MatchDetail opponentMatch) {
        this.opponentLineup = new Lineup();
        for ( PlayerPerformance playerPerformance: opponentMatch.getPerformances() ) {
            if (playerPerformance.getStatus() == PlayerDataManager.AVAILABLE) {     // if status is UNKNOWN user has to download players info
                this.opponentLineup.setPosition(playerPerformance.getMatchRoleID());
                // playerPerformance -> PLayer
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
                    player.setAlter(age);
                    player.setGehalt(wage);
                    player.setTSI(tsi);
                    player.setForm((int)form);
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
            }
            else {
                // PLayer SOLD, SUSPENDED or INJURED
                OpponentPlayer player = (OpponentPlayer) opponentPlayerInLineup.get(playerPerformance.getSpielerId());
                if ( player != null){
                    opponentPlayerInLineup.remove(player);
                }
            }
        }
    }

    public Player getPlayer(IMatchRoleID id){
        MatchRoleID mid = (MatchRoleID) id;
        return getPlayer(mid.getSpielerId());
    }

    public Player getPlayer(int id){
        return this.playerInLineup.get(id);
    }

    public Player getOpponentPlayer(IMatchRoleID id){
        MatchRoleID mid = (MatchRoleID) id;
        return getOpponentPlayer(mid.getSpielerId());
    }

    public Player getOpponentPlayer(int id){
        return this.opponentPlayerInLineup.get(id);
    }

    public List<SpecialEventsPrediction> getTeamEvents(){
        if (this.teamAnalyse != null) {
            return this.teamAnalyse.getEvents();
        }
        return null;
    }

    public List<SpecialEventsPrediction> getOpponentEvents(){
        if ( this.opponentAnalyse != null){
            return  this.opponentAnalyse.getEvents();
        }
        return null;
    }
}
