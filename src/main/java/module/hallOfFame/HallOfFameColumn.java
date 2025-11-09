package module.hallOfFame;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.UserColumn;
import module.youth.YouthPlayer;

public class HallOfFameColumn extends UserColumn {
    static int nextId = 0;

    public HallOfFameColumn(String name) {
        super(nextId++, name);
        this.index= this.getId();
//        this.minWidth=minWidth;
//        this.setPreferredWidth(minWidth);
        this.setDisplay(true);

    }

    public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {return null;}

}
