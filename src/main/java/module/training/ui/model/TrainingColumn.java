package module.training.ui.model;

import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.UserColumn;
import core.training.TrainingPerWeek;
import module.training.PlayerSkillChange;
import module.training.TrainWeekEffect;

public class TrainingColumn extends UserColumn {
    static int nextId = 0;

    public TrainingColumn(String name, int minWidth) {
        this(nextId++, name, name, minWidth);
    }

    public TrainingColumn(int id, String name, String tooltip, int minWidth) {
        super(id, name, tooltip);
        this.index = this.getId();
        this.minWidth = minWidth;
        preferredWidth = minWidth;
        this.setDisplay(true);
    }

    public TrainingColumn(int id, String name, int minWidth) {
        this(id, name, name, minWidth);
    }

    public IHOTableCellEntry getTableEntry(TrainingPerWeek entry) {
        return null;
    }

    public IHOTableCellEntry getTableEntry(FutureTrainingEntry entry) {
        return null;
    }

    public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
        return null;
    }

    public IHOTableCellEntry getTableEntry(TrainWeekEffect entry) {
        return null;
    }
}
