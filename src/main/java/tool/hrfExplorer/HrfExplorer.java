package tool.hrfExplorer;

import core.db.DBManager;
import core.file.ExampleFileFilter;
import core.gui.IRefreshable;
import core.gui.RefreshManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.enums.MatchType;
import core.util.HOLogger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;


/**
 * @author KickMuck
 */

public class HrfExplorer extends ImagePanel implements ActionListener,ItemListener,MouseListener,IRefreshable,TableColumnModelListener
{

	private static final long serialVersionUID = -4187405421481083232L;
	private MatchKurzInfo[] m_kurzInfo;				// Adapter für Spieleinfos, u.a. ob Friendly oder Liga
	// Members für Farben
	private Color gruen = ThemeManager.getColor(HOColorName.HRF_GREEN_BG);
	private Color hellblau = ThemeManager.getColor(HOColorName.HRF_LIGHTBLUE_BG);
	private Color dunkelblau = ThemeManager.getColor(HOColorName.HRF_DARKBLUE_BG);
	private Color rot = ThemeManager.getColor(HOColorName.HRF_RED_BG);
	private Color m_LineColor;

	//Members für die GUI
	private JSplitPane m_SplitPane_main = null;
	private JSplitPane m_SplitPane_top = null;
	private JSplitPane m_SplitPane_top_left = null;		// SplitPane für Calendar und Imports

	private JScrollPane m_ScrollPane_Calendar = null;
	private JScrollPane m_ScrollPane_FileTable = null;
	private JScrollPane m_ScrollPane_Details = null;
	private JScrollPane m_ScrollPane_Imports = null;	// nimmt die Tabelle für die zu importierenden Dateien auf

	private JPanel m_Panel_Calendar_main = null;
	private JPanel m_Panel_Calendar_main_north = null;
	private JPanel m_Panel_Details_main = null;
	private JPanel m_Panel_Details_north = null;
	private JPanel m_Panel_FileTable_main = null;
	private JPanel m_Panel_FileTable_main_north = null;
	private JPanel m_Panel_Imports_main = null;			// CENTER=m_ScrollPane_Imports,NORTH=m_Button_ResetImports

	private JButton m_Button_load_file = null;
	private JButton m_Button_delete_file = null;
	private JButton m_Button_delete_db = null;
	private JButton m_Button_Delete_Row = null;
	private JButton m_Button_ImportList = null;
	private JButton m_Button_Select_All = null;
	private JButton m_Button_reset = null;
	private JButton m_Button_Month_Forward = null;
	private JButton m_Button_Month_Back = null;
	private JButton m_Button_GoTo = null;
	private JButton m_Button_ResetImports = null;

	private JLabel m_Label_Monat = null;
	private JLabel m_Label_DetailHeader = null;

	private JComboBox m_CB_year = null;
	private JComboBox m_CB_month = null;

	// Members für die Tabellen
	private JTable m_Table_Calendar = null;
	private JTable m_Table_Filelist = null;
	private JTable m_Table_Details = null;
	private JTable m_Table_Imports = null;				// Tabelle mit den Pfaden für Importdateien

	private HrfTableModel m_TableModel_Calendar = null;
	private HrfTableModel m_TableModel_Filelist = null;
	private HrfTableModel m_TableModel_Details = null;
	private HrfTableModel m_TableModel_Imports = null;


	private HrfPanelCellRenderer m_renderer = new HrfPanelCellRenderer();


	private static int m_int_selectedMonth;				// int für den Monat im Calendar-Panel (0-11)
	private static int m_int_selectedYear;				// int für das Jahr aus dem Calendar-Panel
	private int m_int_firstYearInDB;					// int für das Jahr des ersten DB-Eintrages
	private int m_int_actualYear;						// int für das aktuelle Jahr

	private int m_int_Hoehe_DetailPanels;				// Höhe des Panels, in dem alle Details in der Detail-Tabelle stehen
	private int m_int_Breite_Detail_Fixed = 130;		// Breite der 1. Spalte der Detail-Tabelle
	private int m_int_Breite_Detail_Var = 140;			// Breite der weiteren Spalten der Detail-Tabelle
	private int m_int_Hoehe_Label = 16;					// Höhe der Labels für die Details in der Detail-Tabelle

	private int m_int_anz_DBEintraege = 0;				// Anzahl der HRF-Files in der DB
	private int m_TeamID;								// Die TeamID

	// Breiten der Spalten in der jeweiligen Tabelle
	private int[] m_intAr_col_width_Filelist = {30,130,140,80,40,60,60,110,40,60};
	private int[] m_intAr_col_width_Calendar = {40,40,40,40,40,40,40,40};
	private int[] m_intAr_col_width_Details = {140};

	private String m_Str_hrfPfad = "";					// Pfad aus UserSettings, dort werden normalerweise die hrf-files hingespeichert

	private String[] m_Ar_Detail_Label_fix;				// Bezeichnungen in der 1.Spalte der Detail-Tabelle


	//Variablen für Detailtabelle
	@SuppressWarnings("unchecked")
	private Vector m_V_Details_Header;
	@SuppressWarnings("unchecked")
	private Vector m_V_Details_Values;
	//Variablen für Filelist-Tabelle
	@SuppressWarnings("unchecked")
	private Vector m_V_Filelist_Header;
	@SuppressWarnings("unchecked")
	private Vector m_V_Filelist_Values;
//	Variablen für Calendar-Tabelle
	@SuppressWarnings("unchecked")
	private Vector m_V_Calendar_Header;
	@SuppressWarnings("unchecked")
	private Vector m_V_Calendar_Values;

	private static String[] m_Ar_days = new String[7];
	private static Vector<String> m_V_months = null;
	@SuppressWarnings("unchecked")
	private Vector m_V_Filelist_Keys = new Vector();

	private File[] m_files;
	private JFileChooser m_FileChooser_chooser;
	private GregorianCalendar m_gc;
	private ResultSet m_queryResult;
	private ResultSet m_Result_SpecialEvent;

	@SuppressWarnings("unchecked")
	private static Hashtable m_HashTable_DayInDB = new Hashtable(40);		// KEY: Tag des gewählten Monats in Calendar, 		VALUE: HRF-ID für diesen Tag
	@SuppressWarnings("unchecked")
	private Hashtable m_HashTable_Details = new Hashtable(40);			// KEY: Pfad oder Datum eines HrfDetails-Objekt, 	VALUE: das HrfDetails-Objekt
	@SuppressWarnings("unchecked")
	private Hashtable m_HashTable_Details_ColHeader = new Hashtable(40);	// KEY: Datum eines HrfDetails-Objekt				VALUE: das HrfDetails-Objekt
	@SuppressWarnings("unchecked")
	private Hashtable m_HashTable_Columns = new Hashtable(40);			// KEY: Spaltenname der Detailtabelle				VALUE: Vector mit dem Inhalt einer Spalte der Detailtabelle
	@SuppressWarnings("unchecked")
	private static Hashtable m_HashTable_DatumKey = new Hashtable(40);	// KEY: Datum im Format YYYY-MM-DD					VALUE: Dateipfad
	@SuppressWarnings("unchecked")
	private Hashtable m_HashTable_Import = new Hashtable(40);				// KEY: Pfad der Dateien aus der Importtabelle		VALUE: ---
	@SuppressWarnings("unchecked")
	private static Hashtable m_HashTable_isEvent = new Hashtable(40);		// KEY: Tag des gewählten Monats in Calendar, 		VALUE: Matchtyp als String
	@SuppressWarnings("unchecked")
	private Hashtable m_HashTable_MatchTyp = new Hashtable(40);			// KEY: Match-ID, 									VALUE: Matchtyp
	private static Hashtable<String,String> m_HashTable_EventInfo = new Hashtable<String,String>(40);
	@SuppressWarnings("unchecked")
	private Hashtable m_HashTable_EventGUI = new Hashtable();			// KEY: Der Tag des Events							VALUE: Vector mit Zeit(sek) und Eventtyp

