package module.pluginFeedback;

import core.HO;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import module.teamAnalyzer.vo.MatchRating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Feedback {

    private Map<String, String> systemInfo;
    private List<Player> playerList;
    private MatchRating rating;
    private Vector<IMatchRoleID> m_vPositionen;

    public Feedback(Vector<IMatchRoleID> m_vPositionen, MatchRating rating, List<Player> playerList, String hoToken, String lineupName) {
        this.systemInfo = new HashMap();
        this.systemInfo.put("OS", System.getProperty("os.name") + " on "
                + System.getProperty("os.arch") + " (" + System.getProperty("os.version")
                + ")");
        this.systemInfo.put("HO! Version", HO.getVersionString());
        this.systemInfo.put("Java Version", System.getProperty("java.version") + " ("
                + System.getProperty("java.vendor") + ")");
        this.systemInfo.put("HO-Token", hoToken);
        this.systemInfo.put("lineupName", lineupName);

        this.playerList = playerList;

        this.rating = rating;

        this.m_vPositionen = m_vPositionen;
    }
}
