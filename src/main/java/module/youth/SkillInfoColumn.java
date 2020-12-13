package module.youth;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.model.HOVerwaltung;
import core.model.player.YouthPlayer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class SkillInfoColumn extends JSlider implements IHOTableEntry {

    YouthPlayer.SkillInfo skillInfo;

    public SkillInfoColumn(YouthPlayer.SkillInfo info){
        super(HORIZONTAL, 0, 85, (int)info.getStartValue()*10);
        this.skillInfo = info;
        this.setExtent((int) (10*info.getCurrentValue()) - getValue());
        this.setToolTipText(createToolTipText(info));
        this.setLabelTable(new Hashtable<Integer,JLabel>(){{
            put(getValue(), new JLabel(String.format("%,.2f", info.getStartValue())));
            put(getValue()+getExtent(), new JLabel(String.format("%.2f", info.getCurrentValue())));
        }});
    }

    private String createToolTipText(YouthPlayer.SkillInfo info) {
        return info.getSkillID().toString() + "\n" +
                HOVerwaltung.instance().getLanguageString("ls.player.start") + ": " + info.getStartValue() + "\n" +
                HOVerwaltung.instance().getLanguageString("ls.player.current") + ": " + info.getCurrentValue() + "\n" +
                HOVerwaltung.instance().getLanguageString("ls.player.max") + ": " + info.getMax() + "\n" +
                HOVerwaltung.instance().getLanguageString("ls.player.startlevel") + ": " + info.getStartLevel() + "\n" +
                HOVerwaltung.instance().getLanguageString("ls.player.currentlevel") + ": " + info.getCurrentLevel() ;
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
