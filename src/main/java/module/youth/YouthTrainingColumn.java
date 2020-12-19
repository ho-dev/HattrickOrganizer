package module.youth;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.UserColumn;
import core.model.match.MatchLineup;

public class YouthTrainingColumn extends UserColumn {
    protected YouthTrainingColumn(int id, String name) {
        super(id, name);
        this.setDisplay(true);
    }
    public YouthTrainingColumn(int id,String name,int minWidth){
        this(id,name,name,minWidth);
    }
    public YouthTrainingColumn(int id,String name, String tooltip, int minWidth){
        super(id,name,tooltip);
        this.minWidth = minWidth;
        preferredWidth = minWidth;
        this.setDisplay(true);
    }

    public IHOTableEntry getTableEntry(MatchLineup lineup) {
        return new ColorLabelEntry(getValue(lineup), ColorLabelEntry.BG_STANDARD, false, 0);
    }

    public int getValue(MatchLineup lineup){
        return lineup.getMatchID();
    }

    @Override
    public boolean isEditable() {
        return false;
    }


}
