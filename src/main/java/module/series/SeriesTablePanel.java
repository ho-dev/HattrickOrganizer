// %3862884693:de.hattrickorganizer.gui.league%
package module.series;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoppelLabelEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.model.VAPTableModel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.series.LigaTabellenEintrag;
import core.util.HOLogger;
import core.util.Helper;
import core.util.StringUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * A panel with a league table
 */
class SeriesTablePanel extends ImagePanel {

	private static final long serialVersionUID = -7087165908899999232L;
	private Color TITLE_BACKGROUND = ThemeManager.getColor(HOColorName.LEAGUE_TITLE_BG);
	private Color TABLE_BACKGROUND = ThemeManager.getColor(HOColorName.LEAGUE_BG);
	private Color TABLE_FOREGROUND = ThemeManager.getColor(HOColorName.LEAGUE_FG);
	private final String[] COLUMNNAMES = {
			HOVerwaltung.instance().getLanguageString("Platz"),
			HOVerwaltung.instance().getLanguageString("Verein"),
			HOVerwaltung.instance().getLanguageString("Serie"),
			HOVerwaltung.instance().getLanguageString("Spiele_kurz"),
			HOVerwaltung.instance().getLanguageString("SerieAuswaertsSieg"),
			HOVerwaltung.instance().getLanguageString("SerieAuswaertsUnendschieden"),
			HOVerwaltung.instance().getLanguageString("SerieAuswaertsNiederlage"),
			HOVerwaltung.instance().getLanguageString("Tore"),
			HOVerwaltung.instance().getLanguageString("Differenz_kurz"),
			HOVerwaltung.instance().getLanguageString("Punkte_kurz"),
			"",

			HOVerwaltung.instance().getLanguageString("Heim_kurz")
					+ HOVerwaltung.instance().getLanguageString("SerieAuswaertsSieg"),

			HOVerwaltung.instance().getLanguageString("Heim_kurz")
					+ HOVerwaltung.instance().getLanguageString("SerieAuswaertsUnendschieden"),

			HOVerwaltung.instance().getLanguageString("Heim_kurz")
					+ HOVerwaltung.instance().getLanguageString("SerieAuswaertsNiederlage"),

			HOVerwaltung.instance().getLanguageString("Heim_kurz")
					+ HOVerwaltung.instance().getLanguageString("Tore"),

			HOVerwaltung.instance().getLanguageString("Heim_kurz")
					+ HOVerwaltung.instance().getLanguageString("Differenz_kurz"),

			HOVerwaltung.instance().getLanguageString("Heim_kurz")
					+ HOVerwaltung.instance().getLanguageString("Punkte_kurz"),
			"",

			HOVerwaltung.instance().getLanguageString("Auswaerts_kurz")
					+ HOVerwaltung.instance().getLanguageString("SerieAuswaertsSieg"),

			HOVerwaltung.instance().getLanguageString("Auswaerts_kurz")
					+ HOVerwaltung.instance().getLanguageString("SerieAuswaertsUnendschieden"),

			HOVerwaltung.instance().getLanguageString("Auswaerts_kurz")
					+ HOVerwaltung.instance().getLanguageString("SerieAuswaertsNiederlage"),

			HOVerwaltung.instance().getLanguageString("Auswaerts_kurz")
					+ HOVerwaltung.instance().getLanguageString("Tore"),

			HOVerwaltung.instance().getLanguageString("Auswaerts_kurz")
					+ HOVerwaltung.instance().getLanguageString("Differenz_kurz"),

			HOVerwaltung.instance().getLanguageString("Auswaerts_kurz")
					+ HOVerwaltung.instance().getLanguageString("Punkte_kurz") };
	private JTable seriesTable = new JTable();
	private Object[][] tableValues;
	private final Model model;

	/**
	 * Creates a new LigaTabelle object.
	 */
	SeriesTablePanel(Model model) {
		this.model = model;
		initComponents();

		// Entrys setzen
		initTable();

		// Stadien berechnen
		initSeriesTable();
	}

	final String getSelectedTeam() {
		String team = null;

		if (seriesTable.getSelectedRow() > -1) {
			team = ((ColorLabelEntry) tableValues[seriesTable.getSelectedRow()][1]).getText();
		}

		return team;
	}

	void addListSelectionListener(ListSelectionListener listener) {
		this.seriesTable.getSelectionModel().addListSelectionListener(listener);
	}

	@Override
	public final void addKeyListener(KeyListener listener) {
		seriesTable.addKeyListener(listener);
	}

	// --Listener an Tabelle binden!------------------
	@Override
	public final void addMouseListener(MouseListener listener) {
		seriesTable.addMouseListener(listener);
	}

	// -------Refresh---------------------------------
	public final void changeSaison() {
		reinitTabelle();
	}

	private Color getColor4Row(int row) {
		switch (row) {
		case 1:
			return ThemeManager.getColor(HOColorName.LEAGUE_PROMOTED_BG);

		case 5:
		case 6:
			return ThemeManager.getColor(HOColorName.LEAGUE_RELEGATION_BG);

		case 7:
		case 8:
			return ThemeManager.getColor(HOColorName.LEAGUE_DEMOTED_BG);

		default:
			return TABLE_BACKGROUND;// Color.white;
		}
	}

