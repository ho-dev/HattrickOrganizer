package module.youth;

import core.gui.comp.entry.IHOTableEntry;
import core.model.TranslationFacility;
import core.model.UserParameter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static module.youth.YouthSkillInfo.getSkillName;

public class YouthSkillInfoColumn extends JLabel implements IHOTableEntry {

    YouthSkillInfo skillInfo;

    private static final Color Color_MaxReached = Color.red;
    private static final Color Color_MaxLevelReached = Color.yellow;
    private static final Color Color_PossibleRange = new Color(0, 255, 0, 100);
    private static final Color Color_TrainedRange = new Color(0, 153, 0);
    private static final Color Color_Background = Color.lightGray;

    private static final int bar_width = 8 * UserParameter.instance().fontSize; // pixels to display skill range from 0 to 9 (8.3)

    private final Color[] cellSkillRatingColor = new Color[]{
            new Color(255,204,204),         // light red
            new Color(255,255,160),         // yellow
            new Color(230,255,204),         // lightest green
            new Color(0, 255, 0, 100),   // lighter green
            Color.green
    };

    private Color getSkillRating(YouthSkillInfo skillInfo){
        if (skillInfo.getMax() != null) return cellSkillRatingColor[Math.max(0,skillInfo.getMax()-4)];
        if (skillInfo.getMaximumPotential() < 6) return cellSkillRatingColor[Math.max(0,skillInfo.getMaximumPotential()-4)];
        return Color.white;
    }

    public YouthSkillInfoColumn(YouthSkillInfo info) {
        this.skillInfo = info;
        this.updateComponent();
    }

    private String createToolTipText() {
        var unknown = TranslationFacility.tr("unknown");
        var skillName = getSkillName(this.skillInfo.getSkillID());
        return "<html>" +
                TranslationFacility.tr("ls.youth.player."+skillName) + "<br>" +
                String.format(TranslationFacility.tr("ls.youth.skill.start") + ": %.2f<br>", this.skillInfo.getStartValue()) +
                String.format(TranslationFacility.tr("ls.youth.skill.current") + ": %.2f<br>", this.skillInfo.getCurrentValue()) +
                (this.skillInfo.getPotential17Value() != null ? String.format(TranslationFacility.tr("ls.youth.skill.17") + ": %.2f<br>", this.skillInfo.getPotential17Value()) : "") +
                TranslationFacility.tr("ls.youth.skill.max") + ": " + (this.skillInfo.getMax() != null ? this.skillInfo.getMax() : unknown) + "<br>" +
                (this.skillInfo.isMaxReached() ? TranslationFacility.tr("ls.youth.skill.ismaxreached") + "<br>" : "") +
                TranslationFacility.tr("ls.youth.skill.startlevel") + ": " + (this.skillInfo.getStartLevel() != null ? this.skillInfo.getStartLevel() : unknown) + "<br>" +
                TranslationFacility.tr("ls.youth.skill.currentlevel") + ": " + (this.skillInfo.getCurrentLevel() != null ? this.skillInfo.getCurrentLevel() : unknown) +
                (this.skillInfo.isTop3() != null && this.skillInfo.isTop3() ? "<br>" + TranslationFacility.tr("ls.youth.skill.istop3") : "") +
                "</html>";
    }

    @Override
    public JComponent getComponent(boolean isSelected) {
        setBackground(getSkillRating(skillInfo));
        return this;
    }

    @Override
    public void clear() {

    }

    @Override
    public int compareTo(@NotNull IHOTableEntry obj) {
        if (obj instanceof YouthSkillInfoColumn entry) {
            return Double.compare(this.skillInfo.getCurrentValue(), entry.skillInfo.getCurrentValue());
        }
        return 0;
    }

    @Override
    public int compareToThird(IHOTableEntry obj) {
        return compareTo(obj);
    }

    @Override
    public void createComponent() {
        this.setBackground(getSkillRating(skillInfo));
    }

    @Override
    public void updateComponent() {
        this.setToolTipText(createToolTipText());
        this.setText(String.format("%.2f", this.skillInfo.getCurrentValue()));
        this.setHorizontalAlignment(RIGHT);
        int style = (this.skillInfo.isTop3() != null && this.skillInfo.isTop3()) ? Font.BOLD : Font.PLAIN;
        this.setFont(getFont().deriveFont(style));
        this.setOpaque(true);
        this.setBackground(getSkillRating(skillInfo));
        repaint();
    }

    /* draw a color bar */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        var bar_thickness = this.getHeight();
        // Draw background
        g.setColor(Color_Background);
        g.fillRect(0, 0, bar_width, bar_thickness);

        var f = bar_width/10.0;
        // draw possible range
        g.setColor(Color_PossibleRange);
        var xStart = this.skillInfo.getStartLevel();
        if (xStart != null) xStart = (int)(xStart*f);
        else xStart = 0;
        var xEnd = this.skillInfo.getMaximumPotential();
        if (xEnd < 8) xEnd = (int)(f * (xEnd + .9));
        else xEnd = (int)(8.3*f);
        g.fillRect(xStart, 0, xEnd-xStart, bar_thickness);

        // draw trained range
        xStart = (int) (this.skillInfo.getStartValue() * f);
        g.setColor(getColorTrainedBar());
        xEnd = (int) (this.skillInfo.getCurrentValue() * f);
        g.fillRect(xStart, 0, xEnd-xStart+1, bar_thickness);
    }

    private Color getColorTrainedBar() {
        if (this.skillInfo.isMaxReached()) return Color_MaxReached;
        if (this.skillInfo.isMaxLevelReached()) return Color_MaxLevelReached;
        return Color_TrainedRange;
    }
}
