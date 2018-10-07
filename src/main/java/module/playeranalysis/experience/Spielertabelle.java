package module.playeranalysis.experience;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

class Spielertabelle extends JTable {
	private static final long serialVersionUID = 3117625304079832033L;

	public static final String spaltennamen[] = { "Spieler", "ls.player.age",
			"ls.player.experience", "seit", "Bonus", "SchaetzungMin", "SchaetzungWahrscheinlich",
			"SchaetzungMax", "WochenBisAufwertung", "ls.match.matchtype.internationalfriendly_cup",
			"ls.match.matchtype.internationalfriendly_normal",
			"ls.match.matchtype.hattrickmasters",
			"ls.match.matchtype.internationalcompetition_normal",
			"ls.match.matchtype.nationalteamscompetition_cup",
			"ls.match.matchtype.nationalteamscompetition_normal", "ls.match.matchtype.league",
			"ls.match.matchtype.cup", "ls.match.matchtype.qualification",
			"ls.match.matchtype.nationalteamsfriendly", "ls.match.matchtype.friendly_cup",
			"ls.match.matchtype.friendly_normal", "Notizen" };
	public static final int spaltenweite[] = { 120, 60, 60, 60, 40, 40, 80, 40, 80, 40, 40, 40, 40,
			40, 40, 40, 40, 40, 40, 40, 40, 240 };
	private SpielertabellenSpalte cm;
	private SpielerSortierung spielerSortierung[];
	private Vector<Spieler> spieler;
	private boolean sortierrichtung[];
	private int sortierspalte;
	private AbstractTableModel tm;
	private MouseListener mouseListener;
	protected static String experienceViewerVerzeichnis;
	protected static String spracheVerzeichnis;

	Spielertabelle() {
		cm = null;
		spielerSortierung = null;
		spieler = null;
		sortierrichtung = null;
		sortierspalte = 0;
		tm = null;
		mouseListener = null;
		aktualisieren();
		cm = new SpielertabellenSpalte();
		Spaltenkonfiguration spaltenkonfiguration[] = null;
		if (spaltenkonfiguration == null) {
			int n = spaltennamen.length;
			for (int i = 0; i < n; i++)
				cm.SpalteHinzufuegen(i, spaltennamen[i], spaltenweite[i]);

		}
		sortierrichtung = new boolean[cm.getColumnCount()];
		tm = new SpielertabellenModell();
		setSize(1200, 500);
		setModel(tm);
		setColumnModel(cm);
		setAutoResizeMode(0);
		setDefaultRenderer(java.lang.Object.class, new ColoredTableCellRenderer());
		JTableHeader header = getTableHeader();
		if (header != null) {
			mouseListener = new MouseHandler();
			header.addMouseListener(mouseListener);
			header.addMouseMotionListener(cm.getColumnHeaderToolTips());
		}
	}

	private class SpielertabellenModell extends AbstractTableModel {
		private static final long serialVersionUID = -3365452097304380041L;

		@Override
		public int getRowCount() {
			return HOVerwaltung.instance().getModel().getAllSpieler().size();
		}

