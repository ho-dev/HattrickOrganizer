package module.hallOfFame;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.HOPlayersTableModel;
import core.gui.comp.table.HOTableModel;
import core.gui.model.UserColumnController;
import core.model.player.Player;
import core.util.HODateTime;
import module.youth.YouthPlayer;
import module.youth.YouthPlayerColumn;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class HallOfFameTableModel extends HOPlayersTableModel {
    public HallOfFameTableModel(UserColumnController.ColumnModelId columnModelId) {
        super(columnModelId, "ls.HallOfFame");

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
                        return new ColorLabelEntry(player.getAge() * 112 + player.getAgeDays(), Player.getAgeWithDaysAsString(player.getAge(), player.getAgeDays(), HODateTime.now()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new HallOfFameColumn("ImTeamSeit") {
                    @Override
                    public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                        return new ColorLabelEntry(HODateTime.toEpochSecond(player.getArrivalDate()), HODateTime.toLocaleDateTime(player.getArrivalDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new HallOfFameColumn("ls.halloffame.arrival") {
                    @Override
                    public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                        return new ColorLabelEntry(HODateTime.toEpochSecond(player.getHofDate()), HODateTime.toLocaleDateTime(player.getHofDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new HallOfFameColumn("ls.halloffame.experttype") {
                    @Override
                    public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {
                        return new ColorLabelEntry(player.getExpertType(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                }
        )).toArray(new HallOfFameColumn[0]);
    }

    @Override
    protected void initData() {

    }
}
