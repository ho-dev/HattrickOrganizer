package core.gui.model;

import core.db.DBManager;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.match.MatchKurzInfo;
import core.model.match.Matchdetails;
import core.model.match.MatchType;

import java.awt.Color;

public final class MatchesColumnModel extends HOTableModel {

	private static final long serialVersionUID = -2148644586671286752L;
	private MatchKurzInfo[] m_clMatches;

	protected MatchesColumnModel(int id) {
		super(id, "Matches");
		initialize();
	}

	private void initialize() {
		columns = UserColumnFactory.createMatchesArray();

		if (m_clMatches != null)
			initData();
	}

	public final MatchKurzInfo getMatch(int id) {
		if (id > 0) {
			for (int i = 0; i < m_clMatches.length; i++) {
				if (m_clMatches[i].getMatchID() == id) {
					return m_clMatches[i];
				}
			}
		}
		return null;
	}

	public boolean isEditable() {
		return false;
	}

	public final void setValues(MatchKurzInfo[] matches) {
		m_clMatches = matches;
		initData();
	}

	/**
	 * Erzeugt einen Data[][] aus dem Spielervector
	 */
	@Override
	protected void initData() {
		UserColumn[] tmpDisplayedColumns = getDisplayedColumns();
		m_clData = new Object[m_clMatches.length][tmpDisplayedColumns.length];

		for (int i = 0; i < m_clMatches.length; i++) {

			for (int j = 0; j < tmpDisplayedColumns.length; j++) {
				if(j!=5 && j!=6) 
				{
					m_clData[i][j] = ((MatchKurzInfoColumn) tmpDisplayedColumns[j]).getTableEntry(m_clMatches[i]);
				}
				else {  // HatStats calculations need information from match details
					Matchdetails oMD = DBManager.instance().getMatchDetails(m_clMatches[i].getMatchID());
				m_clData[i][j] = ((MatchKurzInfoColumn) tmpDisplayedColumns[j]).getTableEntry(m_clMatches[i], oMD);}
			}

		}
		fireTableDataChanged();
	}

	/**
	 * Get the color for a certain match type.
	 */
	public static Color getColor4Matchtyp(MatchType typ) {
		switch (typ) {
		case LEAGUE:
			return ThemeManager.getColor(HOColorName.MATCHTYPE_LEAGUE_BG);

		case CUP:
			return ThemeManager.getColor(HOColorName.MATCHTYPE_CUP_BG);

		case QUALIFICATION:
			return ThemeManager.getColor(HOColorName.MATCHTYPE_QUALIFIKATION_BG);

		case MASTERS:
			return ThemeManager.getColor(HOColorName.MATCHTYPE_MASTERS_BG);

		case INTSPIEL:
			return ThemeManager.getColor(HOColorName.MATCHTYPE_MASTERS_BG);

		case INTFRIENDLYCUPRULES:
		case INTFRIENDLYNORMAL:
			return ThemeManager.getColor(HOColorName.MATCHTYPE_INTFRIENDLY_BG);

		case NATIONALCOMPCUPRULES:
		case NATIONALCOMPNORMAL:
		case NATIONALFRIENDLY:
			return ThemeManager.getColor(HOColorName.MATCHTYPE_NATIONAL_BG);

		case FRIENDLYCUPRULES:
		case FRIENDLYNORMAL:
			return ThemeManager.getColor(HOColorName.MATCHTYPE_FRIENDLY_BG);

		case TOURNAMENTGROUP:
			return ThemeManager.getColor(HOColorName.MATCHTYPE_TOURNAMENT_GROUP_BG);
		case TOURNAMENTPLAYOFF:
			return ThemeManager.getColor(HOColorName.MATCHTYPE_TOURNAMENT_FINALS_BG);
			// Fehler?
		default:
			return ThemeManager.getColor(HOColorName.MATCHTYPE_BG);
		}
	}
}
