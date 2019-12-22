// %3426612608:de.hattrickorganizer.gui.arenasizer%
package tool.arenasizer;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoppelLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.util.Helper;
import tool.updater.TableModel;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;



/**
 * Panel mit JTabel für die Arena anzeige und zum Testen
 */
final class ArenaPanel extends JPanel {

	private static final long serialVersionUID = -3049319214078126491L;

    //~ Instance fields ----------------------------------------------------------------------------

    private ArenaSizer m_clArenaSizer = new ArenaSizer();
    private JTable m_jtArena = new JTable();

    //Teststadium
    private Stadium m_clStadium;
    private String[] UEBERSCHRIFT = {"", HOVerwaltung.instance().getLanguageString("Aktuell"), HOVerwaltung.instance().getLanguageString("Maximal"),
    		HOVerwaltung.instance().getLanguageString("Durchschnitt"), HOVerwaltung.instance().getLanguageString("Minimal")};
    private Stadium[] m_clStadien;
    private IHOTableEntry[][] values;

    //~ Constructors -------------------------------------------------------------------------------

    ArenaPanel() {
    	setLayout(new BorderLayout());
    	add(new JScrollPane(m_jtArena));
        m_jtArena.setDefaultRenderer(java.lang.Object.class, new HODefaultTableCellRenderer());
        m_jtArena.getTableHeader().setReorderingAllowed(false);
        initTabelle();
        reInit();
    }

    //-------Refresh---------------------------------
    public void reInit() {
    	HOModel model = HOVerwaltung.instance().getModel();
        m_clStadium = model.getStadium();
        m_clStadien = m_clArenaSizer.calcConstructionArenas(m_clStadium,model.getVerein().getFans());
        //Entrys mit Werten füllen
        reinitTable();
    }

