// %3482096464:de.hattrickorganizer.gui.playeroverview%
package module.playerOverview;

import core.gui.print.ComponentPrintObject;
import core.gui.print.PrintController;

import core.gui.theme.*;

import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.HOLogger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.text.DateFormat;
import java.util.List;

import javax.swing.*;


/**
 * Alle Player der Gruppe werden einer Gruppe zugeordnet
 */
public class RemoveGruppenPanel extends core.gui.comp.panel.ImagePanel
    implements ActionListener
{

	private static final long serialVersionUID = 3606384591123088694L;

	//~ Instance fields ----------------------------------------------------------------------------

    private final JButton doButton = new JButton(ImageUtilities.getSvgIcon(HOIconName.TURN));
    private final JButton m_jbDrucken = new JButton(ImageUtilities.getSvgIcon(HOIconName.PRINTER));
    private final JToggleButton aGruppe = new JToggleButton(
            GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[1], 12, 12, "0.25"));
	private final JToggleButton aGruppe2 = new JToggleButton(
            GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[1], 12, 12, "0.25"));
	private final JToggleButton bGruppe = new JToggleButton(
			GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[2],12, 12, "0.25"));
	private final JToggleButton bGruppe2 = new JToggleButton(
			GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[2], 12, 12, "0.25"));
	private final JToggleButton cGruppe = new JToggleButton(
			GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[3], 12, 12, "0.25"));
	private final JToggleButton cGruppe2 = new JToggleButton(
			GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[3], 12, 12, "0.25"));
	private final JToggleButton dGruppe = new JToggleButton(
			GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[4], 12, 12, "0.25"));
	private final JToggleButton dGruppe2 = new JToggleButton(
			GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[4], 12, 12, "0.25"));
	private final JToggleButton eGruppe = new JToggleButton(
			GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[5], 12, 12, "0.25"));
	private final JToggleButton eGruppe2 = new JToggleButton(
			GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[5], 12, 12, "0.25"));
    private final JToggleButton fGruppe = new JToggleButton(
			GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[6], 12, 12, "0.25"));
	private final JToggleButton fGruppe2 = new JToggleButton(
            GroupTeam.getGroupIcon(GroupTeam.TEAMSMILIES[6], 12, 12, "0.25"));
    private final JToggleButton noGruppe = new JToggleButton(
            GroupTeam.getGroupIcon("", 12, 12, "0.25"));
	private final JToggleButton noGruppe2 = new JToggleButton(
            GroupTeam.getGroupIcon("", 12, 12, "0.25"));

    private PlayerOverviewTable m_clTable;

    // ~ Constructors
	// -------------------------------------------------------------------------------

    /**
	 * Creates a new RemoveGruppenPanel object.
	 *
	 */
    public RemoveGruppenPanel(PlayerOverviewTable spielerTable) {
        m_clTable = spielerTable;
        initComponents();
    }

    public final void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(doButton)) {
            gruppenMarkierung();
        } else if (e.getSource().equals(m_jbDrucken)) {
            drucken();
        }

        //Von beiden Gruppen ein Button selektiert // Nach gruppenMarkierung werden die Button wieder unselected
        if ((getSelectedButton(true) != null) && (getSelectedButton(false) != null)) {
            doButton.setEnabled(true);
        } else {
            doButton.setEnabled(false);
        }
    }

    private String getName4Button(JToggleButton button) {
        if (button.equals(noGruppe) || button.equals(noGruppe2)) {
            return "";
        } else if (button.equals(aGruppe) || button.equals(aGruppe2)) {
            return GroupTeam.TEAMSMILIES[1];
        } else if (button.equals(bGruppe) || button.equals(bGruppe2)) {
            return GroupTeam.TEAMSMILIES[2];
        } else if (button.equals(cGruppe) || button.equals(cGruppe2)) {
            return GroupTeam.TEAMSMILIES[3];
        } else if (button.equals(dGruppe) || button.equals(dGruppe2)) {
            return GroupTeam.TEAMSMILIES[4];
        } else if (button.equals(eGruppe) || button.equals(eGruppe2)) {
            return GroupTeam.TEAMSMILIES[5];
        } else if (button.equals(fGruppe) || button.equals(fGruppe2)) {
            return GroupTeam.TEAMSMILIES[6];
        } else {
            return "";
        }
    }

    private JToggleButton getSelectedButton(boolean obereReihe) {
        if (obereReihe) {
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
     * Drucken der Spielerübersicht
     */
    private void drucken() {
        try {
            //Damit nur bestimmte Spalten gedruckt werden ist eine spezielle Tabelle notwendig.
            //Das Scrollpane benötigt man, damit die Spaltenbeschriftung auch angezeigt wird.
            final SpielerUebersichtPrintTable table = new SpielerUebersichtPrintTable(m_clTable);
            final JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(table.getPreferredSize().width + 10,
                                                      table.getPreferredSize().height + 70));
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.getViewport().setBackground(Color.WHITE);

            final PrintController printController = PrintController.getInstance();

            final java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            final String titel = HOVerwaltung.instance().getLanguageString("Spieleruebersicht")
                                 + " - "
                                 + HOVerwaltung.instance().getModel().getBasics().getTeamName()
                                 + " - "
                                 + DateFormat.getDateTimeInstance().format(calendar.getTime());
            printController.add(new ComponentPrintObject(printController.getPf(),
                                                                                        titel,
                                                                                        scrollPane,
                                                                                        core.gui.print.ComponentPrintObject.NICHTSICHTBAR));

            printController.print();
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),e);
        }
    }


    private void gruppenMarkierung() {
        //Von beiden Gruppen ein Button selektiert
        if ((getSelectedButton(true) != null) && (getSelectedButton(false) != null)) {
            final List<Player> allePlayer = HOVerwaltung.instance().getModel().getCurrentPlayers();
            final String suchName = getName4Button(getSelectedButton(true));
            final String ersatzName = getName4Button(getSelectedButton(false));

            for (Player player : allePlayer){
                //Player in der Gruppe
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
        initButton(aGruppe,tooltipFrom, GroupTeam.TEAMSMILIES[1]);
        constraints.gridx = 1;
        constraints.gridy = 0;
        layout.setConstraints(aGruppe, constraints);
        bg.add(aGruppe);
        add(aGruppe);
        initButton(bGruppe,tooltipFrom, GroupTeam.TEAMSMILIES[2]);
        constraints.gridx = 2;
        constraints.gridy = 0;
        layout.setConstraints(bGruppe, constraints);
        bg.add(bGruppe);
        add(bGruppe);
        initButton(cGruppe,tooltipFrom, GroupTeam.TEAMSMILIES[3]);
        constraints.gridx = 3;
        constraints.gridy = 0;
        layout.setConstraints(cGruppe, constraints);
        bg.add(cGruppe);
        add(cGruppe);
        initButton(dGruppe,tooltipFrom, GroupTeam.TEAMSMILIES[4]);
        constraints.gridx = 4;
        constraints.gridy = 0;
        layout.setConstraints(dGruppe, constraints);
        bg.add(dGruppe);
        add(dGruppe);
        initButton(eGruppe,tooltipFrom, GroupTeam.TEAMSMILIES[5]);
        constraints.gridx = 5;
        constraints.gridy = 0;
        layout.setConstraints(eGruppe, constraints);
        bg.add(eGruppe);
        add(eGruppe);
        initButton(fGruppe,tooltipFrom, GroupTeam.TEAMSMILIES[6]);
        constraints.gridx = 6;
        constraints.gridy = 0;
        layout.setConstraints(fGruppe, constraints);
        bg.add(fGruppe);
        add(fGruppe);

        final ButtonGroup bg2 = new ButtonGroup();

        initButton(noGruppe2,tooltipTo, GroupTeam.NO_TEAM);
        constraints.gridx = 0;
        constraints.gridy = 1;
        layout.setConstraints(noGruppe2, constraints);
        bg2.add(noGruppe2);
        add(noGruppe2);
        initButton(aGruppe2,tooltipTo, GroupTeam.TEAMSMILIES[1]);
        constraints.gridx = 1;
        constraints.gridy = 1;
        layout.setConstraints(aGruppe2, constraints);
        bg2.add(aGruppe2);
        add(aGruppe2);
        initButton(bGruppe2,tooltipTo, GroupTeam.TEAMSMILIES[2]);
        constraints.gridx = 2;
        constraints.gridy = 1;
        layout.setConstraints(bGruppe2, constraints);
        bg2.add(bGruppe2);
        add(bGruppe2);
        initButton(cGruppe2,tooltipTo, GroupTeam.TEAMSMILIES[3]);
        constraints.gridx = 3;
        constraints.gridy = 1;
        layout.setConstraints(cGruppe2, constraints);
        bg2.add(cGruppe2);
        add(cGruppe2);
        initButton(dGruppe2,tooltipTo, GroupTeam.TEAMSMILIES[4]);
        constraints.gridx = 4;
        constraints.gridy = 1;
        layout.setConstraints(dGruppe2, constraints);
        bg2.add(dGruppe2);
        add(dGruppe2);
        initButton(eGruppe2,tooltipTo, GroupTeam.TEAMSMILIES[5]);
        constraints.gridx = 5;
        constraints.gridy = 1;
        layout.setConstraints(eGruppe2, constraints);
        bg2.add(eGruppe2);
        add(eGruppe2);
        initButton(fGruppe2,tooltipTo, GroupTeam.TEAMSMILIES[6]);
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

        m_jbDrucken.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Spieler_drucken"));
        m_jbDrucken.setPreferredSize(new Dimension(28, 28));
        m_jbDrucken.addActionListener(this);
        constraints.gridx = 8;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        layout.setConstraints(m_jbDrucken, constraints);
        add(m_jbDrucken);
    }

    private void initButton(JToggleButton button,String tooltip,String key){
    	button.setToolTipText(tooltip);
    	button.setPreferredSize(new Dimension(16, 16));
        button.setSelectedIcon(GroupTeam.getGroupIcon(key));
    	button.addActionListener(this);
    }

	/**
	 * Tauscht eine Farbe im Image durch eine andere
	 *
	 */
	private Image makeGray(Icon im, float value) {
	    Image image = ImageUtilities.iconToImage(im);

	    final ImageProducer ip = new FilteredImageSource(image.getSource(), new LightGrayFilter(value));
	    return Toolkit.getDefaultToolkit().createImage(ip);
	}
}
