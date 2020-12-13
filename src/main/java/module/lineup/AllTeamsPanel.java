package module.lineup;

import core.gui.theme.GroupTeamFactory;
import core.model.HOVerwaltung;
import core.model.player.Player;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;


/**
 * Assign players to a given group in a single click.
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
            gruppenMarkierung(GroupTeamFactory.TEAMSMILIES[1]);
        } else if (e.getSource().equals(bGroup)) {
            gruppenMarkierung(GroupTeamFactory.TEAMSMILIES[2]);
        } else if (e.getSource().equals(cGroup)) {
            gruppenMarkierung(GroupTeamFactory.TEAMSMILIES[3]);
        } else if (e.getSource().equals(dGroup)) {
            gruppenMarkierung(GroupTeamFactory.TEAMSMILIES[4]);
        } else if (e.getSource().equals(eGroup)) {
            gruppenMarkierung(GroupTeamFactory.TEAMSMILIES[5]);
        } else if (e.getSource().equals(fGroup)) {
            gruppenMarkierung(GroupTeamFactory.TEAMSMILIES[6]);
        }
    }

    private void gruppenMarkierung(String gruppenName) {
        final List<Player> allePlayer = HOVerwaltung.instance().getModel().getCurrentPlayers();
        final Lineup aufstellung = HOVerwaltung.instance().getModel().getLineup();

        //Alle Player auf der Gruppe entfernen und die neuen reinsetzen
        for (Player player : allePlayer) {
            //ein erste 11
            if (aufstellung.isPlayerInStartingEleven(player.getPlayerID())) {
                player.setTeamInfoSmilie(gruppenName);
            }
            //nicht erste 11 und trotzdem in der gleichen Gruppe
            else if (player.getTeamInfoSmilie().equals(gruppenName)) {
                //Gruppe entfernen
                player.setTeamInfoSmilie("");
            }
        }

        core.gui.HOMainFrame.instance().getLineupPanel().update();
    }

    private void initComponents() {

        Border emptyBorder = BorderFactory.createEmptyBorder();

        aGroup.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_AufstellungsGruppe_Zuordnung"));
        aGroup.setPreferredSize(new Dimension(18, 18));
        aGroup.setBorder(emptyBorder);
        aGroup.addActionListener(this);
        add(aGroup);
        bGroup.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_AufstellungsGruppe_Zuordnung"));
        bGroup.setPreferredSize(new Dimension(18, 18));
        bGroup.addActionListener(this);
        bGroup.setBorder(emptyBorder);
        add(bGroup);
        cGroup.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_AufstellungsGruppe_Zuordnung"));
        cGroup.setPreferredSize(new Dimension(18, 18));
        cGroup.addActionListener(this);
        cGroup.setBorder(emptyBorder);
        add(cGroup);
        dGroup.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_AufstellungsGruppe_Zuordnung"));
        dGroup.setPreferredSize(new Dimension(18, 18));
        dGroup.addActionListener(this);
        dGroup.setBorder(emptyBorder);
        add(dGroup);
        eGroup.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_AufstellungsGruppe_Zuordnung"));
        eGroup.setPreferredSize(new Dimension(18, 18));
        eGroup.addActionListener(this);
        eGroup.setBorder(emptyBorder);
        add(eGroup);
        fGroup.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_AufstellungsGruppe_Zuordnung"));
        fGroup.setPreferredSize(new Dimension(18, 18));
        fGroup.addActionListener(this);
        fGroup.setBorder(emptyBorder);
        add(fGroup);
    }
}
