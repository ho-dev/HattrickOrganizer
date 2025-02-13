package module.tsforecast;

import core.model.TranslationFacility;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class TrainingIntensityEditor extends JPanel {

    JLabel trainingIntensityLabel;
    JSlider trainingIntensitySlider;
    LoepiCurve curve;
    Curve.Point point;

    public TrainingIntensityEditor(LoepiCurve curve) {
        super(new BorderLayout());
        trainingIntensitySlider = new JSlider(0, 100, (int)curve.getTrainingIntensity());
        trainingIntensitySlider.setPaintTicks(true);
        trainingIntensitySlider.setPaintLabels(true);
        trainingIntensitySlider.setMajorTickSpacing(10);
        trainingIntensitySlider.setMinorTickSpacing(1);
        point = curve.getCurrentPoint();
        this.curve = curve;
        this.add(trainingIntensitySlider, BorderLayout.CENTER);

        this.add(trainingIntensityLabel = new JLabel(TranslationFacility.tr("ls.team.trainingintensity") + " " + curve.getDate().toLocaleDateTime()), BorderLayout.NORTH);

        trainingIntensityLabel = new JLabel("" + curve.getTrainingIntensity());
        this.trainingIntensitySlider.addChangeListener(e -> trainingIntensityLabel.setText("" + trainingIntensitySlider.getValue()));
        this.add(trainingIntensityLabel, BorderLayout.WEST);
    }

    public void updatePoint(){
        point.trainingIntensity = trainingIntensitySlider.getValue();
        var pos = curve.propagateTrainingIntensity(point);
        if ( pos >= 0) {
            curve.forecast(pos);
        }
    }

    public void addChangeListener(ChangeListener o) {
        this.trainingIntensitySlider.addChangeListener(o);
    }
}
