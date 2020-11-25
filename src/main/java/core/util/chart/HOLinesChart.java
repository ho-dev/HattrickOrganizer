package core.util.chart;

import javax.swing.*;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import org.jetbrains.annotations.Nullable;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.AxesChartStyler;
import org.knowm.xchart.style.Styler;

import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class HOLinesChart implements IChart {

    LinesChartDataModel @Nullable [] m_models;
    List<Date> m_xData;
    XYChart m_chart;
    AxesChartStyler m_axeStyler;
    JPanel m_panel;
    Boolean m_hasLabels;
    Boolean m_hasHelpLines;

    public HOLinesChart(boolean second_axis, @Nullable String y1_axisName, @Nullable String y2_axisName, @Nullable String y1_axisFormat, String y2_axisFormat, Double y1_axisMin, Double y1_axisMax)
    {
        this(second_axis, y1_axisName, y2_axisName, y1_axisFormat, y2_axisFormat, y1_axisMin, y1_axisMax, null, null, false);
    }


    public HOLinesChart(boolean second_axis, @Nullable String y1_axisName, @Nullable String y2_axisName, @Nullable String y1_axisFormat, String y2_axisFormat)
    {
        this(second_axis, y1_axisName, y2_axisName, y1_axisFormat, y2_axisFormat, null, null, null, null, false);
    }

    public HOLinesChart(boolean second_axis, @Nullable String y1_axisName, @Nullable String y2_axisName, @Nullable String y1_axisFormat, String y2_axisFormat, boolean bLegendVisible)
    {
        this(second_axis, y1_axisName, y2_axisName, y1_axisFormat, y2_axisFormat, null, null, null, null, bLegendVisible);
    }

    public void setYAxisMin(int yAxisGroup, @Nullable Double value){
        m_axeStyler.setYAxisMin(yAxisGroup-1, value);
    }

    public void setYAxisMax(int yAxisGroup, @Nullable Double value){
        m_axeStyler.setYAxisMax(yAxisGroup-1, value);
    }


    public HOLinesChart(boolean second_axis, String y1_axisName, String y2_axisName, String y1_axisFormat, String y2_axisFormat,
                        @Nullable Double y1_axisMin, @Nullable Double y1_axisMax, @Nullable Double y2_axisMin, @Nullable Double y2_axisMax, boolean bLegendVisible) {

        this(false, second_axis, y1_axisName, y2_axisName, null, y1_axisFormat, y2_axisFormat, null, y1_axisMin, y1_axisMax, y2_axisMin, y2_axisMax,
                null, null, bLegendVisible);
    }

    public HOLinesChart(boolean third_axis, boolean second_axis, String y1_axisName, String y2_axisName, String y3_axisName, String y1_axisFormat, String y2_axisFormat, String y3_axisFormat,
                        @Nullable Double y1_axisMin, @Nullable Double y1_axisMax, @Nullable Double y2_axisMin, @Nullable Double y2_axisMax, @Nullable Double y3_axisMin, @Nullable Double y3_axisMax,boolean bLegendVisible){

        m_chart = new XYChart(10, 10);
        m_axeStyler = m_chart.getStyler();

        m_chart.getStyler().setLegendVisible(bLegendVisible);
        if (bLegendVisible){
            m_chart.getStyler().setLegendBackgroundColor(ThemeManager.getColor(HOColorName.STAT_PANEL_BG));
        }

        m_chart.getStyler().setChartFontColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));

        m_chart.getStyler().setPlotBackgroundColor(ThemeManager.getColor(HOColorName.STAT_PANEL_BG));
        m_chart.getStyler().setPlotGridLinesColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
        m_chart.getStyler().setChartBackgroundColor(ThemeManager.getColor(HOColorName.STAT_PANEL_BG));

        m_chart.getStyler().setXAxisTickMarksColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
        m_chart.getStyler().setXAxisTickLabelsColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));

        m_chart.getStyler().setPlotGridLinesStroke(new BasicStroke(0.25f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        m_chart.getStyler().setMarkerSize(5);

        m_chart.getStyler().setYAxisGroupTickLabelsColorMap(0, ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
        m_chart.getStyler().setYAxisGroupTickMarksColorMap(0, ThemeManager.getColor(HOColorName.STAT_PANEL_FG));

        if (y1_axisMin != null) m_axeStyler.setYAxisMin(0, y1_axisMin);
        if (y1_axisMax != null) m_axeStyler.setYAxisMax(0, y1_axisMax);

        if (y1_axisFormat != null){
            m_chart.getStyler().putYAxisGroupDecimalPatternMap(0, y1_axisFormat);
        }

        if(third_axis){
            second_axis = true;

            m_chart.getStyler().setYAxisGroupPosition(2, Styler.YAxisPosition.Right);
            if(y3_axisName != null){
                m_chart.setYAxisGroupTitle(2, y3_axisName);
            }

            if (y3_axisFormat != null){
                m_chart.getStyler().putYAxisGroupDecimalPatternMap(2, y3_axisFormat);
            }

            m_chart.getStyler().setYAxisGroupTickLabelsColorMap(2, ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
            m_chart.getStyler().setYAxisGroupTickMarksColorMap(2, ThemeManager.getColor(HOColorName.STAT_PANEL_FG));

            if (y3_axisMin != null) m_axeStyler.setYAxisMin(2, y3_axisMin);
            if (y3_axisMax != null) m_axeStyler.setYAxisMax(2, y3_axisMax);
        }

        if(second_axis){
            m_chart.getStyler().setYAxisGroupPosition(1, Styler.YAxisPosition.Right);
            if(y1_axisName != null){
                m_chart.setYAxisGroupTitle(0, y1_axisName);
            }
            if(y2_axisName != null){
                m_chart.setYAxisGroupTitle(1, y2_axisName);
            }

            if (y2_axisFormat != null){
                m_chart.getStyler().putYAxisGroupDecimalPatternMap(1, y2_axisFormat);
            }

            m_chart.getStyler().setYAxisGroupTickLabelsColorMap(1, ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
            m_chart.getStyler().setYAxisGroupTickMarksColorMap(1, ThemeManager.getColor(HOColorName.STAT_PANEL_FG));

            if (y2_axisMin != null) m_axeStyler.setYAxisMin(1, y2_axisMin);
            if (y2_axisMax != null) m_axeStyler.setYAxisMax(1, y2_axisMax);

        }

        m_panel = new XChartPanel(m_chart);
    }


    private void reverseTS(){
        if (m_models == null) return;

        for (var model : m_models) {
            if (model == null) continue;
            var values = model.getWerte();

            var reverse_values = IntStream.rangeClosed(1, values.length)
                    .mapToDouble(i -> values[values.length - i])
                    .toArray();

            model.setWerte(reverse_values);

        }
    }


    public JPanel getPanel() {
        return m_panel;
    }


    public final void updateGraph(){

        XYSeries serie;

        m_chart.getStyler().setHasAnnotations(m_hasLabels);
        m_chart.getStyler().setPlotGridLinesVisible(m_hasHelpLines);

        var series = m_chart.getSeriesMap();
        String serieName;
        List<Double> serieData;

        if (m_models == null) return;

       for(var model : m_models){
           if (model == null) continue;
           int yGroup = model.getY_axisGroup();
           serieName = model.getName();
           serieData = model.getlValues();

           // Serie is removed
           if (series.containsKey(serieName))
           {
               serie =  m_chart.removeSeries(serieName);
               serie.setShowInLegend(false);
           }

           // Serie is added if should be shown and if contains data
           if (model.isShow() && (serieData.size() != 0))
           {
               serie =  m_chart.addSeries(serieName, this.m_xData, serieData);
               serie.setLineStyle(model.getLineStyle());
               serie.setLineColor(model.getColor());
               serie.setMarker(model.getMarkerStyle());
               serie.setMarkerColor(model.getColor());
               serie.setYAxisGroup(yGroup);
               serie.setShowInLegend(true);
           }
        }

        m_panel.repaint();
    }


    public final void clearAllPlots(){
        if (m_models != null){
            for (int i = 0; i < m_models.length; i++) {
                if (m_models[i] != null) {
                    m_models[i].setShow(false);
                }
            }
            updateGraph();
        }
    }

    public final void setShow(String name, boolean show) {
        if (m_models != null){
            for (int i = 0; i < m_models.length; i++) {
                if ((m_models[i] != null) && (m_models[i].getName().equals(name))) {
                    m_models[i].setShow(show);
                    break;
                }
            }
            updateGraph();
        }
    }

    private final void setShowWithoutUpdate(String name, boolean show) {
        if (m_models != null){
            for (int i = 0; i < m_models.length; i++) {
                if ((m_models[i] != null) && (m_models[i].getName().equals(name))) {
                    m_models[i].setShow(show);
                    break;
                }
            }
        }
    }

    public final void setMultipleShow(String[] names, boolean[] shows) {
        if (m_models != null) {
            for (int i = 0; i < names.length; i++) {
                    setShowWithoutUpdate(names[i], shows[i]);
            }
            updateGraph();
        }

    }

    // Switching the graph labelling on or off
    public final void setLabelling(boolean hasLabels) {
        this.m_hasLabels = hasLabels;
        updateGraph();
    }


     //Switching the guide lines on and off
    public final void setHelpLines(boolean hasHelpLines) {
        this.m_hasHelpLines = hasHelpLines;
        updateGraph();
    }

    public final void setAllValues(LinesChartDataModel @Nullable [] models, double[] inp_xData,
                                   NumberFormat y_axisFormat, String x_axisTitle, String y_axisTitle,
                                   boolean hasLabels, boolean hasHelpLines){
        this.m_models = models;
        reverseTS();

        List<Date> lxData = new ArrayList<>();
        for(double ts:inp_xData){
            lxData.add(new Date((long)ts));
        }

        this.m_xData = lxData;
        this.m_hasLabels = hasLabels;
        this.m_hasHelpLines = hasHelpLines;

        updateGraph();

    }

    @Deprecated
    public final void setAllValues(LinesChartDataModel[] models, String[] xData,
                                   NumberFormat y_axisFormat, String x_axisTitle, String y_axisTitle,
                                   boolean hasLabels, boolean hasHelpLines){

    }



}



