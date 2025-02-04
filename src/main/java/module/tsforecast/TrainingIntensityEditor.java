package module.tsforecast;

import javax.swing.*;

public class TrainingIntensityEditor extends JSlider {
    LoepiCurve curve;
    Curve.Point point;
    public TrainingIntensityEditor(LoepiCurve curve) {
        super(0, 100, (int)curve.getTrainingIntensity());
        point = curve.getCurrentPoint();
        this.curve = curve;
    }

    public void updatePoint(){
        point.trainingIntensity = getValue();
        curve.propagateTrainingIntensity(point);
    }
}
