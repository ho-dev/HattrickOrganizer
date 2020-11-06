package core.gui.model;
import javax.swing.*;
import java.awt.*;


public class PlayerCBItemRenderer implements javax.swing.ListCellRenderer<SpielerCBItem> {

    @Override
    public Component getListCellRendererComponent(JList<? extends SpielerCBItem> list, SpielerCBItem value, int index, boolean isSelected, boolean cellHasFocus) {
        return value.getListCellRendererComponent(list, index, isSelected);
    }
}