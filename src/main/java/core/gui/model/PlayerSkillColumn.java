package core.gui.model;

import core.constants.player.PlayerSkill;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoppelLabelEntry;
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
class PlayerSkillColumn extends PlayerColumn {
	
	/** id for the skill **/
	private int skill;
	
	/** different color for some skills **/
	private Color background;
	
	/**
	 * constructor
	 * @param id
	 * @param name
	 * @param tooltip
	 * @param skill
	 */
	protected PlayerSkillColumn(int id,String name, String tooltip,int skill){
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
		return new DoppelLabelEntry(getSkillValue(player),getCompareValue(player,comparePlayer));
	}
	
	public  IHOTableEntry getSkillValue(Player player){
		if( skill == PlayerSkill.FORM
				|| skill == PlayerSkill.STAMINA
				|| skill == PlayerSkill.LEADERSHIP
				|| skill == PlayerSkill.LOYALTY){
		return new ColorLabelEntry(getSkill(player),
                background,
                false, 0);
		}
		return new SkillEntry(getSkill(player)
                + player.getSubskill4Pos(skill),
              ColorLabelEntry.FG_STANDARD,
              background);
	}
	/**
	 * return a value if comparePlayer is not null
	 * @param player
	 * @param comparePlayer
	 * @return ColorLabelEntry
	 */
	public ColorLabelEntry getCompareValue(Player player, Player comparePlayer){
		if(comparePlayer == null){
			return new ColorLabelEntry("",
	                   ColorLabelEntry.FG_STANDARD,
	                   background,
	                   SwingConstants.RIGHT);
		}
		
		return new ColorLabelEntry(getSkill(player)
                - getSkill(comparePlayer),
                  player.getSubskill4Pos(skill)
                - comparePlayer
                  .getSubskill4Pos(skill),
                !comparePlayer.isOld(),
                background,
                true);
		
	}
	
	/**
	 * returns right value for the skill
	 * @param player
	 * @return
	 */
	private int getSkill(Player player){
		switch(skill){
		case PlayerSkill.KEEPER: 		return player.getGKskill();
		case PlayerSkill.DEFENDING: 	return player.getDEFskill();
		case PlayerSkill.PASSING: 		return player.getPSskill();
		case PlayerSkill.WINGER: 		return player.getWIskill();
		case PlayerSkill.PLAYMAKING: 	return player.getPMskill();
		case PlayerSkill.SET_PIECES: 		return player.getSPskill();
		case PlayerSkill.SCORING: 		return player.getSCskill();
		case PlayerSkill.EXPERIENCE: 	return player.getErfahrung();
		case PlayerSkill.FORM: 			return player.getForm();
		case PlayerSkill.STAMINA: 		return player.getKondition();
		case PlayerSkill.LEADERSHIP:		return player.getFuehrung();
		case PlayerSkill.LOYALTY: 		return player.getLoyalty();
		
		}
		return 0;
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
