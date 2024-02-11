package core.gui.model;

import core.constants.player.PlayerSkill;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoubleLabelEntries;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.entry.SkillEntry;
import core.model.player.Player;
import core.util.Helper;

import java.awt.Color;

import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;



/**
 * Column shows a player skill of a player
 * @author Thorsten Dietz
 * @since 1.36
 */
public class PlayerSkillColumn extends PlayerColumn {
	
	/** id for the skill **/
	private final PlayerSkill skill;
	
	/** different color for some skills **/
	private final Color background;
	
	/**
	 * constructor
	 * @param id column identifier
	 * @param name column header name
	 * @param tooltip tooltip text
	 * @param skill Player skill type
	 */
	protected PlayerSkillColumn(int id,String name, String tooltip,PlayerSkill skill){
		super(id,name,tooltip);
		this.skill = skill;
		background = (skill == PlayerSkill.EXPERIENCE
				||  skill == PlayerSkill.FORM
				|| skill == PlayerSkill.LEADERSHIP
				)?ColorLabelEntry.BG_PLAYERSPECIALVALUES:ColorLabelEntry.BG_SINGLEPLAYERVALUES;
	}
	
	/**
	 * overwritten by created columns
	 */
	@Override
	public IHOTableEntry getTableEntry(Player player, Player comparePlayer){
		return new DoubleLabelEntries(getSkillValue(player),getCompareValue(player,comparePlayer));
	}
	
	public  IHOTableEntry getSkillValue(Player player){
		var value = player.getValue4Skill(skill);
		if( skill == PlayerSkill.FORM
//				|| skill == PlayerSkill.STAMINA
				|| skill == PlayerSkill.LEADERSHIP
				|| skill == PlayerSkill.LOYALTY){
		return new ColorLabelEntry(value,
                background,
                false, 0);
		}
		return new SkillEntry(value + player.getSub4Skill(skill),
              ColorLabelEntry.FG_STANDARD,
              background);
	}
	/**
	 * Get a value if comparePlayer is not null
	 * @param player Player
	 * @param comparePlayer Player to compare with
	 * @return ColorLabelEntry
	 */
	public ColorLabelEntry getCompareValue(Player player, Player comparePlayer){
		if(comparePlayer == null){
			return new ColorLabelEntry("",
	                   ColorLabelEntry.FG_STANDARD,
	                   background,
	                   SwingConstants.RIGHT);
		}
		
		return new ColorLabelEntry(player.getValue4Skill(skill)
                - comparePlayer.getValue4Skill(skill),
                  player.getSub4Skill(skill)
                - comparePlayer.getSub4Skill(skill),
                !comparePlayer.isGoner(),
                background,
                true);
		
	}

	/**
	 * overwrite the method from UserColumn
	 */
	@Override
	public void setSize(TableColumn column){
		final int breite = (int) (55d * (1d + ((core.model.UserParameter.instance().nbDecimals - 1) / 4.5d)));
		column.setMinWidth(20);
		column.setPreferredWidth((preferredWidth == 0)?Helper.calcCellWidth(breite):preferredWidth);
	}
}
