package module.playeranalysis.skillcompare;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.TranslationFacility;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;


/**
 * @author KickMuck
 */
class PlayerTable extends JTable{

	private static final long serialVersionUID = 1453037819569111763L;
	private int anzCols1;

	PlayerTable(TableSorter tm)
	{
		super(tm);
		anzCols1 = tm.getColumnCount();
	    TableColumn col;
	    setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

			/**
			 *
			 */
			private static final long serialVersionUID = 5342806852198198162L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {

				setText(value.toString());
				setHorizontalAlignment(CENTER);
				if(isSelected){
					setBackground(table.getSelectionBackground());
					setForeground(table.getSelectionForeground());
				} else {

					setBackground(column==1?table.getBackground():ThemeManager.getColor(HOColorName.PLAYER_SUBPOS_BG));
					setForeground(table.getForeground());
				}
				return this;
			}
		});
	    setBackground(ThemeManager.getColor(HOColorName.PLAYER_POS_BG));
	    setForeground(ThemeManager.getColor(HOColorName.TABLEENTRY_FG));
	    setSelectionBackground(ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG));
	    setSelectionForeground(ThemeManager.getColor(HOColorName.TABLE_SELECTION_FG));
	    for(int qq = 0; qq < anzCols1; qq++)
	    {
	    	int width = 65;
	    	col = this.getColumnModel().getColumn(qq);
	    	if(qq ==1)
	    		width = 90;
	    	col.setPreferredWidth(width);
	    }
	    this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}


	PlayerTable(TableSorter tm, PlayerTableModel ptm){

		super(tm);
		anzCols1 = tm.getColumnCount();
	    TableColumn col;
	    setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG));
	    setForeground(ThemeManager.getColor(HOColorName.TABLEENTRY_FG));
	    setSelectionBackground(ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG));
	    setSelectionForeground(ThemeManager.getColor(HOColorName.TABLE_SELECTION_FG));
	    for(int pp = 0; pp < anzCols1; pp++)
	    {
	    	int width = 0;
	    	String columnName = ptm.getColumnName(pp);
	    	col = this.getColumnModel().getColumn(pp);
	    	if(pp > 0)
	    	{
	    		col.setCellRenderer(new PlayerTableCellRenderer());
	    	}
	    	if(columnName.equals(TranslationFacility.tr("ls.player.position_short.keeper"))
	    			|| columnName.equals(TranslationFacility.tr("ls.player.position_short.centraldefender"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.centraldefendertowardswing"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.centraldefenderoffensive"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.wingback"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.wingbacktowardsmiddle"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.wingbackoffensive"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.wingbackdefensive"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.innermidfielder"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.innermidfielderdefensive"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.innermidfieldertowardswing"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.innermidfielderoffensive"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.winger"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.wingertowardsmiddle"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.wingeroffensive"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.wingerdefensive"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.forward"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.forwarddefensive"))
					|| columnName.equals(TranslationFacility.tr("ls.player.position_short.forwardtowardswing"))
					|| columnName.equals(TranslationFacility.tr("Gruppe"))
	    	)
	    	{
	    		width = 60;
	    	}
	    	else if(columnName.equals(TranslationFacility.tr("ls.player.name"))
	    			|| columnName.equals(TranslationFacility.tr("BestePosition"))
	    			)
	    	{
	    		width = 175;
	    	}
	    	else if(columnName.equals(TranslationFacility.tr("ls.player.short_leadership"))
	    			|| columnName.equals(TranslationFacility.tr("ls.player.short_experience"))
					|| columnName.equals(TranslationFacility.tr("ls.player.short_form"))
					|| columnName.equals(TranslationFacility.tr("ls.player.skill_short.stamina"))
					|| columnName.equals(TranslationFacility.tr("ls.player.skill_short.keeper"))
					|| columnName.equals(TranslationFacility.tr("ls.player.skill_short.defending"))
					|| columnName.equals(TranslationFacility.tr("ls.player.skill_short.playmaking"))
					|| columnName.equals(TranslationFacility.tr("ls.player.skill_short.passing"))
					|| columnName.equals(TranslationFacility.tr("ls.player.skill_short.winger"))
					|| columnName.equals(TranslationFacility.tr("ls.player.skill_short.scoring"))
					|| columnName.equals(TranslationFacility.tr("ls.player.skill_short.setpieces"))
					|| columnName.equals(TranslationFacility.tr("ls.player.short_loyalty"))
					|| columnName.equals(TranslationFacility.tr("ls.player.short_motherclub"))
					)
	    	{
	    		width = 40;
	    	}
	    	else if(columnName.equals(TranslationFacility.tr("ls.player.wage"))
	    			|| columnName.equals(TranslationFacility.tr("ls.player.id"))
					|| columnName.equals(TranslationFacility.tr("ls.player.tsi"))
					)
	    	{
	    		width = 80;
	    	}
	    	else
	    	{
	    		width = 30;
	    	}
	    	col.setPreferredWidth(width);
	    }

	    this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
}
