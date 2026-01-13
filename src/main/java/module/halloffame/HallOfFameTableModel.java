package module.halloffame;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.HOPlayersTableModel;
import core.gui.model.UserColumnController;
import core.gui.theme.ImageUtilities;
import core.model.HOVerwaltung;
import core.util.HODateTime;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class HallOfFameTableModel extends HOPlayersTableModel {
    public HallOfFameTableModel(UserColumnController.ColumnModelId columnModelId) {
        super(columnModelId, "ls.hof");

        this.columns = new ArrayList<>(List.of(
            new HallOfFameColumn("ls.player.name") {
                @Override
                public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                    return new ColorLabelEntry(player.getFullName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                }

                @Override
                public boolean canBeDisabled() {
                    return false;
                }
            },
            new HallOfFameColumn("ls.player.age") {
                @Override
                public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                    return new ColorLabelEntry(player.getAge(), String.valueOf(player.getAge()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                }
            },
            new HallOfFameColumn("ls.player.nationality") {
                @Override
                public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                    return new ColorLabelEntry(ImageUtilities.getCountryFlagIcon(player.getCountryId()), player.getCountryId(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
                }
            },
            new HallOfFameColumn("ImTeamSeit") {
                @Override
                public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                    return new ColorLabelEntry(HODateTime.toEpochSecond(player.getArrivalDate()), HODateTime.toLocaleDateTime(player.getArrivalDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                }
            },
            new HallOfFameColumn("ls.hof.arrival") {
                @Override
                public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                    return new ColorLabelEntry(HODateTime.toEpochSecond(player.getHofDate()), HODateTime.toLocaleDateTime(player.getHofDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                }
            },
            new HallOfFameColumn("ls.hof.experttype") {
                @Override
                public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                    return new ColorLabelEntry(player.getExpertType(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                }
            },
            new HallOfFameColumn("ls.hof.extrainer.from") {
                @Override
                public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                    return new ColorLabelEntry(player.getTrainerFrom(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                }
            },
            new HallOfFameColumn("ls.hof.extrainer.to") {
                @Override
                public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                    return new ColorLabelEntry(player.getTrainerTo(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                }
            },
            new HallOfFameColumn("ls.hof.extrainer.duration") {
                @Override
                public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                    String str;
                    var duration = player.getTrainerDuration();
                    if (duration != null) str = duration.toString();
                    else str = "";
                    return new ColorLabelEntry(str, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                }
            },
            new HallOfFameColumn("ls.player.matchescurrentteam") {
                @Override
                public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                    var currentTeamMatches = player.getCurrentTeamMatches();
                    String txt;
                    if (currentTeamMatches != null) {
                        txt = String.valueOf(currentTeamMatches);
                    } else {
                        currentTeamMatches = 0;
                        txt = "";
                    }
                    return new ColorLabelEntry(currentTeamMatches, txt, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                }
            },
            new HallOfFameColumn("PlayerOverview.GoalsTeam.long") {
                @Override
                public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                    return new ColorLabelEntry(player.getCurrentTeamGoals(), String.valueOf(player.getCurrentTeamGoals()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                }
            },
            new HallOfFameColumn("PlayerOverview.CareerAssists.long") {
                @Override
                public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                    return new ColorLabelEntry(player.getCareerAssists(), String.valueOf(player.getCareerAssists()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                }
            },
            new HallOfFameColumn("PlayerOverview.AssistsCurrentTeam.long") {
                @Override
                public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                    return new ColorLabelEntry(player.getAssistsCurrentTeam(), String.valueOf(player.getAssistsCurrentTeam()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                }
            }
        )).toArray(new HallOfFameColumn[0]);
    }

    @Override
    protected void initData() {
        var players = HOVerwaltung.instance().getModel().getHallOfFamePlayers();
        this.setPlayers(players);
        m_clData = new Object[players.size()][getDisplayedColumns().length];
        int rownum = 0;
        for (var player : players) {
            int column = 0;
            for ( var col : getDisplayedColumns()){
                if ( col instanceof  HallOfFameColumn hallOfFameColumn) {
                    m_clData[rownum][column] = hallOfFameColumn.getTableEntry(player);
                }
                column++;
            }
            rownum++;
        }
        fireTableDataChanged();
    }
}
