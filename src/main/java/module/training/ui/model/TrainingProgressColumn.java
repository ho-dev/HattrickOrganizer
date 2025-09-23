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

    private static final Color TABLE_BG = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
    private static final Color SELECTION_BG = ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG);
    private static final Color TABLE_FG = ThemeManager.getColor(HOColorName.TABLEENTRY_FG);
    private static final Color BIRTHDAY_BG = ThemeManager.getColor(HOColorName.TRAINING_BIRTHDAY_BG);
    private static final Color FULL_TRAINING_BG = ThemeManager.getColor(HOColorName.TRAINING_FULL_BG);
    private static final Color PARTIAL_TRAINING_BG = ThemeManager.getColor(HOColorName.TRAINING_PARTIAL_BG);
    private static final Color OSMOSIS_TRAINING_BG = ThemeManager.getColor(HOColorName.TRAINING_OSMOSIS_BG);

    static int nextId=0;

    private final HODateTime.HTWeek htWeek;
    private final int trainingWeekIndex;

    public TrainingProgressColumn(HODateTime.HTWeek htweek, int trainingWeekIndex, int minWidth) {
        super(nextId++,htweek.season + " " + htweek.week);
        this.trainingWeekIndex = trainingWeekIndex;
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
        var skillChange = entry.getFutureSkillChanges().stream().filter(s->s.getDate().toHTWeek().equals(this.htWeek)).findAny();
        String text = skillChange.map(change -> PlayerAbility.getNameForSkill(change.getValue(), false)).orElse("");
        return new ColorLabelEntry(text, ColorLabelEntry.FG_STANDARD, getBackgroundColor(entry), SwingConstants.LEFT);
    }

    private Color getBackgroundColor(FutureTrainingEntry entry) {
        var prio = entry.getTrainingPriority(this.trainingWeekIndex);
        if (prio != null) {
            switch (prio) {
                case FULL_TRAINING:
                    return FULL_TRAINING_BG;
                case PARTIAL_TRAINING:
                    return PARTIAL_TRAINING_BG;
                case OSMOSIS_TRAINING:

                    return OSMOSIS_TRAINING_BG;
            }
        }
        return ColorLabelEntry.BG_STANDARD;
    }


}
