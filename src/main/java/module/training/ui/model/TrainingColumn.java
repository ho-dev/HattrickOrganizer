package module.training.ui.model;

import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.UserColumn;
import module.training.PlayerSkillChange;

public class TrainingColumn extends UserColumn {
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

    public IHOTableCellEntry getTableEntry(TrainingEntry entry) {return null;}
    public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {return null;}
}
