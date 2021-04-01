package module.youth;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

public class YouthSkillInfoEditor extends JPanel {
    private YouthSkillInfo skillInfo;

    private static int SliderWidth = 415;
    private static int SliderPos(double val){
        return (int)(val*SliderWidth/8.3);
    }
    private static double ValueOfSliderPos(int pos){
        return pos*8.3/SliderWidth;
    }

    private JLabel skillLabel = new JLabel();
    private JSlider skillStartValue = new JSlider(SwingConstants.HORIZONTAL, 0, SliderPos(8.3), 0);
    private JSlider skillCurrentValue = new JSlider(SwingConstants.HORIZONTAL, 0, SliderPos(8.3), 0);

    public YouthSkillInfoEditor(){
        super(new BorderLayout());
        this.add(skillLabel,BorderLayout.WEST);
        skillStartValue.setPaintLabels(true);
        skillStartValue.setPaintTicks(true);
        this.add(skillStartValue, BorderLayout.CENTER);
        this.add(skillCurrentValue, BorderLayout.EAST);
    }

    public void setSkillInfo(YouthSkillInfo skillInfo) {
        this.skillInfo = skillInfo;

        skillLabel.setText(HOVerwaltung.instance().getLanguageString(skillInfo.getSkillID().toString())+": ");
        // Tag the top 3 skills
        if ( skillInfo.isTop3()) skillLabel.setBackground(ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG));
        else skillLabel.setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG));

        skillStartValue.setMinimum(SliderPos(skillInfo.getStartValueRange().getMin()));
        skillStartValue.setMaximum(SliderPos(skillInfo.getStartValueRange().getMax()));
        skillStartValue.setValue(SliderPos(skillInfo.getStartValue()));
        setSliderLabel(skillStartValue);

        if ( skillInfo.getCurrentLevel() != null){
            skillCurrentValue.setMinimum(SliderPos(Math.max(skillInfo.getStartValue(), (double)skillInfo.getCurrentLevel())));
            if ( skillInfo.getCurrentLevel() < 8){
                skillCurrentValue.setMaximum(SliderPos(skillInfo.getCurrentLevel()+.99));
            }
            else {
                skillCurrentValue.setMaximum(SliderPos(8.3));
            }
        }
        else{
            skillCurrentValue.setMinimum(SliderPos(skillStartValue.getValue()));
            if ( skillInfo.getMax() != null){
                if ( skillInfo.getMax() < 8){
                    skillCurrentValue.setMaximum(SliderPos(skillInfo.getMax()+.99));
                }
                else {
                    skillCurrentValue.setMaximum(SliderPos(8.3));
                }
            }
        }
        skillCurrentValue.setValue(SliderPos(skillInfo.getCurrentValue()));
        setSliderLabel(skillCurrentValue);
    }

    private void setSliderLabel(JSlider slider) {
        var labelTable = new Hashtable();
        labelTable.put(slider.getMinimum(), new JLabel(String.format("%.2f", ValueOfSliderPos(slider.getMinimum()) )));
        labelTable.put( slider.getMaximum(), new JLabel(String.format("%.2f", ValueOfSliderPos(slider.getMaximum()) )));
        slider.setLabelTable(labelTable);
    }
}