		@Override
		public int getColumnCount() {
			return cm.getColumnCount();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			DecimalFormat percentFormat = new DecimalFormat("##0%");
			DateFormat datef = DateFormat.getDateInstance(2);
			int pos = spielerSortierung[rowIndex].index;
			Spieler s = spieler.elementAt(pos);
			String ret;
			switch (columnIndex) {
			case 0: // '\0'
				ret = s.getName();
				break;

			case 1: // '\001'
				ret = "" + s.getAlter();
				break;

			case 2: // '\002'
				ret = "" + s.getErfahrung();
				break;

			case 3: // '\003'
				ret = datef.format(s.getLetzteErfahrungsAufwertung());
				break;

			case 4: // '\004'
				ret = percentFormat.format(s.getErfahrungsBonus());
				break;

			case 5: // '\005'
				ret = decimalFormat.format(s.getErfahrungMin());
				break;

			case 6: // '\006'
				ret = decimalFormat.format(s.getErfahrungWahrscheinlich()) + " \261 "
						+ decimalFormat.format(s.getErfahrungWahrscheinlichFehler());
				break;

			case 7: // '\007'
				ret = decimalFormat.format(s.getErfahrungMax());
				break;

			case 8: // '\b'
				ret = "";
				int anzahlWochen = s.getAnzahlWochen();
				if (anzahlWochen > 0)
					ret = ret + anzahlWochen + " \261 " + s.getAnzahlWochenFehler();
				break;

			case 9: // '\t'
				ret = s.getEinsaetzeAlsText(9);
				break;

			case 10: // '\n'
				ret = s.getEinsaetzeAlsText(8);
				break;

			case 11: // '\013'
				ret = s.getEinsaetzeAlsText(7);
				break;

			case 12: // '\f'
				ret = s.getEinsaetzeAlsText(6);
				break;

			case 13: // '\r'
				ret = s.getEinsaetzeAlsText(11);
				break;

			case 14: // '\016'
				ret = s.getEinsaetzeAlsText(10);
				break;

			case 15: // '\017'
				ret = s.getEinsaetzeAlsText(1);
				break;

			case 16: // '\020'
				ret = s.getEinsaetzeAlsText(3);
				break;

			case 17: // '\021'
				ret = s.getEinsaetzeAlsText(2);
				break;

			case 18: // '\022'
				ret = s.getEinsaetzeAlsText(12);
				break;

			case 19: // '\023'
				ret = s.getEinsaetzeAlsText(5);
				break;

			case 20: // '\024'
				ret = s.getEinsaetzeAlsText(4);
				break;

			case 21: // '\025'
				ret = s.getBemerkung();
				break;

			default:
				ret = "-";
				break;
			}
			return ret;
		}

		SpielertabellenModell() {
		}
	}

	private class SpielertabellenSpalte extends DefaultTableColumnModel {
		private static final long serialVersionUID = 2065608315613845209L;
		private ColumnHeaderToolTips tips;

		public ColumnHeaderToolTips getColumnHeaderToolTips() {
			return tips;
		}

		public void SpalteHinzufuegen(int index, String text, int weite) {
			TableColumn col = new TableColumn(index, weite);
			String t = HOVerwaltung.instance().getLanguageString(text);
			String tipp;
			if (index > 8 && index < 21) {
				String zusatz = HOVerwaltung.instance().getLanguageString(
						"EINSATZSPALTENERKLAERUNG");
				tipp = "<html>" + t + "<br>" + zusatz + "</html>";
			} else {
				tipp = t;
			}
			col.setHeaderValue(t);
			tips.setToolTip(col, tipp);
			addColumn(col);
		}

		public SpielertabellenSpalte() {
			tips = null;
			tips = new ColumnHeaderToolTips();
		}
	}

	private class SpielerSortierung implements Comparable<Object> {

		private int index;

