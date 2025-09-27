package module.training.ui.model;

import core.constants.player.PlayerAbility;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.UserColumn;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.TranslationFacility;
import core.util.HODateTime;
import module.training.ui.TrainingLegendPanel;

import javax.swing.*;
import java.awt.*;
import java.time.temporal.ChronoUnit;

public class TrainingProgressColumn extends UserColumn {

    private static final Color TABLE_BG = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
    private static final Color SELECTION_BG = ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG);
    private static final Color TABLE_FG = ThemeManager.getColor(HOColorName.TABLEENTRY_FG);
    private static final Color BIRTHDAY_BG = ThemeManager.getColor(HOColorName.TRAINING_BIRTHDAY_BG);
    private static final Color FULL_TRAINING_BG = ThemeManager.getColor(HOColorName.TRAINING_FULL_BG);
    private static final Color PARTIAL_TRAINING_BG = ThemeManager.getColor(HOColorName.TRAINING_PARTIAL_BG);
    private static final Color OSMOSIS_TRAINING_BG = ThemeManager.getColor(HOColorName.TRAINING_OSMOSIS_BG);
    private final HODateTime.HTWeek htWeek;
    private final int trainingWeekIndex;

    public TrainingProgressColumn(int id, HODateTime.HTWeek htweek, int trainingWeekIndex, int minWidth) {
        super(id,htweek.season + " " + htweek.week);
        this.trainingWeekIndex = trainingWeekIndex;
        this.translateColumnName = false;
        this.translateColumnTooltip = false;
        this.htWeek = htweek;
        this.index= this.getId();
        this.minWidth = minWidth;
        preferredWidth = minWidth;
        this.setDisplay(true);

    }

    public IHOTableCellEntry getTableEntry(FutureTrainingEntry entry) {
        var colorLabelEntry =  new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, getBackgroundColor(entry), SwingConstants.LEFT);
        var skillChange = entry.getFutureSkillChanges().stream().filter(s->s.getDate().toHTWeek().equals(this.htWeek)).findAny();
        if (skillChange.isPresent()){
            var text = PlayerAbility.getNameForSkill(skillChange.get().getValue(), false) + String.format(" (%.2f)", skillChange.get().getValue());
            var icon = TrainingLegendPanel.getSkillupTypeIcon(skillChange.get().getType(), skillChange.get().getChange());
            var tooltip = skillChange.get().getType().getLanguageString() + ": " + text;
            colorLabelEntry.setIcon(icon);
            colorLabelEntry.setToolTipText(tooltip);
            colorLabelEntry.setText(text);
        }

        // Check if player has birthday
        // every row is an additional week
        var dateWeekBegin = HODateTime.fromHTWeek(this.htWeek);
        int playerAge = (int)entry.getPlayer().getDoubleAgeFromDate(dateWeekBegin);
        int playerAgeUpcomingWeek = (int)entry.getPlayer().getDoubleAgeFromDate(dateWeekBegin.plus(7, ChronoUnit.DAYS));

        // Birthday in this week! Set BG color
        if (playerAge < playerAgeUpcomingWeek) {
            String ageText =  TranslationFacility.tr("ls.player.age.birthday")
                    + " (" + playerAgeUpcomingWeek + " "
                    +  TranslationFacility.tr("ls.player.age.years")
                    + ")";

            var text = colorLabelEntry.getToolTipText();
            if (text == null || text.isEmpty()) {
                colorLabelEntry.setText(ageText);
                colorLabelEntry.setToolTipText(ageText);
            } else {
                colorLabelEntry.setToolTipText( "<html>" + colorLabelEntry.getToolTipText() + "<br>" + ageText + "</html>");
            }
            colorLabelEntry.setBackgroundColor(BIRTHDAY_BG);
        }

        return colorLabelEntry;
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
