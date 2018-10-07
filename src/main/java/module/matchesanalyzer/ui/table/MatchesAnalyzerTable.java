package module.matchesanalyzer.ui.table;

import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellRenderer;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellType;
import module.matchesanalyzer.ui.table.cell.content.MatchesAnalyzerCellContent;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


public class MatchesAnalyzerTable extends JTable {
	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_ROWS_HEIGHT = 21;

	private final MouseMotionAdapter tooltips = new MouseMotionAdapter() {
		TableColumn current = null;
		
	    @Override
		public void mouseMoved(MouseEvent evt) {
	        TableColumn col = null;
	        String name = null;
	        
	        JTableHeader header = (JTableHeader)evt.getSource();
	        MatchesAnalyzerTable table = (MatchesAnalyzerTable)header.getTable();
	        TableColumnModel cModel = table.getColumnModel();
	        MatchesAnalyzerTableModel tModel = (MatchesAnalyzerTableModel)getModel();
	        int index = cModel.getColumnIndexAtX(evt.getX());
	        
	        if (index >= 0) {
	            col = cModel.getColumn(index);
	            name = tModel.getColumnTitle(index);
	        }

	        if (col != current) {
	            header.setToolTipText(name);
	            current = col;
	        }
	    }
	};
	
	public MatchesAnalyzerTable() {
		super(new MatchesAnalyzerTableModel());
		
		setRowHeight(DEFAULT_ROWS_HEIGHT);
		setDefaultRenderer(MatchesAnalyzerCellContent.class, new MatchesAnalyzerCellRenderer());
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setRowSelectionAllowed(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		DefaultTableColumnModel cModel = (DefaultTableColumnModel)getColumnModel();
		for(int i = 0; i < getColumnCount(); i++) {
			TableColumn col = cModel.getColumn(i);
			col.setPreferredWidth(MatchesAnalyzerCellType.values()[i].getStyle().getWidth());
		}
		
		JTableHeader header = getTableHeader();
		header.addMouseMotionListener(tooltips);
	}

}
