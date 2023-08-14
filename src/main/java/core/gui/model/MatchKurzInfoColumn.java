package core.gui.model;

import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.UserColumn;
import core.model.match.MatchKurzInfo;
import core.model.match.Matchdetails;

/**
 * column shows values from a matchKurzInfo
 * @author Thorsten Dietz
 * @since 1.36
 */
public class MatchKurzInfoColumn extends UserColumn {

	/**
	 * constructor
	 * @param id
	 * @param name
	 * @param minWidth
	 */
	protected MatchKurzInfoColumn(int id,String name,int minWidth){
		this(id,name,name,minWidth);
		this.display = true;
		
	}
	
	/**
	 * constructor
	 * @param id
	 * @param name
	 * @param tooltip
	 * @param minWidth
	 */
	protected MatchKurzInfoColumn(int id,String name, String tooltip,int minWidth){
		super(id,name,tooltip);
		this.minWidth = minWidth;
		preferredWidth = minWidth;
		this.display = true;
	}
	
	/**
	 * overwritten by created column
	 * @param match
	 * @return
	 */
	public IHOTableEntry getTableEntry(MatchKurzInfo match){
		return null;
	}
	
	/**
	 * overwritten by created column
	 * @param match
	 * @return
	 */
	public IHOTableEntry getTableEntry(MatchKurzInfo match, Matchdetails matchDetails) {
		return null;
	}
	
	
	
	/**
	 * overwritten by created column
	 * @param spielerCBItem
	 * @return
	 */
	public IHOTableEntry getTableEntry(PlayerMatchCBItem spielerCBItem){
		return null;
	}


}
