package core.util.chart;

import java.text.NumberFormat;

public interface ILinesChart {

    // set all graph parameters
    void setAllValues(GraphDataModel[] models, String[] y_axisLabeling,
                      NumberFormat y_axisFormat, String x_axisTitle, String y_axisTitle,
                      boolean hasLabels, boolean hasHelpLines);

    // make a specific graph visible/invisible
    void setShow(String name, boolean show);

    // Switching the graph labelling on or off
    void setLabelling(boolean hasLabels);

    // Switching the guide lines on and off
    void setHelpLines(boolean hasHelpLines);

}
