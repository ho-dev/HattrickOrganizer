package core.gui.model;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.UserColumn;
import module.youth.YouthPlayer;

import javax.swing.table.TableColumn;

public class YouthPlayerColumn extends UserColumn {

    protected YouthPlayerColumn(int id, String name) {
        super(id, name);
        this.setDisplay(true);
    }
    public YouthPlayerColumn(int id,String name,int minWidth){
        this(id,name,name,minWidth);
    }
    public YouthPlayerColumn(int id,String name, String tooltip, int minWidth){
        super(id,name,tooltip);

        // TODO does not work
        this.minWidth = minWidth;
        preferredWidth = minWidth;

        this.setDisplay(true);
    }

    public IHOTableEntry getTableEntry(YouthPlayer player, YouthPlayer playerCompare) {
        return new ColorLabelEntry(getValue(player), ColorLabelEntry.BG_STANDARD, false, 0);
    }

    public int getValue(YouthPlayer player){
        return player.getId();
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void setSize(TableColumn column) {
        // TODO does not work
        column.setMinWidth(200);
        column.setPreferredWidth(200);
    }
}
