// %1126721330604:hoplugins.transfers.ui%
package module.transfer.history;



import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.ImageUtilities;
import core.model.HOVerwaltung;
import core.model.player.Spieler;
import module.transfer.PlayerRetriever;
import module.transfer.PlayerTransfer;
import module.transfer.ui.layout.TableLayout;
import module.transfer.ui.layout.TableLayoutConstants;
import module.transfer.ui.sorter.DefaultTableSorter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;


/**
 * Panel for showing detailed information on a player.
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
public class PlayerDetailPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -6855218725568752692L;
	//~ Instance fields ----------------------------------------------------------------------------
	private static final HOVerwaltung hov = HOVerwaltung.instance();
    private static final String SKILL_PLAYMAKING = hov.getLanguageString("ls.player.skill.playmaking");
    private static final String SKILL_PASSING = hov.getLanguageString("ls.player.skill.passing");
    private static final String SKILL_WING = hov.getLanguageString("ls.player.skill.winger");
    private static final String SKILL_DEFENSE = hov.getLanguageString("ls.player.skill.defending");
    private static final String SKILL_SCORING = hov.getLanguageString("ls.player.skill.scoring");
    private static final String SKILL_SETPIECES = hov.getLanguageString("ls.player.skill.setpieces");
    private static final String SKILL_STAMINA = hov.getLanguageString("ls.player.skill.stamina");
    private static final String SKILL_KEEPER = hov.getLanguageString("ls.player.skill.keeper");
    private static final String SKILL_EXPERIENCE = hov.getLanguageString("ls.player.experience");

    private Spieler player;
    private JButton updBtn = new JButton();
    private JLabel age = new JLabel("", SwingConstants.LEFT); //$NON-NLS-1$
    private JLabel currTSI = new JLabel(HOVerwaltung.instance().getLanguageString("PlayerDetail.NotAvail"),
                                        SwingConstants.LEFT);
    private JLabel income = new JLabel("", SwingConstants.LEFT); //$NON-NLS-1$
    private JLabel name = new JLabel("", SwingConstants.LEFT); //$NON-NLS-1$

    private JLabel skill_defense = new JLabel("", SwingConstants.LEFT);
    private JLabel skill_experience = new JLabel("", SwingConstants.LEFT);
    private JLabel skill_keeper = new JLabel("", SwingConstants.LEFT);
    private JLabel skill_passing = new JLabel("", SwingConstants.LEFT);
    private JLabel skill_playmaking = new JLabel("", SwingConstants.LEFT);
    private JLabel skill_scoring = new JLabel("", SwingConstants.LEFT);
    private JLabel skill_setpieces = new JLabel("", SwingConstants.LEFT);
    private JLabel skill_stamina = new JLabel("", SwingConstants.LEFT);
    private JLabel skill_wing = new JLabel("", SwingConstants.LEFT);

    private JLabel arrow_defense = new JLabel((Icon) null, SwingConstants.CENTER);
    private JLabel arrow_experience = new JLabel((Icon) null, SwingConstants.CENTER);
    private JLabel arrow_keeper = new JLabel((Icon) null, SwingConstants.CENTER);
    private JLabel arrow_passing = new JLabel((Icon) null, SwingConstants.CENTER);
    private JLabel arrow_playmaking = new JLabel((Icon) null, SwingConstants.CENTER);
    private JLabel arrow_scoring = new JLabel((Icon) null, SwingConstants.CENTER);
    private JLabel arrow_setpieces = new JLabel((Icon) null, SwingConstants.CENTER);
    private JLabel arrow_stamina = new JLabel((Icon) null, SwingConstants.CENTER);
    private JLabel arrow_wing = new JLabel((Icon) null, SwingConstants.CENTER);
    private JTable playerTable;
    private String playerName;
    private int playerId;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a PlayerDetailPanel.
     */
    public PlayerDetailPanel() {
        super(new BorderLayout());

        final TableModel model = new PlayerTransferTableModel(new ArrayList<PlayerTransfer>());
        final TeamTransferSorter sorter = new TeamTransferSorter(model);
        playerTable = new JTable(sorter);
        sorter.setTableHeader(playerTable.getTableHeader());

        final JScrollPane playerPane = new JScrollPane(playerTable);
        playerPane.setOpaque(false);
        add(playerPane, BorderLayout.CENTER);

        final double[][] sizes = {
                               {
                                   10, 50, 150, 20, 75, 50, TableLayoutConstants.FILL, 30, 100, 30, 100, 30,
                                   100, 50, 100, 10
                               },
                               {20, 20, 20}
                           };

        final JPanel detailPanel = new ImagePanel();
        detailPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                                                               HOVerwaltung.instance().getLanguageString("SpielerDetails"))); //$NON-NLS-1$
        detailPanel.setOpaque(false);

        final TableLayout layout = new TableLayout(sizes);
        detailPanel.setLayout(layout);

        detailPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.name"),
                                   SwingConstants.LEFT), "1, 0"); //$NON-NLS-1$
        detailPanel.add(name, "2, 0"); //$NON-NLS-1$
        detailPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.age"),
                                   SwingConstants.LEFT), "1, 1"); //$NON-NLS-1$
        detailPanel.add(age, "2, 1"); //$NON-NLS-1$
        detailPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("Income"), SwingConstants.LEFT), "1, 2"); //$NON-NLS-1$ //$NON-NLS-2$
        detailPanel.add(income, "2, 2"); //$NON-NLS-1$

        detailPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("PlayerDetail.CurrentTSI")), "4, 0");
        detailPanel.add(currTSI, "5, 0");

        detailPanel.add(arrow_scoring, "7, 0"); //$NON-NLS-1$
        detailPanel.add(skill_scoring, "8, 0"); //$NON-NLS-1$
        detailPanel.add(arrow_playmaking, "7, 1"); //$NON-NLS-1$
        detailPanel.add(skill_playmaking, "8, 1"); //$NON-NLS-1$
        detailPanel.add(arrow_defense, "7, 2"); //$NON-NLS-1$
        detailPanel.add(skill_defense, "8, 2"); //$NON-NLS-1$

        detailPanel.add(arrow_wing, "9, 0"); //$NON-NLS-1$
        detailPanel.add(skill_wing, "10, 0"); //$NON-NLS-1$
        detailPanel.add(arrow_passing, "9, 1"); //$NON-NLS-1$
        detailPanel.add(skill_passing, "10, 1"); //$NON-NLS-1$
        detailPanel.add(arrow_stamina, "9, 2"); //$NON-NLS-1$
        detailPanel.add(skill_stamina, "10, 2"); //$NON-NLS-1$

        detailPanel.add(arrow_keeper, "11, 0"); //$NON-NLS-1$
        detailPanel.add(skill_keeper, "12, 0"); //$NON-NLS-1$
        detailPanel.add(arrow_setpieces, "11, 1"); //$NON-NLS-1$
        detailPanel.add(skill_setpieces, "12, 1"); //$NON-NLS-1$
        detailPanel.add(arrow_experience, "11, 2"); //$NON-NLS-1$
        detailPanel.add(skill_experience, "12, 2"); //$NON-NLS-1$

        updBtn.setEnabled(false);
        updBtn.setText(HOVerwaltung.instance().getLanguageString("ls.button.update"));
        updBtn.setToolTipText(HOVerwaltung.instance().getLanguageString("UpdTooltip"));
        updBtn.addActionListener(this);
        updBtn.setFocusable(false);

        detailPanel.add(updBtn, "14, 0, 14, 1");
        add(new JScrollPane(detailPanel), BorderLayout.NORTH);
        setOpaque(false);

        clearPanel();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Sets the player to display information for.
     *
     * @param playerid Player id
     * @param playerName Player Name
     */
    public final void setPlayer(int playerid, String playerName) {
        this.player = PlayerRetriever.getPlayer(playerid);

        if (this.player != null) {
            this.playerId = player.getSpielerID();
            this.playerName = player.getName();
        } else {
            this.playerId = playerid;
            this.playerName = playerName;
        }

        clearPanel();
        updatePanel();
    }

    /** {@inheritDoc} */
    public final void actionPerformed(ActionEvent e) {
        if (this.playerId > 0) {
        	DBManager.instance().updatePlayerTransfers(this.playerId);
            updatePanel();
        }
    }

    /**
     * Clears all information on the panel.
     */
    public final void clearPanel() {
        updBtn.setEnabled(false);
        name.setText(""); //$NON-NLS-1$
        age.setText(""); //$NON-NLS-1$
        income.setText("");
        currTSI.setText(HOVerwaltung.instance().getLanguageString("PlayerDetail.NotAvail"));

        skill_keeper.setText(SKILL_KEEPER);
        skill_playmaking.setText(SKILL_PLAYMAKING);
        skill_passing.setText(SKILL_PASSING);
        skill_wing.setText(SKILL_WING);
        skill_defense.setText(SKILL_DEFENSE);
        skill_scoring.setText(SKILL_SCORING);
        skill_setpieces.setText(SKILL_SETPIECES);
        skill_stamina.setText(SKILL_STAMINA);
        skill_experience.setText(SKILL_EXPERIENCE);

        arrow_keeper.setIcon(null);
        arrow_playmaking.setIcon(null);
        arrow_passing.setIcon(null);
        arrow_wing.setIcon(null);
        arrow_defense.setIcon(null);
        arrow_scoring.setIcon(null);
        arrow_setpieces.setIcon(null);
        arrow_stamina.setIcon(null);
        arrow_experience.setIcon(null);
        refreshPlayerTable(new Vector<PlayerTransfer>());
    }

    /**
     * Refreshes the table with player transfer information.
     *
     * @param values List of player transfers to display.
     */
    private void refreshPlayerTable(List<PlayerTransfer> values) {
        final DefaultTableSorter sorter = (DefaultTableSorter) playerTable.getModel();
        sorter.setTableModel(new PlayerTransferTableModel(values));
        playerTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        playerTable.getColumnModel().getColumn(4).setCellRenderer(new IconCellRenderer());
        playerTable.getColumnModel().getColumn(4).setMaxWidth(20);
        playerTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        playerTable.getColumnModel().getColumn(8).setCellRenderer(new ButtonCellRenderer());
        playerTable.getColumnModel().getColumn(8).setCellEditor(new ButtonCellEditor(this, values));
    }

    /**
     * Update the detail panel.
     */
    private void updatePanel() {
        if (playerId > 0) {
            updBtn.setEnabled(true);
            name.setText(this.playerName);

            if (player != null) {
                age.setText(Integer.toString(this.player.getAlter()));

                if (!player.isOld()) {
                    currTSI.setText(Integer.toString(this.player.getTSI()));
                }

                skill_keeper.setText(SKILL_KEEPER + " (" + player.getTorwart() + ")");
                skill_playmaking.setText(SKILL_PLAYMAKING + " (" + player.getSpielaufbau() + ")");
                skill_passing.setText(SKILL_PASSING + " (" + player.getPasspiel() + ")");
                skill_wing.setText(SKILL_WING + " (" + player.getFluegelspiel() + ")");
                skill_defense.setText(SKILL_DEFENSE + " (" + player.getVerteidigung() + ")");
                skill_scoring.setText(SKILL_SCORING + " (" + player.getTorschuss() + ")");
                skill_setpieces.setText(SKILL_SETPIECES + " (" + player.getStandards() + ")");
                skill_stamina.setText(SKILL_STAMINA + " (" + player.getKondition() + ")");
                skill_experience.setText(SKILL_EXPERIENCE + " (" + player.getErfahrung() + ")");


                arrow_keeper.setIcon(ImageUtilities.getImageIcon4Veraenderung(player.getAllLevelUp(PlayerSkill.KEEPER).size(),true));
                arrow_playmaking.setIcon(ImageUtilities.getImageIcon4Veraenderung(player.getAllLevelUp(PlayerSkill.PLAYMAKING).size(),true));
                arrow_passing.setIcon(ImageUtilities.getImageIcon4Veraenderung(player.getAllLevelUp(PlayerSkill.PASSING).size(),true));
                arrow_wing.setIcon(ImageUtilities.getImageIcon4Veraenderung(player.getAllLevelUp(PlayerSkill.WINGER).size(),true));
                arrow_defense.setIcon(ImageUtilities.getImageIcon4Veraenderung(player.getAllLevelUp(PlayerSkill.DEFENDING).size(),true));
                arrow_scoring.setIcon(ImageUtilities.getImageIcon4Veraenderung(player.getAllLevelUp(PlayerSkill.SCORING).size(),true));
                arrow_setpieces.setIcon(ImageUtilities.getImageIcon4Veraenderung(player.getAllLevelUp(PlayerSkill.SET_PIECES).size(),true));
                arrow_stamina.setIcon(ImageUtilities.getImageIcon4Veraenderung(player.getAllLevelUp(PlayerSkill.STAMINA).size(),true));
                arrow_experience.setIcon(ImageUtilities.getImageIcon4Veraenderung(player.getAllLevelUp(PlayerSkill.EXPERIENCE).size(),true));
            }

            final List<PlayerTransfer> transfers = DBManager.instance().getTransfers(this.playerId, true);
            int valIncome = 0;
            final int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();

            for (final Iterator<PlayerTransfer> iter = transfers.iterator(); iter.hasNext();) {
                final PlayerTransfer transfer = iter.next();

                if (transfer.getBuyerid() == teamid) {
                    valIncome -= transfer.getPrice();
                }

                if (transfer.getSellerid() == teamid) {
                    valIncome += transfer.getPrice();
                }
            }

            income.setText(HOVerwaltung.instance().getModel().getXtraDaten().getCurrencyName() + " " + valIncome);
            refreshPlayerTable(transfers);
        }
    }
}
