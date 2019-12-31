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
import module.teamAnalyzer.ht.HattrickManager;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.vo.MatchDetail;
import module.teamAnalyzer.vo.PlayerInfo;
import module.teamAnalyzer.vo.PlayerPerformance;

import javax.sound.sampled.Line;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class SpecialEventsPredictionManager {

    static private OppPlayerSkillEstimator m_cOppPlayerSkillEstimator = new OppPlayerSkillEstimator();
    static Vector<ISpecialEventPredictionAnalyzer> analyzers;
    private Lineup m_cLineup=null;
    private Lineup m_cOpponentLineup = null;
    private HashMap<Integer, Player> playerInLineup = new HashMap<Integer, Player>();
    private HashMap<Integer, Player> opponentPlayerInLineup = new HashMap<Integer, Player>();

    public class Analyse
    {
        private List<SpecialEventsPrediction> m_vSpecialEventsPredictions;
        private Lineup m_cLineup=null;
        private Lineup m_cOpponentLineup = null;
        private HashMap<Integer, Player> playerInLineup = new HashMap<Integer, Player>();
        private HashMap<Integer, Player> opponentPlayerInLineup = new HashMap<Integer, Player>();

        public Analyse(Lineup lineup, Lineup oppLineup, HashMap<Integer, Player> player, HashMap<Integer, Player> oppPlayer){
            m_cLineup = lineup;
            m_cOpponentLineup = oppLineup;
            playerInLineup = player;
            opponentPlayerInLineup = oppPlayer;
        }

        public void analyzeLineup() {
            this.m_vSpecialEventsPredictions = new Vector<SpecialEventsPrediction>();
            for (ISpecialEventPredictionAnalyzer analyzer : analyzers) {
                for (IMatchRoleID position : m_cLineup.getFieldPositions()) {
                    MatchRoleID mid = (MatchRoleID) position;
                    if (mid.getSpielerId() == 0) continue;
                    this.m_vSpecialEventsPredictions.addAll(analyzer.analyzePosition(this, mid));
                }
            }
        }

        public Lineup getLineup() {
            return m_cLineup;
        }

        public Lineup getOpponentLineup() {
            return m_cOpponentLineup;
        }

        public Player getPlayer(int playerId) {
            return this.playerInLineup.get(playerId);
        }

        public Player getOpponentPlayer(int id){
            return this.opponentPlayerInLineup.get(id);
        }

        public List<SpecialEventsPrediction> getEvents() {
            return this.m_vSpecialEventsPredictions;
        }
    }

    private Analyse teamAnalyse;
    private Analyse opponentAnalyse;

    private void  initAnalyzers()
    {
        analyzers = new Vector<ISpecialEventPredictionAnalyzer>();
        analyzers.add(new ExperienceEventPredictionAnalyzer(this));
        analyzers.add(new UnpredictableEventPredictionAnalyzer(this));
        analyzers.add(new WingerEventPredictionAnalyzer(this));
        analyzers.add(new PowerfulForwardEventPredictionAnalyzer(this));
        analyzers.add(new SittingMidfielderEventPredictionAnalyzer(this));
    }

    public SpecialEventsPredictionManager()
    {
        initAnalyzers();
    }

    public void analyzeLineup(Lineup lineup, MatchDetail opponentMatch) {
        setLineup(lineup);
        setOpponentLineup(opponentMatch);
        this.teamAnalyse = new Analyse(m_cLineup, m_cOpponentLineup, playerInLineup, opponentPlayerInLineup);
        this.teamAnalyse.analyzeLineup();
        this.opponentAnalyse = new Analyse(m_cOpponentLineup, m_cLineup, opponentPlayerInLineup, playerInLineup);
        this.opponentAnalyse.analyzeLineup();
    }

    public void setLineup(Lineup m_cLineup) {
        this.m_cLineup = m_cLineup;
        HOModel model = HOVerwaltung.instance().getModel();
        for (IMatchRoleID matchRoleID : this.m_cLineup.getFieldPositions())
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
        this.m_cOpponentLineup = new Lineup();
        for ( PlayerPerformance playerPerformance: opponentMatch.getPerformances() ) {
            if (playerPerformance.getStatus() == PlayerDataManager.AVAILABLE) {     // if status is UNKNOWN user has to download players info
                this.m_cOpponentLineup.setPosition(playerPerformance.getMatchRoleID());
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
                    boolean motherClubBonus = latestPlayerInfo.getMotherClubBonus();
                    int role = playerPerformance.getMatchRoleID().getId();
                    player = m_cOppPlayerSkillEstimator.calcPlayer(age, wage, tsi, form, stamina, spec, role);
                    player.setSpielerID(playerPerformance.getSpielerId());
                    player.setName(playerPerformance.getSpielerName());
                    player.setHomeGrown(latestPlayerInfo.getMotherClubBonus());
                    player.setLoyalty(latestPlayerInfo.getLoyalty());
                    this.opponentPlayerInLineup.put(playerPerformance.getSpielerId(), player);
                }

                int positionId = playerPerformance.getPosition();
                byte tacticId = playerPerformance.getTaktik();
                MatchType matchtype = opponentMatch.getMatchDetail().getMatchType();
                double ratingStart = playerPerformance.getRating();
                double ratingEnd = playerPerformance.getRatingEnd();
                PlayedPosition pos = new PlayedPosition(positionId, tacticId, matchtype, ratingStart, ratingEnd);
                player.addPlayedPosition(pos);
                m_cOppPlayerSkillEstimator.CalculateSkillsForPlayer(player);
            }
            else {
                // PLayer SUSPENDED or INJURED
                OpponentPlayer player = (OpponentPlayer) opponentPlayerInLineup.get(playerPerformance.getSpielerId());
                if ( player != null){
                    opponentPlayerInLineup.remove(player);
                }
            }
        }
    }

    public List<SpecialEventsPrediction> getTeamevents(){
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
