package module.matches.statistics;

import core.gui.comp.table.UserColumn;

public class MatchesOverviewColumn extends UserColumn {

	protected MatchesOverviewColumn(int id, String name) {
		super(id, name);
		setDisplay(true);
	}

	protected MatchesOverviewColumn(int id,String name, String tooltip,int minWidth){
		super(id,name,tooltip);
		this.minWidth = minWidth;
		preferredWidth = minWidth;
	}
	
	
}
