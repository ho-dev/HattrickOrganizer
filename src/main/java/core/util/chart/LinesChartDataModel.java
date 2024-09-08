package core.util.chart;


import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LinesChartDataModel {

    private String m_sName;
    private double[] m_values;
    private final List<Double> lValues;
    private boolean m_bShow;
    private boolean dataBasedBoundaries = false;
    private boolean m_IsVisibleLegend = true;
    private double m_dFactor;
    private final BasicStroke m_LineStyle;
    private final Marker m_MarkerStyle;
    private java.awt.Color m_LineColor;
    private final int y_axisGroup;

    public int getY_axisGroup() {
        return y_axisGroup;
    }

    public Marker getMarkerStyle() {
        return m_MarkerStyle;
    }

    public List<Double> getlValues() {
        return lValues;
    }

    public Boolean getIsVisibleLegend() {
        return m_IsVisibleLegend;
    }

    public void setNotVisibleLegend() {
            m_IsVisibleLegend = false;
    }

    public BasicStroke getLineStyle() {
        return m_LineStyle;
    }

    public LinesChartDataModel(double[] values, String name, boolean show, java.awt.Color color) {
        this(values, name, show, color,  1);
    }

    public LinesChartDataModel(double[] values, String name, boolean show, java.awt.Color color, double factor) {
        this(values, name, show, color, SeriesLines.SOLID, SeriesMarkers.DIAMOND, factor, false);
    }

    public LinesChartDataModel(double[] values, String name, boolean show, java.awt.Color color, double factor, boolean second_Y_axis) {
        this(values, name, show, color, SeriesLines.SOLID, SeriesMarkers.DIAMOND, factor, second_Y_axis);
    }

    public LinesChartDataModel(double[] values, String name, boolean show, java.awt.Color color, double factor, String Y_axis) {
        this(values, name, show, color, SeriesLines.SOLID, SeriesMarkers.DIAMOND, factor, Y_axis);
    }

    public LinesChartDataModel(double[] values, String name, boolean show, java.awt.Color color, BasicStroke lineStyle,
                               Marker markerStyle, double factor, String Y_axis) {
        m_values = values;
        lValues = Arrays.stream(values).boxed().collect(Collectors.toList());
        m_sName = name;
        m_bShow = show;
        m_LineColor = color;
//        m_clFormat = format;
        m_dFactor = factor;
        m_LineStyle = lineStyle;
        m_MarkerStyle = markerStyle;
        switch (Y_axis) {
            case "Y2" ->  y_axisGroup = 1;
            case "Y3" ->  y_axisGroup = 2;
            default ->  y_axisGroup = 0;
        }
    }

    public LinesChartDataModel(double[] values, String name, boolean show, java.awt.Color color, BasicStroke lineStyle,
                               Marker markerStyle, double factor, boolean second_Y_axis) {
        m_values = values;
        lValues = Arrays.stream(values).boxed().collect(Collectors.toList());
        m_sName = name;
        m_bShow = show;
        m_LineColor = color;
//        m_clFormat = format;
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
     * Setter for property m_sName.
     *
     * @param m_sName New value of property m_sName.
     */
    public final void setName(String m_sName) {
        this.m_sName = m_sName;
    }

    /**
     * Getter for property m_sName.
     *
     * @return Value of property m_sName.
     */
    public final String getName() {
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
