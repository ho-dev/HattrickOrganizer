package core.util.chart;

import javax.swing.*;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class LinesChart implements ILinesChart{

    GraphDataModel[] m_models;
    List<Date> m_xData;
    XYChart m_chart;
    JPanel m_panel;
    Boolean m_hasLabels;
    Boolean m_hasHelpLines;

    public LinesChart(boolean second_axis, String y1_axisName, String y2_axisName, String y1_axisFormat, String y2_axisFormat){
        m_chart = new XYChart(10, 10);

        m_chart.getStyler().setLegendVisible(false);
        m_chart.getStyler().setPlotBackgroundColor(ThemeManager.getColor(HOColorName.STAT_PANEL_BG));
        m_chart.getStyler().setPlotGridLinesColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
        m_chart.getStyler().setChartBackgroundColor(ThemeManager.getColor(HOColorName.STAT_PANEL_BG));
        m_chart.getStyler().setXAxisTickMarksColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
        m_chart.getStyler().setXAxisTickLabelsColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
        m_chart.getStyler().setYAxisGroupTickLabelsColorMap(0, ThemeManager.getColor(HOColorName.STAT_PANEL_FG));

        if (y1_axisFormat != null){
            m_chart.getStyler().putYAxisGroupDecimalPatternMap(0, y1_axisFormat);
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

        }
        m_panel = new XChartPanel(m_chart);
    }


    private void reverseTS(){
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
        m_chart.getStyler().setMarkerSize(5);

        var series = m_chart.getSeriesMap();
        String serieName;
        List<Double> serieData;

        if (m_models == null) return;

       for(var model : m_models){
           if (model == null) continue;
           int yGroup = model.getY_axisGroup();
           serieName = model.getName() + " (y" + yGroup + ")";;
           serieData = model.getlValues();
           if ( model.isShow() && (!series.containsKey(serieName)))
           {
               serie =  m_chart.addSeries(serieName, this.m_xData, serieData);
               serie.setLineStyle(model.getLineStyle());
               serie.setLineColor(model.getColor());
               serie.setMarker(model.getMarkerStyle());
               serie.setMarkerColor(model.getColor());
               serie.setYAxisGroup(yGroup);
           }
           else if ( (!model.isShow()) && series.containsKey(serieName) )
           {
               m_chart.removeSeries(serieName);
           }

        }

        m_panel.repaint();
    }


    public final void setShow(String name, boolean show) {
        if (m_models != null){
            for (int i = 0; i <= m_models.length; i++) {
                if ((m_models[i] != null) && (m_models[i].getName().equals(name))) {
                    m_models[i].setShow(show);
                    break;
                }
            }
        }
        updateGraph();
    }

    /**
     * Switching the graph labelling on or off
     */
    public final void setLabelling(boolean hasLabels) {
        this.m_hasLabels = hasLabels;
        updateGraph();
    }

    /**
     * Switching the guide lines on and off
     */
    public final void setHelpLines(boolean hasHelpLines) {
        this.m_hasHelpLines = hasHelpLines;
        updateGraph();
    }

    public final void setAllValues(GraphDataModel[] models, double[] inp_xData,
                                   NumberFormat y_axisFormat, String x_axisTitle, String y_axisTitle,
                                   boolean hasLabels, boolean hasHelpLines){
        this.m_models = models;
        reverseTS();

        List<Date> lxData = new ArrayList<>();
        for(double ts:inp_xData){
            lxData.add(new Date((long)ts));
        }


        this.m_xData = lxData;


//        this.xBezeichner = x_axisTitle;
//        this.yBezeichner = y_axisTitle;
        this.m_hasLabels = hasLabels;
        this.m_hasHelpLines = hasHelpLines;
//        this.m_clYAchseFormat = y_axisFormat;
        updateGraph();

    }

    public final void setAllValues(GraphDataModel[] models, String[] xData,
                                   NumberFormat y_axisFormat, String x_axisTitle, String y_axisTitle,
                                   boolean hasLabels, boolean hasHelpLines){

    }



}



