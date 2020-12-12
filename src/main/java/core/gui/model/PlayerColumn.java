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
	 * @param id
	 * @param name
	 * @param tooltip
	 */
	public PlayerColumn(int id,String name,String tooltip){
		super(id,name,tooltip);
	}
	
	/**
	 * constructor
	 * @param id
	 * @param name
	 * @param minWidth
	 */
	public PlayerColumn(int id,String name,int minWidth){
		this(id,name,name,minWidth);
	}
	
	/**
	 * constructor
	 * @param id
	 * @param name
	 * @param tooltip
	 * @param minWidth
	 */
	public PlayerColumn(int id,String name, String tooltip,int minWidth){
		super(id,name,tooltip);
		this.minWidth = minWidth;
		preferredWidth = minWidth;
	}
	
	/**
	 * returns a TableEntry
	 * overwritten by all created columns
	 * @param player
	 * @param comparePlayer
	 * @return
	 */
	public IHOTableEntry getTableEntry(Player player, @Nullable Player comparePlayer){
		return new ColorLabelEntry(getValue(player),
	            ColorLabelEntry.BG_STANDARD, false, 0);
	}
	
	/**
	 * return the individual playerValue
	 * overwritten by created columns
	 * @param player
	 * @return
	 */
	public int getValue(Player player){
		return player.getSpielerID();
	}
	

}
