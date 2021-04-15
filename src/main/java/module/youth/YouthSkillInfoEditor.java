package module.youth;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

public class YouthSkillInfoEditor extends JPanel {
    private YouthSkillInfo skillInfo;

    private static int SliderWidth = 830;

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

    class SkillInfoSlider extends JPanel {
        private JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 0, SliderWidth, 0);
        private JLabel minLabel = new JLabel("0.00");
        private JLabel maxLabel = new JLabel("8.30");

        public SkillInfoSlider(){
            super(new BorderLayout());
            slider.setPaintLabels(true);
            slider.setPaintTicks(true);
            this.add(this.minLabel, BorderLayout.WEST);
            this.add(this.slider, BorderLayout.CENTER);
            this.add(this.maxLabel, BorderLayout.EAST);
        }

        public void set(double value, YouthSkillInfo.SkillRange range) {
            slider.setMinimum(SliderPos(range.getMin()));
            slider.setMaximum(SliderPos(range.getMax()));
            slider.setValue(SliderPos(value));

            minLabel.setText(String.format("%.2f", range.getMin()));
            maxLabel.setText(String.format("%.2f", range.getMax()));

            var labelTable = new Hashtable<Integer, JLabel>();
            labelTable.put(slider.getValue(), new JLabel(String.format("%.2f", value)));
            slider.setLabelTable(labelTable);
        }
    }

    private JLabel skillLabel = new JLabel();
    private SkillInfoSlider skillStartValue = new SkillInfoSlider();
    private SkillInfoSlider skillCurrentValue = new SkillInfoSlider();

    public YouthSkillInfoEditor() {
        //super(new BorderLayout());
        skillLabel.setOpaque(false);
        this.add(skillLabel);
        this.add(new JLabel(HOVerwaltung.instance().getLanguageString("startValue:")));
        this.add(skillStartValue);
        this.add(new JLabel(HOVerwaltung.instance().getLanguageString("currentValue:")));
        this.add(skillCurrentValue);
    }

    public void setSkillInfo(YouthSkillInfo skillInfo) {
        this.skillInfo = skillInfo;

        skillLabel.setText(HOVerwaltung.instance().getLanguageString(skillInfo.getSkillID().toString()) + ": ");

        // TODO: Tag the top 3 skills
        if (skillInfo.isTop3()) skillLabel.setBackground(ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG));
        else skillLabel.setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG));

        skillStartValue.set(skillInfo.getStartValue(), skillInfo.getStartValueRange());
        skillCurrentValue.set(skillInfo.getCurrentValue(), skillInfo.getCurrentValueRange());
    }

}
