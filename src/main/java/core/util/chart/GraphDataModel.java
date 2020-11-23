package core.util.chart;


import org.jetbrains.annotations.Nullable;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GraphDataModel {

    private java.text.NumberFormat m_clFormat;
    private String m_sName;
    private double[] m_values;
    private List<Double> lValues;
    private boolean m_bShow;
    private boolean dataBasedBoundaries = false;
    private double m_dFactor;
    private BasicStroke m_LineStyle;
    private Marker m_MarkerStyle;
    private java.awt.Color m_LineColor;
    private int y_axisGroup;

    public int getY_axisGroup() {
        return y_axisGroup;
    }

    public Marker getMarkerStyle() {
        return m_MarkerStyle;
    }

    public void setM_MarkerStyle(Marker m_MarkerStyle) {
        this.m_MarkerStyle = m_MarkerStyle;
    }



    public List<Double> getlValues() {
        return lValues;
    }

    public BasicStroke getLineStyle() {
        return m_LineStyle;
    }

    public void setM_LineStyle(BasicStroke m_LineStyle) {
        this.m_LineStyle = m_LineStyle;
    }

    public GraphDataModel(double[] values, String name, boolean show, java.awt.Color color,
                          java.text.NumberFormat format, int yAxisGroup) {
        this(values, name, show, color, SeriesLines.SOLID, SeriesMarkers.DIAMOND, format, 1, false);
    }

    public GraphDataModel(double[] values, String name, boolean show, java.awt.Color color,
                          java.text.@Nullable NumberFormat format) {
        this(values, name, show, color, format, 1);
    }

    public GraphDataModel(double[] values, String name, boolean show, java.awt.Color color,
                          java.text.NumberFormat format, double factor) {
        this(values, name, show, color, SeriesLines.SOLID, SeriesMarkers.DIAMOND, format, factor, false);
    }

    public GraphDataModel(double[] values, String name, boolean show, java.awt.Color color,
                          java.text.NumberFormat format, double factor,  int yAxisGroup) {
        this(values, name, show, color, SeriesLines.SOLID, SeriesMarkers.DIAMOND, format, factor, false);
    }

    public GraphDataModel(double[] values, String name, boolean show, java.awt.Color color, BasicStroke lineStyle,
                          Marker markerStyle, java.text.NumberFormat format, double factor, boolean second_Y_axis) {
        m_values = values;
        lValues = Arrays.stream(values).boxed().collect(Collectors.toList());
        m_sName = name;
        m_bShow = show;
        m_LineColor = color;
        m_clFormat = format;
        m_dFactor = factor;
        m_LineStyle = lineStyle;
        m_MarkerStyle = markerStyle;
        if (second_Y_axis) y_axisGroup = 1;
        else y_axisGroup = 0;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_clColor.
     *
     * @param m_clColor New value of property m_clColor.
     */
    public final void setColor(java.awt.Color m_clColor) {
        this.m_LineColor = m_clColor;
    }

    /**
     * Getter for property m_clColor.
     *
     * @return Value of property m_clColor.
     */
    public final java.awt.Color getColor() {
        return m_LineColor;
    }

    /**
     * Setter for property m_iFaktor.
     *
     * @param m_dFaktor New value of property m_iFaktor.
     */
    public final void setFaktor(double m_dFaktor) {
        this.m_dFactor = m_dFaktor;
    }

    /**
     * Getter for property m_iFaktor.
     *
     * @return Value of property m_iFaktor.
     */
    public final double getFaktor() {
        return m_dFactor;
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
        double max;
        if(dataBasedBoundaries) max = Integer.MIN_VALUE;
        else max = 0;

        for (int i = 0; (m_values != null) && (i < m_values.length); i++) {
            if (m_values[i] > max) {
                max = m_values[i];
            }
        }

        return (max);
    }

    public final double getMinValue() {
        double min;
        if(dataBasedBoundaries) min = Integer.MAX_VALUE;
        else min = 0;

        for (int i = 0; (m_values != null) && (i < m_values.length); i++) {
            if (m_values[i] < min) {
                min = m_values[i];
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
        this.m_values = m_clWerte;
    }

    /**
     * Getter for property m_clWerte.
     *
     * @return Value of property m_clWerte.
     */
    public final double[] getWerte() {
        return this.m_values;
    }

    /**
     * Setter for property dataBasedBoundaries.
     *
     * @param value New value of property dataBasedBoundaries.
     */
    public final void setDataBasedBoundaries(boolean value) {
        dataBasedBoundaries = value;
    }
}
