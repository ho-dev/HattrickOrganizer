// %3513105810:hoplugins.trainingExperience.ui.model%
package module.training.ui.model;

import core.constants.player.PlayerSkill;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.player.Player;
import core.training.FutureTrainingManager;
import core.training.WeeklyTrainingType;
import core.util.HODateTime;
import core.util.Helper;
import module.training.Skills;
import module.training.ui.comp.PlayerNameCell;
import module.training.ui.comp.TrainingPriorityCell;
import module.training.ui.comp.VerticalIndicator;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table Model for the main table showing training results
 *
 * @author Mag. Bernhard Hödl AH - Solutions Augsten & Hödl OEG Neubachgasse 12
 * A - 2325 Himberg Tabellenmodel und Daten für die dargestellte Tabelle
 * für das HO Plugin
 */
public class OutputTableModel extends AbstractTableModel {

    // common column of fixed and scrolled tables
    private static final int COL_PLAYER_ID = 11;
    private List<FutureTrainingManager> data = new ArrayList<>();
    private final TrainingModel model;

    /**
     * Constructor
     *
     * @param model the training model
     */
    public OutputTableModel(TrainingModel model) {
        this.model = model;
    }

    public int getPlayerIdColumn() {
        return COL_PLAYER_ID;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case COL_PLAYER_ID -> String.class;
            case 0 -> PlayerNameCell.class;
            case 1 -> String.class;
            case 2 -> TrainingPriorityCell.class;
            case 3, 4, 5, 6, 7, 8, 9, 10 -> VerticalIndicator.class;
            default -> super.getColumnClass(columnIndex);
        };
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return 12;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int columnIndex) {
        return switch (columnIndex) {
            case COL_PLAYER_ID -> TranslationFacility.tr("ls.player.id");
            case 0 -> TranslationFacility.tr("Spieler");
            case 1 -> TranslationFacility.tr("ls.player.age");
            case 2 -> TranslationFacility.tr("trainpre.priority");
            case 3 -> TranslationFacility.tr("ls.player.skill.keeper");
            case 4 -> TranslationFacility.tr("ls.player.skill.defending");
            case 5 -> TranslationFacility.tr("ls.player.skill.playmaking");
            case 6 -> TranslationFacility.tr("ls.player.skill.passing");
            case 7 -> TranslationFacility.tr("ls.player.skill.winger");
            case 8 -> TranslationFacility.tr("ls.player.skill.scoring");
            case 9 -> TranslationFacility.tr("ls.player.skill.setpieces");
            case 10 -> TranslationFacility.tr("ls.player.skill.stamina");
            default -> "";
        };
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        return (this.data != null) ? data.size() : 0;
    }

    /**
     * Returns as toolTip for the cell, the last skillup for the proper player
     * and skill
     *
     * @param rowIndex    row
     * @param columnIndex column
     * @return toolTip
     */
    public String getToolTipAt(int rowIndex, int columnIndex) {
        var val = getValueAt(rowIndex, columnIndex);
        return switch (columnIndex) {
            case 0 -> ((JLabel) val).getToolTipText();
            case 1, 2 -> val.toString();
            case 3, 4, 5, 6, 7, 8, 9, 10 -> ((VerticalIndicator) val).getToolTipText();
            default -> "";
        };
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var ftm = data.get(rowIndex);
        var player = ftm.getPlayer();
        return switch (columnIndex) {
            case COL_PLAYER_ID -> Integer.toString(player.getPlayerId());
            case 0 -> createPlayerNameCell(player, ftm.getTrainingSpeed());
            case 1 -> player.getAgeWithDaysAsString();
            case 2 -> createBestPositionCell(player);
            case 3 -> createIcon(player, PlayerSkill.KEEPER);
            case 4 -> createIcon(player, PlayerSkill.DEFENDING);
            case 5 -> createIcon(player, PlayerSkill.PLAYMAKING);
            case 6 -> createIcon(player, PlayerSkill.PASSING);
            case 7 -> createIcon(player, PlayerSkill.WINGER);
            case 8 -> createIcon(player, PlayerSkill.SCORING);
            case 9 -> createIcon(player, PlayerSkill.SETPIECES);
            case 10 -> createIcon(player, PlayerSkill.STAMINA);
            default -> "";
        };
    }

    /**
     * Refill the table with the new training based on the last changes
     */
    public void fillWithData() {
        this.data = new ArrayList<>();
        for (var player : HOVerwaltung.instance().getModel().getCurrentPlayers()) {
            this.data.add(new FutureTrainingManager(player, this.model.getFutureTrainings()));
        }
        fireTableDataChanged();
    }

    /**
     * Get the training length for a player in a specific skill
     *
     * @param player     player to be considered
     * @param skillIndex skill trained
     * @return predicted training length
     */
    private double getTrainingLength(Player player, PlayerSkill skillIndex) {
        WeeklyTrainingType wt = WeeklyTrainingType.instance(Skills.getTrainingTypeForSkill(skillIndex));
        if (wt != null) {
            var model = HOVerwaltung.instance().getModel();
            return 1 / wt.calculateSkillIncreaseOfTrainingWeek(
                    player.getValue4Skill(skillIndex),
                    model.getTrainer().getCoachSkill(),
                    model.getClub().getCoTrainer(),
                    model.getTeam().getTrainingslevel(),
                    model.getTeam().getStaminaTrainingPart(),
                    player.getAge(),
                    90, 0, 0, 0);
        }
        return 0;
    }

    /**
     * Create a VerticalIndicator object
     *
     * @param player     object from which create the indicator
     * @param skillIndex points to skillup
     * @return the VerticalIndicator object
     */
    private VerticalIndicator createIcon(Player player, PlayerSkill skillIndex) {
        double trainingLength = getTrainingLength(player, skillIndex);
        double point = trainingLength * player.getSub4Skill(skillIndex);
        return new VerticalIndicator(Helper.round(point, 1), Helper.round(
                trainingLength, 1));
    }

    private PlayerNameCell createPlayerNameCell(Player player, int speed) {
        return new PlayerNameCell(player, speed);
    }

    private TrainingPriorityCell createBestPositionCell(Player player) {
        var firstTrainingDate = model.getFutureTrainings().isEmpty() ?
                HODateTime.now() :
                model.getFutureTrainings().get(0).getTrainingDate();
        return new TrainingPriorityCell(player, firstTrainingDate);
    }
}
