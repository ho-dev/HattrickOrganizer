package module.tsforecast;

import core.model.TranslationFacility;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Editor to set training intensity of future training weeks
 */
public class TrainingIntensityEditor extends JPanel {

    /**
     * Label showing the training intensity value
     */
    JLabel trainingIntensityLabel;

    /**
     * Slider to edit training intensity value
     */
    JSlider trainingIntensitySlider;

    /**
     * The complete curve data
     */
    LoepiCurve curve;

    /**
     * The training intensity event point
     */
    Curve.Point point;

    /**
     * Create a training intensity editor
     * The event point that should be edited is the current point of the given loepi curve
     * @param curve LoepiCurve, that should be edited
     */
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

    /**
     * Update the point.
     * The new value is read from the slider and propagated to the point and all it's successors.
     */
    public void updatePoint(){
        point.trainingIntensity = trainingIntensitySlider.getValue();
        var pos = curve.propagateTrainingIntensity(point);
        if ( pos >= 0) {
            curve.forecast(pos-1);
        }
    }

    /**
     * Add a change listener to the training slider
     * @param o ChangeListener
     */
    public void addChangeListener(ChangeListener o) {
        this.trainingIntensitySlider.addChangeListener(o);
    }
}
