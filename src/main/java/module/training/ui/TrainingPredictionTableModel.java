package module.training.ui;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.MatchRoleID;
import core.training.FutureTrainingManager;
import core.training.TrainingPreviewPlayers;
import module.training.ui.model.TrainingColumn;
import module.training.ui.model.FutureTrainingEntry;
import module.training.ui.model.TrainingModel;
import module.training.ui.model.TrainingProgressColumn;

import javax.swing.*;
import java.awt.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TrainingPredictionTableModel  extends HOTableModel {


    private TrainingModel model;

        /**
         * Constructor
         */
        public TrainingPredictionTableModel(UserColumnController.ColumnModelId columnModelId) {
            super(columnModelId, "TrainingPrediction");
            List<UserColumn> newColumns = new ArrayList<>(List.of(
                    new TrainingColumn("Spieler", 150) {
                        @Override
                        public IHOTableCellEntry getTableEntry(FutureTrainingEntry entry) {
                            var ret = new ColorLabelEntry(entry.getTrainingSpeed(), entry.getPlayer().getFullName(), ColorLabelEntry.FG_STANDARD, getBackgroundColor(entry), SwingConstants.LEFT);
                            ret.setIcon(TrainingPreviewPlayers.instance().getTrainPreviewPlayer(entry.getPlayer()).getIcon());
                            ret.setToolTipText(TrainingPreviewPlayers.instance().getTrainPreviewPlayer(entry.getPlayer()).getText());
                            return ret;
                        }

                        @Override
                        public boolean canBeDisabled() {
                            return false;
                        }
                    },
                    new TrainingColumn("ls.player.age", 60) {
                        @Override
                        public IHOTableCellEntry getTableEntry(FutureTrainingEntry entry) {
                            return new ColorLabelEntry(entry.getPlayer().getAgeWithDaysAsString(), ColorLabelEntry.FG_STANDARD, getBackgroundColor(entry), SwingConstants.LEFT);
                        }
                    },
                    new TrainingColumn("BestePosition", 140) {
                        @Override
                        public IHOTableCellEntry getTableEntry(FutureTrainingEntry entry) {
                            var pos = entry.getPlayer().getIdealPosition();
                            return new ColorLabelEntry(String.format("%s (%.2f)", MatchRoleID.getNameForPosition(pos), entry.getPlayer().getIdealPositionRating()), ColorLabelEntry.FG_STANDARD, getBackgroundColor(entry), SwingConstants.LEFT);
                        }
                    },
                    new TrainingColumn("Speed", 140) {
                        @Override
                        public IHOTableCellEntry getTableEntry(FutureTrainingEntry entry) {
                            return new ColorLabelEntry(String.valueOf((int)entry.getTrainingSpeed()), ColorLabelEntry.FG_STANDARD, getBackgroundColor(entry), SwingConstants.LEFT);
                        }
                    },
                    new TrainingColumn("ls.player.id", 140) {
                        @Override
                        public IHOTableCellEntry getTableEntry(FutureTrainingEntry entry) {
                            return new ColorLabelEntry(Integer.toString(entry.getPlayer().getPlayerId()), ColorLabelEntry.FG_STANDARD, getBackgroundColor(entry), SwingConstants.LEFT);
                        }
                        @Override
                        public boolean isHidden() {
                            return true;
                        }
                    }
            ));
            var actualWeek = HOVerwaltung.instance().getModel().getBasics().getHattrickWeek();

            // We are in the middle where season has not been updated!
            try {
                if (HOVerwaltung.instance().getModel().getXtraDaten().getNextTrainingDate()
                        .isAfter(HOVerwaltung.instance().getModel().getXtraDaten().getSeriesMatchDate())) {
                    actualWeek = actualWeek.plus(7, ChronoUnit.DAYS);
                }
            } catch (Exception e1) {
                // Null when first time HO is launched
            }

            for (int i = 0; i < UserParameter.instance().futureWeeks; i++) {
                var htweek = actualWeek.toLocaleHTWeek();
                var column = new TrainingProgressColumn(htweek, 60);
                newColumns.add(column);
                actualWeek = actualWeek.plus(7, ChronoUnit.DAYS);
            }

            this.columns = newColumns.toArray(new UserColumn[0]);
        }

    private Color getBackgroundColor(FutureTrainingEntry entry) {
        int speed = (int)entry.getTrainingSpeed();
        // Speed range is 16 to 125
        if (speed > (125 + 50) / 2) {
            return ThemeManager.getColor(HOColorName.PLAYER_SKILL_SPECIAL_BG);
        } else if (speed > (50 + 16) / 2) {
            return ThemeManager.getColor(HOColorName.PLAYER_SKILL_BG);
        }
        return ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
    }

    @Override
    protected void initData() {
        var currentPlayers = HOVerwaltung.instance().getModel().getCurrentPlayers();
        m_clData = new Object[currentPlayers.size()][getDisplayedColumns().length];
        int rownum = 0;
        for (var player : currentPlayers) {
            int column = 0;
            var training = new FutureTrainingEntry(new FutureTrainingManager(player, this.model.getFutureTrainings()));
            for ( var col : getDisplayedColumns()){
                if ( col instanceof  TrainingColumn trainingColumn) {
                    m_clData[rownum][column] = trainingColumn.getTableEntry(training);
                }
                else if ( col instanceof  TrainingProgressColumn trainingProgressColumn){
                    m_clData[rownum][column] = trainingProgressColumn.getTableEntry(training);
                }
                column++;
            }
            rownum++;
        }
        fireTableDataChanged();
    }

    public void setTrainingModel(TrainingModel model) {
        this.model = model;
    }

}