	private void setTableColumnWidth() {
		final TableColumnModel columnModel = seriesTable.getColumnModel();

		// Platz
		columnModel.getColumn(0).setPreferredWidth(Helper.calcCellWidth(45));

		// Verein
		columnModel.getColumn(1).setPreferredWidth(Helper.calcCellWidth(200));

		// Serie
		columnModel.getColumn(2).setPreferredWidth(Helper.calcCellWidth(110));

		// Spiele
		columnModel.getColumn(3).setPreferredWidth(Helper.calcCellWidth(25));

		// Gewonnen
		columnModel.getColumn(4).setPreferredWidth(Helper.calcCellWidth(25));

		// Unendschieden
		columnModel.getColumn(5).setPreferredWidth(Helper.calcCellWidth(25));

		// Verloren
		columnModel.getColumn(6).setPreferredWidth(Helper.calcCellWidth(25));

		// Tore
		columnModel.getColumn(7).setPreferredWidth(Helper.calcCellWidth(45));

		// Differenz
		columnModel.getColumn(8).setPreferredWidth(Helper.calcCellWidth(30));

		// Punkte
		columnModel.getColumn(9).setPreferredWidth(Helper.calcCellWidth(30));

		// Unterteilung
		TableColumn column = columnModel.getColumn(10);
		column.setMaxWidth(Helper.calcCellWidth(5));
		column.setMinWidth(Helper.calcCellWidth(5));
		column.setPreferredWidth(Helper.calcCellWidth(5));

		// zuhause
		// Gewonnen
		columnModel.getColumn(11).setPreferredWidth(Helper.calcCellWidth(25));

		// Unendschieden
		columnModel.getColumn(12).setPreferredWidth(Helper.calcCellWidth(25));

		// Verloren
		columnModel.getColumn(13).setPreferredWidth(Helper.calcCellWidth(25));

		// Tore
		columnModel.getColumn(14).setPreferredWidth(Helper.calcCellWidth(45));

		// Differenz
		columnModel.getColumn(15).setPreferredWidth(Helper.calcCellWidth(30));

		// Punkte
		columnModel.getColumn(16).setPreferredWidth(Helper.calcCellWidth(30));

		// Unterteilung
		column = columnModel.getColumn(17);
		column.setMaxWidth(Helper.calcCellWidth(5));
		column.setMinWidth(Helper.calcCellWidth(5));
		column.setPreferredWidth(Helper.calcCellWidth(5));

		// auswärts
		// Gewonnen
		columnModel.getColumn(18).setPreferredWidth(Helper.calcCellWidth(25));

		// Unendschieden
		columnModel.getColumn(19).setPreferredWidth(Helper.calcCellWidth(25));

		// Verloren
		columnModel.getColumn(20).setPreferredWidth(Helper.calcCellWidth(25));

		// Tore
		columnModel.getColumn(21).setPreferredWidth(Helper.calcCellWidth(45));

		// Differenz
		columnModel.getColumn(22).setPreferredWidth(Helper.calcCellWidth(30));

		// Punkte
		columnModel.getColumn(23).setPreferredWidth(Helper.calcCellWidth(30));
	}

	private void initComponents() {
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(4, 4, 4, 4);

		setLayout(new BorderLayout());

		seriesTable.setDefaultRenderer(java.lang.Object.class,
				new core.gui.comp.renderer.HODefaultTableCellRenderer());

		final JPanel panel = new ImagePanel(layout);
		layout.setConstraints(panel, constraints);
		panel.add(seriesTable);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.anchor = GridBagConstraints.NORTH;
		layout.setConstraints(panel, constraints);
		add(panel);
	}

	// Init aus dem HRF-XML
	private void initSeriesTable() {
		// Entrys mit Werten füllen
		// Ein Model vorhanden?
		if ((HOVerwaltung.instance().getModel().getSpielplan() != null)
				&& (HOVerwaltung.instance().getModel().getSpielplan().getSaison() > 0)) {
			// Daten in die Tabelle füllen
			reinitTabelle();
		}
	}

