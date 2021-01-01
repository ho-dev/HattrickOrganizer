package module.youth;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.HOTableModel;
import module.training.Skills;

import javax.swing.*;
import java.text.SimpleDateFormat;

public class YouthPlayerDetailsTableModel extends HOTableModel {

    private YouthPlayer youthPlayer;

    public YouthPlayerDetailsTableModel(int id) {
        super(id,"YouthPlayerDetails");
        columns =  initColumns();
    }

    private YouthPlayerDetailsColumn[] initColumns() {
        return new YouthPlayerDetailsColumn[]{
                new YouthPlayerDetailsColumn(0, "ls.player.training.date") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(entry.getMatchDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerDetailsColumn(1, "ls.player.training.match") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getMatchName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerDetailsColumn(2, "ls.player.age") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getPlayerAge(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerDetailsColumn(3, "ls.training.primary") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getTrainingType(YouthTraining.Priority.Primary), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerDetailsColumn(4, "ls.training.secondary") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getTrainingType(YouthTraining.Priority.Primary), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerDetailsColumn(5, "ls.player.position") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getPlayerPosition(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerDetailsColumn(6, "ls.player.Keeper") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getSkillValue(Skills.HTSkillID.Keeper));
                    }
                },
                new YouthPlayerDetailsColumn(7, "ls.player.Defender") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getSkillValue(Skills.HTSkillID.Defender));
                    }
                },
                new YouthPlayerDetailsColumn(8, "ls.player.Playmaker") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getSkillValue(Skills.HTSkillID.Playmaker));
                    }
                },
                new YouthPlayerDetailsColumn(9, "ls.player.Winger") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getSkillValue(Skills.HTSkillID.Winger));
                    }
                },
                new YouthPlayerDetailsColumn(10, "ls.player.Passing") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getSkillValue(Skills.HTSkillID.Passing));
                    }
                },
                new YouthPlayerDetailsColumn(11, "ls.player.Scorer") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getSkillValue(Skills.HTSkillID.Scorer));
                    }
                },
                new YouthPlayerDetailsColumn(12, "ls.player.SetPieces") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getSkillValue(Skills.HTSkillID.SetPieces));
                    }
                }
        };
    }

    @Override
    protected void initData() {
        if (youthPlayer != null) {
            var trainings = this.youthPlayer.getTrainings();
            m_clData = new Object[trainings.size()][columns.length];
            int rownum = 0;
            for (var training : trainings.values()) {
                int columnnum = 0;
                for (var col : columns) {
                    m_clData[rownum][columnnum] = ((YouthPlayerDetailsColumn) col).getTableEntry((TrainingDevelopmentEntry) training);
                    columnnum++;
                }
                rownum++;
            }
        }
    }

    public void setYouthPlayer(YouthPlayer youthPlayer) {
        this.youthPlayer = youthPlayer;
    }

    public YouthPlayer getYouthPlayer() {
        return youthPlayer;
    }
}
