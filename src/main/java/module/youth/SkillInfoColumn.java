package module.youth;

import core.gui.comp.entry.IHOTableEntry;
import core.model.HOVerwaltung;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

public class SkillInfoColumn extends JSlider implements IHOTableEntry {

    YouthPlayer.SkillInfo skillInfo;

    private static final Color Color_MaxReached = Color.red;
    private static final Color Color_Standard = Color.green;

    public SkillInfoColumn(YouthPlayer.SkillInfo info){
        super(HORIZONTAL, 0, 85, (int)info.getStartValue()*10);
        this.skillInfo = info;
        this.setExtent((int) (10*info.getCurrentValue()) - getValue());
        this.setForeground(skillInfo.isMaxReached()?Color_MaxReached: Color_Standard);
        this.setToolTipText(createToolTipText(info));
        this.setLabelTable(new Hashtable<Integer,JLabel>(){{
            put(getValue(), new JLabel(String.format("%,.2f", info.getStartValue())));
            put(getValue()+getExtent(), new JLabel(String.format("%.2f", info.getCurrentValue())));
        }});
    }

    private String createToolTipText(YouthPlayer.SkillInfo info) {
        var hov = HOVerwaltung.instance();
        return "<html>" + info.getSkillID().toString() + "<br>" +
                hov.getLanguageString("ls.youth.skill.start") + ": " + info.getStartValue() + "<br>" +
                hov.getLanguageString("ls.youth.skill.current") + ": " + info.getCurrentValue() + "<br>" +
                hov.getLanguageString("ls.youth.skill.max") + ": " + info.getMax() + "<br>" +
                hov.getLanguageString("ls.youth.skill.maxreached") + ": " + hov.getLanguageString("ls.youth." + String.valueOf(info.isMaxReached())) + "<br>" +
                hov.getLanguageString("ls.youth.skill.startlevel") + ": " + info.getStartLevel() + "<br>" +
                hov.getLanguageString("ls.youth.skill.currentlevel") + ": " + info.getCurrentLevel() + "</html>";
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
        if (obj instanceof SkillInfoColumn) {
            final SkillInfoColumn entry = (SkillInfoColumn) obj;
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

    }
}
