package module.youth;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.HOTableModel;

import core.model.HOVerwaltung;
import core.model.match.MatchLineup;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;


public class YouthTrainingViewTableModel extends HOTableModel {

    public YouthTrainingViewTableModel(int id) {
        super(id,"YouthTrainingView");
        columns =  initColumns();
    }

    private YouthTrainingColumn[] initColumns() {
        return new YouthTrainingColumn[]{
                // TODO include match type icon in first column
                new YouthTrainingColumn(0, "ls.training.date", 0) {
                    @Override
                    public IHOTableEntry getTableEntry(MatchLineup lineup) {
                        return new ColorLabelEntry(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(lineup.getMatchDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthTrainingColumn(1, "ls.training.hometeam") {
                    @Override
                    public IHOTableEntry getTableEntry(MatchLineup lineup) {
                        return new ColorLabelEntry(lineup.getHomeTeamName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthTrainingColumn(2, "ls.training.guestteam") {
                    @Override
                    public IHOTableEntry getTableEntry(MatchLineup lineup) {
                        return new ColorLabelEntry(lineup.getGuestTeamName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },

                new YouthTrainingColumn(99, "ls.training.id", 0) {
                    @Override
                    public boolean isDisplay() {
                        return false;
                    }
                }
        };
    }

    @Override
    protected void initData() {
        var youthMatchLineups = HOVerwaltung.instance().getModel().getYouthMatchLineups()
                .stream()
                .sorted( (i1, i2) -> i2.getMatchDate().compareTo(i1.getMatchDate()))
                .collect(Collectors.toList());
        m_clData = new Object[youthMatchLineups.size()][columns.length];
        int rownum=0;
        for ( var lineup: youthMatchLineups ) {
            int columnnum=0;
            for (var col: columns){
                m_clData[rownum][columnnum] = ((YouthTrainingColumn)col).getTableEntry(lineup);
                columnnum++;
            }
            rownum++;
        }
    }

}
