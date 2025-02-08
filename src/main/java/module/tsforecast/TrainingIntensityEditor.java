package module.tsforecast;

import javax.swing.*;

public class TrainingIntensityEditor extends JSlider {
    LoepiCurve curve;
    Curve.Point point;
    public TrainingIntensityEditor(LoepiCurve curve) {
        super(0, 100, (int)curve.getTrainingIntensity());
        this.setPaintTicks(true);
        this.setPaintLabels(true);
        this.setMajorTickSpacing(10);
        this.setMinorTickSpacing(1);
        point = curve.getCurrentPoint();
        this.curve = curve;
    }

    public void updatePoint(){
        point.trainingIntensity = getValue();
        curve.propagateTrainingIntensity(point);
    }
}