	public HrfExplorer() {
		initialize();
	}
	/**
	 * Wird von HO aufgerufen, wenn das Tab aktiviert wird
	 * @param hOMiniModel Das MiniModel, übergeben von HO
	 */
	@SuppressWarnings("unchecked")
	private void initialize() {

		HOVerwaltung hoV = HOVerwaltung.instance();
		// Aktuelles Datum ermitteln und in die Members schreiben
		m_gc = new GregorianCalendar();
		m_int_selectedMonth = m_gc.get(GregorianCalendar.MONTH);
		m_int_selectedYear = m_gc.get(GregorianCalendar.YEAR);
		m_int_actualYear = m_gc.get(GregorianCalendar.YEAR);

		// TeamID setzen
		m_TeamID = hoV.getModel().getBasics().getTeamId();

		// Matches für das Team holen und in die Hashtable m_HashTable_MatchTyp füllen
		m_kurzInfo = DBManager.instance().getMatchesKurzInfo(m_TeamID);
		for(int ii = 0; ii < m_kurzInfo.length; ii++)
		{
			m_HashTable_MatchTyp.put(new Integer(m_kurzInfo[ii].getMatchID()),new Integer(m_kurzInfo[ii].getMatchType().getId()));
		}

        // Namen der Tage in m_Ar_days schreiben
        setTage();

        //Füllen der HashTable m_HashTable_EventInfo mit den Sprachenabhängigen Wörtern

        m_HashTable_EventInfo.put("L",hoV.getLanguageString("ls.match.matchtype.league"));
        m_HashTable_EventInfo.put("F",hoV.getLanguageString("ls.match.matchtype.friendly_normal"));
        m_HashTable_EventInfo.put("I",hoV.getLanguageString("ls.match.matchtype.internationalfriendly_normal"));
        m_HashTable_EventInfo.put("P",hoV.getLanguageString("ls.match.matchtype.cup"));
        m_HashTable_EventInfo.put("Q",hoV.getLanguageString("ls.match.matchtype.qualification"));
        m_HashTable_EventInfo.put("DB",hoV.getLanguageString("ttCalDB"));
        m_HashTable_EventInfo.put("FILE",hoV.getLanguageString("ttCalFile"));

		// Anzahl der HRF-Files in der DB ermitteln
		doSelect("SELECT COUNT(*) FROM HRF");
		try {
			while(m_queryResult.next())	{
				m_int_anz_DBEintraege = m_queryResult.getInt(1);
			}
		}
		catch(SQLException sexc) {
			//debugWindow.append("HHHHHHHHH");
			//JDialog tmp = new JDialog(m_gui.getOwner4Dialog(),"File read error");
        	//tmp.add(new JLabel("An error occured while loading language.properties. Please report to me."));
        	//tmp.show();
		}

		// Jahr des ersten HRF in der DB ermitteln
		doSelect("SELECT MIN(DATUM) FROM HRF");
		try
		{
			while(m_queryResult.next())
			{
				try
				{
					String jahr = (m_queryResult.getObject(1).toString()).substring(0,4);
					m_int_firstYearInDB = Integer.parseInt(jahr);
				}
				catch(Exception e)
				{
					m_int_firstYearInDB = m_int_actualYear;
				}
			}
		}
		catch(SQLException s)
		{
			HOLogger.instance().error(getClass(), s);
		}

		// Ausgangspfad für den Start des JFileChooser ermitteln

		m_Str_hrfPfad = null;

		doSelect("SELECT CONFIG_VALUE FROM USERCONFIGURATION WHERE CONFIG_KEY ='hrfImport_HRFPath'");
		try
		{
			while(m_queryResult.next())
			{
				if(m_queryResult.wasNull())
				{
					m_Str_hrfPfad = null;
				}
				else
				{
					m_Str_hrfPfad = m_queryResult.getString(1);
				}
			}
		}
		catch(SQLException sexc)
		{
			m_Str_hrfPfad = null;
		}

		/*
         *Erstellen der Dummy Tabelle "Importliste"
         */
		Vector importHeader = new Vector();
		Vector importValues = new Vector();

		importHeader.add(hoV.getLanguageString("pfad"));

		m_TableModel_Imports = new HrfTableModel(importHeader, importValues);
		m_Table_Imports = new HrfTable(m_TableModel_Imports, "HRFImportieren");

        /*
         *Erstellen der Dummy Tabelle "Filelist"
         */
        m_V_Filelist_Header = new Vector();
        m_V_Filelist_Values = new Vector();
        Vector tmpV = new Vector();

        m_V_Filelist_Header.add("");
        m_V_Filelist_Header.add(hoV.getLanguageString("datname"));
        m_V_Filelist_Header.add(hoV.getLanguageString("Datum"));
        m_V_Filelist_Header.add(hoV.getLanguageString("tag"));
        m_V_Filelist_Header.add(hoV.getLanguageString("kw"));
        m_V_Filelist_Header.add(hoV.getLanguageString("Season"));
        m_V_Filelist_Header.add(hoV.getLanguageString("Liga"));
        m_V_Filelist_Header.add(hoV.getLanguageString("Training"));
        m_V_Filelist_Header.add("%");
        m_V_Filelist_Header.add(hoV.getLanguageString("indb"));

        m_TableModel_Filelist = new HrfTableModel(m_V_Filelist_Header, m_V_Filelist_Values);
        m_Table_Filelist = new HrfTable(m_TableModel_Filelist,m_intAr_col_width_Filelist, "filelist");
        m_Table_Filelist.addMouseListener(this);

        /*
         *Erstellen der Dummy Tabelle "Calendar"
         */
        m_V_Calendar_Header = new Vector();
        m_V_Calendar_Values = new Vector();

        m_V_Calendar_Header.add(hoV.getLanguageString("kw"));
        m_V_Calendar_Header.add(hoV.getLanguageString("monkurz"));
        m_V_Calendar_Header.add(hoV.getLanguageString("diekurz"));
        m_V_Calendar_Header.add(hoV.getLanguageString("mitkurz"));
        m_V_Calendar_Header.add(hoV.getLanguageString("donkurz"));
        m_V_Calendar_Header.add(hoV.getLanguageString("frekurz"));
        m_V_Calendar_Header.add(hoV.getLanguageString("samkurz"));
        m_V_Calendar_Header.add(hoV.getLanguageString("sonkurz"));

        m_TableModel_Calendar = new HrfTableModel(m_V_Calendar_Header, m_V_Calendar_Values);
        m_Table_Calendar = new HrfTable(m_TableModel_Calendar,m_intAr_col_width_Calendar, "calendar");
        m_Table_Calendar.addMouseListener(this);
        m_Table_Calendar.setIntercellSpacing(new Dimension(2,2));
        m_Table_Calendar.setRowHeight(20);

        /*
         *Erstellen der Dummy Tabelle "Details"
         */
        //Vorbereiten der fixen Labelbeschriftungen
        m_Ar_Detail_Label_fix = new String[12];
        m_Ar_Detail_Label_fix[0] = hoV.getLanguageString("Liga");
        m_Ar_Detail_Label_fix[1] = hoV.getLanguageString("Season") + " / " +hoV.getLanguageString("Spieltag");
        m_Ar_Detail_Label_fix[2] = hoV.getLanguageString("Punkte") + " / " + hoV.getLanguageString("Tore");
        m_Ar_Detail_Label_fix[3] =hoV.getLanguageString("Platzierung");
        m_Ar_Detail_Label_fix[4] = hoV.getLanguageString("ls.team.trainingtype");
        m_Ar_Detail_Label_fix[5] = hoV.getLanguageString("ls.team.trainingintensity");
        m_Ar_Detail_Label_fix[6] = hoV.getLanguageString("ls.club.staff.assistantcoach");
        m_Ar_Detail_Label_fix[7] = hoV.getLanguageString("ls.team.confidence");
        m_Ar_Detail_Label_fix[8] = hoV.getLanguageString("AnzahlSpieler");
        m_Ar_Detail_Label_fix[9] =hoV.getLanguageString("ls.team.teamspirit");
		m_Ar_Detail_Label_fix[10] = hoV.getLanguageString("lasthrf");
		m_Ar_Detail_Label_fix[11] = hoV.getLanguageString("nexthrf");

		m_int_Hoehe_DetailPanels = m_Ar_Detail_Label_fix.length * m_int_Hoehe_Label;	// Festlege der Gesamthöhe des Detailpanels

        m_V_Details_Header = new Vector();
        m_V_Details_Values = new Vector();

        m_TableModel_Details = new HrfTableModel(m_V_Details_Header, m_V_Details_Values);
        m_Table_Details = new HrfTable(m_TableModel_Details,m_intAr_col_width_Details, "details");
        m_Table_Details.setRowMargin(-10);
        m_Table_Details.addMouseListener(this);
        m_Table_Details.getColumnModel().addColumnModelListener(this);

        // Erstellen des Panels für die 1. Spalte der Detailtabelle
        HrfPanel fixedPanel = new HrfPanel(m_int_Breite_Detail_Fixed,m_int_Hoehe_DetailPanels);
        fixedPanel.setLayout(new GridLayout(m_Ar_Detail_Label_fix.length,1));
        fixedPanel.addMouseListener(this);

        for(int ii = 0; ii < m_Ar_Detail_Label_fix.length; ii++)
        {
        	if(ii == 0 || ii%2 == 0)
        	{
        		m_LineColor = hellblau;
        	}
        	else
        	{
        		m_LineColor = dunkelblau;
        	}
        	fixedPanel.add(createLabel((m_Ar_Detail_Label_fix[ii] + " :"),m_int_Breite_Detail_Fixed,m_int_Hoehe_Label,JLabel.RIGHT,m_LineColor));
        }

        HrfPanel emptyPanel = new HrfPanel(m_int_Breite_Detail_Fixed,m_int_Hoehe_Label + 4);
    	JLabel emptyLabel = createLabel("",m_int_Breite_Detail_Fixed,m_int_Hoehe_Label,SwingConstants.CENTER,null);
    	emptyPanel.add(emptyLabel);

        Vector fixedColumn = new Vector();
        fixedColumn.add(emptyPanel);
        fixedColumn.add(fixedPanel);

        // Die 1. Spalte zu der Tabelle hinzufügen
        m_TableModel_Details.addColumn(" ",fixedColumn);
        m_Table_Details.setDefaultRenderer(JPanel.class, m_renderer );
    	m_Table_Details.setRowHeight(1,m_int_Hoehe_DetailPanels);
    	m_Table_Details.setRowHeight(0,m_int_Hoehe_Label + 5);
    	TableColumn colFixed = m_Table_Details.getColumnModel().getColumn(0);
    	colFixed.setPreferredWidth(m_int_Breite_Detail_Fixed);



    	Border kante = BorderFactory.createBevelBorder(BevelBorder.RAISED,hellblau,dunkelblau);
        /*****************
         *Erstellen der Buttons
         *****************/
        m_Button_load_file = new JButton(hoV.getLanguageString("btLoadFile"));
        m_Button_load_file.setToolTipText(hoV.getLanguageString("ttLoadFile"));
        m_Button_load_file.addActionListener(this);

        m_Button_delete_db = new JButton(hoV.getLanguageString("btDeleteDB"));
        m_Button_delete_db.setToolTipText(hoV.getLanguageString("ttDeleteDB"));
        m_Button_delete_db.addActionListener(this);

        m_Button_delete_file = new JButton(hoV.getLanguageString("btDeleteFile"));
        m_Button_delete_file.setToolTipText(hoV.getLanguageString("ttDeleteFile"));
        m_Button_delete_file.addActionListener(this);

        m_Button_ImportList = new JButton(hoV.getLanguageString("btImport"));
        m_Button_ImportList.setToolTipText(hoV.getLanguageString("ttImport"));
        m_Button_ImportList.addActionListener(this);

        m_Button_Select_All = new JButton(hoV.getLanguageString("btSelect"));
        m_Button_Select_All.setToolTipText(hoV.getLanguageString("ttSelect"));
        m_Button_Select_All.addActionListener(this);

        m_Button_reset = new JButton(hoV.getLanguageString("ls.button.reset"));
        m_Button_reset.setToolTipText(hoV.getLanguageString("ttReset"));
        m_Button_reset.addActionListener(this);

        m_Button_Delete_Row = new JButton(hoV.getLanguageString("btRemove"));
        m_Button_Delete_Row.setToolTipText(hoV.getLanguageString("ttRemove"));
        m_Button_Delete_Row.addActionListener(this);

        m_Button_GoTo = new JButton(hoV.getLanguageString("ls.button.apply"));
        m_Button_GoTo.setToolTipText(hoV.getLanguageString("ttok"));
        m_Button_GoTo.setBackground(hellblau);
        m_Button_GoTo.addActionListener(this);

        m_Button_Month_Forward = new JButton();
        //Image tmp_bild_left = HelperWrapper.instance().makeColorTransparent(new ImageIcon("hoplugins/hrfExplorer/pics/arRight.gif").getImage(),gruen);
        //m_Button_Month_Forward.setIcon(new ImageIcon(tmp_bild_left));
        m_Button_Month_Forward.setIcon(ThemeManager.getIcon(HOIconName.TRANSFER_OUT));
        m_Button_Month_Forward.addActionListener(this);

        m_Button_Month_Back = new JButton();
        //Image tmp_bild_right = HelperWrapper.instance().makeColorTransparent(new ImageIcon("hoplugins/hrfExplorer/pics/arLeft.gif").getImage(),gruen);
        //m_Button_Month_Back.setIcon(new ImageIcon(tmp_bild_right));
        m_Button_Month_Back.setIcon(ThemeManager.getIcon(HOIconName.TRANSFER_IN));
        m_Button_Month_Back.addActionListener(this);

        m_Button_ResetImports = new JButton(hoV.getLanguageString("btImports"));
        m_Button_ResetImports.setToolTipText(hoV.getLanguageString("ttImports"));
        m_Button_ResetImports.addActionListener(this);

        /*****************
         * Erstellen der Labels
         *****************/
        m_Label_Monat = new JLabel("");
        m_Label_Monat.setFont(new Font("Verdana",Font.BOLD,10));
        m_Label_Monat.setBackground(gruen);
        m_Label_DetailHeader = new JLabel("");

        /*****************
         *Erstellen der ComboBoxen
         *****************/
        m_CB_year = new JComboBox();

        for(int ii = m_int_firstYearInDB; ii <= m_int_actualYear; ii++)
        {
        	m_CB_year.addItem("" + ii);
        }
        m_CB_year.setSelectedIndex(m_CB_year.getItemCount()-1);
        m_CB_year.setBackground(hellblau);
        m_CB_year.addItemListener(this);

        m_V_months = new Vector();
        setMonate();

        m_CB_month = new JComboBox(m_V_months);
        m_CB_month.addItemListener(this);
        m_CB_month.setSelectedIndex(m_int_selectedMonth);
        m_CB_month.setBackground(hellblau);

        /*****************
         * *******************
         * Erstellen des GUI
         * *******************
         *****************/

        /*****************
         * Erstellen des Hauptpanels
         *****************/

        setLayout(new BorderLayout());

        /*****************
         * Erstellen des Calendar Bereichs
         *****************/
        m_Panel_Calendar_main = new JPanel(new BorderLayout());
        m_Panel_Calendar_main_north = new JPanel(new GridLayout(2,3));

        m_ScrollPane_Calendar = new JScrollPane(m_Table_Calendar);
        m_Panel_Calendar_main_north.add(m_CB_month);
        m_Panel_Calendar_main_north.add(m_CB_year);
        m_Panel_Calendar_main_north.add(m_Button_GoTo);
        m_Panel_Calendar_main_north.add(m_Button_Month_Back);
        m_Panel_Calendar_main_north.add(m_Label_Monat);
        m_Panel_Calendar_main_north.add(m_Button_Month_Forward);

        m_Panel_Calendar_main.add(m_Panel_Calendar_main_north,BorderLayout.NORTH);
        m_Panel_Calendar_main.add(m_ScrollPane_Calendar,BorderLayout.CENTER);

        /*****************
         * Erstellen des FileTable Bereichs
         *****************/
        m_Panel_FileTable_main = new JPanel(new BorderLayout());
        m_Panel_FileTable_main_north = new JPanel(new GridLayout(2,8));

        m_ScrollPane_FileTable = new JScrollPane(m_Table_Filelist);

        m_Panel_FileTable_main_north.add(m_Button_load_file);
        m_Panel_FileTable_main_north.add(m_Button_delete_file);
        m_Panel_FileTable_main_north.add(m_Button_ImportList);
        m_Panel_FileTable_main_north.add(m_Button_reset);
        m_Panel_FileTable_main_north.add(m_Button_delete_db);
        m_Panel_FileTable_main_north.add(m_Button_Delete_Row);
        m_Panel_FileTable_main_north.add(m_Button_Select_All);

        m_Panel_FileTable_main.add(m_Panel_FileTable_main_north,BorderLayout.NORTH);
        m_Panel_FileTable_main.add(m_ScrollPane_FileTable,BorderLayout.CENTER);

        /*****************
         * Erstellen des Detail Bereichs
         *****************/
        m_Panel_Details_main = new JPanel(new BorderLayout());
        m_Panel_Details_north = new JPanel(new BorderLayout());

        m_ScrollPane_Details = new JScrollPane(m_Table_Details);

        m_Panel_Details_main.add(m_Panel_Details_north,BorderLayout.NORTH);
        m_Panel_Details_main.add(m_ScrollPane_Details,BorderLayout.CENTER);
        m_Panel_Details_north.add(m_Label_DetailHeader,BorderLayout.NORTH);

        /*****************
         * Erstellen des Import Bereichs
         *****************/
        m_Panel_Imports_main = new JPanel(new BorderLayout());

        m_ScrollPane_Imports = new JScrollPane(m_Table_Imports);

        m_Panel_Imports_main.add(m_Button_ResetImports,BorderLayout.NORTH);
        m_Panel_Imports_main.add(m_ScrollPane_Imports,BorderLayout.CENTER);

        /*****************
         * Erstellen der SplitPanes
         ******************/
        m_SplitPane_top_left = new JSplitPane(JSplitPane.VERTICAL_SPLIT,m_Panel_Calendar_main,m_Panel_Imports_main);
        m_SplitPane_top = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,m_SplitPane_top_left,m_Panel_FileTable_main);
		m_SplitPane_main = new JSplitPane(JSplitPane.VERTICAL_SPLIT,m_SplitPane_top,m_Panel_Details_main);
        m_SplitPane_main.setDividerLocation(350);
        m_SplitPane_top.setDividerLocation(340);
        m_SplitPane_top_left.setDividerLocation(220);

