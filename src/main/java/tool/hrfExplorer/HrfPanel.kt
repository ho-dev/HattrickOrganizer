/*
 * Created on 05.06.2005
 */
package tool.hrfExplorer;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Color;

import javax.swing.JPanel;

/**
 * @author KickMuck
 */

public class HrfPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 594549010839252273L;
	int m_Breite;
	int m_Hoehe;
	
	public HrfPanel(int breite, int hoehe)
	{
		super();
		setBreite(breite);
		setHoehe(hoehe);
		setOpaque(true);
	}
	public HrfPanel(int breite, int hoehe, Color bg)
	{
		super();
		setBreite(breite);
		setHoehe(hoehe);
		setBackground(bg);
		setOpaque(true);
	}
	
	@Override
	public Dimension getPreferredSize()
    {
        return new Dimension(getBreite(),getHoehe());
    }
	@Override
	public Insets getInsets()
	{
		return new Insets(0,0,0,0);
	}
	/**
	 * @return Returns the m_Breite.
	 */
	public int getBreite() {
		return m_Breite;
	}
	/**
	 * @return Returns the m_Hoehe.
	 */
	public int getHoehe() {
		return m_Hoehe;
	}
	/**
	 * @param breite The m_Breite to set.
	 */
	public void setBreite(int breite) {
		m_Breite = breite;
	}
	/**
	 * @param hoehe The m_Hoehe to set.
	 */
	public void setHoehe(int hoehe) {
		m_Hoehe = hoehe;
	}
}
