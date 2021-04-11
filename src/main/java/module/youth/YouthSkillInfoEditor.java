package module.youth;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;

import javax.swing.*;
import java.util.Hashtable;

public class YouthSkillInfoEditor extends JPanel {
    private YouthSkillInfo skillInfo;

    private static int SliderWidth = 415;

    /**
     * Calculate slider position from skill value
     *
     * @param skillValue skill value
     * @return slider position [0..SliderWidth]
     */
    private static int SliderPos(double skillValue) {
        return (int) (skillValue * SliderWidth / 8.3);
    }

    /**
     * Calculate skill value from slider position
     *
     * @param sliderPosition slider position [0..SliderWidth]
     * @return skill value
     */
    private static double SkillValue(int sliderPosition) {
        return sliderPosition * 8.3 / SliderWidth;
    }

    private JLabel skillLabel = new JLabel();
    private JSlider skillStartValue = new JSlider(SwingConstants.HORIZONTAL, 0, SliderWidth, 0);
    private JSlider skillCurrentValue = new JSlider(SwingConstants.HORIZONTAL, 0, SliderWidth, 0);

    public YouthSkillInfoEditor() {
        //super(new BorderLayout());
        this.add(skillLabel);
        this.add(new JLabel(HOVerwaltung.instance().getLanguageString("startValue:")));
        skillStartValue.setPaintLabels(true);
        skillStartValue.setPaintTicks(true);
        this.add(skillStartValue);
        this.add(new JLabel(HOVerwaltung.instance().getLanguageString("currentValue:")));
        skillCurrentValue.setPaintLabels(true);
        skillCurrentValue.setPaintTicks(true);
        this.add(skillCurrentValue);
    }

    public void setSkillInfo(YouthSkillInfo skillInfo) {
        this.skillInfo = skillInfo;

        skillLabel.setText(HOVerwaltung.instance().getLanguageString(skillInfo.getSkillID().toString()) + ": ");
        // Tag the top 3 skills
        if (skillInfo.isTop3()) skillLabel.setBackground(ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG));
        else skillLabel.setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG));

        setSlider(skillStartValue, skillInfo.getStartValue(), skillInfo.getStartValueRange());
        setSlider(skillCurrentValue, skillInfo.getCurrentValue(), skillInfo.getCurrentValueRange());
    }

    private void setSlider(JSlider slider, double value, YouthSkillInfo.SkillRange skillrange) {
        slider.setMinimum(SliderPos(skillrange.getMin()));
        slider.setMaximum(SliderPos(skillrange.getMax()));
        slider.setValue(SliderPos(value));

        var labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(slider.getMinimum(), new JLabel(String.format("%.2f", skillrange.getMin())));
        labelTable.put(slider.getMaximum(), new JLabel(String.format("%.2f", skillrange.getMax())));
        labelTable.put(slider.getValue(), new JLabel(String.format("%.2f", value)));
        slider.setLabelTable(labelTable);
    }
}
