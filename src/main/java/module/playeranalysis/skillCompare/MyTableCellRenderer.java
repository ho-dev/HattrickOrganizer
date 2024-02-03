/*
 * Created on 06.07.2004
 */
package module.playeranalysis.skillCompare;

import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.MatchRoleID;
import core.util.Helper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

class MyTableCellRenderer  implements TableCellRenderer{
	private Color gelb;
	private Color gruen;
	private final Color dklgruen = new Color (0,200,0);
	private final Color rot = new Color (255,200,200);
	private final Color dklrot = new Color (200,0,0);
	private final Color hellblau;
	private final Color dunkelblau;

	private final String[] name = new String[2];
    private final DecimalFormat df = new DecimalFormat("#,###,##0.00");

	public MyTableCellRenderer()
	{
		gelb = ThemeManager.getColor(HOColorName.PLAYER_SKILL_BG);
		gruen= ThemeManager.getColor(HOColorName.PLAYER_SKILL_SPECIAL_BG);
		hellblau = ThemeManager.getColor(HOColorName.PLAYER_SUBPOS_BG);
		dunkelblau = ThemeManager.getColor(HOColorName.PLAYER_POS_BG);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
            int row,
            int column)
	{
        JLabel label = new JLabel();

		if(table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.name"))) {
			int i = 0;
			int spezWert = 0;
			StringTokenizer tk = new StringTokenizer(table.getValueAt(row,column).toString(),";");
			while (tk.hasMoreTokens()) {
		         name[i]=tk.nextToken();
		         i++;
		    }
			try	{
				spezWert = Integer.parseInt(name[1]);
			}
			catch(Exception ignored){}

			Icon ic = ImageUtilities.getSmallPlayerSpecialtyIcon(HOIconName.SPECIALTIES[spezWert]);
			label.setLayout(new BorderLayout());
			label.setText(name[0]);
			JLabel l2 = new JLabel(ic,SwingConstants.LEFT);
			label.add(l2,BorderLayout.EAST);
			label.setBackground(table.getBackground());
			label.validate();
		}
		else if(column == 2) {
            int natWert = (Integer) table.getValueAt(row, column);
			label.setIcon(ImageUtilities.getCountryFlagIcon(natWert));
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setBackground(table.getBackground());
		}
		else if(table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("Gruppe")))	{


			String group = ((String)table.getValueAt(row,column));
			if(group != null && group.length() > 3)
				label.setIcon(ThemeManager.getIcon(group));
			label.setBackground(table.getBackground());
		} else if(table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("BestePosition")))		{
			// we parse back into best position and best rating value
			byte tmpPos = ((Float)table.getValueAt(row,column)).byteValue();
			float ratingValue = ((Float) table.getValueAt(row, column) - tmpPos) * 1000;
			label.setText(MatchRoleID.getNameForPosition(tmpPos) + " ("+ String.format("%.1f", ratingValue) +"%)");
			label.setBackground(table.getBackground());
		} else if(table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.short_motherclub"))) {
			double skillwert = 0;
			String skillwertS;
			try	{
				skillwertS = (table.getValueAt(row,column)).toString();
				skillwert = Double.parseDouble(skillwertS);

			} catch(Exception ignored){}

			int skillWertNew = (int) skillwert;
			int skillWertOld = (int) ((skillwert - skillWertNew) * 100 + 0.1);
			int changeWert = skillWertNew - skillWertOld;
			if (skillwert == 2 || skillWertNew == 2 || skillWertOld == 2)
			{
				label.setIcon(ThemeManager.getIcon(HOIconName.HOMEGROWN));
				label.setHorizontalAlignment(SwingConstants.CENTER);
			}
			if (changeWert < 0)
				label.setBackground(Color.RED);
			else if (changeWert > 0)
				label.setBackground(Color.GREEN);
			else
				label.setBackground(table.getBackground());
		}
		else if(column<19 &&
				(table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.short_experience"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.short_leadership"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.short_form"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.skill_short.stamina"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.skill_short.keeper"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.skill_short.defending"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.skill_short.playmaking"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.skill_short.passing"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.skill_short.winger"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.skill_short.scoring"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.skill_short.setpieces"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.short_loyalty")))
				)
		{
			double skillwert = 0;
			String skillwertS;
			try
			{
				skillwertS = (table.getValueAt(row,column)).toString();
				skillwert = Double.parseDouble(skillwertS);
			}
			catch(Exception ignored){}

			int skillWertNew = (int) skillwert;
			int skillWertOld = (int) ((skillwert - skillWertNew) * 100 + 0.1);
			int changeWert = skillWertNew - skillWertOld;

			Icon ii =  ImageUtilities.getImageIcon4Change(changeWert,true);
			label = new JLabel(""+skillWertNew,ii,SwingConstants.CENTER);
			label.setHorizontalTextPosition(SwingConstants.LEADING);
			if(table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.short_experience"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.short_leadership"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.short_form"))
				)
			{
				label.setBackground(gruen);
			}
			else
			{
				label.setBackground(gelb);
			}

		}
		else if(column>=19 &&
				(table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.centraldefendertowardswing"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.centraldefenderoffensive"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingbacktowardsmiddle"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingbackdefensive"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingbackoffensive"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfieldertowardswing"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfielderdefensive"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfielderoffensive"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingertowardsmiddle"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingeroffensive"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingerdefensive"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.forwarddefensive"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.keeper"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.centraldefender"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingback"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfielder"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.winger"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.forward"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.forwardtowardswing")))
				)
		{
			int i = 0;
			float neuerWert = 0;
			String []skill = new String[2];
			float changeValue = 0;
			String chValue = "";

			StringTokenizer tk = new StringTokenizer(table.getValueAt(row,column).toString(),";");
			while (tk.hasMoreTokens()) {
		         skill[i]=tk.nextToken();
		         i++;
		     }
			try
			{
				neuerWert = Float.parseFloat(skill[0]);
				changeValue = Helper.round(Float.parseFloat(skill[1]),UserParameter.instance().nbDecimals);
			}
			catch(Exception ignored){}

            JLabel wertAlt = new JLabel();

			if(changeValue > 0)
			{
				chValue += "+" + changeValue;
				wertAlt.setForeground(dklgruen);
				wertAlt.setText(chValue);
			}
			else if(changeValue == 0){
				wertAlt.setText("");
			}
			else {
				chValue += Float.toString(changeValue);
				wertAlt.setForeground(dklrot);
				wertAlt.setText("" + changeValue);
			}

            JLabel wertNeu = new JLabel("" + neuerWert);
			wertNeu.setHorizontalAlignment(SwingConstants.RIGHT);
			wertAlt.setHorizontalAlignment(SwingConstants.CENTER);

			label.setLayout(new GridLayout());
			label.add(wertNeu);
			label.add(wertAlt);

			if(table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.keeper"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.centraldefender"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingback"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfielder"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.winger"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.position_short.forward"))
				)
			{
				label.setBackground(dunkelblau);
				wertNeu.setBackground(dunkelblau);
				wertAlt.setBackground(dunkelblau);
			}
			else
			{
				label.setBackground(hellblau);
				wertNeu.setBackground(hellblau);
				wertAlt.setBackground(hellblau);
			}

			label.validate();
		}
		else if(table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.wage")))	{
			label.setText(Helper.getNumberFormat(true, 0).format(value));

			label.setHorizontalAlignment(SwingConstants.RIGHT);
			label.setBackground(table.getBackground());
		}
		else if(table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.tsi"))
				|| table.getColumnName(column).equals(HOVerwaltung.instance().getLanguageString("ls.player.id"))){
			label.setText(value.toString());
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			label.setBackground(table.getBackground());
		}
		else if(!(table.getValueAt(0, column) instanceof Boolean)) {
			label.setText(value.toString());
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setBackground(table.getBackground());
		}
		label.setOpaque(true);
		label.setFont(table.getFont());
		label.setForeground(table.getForeground());


		if(isSelected){
			label.setBackground(table.getSelectionBackground());
			label.setForeground(table.getSelectionForeground());
		}
		if(table.getValueAt(row,0) == Boolean.TRUE)
		{
			label.setBackground(rot);
		}
			return label;
	}
}