	private void initTable() {
		// Tablewerte setzen
		tableValues = new Object[9][COLUMNNAMES.length];

		// Überschrift
		for (int i = 0; i < COLUMNNAMES.length; i++) {
			tableValues[0][i] = new ColorLabelEntry(COLUMNNAMES[i], ColorLabelEntry.FG_STANDARD,
					TITLE_BACKGROUND, SwingConstants.CENTER);
		}

		for (int i = 1; i < 9; i++) {
			final Color bg_Color = getColor4Row(i);
			tableValues[i][0] = new DoppelLabelEntry(new ColorLabelEntry("",
					ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.RIGHT),
					new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
							SwingConstants.RIGHT));
			tableValues[i][1] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.LEFT);
			tableValues[i][2] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.LEFT);
			tableValues[i][3] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][4] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][5] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][6] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][7] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.CENTER);
			tableValues[i][8] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][9] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][10] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
					TITLE_BACKGROUND, SwingConstants.RIGHT);
			tableValues[i][11] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][12] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][13] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][14] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.CENTER);
			tableValues[i][15] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][16] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][17] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
					TITLE_BACKGROUND, SwingConstants.RIGHT);
			tableValues[i][18] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][19] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][20] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][21] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.CENTER);
			tableValues[i][22] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][23] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
		}

		// Model setzen
		seriesTable.setModel(new VAPTableModel(COLUMNNAMES, tableValues));

		setTableColumnWidth();
	}

	private void reinitTabelle() {
		try {
			if (this.model.getCurrentSeries() != null) {
				final Font monospacedFont = new Font("monospaced", Font.PLAIN,
						core.model.UserParameter.instance().schriftGroesse + 1);

				final Vector<LigaTabellenEintrag> tabelleneintraege = this.model.getCurrentSeries()
						.getTabelle().getEintraege();
				final int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
				int j = -1;

				for (int i = 0; i < tabelleneintraege.size(); i++) {
					final LigaTabellenEintrag eintrag = (LigaTabellenEintrag) tabelleneintraege
							.get(i);

					if (eintrag.getPunkte() > -1) {
						j = i + 1;

						((DoppelLabelEntry) tableValues[j][0]).getLinks().setText(
								eintrag.getPosition() + ".");
						((DoppelLabelEntry) tableValues[j][0]).getLinks().setFontStyle(Font.BOLD);
						((DoppelLabelEntry) tableValues[j][0]).getRechts().setText(
								"(" + eintrag.getAltePosition() + ")");
						((ColorLabelEntry) tableValues[j][1]).setText(eintrag.getTeamName());
						((ColorLabelEntry) tableValues[j][1]).setFontStyle(Font.BOLD);

						if (eintrag.getTeamId() == teamid) {
							((ColorLabelEntry) tableValues[j][1]).setFGColor(ThemeManager
									.getColor(HOColorName.TEAM_FG));
						} else {
							((ColorLabelEntry) tableValues[j][1]).setFGColor(TABLE_FOREGROUND);// );Color.black
						}

						((ColorLabelEntry) tableValues[j][2]).setText(eintrag.getSerieAsString());
						((ColorLabelEntry) tableValues[j][2]).setFont(monospacedFont);
						((ColorLabelEntry) tableValues[j][3]).setText(eintrag.getAnzSpiele() + "");
						((ColorLabelEntry) tableValues[j][4]).setText(eintrag.getG_Siege() + "");
						((ColorLabelEntry) tableValues[j][5]).setText(eintrag.getG_Un() + "");
						((ColorLabelEntry) tableValues[j][6]).setText(eintrag.getG_Nied() + "");
						((ColorLabelEntry) tableValues[j][7]).setText(StringUtils.getResultString(
								eintrag.getToreFuer(), eintrag.getToreGegen()));
						((ColorLabelEntry) tableValues[j][8]).setSpecialNumber(
								eintrag.getGesamtTorDiff(), false);
						((ColorLabelEntry) tableValues[j][9]).setText(eintrag.getPunkte() + "");
						((ColorLabelEntry) tableValues[j][9]).setFontStyle(Font.BOLD);
						((ColorLabelEntry) tableValues[j][11]).setText(eintrag.getH_Siege() + "");
						((ColorLabelEntry) tableValues[j][12]).setText(eintrag.getH_Un() + "");
						((ColorLabelEntry) tableValues[j][13]).setText(eintrag.getH_Nied() + "");
						((ColorLabelEntry) tableValues[j][14]).setText(StringUtils.getResultString(
								eintrag.getH_ToreFuer(), eintrag.getH_ToreGegen()));
						((ColorLabelEntry) tableValues[j][15]).setSpecialNumber(
								eintrag.getHeimTorDiff(), false);
						((ColorLabelEntry) tableValues[j][16]).setText(eintrag.getH_Punkte() + "");
						((ColorLabelEntry) tableValues[j][16]).setFontStyle(Font.BOLD);
						((ColorLabelEntry) tableValues[j][18]).setText(eintrag.getA_Siege() + "");
						((ColorLabelEntry) tableValues[j][19]).setText(eintrag.getA_Un() + "");
						((ColorLabelEntry) tableValues[j][20]).setText(eintrag.getA_Nied() + "");
						((ColorLabelEntry) tableValues[j][21]).setText(StringUtils.getResultString(
								eintrag.getA_ToreFuer(), eintrag.getA_ToreGegen()));
						((ColorLabelEntry) tableValues[j][22]).setSpecialNumber(
								eintrag.getAwayTorDiff(), false);
						((ColorLabelEntry) tableValues[j][23]).setText(eintrag.getA_Punkte() + "");
						((ColorLabelEntry) tableValues[j][23]).setFontStyle(Font.BOLD);
					}
				}

				// Model setzen
				seriesTable.setModel(new VAPTableModel(COLUMNNAMES, tableValues));
			}

			setTableColumnWidth();
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "Reinit Tabelle : " + e);
			HOLogger.instance().log(getClass(), e);
		}
	}
}
