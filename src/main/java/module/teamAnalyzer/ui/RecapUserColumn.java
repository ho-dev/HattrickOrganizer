package module.teamAnalyzer.ui;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.UserColumn;
import module.teamAnalyzer.vo.TeamLineup;

import javax.swing.*;

class RecapUserColumn extends UserColumn {
    private static int nextId = 0;

    public RecapUserColumn(String name, int minWidth) {
        super(nextId++, name, name);
        this.index = this.getId();
        this.minWidth = minWidth;
        this.setPreferredWidth(minWidth);
        this.setDisplay(true);
    }

    public IHOTableEntry getTableEntry(TeamLineup lineup) {
        return new ColorLabelEntry(lineup.getName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
    }

    @Override
    public boolean isEditable() {
        return false;
    }

}