		@Override
		public int compareTo(Object o) {
			int o1 = index;
			int o2 = ((SpielerSortierung) o).index;
			Spieler s1 = (Spieler) spieler.elementAt(o1);
			Spieler s2 = (Spieler) spieler.elementAt(o2);
			int col = GibSortierspalte();
			int r;
			if (istAufwaertsSortiert(col))
				r = 1;
			else
				r = -1;
			switch (col) {
			case 0: // '\0'
			{
				return r * s1.getName().compareTo(s2.getName());
			}

			case 1: // '\001'
			{
				Integer i1 = new Integer(s1.getAlter());
				return r * i1.compareTo(new Integer(s2.getAlter()));
			}

			case 2: // '\002'
			{
				Integer i1 = new Integer(s1.getErfahrung());
				return r * i1.compareTo(new Integer(s2.getErfahrung()));
			}

			case 3: // '\003'
			{
				return r
						* s1.getLetzteErfahrungsAufwertung().compareTo(
								s2.getLetzteErfahrungsAufwertung());
			}

			case 4: // '\004'
			{
				Double d1 = new Double(s1.getErfahrungsBonus());
				return r * d1.compareTo(new Double(s2.getErfahrungsBonus()));
			}

			case 5: // '\005'
			{
				Double d1 = new Double(s1.getErfahrungMin());
				return r * d1.compareTo(new Double(s2.getErfahrungMin()));
			}

			case 6: // '\006'
			{
				Double d1 = new Double(s1.getErfahrungWahrscheinlich());
				return r * d1.compareTo(new Double(s2.getErfahrungWahrscheinlich()));
			}

			case 7: // '\007'
			{
				Double d1 = new Double(s1.getErfahrungMax());
				return r * d1.compareTo(new Double(s2.getErfahrungMax()));
			}

			case 8: // '\b'
			{
				Integer i1 = new Integer(s1.getAnzahlWochen());
				return r * i1.compareTo(new Integer(s2.getAnzahlWochen()));
			}

			case 9: // '\t'
			{
				return r * vergleicheEinsaetze(s1, s2, 9);
			}

			case 10: // '\n'
			{
				return r * vergleicheEinsaetze(s1, s2, 8);
			}

			case 11: // '\013'
			{
				return r * vergleicheEinsaetze(s1, s2, 7);
			}

			case 12: // '\f'
			{
				return r * vergleicheEinsaetze(s1, s2, 6);
			}

			case 13: // '\r'
			{
				return r * vergleicheEinsaetze(s1, s2, 11);
			}

			case 14: // '\016'
			{
				return r * vergleicheEinsaetze(s1, s2, 10);
			}

			case 15: // '\017'
			{
				return r * vergleicheEinsaetze(s1, s2, 1);
			}

			case 16: // '\020'
			{
				return r * vergleicheEinsaetze(s1, s2, 3);
			}

			case 17: // '\021'
			{
				return r * vergleicheEinsaetze(s1, s2, 2);
			}

			case 18: // '\022'
			{
				return r * vergleicheEinsaetze(s1, s2, 12);
			}

			case 19: // '\023'
			{
				return r * vergleicheEinsaetze(s1, s2, 5);
			}

			case 20: // '\024'
			{
				return r * vergleicheEinsaetze(s1, s2, 4);
			}

			case 21: // '\025'
			{
				return r * s1.getBemerkung().compareTo(s2.getBemerkung());
			}
			}
			return 0;
		}

		public SpielerSortierung(int index) {
			this.index = index;
		}
	}

