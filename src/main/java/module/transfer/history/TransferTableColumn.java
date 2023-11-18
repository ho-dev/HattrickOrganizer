package module.transfer.history;

import core.gui.comp.entry.IHOTableEntry;
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

    public IHOTableEntry getTableEntry(PlayerTransfer transfer) {
        return null;
    }
    public IHOTableEntry getTableEntry(Player playerInfo) {
        return null;
    }

    public IHOTableEntry getPlayerInfoTableEntry(PlayerTransfer transfer) {
        if (transfer.getPlayerInfo() != null){
            return getTableEntry(transfer.getPlayerInfo());
        }
        return null;
    }


}
