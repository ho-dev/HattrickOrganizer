// %3622084902:de.hattrickorganizer.gui.lineup%
package module.lineup;

import core.gui.theme.GroupTeam;
import core.model.HOVerwaltung;
import core.model.player.Player;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Hier lassen sich mit einem Klick alle aufgestellten Player einer Gruppe zuordnen
 */
final class AufstellungsGruppenPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 955755336335567688L;

    //~ Instance fields ----------------------------------------------------------------------------

	private JButton aGruppe = new JButton(GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[1], 16, 16));
    private JButton bGruppe = new JButton(GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[2], 16, 16));
    private JButton cGruppe = new JButton(GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[3], 16, 16));
    private JButton dGruppe = new JButton(GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[4], 16, 16));
    private JButton eGruppe = new JButton(GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[5], 16, 16));
    private JButton fGruppe = new JButton(GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[6], 16, 16));

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new AufstellungsGruppenPanel object.
     */
    public AufstellungsGruppenPanel() {
        this.setOpaque(false);

        initComponents();
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(aGruppe)) {
            gruppenMarkierung(GroupTeam.TEAMSMILIES[1]);
        } else if (e.getSource().equals(bGruppe)) {
            gruppenMarkierung(GroupTeam.TEAMSMILIES[2]);
        } else if (e.getSource().equals(cGruppe)) {
            gruppenMarkierung(GroupTeam.TEAMSMILIES[3]);
        } else if (e.getSource().equals(dGruppe)) {
            gruppenMarkierung(GroupTeam.TEAMSMILIES[4]);
        } else if (e.getSource().equals(eGruppe)) {
            gruppenMarkierung(GroupTeam.TEAMSMILIES[5]);
        } else if (e.getSource().equals(fGruppe)) {
            gruppenMarkierung(GroupTeam.TEAMSMILIES[6]);
        }
    }

    private void gruppenMarkierung(String gruppenName) {
        final List<Player> allePlayer = HOVerwaltung.instance().getModel().getCurrentPlayers();
        final Lineup aufstellung = HOVerwaltung.instance().getModel().getLineup();

        //Alle Player auf der Gruppe entfernen und die neuen reinsetzen
        for (Player player : allePlayer) {
            //ein erste 11
            if (aufstellung.isPlayerInStartingEleven(player.getSpielerID())) {
                player.setTeamInfoSmilie(gruppenName);
            }
            //nicht erste 11 und trotzdem in der gleichen Gruppe
            else if (player.getTeamInfoSmilie().equals(gruppenName)) {
                //Gruppe entfernen
                player.setTeamInfoSmilie("");
            }
        }

        core.gui.HOMainFrame.instance().getAufstellungsPanel().update();
    }

    private void initComponents() {
    	
    	// Horizontal by Blaghaid - away with size specification
    	
        //setPreferredSize(new Dimension(20, 200));

        //Platzhalter
        add(new JLabel("   "));
        aGruppe.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_AufstellungsGruppe_Zuordnung"));
        aGruppe.setPreferredSize(new Dimension(18, 18));
        aGruppe.addActionListener(this);
        add(aGruppe);
        bGruppe.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_AufstellungsGruppe_Zuordnung"));
        bGruppe.setPreferredSize(new Dimension(18, 18));
        bGruppe.addActionListener(this);
        add(bGruppe);
        cGruppe.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_AufstellungsGruppe_Zuordnung"));
        cGruppe.setPreferredSize(new Dimension(18, 18));
        cGruppe.addActionListener(this);
        add(cGruppe);
        dGruppe.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_AufstellungsGruppe_Zuordnung"));
        dGruppe.setPreferredSize(new Dimension(18, 18));
        dGruppe.addActionListener(this);
        add(dGruppe);
        eGruppe.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_AufstellungsGruppe_Zuordnung"));
        eGruppe.setPreferredSize(new Dimension(18, 18));
        eGruppe.addActionListener(this);
        add(eGruppe);
        fGruppe.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_AufstellungsGruppe_Zuordnung"));
        fGruppe.setPreferredSize(new Dimension(18, 18));
        fGruppe.addActionListener(this);
        add(fGruppe);
    }
}
