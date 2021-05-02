package module.youth;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.UserColumn;

public class YouthPlayerDetailsColumn extends UserColumn {
    static int nextId=0;
    protected YouthPlayerDetailsColumn(String name) {
        this( name, 80);
        this.setDisplay(true);
    }
    public YouthPlayerDetailsColumn(String name, int minWidth){
        this(name,name,minWidth);
    }
    public YouthPlayerDetailsColumn(String name, String tooltip, int minWidth){
        super(nextId++,name,tooltip);
        this.index= this.getId();
        this.minWidth = minWidth;
        preferredWidth = minWidth;
        this.setDisplay(true);
    }

    public IHOTableEntry getTableEntry(YouthTrainingDevelopmentEntry youthTraining) {
        return new ColorLabelEntry(getValue(youthTraining), ColorLabelEntry.BG_STANDARD, false, 0);
    }
    public int getValue(YouthTrainingDevelopmentEntry youthTraining){
        return youthTraining.getMatchId();
    }

    @Override
    public boolean isEditable() {
        return false;
    }


}
