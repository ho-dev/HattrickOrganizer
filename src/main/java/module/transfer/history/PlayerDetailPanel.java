package module.transfer.history;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.model.UserColumnController;
import core.gui.theme.ImageUtilities;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.player.Player;
import core.util.HODateTime;
import core.util.Helper;
import module.transfer.PlayerRetriever;
import module.transfer.PlayerTransfer;
import module.transfer.XMLParser;
import module.transfer.ui.layout.TableLayout;
import module.transfer.ui.layout.TableLayoutConstants;
import module.transfer.ui.sorter.DefaultTableSorter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.util.List;
import java.util.Vector;

import static core.util.CurrencyUtils.convertCurrency;

/**
 * Panel for showing detailed information on a player.
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
public class PlayerDetailPanel extends JPanel implements ActionListener {
    @Serial
    private static final long serialVersionUID = -6855218725568752692L;
    private static final String SKILL_PLAYMAKING = TranslationFacility.tr("ls.player.skill.playmaking");
    private static final String SKILL_PASSING = TranslationFacility.tr("ls.player.skill.passing");
    private static final String SKILL_WING = TranslationFacility.tr("ls.player.skill.winger");
    private static final String SKILL_DEFENSE = TranslationFacility.tr("ls.player.skill.defending");
    private static final String SKILL_SCORING = TranslationFacility.tr("ls.player.skill.scoring");
    private static final String SKILL_SETPIECES = TranslationFacility.tr("ls.player.skill.setpieces");
    private static final String SKILL_STAMINA = TranslationFacility.tr("ls.player.skill.stamina");
    private static final String SKILL_KEEPER = TranslationFacility.tr("ls.player.skill.keeper");
    private static final String SKILL_EXPERIENCE = TranslationFacility.tr("ls.player.experience");

    private Player player;
    private final JButton updBtn = new JButton();
    private final JLabel age = new JLabel("", SwingConstants.LEFT); //$NON-NLS-1$
    private final JLabel lengthOfStayInTeam = new JLabel("", SwingConstants.LEFT); //$NON-NLS-1$
    private final JLabel totalCostOfOwnership = new JLabel("", SwingConstants.LEFT); //$NON-NLS-1$
    private final JLabel sumOfWage = new JLabel("", SwingConstants.LEFT);
    private final JLabel currTSI = new JLabel(TranslationFacility.tr("PlayerDetail.NotAvail"),
            SwingConstants.LEFT);
    private final JLabel income = new JLabel("", SwingConstants.LEFT); //$NON-NLS-1$
    private final JLabel name = new JLabel("", SwingConstants.LEFT); //$NON-NLS-1$
    private final JLabel fired = new JLabel(TranslationFacility.tr("FiredPlayer"), SwingConstants.LEFT);

    private final JLabel skill_defense = new JLabel("", SwingConstants.LEFT);
    private final JLabel skill_experience = new JLabel("", SwingConstants.LEFT);
    private final JLabel skill_keeper = new JLabel("", SwingConstants.LEFT);
    private final JLabel skill_passing = new JLabel("", SwingConstants.LEFT);
    private final JLabel skill_playmaking = new JLabel("", SwingConstants.LEFT);
    private final JLabel skill_scoring = new JLabel("", SwingConstants.LEFT);
    private final JLabel skill_setpieces = new JLabel("", SwingConstants.LEFT);
    private final JLabel skill_stamina = new JLabel("", SwingConstants.LEFT);
    private final JLabel skill_wing = new JLabel("", SwingConstants.LEFT);

    private final JLabel arrow_defense = new JLabel((Icon) null, SwingConstants.CENTER);
    private final JLabel arrow_experience = new JLabel((Icon) null, SwingConstants.CENTER);
    private final JLabel arrow_keeper = new JLabel((Icon) null, SwingConstants.CENTER);
    private final JLabel arrow_passing = new JLabel((Icon) null, SwingConstants.CENTER);
    private final JLabel arrow_playmaking = new JLabel((Icon) null, SwingConstants.CENTER);
    private final JLabel arrow_scoring = new JLabel((Icon) null, SwingConstants.CENTER);
    private final JLabel arrow_setpieces = new JLabel((Icon) null, SwingConstants.CENTER);
    private final JLabel arrow_stamina = new JLabel((Icon) null, SwingConstants.CENTER);
    private final JLabel arrow_wing = new JLabel((Icon) null, SwingConstants.CENTER);
    private final JTable playerTable;
    private String playerName;
    private int playerId;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a PlayerDetailPanel.
     */
    public PlayerDetailPanel() {
        super(new BorderLayout());

        var model = getTableModel();
        var sorter = new DefaultTableSorter(model);
        playerTable = new JTable(sorter);
        playerTable.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
        playerTable.setOpaque(true);
        sorter.setTableHeader(playerTable.getTableHeader());

        model.restoreUserSettings(playerTable);

        final JScrollPane playerPane = new JScrollPane(playerTable);
        playerPane.setOpaque(false);
        add(playerPane, BorderLayout.CENTER);

        final double[][] sizes = {
                {10, 95, 150, 20, 100, 75, 100, TableLayoutConstants.FILL, 30, 110, 30, 110, 30, 110, 30, 120, 10},
                {20, 20, 20}
        };

        final JPanel detailPanel = new ImagePanel();
        detailPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                TranslationFacility.tr("SpielerDetails"))); //$NON-NLS-1$
        detailPanel.setOpaque(false);

        final TableLayout layout = new TableLayout(sizes);
        detailPanel.setLayout(layout);

        detailPanel.add(createPlayerDetailLabel("ls.player.name"), "1, 0"); //$NON-NLS-1$
        detailPanel.add(name, "2, 0"); //$NON-NLS-1$
        detailPanel.add(createPlayerDetailLabel("ls.player.age"), "1, 1"); //$NON-NLS-1$
        detailPanel.add(createPlayerDetailLabel("ls.player.lengthofstayinteam"), "4, 1"); //$NON-NLS-1$
        detailPanel.add(createPlayerDetailLabel("ls.player.sumofwage"), "4, 2"); //$NON-NLS-1$
        detailPanel.add(createPlayerDetailLabel("ls.player.toc"), "6, 2"); //$NON-NLS-1$
        detailPanel.add(age, "2, 1"); //$NON-NLS-1$
        detailPanel.add(lengthOfStayInTeam, "5, 1");
        detailPanel.add(sumOfWage, "5, 2");
        detailPanel.add(totalCostOfOwnership, "7, 2");
        detailPanel.add(createPlayerDetailLabel("Income"), "1, 2"); //$NON-NLS-1$ //$NON-NLS-2$
        detailPanel.add(income, "2, 2"); //$NON-NLS-1$

        detailPanel.add(createPlayerDetailLabel("PlayerDetail.CurrentTSI"), "4, 0");
        detailPanel.add(currTSI, "5, 0");

        fired.setVisible(false);
        detailPanel.add(fired, "6, 1");

        detailPanel.add(arrow_scoring, "8, 0"); //$NON-NLS-1$
        detailPanel.add(skill_scoring, "9, 0"); //$NON-NLS-1$
        detailPanel.add(arrow_playmaking, "8, 1"); //$NON-NLS-1$
        detailPanel.add(skill_playmaking, "9, 1"); //$NON-NLS-1$
        detailPanel.add(arrow_defense, "8, 2"); //$NON-NLS-1$
        detailPanel.add(skill_defense, "9, 2"); //$NON-NLS-1$

        detailPanel.add(arrow_wing, "10, 0"); //$NON-NLS-1$
        detailPanel.add(skill_wing, "11, 0"); //$NON-NLS-1$
        detailPanel.add(arrow_passing, "10, 1"); //$NON-NLS-1$
        detailPanel.add(skill_passing, "11, 1"); //$NON-NLS-1$
        detailPanel.add(arrow_stamina, "10, 2"); //$NON-NLS-1$
        detailPanel.add(skill_stamina, "11, 2"); //$NON-NLS-1$

        detailPanel.add(arrow_keeper, "12, 0"); //$NON-NLS-1$
        detailPanel.add(skill_keeper, "13, 0"); //$NON-NLS-1$
        detailPanel.add(arrow_setpieces, "12, 1"); //$NON-NLS-1$
        detailPanel.add(skill_setpieces, "13, 1"); //$NON-NLS-1$
        detailPanel.add(arrow_experience, "12, 2"); //$NON-NLS-1$
        detailPanel.add(skill_experience, "13, 2"); //$NON-NLS-1$

        updBtn.setEnabled(false);
        updBtn.setText(TranslationFacility.tr("ls.button.update"));
        updBtn.setToolTipText(TranslationFacility.tr("UpdTooltip"));
        updBtn.addActionListener(this);
        updBtn.setFocusable(false);

        detailPanel.add(updBtn, "15, 0, 15, 1");
        add(new JScrollPane(detailPanel), BorderLayout.NORTH);
        setOpaque(false);

        clearPanel();
    }

    private JLabel createPlayerDetailLabel(String s) {
        var languageString = TranslationFacility.tr(s);
        var ret = new JLabel(languageString, SwingConstants.LEFT);
        ret.setToolTipText(languageString);
        return ret;
    }

    private PlayerTransferTableModel getTableModel() {
        return UserColumnController.instance().getPlayerTransferTableModel();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Sets the player to display information for.
     *
     * @param playerid   Player id
     * @param playerName Player Name
     */
    public final void setPlayer(int playerid, String playerName) {
        this.player = PlayerRetriever.getPlayer(playerid);

        if (this.player != null) {
            this.playerId = player.getPlayerId();
            this.playerName = player.getFullName();
        } else {
            this.playerId = playerid;
            this.playerName = playerName;
        }

        clearPanel();
        updatePanel();
    }

    public final void setPlayer(PlayerTransfer transfer) {
        this.player = transfer.getPlayerInfo();

        this.playerId = player.getPlayerId();
        this.playerName = player.getFullName();

        clearPanel();
        updatePanel();
    }

    /**
     * {@inheritDoc}
     */
    public final void actionPerformed(ActionEvent e) {
        if (this.playerId > 0) {
            XMLParser.updatePlayerTransfers(this.playerId);
            updatePanel();
        }
    }

    /**
     * Clears all information on the panel.
     */
    public final void clearPanel() {
        updBtn.setEnabled(false);
        fired.setVisible(false);
        name.setText(""); //$NON-NLS-1$
        age.setText(""); //$NON-NLS-1$
        lengthOfStayInTeam.setText("");
        totalCostOfOwnership.setText("");
        income.setText("");
        currTSI.setText(TranslationFacility.tr("PlayerDetail.NotAvail"));

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
        refreshPlayerTable(new Vector<>());
    }

    /**
     * Refreshes the table with player transfer information.
     *
     * @param values List of player transfers to display.
     */
    private void refreshPlayerTable(List<PlayerTransfer> values) {
        var model = getTableModel();
        model.setValues(values);
        playerTable.getColumnModel().getColumn(4).setCellRenderer(new IconCellRenderer());
        playerTable.getColumnModel().getColumn(4).setMaxWidth(20);
    }

    /**
     * Update the detail panel.
     */
    private void updatePanel() {
        if (playerId > 0) {
            var playerNotes = DBManager.instance().loadPlayerNotes(playerId);
            if (playerNotes.isFired()) {
                fired.setVisible(true);
                updBtn.setEnabled(false);
            } else {
                fired.setVisible(false);
                updBtn.setEnabled(true);
            }

            name.setText(this.playerName);

            HODateTime arrivalDate = null;
            if (player != null) {
                arrivalDate = player.getArrivalDate();
                String ageText = "";
                var hrfDate = this.player.getHrfDate();
                if (hrfDate != null) {
                    ageText = this.player.getAgeAtDate(this.player.getHrfDate()).toString();
                }
                age.setText(ageText);

                if (!player.isGoner()) {
                    currTSI.setText(Integer.toString(this.player.getTsi()));
                }

                skill_keeper.setText(SKILL_KEEPER + " (" + player.getGoalkeeperSkill() + ")");
                skill_playmaking.setText(SKILL_PLAYMAKING + " (" + player.getPlaymakingSkill() + ")");
                skill_passing.setText(SKILL_PASSING + " (" + player.getPassingSkill() + ")");
                skill_wing.setText(SKILL_WING + " (" + player.getWingerSkill() + ")");
                skill_defense.setText(SKILL_DEFENSE + " (" + player.getDefendingSkill() + ")");
                skill_scoring.setText(SKILL_SCORING + " (" + player.getScoringSkill() + ")");
                skill_setpieces.setText(SKILL_SETPIECES + " (" + player.getSetPiecesSkill() + ")");
                skill_stamina.setText(SKILL_STAMINA + " (" + player.getStamina() + ")");
                skill_experience.setText(SKILL_EXPERIENCE + " (" + player.getExperience() + ")");


                arrow_keeper.setIcon(ImageUtilities.getImageIcon4Change(player.getAllLevelUp(PlayerSkill.KEEPER).size(), true));
                arrow_playmaking.setIcon(ImageUtilities.getImageIcon4Change(player.getAllLevelUp(PlayerSkill.PLAYMAKING).size(), true));
                arrow_passing.setIcon(ImageUtilities.getImageIcon4Change(player.getAllLevelUp(PlayerSkill.PASSING).size(), true));
                arrow_wing.setIcon(ImageUtilities.getImageIcon4Change(player.getAllLevelUp(PlayerSkill.WINGER).size(), true));
                arrow_defense.setIcon(ImageUtilities.getImageIcon4Change(player.getAllLevelUp(PlayerSkill.DEFENDING).size(), true));
                arrow_scoring.setIcon(ImageUtilities.getImageIcon4Change(player.getAllLevelUp(PlayerSkill.SCORING).size(), true));
                arrow_setpieces.setIcon(ImageUtilities.getImageIcon4Change(player.getAllLevelUp(PlayerSkill.SETPIECES).size(), true));
                arrow_stamina.setIcon(ImageUtilities.getImageIcon4Change(player.getAllLevelUp(PlayerSkill.STAMINA).size(), true));
                arrow_experience.setIcon(ImageUtilities.getImageIcon4Change(player.getAllLevelUp(PlayerSkill.EXPERIENCE).size(), true));
            }

            final List<PlayerTransfer> transfers = DBManager.instance().getTransfers(this.playerId, true);
            int valIncome = 0;
            HODateTime soldDate = null;
            final int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
            for (final PlayerTransfer transfer : transfers) {
                if (transfer.getBuyerid() == teamid) {
                    valIncome -= transfer.getPrice();
                    if (arrivalDate == null || transfer.getDate().isAfter(arrivalDate)) {
                        arrivalDate = transfer.getDate();
                    }
                }

                if (transfer.getSellerid() == teamid) {
                    valIncome += transfer.getPrice();
                    if (soldDate == null || transfer.getDate().isAfter(soldDate)) {
                        soldDate = transfer.getDate();
                    }
                }
            }

            income.setText(core.util.Helper.getNumberFormat(true, 0).format(convertCurrency(valIncome)));
            lengthOfStayInTeam.setText("");
            sumOfWage.setText("");
            if (arrivalDate != null) {
                HODateTime to = soldDate;
                if (to == null) {
                    var latestPlayerInfo = this.player.getLatestPlayerInfo();
                    if (latestPlayerInfo != null) {
                        to = latestPlayerInfo.getHrfDate();
                    } else {
                        to = HODateTime.now();
                    }
                }

                var activeDuration = HODateTime.HODuration.between(arrivalDate, to);
                if (activeDuration.seasons >= 0) {
                    lengthOfStayInTeam.setText(activeDuration.toString());
                }
                var sum = this.player.getSumOfWage(arrivalDate, to);
                sumOfWage.setText(Helper.getNumberFormat(true, 0).format(convertCurrency(sum)));
                totalCostOfOwnership.setText(Helper.getNumberFormat(true, 0).format(convertCurrency(sum - valIncome)));
            }

            refreshPlayerTable(transfers);
        }
    }

    public void storeUserSettings() {
        var model = getTableModel();
        model.storeUserSettings(this.playerTable);
    }
}