    private void initTabelle() {
        //Tablewerte setzen
        values = new IHOTableEntry[9][5];
        HOVerwaltung hoV = HOVerwaltung.instance();

        String[] columnText = {"ls.club.arena.terraces","ls.club.arena.basicseating","ls.club.arena.seatsunderroof","ls.club.arena.seatsinvipboxes","Gesamt","Einnahmen","Unterhalt","Gewinn","Baukosten"};
        for (int i = 0; i < columnText.length; i++) {
        	values[i][0] = new ColorLabelEntry(hoV.getLanguageString(columnText[i]),
                    ColorLabelEntry.FG_STANDARD,  ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, SwingConstants.LEFT);
		}


        //Platzwerte
        for (int i = 0; i < 9; i++) {
        	for (int j = 1; j < 5; j++) {
        		if(i<4)
        			values[i][j] = createDoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
        		else if(i == 4)
        			values[i][j] = createDoppelLabelEntry(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
        		else if(i > 4)
        			 values[i][j] = createDoppelLabelEntry(ColorLabelEntry.BG_SINGLEPLAYERVALUES);
			}

		}

        m_jtArena.setModel(new TableModel(values,UEBERSCHRIFT));

        final TableColumnModel columnModel = m_jtArena.getColumnModel();
        columnModel.getColumn(0).setMinWidth(Helper.calcCellWidth(150));
        columnModel.getColumn(1).setMinWidth(Helper.calcCellWidth(160));
        columnModel.getColumn(2).setMinWidth(Helper.calcCellWidth(160));
        columnModel.getColumn(3).setMinWidth(Helper.calcCellWidth(160));
        columnModel.getColumn(4).setMinWidth(Helper.calcCellWidth(160));
    }

    /**
     * create a new DoppelLabelEntry with default values
     * @param background
     * @return
     */
    private DoppelLabelEntry createDoppelLabelEntry(Color background){
    	return new DoppelLabelEntry(new ColorLabelEntry("",
                						ColorLabelEntry.FG_STANDARD,
                						background, SwingConstants.RIGHT),
                	               new ColorLabelEntry("",
                	            		   ColorLabelEntry.FG_STANDARD,
                	            		   background, SwingConstants.RIGHT));
    }

    void reinitArena(Stadium currentArena, int maxSupporter, int normalSupporter, int minSupporter) {
        m_clStadium = currentArena;
        m_clStadien = m_clArenaSizer.calcConstructionArenas(currentArena, maxSupporter, normalSupporter, minSupporter);

        //Entrys mit Werten füllen
        reinitTable();
    }

    private void reinitTable() {
        final Stadium stadium = HOVerwaltung.instance().getModel().getStadium();
        if (m_clStadium != null) {
                ((DoppelLabelEntry) values[0][1]).getLinks().setText(m_clStadium.getStehplaetze() + "");
                ((DoppelLabelEntry) values[0][1]).getRechts().setSpecialNumber(m_clStadium.getStehplaetze() - stadium.getStehplaetze(),false);
                ((DoppelLabelEntry) values[1][1]).getLinks().setText(m_clStadium.getSitzplaetze() + "");
                ((DoppelLabelEntry) values[1][1]).getRechts().setSpecialNumber(m_clStadium.getSitzplaetze() - stadium.getSitzplaetze(),false);
                ((DoppelLabelEntry) values[2][1]).getLinks().setText(m_clStadium.getUeberdachteSitzplaetze()+ "");
                ((DoppelLabelEntry) values[2][1]).getRechts().setSpecialNumber(m_clStadium.getUeberdachteSitzplaetze() - stadium.getUeberdachteSitzplaetze(),false);
                ((DoppelLabelEntry) values[3][1]).getLinks().setText(m_clStadium.getLogen()+ "");
                ((DoppelLabelEntry) values[3][1]).getRechts().setSpecialNumber(m_clStadium.getLogen() - stadium.getLogen(), false);
                ((DoppelLabelEntry) values[4][1]).getLinks().setText(m_clStadium.getGesamtgroesse()+ "");
                ((DoppelLabelEntry) values[4][1]).getRechts().setSpecialNumber(m_clStadium.getGesamtgroesse() - stadium.getGesamtgroesse(), false);
                ((DoppelLabelEntry) values[5][1]).getLinks().setSpecialNumber(m_clArenaSizer.calcMaxIncome(m_clStadium),true);
                ((DoppelLabelEntry) values[5][1]).getRechts().setSpecialNumber(m_clArenaSizer.calcMaxIncome(m_clStadium) - m_clArenaSizer.calcMaxIncome(stadium),true);
                ((DoppelLabelEntry) values[6][1]).getLinks().setSpecialNumber(-m_clArenaSizer.calcMaintenance(m_clStadium), true);
                ((DoppelLabelEntry) values[6][1]).getRechts().setSpecialNumber(-(m_clArenaSizer.calcMaintenance(m_clStadium) - m_clArenaSizer.calcMaintenance(stadium)),true);
                ((DoppelLabelEntry) values[7][1]).getLinks().setSpecialNumber(m_clArenaSizer.calcMaxIncome(m_clStadium) - m_clArenaSizer.calcMaintenance(m_clStadium),true);
                ((DoppelLabelEntry) values[7][1]).getRechts().setSpecialNumber((m_clArenaSizer.calcMaxIncome(m_clStadium) - m_clArenaSizer.calcMaintenance(m_clStadium))- (m_clArenaSizer.calcMaxIncome(stadium)
                                                                                      - m_clArenaSizer.calcMaintenance(stadium)), true);
                ((DoppelLabelEntry) values[8][1]).getLinks().setSpecialNumber(-m_clArenaSizer.calcConstructionCosts(m_clStadium.getStehplaetze()- stadium.getStehplaetze(),
                                                                                                     m_clStadium.getSitzplaetze()- stadium.getSitzplaetze(),
                                                                                                     m_clStadium.getUeberdachteSitzplaetze()- stadium.getUeberdachteSitzplaetze(),
                                                                                                     m_clStadium.getLogen()- stadium.getLogen()),true);
                ((DoppelLabelEntry) values[8][1]).getRechts().setText("");

                for (int i = 2; i < 5; i++) {
                    ((DoppelLabelEntry) values[0][i]).getLinks().setText(m_clStadien[i - 2].getStehplaetze()+ "");
                    ((DoppelLabelEntry) values[0][i]).getRechts().setSpecialNumber(m_clStadien[i- 2].getStehplaetze()- m_clStadium.getStehplaetze(),false);
                    ((DoppelLabelEntry) values[1][i]).getLinks().setText(m_clStadien[i - 2].getSitzplaetze()+ "");
                    ((DoppelLabelEntry) values[1][i]).getRechts().setSpecialNumber(m_clStadien[i- 2].getSitzplaetze()- m_clStadium.getSitzplaetze(),false);
                    ((DoppelLabelEntry) values[2][i]).getLinks().setText(m_clStadien[i - 2].getUeberdachteSitzplaetze()+ "");
                    ((DoppelLabelEntry) values[2][i]).getRechts().setSpecialNumber(m_clStadien[i- 2].getUeberdachteSitzplaetze()- m_clStadium.getUeberdachteSitzplaetze(),false);
                    ((DoppelLabelEntry) values[3][i]).getLinks().setText(m_clStadien[i - 2].getLogen() + "");
                    ((DoppelLabelEntry) values[3][i]).getRechts().setSpecialNumber(m_clStadien[i- 2].getLogen()- m_clStadium.getLogen(),false);
                    ((DoppelLabelEntry) values[4][i]).getLinks().setText(m_clStadien[i - 2].getGesamtgroesse()+ "");
                    ((DoppelLabelEntry) values[4][i]).getRechts().setSpecialNumber(m_clStadien[i- 2].getGesamtgroesse()- m_clStadium.getGesamtgroesse(),false);
                    ((DoppelLabelEntry) values[5][i]).getLinks().setSpecialNumber(m_clArenaSizer.calcMaxIncome(m_clStadien[i- 2]),true);
                    ((DoppelLabelEntry) values[5][i]).getRechts().setSpecialNumber(m_clArenaSizer.calcMaxIncome(m_clStadien[i- 2])- m_clArenaSizer.calcMaxIncome(m_clStadium),true);
                    ((DoppelLabelEntry) values[6][i]).getLinks().setSpecialNumber(-m_clArenaSizer.calcMaintenance(m_clStadien[i- 2]),true);
                    ((DoppelLabelEntry) values[6][i]).getRechts().setSpecialNumber(-(m_clArenaSizer.calcMaintenance(m_clStadien[i- 2])
                                                                                          - m_clArenaSizer.calcMaintenance(m_clStadium)),true);
                    ((DoppelLabelEntry) values[7][i]).getLinks().setSpecialNumber(m_clArenaSizer.calcMaxIncome(m_clStadien[i- 2])- m_clArenaSizer.calcMaintenance(m_clStadien[i- 2]),true);
                    ((DoppelLabelEntry) values[7][i]).getRechts().setSpecialNumber((m_clArenaSizer.calcMaxIncome(m_clStadien[i- 2])- m_clArenaSizer.calcMaintenance(m_clStadien[i- 2]))
                                                                                          - (m_clArenaSizer.calcMaxIncome(m_clStadium)- m_clArenaSizer.calcMaintenance(m_clStadium)),true);
                    ((DoppelLabelEntry) values[8][i]).getLinks().setSpecialNumber(-m_clStadien[i- 2].getAusbauKosten(),true);
                }

                m_jtArena.setModel(new TableModel(values,UEBERSCHRIFT));
                m_jtArena.getColumnModel().getColumn(0).setMinWidth(Helper.calcCellWidth(150));
                m_jtArena.getColumnModel().getColumn(1).setMinWidth(Helper.calcCellWidth(160));
                m_jtArena.getColumnModel().getColumn(2).setMinWidth(Helper.calcCellWidth(160));
                m_jtArena.getColumnModel().getColumn(3).setMinWidth(Helper.calcCellWidth(160));
                m_jtArena.getColumnModel().getColumn(4).setMinWidth(Helper.calcCellWidth(160));
        }
    }
}
