package module.playerOverview;

import core.gui.comp.table.TableSorter;
import core.gui.model.PlayerOverviewModel;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

public class SpielerUebersichtPrintTable extends JTable {

	private static final long serialVersionUID = -523733130618224089L;
	
	//~ Instance fields ----------------------------------------------------------------------------

    //TableSorter sorter;
    private PlayerOverviewModel m_clTableModel;
    private TableSorter m_clTableSorter;

    //~ Constructors -------------------------------------------------------------------------------


//  private DragSource                  m_clDragsource  =   null;
    public SpielerUebersichtPrintTable(PlayerOverviewTable table) {
        super();
        m_clTableSorter = table.getSorter();
        m_clTableModel = (PlayerOverviewModel) m_clTableSorter.getModel();
        initModel(table.getSpaltenreihenfolge());
        setDefaultRenderer(java.lang.Object.class,
                           new core.gui.comp.renderer.HODefaultTableCellRenderer());
        setSelectionBackground(core.gui.comp.renderer.HODefaultTableCellRenderer.SELECTION_BG);
    }
    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Initialisiert das Model
     */
    private void initModel(int[][] spaltenreihenfolge) {
        setOpaque(false);

        setModel(m_clTableSorter);

        final TableColumnModel tableColumnModel = getColumnModel();

        for (int i = 0; i < m_clTableModel.getColumnCount(); i++) {
            tableColumnModel.getColumn(i).setIdentifier(new Integer(i));
        }

//        int[][] targetColumn = m_clTableModel.getColumnOrder();//gui.UserParameter.instance().spieleruebersichtsspaltenreihenfolge;

//        for (int i = 0; i < 45; i++) {
//            tableColumnModel.getColumn(i).setIdentifier(new Integer(i));
//        }

        int[][] targetColumn = spaltenreihenfolge;

        //Reihenfolge -> nach [][1] sortieren
        targetColumn = core.util.Helper.sortintArray(targetColumn, 1);

        if (targetColumn != null) {
            for (int i = 0; i < targetColumn.length; i++) {
                this.moveColumn(getColumnModel().getColumnIndex(Integer.valueOf(targetColumn[i][0])),
                                targetColumn[i][1]);
            }
        }

        m_clTableSorter.addMouseListenerToHeaderInTable(this);

        //        int breite = (int) (55d * (1d
        //                     + ((gui.UserParameter.instance().anzahlNachkommastellen - 1) / 4.5d)));
//        final int breite2 = (int) (55d * (1d
//                            + ((gui.UserParameter.instance().anzahlNachkommastellen - 1) / 4.5d)));

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        m_clTableModel.setColumnsSize(getColumnModel());
//        
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(0)))
//                        .setPreferredWidth(167);
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(0))).setMinWidth(167);
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(0)))
//                        .setPreferredWidth(167);
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(1))).setMinWidth(0);
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(1)))
//                        .setPreferredWidth(0);
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(2))).setMinWidth(25);
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(2)))
//                        .setPreferredWidth(25);
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(3))).setMinWidth(de.hattrickorganizer.tools.Helper
//                                                                                                .calcCellWidth(20));
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(3)))
//                        .setPreferredWidth(de.hattrickorganizer.tools.Helper.calcCellWidth(20));
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(4)))
//                        .setPreferredWidth(gui.UserParameter.instance().bestPostWidth);
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(5))).setMinWidth(20);
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(5)))
//                        .setPreferredWidth(20);
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(6))).setMinWidth(50);
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(6)))
//                        .setPreferredWidth(50);
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(7))).setMinWidth(50);
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(7)))
//                        .setPreferredWidth(50);
//
//        for (int i = 8; i < 12; i++) {
//            tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(i))).setMinWidth(Helper
//                                                                                                    .calcCellWidth(40));
//            tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(i)))
//                            .setPreferredWidth(de.hattrickorganizer.tools.Helper.calcCellWidth(40));
//        }
//
//        for (int i = 12; i < 19; i++) {
//            tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(i))).setMinWidth(Helper
//                                                                                                    .calcCellWidth(breite2));
//            tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(i)))
//                            .setPreferredWidth(de.hattrickorganizer.tools.Helper.calcCellWidth(breite2));
//        }
//
//        for (int i = 19; i < 37; i++) {
//            tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(i))).setMinWidth(Helper
//                                                                                                    .calcCellWidth(0));
//            tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(i)))
//                            .setPreferredWidth(de.hattrickorganizer.tools.Helper.calcCellWidth(0));
//        }
//
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(37)))
//                        .setPreferredWidth(de.hattrickorganizer.tools.Helper.getMaxBewertungWidth(de.hattrickorganizer.model.HOVerwaltung.instance()
//                                                                                                                                         .getModel()
//                                                                                                                                         .getAllSpieler()));
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(37))).setMinWidth(70);
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(38))).setMinWidth(de.hattrickorganizer.tools.Helper
//                                                                                                 .calcCellWidth(0));
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(38)))
//                        .setPreferredWidth(de.hattrickorganizer.tools.Helper.calcCellWidth(0));
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(39))).setMinWidth(de.hattrickorganizer.tools.Helper
//                                                                                                 .calcCellWidth(0));
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(39)))
//                        .setPreferredWidth(de.hattrickorganizer.tools.Helper.calcCellWidth(0));
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(40))).setMinWidth(de.hattrickorganizer.tools.Helper
//                                                                                                 .calcCellWidth(0));
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(40)))
//                        .setPreferredWidth(de.hattrickorganizer.tools.Helper.calcCellWidth(0));
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(41))).setMinWidth(de.hattrickorganizer.tools.Helper
//                                                                                                 .calcCellWidth(0));
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(41)))
//                        .setPreferredWidth(de.hattrickorganizer.tools.Helper.calcCellWidth(0));
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(42))).setMinWidth(de.hattrickorganizer.tools.Helper
//                                                                                                 .calcCellWidth(60));
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(43))).setMinWidth(de.hattrickorganizer.tools.Helper
//                                                                                                 .calcCellWidth(90));
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(44))).setMinWidth(de.hattrickorganizer.tools.Helper
//                                                                                                 .calcCellWidth(0));
//        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(new Integer(44)))
//                        .setPreferredWidth(de.hattrickorganizer.tools.Helper.calcCellWidth(0));
        setSelectionMode(0);
        setRowSelectionAllowed(true);

        //setGridColor(new Color(220, 220, 220));
        //getTableHeader().setReorderingAllowed( false );
        //m_clDragsource = new DragSource();
        //m_clDragsource.createDefaultDragGestureRecognizer( this, 1, this );
    }
}
