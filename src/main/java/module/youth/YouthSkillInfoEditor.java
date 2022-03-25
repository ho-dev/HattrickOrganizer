package module.youth;

import core.model.HOVerwaltung;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

public class YouthSkillInfoEditor extends JPanel {

    private YouthSkillInfo skillInfo;

    private static int SliderWidth = 1660;

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

    public void setStartSkillValue() {
        skillInfo.setStartValue(SkillValue(skillStartValue.slider.getValue()));
    }


    class SkillInfoSlider extends JPanel {
        private YouthSkillInfo skillInfo;
        private JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 0, SliderWidth, 0);
        private JLabel minLabel = new JLabel("0.00", SwingConstants.RIGHT);
        private JLabel maxLabel = new JLabel("8.30", SwingConstants.LEFT);

        public SkillInfoSlider(String label){
            super(new BorderLayout());
            slider.setPaintLabels(true);
            slider.setPaintTicks(true);

            var leftLabels = new JPanel(new GridLayout(0,1, 0, 12));
            leftLabels.add(this.minLabel);
            leftLabels.add(new JLabel(label));

            this.add(leftLabels, BorderLayout.WEST);
            this.add(this.slider, BorderLayout.CENTER);

            var rightLabels = new JPanel(new GridLayout(0,1, 0, 12));
            rightLabels.add(this.maxLabel);
            rightLabels.add(new JLabel());
            this.add(rightLabels, BorderLayout.EAST);
        }

        public void set(YouthSkillInfo skillInfo, double value, YouthSkillInfo.SkillRange range) {
            this.skillInfo = skillInfo;
            slider.setMinimum(SliderPos(range.getGreaterEqual()));
            slider.setMaximum(SliderPos(range.getLessThan()));
            slider.setValue(SliderPos(value));

            minLabel.setText(String.format("%.2f", range.getGreaterEqual()));
            maxLabel.setText(String.format("%.2f", range.getLessThan()));

            setValueLabel();
        }

        public void setValueLabel() {
            var labelTable = new Hashtable<Integer, JLabel>();
            labelTable.put(slider.getValue(), new JLabel(String.format("%.2f", getSkillValue())));
            slider.setLabelTable(labelTable);
        }

        public YouthSkillInfo getSkillInfo(){return skillInfo;}
        public void addChangeListener(ChangeListener l){
            this.slider.addChangeListener(l);
        }
        double getSkillValue(){ return SkillValue(this.slider.getValue());}

    }

    private JLabel skillLabel = new JLabel();
    private SkillInfoSlider skillStartValue = new SkillInfoSlider(HOVerwaltung.instance().getLanguageString("ls.youth.player.skillstartvalue")+": ");
    private SkillInfoSlider skillCurrentValue = new SkillInfoSlider(HOVerwaltung.instance().getLanguageString("ls.youth.player.skillcurrentvalue")+": ");
    private ImageIcon getImageIcon4Color(Color color) {
        final BufferedImage bufferedImage = new BufferedImage(14, 14, BufferedImage.TYPE_INT_ARGB);

        final java.awt.Graphics2D g2d = (java.awt.Graphics2D) bufferedImage.getGraphics();

        g2d.setColor(color);
        g2d.fillRect(0, 0, 13, 13);

        return new ImageIcon(bufferedImage);
    }

    public YouthSkillInfoEditor(Color color) {
        super(new BorderLayout());
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        skillLabel.setOpaque(false);
        skillLabel.setIcon(getImageIcon4Color(color));
        this.add(skillLabel, BorderLayout.NORTH);
        var valuesPanel = new JPanel(new GridLayout(1,0, 20, 0));
        valuesPanel.add(skillStartValue);
        valuesPanel.add(skillCurrentValue);
        this.add(valuesPanel, BorderLayout.CENTER);
    }

    public void setSkillInfo(YouthSkillInfo skillInfo) {
        this.skillInfo = skillInfo;
        skillLabel.setText(HOVerwaltung.instance().getLanguageString("ls.youth.player." + skillInfo.getSkillID().toString()) + ": ");
        skillStartValue.set(skillInfo, skillInfo.getStartValue(), skillInfo.getStartValueRange());
        skillCurrentValue.set(skillInfo, skillInfo.getCurrentValue(), skillInfo.getCurrentValueRange());
    }

    public void addStartValueChangeListener(ChangeListener l){
        skillStartValue.addChangeListener(l);
    }

    public void addCurrentValueChangeListener(ChangeListener l){
        skillCurrentValue.addChangeListener(l);
    }

}
