package module.youth;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.model.player.YouthPlayer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class SkillInfoColumn extends JSlider implements IHOTableEntry {

    YouthPlayer.SkillInfo skillInfo;

    public SkillInfoColumn(YouthPlayer.SkillInfo info){
        super(HORIZONTAL, 0, 85, (int)info.getStartValue()*10);
        this.skillInfo = info;
        this.setExtent((int) (10*info.getCurrentValue()) - getValue());
        this.setToolTipText(info.toString());
        this.setLabelTable(new Hashtable<Integer,JLabel>(){{
            put(getValue(), new JLabel(String.format("%,.2f", info.getStartValue())));
            put(getValue()+getExtent(), new JLabel(String.format("%.2f", info.getCurrentLevel())));
        }});
    }

    @Override
    public JComponent getComponent(boolean isSelected) {
        return this;
    }

    @Override
    public void clear() {

    }

    @Override
    public int compareTo(IHOTableEntry obj) {
        if (obj instanceof SkillInfoColumn) {
            final SkillInfoColumn entry = (SkillInfoColumn) obj;
            return ((Double)this.skillInfo.getCurrentValue()).compareTo(entry.skillInfo.getCurrentValue());
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
