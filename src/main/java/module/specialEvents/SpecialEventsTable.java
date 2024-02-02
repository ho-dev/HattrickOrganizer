package module.specialEvents;

import core.gui.HOMainFrame;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.HOTableModel;
import core.gui.model.UserColumnController;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JTable;

public class SpecialEventsTable extends JTable {

	public SpecialEventsTable() {
		super(UserColumnController.instance().getSpecialEventsTableModel());
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				JTable table = (JTable) me.getSource();
				Point p = me.getPoint();
				int col = table.columnAtPoint(p);
				var tableModel = (HOTableModel) table.getModel();
				var matchDateColumn = Arrays.stream(tableModel.getColumns()).findFirst(); // Match date column is first column in table model
				if (matchDateColumn.isPresent()) {
					if (col == matchDateColumn.get().getIndex()) {
						try {
							SpecialEventsTableModel model = UserColumnController.instance().getSpecialEventsTableModel();
							Match oMatch = model.getMatch(table.rowAtPoint(p));
							if (me.isShiftDown()) {
								Desktop.getDesktop().browse(oMatch.getHTURL());
							} else {
								HOMainFrame.instance().showMatch(oMatch.getMatchId());
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		setOpaque(false);
		setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
		SpecialEventsTableModel tblModel = UserColumnController.instance().getSpecialEventsTableModel();
		setModel(tblModel);
		tblModel.restoreUserSettings(this);
	}

	public void storeUserSettings() {
		var tableModel = (HOTableModel)getModel();
		tableModel.storeUserSettings(this);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int mColIndex) {
		return false;
	}
}
