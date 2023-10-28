package core.util.chart;


import org.jetbrains.annotations.Nullable;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PieChartDataModel {

    private String m_sName;
    private double m_dValue;
    private boolean m_bShow;
    private Color m_SectionColor;


//
//    public PieChartDataModel(double[] values, String name, boolean show, Color color,
//                             java.text.NumberFormat format, int yAxisGroup) {
//        this(values, name, show, color, SeriesLines.SOLID, SeriesMarkers.DIAMOND, format, 1, false);
//    }
//
//    public PieChartDataModel(double[] values, String name, boolean show, Color color,
//                             java.text.@Nullable NumberFormat format) {
//        this(values, name, show, color, format, 1);
//    }
//
//    public PieChartDataModel(double[] values, String name, boolean show, Color color,
//                             java.text.NumberFormat format, double factor) {
//        this(values, name, show, color, SeriesLines.SOLID, SeriesMarkers.DIAMOND, format, factor, false);
//    }
//
//    public PieChartDataModel(double[] values, String name, boolean show, Color color,
//                             java.text.NumberFormat format, double factor, boolean second_Y_axis) {
//        this(values, name, show, color, SeriesLines.SOLID, SeriesMarkers.DIAMOND, format, factor, second_Y_axis);
//    }

    public PieChartDataModel(String name, double value) {
        m_dValue = value;
        m_sName = name;
        m_bShow = true;
        m_SectionColor = null;
    }

    public PieChartDataModel(String name, double value, boolean show, Color color) {
        m_dValue = value;
        m_sName = name;
        m_bShow = show;
        m_SectionColor = color;
    }


    public final void setColor(Color m_clColor) {
        this.m_SectionColor = m_clColor;
    }

    public final Color getColor() {
        return m_SectionColor;
    }

    public final void setName(String m_sName) {
        this.m_sName = m_sName;
    }

    public final String getName() {
        return m_sName;
    }

    public final void setShow(boolean m_bShow) {
        this.m_bShow = m_bShow;
    }

    public final boolean isShow() {
        return m_bShow;
    }

    public final void setValue(double _value) {
        this.m_dValue = _value;
    }

    public final double getValue() {
        return this.m_dValue;
    }

}
