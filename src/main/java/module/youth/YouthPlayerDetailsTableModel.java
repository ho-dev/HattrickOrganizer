package module.youth;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.theme.ThemeManager;
import core.model.enums.MatchType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class YouthPlayerDetailsTableModel extends HOTableModel {

    // TODO add feature to edit players start skills
    // TODO examine rating and compare to hattrick's values to help adjust start skills

    private YouthPlayer youthPlayer;

    public YouthPlayerDetailsTableModel(int id) {
        super(id,"YouthPlayerDetails");
        columns =  initColumns();
    }

    private YouthPlayerDetailsColumn[] initColumns() {
        var tmp = new ArrayList<>(List.of(
                new YouthPlayerDetailsColumn("Datum") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(time2Int(entry.getMatchDate()), formatTime(entry.getMatchDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerDetailsColumn("ls.youth.player.training.match") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getMatchName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerDetailsColumn("ls.youth.player.training.matchtype") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(getYouthMatchTypeIcon(entry.getMatchType()), entry.getMatchType().getId(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerDetailsColumn("Rating") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getRating(), String.format("%.1f", entry.getRating()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerDetailsColumn("ls.player.age") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTrainingDevelopmentEntry entry) {
                        // Sortindex Age corresponds with matchdate
                        return new ColorLabelEntry( entry.getMatchDate().instant.getEpochSecond(), entry.getPlayerAge(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerDetailsColumn("ls.youth.training.primary") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getTrainingType(YouthTraining.Priority.Primary), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerDetailsColumn("ls.youth.training.secondary") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getTrainingType(YouthTraining.Priority.Secondary), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerDetailsColumn("ls.youth.player.sector") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getPlayerSector(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                // TODO: Specialty column should include the specialty icon
                new YouthPlayerDetailsColumn("ls.youth.player.Specialty") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getSpecialtyString());
                    }
                },
                new YouthPlayerDetailsColumn("ls.player.warningstatus.suspended") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getSupendedAsString());
                    }
                },
                new YouthPlayerDetailsColumn("ls.player.injurystatus.injured") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTrainingDevelopmentEntry entry) {
                        return new ColorLabelEntry(entry.getInjuredLevelAsString());
                    }
                }
        ));

        // TODO a final version should show skill development by a 2d-plot (graphic)
        for (var skillId : YouthPlayer.skillIds) {
            tmp.add(new YouthPlayerDetailsColumn("ls.youth.player." + skillId.toString()) {
                @Override
                public IHOTableEntry getTableEntry(YouthTrainingDevelopmentEntry entry) {
                    return new ColorLabelEntry(entry.getSkillValue(skillId));
                }
            });
        }
        return tmp.toArray(new YouthPlayerDetailsColumn[0]);
    }

    private static Icon getYouthMatchTypeIcon(MatchType matchType) {
        return switch (matchType) {
            case YOUTHFRIENDLY, YOUTHFRIENDLYCUPRULES, YOUTHINTERNATIONALFRIENDLYCUPRULES, YOUTHINTERNATIONALFRIENDLY -> ThemeManager.getIcon("FRIENDLY");
            default -> ThemeManager.getIcon("LEAGUE");
        };
    }

    @Override
    protected void initData() {
        if (youthPlayer != null) {
            var trainings = this.youthPlayer.getTrainingDevelopment().descendingMap();
            m_clData = new Object[trainings.size()][columns.length];
            int rownum = 0;
            for (var training : trainings.values()) {
                int columnnum = 0;
                for (var col : columns) {
                    m_clData[rownum][columnnum] = ((YouthPlayerDetailsColumn) col).getTableEntry(training);
                    columnnum++;
                }
                rownum++;
            }
        }
        fireTableDataChanged();
    }

    public void setYouthPlayer(YouthPlayer youthPlayer) {
        this.youthPlayer = youthPlayer;
    }

    public YouthPlayer getYouthPlayer() {
        return youthPlayer;
    }
}
