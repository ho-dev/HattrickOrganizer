package module.training.ui.model;

import core.constants.player.PlayerAbility;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.UserColumn;
import core.util.HODateTime;

import javax.swing.*;

public class TrainingProgressColumn extends UserColumn {
    static int nextId=0;

    private HODateTime.HTWeek htWeek;
    public TrainingProgressColumn(HODateTime.HTWeek htweek, int minWidth) {
        super(nextId++,htweek.season + " " + htweek.week);
        this.translateColumnName = false;
        this.htWeek = htweek;
        this.index= this.getId();
        this.minWidth = minWidth;
        preferredWidth = minWidth;
        this.setDisplay(true);

    }

//    public TrainingColumn(String name, int minWidth){
//        this(name,name,minWidth);
//    }
//
//    public TrainingColumn(String name, String tooltip, int minWidth){
//        super(nextId++,name,tooltip);
//        this.index= this.getId();
//        this.minWidth = minWidth;
//        preferredWidth = minWidth;
//        this.setDisplay(true);
//    }

    public IHOTableCellEntry getTableEntry(TrainingEntry entry) {
        var skillChange = entry.getFutureSkillups().stream().filter(s->s.getDate().toHTWeek() == this.htWeek).findAny();
        String text = skillChange.map(change -> PlayerAbility.getNameForSkill(change.getValue(), false)).orElse("");
        return new ColorLabelEntry(text, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
    }

}
