package module.transfer.history;

import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.UserColumn;
import core.model.player.Player;
import module.transfer.PlayerTransfer;

public class TransferTableColumn extends UserColumn {

    protected TransferTableColumn(int id, String name) {
        this(id, name, name, 50);
    }

    public TransferTableColumn(int id, String name, String tooltip, int minWidth) {
        super(id, name, tooltip);
        this.index= this.getId();
        this.minWidth=minWidth;
        this.setPreferredWidth(minWidth);
        this.setDisplay(true);
    }

    public IHOTableCellEntry getTableEntry(PlayerTransfer transfer) {
        return null;
    }
    public IHOTableCellEntry getTableEntry(Player playerInfo) {
        return null;
    }

    public IHOTableCellEntry getPlayerInfoTableEntry(PlayerTransfer transfer) {
        if (transfer.getPlayerInfo() != null){
            return getTableEntry(transfer.getPlayerInfo());
        }
        return null;
    }


}