	private class MouseHandler extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			JTableHeader h = (JTableHeader) e.getSource();
			TableColumnModel columnModel = h.getColumnModel();
			int viewColumn = columnModel.getColumnIndexAtX(e.getX());
			int column = columnModel.getColumn(viewColumn).getModelIndex();
			if (column != -1) {
				boolean aufwaerts = istAufwaertsSortiert(column);
				tabelleSortieren(column, !aufwaerts);
			}
		}

		MouseHandler() {
		}
	}

	class ColumnHeaderToolTips extends MouseMotionAdapter {

		TableColumn curCol;
		Map<TableColumn, String> tips;

		public void setToolTip(TableColumn col, String tooltip) {
			if (tooltip == null)
				tips.remove(col);
			else
				tips.put(col, tooltip);
		}

		@Override
		public void mouseMoved(MouseEvent evt) {
			TableColumn col = null;
			JTableHeader header = (JTableHeader) evt.getSource();
			JTable table = header.getTable();
			TableColumnModel colModel = table.getColumnModel();
			int vColIndex = colModel.getColumnIndexAtX(evt.getX());
			if (vColIndex >= 0)
				col = colModel.getColumn(vColIndex);
			if (col != curCol) {
				header.setToolTipText((String) tips.get(col));
				curCol = col;
			}
		}

		public ColumnHeaderToolTips() {
			tips = new HashMap<TableColumn, String>();
		}
	}

	class ColoredTableCellRenderer implements TableCellRenderer {
		private Color hellblau = ThemeManager.getColor(HOColorName.PLAYER_POS_BG);
		private Color dunkelblau = ThemeManager.getColor(HOColorName.PLAYER_SUBPOS_BG);
		private Color hellgelb = ThemeManager.getColor(HOColorName.PLAYER_SKILL_BG);
		private Color hellgruen = ThemeManager.getColor(HOColorName.PLAYER_SKILL_SPECIAL_BG);
		private JLabel label;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (label == null)
				label = new JLabel((String) value);
			else
				label.setText((String) value);
			label.setOpaque(true);
			javax.swing.border.Border b = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			label.setBorder(b);
			label.setFont(table.getFont());
			label.setForeground(table.getForeground());
			label.setBackground(table.getBackground());
			if (hasFocus)
				label.setBackground(Color.lightGray);
			else if (isSelected) {
				label.setBackground(Color.lightGray);
			} else {
				column = table.convertColumnIndexToModel(column);
				switch (column) {
				case 2: // '\002'
				case 3: // '\003'
				case 4: // '\004'
					label.setBackground(hellgruen);
					break;

				case 5: // '\005'
					label.setBackground(hellgelb);
					break;

				case 6: // '\006'
					label.setBackground(hellgelb);
					break;

				case 7: // '\007'
				case 8: // '\b'
					label.setBackground(hellgelb);
					break;

				case 9: // '\t'
				case 10: // '\n'
					label.setBackground(hellblau);
					break;

				case 11: // '\013'
				case 12: // '\f'
				case 13: // '\r'
				case 14: // '\016'
				case 15: // '\017'
				case 16: // '\020'
				case 17: // '\021'
				case 18: // '\022'
					label.setBackground(dunkelblau);
					break;

				case 19: // '\023'
				case 20: // '\024'
					label.setBackground(hellblau);
					break;
				}
			}
			return label;
		}

	}

	private class Spaltenkonfiguration {

		public int index;
		public int weite;
	}

	void aktualisieren() {
		Vector<core.model.player.Spieler> alleSpieler = HOVerwaltung.instance().getModel()
				.getAllSpieler();
		spielerSortierung = new SpielerSortierung[alleSpieler.size()];
		spieler = new Vector<Spieler>(alleSpieler.size());
		int pos = 0;
		for (Enumeration<core.model.player.Spieler> el = alleSpieler.elements(); el
				.hasMoreElements();) {
			spieler.add(new Spieler(el.nextElement()));
			spielerSortierung[pos] = new SpielerSortierung(pos);
			pos++;
		}

	}

	private int vergleicheEinsaetze(Spieler s1, Spieler s2, int spieltyp) {
		int ret = 0;
		Integer i1 = new Integer(s1.getEinsaetze(spieltyp));
		ret = i1.compareTo(new Integer(s2.getEinsaetze(spieltyp)));
		if (ret == 0) {
			i1 = new Integer(s1.getEinsaetzeNachAufwertung(spieltyp));
			ret = i1.compareTo(new Integer(s2.getEinsaetzeNachAufwertung(spieltyp)));
			if (ret == 0) {
				i1 = new Integer(s1.getEinsaetzeMitAktualisierungNachAufwertung(spieltyp));
				ret = i1.compareTo(new Integer(s2
						.getEinsaetzeMitAktualisierungNachAufwertung(spieltyp)));
			}
		}
		return ret;
	}

	private int GibSortierspalte() {
		return sortierspalte;
	}

	private boolean istAufwaertsSortiert(int column) {
		if (sortierrichtung != null && column > -1 && column < cm.getColumnCount())
			return sortierrichtung[column];
		else
			return false;
	}

	private void tabelleSortieren(int spalte, boolean aufwaerts) {
		sortierspalte = spalte;
		sortierrichtung[spalte] = aufwaerts;
		Arrays.sort(spielerSortierung);
		TabelleGeaendert();
	}

	private void TabelleGeaendert() {
		tm.fireTableDataChanged();
	}

	private String gibKonfigurationsdateiname() {
		return experienceViewerVerzeichnis + File.separator + "ExperienceViewer.cfg";
	}
}
