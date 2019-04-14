package tool.pluginFeedback;

import core.model.player.IMatchRoleID;
import core.model.player.Player;
import module.teamAnalyzer.vo.MatchRating;

import java.util.List;
import java.util.Vector;

public class Feedback {

    private String systemInfo;
    private List<Player> playerList;
    private MatchRating rathing;
    private Vector<IMatchRoleID> m_vPositionen;

    public Feedback(Vector<IMatchRoleID> m_vPositionen, MatchRating rathing, List<Player> playerList) {
        this.systemInfo = "HO V 1.436 r 38724387; linux;";
        this.playerList = playerList;
        this.rathing = rathing;
        this.m_vPositionen = m_vPositionen;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public MatchRating getRathing() {
        return rathing;
    }

    public void setRathing(MatchRating rathing) {
        this.rathing = rathing;
    }

    public Vector<IMatchRoleID> getM_vPositionen() {
        return m_vPositionen;
    }

    public void setM_vPositionen(Vector<IMatchRoleID> m_vPositionen) {
        this.m_vPositionen = m_vPositionen;
    }

    public String getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(String systemInfo) {
        this.systemInfo = systemInfo;
    }
}
