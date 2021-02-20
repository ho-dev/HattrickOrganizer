// %3513105810:hoplugins.trainingExperience.ui.model%
package module.training.ui.model;

import core.constants.player.PlayerSkill;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.training.FutureTrainingManager;
import core.training.HattrickDate;
import core.training.WeeklyTrainingType;
import core.util.Helper;
import module.training.Skills;
import module.training.ui.comp.TrainingPriorityCell;
import module.training.ui.comp.PlayerNameCell;
import module.training.ui.comp.VerticalIndicator;

import java.util.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

/**
 * Table Model for the main table showing training results
 *
 * @author Mag. Bernhard Hödl AH - Solutions Augsten & Hödl OEG Neubachgasse 12
 * A - 2325 Himberg Tabellenmodel und Daten für die dargestellte Tabelle
 * für das HO Plugin
 */
public class OutputTableModel extends AbstractTableModel {

    public final static int COL_PLAYER_ID = 11;
    private static final long serialVersionUID = -1695207352334612268L;
    private List<Player> data = new ArrayList<>();
    private final TrainingModel model;

    /**
     * Constructor
     *
     * @param model the training model
     */
    public OutputTableModel(TrainingModel model) {
        this.model = model;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> PlayerNameCell.class;
            case 1, 2 -> TrainingPriorityCell.class;
            case COL_PLAYER_ID -> String.class;
            case 3, 4, 5, 6, 7, 8, 9, 10 -> VerticalIndicator.class;
            case 12 -> Integer.class;
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
        return 13;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> HOVerwaltung.instance().getLanguageString("Spieler");
            case 1 -> HOVerwaltung.instance().getLanguageString("ls.player.age");
            case 2 -> HOVerwaltung.instance().getLanguageString("trainpre.priority");
            case 3 -> HOVerwaltung.instance().getLanguageString("ls.player.skill.keeper");
            case 4 -> HOVerwaltung.instance().getLanguageString("ls.player.skill.defending");
            case 5 -> HOVerwaltung.instance().getLanguageString("ls.player.skill.playmaking");
            case 6 -> HOVerwaltung.instance().getLanguageString("ls.player.skill.passing");
            case 7 -> HOVerwaltung.instance().getLanguageString("ls.player.skill.winger");
            case 8 -> HOVerwaltung.instance().getLanguageString("ls.player.skill.scoring");
            case 9 -> HOVerwaltung.instance().getLanguageString("ls.player.skill.setpieces");
            case 10 -> HOVerwaltung.instance().getLanguageString("ls.player.skill.stamina");
            case COL_PLAYER_ID -> HOVerwaltung.instance().getLanguageString("ls.player.id");
            case 12 -> "speed";
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
     * @param rowIndex
     * @param columnIndex
     * @return toolTip
     */
    public Object getToolTipAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return ((JLabel) getValueAt(rowIndex, columnIndex)).getToolTipText();
        } else
            return ((VerticalIndicator) getValueAt(rowIndex, columnIndex)).getToolTipText();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Player player = data.get(rowIndex);

        FutureTrainingManager ftm = new FutureTrainingManager(player, this.model.getFutureTrainings());

        // Spielername
        // Spieleralter
        // Beste Postion
        return switch (columnIndex) {
            case 0 -> createPlayerNameCell(player, ftm.getTrainingSpeed());
            case 1 -> player.getAlterWithAgeDaysAsString();
            case 2 -> createBestPositionCell(player);
            case 3 -> createIcon(player, PlayerSkill.KEEPER);
            case 4 -> createIcon(player, PlayerSkill.DEFENDING);
            case 5 -> createIcon(player, PlayerSkill.PLAYMAKING);
            case 6 -> createIcon(player, PlayerSkill.PASSING);
            case 7 -> createIcon(player, PlayerSkill.WINGER);
            case 8 -> createIcon(player, PlayerSkill.SCORING);
            case 9 -> createIcon(player, PlayerSkill.SET_PIECES);
            case 10 -> createIcon(player, PlayerSkill.STAMINA);
            case COL_PLAYER_ID -> Integer.toString(player.getPlayerID());
            case 12 -> ftm.getTrainingSpeed();
            default -> "";
        };
    }

    /**
     * Refill the table with the new training based on the last changes
     */
    public void fillWithData() {
        this.data = new ArrayList<>(HOVerwaltung.instance().getModel().getCurrentPlayers());
        fireTableDataChanged();
    }

    /**
     * Get the training length for a player in a specific skill
     *
     * @param player     player to be considered
     * @param skillIndex skill trained
     * @return predicted training length
     */
    private double getTrainingLength(Player player, int skillIndex) {
        double dReturn = 0;
        WeeklyTrainingType wt = WeeklyTrainingType.instance(Skills.getTrainedSkillCode(skillIndex));
        if (wt != null) {
            dReturn = wt.getTrainingLength(player,
                    this.model.getTrainerLevel(),
                    HOVerwaltung.instance().getModel().getTeam().getTrainingslevel(),
                    HOVerwaltung.instance().getModel().getTeam().getStaminaTrainingPart(),
                    HOVerwaltung.instance().getModel().getClub().getCoTrainer());
        }
        return dReturn;
    }

    /**
     * Method that returns the offset in Training point
     *
     * @param player player to be considered
     * @param skill  skill trained
     * @return training point offset, if any
     */
    private double getOffset(Player player, int skill) {
        double offset = player.getSub4Skill(skill);
        double length = getTrainingLength(player, skill);
        return offset * length;
    }

    /**
     * Create a VerticalIndicator object
     *
     * @param player     object from which create the indicator
     * @param skillIndex points to skillup
     * @return the VerticalIndicator object
     */
    private VerticalIndicator createIcon(Player player, int skillIndex) {
        double point = getOffset(player, skillIndex);
        double trainingLength = getTrainingLength(player, skillIndex);
        return new VerticalIndicator(Helper.round(point, 1), Helper.round(
                trainingLength, 1));
    }

    private PlayerNameCell createPlayerNameCell(Player player, int speed) {
        return new PlayerNameCell(player, speed);
    }

    private TrainingPriorityCell createBestPositionCell(Player player) {
        return new TrainingPriorityCell(player, HattrickDate.fromInstant( model.getFutureTrainings().get(0).getTrainingDate()));
    }

}
