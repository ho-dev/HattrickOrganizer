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

    private List<SpecialEventsPrediction> m_vSpecialEventsPredictions;

    static private OppPlayerSkillEstimator m_cOppPlayerSkillEstimator = new OppPlayerSkillEstimator();
    static Vector<ISpecialEventPredictionAnalyzer> analyzers;
    private boolean m_bAnalyzeOpponentLineup = false;
    private Lineup m_cLineup=null;
    private Lineup m_cOpponentLineup = null;
    private HashMap<Integer, Player> playerInLineup = new HashMap<Integer, Player>();
    private HashMap<Integer, Player> opponentPlayerInLineup = new HashMap<Integer, Player>();

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

    public List<SpecialEventsPrediction> analyzeLineup(Lineup lineup, MatchDetail opponentMatch) {
        setLineup(lineup);
        setOpponentLineup(opponentMatch);
        this.m_vSpecialEventsPredictions  = new Vector<SpecialEventsPrediction>();
        for ( ISpecialEventPredictionAnalyzer analyzer: analyzers) {
            for ( IMatchRoleID position: lineup.getFieldPositions()){
                MatchRoleID mid = (MatchRoleID) position;
                if ( mid.getSpielerId() == 0 ) continue;
                this.m_vSpecialEventsPredictions.addAll(analyzer.analyzePosition(mid));
            }
        }
        this.m_bAnalyzeOpponentLineup = true;
        for ( ISpecialEventPredictionAnalyzer analyzer: analyzers) {
            for ( IMatchRoleID position: this.m_cOpponentLineup.getFieldPositions()){
                MatchRoleID mid = (MatchRoleID) position;
                if ( mid.getSpielerId() == 0 ) continue;
                List<SpecialEventsPrediction> specialEventsPredictions = analyzer.analyzePosition(mid);
                for ( SpecialEventsPrediction se: specialEventsPredictions){
                    se.ChangeToOpponentEvent();
                }
                this.m_vSpecialEventsPredictions.addAll(specialEventsPredictions);
            }
        }
        this.m_bAnalyzeOpponentLineup = false;

        return this.m_vSpecialEventsPredictions;
    }

    public Lineup getLineup() {
        if ( m_bAnalyzeOpponentLineup == false )
            return m_cLineup;
        return m_cOpponentLineup;
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

    public Player getPlayer(int playerId)
    {
        return this.playerInLineup.get(playerId);
    }

    public Lineup getOpponentLineup(){
        if ( m_bAnalyzeOpponentLineup == false )
            return this.m_cOpponentLineup;
        return this.m_cLineup;
    }

    public void setOpponentLineup(MatchDetail opponentMatch) {
        this.m_cOpponentLineup = new Lineup();
        for ( PlayerPerformance playerPerformance: opponentMatch.getPerformances() ){
            this.m_cOpponentLineup.setPosition(playerPerformance.getMatchRoleID());
            // playerPerformance -> PLayer
            OpponentPlayer player = (OpponentPlayer) this.opponentPlayerInLineup.get(playerPerformance.getSpielerId());
            if ( player == null){
                PlayerInfo latestPlayerInfo = PlayerDataManager.getLatestPlayerInfo(playerPerformance.getSpielerId());
                int age = latestPlayerInfo.getAge();
                int wage = latestPlayerInfo.getSalary();
                int tsi = latestPlayerInfo.getTSI();
                double form = latestPlayerInfo.getForm();
                double stamina = latestPlayerInfo.getStamina();
                int spec = latestPlayerInfo.getSpecialEvent();
                int role = playerPerformance.getMatchRoleID().getId();
                player = m_cOppPlayerSkillEstimator.calcPlayer(age, wage, tsi, form, stamina, spec, role);
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
    }

    public Player getOpponentPlayer(int id)
    {
        if ( this.m_bAnalyzeOpponentLineup == false )
            return this.opponentPlayerInLineup.get(id);
        return this.playerInLineup.get(id);
    }

}
