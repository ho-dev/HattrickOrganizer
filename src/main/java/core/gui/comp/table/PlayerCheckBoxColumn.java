package core.gui.comp.table;

import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.model.PlayerColumn;
import core.model.player.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerCheckBoxColumn extends PlayerColumn {

    public PlayerCheckBoxColumn(int id, String name, String tooltip, int minWidth) {
        super(id, name, tooltip);
        this.minWidth = minWidth;
        this.preferredWidth = minWidth;
    }

    public IHOTableCellEntry getTableEntry(@NotNull Player player) {
        return null;
    }
}
