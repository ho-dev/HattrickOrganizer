package core.specialevents;

import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import module.lineup.Lineup;
import module.teamAnalyzer.vo.PlayerPerformance;

import javax.sound.sampled.Line;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class SpecialEventsPredictionManager {

    static Vector<ISpecialEventPredictionAnalyzer> analyzers;
    private Lineup m_cLineup=null;
    private Lineup m_cOpponentLineup = null;
    private HashMap<Integer, Player> playerInLineup = new HashMap<Integer, Player>();
    private HashMap<Integer, PlayerPerformance> opponentPlayerInLineup = new HashMap<Integer, PlayerPerformance>();

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

    public List<SpecialEventsPrediction> analyzeLineup(Lineup lineup, List<PlayerPerformance> opponent) {
        setLineup(lineup);
        setOpponentLineup(opponent);
        List<SpecialEventsPrediction> ret = new Vector<SpecialEventsPrediction>();
        for ( ISpecialEventPredictionAnalyzer analyzer: analyzers) {
            for ( IMatchRoleID position: lineup.getFieldPositions()){
                MatchRoleID mid = (MatchRoleID) position;
                if ( mid.getSpielerId() == 0 ) continue;
                ret.addAll(analyzer.analyzePosition(mid));
            }
        }
        return ret;
    }

    public Lineup getLineup() {
        return m_cLineup;
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

    public Lineup getOpponentLineup(){ return this.m_cOpponentLineup;}
    public void setOpponentLineup(List<PlayerPerformance> opponentLineup) {
        this.m_cOpponentLineup = new Lineup();
        for ( PlayerPerformance playerPerformance: opponentLineup ){
            this.m_cOpponentLineup.setPosition(playerPerformance.getMatchRoleID());
            this.opponentPlayerInLineup.put(playerPerformance.getSpielerId(), playerPerformance);
        }
    }

    public PlayerPerformance getOpponentPlayer(int id){
        return this.opponentPlayerInLineup.get(id);
    }

}