        add(m_SplitPane_main,BorderLayout.CENTER);

        createCalendarTable(m_int_selectedMonth, m_int_selectedYear);
 	}

	private JLabel createLabel(String text, int breite, int hoehe, int position, Color bg)
	{
		JLabel lbl = new JLabel(text,position);
		lbl.setSize(breite, hoehe);
		if(bg != null)
			lbl.setBackground(bg);
		lbl.setOpaque(true);
		return lbl;
	}

	/******************
	 * Schreibt die Monatsnamen in der gewählten Sprache in den Vector m_V_months
	 ******************/
	public void setMonate()
	{
		HOVerwaltung hoV = HOVerwaltung.instance();
		m_V_months.add(hoV.getLanguageString("jan"));
		m_V_months.add(hoV.getLanguageString("feb"));
		m_V_months.add(hoV.getLanguageString("mar"));
		m_V_months.add(hoV.getLanguageString("apr"));
		m_V_months.add(hoV.getLanguageString("may"));
		m_V_months.add(hoV.getLanguageString("jun"));
		m_V_months.add(hoV.getLanguageString("jul"));
		m_V_months.add(hoV.getLanguageString("aug"));
		m_V_months.add(hoV.getLanguageString("sep"));
		m_V_months.add(hoV.getLanguageString("oct"));
		m_V_months.add(hoV.getLanguageString("nov"));
		m_V_months.add(hoV.getLanguageString("dec"));
	}

	/******************
	 * Gibt einen Vector zurück, der alle Monatsnamen enthält
	 * @return Gibt den Vector m_V_months zurück
	 ******************/
	@SuppressWarnings("unchecked")
	public static Vector getMonate()
	{
		return m_V_months;
	}

	/******************
	 * Schreibt die Tagesnamen in der gewählten Sprache in das Array m_Ar_days
	 ******************/
	public void setTage()
	{
		HOVerwaltung hoV = HOVerwaltung.instance();
		m_Ar_days[0] = hoV.getLanguageString("mon");
		m_Ar_days[1] = hoV.getLanguageString("die");
		m_Ar_days[2] = hoV.getLanguageString("wed");
		m_Ar_days[3] = hoV.getLanguageString("don");
		m_Ar_days[4] = hoV.getLanguageString("fre");
		m_Ar_days[5] = hoV.getLanguageString("sam");
		m_Ar_days[6] = hoV.getLanguageString("son");
	}

	/******************
	 * Gibt ein Array zurück, das alle Tage als Namen enthält
	 * @return Gibt das Array m_Ar_days zurück
	 ******************/
	public static String[] getTage()
	{
		return m_Ar_days;
	}

	/******************
	 * Erstellt die Calendar-Tabelle
	 * @param monat Der Monat für den die Tabelle erstellt wird
	 * @param jahr Das Jahr für das die Tabelle erstellt wird
     ******************/
	@SuppressWarnings("unchecked")
	public void createCalendarTable(int monat, int jahr)
	{
		String monat_Start;
		String jahr_Start;
		String monat_Ende;
		String jahr_Ende;

		//Monat um 1 heraufzählen, weil Monatszählung bei 0 anfängt :-(
		monat++;
		if(monat < 10)
		{
			monat_Start = "0" + monat;
			jahr_Start = "" + jahr;
		}
		else
		{
			monat_Start = "" + monat;
			jahr_Start = "" + jahr;
		}
		if(monat + 1 < 10)
		{
			monat_Ende = "0" + (monat + 1);
			jahr_Ende = "" + jahr;
		}
		else if(monat +1 > 12)
		{
			monat_Ende = "01";
			jahr_Ende = "" + (jahr + 1);
		}
		else
		{
			monat_Ende = "" + (monat + 1);
			jahr_Ende = "" + jahr;
		}

		// Holen der HRF-ID und des Datums der Einträge, die in dem gewählten Monat liegen
		doSelect("SELECT DATUM,HRF_ID FROM HRF where DATUM between '" + jahr_Start + "-" + monat_Start + "-01' and '" + jahr_Ende + "-" + monat_Ende + "-01'");
		//********************************************************************************************
		//Leeren der Hashtables
		m_HashTable_DayInDB.clear();
		m_HashTable_isEvent.clear();

		/*Erstmal die Vorarbeiten:
		 * alle benötigten Werte ermitteln,
		 * Label erstellen
		 */
		int akt_Monat = m_int_selectedMonth;
		int back_Monat;
		int fw_Monat;
		int anzRows = m_TableModel_Calendar.getRowCount();
		int anzCols = m_TableModel_Calendar.getColumnCount();
		if(m_int_selectedMonth - 1 < 0)
		{
			back_Monat = 11;
		}
		else
		{
			back_Monat = m_int_selectedMonth - 1;
		}
		if(m_int_selectedMonth + 1 > 11)
		{
			fw_Monat = 0;
		}
		else
		{
			fw_Monat = m_int_selectedMonth + 1;
		}
		m_Label_Monat.setText(m_V_months.get(m_int_selectedMonth).toString() + " " + m_int_selectedYear);
		m_Label_Monat.setHorizontalAlignment(JLabel.CENTER);
		m_Label_Monat.setBackground(gruen);

		GregorianCalendar gc = new GregorianCalendar(m_int_selectedYear,m_int_selectedMonth,1);
		int last_day = gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

		/*
		 * Alle Werte aus dem SELECT lesen und in die Hashtable schreiben
		 */
		try
		{
			while(m_queryResult.next())
			{
				m_queryResult.getObject(1);

				if( m_queryResult.wasNull())
				{
					//debugWindow.append("Select war null");
				}
				else
				{
					Timestamp datum = m_queryResult.getTimestamp("DATUM");
					int id = m_queryResult.getInt("HRF_ID");
					int tag = Integer.parseInt((datum.toString()).substring(8,10));
					String strDatum = datum.toString().substring(0,19);
					if(m_HashTable_DayInDB.containsKey(new Integer(tag)))
					{
						Hashtable tmp = (Hashtable)m_HashTable_DayInDB.get(new Integer(tag));
						tmp.put(new Integer(id),strDatum);
						m_HashTable_DayInDB.put(new Integer(tag),tmp);
					}
					else
					{
						Hashtable tmp = new Hashtable();
						tmp.put(new Integer(id),strDatum);
						m_HashTable_DayInDB.put(new Integer(tag),tmp);
					}
				}
			}
		}
		catch(SQLException sexc)
		{
			//debugWindow.append("" + sexc);
		}

		/*
		 * Alle Spiele und Trainings für den gewählten Monat herausfinden
		 */
		GregorianCalendar cStart = new GregorianCalendar(Integer.parseInt(jahr_Start),Integer.parseInt(monat_Start)-1,1);
		Timestamp tsStart = new Timestamp(cStart.getTimeInMillis());
		GregorianCalendar cStop = new GregorianCalendar(Integer.parseInt(jahr_Ende),Integer.parseInt(monat_Ende)-1,1);
		Timestamp tsStop = new Timestamp(cStop.getTimeInMillis());
		doSelect("SELECT SPIELDATUM,MATCHID FROM MATCHDETAILS WHERE SPIELDATUM between '" + tsStart + "' AND '" + tsStop + "' AND ( GASTID = '" + m_TeamID + "' OR HEIMID = '" + m_TeamID + "' )");

		try
		{
			while(m_queryResult.next())
			{
				m_queryResult.getObject(1);

				if( m_queryResult.wasNull())
				{
					//debugWindow.append("Select war null");
				}
				else
				{
					Timestamp datum = m_queryResult.getTimestamp("SPIELDATUM");
					int match_id = m_queryResult.getInt("MATCHID");
					int tag = Integer.parseInt((datum.toString()).substring(8,10));
					String strDatum = datum.toString().substring(0,19);
					int matchTyp = ((Integer)m_HashTable_MatchTyp.get(new Integer(match_id))).intValue();

					if(matchTyp == MatchType.LEAGUE.getId())
					{
						m_HashTable_isEvent.put(new Integer(tag),"L");
					}
					else if(matchTyp == MatchType.CUP.getId())
					{
						m_HashTable_isEvent.put(new Integer(tag),"P");
					}
					else if(matchTyp == MatchType.FRIENDLYNORMAL.getId()
							|| matchTyp == MatchType.FRIENDLYCUPRULES.getId())
					{
						m_HashTable_isEvent.put(new Integer(tag),"F");
					}
					else if(matchTyp == MatchType.INTFRIENDLYNORMAL.getId()
							|| matchTyp == MatchType.INTFRIENDLYCUPRULES.getId())
					{
						m_HashTable_isEvent.put(new Integer(tag),"I");
					}
					else if(matchTyp == MatchType.QUALIFICATION.getId())
					{
						m_HashTable_isEvent.put(new Integer(tag),"Q");
					}
					m_kurzInfo = null;
				}
			}
		}
		catch(SQLException sexc)
		{
			//debugWindow.append("" + sexc);
		}
		/*
		 * So, jetzt beginnt der Aufbau der neuen Tabelle...
		 */
		m_TableModel_Calendar.removeAllRows();


		int tag_der_woche;
		int actual_day = 1;
		int tmp_kw = 0;

		while(actual_day <= last_day)
		{
			Vector tmp = new Vector();
			GregorianCalendar gc_tmp = new GregorianCalendar(m_int_selectedYear,m_int_selectedMonth,actual_day);
			tmp_kw = gc_tmp.get(GregorianCalendar.WEEK_OF_YEAR);
			tmp.add(" " + tmp_kw);
			for(int ii = 1; ii <= 7; ii++)
			{
				if((gc_tmp.get(GregorianCalendar.DAY_OF_WEEK) -1) > 0)
				{
					tag_der_woche = gc_tmp.get(GregorianCalendar.DAY_OF_WEEK) - 1;
				}
				else
				{
					tag_der_woche = 7;
				}

				if(ii < tag_der_woche && actual_day < 7)
				{
					tmp.add(" ");
				}
				else if(actual_day <= last_day)
				{
					tmp.add("" + actual_day);
					actual_day++;
				}
				else
				{
					tmp.add(" ");
				}
			}
			m_TableModel_Calendar.addRow(tmp);
		}
		m_TableModel_Calendar.fireTableDataChanged();
		m_ScrollPane_Calendar.doLayout();
	}

	/******************
	 * Erstellt die Detail-Tabelle
	 * @param hashwert Dieser Parameter ist der Key, mit dem das HrfDetails-Object aus der Hashtable m_HashTable_Details geholt wird
     ******************/
	@SuppressWarnings("unchecked")
	public void createDetailTable(String hashwert)
	{
		HrfDetails selectedObject = (HrfDetails)m_HashTable_Details.get((String)hashwert);
    	if(m_HashTable_Details_ColHeader.containsKey(selectedObject.getStr_Datum()) == false)
    	{
    		m_HashTable_Details_ColHeader.put(selectedObject.getStr_Datum(),selectedObject);
			//erstellen der Detailtabelle
	    	//Label der überschrift füllen
	    	m_Label_DetailHeader.setText(selectedObject.getTeamName() + " (" + selectedObject.getTeamID() + ")");
	    	// Panel für die Detail-ScrollPane erstellen
//	    	 Array für die nicht fixen Details
	    	String[] objectDetails = {
	    			" " + selectedObject.getLiga(),
	    			" " + selectedObject.getSaison() + " / " + selectedObject.getSpieltag(),
	    			" " + selectedObject.getPunkte() + " / " + selectedObject.getToreFuer() + ":" + selectedObject.getToreGegen(),
	    			" " + selectedObject.getPlatz(),
	    			" " + selectedObject.getTrArt(),
	    			" " + selectedObject.getTrInt() + "%",
	    			" " + selectedObject.getAnzCoTrainer(),
	    			" " + selectedObject.getSelbstvertrauen(),
	    			" " + selectedObject.getAnzSpieler(),
	    			" " + selectedObject.getStimmung(),
	    			" " + selectedObject.getStr_DatumVorher(),
	    			" " + selectedObject.getStr_DatumDanach()
	    	};
	    	HrfPanel teamDetails = new HrfPanel(m_int_Breite_Detail_Var,m_int_Hoehe_DetailPanels);
	    	teamDetails.setLayout(new GridLayout(objectDetails.length,1));
	    	String columnHeader = selectedObject.getStr_Datum();

	    	// Labels für das DetailPanel erstellen und einfügen
	    	for(int ii = 0; ii < objectDetails.length; ii++)
	    	{
	    		if(ii == 0 || ii%2 == 0)
	        	{
	        		m_LineColor = hellblau;
	        	}
	        	else
	        	{
	        		m_LineColor = dunkelblau;
	        	}
	    		teamDetails.add(createLabel(objectDetails[ii],m_int_Breite_Detail_Var,m_int_Hoehe_Label,JLabel.LEFT,m_LineColor));
	    	}
	    	// Entfernen-Panel und -Label erstellen
	    	HrfPanel entfernen = new HrfPanel(m_int_Breite_Detail_Var,m_int_Hoehe_Label + 5,rot);
	    	entfernen.setLayout(new GridLayout(1,1));
	    	JLabel remove = createLabel(HOVerwaltung.instance().getLanguageString("ls.button.remove"),m_int_Breite_Detail_Var,m_int_Hoehe_Label,JLabel.CENTER,rot);
	    	remove.getInsets();
	    	entfernen.add(remove);

	    	//Vector für die Objekte in der Detailtabelle
	    	Vector details = new Vector();
	    	details.add(entfernen);
	    	details.add(teamDetails);

	    	m_TableModel_Details.addColumn(columnHeader,details);
	    	m_HashTable_Columns.put(columnHeader,details);

	    	int anzCols = m_TableModel_Details.getColumnCount();
	    	setDetailTableSize(anzCols);
    	}
	}

	/******************
	 * Entfernt ein Panel aus der Detailtabelle und baut diese anschliessend neu auf
     * @param colKey Name der Spalte, die entfernt werden soll
	 ******************/
	@SuppressWarnings("unchecked")
	public void rebuildDetailTable(String colKey)
	{
		int anzCols = 1;
		m_TableModel_Details.setColumnCount(1);
		m_Table_Details.revalidate();
		if(colKey.equals("alle"))
		{
			m_HashTable_Columns.clear();
		}
		else
		{
			m_HashTable_Columns.remove((String)colKey);

			Enumeration enu = m_HashTable_Columns.keys();
			Set keys = m_HashTable_Columns.keySet();
			int menge = keys.size();
			Object[] schluessel = keys.toArray();
			int counter = 0;
			while(enu.hasMoreElements())
			{
				enu.nextElement();
				String keyString = schluessel[counter].toString();
				Vector details = (Vector)m_HashTable_Columns.get((String)keyString);
				m_TableModel_Details.addColumn(keyString,details);
		    	counter ++;
			}
			anzCols = m_TableModel_Details.getColumnCount();
		}
		setDetailTableSize(anzCols);
	}

	/******************
	 * Setzt die Breiten und Höhen für die Detail-Tabelle
	 * @param anzColumns Anzahl der Spalten in der Detailtabelle
	 ******************/
	public void setDetailTableSize(int anzColumns)
	{
		for(int ii = 0; ii < anzColumns; ii++)
    	{
    		TableColumn col = m_Table_Details.getColumnModel().getColumn(ii);
    		int breite;
    		if(ii == 0)
    		{
    			breite = m_int_Breite_Detail_Fixed;
    		}
    		else
    		{
    			breite = m_int_Breite_Detail_Var;
    		}
    		col.setResizable(false);
	    	col.setPreferredWidth( breite );
	    	col.setMinWidth(breite);
    	}
		m_Table_Details.setDefaultRenderer(JPanel.class, m_renderer );
    	m_Table_Details.setRowHeight(1,m_int_Hoehe_DetailPanels);
    	m_Table_Details.setRowHeight(0,m_int_Hoehe_Label + 4);
	}

	/******************
	 * Liefert ein boolean Ergebnis, ob für einen Tag ein HRF-File in der DB ist und wertet dabei die Hashtable aus
     * @param tag Der Tag, der geprüft werden soll
     * @return Ein boolean-Wert, der anzeigt, ob für den übergebenen Tag ein DB-Eintrag existiert
	 ******************/
	public static boolean hrfForDay(int tag)
	{
		boolean return_value = false;
		if(m_HashTable_DayInDB.containsKey(new Integer(tag)))
		{
			return_value = true;
		}
		return return_value;
	}

	/******************
	 * Liefert ein boolean Ergebnis, ob an einem Tag ein Spiel oder Training stattgefunden hat
     * @param tag Der Tag, der geprüft werden soll
     * @return Ein boolean-Wert, der anzeigt, ob an dem übergebenen Tag ein SpecialEvent (z.B. ein Spiel) stattgefunden hat.
	 ******************/
	public static boolean isSpecialEvent(int tag)
	{
		boolean return_value = false;
		if(m_HashTable_isEvent.containsKey(new Integer(tag)))
		{
			return_value = true;
		}
		return return_value;
	}

	/******************
	 * Liefert den Wert des SpecialEvents
     * @param tag Der Tag, für den das SpecialEvent geliefert werden soll
     * @return Liefert die Art des SpecialEvents für den übergebenen Tag als String.
	 ******************/
	public static String getSpecialEvent(int tag)
	{
		return m_HashTable_isEvent.get(new Integer(tag)).toString();
	}

	/******************
	 * Liefert den Wert des SpecialEvents
     * @param tag Der Tag, für den das SpecialEvent geliefert werden soll
     * @return Liefert die Art des SpecialEvents für den übergebenen Tag als String.
	 ******************/
	public static String getNameForEvent(String event)
	{
		String eventName = (m_HashTable_EventInfo.get(event)).toString();
		return eventName;
	}

	/******************
	 * Liefert die HRF-ID, die für den übergebenen Tag in der DB steht
     * @param tag Der Tag, für den die ID geholt werden soll
	 ******************/
	/*public int getHrfID(int tag)
	{
		int return_value = 0;
		if(m_HashTable_DayInDB.containsKey(new Integer(tag)))
		{
			Object id = m_HashTable_DayInDB.get(new Integer(tag));
			return_value = new Integer(tag).intValue();
		}
		return return_value;
	}*/


	/******************
	 * Prüft, ob ein File für ein Datum eines Monats geladen ist
	 * @param tag Tag des Monats der geprüft werden soll
	 * @return Ein boolean-Wert, der anzeigt, ob eine gewählte Datei bereits in der Datenbank existiert.
	 ******************/
	public static boolean hrfAsFile(String tag)
	{
		String monat = "" + (m_int_selectedMonth + 1);
		String jahr = "" + m_int_selectedYear;
		boolean inDB = false;
		if(m_int_selectedMonth < 11 || Integer.parseInt(tag) < 10)
		{
			monat = "0" + monat;
		}
		if(Integer.parseInt(tag) < 10)
		{
			tag = "0" + tag;
		}
		String datum = jahr + "-" + monat + "-" + tag;
		if(m_HashTable_DatumKey.containsKey((String)datum))
		{
			inDB = true;
		}
		return inDB;
	}

	// *********** Ende allgemeiner Methoden *********************

	// *********** Beginn der Listener-Methoden *******************
	/*****************
     * Methode für die Behandlung von Mausklicks auf einen Button
     * @param e Wertet den Klick auf Buttons aus
     *****************/
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e)
    {
		/*
		 * Button LoadFile
		 */
		if(e.getSource().equals(m_Button_load_file))
		{
			m_FileChooser_chooser = new JFileChooser();
			m_FileChooser_chooser.setMultiSelectionEnabled(true);
			m_FileChooser_chooser.setFileFilter(new ExampleFileFilter("hrf"));
			m_FileChooser_chooser.setCurrentDirectory(new File(m_Str_hrfPfad));

			int state = m_FileChooser_chooser.showOpenDialog(null);
			m_files = m_FileChooser_chooser.getSelectedFiles();

			if(m_files != null && state == JFileChooser.APPROVE_OPTION)
			{
				int anzFiles = m_files.length;
				String tmp_Datum = "";
				String tmp_Pfad = "";
				for(int ii = 0; ii < anzFiles; ii++)
				{
					HrfFileDetails tmp = new HrfFileDetails(m_files[ii].getPath());
					tmp_Datum = tmp.getStr_Datum().substring(0,10);
					tmp_Pfad = tmp.getPfad();
					if(m_V_Filelist_Keys.contains(tmp_Pfad) == false)
					{
						m_TableModel_Filelist.addRow(tmp.getDatenVector());
						m_HashTable_Details.put(tmp_Pfad,tmp);
						m_HashTable_DatumKey.put(tmp_Datum,tmp_Pfad);
						m_V_Filelist_Keys.add(tmp_Pfad);
						if(Integer.parseInt(tmp_Datum.substring(5,7)) == m_int_selectedMonth + 1)
						{
							createCalendarTable(m_int_selectedMonth , m_int_selectedYear);
						}
						m_Table_Filelist.revalidate();
						m_Table_Filelist.repaint();
					}
				}
			}
		}
		/*
		 * Button ImportList
		 */
		else if(e.getSource().equals(m_Button_ImportList))
		{
			int anzRows = m_TableModel_Filelist.getRowCount();
			if(anzRows > 0)
			{
				for(int ii = 0;ii < anzRows; ii++)
				{
					Vector tmpV = (Vector)(m_TableModel_Filelist.getDataVector()).elementAt(ii);
					String dateiPfad = tmpV.elementAt(tmpV.size()-1).toString();
					//String dateiPfad = m_TableModel_Filelist.getValueAt(ii,m_TableModel_Filelist.getColumnCount()).toString();
					if(((Boolean)m_TableModel_Filelist.getValueAt(ii,0)).booleanValue() == true
							&& m_HashTable_DatumKey.containsValue(dateiPfad)
							&& m_HashTable_Import.containsKey((String)dateiPfad) == false)
					{
						Vector tmp = new Vector();
						tmp.add(dateiPfad);
						//tmp.add(m_TableModel_Filelist.getValueAt(ii,m_TableModel_Filelist.getColumnCount()).toString());
						m_HashTable_Import.put(dateiPfad,Boolean.FALSE);
						m_TableModel_Imports.addRow(tmp);
					}
				}
				m_Table_Imports.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				m_Table_Imports.revalidate();
				m_Table_Imports.repaint();
			}
		}
		/*
		 * Button DeleteRow
		 */
		else if(e.getSource().equals(m_Button_Delete_Row))
		{
			int anzRows = m_TableModel_Filelist.getRowCount();

			for(int ii = 0; ii < anzRows; ii++)
			{
				if(((Boolean)m_TableModel_Filelist.getValueAt(ii,0)).booleanValue() == true)
				{
					Vector tmpV = (Vector)(m_TableModel_Filelist.getDataVector()).elementAt(ii);
					String delete_key = tmpV.elementAt(tmpV.size()-1).toString();
					//String delete_key = "" + m_TableModel_Filelist.getValueAt(ii,m_TableModel_Filelist.getDataVector().capacity());
					String rem_DatumKey = ((HrfDetails)m_HashTable_Details.get(delete_key)).getStr_Datum();
					String rem_DatumKeyJahr = rem_DatumKey.substring(0,10);
					m_HashTable_Details.remove(delete_key);
					m_HashTable_DatumKey.remove(rem_DatumKeyJahr);
					m_HashTable_Details_ColHeader.remove(rem_DatumKey);
					m_V_Filelist_Keys.remove(delete_key);
					//m_V_Filelist_Keys.remove(m_TableModel_Filelist.getValueAt(ii,m_TableModel_Filelist.getDataVector().capacity()));
					rebuildDetailTable(rem_DatumKey);
					m_TableModel_Filelist.removeRow(ii);
					ii--;
					anzRows = m_TableModel_Filelist.getRowCount();
				}
			}
			createCalendarTable(m_int_selectedMonth , m_int_selectedYear);
		}
		/*
		 * Button DeleteFile
		 */
		else if(e.getSource().equals(m_Button_delete_file))
		{
			//Ein oder mehrere ausgewählte Files werden physikalisch von der Festplatte entfernt
			int anzRows = m_TableModel_Filelist.getRowCount();
			//int anzCols = m_TableModel_Filelist.getDataVector().capacity();

			for(int i = 0; i < anzRows; i++)
        	{
        		if(((Boolean)m_TableModel_Filelist.getValueAt(i,0)).booleanValue() == true && m_TableModel_Filelist.getValueAt(i,1).equals("---") == false)
        		{
        			Vector tmpV = (Vector)(m_TableModel_Filelist.getDataVector()).elementAt(i);
        			String deletePath = tmpV.elementAt(tmpV.size()-1).toString();
        			//String deletePath = "" + m_TableModel_Filelist.getValueAt(i,m_TableModel_Filelist.getDataVector().capacity());
        			String rem_DatumKey = ((HrfFileDetails)m_HashTable_Details.get(deletePath)).getStr_Datum().substring(0,10);

        			File tmp_File = new File(deletePath);

        			int option = JOptionPane.showConfirmDialog(null,HOVerwaltung.instance().getLanguageString("deletefile") + "\n" + deletePath,HOVerwaltung.instance().getLanguageString("deletefile"),JOptionPane.YES_NO_OPTION);
        			if(option == 0)
        			{
        				//Löschen der Datei von der Platte
        				tmp_File.delete();
        				//Löschen der Datei aus der Hashtable Details
        				m_HashTable_Details.remove(deletePath);
        				m_HashTable_DatumKey.remove(rem_DatumKey);
        				m_TableModel_Filelist.removeRow(i);
        				anzRows = m_TableModel_Filelist.getRowCount();
        				i--;
        			}
        		}
        	}
			createCalendarTable(m_int_selectedMonth , m_int_selectedYear);
		}
		/*
		 * Button Delete_db
		 */
		else if(e.getSource().equals(m_Button_delete_db))
		{
			// Löschen aller Informationen aus der DB die zu den ausgewählten Files gehören
			int anzRows = m_TableModel_Filelist.getRowCount();
			//int anzCols = m_TableModel_Filelist.getDataVector().capacity();
			for(int i = 0; i < anzRows; i++)
        	{
        		if(((Boolean)m_TableModel_Filelist.getValueAt(i,0)).booleanValue() == true
        				&& m_TableModel_Filelist.getValueAt(i,1).equals("---") == true)
        		{
        			//Holen der HRF_ID
        			Vector tmpV = (Vector)(m_TableModel_Filelist.getDataVector()).elementAt(i);
        			int deleteHRF_ID = ((Integer)tmpV.elementAt(tmpV.size()-1)).intValue();
        			//int deleteHRF_ID = ((Integer)(m_TableModel_Filelist.getValueAt(i,m_TableModel_Filelist.getDataVector().capacity()))).intValue();

        			//Tabelle und die Zählwerte anpassen
        			m_TableModel_Filelist.removeRow(i);
					m_V_Filelist_Keys.remove(new Integer(deleteHRF_ID));
        			anzRows = m_TableModel_Filelist.getRowCount();
   					i--;
   					DBManager.instance().deleteHRF(deleteHRF_ID);
        		}
        	}
			RefreshManager.instance().doReInit();
		}
		/*
		 * Button Select_All
		 */
		else if(e.getSource().equals(m_Button_Select_All))
		{
			int anzRows = m_TableModel_Filelist.getRowCount();

			for(int ii = 0; ii < anzRows; ii++)
			{
				Vector tmpV = (Vector)(m_TableModel_Filelist.getDataVector()).elementAt(ii);
				String hashKey = tmpV.elementAt(tmpV.size()-1).toString();
				m_TableModel_Filelist.setValueAt(new Boolean(true),ii,0);
				createDetailTable(hashKey);
			}
		}
		/*
		 * Button Month_Back
		 */
		else if(e.getSource().equals(m_Button_Month_Back))
		{
			if((m_int_selectedMonth - 1) < 0)
			{
				m_int_selectedMonth = 11;
				m_int_selectedYear = m_int_selectedYear - 1;
			}
			else
			{
				m_int_selectedMonth = m_int_selectedMonth - 1;
			}
			createCalendarTable(m_int_selectedMonth , m_int_selectedYear);
		}
		/*
		 * Button Month_Forward
		 */
		else if(e.getSource().equals(m_Button_Month_Forward))
		{
			if((m_int_selectedMonth + 1) > 11)
			{
				m_int_selectedMonth = 0;
				m_int_selectedYear = m_int_selectedYear + 1;
			}
			else
			{
				m_int_selectedMonth = m_int_selectedMonth + 1;
			}
			createCalendarTable(m_int_selectedMonth, m_int_selectedYear);
		}
		/*
		 * Button GoTo
		 */
		else if(e.getSource().equals(m_Button_GoTo))
		{
			m_int_selectedMonth = m_CB_month.getSelectedIndex();
			m_int_selectedYear = Integer.parseInt((String)m_CB_year.getSelectedItem());
			createCalendarTable(m_CB_month.getSelectedIndex(),Integer.parseInt((String)m_CB_year.getSelectedItem()));
		}
		/*
		 * Button Reset
		 */
		else if(e.getSource().equals(m_Button_reset))
		{
			m_TableModel_Filelist.removeAllRows();
			m_HashTable_Details.clear();
			m_HashTable_Details_ColHeader.clear();
			m_HashTable_DatumKey.clear();
			m_V_Filelist_Keys.clear();
			rebuildDetailTable("alle");
		}
		else if(e.getSource().equals(m_Button_ResetImports))
		{
			m_HashTable_Import.clear();
			m_TableModel_Imports.removeAllRows();
			m_Table_Imports.revalidate();
			m_Table_Imports.repaint();
		}

    }

	/*****************
     * Methode für die Behandlung von änderungen einer JComboBox
     * @param ie Das Event einer ComboBox
     *****************/

	public void itemStateChanged(ItemEvent ie)
	{
		if(ie.getSource().equals(m_CB_year))
		{
			m_int_selectedYear = Integer.parseInt((String)m_CB_year.getSelectedItem());
		}
		else if(ie.getSource().equals(m_CB_month))
		{
			String monat = "";
			int sel_monat = m_CB_month.getSelectedIndex();
			if(sel_monat < 10)
			{
				monat += "0";
			}
			m_int_selectedMonth = sel_monat;
		}
	}
