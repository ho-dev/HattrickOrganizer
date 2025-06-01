package module.training.ui;

import core.constants.player.PlayerSkill;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.model.UserColumnController;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.training.FutureTrainingManager;
import core.training.TrainingPreviewPlayers;
import core.training.WeeklyTrainingType;
import core.util.Helper;
import module.training.Skills;
import module.training.ui.comp.VerticalIndicator;
import module.training.ui.model.TrainingColumn;
import module.training.ui.model.TrainingEntry;
import module.training.ui.model.TrainingModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Table Model for the main table showing training results
 *
 * @author Mag. Bernhard Hödl AH - Solutions Augsten & Hödl OEG Neubachgasse 12
 * A - 2325 Himberg Tabellenmodel und Daten für die dargestellte Tabelle
 * für das HO Plugin
 */
public class TrainingProgressTableModel extends HOTableModel {

    // common column of fixed and scrolled tables
    private static final int COL_PLAYER_ID = 11;
//    private List<FutureTrainingManager> data = new ArrayList<>();
    private TrainingModel model;

    /**
     * Constructor
     *
     * @param columnModelId Column model id defined in UserColumnController
     */
    public TrainingProgressTableModel(UserColumnController.ColumnModelId columnModelId) {
        super(columnModelId, "TrainingOverview");
        columns = new ArrayList<>(List.of(
                new TrainingColumn("Spieler", 150) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingEntry entry) {
                        var ret = new ColorLabelEntry(entry.getTrainingSpeed(), entry.getPlayer().getFullName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                        ret.setIcon(TrainingPreviewPlayers.instance().getTrainPreviewPlayer(entry.getPlayer()).getIcon());
                        return ret;
                    }

                    @Override
                    public boolean canBeDisabled() {
                        return false;
                    }
                },
                new TrainingColumn("ls.player.age", 60) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingEntry entry) {
                        return new ColorLabelEntry(entry.getPlayer().getAgeWithDaysAsString(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("trainpre.priority", 140) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingEntry entry) {
                        return new ColorLabelEntry(entry.getTrainingPriority(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new TrainingColumn("ls.player.skill.keeper", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingEntry entry) {
                        return createIcon(entry.getPlayer(), PlayerSkill.KEEPER);
                    }
                },
                new TrainingColumn("ls.player.skill.defending", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingEntry entry) {
                        return createIcon(entry.getPlayer(), PlayerSkill.DEFENDING);
                    }
                },
                new TrainingColumn("ls.player.skill.playmaking", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingEntry entry) {
                        return createIcon(entry.getPlayer(), PlayerSkill.PLAYMAKING);
                    }
                },
                new TrainingColumn("ls.player.skill.passing", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingEntry entry) {
                        return createIcon(entry.getPlayer(), PlayerSkill.PASSING);
                    }
                },
                new TrainingColumn("ls.player.skill.winger", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingEntry entry) {
                        return createIcon(entry.getPlayer(), PlayerSkill.WINGER);
                    }
                },
                new TrainingColumn("ls.player.skill.scoring", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingEntry entry) {
                        return createIcon(entry.getPlayer(), PlayerSkill.SCORING);
                    }
                },
                new TrainingColumn("ls.player.skill.setpieces", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingEntry entry) {
                        return createIcon(entry.getPlayer(), PlayerSkill.SETPIECES);
                    }
                },
                new TrainingColumn("ls.player.skill.stamina", 70) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingEntry entry) {
                        return createIcon(entry.getPlayer(), PlayerSkill.STAMINA);
                    }
                },
                new TrainingColumn("ls.player.id", 0) {
                    @Override
                    public IHOTableCellEntry getTableEntry(TrainingEntry entry) {
                        return new ColorLabelEntry(entry.getPlayer().getPlayerId(), String.valueOf(entry.getPlayer().getPlayerId()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }

                    @Override
                    public boolean canBeDisabled() {
                        return false;
                    }

                    @Override
                    public boolean isHidden() {
                        return true;
                    }
                }
        )).toArray(new TrainingColumn[0]);
    }

    public TrainingColumn getPlayerIdColumn(){
        return (TrainingColumn) columns[COL_PLAYER_ID];
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


    @Override
    protected void initData() {
        var currentPlayers = HOVerwaltung.instance().getModel().getCurrentPlayers();
        m_clData = new Object[currentPlayers.size()][getDisplayedColumns().length];
        int rownum = 0;
        for (var player : currentPlayers) {
            int colnum = 0;
            for ( var col : getDisplayedColumns()){
                m_clData[rownum][colnum] = ((TrainingColumn)col).getTableEntry(new TrainingEntry(new FutureTrainingManager(player, this.model.getFutureTrainings())));
                colnum++;
            }
            rownum++;
        }
        fireTableDataChanged();
    }

    public void setModel(TrainingModel model) {
        this.model = model;
    }
}
