package tool.hrfExplorer;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;

class HrfPanelCellRenderer extends JPanel implements TableCellRenderer{

	private static final long serialVersionUID = -2474494701054971719L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
	removeAll();
	add((JComponent)value);
	return this;
	}
}
