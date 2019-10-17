// %3513105810:hoplugins.trainingExperience.ui.model%
package module.training.ui.model;

import core.constants.player.PlayerSkill;
import core.model.HOVerwaltung;
import core.model.player.ISkillup;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.training.FutureTrainingManager;
import core.training.WeeklyTrainingType;
import core.util.Helper;
import module.training.Skills;
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
    private List<Player> data = new ArrayList<Player>();
    private final TrainingModel model;

    /**
     * Constructor
     *
     * @param model
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
        switch (columnIndex) {
            case 0:
                return JLabel.class;
            case 1:
            case 2:
            case COL_PLAYER_ID:
                return String.class;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                return VerticalIndicator.class;
            default:
                return super.getColumnClass(columnIndex);
        }

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
        switch (columnIndex) {
            case 0:
                return HOVerwaltung.instance().getLanguageString("Spieler");
            case 1:
                return HOVerwaltung.instance().getLanguageString("ls.player.age");
            case 2:
                return HOVerwaltung.instance().getLanguageString("BestePosition");
            case 3:
                return HOVerwaltung.instance().getLanguageString("ls.player.skill.keeper");
            case 4:
                return HOVerwaltung.instance().getLanguageString("ls.player.skill.defending");
            case 5:
                return HOVerwaltung.instance().getLanguageString("ls.player.skill.playmaking");
            case 6:
                return HOVerwaltung.instance().getLanguageString("ls.player.skill.passing");
            case 7:
                return HOVerwaltung.instance().getLanguageString("ls.player.skill.winger");
            case 8:
                return HOVerwaltung.instance().getLanguageString("ls.player.skill.scoring");
            case 9:
                return HOVerwaltung.instance().getLanguageString("ls.player.skill.setpieces");
            case 10:
                return HOVerwaltung.instance().getLanguageString("ls.player.skill.stamina");
            case COL_PLAYER_ID:
                return HOVerwaltung.instance().getLanguageString("ls.player.id");
            case 12:
                return "speed";
            default:
                return "";
        }
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

        FutureTrainingManager ftm = new FutureTrainingManager(player,
                this.model.getFutureTrainings(), 0,
                this.model.getTrainerLevel(), this.model.getAssistants());

        switch (columnIndex) {
            case 0:
                // Spielername
                JLabel jl_Name = new JLabel();
                jl_Name.setText(player.getName());
                return jl_Name;
            case 1:
                // Spieleralter
                return player.getAlterWithAgeDaysAsString();
            case 2:
                // Beste Postion
                return MatchRoleID.getNameForPosition(player.getIdealPosition()) + " ("
                        + player.getIdealPosStaerke(true) + ")";
            case 3:
                return createIcon(player, PlayerSkill.KEEPER);
            case 4:
                return createIcon(player, PlayerSkill.DEFENDING);
            case 5:
                return createIcon(player, PlayerSkill.PLAYMAKING);
            case 6:
                return createIcon(player, PlayerSkill.PASSING);
            case 7:
                return createIcon(player, PlayerSkill.WINGER);
            case 8:
                return createIcon(player, PlayerSkill.SCORING);
            case 9:
                return createIcon(player, PlayerSkill.SET_PIECES);
            case 10:
                return createIcon(player, PlayerSkill.STAMINA);
            case COL_PLAYER_ID:
                return Integer.toString(player.getSpielerID());
            case 12:
                return Integer.toString(ftm.getTrainingSpeed());
            default:
                return "";
        }
    }

    /**
     * Refill the table with the new training based on the last changes
     */
    public void fillWithData() {
        this.data = new ArrayList<Player>(HOVerwaltung.instance().getModel().getAllSpieler());
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
            dReturn = wt.getTrainingLength(player, this.model.getNumberOfCoTrainers(),
                    this.model.getTrainerLevel(), HOVerwaltung.instance().getModel().getTeam()
                            .getTrainingslevel(), HOVerwaltung.instance().getModel().getTeam()
                            .getStaminaTrainingPart(), HOVerwaltung.instance().getModel().getStaff());
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
        double offset = player.getSubskill4Pos(skill);
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

        VerticalIndicator vi = new VerticalIndicator(Helper.round(point, 1), Helper.round(
                trainingLength, 1));

        return vi;
    }
}
