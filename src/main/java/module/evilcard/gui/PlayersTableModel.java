package module.evilcard.gui;

import core.constants.player.PlayerAggressiveness;
import core.constants.player.PlayerHonesty;
import core.db.DBManager;
import core.gui.model.SpielerMatchCBItem;
import core.model.HOVerwaltung;
import core.model.match.MatchEvent;
import core.model.player.Player;
import module.evilcard.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

class PlayersTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 146702588079846156L;

	static final int COL_ID = 0;
	static final int COL_NAME = 1;
	static final int COL_AGGRESSIVITY = 2;
	static final int COL_HONESTY = 3;
	static final int COL_CARDS = 4;
	static final int COL_DIRECT_RED_CARDS = 5;
	static final int COL_WARNINGS = 6;
	static final int COL_WARNINGS_TYPE1 = 7;
	static final int COL_WARNINGS_TYPE2 = 8;
	static final int COL_WARNINGS_TYPE3 = 9;
	static final int COL_WARNINGS_TYPE4 = 10;
	static final int COL_RAW_AVERAGE = 11;
	static final int COL_WEIGHTED_AVERAGE = 12;
	static final int COL_MATCHES = 13;
	static final int cols = 14;

	private String[] columnNames;
	private Object[][] data = {};
	private int m_typePlayer;
	private int playersNumber;

	PlayersTableModel() {
		columnNames = new String[cols];

		// Riempimento valori
		columnNames[COL_ID] = HOVerwaltung.instance().getLanguageString("ls.player.id");
		columnNames[COL_NAME] = HOVerwaltung.instance().getLanguageString("Spieler");
		columnNames[COL_AGGRESSIVITY] = HOVerwaltung.instance().getLanguageString(
				"ls.player.aggressiveness");
		columnNames[COL_HONESTY] = HOVerwaltung.instance().getLanguageString("ls.player.honesty");
		columnNames[COL_CARDS] = HOVerwaltung.instance().getLanguageString("Gesamt");
		columnNames[COL_DIRECT_RED_CARDS] = HOVerwaltung.instance().getLanguageString(
				"column.RedCards");
		columnNames[COL_WARNINGS] = HOVerwaltung.instance().getLanguageString("GelbeKarten");
		columnNames[COL_WARNINGS_TYPE1] = HOVerwaltung.instance().getLanguageString(
				"column.WarningType1");
		columnNames[COL_WARNINGS_TYPE2] = HOVerwaltung.instance().getLanguageString(
				"column.WarningType2");
		columnNames[COL_WARNINGS_TYPE3] = HOVerwaltung.instance().getLanguageString(
				"column.WarningType3");
		columnNames[COL_WARNINGS_TYPE4] = HOVerwaltung.instance().getLanguageString(
				"column.WarningType4");
		columnNames[COL_RAW_AVERAGE] = HOVerwaltung.instance().getLanguageString(
				"column.RawAverage");
		columnNames[COL_WEIGHTED_AVERAGE] = HOVerwaltung.instance().getLanguageString(
				"column.WeightedAverage");
		columnNames[COL_MATCHES] = HOVerwaltung.instance().getLanguageString("Spiele_kurz");
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case COL_NAME:
			return String.class;
		case COL_ID:
			return Integer.class;
		case COL_AGGRESSIVITY:
			return Aggressive.class;
		case COL_HONESTY:
			return Honesty.class;
		case COL_MATCHES:
		case COL_CARDS:
		case COL_DIRECT_RED_CARDS:
		case COL_WARNINGS:
		case COL_WARNINGS_TYPE1:
		case COL_WARNINGS_TYPE2:
		case COL_WARNINGS_TYPE3:
		case COL_WARNINGS_TYPE4:
			return Integer.class;
		case COL_RAW_AVERAGE:
			return Double.class;
		case COL_WEIGHTED_AVERAGE:
			return Double.class;			
		default:
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int c) {
		return columnNames[c];
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}

	public void refresh(int filterMode) {
		this.m_typePlayer = filterMode;
		generateData();
		fireTableDataChanged();
	}

	private void aggiornaMedie(int row) {
		int matches = ((Integer) data[row][COL_MATCHES]).intValue();

		if (matches > 0) {
			int warnings = ((Integer) data[row][COL_WARNINGS]).intValue();
			int direct_reds = ((Integer) data[row][COL_DIRECT_RED_CARDS]).intValue();
			int total = ((Integer) data[row][COL_CARDS]).intValue();

			double val = (approssima((total * 100.0) / matches));
			data[row][COL_RAW_AVERAGE] = new Double(val);

			val = approssima(((warnings + (direct_reds * 2)) * 100.0) / (matches));
			data[row][COL_WEIGHTED_AVERAGE] = new Double(val);
		} else {
			data[row][COL_RAW_AVERAGE] = null;
			data[row][COL_WEIGHTED_AVERAGE] = null;
		}
	}

	private double approssima(double valore) {
		long approx = (new Double(valore * 10.0)).longValue();
		return approx / 10.0;
	}

	/**
	 * Helper class to sort and show aggressive information
	 * 
	 */
	private class Aggressive implements Comparable<Aggressive> {
		private Player _player;

		Aggressive(Player player) {
			_player = player;
		}

		@Override
		public String toString() {
			return PlayerAggressiveness.toString(_player.getAgressivitaet()) + " ("
					+ _player.getAgressivitaet() + ")";
		}

		@Override
		public int compareTo(Aggressive o2) {
			Player p1 = _player;
			Player p2 = o2._player;

			if (p1.getAgressivitaet() == p2.getAgressivitaet())
				return 0;
			return p1.getAgressivitaet() < p2.getAgressivitaet() ? -1 : 1;
		}
	}

	/**
	 * Helper class to sort and show honesty information
	 * 
	 */
	private class Honesty implements Comparable<Honesty> {
		private Player _player;

		Honesty(Player player) {
			_player = player;
		}

		@Override
		public String toString() {
			return PlayerHonesty.toString(_player.getAnsehen()) + " (" + _player.getAnsehen()
					+ ")";
		}

		@Override
		public int compareTo(Honesty o2) {
			Player p1 = _player;
			Player p2 = o2._player;

			if (p1.getAnsehen() == p2.getAnsehen())
				return 0;
			return p1.getAnsehen() < p2.getAnsehen() ? -1 : 1;
		}
	}

	private void generateData() {
		// Get current players.
		Vector<Player> players = new Vector<Player>();
		players.addAll(HOVerwaltung.instance().getModel().getAllSpieler());

		// Add old players, when requested.
		if (m_typePlayer == Model.TYPE_ALL_PLAYERS) {
			players.addAll(HOVerwaltung.instance().getModel().getAllOldSpieler());
		}

		playersNumber = players.size();

		// Reset table data.
		data = new Object[playersNumber][cols];

		for (int row = 0; row < playersNumber; row++) {
			// giocatore
			Player player = (Player) players.get(row);

			int id = player.getSpielerID();
			
			data[row][COL_NAME] = player.getFullName();
			data[row][COL_ID] = new Integer(id);
			data[row][COL_AGGRESSIVITY] = new Aggressive(player);
			data[row][COL_HONESTY] = new Honesty(player);

			for (int col = 4; col < cols; col++) {
				data[row][col] = new Integer(0);
			}

			ArrayList<MatchEvent> highlights = DBManager.instance().getMatchHighlightsByTypIdAndPlayerId(MatchEvent.MatchEventCategory.MATCH_EVENT_CARDS.ordinal(), id);

			int matchid = 0;

			for (Iterator<MatchEvent> iterator = highlights.iterator(); iterator.hasNext();) {
				MatchEvent matchHighlight = iterator.next();
				MatchEvent.MatchEventID me = matchHighlight.getMatchEventID();
				int matchidlast = matchHighlight.getMatchId();

				if (matchid != matchidlast) {
					matchid = matchidlast;
				}

				incrementaValoreColonna(row, COL_CARDS);

				switch (me) {
				case YELLOW_CARD_NASTY_PLAY:   //#510
					incrementaValoreColonna(row, COL_WARNINGS_TYPE1);
					incrementaValoreColonna(row, COL_WARNINGS);
					break;

				case RED_CARD_2ND_WARNING_NASTY_PLAY:    //#512
					incrementaValoreColonna(row, COL_WARNINGS_TYPE2);
					incrementaValoreColonna(row, COL_WARNINGS);
					incrementaValoreColonna(row, COL_MATCHES);
					break;

				case YELLOW_CARD_CHEATING:   //#511
					incrementaValoreColonna(row, COL_WARNINGS_TYPE3);
					incrementaValoreColonna(row, COL_WARNINGS);
					break;

				case RED_CARD_2ND_WARNING_CHEATING:   //#513
					incrementaValoreColonna(row, COL_WARNINGS_TYPE4);
					incrementaValoreColonna(row, COL_WARNINGS);
					incrementaValoreColonna(row, COL_MATCHES);
					break;

				case RED_CARD_WITHOUT_WARNING:   //#514
					incrementaValoreColonna(row, COL_DIRECT_RED_CARDS);
					incrementaValoreColonna(row, COL_MATCHES);
					break;

				// TODO: check wheter or not this block can be removed
				// infortunio
				case BADLY_INJURED_LEAVES_FIELD:
					incrementaValoreColonna(row, COL_MATCHES);
					System.out.println("not expecting to get here ..... Check what this block is doing !!!");
					break;
				}
			}

			Vector<SpielerMatchCBItem> matches = DBManager.instance().getSpieler4Matches(id);
			data[row][COL_MATCHES] = matches.size();

			// GESITONE MEDIE
			aggiornaMedie(row);
		}

		// for (per tutti i giocatori)
	}

	private void incrementaValoreColonna(int rowFind, int colonna) {
		// solo se presenti
		if (rowFind != -1) {
			data[rowFind][colonna] = new Integer(((Integer) data[rowFind][colonna]).intValue() + 1);
		}
	}
}
