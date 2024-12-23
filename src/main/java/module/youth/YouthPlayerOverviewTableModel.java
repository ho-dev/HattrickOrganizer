package module.youth;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController;
import core.gui.theme.HOColorName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.HODateTime;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static module.youth.YouthSkillInfo.getSkillName;


public class YouthPlayerOverviewTableModel extends HOTableModel {

    private final Color aboveAverageRatingColor = ThemeManager.getColor(HOColorName.YOUTH_ABOVE_AVERAGE_RATING);
    private final Color highRatingColor = ThemeManager.getColor(HOColorName.YOUTH_HIGH_RATING);
    private final Color highestRatingColor = ThemeManager.getColor(HOColorName.YOUTH_HIGHEST_RATING);

    public YouthPlayerOverviewTableModel(UserColumnController.ColumnModelId id) {
        super(id, "YouthPlayerOverview");
        columns = initColumns();
    }

    private YouthPlayerColumn[] initColumns() {
        var tmp = new ArrayList<>(List.of(
                new YouthPlayerColumn("ls.player.name") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getFullName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }

                    @Override
                    public boolean canBeDisabled() {
                        return false;
                    }
                },
                new YouthPlayerColumn("ls.player.age") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getAgeYears() * 112 + player.getAgeDays(), Player.getAgeWithDaysAsString(player.getAgeYears(), player.getAgeDays(), HODateTime.now()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.arrival") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(HODateTime.toEpochSecond(player.getArrivalDate()), HODateTime.toLocaleDateTime(player.getArrivalDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.lastmatchdate") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(HODateTime.toEpochSecond(player.getYouthMatchDate()), HODateTime.toLocaleDateTime(player.getYouthMatchDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.canBePromotedIn") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getCanBePromotedIn(), player.getCanBePromotedInAtDate(HODateTime.now()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.Specialty") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        var specialty = player.getSpecialtyString();
                        var ret = new ColorLabelEntry(specialty, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                        if (!specialty.isEmpty()) {
                            ret.setIcon(ImageUtilities.getSmallPlayerSpecialtyIcon(player.getSpecialty()));
                        }
                        return ret;
                    }
                },
                new YouthPlayerColumn("ls.youth.player.potential") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        var score = player.getPotential();
                        Color backgroundColor;
                        if (score >= 30) {
                            backgroundColor = highestRatingColor;
                        } else if (score >= 24) {
                            backgroundColor = highRatingColor;
                        } else if (score >= 18) {
                            backgroundColor = aboveAverageRatingColor;
                        } else {
                            backgroundColor = ColorLabelEntry.BG_STANDARD;
                        }
                        return new ColorLabelEntry(player.getPotential(), "" + player.getPotential(), ColorLabelEntry.FG_STANDARD, backgroundColor, SwingConstants.RIGHT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.average") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getMinimumOverallSkillsLevel(), player.getOverallSkillsLevelAsString(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.matchcount") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getMatchCount(), "" + player.getMatchCount(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.trainingsum") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getTrainedSkillSum(), String.format("%.2f", player.getTrainedSkillSum()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.trainingprogress") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getProgressLastMatch(), String.format("%.2f", player.getProgressLastMatch()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                    }
                }
        ));

        for (var skillId : YouthPlayer.skillIds) {
            var skillName = getSkillName(skillId);
            tmp.add(new YouthPlayerColumn("ls.youth.player." + skillName, 130) {
                @Override
                public IHOTableEntry getTableEntry(YouthPlayer player) {
                    return new YouthSkillInfoColumn(player.getSkillInfo(skillId));
                }
            });
        }

        tmp.add(new YouthPlayerColumn("ls.player.shirtnumber.short", "ls.player.shirtnumber", 10) {
            @Override
            public IHOTableEntry getTableEntry(YouthPlayer player) {
                return new ColorLabelEntry(getPlayerNumberAsInt(player), player.getPlayerNumber(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            }
        });
        tmp.add(new YouthPlayerColumn("ls.player.category") {
            @Override
            public IHOTableEntry getTableEntry(YouthPlayer player) {
                return new ColorLabelEntry(getPlayerCategoryAsInt(player), player.getPlayerCategory().toString(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            }
        });
        tmp.add(new YouthPlayerColumn("ls.player.ownernotes") {
            @Override
            public IHOTableEntry getTableEntry(YouthPlayer player) {
                return new ColorLabelEntry(player.getOwnerNotes(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            }
        });
        tmp.add(new YouthPlayerColumn("ls.player.statement") {
            @Override
            public IHOTableEntry getTableEntry(YouthPlayer player) {
                return new ColorLabelEntry(player.getStatement(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            }
        });
        tmp.add(new YouthPlayerColumn("ls.player.injurystatus") {
            @Override
            public IHOTableEntry getTableEntry(YouthPlayer player) {
                var ret = new ColorLabelEntry(player.getInjuryLevel(), getInjuryLevelAsString(player.getInjuryLevel()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
                ret.setIcon(getInjuryLevelIcon(player.getInjuryLevel()));
                return ret;
            }
        });
        tmp.add(new YouthPlayerColumn("ls.player.career_goals") {
            @Override
            public IHOTableEntry getTableEntry(YouthPlayer player) {
                return new ColorLabelEntry(player.getCareerGoals(), String.valueOf(player.getCareerGoals()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            }
        });
        tmp.add(new YouthPlayerColumn("ls.player.season_series_goals") {
            @Override
            public IHOTableEntry getTableEntry(YouthPlayer player) {
                return new ColorLabelEntry(player.getLeagueGoals(), String.valueOf(player.getLeagueGoals()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            }
        });
        tmp.add(new YouthPlayerColumn("ls.player.hattricks") {
            @Override
            public IHOTableEntry getTableEntry(YouthPlayer player) {
                return new ColorLabelEntry(player.getCareerHattricks(), String.valueOf(player.getCareerHattricks()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            }
        });
        tmp.add(new YouthPlayerColumn("ls.player.ratemyacademyscore", "ls.player.ratemyacademyscore.tooltip", 60) {
            @Override
            public IHOTableEntry getTableEntry(YouthPlayer player) {
                var score = player.calculateRateMyAcademyScore();
                Color backgroundColor;
                if (score >= 3000) {
                    backgroundColor = highestRatingColor;
                } else if (score >= 2400) {
                    backgroundColor = highRatingColor;
                } else if (score >= 2000) {
                    backgroundColor = aboveAverageRatingColor;
                } else {
                    backgroundColor = ColorLabelEntry.BG_STANDARD;
                }
                return new ColorLabelEntry(score, String.valueOf(score), ColorLabelEntry.FG_STANDARD, backgroundColor, SwingConstants.RIGHT);
            }
        });

        return tmp.toArray(new YouthPlayerColumn[0]);
    }

    private Icon getInjuryLevelIcon(int injuryLevel) {
        if (injuryLevel > 0) {
            return ImageUtilities.getInjuryIcon(12,12);
        } else if (injuryLevel == 0) {
            return ImageUtilities.getPlasterIcon(12,12);
        }
        return null;
    }

    private String getInjuryLevelAsString(int injuryLevel) {
        if (injuryLevel >= 0) { return String.valueOf(injuryLevel); }
        return "";
    }

    private int getPlayerCategoryAsInt(YouthPlayer player) {
        var id = player.getPlayerCategory().getId();
        if ( id != 0) return id;
        return 100;
    }

    private int getPlayerNumberAsInt(YouthPlayer player) {
        try {
            return Integer.parseInt(player.getPlayerNumber());
        }
        catch (NumberFormatException ignored) {}
        return 0;
    }

    @Override
    protected void initData() {
        UserColumn[] displayedColumns = getDisplayedColumns();
        var youthplayers = HOVerwaltung.instance().getModel().getCurrentYouthPlayers();
        m_clData = new Object[youthplayers.size()][columns.length];
        int playernum = 0;
        for (var player : youthplayers) {
            int columnnum = 0;
            for (var col : displayedColumns) {
                m_clData[playernum][columnnum] = ((YouthPlayerColumn) col).getTableEntry(player);
                columnnum++;
            }
            playernum++;
        }
        fireTableDataChanged();
    }
}
