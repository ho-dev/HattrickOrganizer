package core.gui.model;

import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.UserColumn;


// TODO: investigate wh a second player column is required
class PlayerColumn2 extends UserColumn {


	protected PlayerColumn2(int id, String name){
		super(id,name,name);
		this.minWidth = 20;
		preferredWidth = 80;
	}

	public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem){
		return null;
	}
}
