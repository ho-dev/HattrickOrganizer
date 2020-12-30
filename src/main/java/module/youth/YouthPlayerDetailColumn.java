package module.youth;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.UserColumn;

public class YouthPlayerDetailColumn extends UserColumn {
    protected YouthPlayerDetailColumn(int id, String name) {
        super(id, name);
        this.setDisplay(true);
    }
    public YouthPlayerDetailColumn(int id,String name,int minWidth){
        this(id,name,name,minWidth);
    }
    public YouthPlayerDetailColumn(int id,String name, String tooltip, int minWidth){
        super(id,name,tooltip);
        this.minWidth = minWidth;
        preferredWidth = minWidth;
        this.setDisplay(true);
    }

    public IHOTableEntry getTableEntry(TrainingDevelopmentEntry youthTraining) {
        return new ColorLabelEntry(getValue(youthTraining), ColorLabelEntry.BG_STANDARD, false, 0);
    }
    public int getValue(TrainingDevelopmentEntry youthTraining){
        return youthTraining.getMatchId();
    }

    @Override
    public boolean isEditable() {
        return false;
    }


}
