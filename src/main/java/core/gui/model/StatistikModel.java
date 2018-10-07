// %856946727:de.hattrickorganizer.gui.model%
package core.gui.model;

/**
 * Model für das StatistikPanel
 */
public class StatistikModel {
    //~ Instance fields ----------------------------------------------------------------------------

    private java.awt.Color m_clColor = java.awt.Color.blue;
    private java.text.NumberFormat m_clFormat;
    private String m_sName = "";
    private double[] m_clWerte;
    private boolean m_bShow = true;
    private double m_dFaktor = 1;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new StatistikModel object.
     */
    public StatistikModel(double[] werte, String name, boolean show, java.awt.Color farbe,
                          java.text.NumberFormat format) {
        this(werte, name, show, farbe, format, 1);
    }

    /**
     * Creates a new StatistikModel object.
     */
    public StatistikModel(double[] werte, String name, boolean show, java.awt.Color farbe,
                          java.text.NumberFormat format, double faktor) {
        m_clWerte = werte;
        m_sName = name;
        m_bShow = show;
        m_clColor = farbe;
        m_clFormat = format;
        m_dFaktor = faktor;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_clColor.
     *
     * @param m_clColor New value of property m_clColor.
     */
    public final void setColor(java.awt.Color m_clColor) {
        this.m_clColor = m_clColor;
    }

    /**
     * Getter for property m_clColor.
     *
     * @return Value of property m_clColor.
     */
    public final java.awt.Color getColor() {
        return m_clColor;
    }

    /**
     * Setter for property m_iFaktor.
     *
     * @param m_dFaktor New value of property m_iFaktor.
     */
    public final void setFaktor(double m_dFaktor) {
        this.m_dFaktor = m_dFaktor;
    }

    /**
     * Getter for property m_iFaktor.
     *
     * @return Value of property m_iFaktor.
     */
    public final double getFaktor() {
        return m_dFaktor;
    }

    /**
     * Setter for property m_clFormat.
     *
     * @param m_clFormat New value of property m_clFormat.
     */
    public final void setFormat(java.text.NumberFormat m_clFormat) {
        this.m_clFormat = m_clFormat;
    }

    /**
     * Getter for property m_clFormat.
     *
     * @return Value of property m_clFormat.
     */
    public final java.text.NumberFormat getFormat() {
        return m_clFormat;
    }

    //-----------------------------------
    public final double getMaxValue() {
        double max = 0;

        for (int i = 0; (m_clWerte != null) && (i < m_clWerte.length); i++) {
            if (m_clWerte[i] > max) {
                max = m_clWerte[i];
            }
        }

        return (max);
    }

    public final double getMinValue() {
        double min = 0;

        for (int i = 0; (m_clWerte != null) && (i < m_clWerte.length); i++) {
            if (m_clWerte[i] < min) {
                min = m_clWerte[i];
            }
        }

        return (min);
    }

    /**
     * Setter for property m_sName.
     *
     * @param m_sName New value of property m_sName.
     */
    public final void setName(java.lang.String m_sName) {
        this.m_sName = m_sName;
    }

    /**
     * Getter for property m_sName.
     *
     * @return Value of property m_sName.
     */
    public final java.lang.String getName() {
        return m_sName;
    }

    /**
     * Setter for property m_bShow.
     *
     * @param m_bShow New value of property m_bShow.
     */
    public final void setShow(boolean m_bShow) {
        this.m_bShow = m_bShow;
    }

    /**
     * Getter for property m_bShow.
     *
     * @return Value of property m_bShow.
     */
    public final boolean isShow() {
        return m_bShow;
    }

    /**
     * Setter for property m_clWerte.
     *
     * @param m_clWerte New value of property m_clWerte.
     */
    public final void setWerte(double[] m_clWerte) {
        this.m_clWerte = m_clWerte;
    }

    /**
     * Getter for property m_clWerte.
     *
     * @return Value of property m_clWerte.
     */
    public final double[] getWerte() {
        return this.m_clWerte;
    }
}
