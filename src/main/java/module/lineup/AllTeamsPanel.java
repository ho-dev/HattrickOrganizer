package module.lineup;

import core.gui.theme.GroupTeamFactory;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.Helper;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.Border;


/**
 * Assign players to a given group or clear group in a single click
 */
public final class AllTeamsPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 955755336335567688L;

    //~ Instance fields ----------------------------------------------------------------------------

	private final JButton aGroup = new JButton(GroupTeamFactory.instance().getActiveGroupIcon(GroupTeamFactory.TEAMSMILIES[1]));
    private final JButton bGroup = new JButton(GroupTeamFactory.instance().getActiveGroupIcon(GroupTeamFactory.TEAMSMILIES[2]));
    private final JButton cGroup = new JButton(GroupTeamFactory.instance().getActiveGroupIcon(GroupTeamFactory.TEAMSMILIES[3]));
    private final JButton dGroup = new JButton(GroupTeamFactory.instance().getActiveGroupIcon(GroupTeamFactory.TEAMSMILIES[4]));
    private final JButton eGroup = new JButton(GroupTeamFactory.instance().getActiveGroupIcon(GroupTeamFactory.TEAMSMILIES[5]));
    private final JButton fGroup = new JButton(GroupTeamFactory.instance().getActiveGroupIcon(GroupTeamFactory.TEAMSMILIES[6]));
    private final JButton m_jbClean = new JButton(ImageUtilities.getSvgIcon(HOIconName.GROUP_TEAM_CLEAN, Map.of("fillColor", HOColorName.TABLEENTRY_DECLINE_FG)));
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new AufstellungsGruppenPanel object.
     */
    public AllTeamsPanel() {
        this.setOpaque(true);
        initComponents();
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(aGroup)) {
            setGroup(GroupTeamFactory.TEAMSMILIES[1]);
        } else if (e.getSource().equals(bGroup)) {
            setGroup(GroupTeamFactory.TEAMSMILIES[2]);
        } else if (e.getSource().equals(cGroup)) {
            setGroup(GroupTeamFactory.TEAMSMILIES[3]);
        } else if (e.getSource().equals(dGroup)) {
            setGroup(GroupTeamFactory.TEAMSMILIES[4]);
        } else if (e.getSource().equals(eGroup)) {
            setGroup(GroupTeamFactory.TEAMSMILIES[5]);
        } else if (e.getSource().equals(fGroup)) {
            setGroup(GroupTeamFactory.TEAMSMILIES[6]);
        }
        else if (e.getSource().equals(m_jbClean)) {
            setGroup("");
        }
    }

    private void setGroup(String sGroup) {
        final HOModel model = HOVerwaltung.instance().getModel();
        final List<Player> lPlayers = model.getCurrentPlayers();
        final Lineup lineup = model.getLineup();

        //Remove all players on the group and put the new ones in
        for (Player player : lPlayers) {
            // players in starting lineup are put in selected group
            if (lineup.isPlayerInStartingEleven(player.getPlayerID())) {
                player.setTeamInfoSmilie(sGroup);
            }
            // all other players are not part of that group
            else if (player.getTeamInfoSmilie().equals(sGroup)) {
                player.setTeamInfoSmilie("");
            }
        }

        core.gui.HOMainFrame.instance().getLineupPanel().update();
    }

    private void initComponents() {
        makeGroupButton(aGroup, "tt_AufstellungsGruppe_Zuordnung");
        makeGroupButton(bGroup, "tt_AufstellungsGruppe_Zuordnung");
        makeGroupButton(cGroup, "tt_AufstellungsGruppe_Zuordnung");
        makeGroupButton(dGroup, "tt_AufstellungsGruppe_Zuordnung");
        makeGroupButton(eGroup, "tt_AufstellungsGruppe_Zuordnung");
        makeGroupButton(fGroup, "tt_AufstellungsGruppe_Zuordnung");
        makeGroupButton(m_jbClean, "tt_Lineup_RemoveGroup");
    }

    private void makeGroupButton(JButton jbGroup, String tooltip) {
        jbGroup.setToolTipText(Helper.getTranslation(tooltip));
        jbGroup.setPreferredSize(new Dimension(28, 28));
//        jbGroup.setBorder(BorderFactory.createEmptyBorder());
        jbGroup.addActionListener(this);
        add(jbGroup);
    }
}
