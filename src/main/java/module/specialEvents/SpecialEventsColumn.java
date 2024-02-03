package module.specialEvents;

import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.UserColumn;

public class SpecialEventsColumn extends UserColumn {
    static int nextId=0;

    public SpecialEventsColumn(String name) {
        super(nextId++,name,name);
        this.index= this.getId();
        this.minWidth = 80;
        preferredWidth = 80;
        this.setDisplay(true);
    }

    public IHOTableEntry getTableEntry(MatchRow row) {
        return null;
    }

}
