// %3482096464:de.hattrickorganizer.gui.playeroverview%
package module.playerOverview;

import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.*;

import core.model.HOVerwaltung;
import core.model.player.Player;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import static core.gui.theme.HOIconName.TRANSFERLISTED_TINY;


/**
 * Panel handling group selection for the players.
 */
public class RemoveGruppenPanel extends ImagePanel implements ActionListener {

	private static final long serialVersionUID = 3606384591123088694L;

	//~ Instance fields ----------------------------------------------------------------------------
    private final JButton doButton = new JButton(ImageUtilities.getSvgIcon(HOIconName.TURN));
    private final JButton m_jbClean = new JButton(ImageUtilities.getSvgIcon(HOIconName.GROUP_TEAM_CLEAN, Map.of("fillColor", HOColorName.TABLEENTRY_DECLINE_FG)));

    private final JToggleButton aGruppe = new JToggleButton(
            GroupTeamFactory.instance().getGreyedGroupIcon(GroupTeamFactory.TEAMSMILIES[1])
    );
	private final JToggleButton aGruppe2 = new JToggleButton(
	        GroupTeamFactory.instance().getGreyedGroupIcon(GroupTeamFactory.TEAMSMILIES[1])
    );
	private final JToggleButton bGruppe = new JToggleButton(
	        GroupTeamFactory.instance().getGreyedGroupIcon(GroupTeamFactory.TEAMSMILIES[2])
    );
	private final JToggleButton bGruppe2 = new JToggleButton(
	        GroupTeamFactory.instance().getGreyedGroupIcon(GroupTeamFactory.TEAMSMILIES[2])
    );
	private final JToggleButton cGruppe = new JToggleButton(
	        GroupTeamFactory.instance().getGreyedGroupIcon(GroupTeamFactory.TEAMSMILIES[3])
    );
	private final JToggleButton cGruppe2 = new JToggleButton(
	        GroupTeamFactory.instance().getGreyedGroupIcon(GroupTeamFactory.TEAMSMILIES[3])
    );
	private final JToggleButton dGruppe = new JToggleButton(
	        GroupTeamFactory.instance().getGreyedGroupIcon(GroupTeamFactory.TEAMSMILIES[4])
    );
	private final JToggleButton dGruppe2 = new JToggleButton(
	        GroupTeamFactory.instance().getGreyedGroupIcon(GroupTeamFactory.TEAMSMILIES[4])
    );
	private final JToggleButton eGruppe = new JToggleButton(
	        GroupTeamFactory.instance().getGreyedGroupIcon(GroupTeamFactory.TEAMSMILIES[5])
    );
	private final JToggleButton eGruppe2 = new JToggleButton(
	        GroupTeamFactory.instance().getGreyedGroupIcon(GroupTeamFactory.TEAMSMILIES[5])
    );
    private final JToggleButton fGruppe = new JToggleButton(
            GroupTeamFactory.instance().getGreyedGroupIcon(GroupTeamFactory.TEAMSMILIES[6])
    );
	private final JToggleButton fGruppe2 = new JToggleButton(
	        GroupTeamFactory.instance().getGreyedGroupIcon(GroupTeamFactory.TEAMSMILIES[6])
    );
    private final JToggleButton noGruppe = new JToggleButton(
            GroupTeamFactory.instance().getGreyedGroupIcon(GroupTeamFactory.TEAMSMILIES[0])
    );
	private final JToggleButton noGruppe2 = new JToggleButton(
	        GroupTeamFactory.instance().getGreyedGroupIcon(GroupTeamFactory.TEAMSMILIES[0])
    );

    // ~ Constructors
	// -------------------------------------------------------------------------------

    /**
	 * Creates a new RemoveGruppenPanel object.
	 *
	 */
    public RemoveGruppenPanel(PlayerOverviewTable spielerTable) {
        initComponents();
    }