//	 *********** Ende der Listener-Methoden *********************
//	 *************************************************************

//	*************************************************************
//	********** Beginn Mouse Events ******************************
    @SuppressWarnings("unchecked")
	public void mouseClicked(MouseEvent e)
	{
    	if(e.getSource().equals(m_Table_Calendar))
    	{
			int zeile = m_Table_Calendar.getSelectedRow();
			int spalte = m_Table_Calendar.getSelectedColumn();
			int tag = 0;
			int id = 0;
			Vector TagesIDs = new Vector();
			if (spalte != 0) {
				Object tmpObj = m_TableModel_Calendar.getValueAt(zeile, spalte);
				if (!tmpObj.toString().equals("")) {
					tag = Integer.parseInt(tmpObj.toString());
					// id =((Integer)m_HashTable_DayInDB.get(new
					// Integer(tag))).intValue();
					Hashtable tmp = (Hashtable) m_HashTable_DayInDB.get(new Integer(tag));
					Enumeration enu = (tmp != null ? tmp.keys() : null);
					while (enu != null && enu.hasMoreElements()) {
						TagesIDs.add(enu.nextElement());
					}
				}
			}
    		for(int ii = 0; ii < TagesIDs.size(); ii++)
    		{
    			id = ((Integer)TagesIDs.elementAt(ii)).intValue();
	    		if(id != 0)
	    		{
	    			//doSelect("SELECT NAME,DATUM,LIGANAME,PUNKTE,TOREFUER,TOREGEGEN,PLATZ,TEAMID,TEAMNAME,SPIELTAG,SAISON,TRAININGSINTENSITAET,TRAININGSART,ISTIMMUNG,ISELBSTVERTRAUEN,COTRAINER,TWTRAINER,FANS,HRF_ID,(SELECT COUNT(*) FROM SPIELER WHERE HRF_ID = '" + id + "') AS \"ANZAHL\" FROM HRF a, LIGA b, BASICS c, TEAM d, VEREIN e WHERE a.HRF_ID = '" + id + "' AND b.HRF_ID=a.HRF_ID AND c.HRF_ID=a.HRF_ID AND d.HRF_ID=a.HRF_ID AND e.HRF_ID=a.HRF_ID");
		    		HrfDbDetails dbDetail = new HrfDbDetails(id);
		    		if(m_HashTable_Details.containsKey("" + dbDetail.getHrf_ID()) == false)
		    		{
		    			m_TableModel_Filelist.addRow(dbDetail.getDatenVector());
		    			m_HashTable_Details.put("" + dbDetail.getHrf_ID(),dbDetail);
		    			m_V_Filelist_Keys.add(new Integer(id));
		    			m_Table_Filelist.revalidate();
		    			m_Table_Filelist.repaint();
		    		}
	    		}
    		}
    	}

    	else if(e.getSource().equals(m_Table_Filelist))
    	{
	    	//Gewählte Zeile ermitteln
	    	int rowNr = m_Table_Filelist.getSelectedRow();
	    	// Key für die HashTable ermitteln
	    	Vector tmpV = (Vector)(m_TableModel_Filelist.getDataVector()).elementAt(rowNr);
			String hashKey = tmpV.elementAt(tmpV.size()-1).toString();
	    	// Objekt aus der Hashtable holen
	    	createDetailTable(hashKey);
    	}
    	else if(e.getSource().equals(m_Table_Details))
    	{
    		int selCol = m_Table_Details.getSelectedColumn();
    		if(m_Table_Details.getSelectedRow() == 0 || selCol > 0)
    		{
    			String hashKey = m_Table_Details.getColumnModel().getColumn(selCol).getHeaderValue().toString();
    			m_HashTable_Details_ColHeader.remove(hashKey);
    			rebuildDetailTable(hashKey);
    		}
    	}
	}
	public void mouseEntered(MouseEvent e)
	{

	}
    public void mouseExited(MouseEvent e)
	{

	}
    public void mousePressed(MouseEvent e)
	{

	}
    public void mouseReleased(MouseEvent e)
	{

	}
