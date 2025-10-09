package core.gui.model;

import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.UserColumn;


// TODO: investigate wh a second player column is required
public class PlayerColumn2 extends UserColumn {


	protected PlayerColumn2(int id, String name){
		super(id,name,name);
		this.minWidth = 20;
		preferredWidth = 80;
	}

	public IHOTableCellEntry getTableEntry(PlayerMatchCBItem spielerCBItem){
		return null;
	}
}
