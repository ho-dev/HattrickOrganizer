package core.util.chart;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import org.jetbrains.annotations.Nullable;
import org.knowm.xchart.PieSeries;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;
import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Map;

// Class that produce a panel containing 2 pie charts side by side
public class HODoublePieChart implements IChart {

    private PieChartDataModel @Nullable [] m_modelsL;
    private PieChartDataModel @Nullable [] m_modelsR;
    private PieChart m_chartL;
    private PieChart m_chartR;
    private PieStyler m_stylerL;
    private PieStyler m_stylerR;
    private Map<String, PieSeries> seriesL;
    private Map<String, PieSeries> seriesR;
    private boolean bLegendVisible;
    private JPanel m_panel;


    public HODoublePieChart(boolean _bLegendVisible){
        this(_bLegendVisible, PieStyler.LabelType.Percentage, PieStyler.LabelType.Percentage);
    }

    public HODoublePieChart(boolean _bLegendVisible, PieStyler.LabelType AnnotationTypeL, PieStyler.LabelType AnnotationTypeR){

        bLegendVisible = _bLegendVisible;

        m_chartL = new PieChart(10, 10);
        m_chartR = new PieChart(10, 10);
        m_stylerL = m_chartL.getStyler();
        m_stylerR = m_chartR.getStyler();

        // General plot styling
        m_stylerL.setChartFontColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
        m_stylerL.setPlotBackgroundColor(ThemeManager.getColor(HOColorName.STAT_PANEL_BG));
        m_stylerL.setChartBackgroundColor(ThemeManager.getColor(HOColorName.STAT_PANEL_BG));
        m_stylerR.setChartFontColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
        m_stylerR.setPlotBackgroundColor(ThemeManager.getColor(HOColorName.STAT_PANEL_BG));
        m_stylerR.setChartBackgroundColor(ThemeManager.getColor(HOColorName.STAT_PANEL_BG));
        m_stylerL.setLabelType(AnnotationTypeL);
        m_stylerR.setLabelType(AnnotationTypeR);

        Font cFont = m_stylerL.getAnnotationTextFont();
        cFont = cFont.deriveFont(Font.BOLD, cFont.getSize()+3);
        m_stylerL.setAnnotationTextFont(cFont);
        m_stylerR.setAnnotationTextFont(cFont);


        // Legend
        m_stylerL.setLegendVisible(bLegendVisible);
        m_stylerR.setLegendVisible(bLegendVisible);
        if (bLegendVisible){
            m_stylerL.setLegendBackgroundColor(ThemeManager.getColor(HOColorName.STAT_PANEL_BG));
            m_stylerR.setLegendBackgroundColor(ThemeManager.getColor(HOColorName.STAT_PANEL_BG));
            m_stylerL.setLegendPosition(Styler.LegendPosition.InsideSW);
            m_stylerR.setLegendPosition(Styler.LegendPosition.InsideSW);
            m_stylerL.setLegendLayout(Styler.LegendLayout.Horizontal);
            m_stylerR.setLegendLayout(Styler.LegendLayout.Horizontal);

            cFont = m_stylerL.getLegendFont();
            cFont = cFont.deriveFont(Font.BOLD, cFont.getSize()+3);
            m_stylerL.setLegendFont(cFont);
            m_stylerR.setLegendFont(cFont);
        }


        m_panel= new JPanel(new GridLayout(1,2));
        JPanel left = new XChartPanel<>(m_chartL);
        JPanel right = new XChartPanel<>(m_chartR);
        m_panel.add(left);
        m_panel.add(right);
    }



    public JPanel getPanel() {
        return m_panel;
    }


    public final void updateGraph(){

        PieSeries serie;
        String serieName;
        double value;

        seriesL = m_chartL.getSeriesMap();
        seriesR = m_chartR.getSeriesMap();

        // update Left Pie Chart
        if (m_modelsL != null) {

            for (var model : m_modelsL) {
                if (model == null) continue;
                serieName = model.getName();
                value = model.getValue();

                // Serie is removed
                if (seriesL.containsKey(serieName)) {
                    serie = m_chartL.removeSeries(serieName);
                    serie.setShowInLegend(false);
                }

                // Serie is added if should be shown and if value <> 0
                if (model.isShow() && (value != 0)) {
                    serie = m_chartL.addSeries(serieName, value);
                    serie.setShowInLegend(true);
                }
            }
        }

        // update Right Pie Chart
        if (m_modelsR != null) {

            for (var model : m_modelsR) {
                if (model == null) continue;
                serieName = model.getName();
                value = model.getValue();

                // Serie is removed
                if (seriesR.containsKey(serieName)) {
                    serie = m_chartR.removeSeries(serieName);
                    serie.setShowInLegend(false);
                }

                // Serie is added if should be shown and if value <> 0
                if (model.isShow() && (value != 0)) {
                    serie = m_chartR.addSeries(serieName, value);
                    serie.setShowInLegend(true);
                }
            }
        }

        m_panel.repaint();
    }


    public final void setShow(String name, boolean show) {  }

    public final void setHelpLines(boolean hasHelpLines) {
    }

    public final void setAllValues(PieChartDataModel @Nullable [] modelsL, PieChartDataModel @Nullable [] modelsR){
        this.m_modelsL = modelsL;
        this.m_modelsR = modelsR;

        updateGraph();

    }

    @Deprecated
    public final void setAllValues(LinesChartDataModel[] models, String[] xData,
                                   NumberFormat y_axisFormat, String x_axisTitle, String y_axisTitle,
                                   boolean hasLabels, boolean hasHelpLines){

    }



}



