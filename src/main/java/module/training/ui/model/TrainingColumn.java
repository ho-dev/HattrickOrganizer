package module.training.ui.model;

import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.UserColumn;

public abstract class TrainingColumn extends UserColumn {
    static int nextId=0;
    public TrainingColumn(String name) {
        this( name, 80);
    }
    public TrainingColumn(String name, int minWidth){
        this(name,name,minWidth);
    }
    public TrainingColumn(String name, String tooltip, int minWidth){
        super(nextId++,name,tooltip);
        this.index= this.getId();
        this.minWidth = minWidth;
        preferredWidth = minWidth;
        this.setDisplay(true);
    }

    public abstract IHOTableCellEntry getTableEntry(TrainingEntry entry);
}