    public final void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(doButton)) {
            gruppenMarkierung();
        } else if (e.getSource().equals(m_jbClean)) {
            groupsClear();
        }

        // Button selected in both groups
        doButton.setEnabled((getSelectedButton(true) != null) && (getSelectedButton(false) != null));
    }

    private String getName4Button(JToggleButton button) {
        if (button.equals(noGruppe) || button.equals(noGruppe2)) {
            return "";
        } else if (button.equals(aGruppe) || button.equals(aGruppe2)) {
            return GroupTeamFactory.TEAMSMILIES[1];
        } else if (button.equals(bGruppe) || button.equals(bGruppe2)) {
            return GroupTeamFactory.TEAMSMILIES[2];
        } else if (button.equals(cGruppe) || button.equals(cGruppe2)) {
            return GroupTeamFactory.TEAMSMILIES[3];
        } else if (button.equals(dGruppe) || button.equals(dGruppe2)) {
            return GroupTeamFactory.TEAMSMILIES[4];
        } else if (button.equals(eGruppe) || button.equals(eGruppe2)) {
            return GroupTeamFactory.TEAMSMILIES[5];
        } else if (button.equals(fGruppe) || button.equals(fGruppe2)) {
            return GroupTeamFactory.TEAMSMILIES[6];
        } else {
            return "";
        }
    }

    private JToggleButton getSelectedButton(boolean topRow) {
        if (topRow) {
            if (noGruppe.isSelected()) {
                return noGruppe;
            } else if (aGruppe.isSelected()) {
                return aGruppe;
            } else if (bGruppe.isSelected()) {
                return bGruppe;
            } else if (cGruppe.isSelected()) {
                return cGruppe;
            } else if (dGruppe.isSelected()) {
                return dGruppe;
            } else if (eGruppe.isSelected()) {
                return eGruppe;
            } else if (fGruppe.isSelected()) {
                return fGruppe;
            }
        } else {
            if (noGruppe2.isSelected()) {
                return noGruppe2;
            } else if (aGruppe2.isSelected()) {
                return aGruppe2;
            } else if (bGruppe2.isSelected()) {
                return bGruppe2;
            } else if (cGruppe2.isSelected()) {
                return cGruppe2;
            } else if (dGruppe2.isSelected()) {
                return dGruppe2;
            } else if (eGruppe2.isSelected()) {
                return eGruppe2;
            } else if (fGruppe2.isSelected()) {
                return fGruppe2;
            }
        }

        return null;
    }

    /**
     * Clear current group assignment.
     */
    private void groupsClear() {
        final List<Player> allePlayer = HOVerwaltung.instance().getModel().getCurrentPlayers();
        boolean update = false;

        for (Player player : allePlayer) {
            if (!player.getTeamInfoSmilie().equals("")) {
                player.setTeamInfoSmilie("");
                update = true;
            }
        }

        if (update) {
            HOMainFrame.instance().getAufstellungsPanel().update();
        }

    }

    private void gruppenMarkierung() {
        // Button selected in both groups
        if ((getSelectedButton(true) != null) && (getSelectedButton(false) != null)) {
            final List<Player> allePlayer = HOVerwaltung.instance().getModel().getCurrentPlayers();
            final String suchName = getName4Button(getSelectedButton(true));
            final String ersatzName = getName4Button(getSelectedButton(false));

            for (Player player : allePlayer){
                // Put player in the group.
                if (player.getTeamInfoSmilie().equals(suchName)) {
                    player.setTeamInfoSmilie(ersatzName);
                }
            }

            core.gui.HOMainFrame.instance().getAufstellungsPanel().update();
        }
    }

    private void initComponents() {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(3, 2, 3, 2);

        setLayout(layout);

        final ButtonGroup bg = new ButtonGroup();
        final String tooltipFrom = HOVerwaltung.instance().getLanguageString("tt_Gruppe_von");
        final String tooltipTo = HOVerwaltung.instance().getLanguageString("tt_Gruppe_nach");

        initButton(noGruppe,tooltipFrom,"No-Team.png");
        constraints.gridx = 0;
        constraints.gridy = 0;
        layout.setConstraints(noGruppe, constraints);
        bg.add(noGruppe);
        add(noGruppe);
        initButton(aGruppe,tooltipFrom, GroupTeamFactory.TEAMSMILIES[1]);
        constraints.gridx = 1;
        constraints.gridy = 0;
        layout.setConstraints(aGruppe, constraints);
        bg.add(aGruppe);
        add(aGruppe);
        initButton(bGruppe,tooltipFrom, GroupTeamFactory.TEAMSMILIES[2]);
        constraints.gridx = 2;
        constraints.gridy = 0;
        layout.setConstraints(bGruppe, constraints);
        bg.add(bGruppe);
        add(bGruppe);
        initButton(cGruppe,tooltipFrom, GroupTeamFactory.TEAMSMILIES[3]);
        constraints.gridx = 3;
        constraints.gridy = 0;
        layout.setConstraints(cGruppe, constraints);
        bg.add(cGruppe);
        add(cGruppe);
        initButton(dGruppe,tooltipFrom, GroupTeamFactory.TEAMSMILIES[4]);
        constraints.gridx = 4;
        constraints.gridy = 0;
        layout.setConstraints(dGruppe, constraints);
        bg.add(dGruppe);
        add(dGruppe);
        initButton(eGruppe,tooltipFrom, GroupTeamFactory.TEAMSMILIES[5]);
        constraints.gridx = 5;
        constraints.gridy = 0;
        layout.setConstraints(eGruppe, constraints);
        bg.add(eGruppe);
        add(eGruppe);
        initButton(fGruppe,tooltipFrom, GroupTeamFactory.TEAMSMILIES[6]);
        constraints.gridx = 6;
        constraints.gridy = 0;
        layout.setConstraints(fGruppe, constraints);
        bg.add(fGruppe);
        add(fGruppe);

        final ButtonGroup bg2 = new ButtonGroup();

        initButton(noGruppe2,tooltipTo, GroupTeamFactory.NO_TEAM);
        constraints.gridx = 0;
        constraints.gridy = 1;
        layout.setConstraints(noGruppe2, constraints);
        bg2.add(noGruppe2);
        add(noGruppe2);
        initButton(aGruppe2,tooltipTo, GroupTeamFactory.TEAMSMILIES[1]);
        constraints.gridx = 1;
        constraints.gridy = 1;
        layout.setConstraints(aGruppe2, constraints);
        bg2.add(aGruppe2);
        add(aGruppe2);
        initButton(bGruppe2,tooltipTo, GroupTeamFactory.TEAMSMILIES[2]);
        constraints.gridx = 2;
        constraints.gridy = 1;
        layout.setConstraints(bGruppe2, constraints);
        bg2.add(bGruppe2);
        add(bGruppe2);
        initButton(cGruppe2,tooltipTo, GroupTeamFactory.TEAMSMILIES[3]);
        constraints.gridx = 3;
        constraints.gridy = 1;
        layout.setConstraints(cGruppe2, constraints);
        bg2.add(cGruppe2);
        add(cGruppe2);
        initButton(dGruppe2,tooltipTo, GroupTeamFactory.TEAMSMILIES[4]);
        constraints.gridx = 4;
        constraints.gridy = 1;
        layout.setConstraints(dGruppe2, constraints);
        bg2.add(dGruppe2);
        add(dGruppe2);
        initButton(eGruppe2,tooltipTo, GroupTeamFactory.TEAMSMILIES[5]);
        constraints.gridx = 5;
        constraints.gridy = 1;
        layout.setConstraints(eGruppe2, constraints);
        bg2.add(eGruppe2);
        add(eGruppe2);
        initButton(fGruppe2,tooltipTo, GroupTeamFactory.TEAMSMILIES[6]);
        constraints.gridx = 6;
        constraints.gridy = 1;
        layout.setConstraints(fGruppe2, constraints);
        bg2.add(fGruppe2);
        add(fGruppe2);

        doButton.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Gruppe_wechseln"));
        doButton.setPreferredSize(new Dimension(28, 28));
        doButton.setEnabled(false);
        doButton.addActionListener(this);
        constraints.gridx = 7;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        layout.setConstraints(doButton, constraints);
        add(doButton);

        m_jbClean.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Group_team_clear"));
        m_jbClean.setPreferredSize(new Dimension(28, 28));
        m_jbClean.addActionListener(this);
        constraints.gridx = 8;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        layout.setConstraints(m_jbClean, constraints);
        add(m_jbClean);
    }

    private void initButton(JToggleButton button, String tooltip, String key) {
        button.setToolTipText(tooltip);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setPreferredSize(new Dimension(18, 18));
        button.setSelectedIcon(GroupTeamFactory.instance().getActiveGroupIcon(key));
        button.addActionListener(this);
    }
}
