package tool.hrfExplorer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
/**
 * @author KickMuck
 */
public class HrfTableCellRenderer extends JLabel implements TableCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6661171826323106858L;
	private JLabel label;
	private Color grau = new Color (240,240,240);
	private Color dunkelgrau = new Color (200,200,200);
	private Color rot = new Color (254,200,200);
	private Color hellblau = new Color (235,235,254);
	private Color dunkelblau = new Color (220,220,254);
	private Color gruen = new Color (220,255,220);
	private Color mittelgruen = new Color (70,200,70);
	private Color dunkelgruen = new Color (11,148,4);
	private Color helltuerkis = new Color (53,221,241);
	private Color dunkeltuerkis = new Color (37,165,180);
	private Color gelb = new Color(254,213,90);
	private Color schwarz = new Color(0,0,0);
	private Color weiss = new Color(254,254,254);
	
	public Component getTableCellRendererComponent(JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
            int row,
            int column)
	{
		if((table.getName()).equals("calendar"))
		{
			label = new JLabel();
			if(column == 0)
			{
				label.setBackground(dunkelgrau);
				label.setForeground(weiss);
			}
			else
			{
				if(value.toString().equals(" "))
				{
					label.setBackground(grau);
				}
				else
				{
					if(HrfExplorer.hrfForDay(Integer.parseInt(value.toString())) && column != 0)
					{
						label.setBackground(gruen);
						label.setToolTipText(HrfExplorer.getNameForEvent("DB"));
					}
					else if(HrfExplorer.hrfAsFile(value.toString()))
					{
						label.setBackground(hellblau);
						label.setToolTipText(HrfExplorer.getNameForEvent("FILE"));
					}
					else
					{
						label.setBackground(table.getBackground());
					}
					if(HrfExplorer.isSpecialEvent(Integer.parseInt(value.toString())) && column != 0)
					{
						if(HrfExplorer.getSpecialEvent(Integer.parseInt(value.toString())).equals("L"))
						{
							label.setBorder(BorderFactory.createLineBorder(dunkelgruen,2));
							label.setToolTipText(HrfExplorer.getNameForEvent("L"));
							//label.setIcon(HrfExplorer.getBild("L"));
						}
						else if(HrfExplorer.getSpecialEvent(Integer.parseInt(value.toString())).equals("I"))
						{
							label.setBorder(BorderFactory.createLineBorder(dunkeltuerkis,2));
							label.setToolTipText(HrfExplorer.getNameForEvent("I"));
						}
						else if(HrfExplorer.getSpecialEvent(Integer.parseInt(value.toString())).equals("P"))
						{
							label.setBorder(BorderFactory.createLineBorder(gelb,2));
							label.setToolTipText(HrfExplorer.getNameForEvent("P"));
						}
						else if(HrfExplorer.getSpecialEvent(Integer.parseInt(value.toString())).equals("F"))
						{
							label.setBorder(BorderFactory.createLineBorder(helltuerkis,2));
							label.setToolTipText(HrfExplorer.getNameForEvent("F"));
						}
						else if(HrfExplorer.getSpecialEvent(Integer.parseInt(value.toString())).equals("Q"))
						{
							label.setBorder(BorderFactory.createLineBorder(rot,2));
							label.setToolTipText(HrfExplorer.getNameForEvent("Q"));
						}
					}
				}
			}
		}
		else if(table.getName().equals("import"))
		{
			label = new JLabel();
			if(row % 2 != 0)
			{
				label.setBackground(hellblau);
			}
			else
			{
				label.setBackground(dunkelblau);
			}
		}
		else if(table.getName().equals("filelist"))
		{
			label = new JLabel();
			if(table.getValueAt(row,1).equals("---"))
			{
				label.setBackground(gruen);
			}
			else
			{
				label.setBackground(hellblau);
			}
		}
		else
		{
			label = new JLabel();
		}
		if(value.getClass().equals(ImageIcon.class) == false)
		{
			label.setText(value.toString());
			label.setFont(table.getFont());
			if(label.getBackground().equals(mittelgruen))
			{
				label.setForeground(weiss);
			}
			else
			{
				label.setForeground(table.getForeground());
			}
			
		}
		else
		{
			label.setIcon((ImageIcon)value);
		}
		
		label.setHorizontalAlignment(CENTER);
		label.setOpaque(true);
		
		return label;
	}
}
