package module.evilcard.gui;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.MatchEvent;
import core.model.match.Matchdetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

class DetailsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 5909157286656017836L;

	static final int cols = 10;
	static final int COL_DIRECT_RED_CARDS = 0;
	static final int COL_WARNINGS_TYPE1 = 1;
	static final int COL_WARNINGS_TYPE2 = 2;
	static final int COL_WARNINGS_TYPE3 = 3;
	static final int COL_WARNINGS_TYPE4 = 4;
	static final int COL_MATCH_ID = 5;
	static final int COL_MATCH_HOME = 6;
	static final int COL_MATCH_GUEST = 7;
	static final int COL_MATCH_RESULT = 8;
	static final int COL_EVENT = 9;
	static final String CHECKED = "X";
	static final String UNDEFINED = "";

	private Vector<String> vColumnNames = null;
	private Object[][] data = null;
	private int playerId = 0;

	DetailsTableModel() {
		data = new Object[0][cols];
		initColumnNames();
		// dimensione campi
		generateData();
	}

	@Override
	public Class<?> getColumnClass(int c) {
		return Object.class;
	}

	@Override
	public int getColumnCount() {
		return vColumnNames.size();
	}

	@Override
	public String getColumnName(int c) {
		return (String) vColumnNames.get(c);
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}

	public void refresh(int playerId) {
		this.playerId = playerId;
		generateData();
	}

	private void initColumnNames() {
		vColumnNames = new Vector<String>(Arrays.asList(new String[cols]));

		// Riempimento valori
		vColumnNames.set(COL_DIRECT_RED_CARDS,
				HOVerwaltung.instance().getLanguageString("column.RedCards"));
		vColumnNames.set(COL_WARNINGS_TYPE1,
				HOVerwaltung.instance().getLanguageString("column.WarningType1"));
		vColumnNames.set(COL_WARNINGS_TYPE2,
				HOVerwaltung.instance().getLanguageString("column.WarningType2"));
		vColumnNames.set(COL_WARNINGS_TYPE3,
				HOVerwaltung.instance().getLanguageString("column.WarningType3"));
		vColumnNames.set(COL_WARNINGS_TYPE4,
				HOVerwaltung.instance().getLanguageString("column.WarningType4"));
		vColumnNames.set(COL_MATCH_ID, HOVerwaltung.instance().getLanguageString("ls.match.id"));
		vColumnNames.set(COL_MATCH_HOME, HOVerwaltung.instance().getLanguageString("Heim"));
		vColumnNames.set(COL_MATCH_GUEST, HOVerwaltung.instance().getLanguageString("Gast"));
		vColumnNames.set(COL_MATCH_RESULT,
				HOVerwaltung.instance().getLanguageString("ls.match.result"));
		vColumnNames.set(COL_EVENT, HOVerwaltung.instance().getLanguageString("column.Event"));
	}

	private void generateData() {
		// Inserimento valori iniziali
		if (playerId > 0) {
			ArrayList<MatchEvent> highlights = DBManager.instance()
					.getMatchHighlightsByTypIdAndPlayerId(MatchEvent.MatchEventCategory.MATCH_EVENT_CARDS.ordinal(), playerId);

			int i = 0;
			int rows = highlights.size();

			if (rows > 0) {
				// inizializazione
				data = new Object[rows][cols];

				for (Iterator<MatchEvent> iterator = highlights.iterator(); iterator.hasNext();) {
					MatchEvent matchHighlight = iterator.next();

					data[i][COL_MATCH_ID] = Integer.valueOf(matchHighlight.getMatchId());

					data[i][COL_EVENT] = new String("<html>" + matchHighlight.getEventText());

					// controllo ammonizioni
					switch (matchHighlight.getMatchEventID()) {
						case YELLOW_CARD_NASTY_PLAY:  // #510
						data[i][COL_WARNINGS_TYPE1] = CHECKED;
						break;

						case RED_CARD_2ND_WARNING_NASTY_PLAY: // #512
						data[i][COL_WARNINGS_TYPE2] = CHECKED;
						break;

						case YELLOW_CARD_CHEATING:  // #511
						data[i][COL_WARNINGS_TYPE3] = CHECKED;
						break;

						case RED_CARD_2ND_WARNING_CHEATING: // #513
						data[i][COL_WARNINGS_TYPE4] = CHECKED;
						break;

						case RED_CARD_WITHOUT_WARNING:  // #514
						data[i][COL_DIRECT_RED_CARDS] = CHECKED;
						break;
					}

					data[i][COL_MATCH_HOME] = UNDEFINED;
					data[i][COL_MATCH_HOME] = UNDEFINED;
					data[i][COL_MATCH_RESULT] = UNDEFINED;
					i++;
				}

				// MATCH
				for (i = 0; i < rows; i++) {
					Matchdetails matchDetail = DBManager.instance().getMatchDetails(
							((Integer) data[i][COL_MATCH_ID]).intValue());

					data[i][COL_MATCH_HOME] = matchDetail.getHeimName();
					data[i][COL_MATCH_GUEST] = matchDetail.getGastName();
					data[i][COL_MATCH_RESULT] = matchDetail.getHomeGoals() + " - "
							+ matchDetail.getGuestGoals();
				}
			} else {
				data = new Object[0][cols];
			}
		} else {
			data = new Object[0][cols];
		}

		fireTableDataChanged();
	}
}
