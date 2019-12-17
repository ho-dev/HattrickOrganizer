// %1341813328:de.hattrickorganizer.gui.templates%
package core.gui.comp.entry;

import core.gui.comp.renderer.HODefaultTableCellRenderer;

import java.awt.Color;
import java.text.NumberFormat;

import javax.swing.JProgressBar;



/**
 * Progress bar as table cell.
 */
public class ProgressbarTableEntry extends AbstractHOTableEntry {
    //~ Instance fields ----------------------------------------------------------------------------

    private Color m_clBGColor = Color.WHITE;
    private Color m_clFGColor = Color.BLUE;
    private JProgressBar m_clProgressbar;
    private String m_sAddText = "";
    private double m_dFaktor4Label = 1;
    private int m_iAktuellerWert;
    private int m_iMaxWert;
    private int m_iMinWert;
    private NumberFormat nf;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new ProgressbarTableEntry object.
     *
     * @param aktuellerwert current value
     * @param minwert minimum value
     * @param maxwert maximum value
     * @param fractionDigits fraction digits
     * @param faktor4Label factor
     * @param bgcolor background color
     * @param fgcolor foreground color
     * @param addText additional label text
     */
    public ProgressbarTableEntry(int aktuellerwert, int minwert, int maxwert, int fractionDigits,
                                 double faktor4Label, Color bgcolor,
                                 Color fgcolor, String addText) {
        m_iAktuellerWert = aktuellerwert;
        m_iMaxWert = maxwert;
        m_iMinWert = minwert;
        nf = NumberFormat.getNumberInstance();
        nf.setMinimumFractionDigits(fractionDigits);
        nf.setMaximumFractionDigits(fractionDigits);
        m_dFaktor4Label = faktor4Label;
        m_clBGColor = bgcolor;
        m_clFGColor = fgcolor;
        m_sAddText = addText;
        createComponent();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_clFGColor.
     *
     * @param m_sAddText New value of property m_clFGColor.
     */
    public final void setAddText(String m_sAddText) {
        this.m_sAddText = m_sAddText;
        updateComponent();
    }

    /**
     * Getter for property m_clFGColor.
     *
     * @return Value of property m_clFGColor.
     */
    public final String getAddText() {
        return m_sAddText;
    }

    /**
     * Setter for property m_iAktuellerWert.
     *
     * @param m_iAktuellerWert New value of property m_iAktuellerWert.
     */
    public final void setAktuellerWert(int m_iAktuellerWert) {
        this.m_iAktuellerWert = m_iAktuellerWert;
        updateComponent();
    }

    /**
     * Getter for property m_iAktuellerWert.
     *
     * @return Value of property m_iAktuellerWert.
     */
    public final int getAktuellerWert() {
        return m_iAktuellerWert;
    }

    /**
     * Setter for property m_clBGColor.
     *
     * @param m_clBGColor New value of property m_clBGColor.
     */
    public final void setBGColor(Color m_clBGColor) {
        this.m_clBGColor = m_clBGColor;
        updateComponent();
    }

    /**
     * Getter for property m_clBGColor.
     *
     * @return Value of property m_clBGColor.
     */
    public final Color getBGColor() {
        return m_clBGColor;
    }

    /**
     * Implement getComponent().
     */
	public final javax.swing.JComponent getComponent(boolean isSelected) {
        if (isSelected) {
            m_clProgressbar.setOpaque(true);
            m_clProgressbar.setBackground(HODefaultTableCellRenderer.SELECTION_BG);
        } else {
            m_clProgressbar.setOpaque(true);
            m_clProgressbar.setBackground(m_clBGColor);
        }

        return m_clProgressbar;
    }

    /**
     * Setter for property m_clFGColor.
     *
     * @param m_clFGColor New value of property m_clFGColor.
     */
    public final void setFGColor(Color m_clFGColor) {
        this.m_clFGColor = m_clFGColor;
        updateComponent();
    }

    /**
     * Getter for property m_clFGColor.
     *
     * @return Value of property m_clFGColor.
     */
    public final Color getFGColor() {
        return m_clFGColor;
    }

    /**
     * Setter for property m_dFaktor4Label.
     *
     * @param m_dFaktor4Label New value of property m_dFaktor4Label.
     */
    public final void setFaktor4Label(double m_dFaktor4Label) {
        this.m_dFaktor4Label = m_dFaktor4Label;
        updateComponent();
    }

    /**
     * Getter for property m_dFaktor4Label.
     *
     * @return Value of property m_dFaktor4Label.
     */
    public final double getFaktor4Label() {
        return m_dFaktor4Label;
    }

    /**
     * Setter for property m_iMaxWert.
     *
     * @param m_iMaxWert New value of property m_iMaxWert.
     */
    public final void setMaxWert(int m_iMaxWert) {
        this.m_iMaxWert = m_iMaxWert;
        updateComponent();
    }

    /**
     * Getter for property m_iMaxWert.
     *
     * @return Value of property m_iMaxWert.
     */
    public final int getMaxWert() {
        return m_iMaxWert;
    }

    /**
     * Setter for property m_iMinWert.
     *
     * @param m_iMinWert New value of property m_iMinWert.
     */
    public final void setMinWert(int m_iMinWert) {
        this.m_iMinWert = m_iMinWert;
        updateComponent();
    }

    /**
     * Getter for property m_iMinWert.
     *
     * @return Value of property m_iMinWert.
     */
    public final int getMinWert() {
        return m_iMinWert;
    }

    /**
     * Setter for property m_iNachkommastellen.
     *
     * @param fractionDigits New value of property m_iNachkommastellen.
     */
    public final void setNachkommastellen(int fractionDigits) {
        nf.setMinimumFractionDigits(fractionDigits);
    	nf.setMaximumFractionDigits(fractionDigits);
        updateComponent();
    }

    /**
     * Clear value.
     */
	public final void clear() {
        m_clProgressbar.setString("");
        m_clProgressbar.setValue(0);
    }

    /**
     * Implement compareTo() for sorting.
     */
	public final int compareTo(IHOTableEntry o) {
        if (o instanceof ProgressbarTableEntry) {
            final ProgressbarTableEntry entry = (ProgressbarTableEntry) o;

            if (getAktuellerWert() < entry.getAktuellerWert()) {
                return -1;
            } else if (getAktuellerWert() > entry.getAktuellerWert()) {
                return 1;
            } else {
                return 0;
            }
        }

        return 0;
    }

    /**
     * Create the component and set the text.
     */
	public final void createComponent() {
        m_clProgressbar = new JProgressBar();
        m_clProgressbar.setStringPainted(true);
        updateComponent();
    }

    /**
     * Update label text.
     */
	public final void updateComponent() {
        m_clProgressbar.setMinimum(m_iMinWert);
        m_clProgressbar.setMaximum(m_iMaxWert);
        m_clProgressbar.setValue(m_iAktuellerWert);
        m_clProgressbar.setBackground(m_clBGColor);
        m_clProgressbar.setForeground(m_clFGColor);
        m_clProgressbar.setString(nf.format(m_iAktuellerWert * m_dFaktor4Label) + m_sAddText);
    }
}
