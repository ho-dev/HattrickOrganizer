package module.hallOfFame;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.model.UserColumnController;
import core.model.player.Player;
import core.util.HODateTime;
import module.youth.YouthPlayer;
import module.youth.YouthPlayerColumn;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class HallOfFameTableModel extends HOTableModel {
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
                new YouthPlayerColumn("ls.player.age") {
                    @Override
                    public IHOTableCellEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(player.getAgeYears() * 112 + player.getAgeDays(), Player.getAgeWithDaysAsString(player.getAgeYears(), player.getAgeDays(), HODateTime.now()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthPlayerColumn("ls.youth.player.arrival") {
                    @Override
                    public IHOTableCellEntry getTableEntry(YouthPlayer player) {
                        return new ColorLabelEntry(HODateTime.toEpochSecond(player.getArrivalDate()), HODateTime.toLocaleDateTime(player.getArrivalDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                }
    }

    @Override
    protected void initData() {

    }
}
