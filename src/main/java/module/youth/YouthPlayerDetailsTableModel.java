package module.youth;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.model.YouthPlayerColumn;

import javax.swing.*;
import java.text.SimpleDateFormat;

public class YouthPlayerDetailsTableModel extends HOTableModel {

    private YouthPlayer youthPlayer;

    public YouthPlayerDetailsTableModel(int id) {
        super(id,"YouthPlayerDetails");
        columns =  initColumns();
    }

    private YouthPlayerDetailColumn[] initColumns() {
        return new YouthPlayerDetailColumn[]{
                new YouthPlayerDetailColumn(0, "ls.player.training.date") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(entry.getMatchDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerDetailColumn(1, "ls.player.training.match") {
                    @Override
                    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getMatchName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                }
        };
    }

    @Override
    protected void initData() {
        var trainings = this.youthPlayer.getTrainings();
        m_clData = new Object[trainings.size()][columns.length];
        int rownum=0;
        for ( var training: trainings.values() ) {
            int columnnum=0;
            for (var col: columns){
                m_clData[rownum][columnnum] = ((YouthPlayerDetailColumn)col).getTableEntry((TrainingDevelopmentEntry) training);
                columnnum++;
            }
            rownum++;
        }
    }

    public void setYouthPlayer(YouthPlayer youthPlayer) {
        this.youthPlayer = youthPlayer;
    }

    public YouthPlayer getYouthPlayer() {
        return youthPlayer;
    }
}
