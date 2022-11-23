package core.gui.model;


import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.UserColumn;
import core.model.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Column shows skill of a player
 * @author Thorsten Dietz
 * @since V1.36
 *
 */
public class PlayerColumn extends UserColumn {

	/**
	 * constructor
	 * @param id column identifier
	 * @param name column header name
	 * @param tooltip tooltip text
	 */
	public PlayerColumn(int id,String name,String tooltip){
		super(id,name,tooltip);
		setDisplay(true);
	}
	
	/**
	 * constructor
	 * @param id column identifier
	 * @param name column header name
	 * @param minWidth minimum width
	 */
	public PlayerColumn(int id,String name,int minWidth){
		this(id,name,name,minWidth);
		setDisplay(true);
	}
	
	/**
	 * constructor
	 * @param id column identifier
	 * @param name column header name
	 * @param tooltip tooltip text
	 * @param minWidth minimum width
	 */
	public PlayerColumn(int id,String name, String tooltip,int minWidth){
		super(id,name,tooltip);
		this.minWidth = minWidth;
		preferredWidth = minWidth;
		setDisplay(true);
	}
	
	/**
	 * returns a TableEntry
	 * overwritten by all created columns
	 * @param player Player
	 * @param comparePlayer Player status to compare with
	 * @return IHOTableEntry
	 */
	public IHOTableEntry getTableEntry(Player player, @Nullable Player comparePlayer){
		return new ColorLabelEntry(getValue(player),
	            ColorLabelEntry.BG_STANDARD, false, 0);
	}
	
	/**
	 * return the individual playerValue
	 * overwritten by created columns
	 * @param player Player
	 * @return player id
	 */
	public int getValue(Player player){
		return player.getPlayerID();
	}
	

}
