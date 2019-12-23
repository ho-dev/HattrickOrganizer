package core.specialevents;

import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import module.lineup.Lineup;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class SpecialEventsPredictionManager {

    List<ISpecialEventPredictionAnalyzer> analyzers;
    private Lineup m_cLineup=null;
    private Map<Integer, Player> playerInLineup;

    private void  initAnalyzers()
    {
        analyzers.add(new ExperienceEventPredictionAnalyzer(this));
        analyzers.add(new UnpredictableEventPredictionAnalyzer(this));
    }

    public SpecialEventsPredictionManager()
    {
        initAnalyzers();
    }

    public List<SpecialEventsPrediction> analyzeLineup(Lineup lineup)
    {
        setLineup(lineup);
        List<SpecialEventsPrediction> ret = new Vector<SpecialEventsPrediction>();
        for ( ISpecialEventPredictionAnalyzer analyzer: analyzers) {
            for ( IMatchRoleID position: lineup.getFieldPositions()){
                MatchRoleID mid = (MatchRoleID) position;
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
}
