package module.halloffame;

import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.UserColumn;

/**
 * Column of hall of fame table
 */
public class HallOfFameColumn extends UserColumn {
    static int nextId = 0;

    /**
     * Create hall of fame column
     * @param name String
     */
    public HallOfFameColumn(String name) {
        super(nextId++, name);
        this.index= this.getId();
        this.setDisplay(true);
    }

    /**
     * Get table entry
     * Dummy, overridden by table model
     * @param player HallOflFamePlayer
     * @return IHOTabeCellEntry
     */
    public IHOTableCellEntry getTableEntry(HallOfFamePlayer player) {return null;}
}
