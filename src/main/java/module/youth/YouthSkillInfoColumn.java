package module.youth;

import core.gui.comp.entry.IHOTableEntry;
import core.model.HOVerwaltung;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class YouthSkillInfoColumn extends JLabel implements IHOTableEntry {

    YouthSkillInfo skillInfo;

    private static final Color Color_MaxReached = Color.red;
    private static final Color Color_PossibleRange = new Color(0, 255, 0, 100);
    private static final Color Color_TrainedRange = new Color(0, 153, 0);
    private static final Color Color_Background = Color.lightGray;

    private static final int bar_width = 100; // pixels to display skill range from 0 to 9 (8.3)
    private static final int bar_thickness = 12;

    public YouthSkillInfoColumn(YouthSkillInfo info) {
        this.skillInfo = info;
        this.updateComponent();
    }

    private String createToolTipText() {
        var hov = HOVerwaltung.instance();
        return "<html>" + this.skillInfo.getSkillID().toString() + "<br>" +
                String.format(hov.getLanguageString("ls.youth.skill.start") + ": %.2f<br>",this.skillInfo.getStartValue() ) +
                String.format(hov.getLanguageString("ls.youth.skill.current") + ": %.2f<br>", this.skillInfo.getCurrentValue() ) +
                hov.getLanguageString("ls.youth.skill.max") + ": " + this.skillInfo.getMax() + "<br>" +
                hov.getLanguageString("ls.youth.skill.maxreached") + ": " + hov.getLanguageString("ls.youth." + this.skillInfo.isMaxReached()) + "<br>" +
                hov.getLanguageString("ls.youth.skill.startlevel") + ": " + this.skillInfo.getStartLevel() + "<br>" +
                hov.getLanguageString("ls.youth.skill.currentlevel") + ": " + this.skillInfo.getCurrentLevel() + "</html>";
    }

    @Override
    public JComponent getComponent(boolean isSelected) {
        return this;
    }

    @Override
    public void clear() {

    }

    @Override
    public int compareTo(@NotNull IHOTableEntry obj) {
        if (obj instanceof YouthSkillInfoColumn) {
            final YouthSkillInfoColumn entry = (YouthSkillInfoColumn) obj;
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
        setOpaque(true);
    }

    @Override
    public void updateComponent() {
        this.setToolTipText(createToolTipText());
        this.setText(String.format("%.2f", this.skillInfo.getCurrentValue()));
        this.setHorizontalAlignment(RIGHT);
    }

    /* draw a color bar */
    @Override
    public void paint(Graphics g) {

        // Draw background
        g.setColor(Color_Background);
        g.fillRect(0, 0, bar_width, bar_thickness);

        // draw possible range
        g.setColor(Color_PossibleRange);
        var xStart = this.skillInfo.getStartLevel();
        if (xStart != null) xStart*=10;
        else xStart = 0;
        var xEnd = this.skillInfo.getMax();
        if (xEnd != null && xEnd < 8) xEnd = 10 * xEnd + 9;
        else xEnd = 83;
        g.fillRect(xStart, 0, xEnd-xStart, bar_thickness);

        // draw trained range
        g.setColor(this.skillInfo.isMaxReached() ? Color_MaxReached : Color_TrainedRange);
        xStart = (int) (this.skillInfo.getStartValue() * 10);
        xEnd = (int) (this.skillInfo.getCurrentValue() * 10);
        g.fillRect(xStart, 0, xEnd-xStart+1, bar_thickness);

        super.paint(g);
    }
}
