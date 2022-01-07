package core.gui.model;

import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.enums.MatchTypeExtended;
import core.model.match.IMatchType;
import core.model.match.MatchKurzInfo;
import core.model.enums.MatchType;

import java.awt.Color;

public final class MatchesColumnModel extends HOTableModel {

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

	public MatchKurzInfo getMatch(int id) {
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

	public void setValues(MatchKurzInfo[] matches) {
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
				if (j != 5 && j != 6) {
					m_clData[i][j] = ((MatchKurzInfoColumn) tmpDisplayedColumns[j]).getTableEntry(m_clMatches[i]);
				} else {  // HatStats calculations need information from match details
					var oMD = m_clMatches[i].getMatchdetails();
					m_clData[i][j] = ((MatchKurzInfoColumn) tmpDisplayedColumns[j]).getTableEntry(m_clMatches[i], oMD);
				}
			}

		}
		fireTableDataChanged();
	}

	/**
	 * Get the color for a certain match type.
	 */
	public static Color getColor4Matchtyp(IMatchType iMatchType) {
		if(iMatchType instanceof MatchType){
			return switch ((MatchType) iMatchType){
				case LEAGUE -> ThemeManager.getColor(HOColorName.MATCHTYPE_LEAGUE_BG);
				case CUP -> ThemeManager.getColor(HOColorName.MATCHTYPE_CUP_BG);
				case QUALIFICATION -> ThemeManager.getColor(HOColorName.MATCHTYPE_QUALIFIKATION_BG);
				case MASTERS -> ThemeManager.getColor(HOColorName.MATCHTYPE_MASTERS_BG);
				case INTSPIEL ->ThemeManager.getColor(HOColorName.MATCHTYPE_MASTERS_BG);
				case INTFRIENDLYCUPRULES, INTFRIENDLYNORMAL -> ThemeManager.getColor(HOColorName.MATCHTYPE_INTFRIENDLY_BG);
				case NATIONALCOMPCUPRULES, NATIONALCOMPNORMAL, NATIONALFRIENDLY -> ThemeManager.getColor(HOColorName.MATCHTYPE_NATIONAL_BG);
				case FRIENDLYCUPRULES, FRIENDLYNORMAL -> ThemeManager.getColor(HOColorName.MATCHTYPE_FRIENDLY_BG);
				case TOURNAMENTGROUP -> ThemeManager.getColor(HOColorName.MATCHTYPE_TOURNAMENT_GROUP_BG);
				case TOURNAMENTPLAYOFF -> ThemeManager.getColor(HOColorName.MATCHTYPE_TOURNAMENT_FINALS_BG);
				default -> ThemeManager.getColor(HOColorName.MATCHTYPE_BG);
			};
		}
		else {
			return switch ((MatchTypeExtended) iMatchType){
				case DIVISIONBATTLE -> ThemeManager.getColor(HOColorName.MATCHTYPE_DIVISIONBATTLE_BG);
				default -> ThemeManager.getColor(HOColorName.MATCHTYPE_BG);
			};
		}

		}

}
