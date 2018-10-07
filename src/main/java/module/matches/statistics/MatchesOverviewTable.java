package module.matches.statistics;

import core.db.DBManager;
import core.gui.comp.table.ToolTipHeader;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController;
import core.model.match.MatchesOverviewRow;
import core.util.Helper;
import module.matches.SpielePanel;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;


public class MatchesOverviewTable extends JTable {
	private static final long serialVersionUID = -8724051830928497450L;
	
	private MatchesOverviewColumnModel tableModel;
	// private TableSorter m_clTableSorter;
	 
	public MatchesOverviewTable(int matchtyp){
		super();
	    initModel(matchtyp);
        setDefaultRenderer(Object.class,new MatchesOverviewRenderer());
        setDefaultRenderer(Integer.class,new MatchesOverviewRenderer());
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

	}
	
    private void initModel(int matchtyp) {
        setOpaque(false);

        if (tableModel == null) {
        	tableModel = UserColumnController.instance().getMatchesOverview1ColumnModel();
        	if(matchtyp == SpielePanel.ALLE_SPIELE || matchtyp == SpielePanel.NUR_FREMDE_SPIELE){
            	MatchesOverviewRow[] tmp = new MatchesOverviewRow[0];
            	tableModel.setValues(tmp);
            } else {
            	tableModel.setValues(DBManager.instance().getMatchesOverviewValues(matchtyp));
            }

            final ToolTipHeader header = new ToolTipHeader(getColumnModel());
            header.setToolTipStrings(tableModel.getTooltips());
            header.setToolTipText("");
            setTableHeader(header);
            setModel(tableModel);

            final TableColumnModel tableColumnModel = getColumnModel();

            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                tableColumnModel.getColumn(i).setIdentifier(new Integer(i));
            }

            int[][] targetColumn = tableModel.getColumnOrder();

            //Reihenfolge -> nach [][1] sortieren
            targetColumn = Helper.sortintArray(targetColumn, 1);

            if (targetColumn != null) {
                for (int i = 0; i < targetColumn.length; i++) {
                    this.moveColumn(getColumnModel().getColumnIndex(Integer.valueOf(targetColumn[i][0])),
                                    targetColumn[i][1]);
                }
            }

            //m_clTableSorter.addMouseListenerToHeaderInTable(this);
            tableModel.setColumnsSize(getColumnModel());
        } else {
        	tableModel.setValues(DBManager.instance().getMatchesOverviewValues(matchtyp));
        }

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setRowSelectionAllowed(true);

        //m_clTableSorter.initsort();
    }
	
    int[][] getSpaltenreihenfolge() {
        final int[][] reihenfolge = new int[tableModel.getColumnCount()][2];

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            // Modelindex
            reihenfolge[i][0] = i;

            //ViewIndex
            reihenfolge[i][1] = convertColumnIndexToView(i);
        }

        return reihenfolge;
    }

    public final void saveColumnOrder(){
    	final UserColumn[] columns = tableModel.getDisplayedColumns();
    	final TableColumnModel tableColumnModel = getColumnModel();
    	for (int i = 0; i < columns.length; i++) {
    		columns[i].setIndex(convertColumnIndexToView(i));
    		columns[i].setPreferredWidth(tableColumnModel.getColumn(convertColumnIndexToView(i)).getWidth());
    	}
    	tableModel.setCurrentValueToColumns(columns);
    	DBManager.instance().saveHOColumnModel(tableModel);
    }
    
    public void refresh(int matchtypen) {
        if(matchtypen == SpielePanel.ALLE_SPIELE || matchtypen == SpielePanel.NUR_FREMDE_SPIELE){
        	MatchesOverviewRow[] tmp = new MatchesOverviewRow[0];
        	tableModel.setValues(tmp);
        } else {
        	initModel(matchtypen);
        }
    }
 
}