//  ********** Ende Mouse Events ********************
//  *************************************************
//  ********** Beginn Table Events ******************
    public void columnAdded(TableColumnModelEvent e)
	{

	}
    public void columnMarginChanged(ChangeEvent e)
	{

	}
    public void columnMoved(TableColumnModelEvent e)
	{
    	if(m_Table_Details.getColumnModel().getColumnIndex(" ") != 0)
    	{
    		m_Table_Details.getColumnModel().moveColumn(m_Table_Details.getColumnModel().getColumnIndex(" "),0);
    	}
	}
    public void columnRemoved(TableColumnModelEvent e)
	{

	}
    public void columnSelectionChanged(ListSelectionEvent e)
	{

	}
//  ********** Ende Table Events ******************

    //******** Start der Methoden für IOfficialPlugin



//  ******** Start der Methoden für IOfficialPlugin
//	 *********** Beginn der Datenbank-Methoden *******************
	/*****************
	 * Führt ein query gegen die DB aus
	 * @param query
	 */
	public void doSelect(String query)
	{
		try
		{
			m_queryResult = DBManager.instance().getAdapter().executeQuery(query);
		}
		catch(Exception e)
		{
			//debugWindow.append("FEHLER");
		}
	}
//	 *********** Ende der Datenbank-Methoden **********************
//	 **************************************************************
	//********** Beginn Für HO notwendige Methoden ****************
	/**
	 * Führt ein refresh für das Plugin durch
	 */
	@SuppressWarnings("unchecked")
	public void refresh()
    {
		doSelect("SELECT COUNT(*) FROM HRF");
		try
		{
			while(m_queryResult.next())
			{
				m_int_anz_DBEintraege = m_queryResult.getInt(1);
			}
		}
		catch(SQLException sexc)
		{
			//debugWindow.append("" + sexc);
		}
		createCalendarTable(m_int_selectedMonth , m_int_selectedYear);

		// Leeren der Hashtables, da
		m_HashTable_Details.clear();
		m_HashTable_DatumKey.clear();
		m_TableModel_Filelist.removeAllRows();

		Vector tmp_Pfade = new Vector();
		Vector tmp_IDs = new Vector();
		//debugWindow.append("Anzahl Keys in m_V_Filelist_Keys: " + m_V_Filelist_Keys.size());
		for(int ii = 0; ii < m_V_Filelist_Keys.size(); ii++)
		{
			//debugWindow.append("Nummer: " + ii);
			//debugWindow.append("Wert: " + m_V_Filelist_Keys.elementAt(ii));
			if(m_V_Filelist_Keys.elementAt(ii).getClass().equals(Integer.class))
			{
				int tmp_id = Integer.parseInt(m_V_Filelist_Keys.elementAt(ii).toString());
				HrfDbDetails dbDetail = new HrfDbDetails(tmp_id);
	    		if(m_HashTable_Details.containsKey("" + dbDetail.getHrf_ID()) == false)
	    		{
	    			m_TableModel_Filelist.addRow(dbDetail.getDatenVector());
	    			m_HashTable_Details.put("" + dbDetail.getHrf_ID(),dbDetail);
	    			m_Table_Filelist.revalidate();
	    			m_Table_Filelist.repaint();
	    		}
			}
			else
			{
				//debugWindow.append("Wert im else: " + m_V_Filelist_Keys.elementAt(ii));
				//HrfFileDetails tmp = new HrfFileDetails(m_files[ii].getPath(),m_clModel);
				HrfFileDetails tmp = new HrfFileDetails(m_V_Filelist_Keys.elementAt(ii).toString());
				String tmp_Datum = tmp.getStr_Datum().substring(0,10);
				String tmp_Pfad = tmp.getPfad();
				m_TableModel_Filelist.addRow(tmp.getDatenVector());
				m_HashTable_Details.put(tmp_Pfad,tmp);
				m_HashTable_DatumKey.put(tmp_Datum,tmp_Pfad);
			}
		}
    }

//	********** Ende Für HO notwendige Methoden ******************
	//***********************************************************
	//********** Beginn Debugging Methoden **********************
	public static void appendText(String test)
	{
		//debugWindow.append(test);
	}
//	********** Ende Debugging Methoden **************************
}
