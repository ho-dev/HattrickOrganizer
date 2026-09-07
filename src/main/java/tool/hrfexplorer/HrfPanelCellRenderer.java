package tool.hrfexplorer;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;

class HrfPanelCellRenderer extends JPanel implements TableCellRenderer{

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
	removeAll();
	add((JComponent)value);
	return this;
	}
}
