package module.training.ui.model;

import core.constants.player.PlayerAbility;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.UserColumn;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.util.HODateTime;

import javax.swing.*;
import java.awt.*;

public class TrainingProgressColumn extends UserColumn {
    static int nextId=0;

    private final HODateTime.HTWeek htWeek;
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

    public IHOTableCellEntry getTableEntry(FutureTrainingEntry entry) {
        var skillChange = entry.getFutureSkillups().stream().filter(s->s.getDate().toHTWeek().equals(this.htWeek)).findAny();
        String text = skillChange.map(change -> PlayerAbility.getNameForSkill(change.getValue(), false)).orElse("");
        return new ColorLabelEntry(text, ColorLabelEntry.FG_STANDARD, getBackgroundColor(entry), SwingConstants.LEFT);
    }

    private Color getBackgroundColor(FutureTrainingEntry entry) {
        int prio = entry.getTrainingPriority(this.htWeek);
        // Speed range is 16 to 125
        if (speed > (125 + 50) / 2) {
            return ThemeManager.getColor(HOColorName.PLAYER_SKILL_SPECIAL_BG);
        } else if (speed > (50 + 16) / 2) {
            return ThemeManager.getColor(HOColorName.PLAYER_SKILL_BG);
        }
        return ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
    }


}
