package core.util.chart;

import java.text.NumberFormat;

public interface IChart {


    // make a specific graph visible/invisible
    void setShow(String name, boolean show);


    // Switching the guide lines on and off
    void setHelpLines(boolean hasHelpLines);

}
