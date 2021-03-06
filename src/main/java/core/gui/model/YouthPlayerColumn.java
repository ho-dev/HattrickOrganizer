package core.gui.model;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.UserColumn;
import module.youth.YouthPlayer;

import javax.swing.table.TableColumn;

public class YouthPlayerColumn extends UserColumn {

    static int nextId = 0;

    protected YouthPlayerColumn(String name) {
        this(name, name, 50);
    }

    public YouthPlayerColumn(String name, int minWidth) {
        this(name, name, minWidth);
    }

    public YouthPlayerColumn(String name, String tooltip, int minWidth) {
        super(nextId++, name, tooltip);
        this.minWidth=minWidth;
        this.setPreferredWidth(minWidth);
        this.setDisplay(true);
    }

    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
        return new ColorLabelEntry(getValue(player), ColorLabelEntry.BG_STANDARD, false, 0);
    }

    public int getValue(YouthPlayer player) {
        return player.getId();
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void setSize(TableColumn column) {
        column.setMinWidth(50);
        column.setPreferredWidth(100);
    }
}
