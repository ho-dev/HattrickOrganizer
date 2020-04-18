// %3862884693:de.hattrickorganizer.gui.league%
package module.series;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoppelLabelEntry;
import core.gui.comp.icon.DrawIcon;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.model.VAPTableModel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.series.LigaTabellenEintrag;
import core.util.HOLogger;
import core.util.Helper;
import core.util.StringUtils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * A panel with a league table
 */
class SeriesTablePanel extends ImagePanel {

	private static final long serialVersionUID = -7087165908899999232L;
	public static final EmptyBorder EMPTY_BORDER = new EmptyBorder(5, 5, 5, 5);
	private Color TITLE_BACKGROUND = ThemeManager.getColor(HOColorName.LEAGUE_TITLE_BG);
	private Color TABLE_BACKGROUND = ThemeManager.getColor(HOColorName.LEAGUE_BG);
	private Color TABLE_FOREGROUND = ThemeManager.getColor(HOColorName.LEAGUE_FG);

	private Color TABLE_EVEN_ROW = ThemeManager.getColor(HOColorName.TABLE_LEAGUE_EVEN);
	private Color TABLE_ODD_ROW = ThemeManager.getColor(HOColorName.TABLE_LEAGUE_ODD);

	private final String[] COLUMNNAMES = {
			"",
			"",
			HOVerwaltung.instance().getLanguageString("Punkte_kurz"),
			HOVerwaltung.instance().getLanguageString("Spiele_kurz"),
			HOVerwaltung.instance().getLanguageString("SerieAuswaertsSieg"),
			HOVerwaltung.instance().getLanguageString("SerieAuswaertsUnendschieden"),
			HOVerwaltung.instance().getLanguageString("SerieAuswaertsNiederlage"),
			HOVerwaltung.instance().getLanguageString("Tore"),
			HOVerwaltung.instance().getLanguageString("Differenz_kurz"),
			HOVerwaltung.instance().getLanguageString("Serie.Last9"),
	};
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
		return (row%2 == 0) ? TABLE_EVEN_ROW : TABLE_ODD_ROW;
	}

	private void setTableColumnWidth() {
		final TableColumnModel columnModel = seriesTable.getColumnModel();

		// Platz
		columnModel.getColumn(0).setPreferredWidth(Helper.calcCellWidth(45));

		// Verein
		columnModel.getColumn(1).setPreferredWidth(Helper.calcCellWidth(200));

		// Punkte
		columnModel.getColumn(2).setPreferredWidth(Helper.calcCellWidth(30));

		// Spiele
		columnModel.getColumn(3).setPreferredWidth(Helper.calcCellWidth(25));

		// Gewonnen
		columnModel.getColumn(4).setPreferredWidth(Helper.calcCellWidth(25));

		// Unendschieden
		columnModel.getColumn(5).setPreferredWidth(Helper.calcCellWidth(25));

		// Verloren
		columnModel.getColumn(6).setPreferredWidth(Helper.calcCellWidth(25));

		// Tore
		columnModel.getColumn(7).setPreferredWidth(Helper.calcCellWidth(50));

		// Differenz
		columnModel.getColumn(8).setPreferredWidth(Helper.calcCellWidth(50));

		// Serie
		columnModel.getColumn(9).setPreferredWidth(Helper.calcCellWidth(140));
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

		seriesTable.setDefaultRenderer(java.lang.Object.class, new HODefaultTableCellRenderer());
		seriesTable.setRowHeight(30);

		final JPanel panel = new ImagePanel(layout);
		layout.setConstraints(panel, constraints);
		panel.add(seriesTable);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.anchor = GridBagConstraints.NORTH;
		layout.setConstraints(panel, constraints);

		setLayout(new BorderLayout());
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

			final ColorLabelEntry left = new ColorLabelEntry("",
					ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.RIGHT);
			left.setBorder(EMPTY_BORDER);
			final ColorLabelEntry right = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			right.setBorder(EMPTY_BORDER);
			tableValues[i][0] = new DoppelLabelEntry(left, right);

			final ColorLabelEntry teamNameEntry = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.LEFT);
			teamNameEntry.setBorder(EMPTY_BORDER);

			tableValues[i][1] = teamNameEntry;

			tableValues[i][2] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][3] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][4] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][5] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][6] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.CENTER);
			tableValues[i][7] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][8] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			tableValues[i][9] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.LEFT);

			for (int j = 2 ; j < 9 ; j++) {
				((JLabel)tableValues[i][j]).setBorder(EMPTY_BORDER);
			}
		}

		// Model setzen
		seriesTable.setModel(new VAPTableModel(COLUMNNAMES, tableValues));
		setTableColumnWidth();
	}

	private void reinitTabelle() {
		try {
			if (this.model.getCurrentSeries() != null) {
				final Vector<LigaTabellenEintrag> tabelleneintraege = this.model.getCurrentSeries()
						.getTabelle().getEintraege();
				final int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
				int j = -1;

				for (int i = 0; i < tabelleneintraege.size(); i++) {
					final LigaTabellenEintrag eintrag = tabelleneintraege.get(i);

					if (eintrag.getPunkte() > -1) {
						j = i + 1;

						((DoppelLabelEntry) tableValues[j][0]).getLinks().setText("");
						((DoppelLabelEntry) tableValues[j][0]).getLinks().setFontStyle(Font.BOLD);
						if (eintrag.getPosition() > eintrag.getAltePosition()) {
							((DoppelLabelEntry) tableValues[j][0]).getLinks().setIcon(new DrawIcon(DrawIcon.UPWARD_DIRECTION));
						} else if (eintrag.getPosition() < eintrag.getAltePosition()) {
							((DoppelLabelEntry) tableValues[j][0]).getLinks().setIcon(new DrawIcon(DrawIcon.DOWNWARD_DIRECTION));
						}

						((DoppelLabelEntry) tableValues[j][0]).getRechts().setText(eintrag.getPosition() + "");
						((DoppelLabelEntry) tableValues[j][0]).getRechts().setFontStyle(Font.BOLD);

						((ColorLabelEntry) tableValues[j][1]).setText(eintrag.getTeamName());
						((ColorLabelEntry) tableValues[j][1]).setFontStyle(Font.BOLD);

						if (eintrag.getTeamId() == teamid) {
							((ColorLabelEntry) tableValues[j][1]).setFGColor(ThemeManager
									.getColor(HOColorName.TEAM_FG));
						} else {
							((ColorLabelEntry) tableValues[j][1]).setFGColor(TABLE_FOREGROUND);// );Color.black
						}

						((ColorLabelEntry) tableValues[j][2]).setText(eintrag.getPunkte() + "");
						((ColorLabelEntry) tableValues[j][2]).setFontStyle(Font.BOLD);
						((ColorLabelEntry) tableValues[j][3]).setText(eintrag.getAnzSpiele() + "");
						((ColorLabelEntry) tableValues[j][4]).setText(eintrag.getG_Siege() + "");
						((ColorLabelEntry) tableValues[j][5]).setText(eintrag.getG_Un() + "");
						((ColorLabelEntry) tableValues[j][6]).setText(eintrag.getG_Nied() + "");
						((ColorLabelEntry) tableValues[j][7]).setText(StringUtils.getResultString(
								eintrag.getToreFuer(), eintrag.getToreGegen()));
						((ColorLabelEntry) tableValues[j][8]).setSpecialNumber(
								eintrag.getGesamtTorDiff(), false);

						FormLabel formLabel = new FormLabel(eintrag.getSerie());
						formLabel.setBgColor(getColor4Row(j));
						tableValues[j][9] = formLabel;
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
