package module.teamAnalyzer.ui;

import core.constants.player.PlayerAbility;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.MatchesColumnModel;
import core.gui.model.UserColumnController;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.enums.MatchType;
import core.model.match.Matchdetails;
import core.util.Helper;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.report.TeamReport;
import module.teamAnalyzer.vo.TeamLineup;

import javax.swing.*;
import java.awt.*;

public class RecapPanelTableModel extends HOTableModel {

    public RecapPanelTableModel( UserColumnController.ColumnModelId id) {
        super(id, "TeamAnalyzerRecap");
        columns = initColumns();
    }

    private RecapUserColumn[] initColumns() {
        return new RecapUserColumn[]{
                new RecapUserColumn("RecapPanel.Game", 100) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getName(), ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("Type", 20) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return getMatchTypeColumnEntry(lineup);
                    }
                },
                new RecapUserColumn("ls.match.result", 40) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getResult(), ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("Week", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(getMatchDate(lineup), lineup.getWeek() > 0 ? "" + lineup.getWeek() : RecapPanel.VALUE_NA, ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("Season", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(getMatchDate(lineup), lineup.getSeason() > 0 ? "" + lineup.getSeason() : RecapPanel.VALUE_NA, ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.match.ratingsector.midfield", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getRating() != null ? getRating((int) lineup.getRating().getMidfield()) : "", ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.match.ratingsector.rightdefence", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getRating() != null ? getRating((int) lineup.getRating().getRightDefense()) : "", ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.match.ratingsector.centraldefence", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getRating() != null ? getRating((int) lineup.getRating().getCentralDefense()) : "", ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.match.ratingsector.leftdefence", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getRating() != null ? getRating((int) lineup.getRating().getLeftDefense()) : "", ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.match.ratingsector.rightattack", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getRating() != null ? getRating((int) lineup.getRating().getRightAttack()) : "", ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.match.ratingsector.centralattack", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getRating() != null ? getRating((int) lineup.getRating().getCentralAttack()) : "", ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.match.ratingsector.leftattack", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getRating() != null ? getRating((int) lineup.getRating().getLeftAttack()) : "", ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("RecapPanel.Stars", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getStars(), "" + lineup.getStars(), ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.match.ratingtype.hatstats", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getRating() != null ? "" + lineup.getRating().getHatStats() : "", ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.match.ratingtype.squad", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getRating() != null ? "" + lineup.getRating().getSquad() : "", ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.match.ratingtype.smartsquad", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getRating() != null ? String.format("%.2f", lineup.getRating().getSquad() / lineup.getStars()) : "", ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.match.ratingtype.loddarstats", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getRating() != null ? String.format("%.2f", lineup.getRating().getLoddarStats()) : "", ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.team.tactic", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(formatTacticColumn(lineup), ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.team.tacticalskill", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getTacticCode() > 0 ? PlayerAbility.getNameForSkill(lineup.getTacticLevel(), false) : RecapPanel.VALUE_NA, ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.team.formation", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getFormation(), ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.team.teamspirit", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getMorale() != null ? "" + lineup.getMorale() : "", ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.team.confidence", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return new ColorLabelEntry(lineup.getSelfConfidence() != null ? "" + lineup.getSelfConfidence() : "", ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), SwingConstants.LEFT);
                    }
                },
                new RecapUserColumn("ls.team.numplayers", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return createIntegerTableEntry(lineup.getPlayerCount(),MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()) );
                    }
                },
                new RecapUserColumn("ls.team.numtransferlisted", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return createIntegerTableEntry(lineup.getTransferlisted(), MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()));
                    }
                },
                new RecapUserColumn("ls.team.numbruised", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return createIntegerTableEntry( lineup.getBruised(), MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()));
                    }
                },
                new RecapUserColumn("ls.team.injuredWeeks", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return createIntegerTableEntry( lineup.getInjuredWeeks(), MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()));
                    }
                },
                new RecapUserColumn("ls.team.numyellowcards", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return createIntegerTableEntry(lineup.getYellowCards(), MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()));
                    }
                },
                new RecapUserColumn("ls.team.num2yellowcards", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return createIntegerTableEntry(lineup.getTwoYellowCards(), MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()));
                    }
                },
                new RecapUserColumn("ls.team.numsuspended", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return createIntegerTableEntry(lineup.getSuspended(), MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()));
                    }
                },
                new RecapUserColumn("ls.team.totaltsi", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return createIntegerTableEntry( lineup.getTsiSum(),MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()));
                    }
                },
                new RecapUserColumn("ls.team.sumsalary", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return createIntegerTableEntry(lineup.getSalarySum(), MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()), true);
                    }
                },
                new RecapUserColumn("ls.team.numhomegrown", 50) {
                    @Override
                    public IHOTableEntry getTableEntry(TeamLineup lineup) {
                        return createIntegerTableEntry(lineup.getHomegrownCount(), MatchesColumnModel.getColor4Matchtyp(lineup.getMatchType()));
                    }
                }
        };
    }

    private double getMatchDate(TeamLineup lineup) {
        var details = lineup.getMatchDetail();
        if ( details != null){
            return details.getMatch().getMatchDate().instant.getEpochSecond();
        }
        return Double.POSITIVE_INFINITY;
    }
    private IHOTableEntry createIntegerTableEntry(Integer ival, Color color4Matchtyp) {
        return createIntegerTableEntry(ival, color4Matchtyp, false);
    }
    private IHOTableEntry createIntegerTableEntry(Integer ival, Color color4Matchtyp, boolean isCurrency) {
        double dval;
        String text;
        if ( ival != null){
            text = Helper.getNumberFormat(isCurrency, 0).format(ival);
            dval = (double) ival;
        }
        else {
            text = "";
            dval = Double.NEGATIVE_INFINITY;
        }
        return new ColorLabelEntry(dval, text, ColorLabelEntry.FG_STANDARD, color4Matchtyp, SwingConstants.RIGHT);
    }

    private ColorLabelEntry getMatchTypeColumnEntry(TeamLineup lineup) {
        var matchType = lineup.getMatchType();
        if (matchType != MatchType.NONE) {
            var ret = new ColorLabelEntry(ThemeManager.getIcon(HOIconName.MATCHICONS[matchType.getIconArrayIndex()]), matchType.getMatchTypeId(), ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(matchType), SwingConstants.LEFT);
            StringBuilder tipText = new StringBuilder(matchType.getName());

            tipText.append(" - "); //$NON-NLS-1$

            if (lineup.isHomeMatch()) {
                tipText.append(HOVerwaltung.instance().getLanguageString("Heim")); //$NON-NLS-1$
            } else {
                tipText.append(HOVerwaltung.instance().getLanguageString("Gast")); //$NON-NLS-1$
            }

            ret.setToolTipText(tipText.toString());
            return ret;
        } else {
            return new ColorLabelEntry(0, RecapPanel.VALUE_NA, ColorLabelEntry.FG_STANDARD, MatchesColumnModel.getColor4Matchtyp(matchType), SwingConstants.LEFT);
        }
    }

    @Override
    protected void initData() {
        if (teamReport == null) return;

        UserColumn[] displayedColumns = getDisplayedColumns();
        int selection = teamReport.getSelection(); // save selection
//            // Empty model
//            while (tableModel.getRowCount() > 0) {
//                tableModel..removeRow(0);
//            }

        if (teamReport.size() < 2) return; // no matches loaded

        m_clData = new Object[teamReport.size()][columns.length];


        for (int i = 0; i < teamReport.size(); i++) {
//                tableModel.addRow(AddLineup(teamReport.getTeamMatchReport(i)));
            var lineup = teamReport.getTeamMatchReport(i);
            int colNum = 0;
            for (var col : displayedColumns) {
                m_clData[i][colNum++] = ((RecapUserColumn) col).getTableEntry(lineup);
            }
        }
        teamReport.setSelection(selection); // restore selection
        fireTableDataChanged();
    }

    private TeamReport teamReport = null;

    public void showTeamReport(TeamReport teamReport) {
        this.teamReport = teamReport;
        initData();
    }

    private String getRating(int rating) {
        return RatingUtil.getRating(rating, SystemManager.isNumericRating.isSet(), SystemManager.isDescriptionRating.isSet());
    }

    private String formatTacticColumn(TeamLineup lineup) {
        var str = new StringBuilder();
        int tactic = lineup.getTacticCode();
        if (tactic != -1) {
            str.append(Matchdetails.getNameForTaktik(tactic));
        }
        if (lineup.getMatchDetail() != null && lineup.getMatchDetail().isManMarking()) {
            if (tactic != -1) str.append("/");
            str.append(HOVerwaltung.instance().getLanguageString("ls.teamanalyzer.manmarking"));
        }

        if (str.isEmpty()) {
            str.append(RecapPanel.VALUE_NA);
        }
        return str.toString();
    }
}
