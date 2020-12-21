package core.model.match;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import module.lineup.Lineup;
import module.lineup.substitution.model.Substitution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class MatchLineupTeam {

	private SourceSystem sourceSystem;
	private String m_sTeamName;
	private Vector<MatchLineupPlayer> m_vAufstellung = new Vector<MatchLineupPlayer>();
	private List<Substitution> m_vSubstitutions = new ArrayList<Substitution>();
	private int m_iErfahrung;
	private int m_iTeamID;
	private int m_iStyleOfPlay;
	// null player to fill empty spots
	private final static MatchLineupPlayer NULLPLAYER = new MatchLineupPlayer(-1, 0, -1, -1d, "", 0);
	private MatchType matchType = MatchType.NONE;
	private int matchId;

	// ~ Constructors
	// -------------------------------------------------------------------------------

	/**
	 * Creates a new instance of MatchLineupTeam
	 */
	public MatchLineupTeam(SourceSystem sourceSystem, int matchId, String teamName, int teamID, int erfahrung, int styleOfPlay) {
		this.sourceSystem = sourceSystem;
		m_sTeamName = teamName;
		m_iErfahrung = erfahrung;
		m_iTeamID = teamID;
		m_iStyleOfPlay = styleOfPlay;
		this.matchId = matchId;
	}

	// ~ Methods
	// ------------------------------------------------------------------------------------

	/**
	 * Setter for property m_vAufstellung.
	 * 
	 * @param m_vAufstellung
	 *            New value of property m_vAufstellung.
	 */
	public final void setAufstellung(Vector<MatchLineupPlayer> m_vAufstellung) {
		this.m_vAufstellung = m_vAufstellung;
	}

	/**
	 * Getter for property m_vAufstellung.
	 * 
	 * @return Value of property m_vAufstellung.
	 */
	public final Vector<MatchLineupPlayer> getAufstellung() {
		return m_vAufstellung;
	}

	/**
	 * Setter for property m_vSubstitutions.
	 * 
	 * @param substitutions
	 *            New value of property m_vSubstitutions.
	 */
	public final void setSubstitutions(List<Substitution> substitutions) {

		// defensive copy
		this.m_vSubstitutions = new ArrayList<Substitution>(substitutions);

		// Make sure substitutions are sorted first on minute, then by ID.
		Collections.sort(this.m_vSubstitutions, new Comparator<Substitution>() {
			@Override
			public int compare(Substitution o1, Substitution o2) {

				if (o1.getMatchMinuteCriteria() == o2.getMatchMinuteCriteria()) {
					if (o1.getPlayerOrderId() == o2.getPlayerOrderId()) {
						return 0;
					}
					if (o1.getPlayerOrderId() < o2.getPlayerOrderId()) {
						return -1;
					}

					return 1;

				}

				if (o1.getMatchMinuteCriteria() < o2.getMatchMinuteCriteria()) {
					return -1;
				}

				// minutes in o1 is greater than o2
				return 1;
			}

			public boolean equals(Substitution o1, Substitution o2) {
				// Lazy solution, a proper would compare all fields, but we
				// don't need that.
				if (o1 == o2) {
					return true;
				} else {
					return false;
				}
			}
		});
	}

	/**
	 * Getter for property m_vSubstitutions.
	 * 
	 * @return Value of property m_vSubstitutions.
	 */
	public final List<Substitution> getSubstitutions() {
		return m_vSubstitutions;
	}

	/**
	 * Setter for property m_iErfahrung.
	 * 
	 * @param m_iErfahrung
	 *            New value of property m_iErfahrung.
	 */
	public final void setErfahrung(int m_iErfahrung) {
		this.m_iErfahrung = m_iErfahrung;
	}

	/**
	 * Getter for property m_iErfahrung.
	 * 
	 * @return Value of property m_iErfahrung.
	 */
	public final int getErfahrung() {
		return m_iErfahrung;
	}

	/**
	 * Returns a player by ID, players in captain and set piece positions are
	 * ignored.
	 * 
	 * @param id
	 *            The spielerId of the player
	 * 
	 * @return The object matching the criteria, or null if none found
	 */
	public final MatchLineupPlayer getPlayerByID(int id) {

		for (MatchLineupPlayer player : m_vAufstellung) {
			if (player.getSpielerId() == id) {
				if ((player.getId() == IMatchRoleID.captain)
						|| (player.getId() == IMatchRoleID.setPieces)) {
					// ignore
				} else {
					return player;
				}
			}
		}

		return null;
	}

	/**
	 * Liefert Einen Player per PositionsID aus der Aufstellung
	 */
	public final MatchLineupPlayer getPlayerByPosition(int id) {
		MatchLineupPlayer player = null;

		for (int i = 0; (m_vAufstellung != null) && (i < m_vAufstellung.size()); i++) {
			player = (MatchLineupPlayer) m_vAufstellung.elementAt(i);

			if (player.getId() == id) {
				return player;
			}
		}
		return NULLPLAYER;
	}

	/**
	 * Setter for property m_iStyleOfPlay.
	 * 
	 * @param m_iStyleOfPlay
	 *            New value of property m_iStyleOfPlay.
	 */
	public final void setStyleOfPlay(int m_iStyleOfPlay) {
		this.m_iStyleOfPlay = m_iStyleOfPlay;
	}

	/**
	 * Getter for property m_iStyleOfPlay.
	 * 
	 * @return Value of property m_iStyleOfPlay.
	 */
	public final int getStyleOfPlay() {
		return m_iStyleOfPlay;
	}
	
	// returns offensive, defensive or neutral depending on styleOfPlay
	// e.g. -3 is 30% defensive, 10 is 100% offensive
	public static String getStyleOfPlayName(int styleOfPlay) {
		HOVerwaltung hov = HOVerwaltung.instance();
		String s;
		
		if (styleOfPlay == 0) {
			return hov.getLanguageString("ls.team.styleofplay.neutral");
		} else {
			s = (styleOfPlay > 0) ? hov.getLanguageString("ls.team.styleofplay.offensive") :
				hov.getLanguageString("ls.team.styleofplay.defensive"); 
		}
		return Math.abs(styleOfPlay * 10) + "% " + s;
	}
	
	/**
	 * Setter for property m_iTeamID.
	 * 
	 * @param m_iTeamID
	 *            New value of property m_iTeamID.
	 */
	public final void setTeamID(int m_iTeamID) {
		this.m_iTeamID = m_iTeamID;
	}

	/**
	 * Getter for property m_iTeamID.
	 * 
	 * @return Value of property m_iTeamID.
	 */
	public final int getTeamID() {
		return m_iTeamID;
	}

	/**
	 * Setter for property m_sTeamName.
	 * 
	 * @param m_sTeamName
	 *            New value of property m_sTeamName.
	 */
	public final void setTeamName(java.lang.String m_sTeamName) {
		this.m_sTeamName = m_sTeamName;
	}

	/**
	 * Getter for property m_sTeamName.
	 * 
	 * @return Value of property m_sTeamName.
	 */
	public final java.lang.String getTeamName() {
		return m_sTeamName;
	}

	public final void add2Aufstellung(MatchLineupPlayer player) {
		m_vAufstellung.add(player);
	}

	/**
	 * Determinates the played formation.
	 */
	public final byte determinateSystem() {
		short abw = 0;
		short mf = 0;
		short st = 0;

		MatchLineupPlayer player = null;

		for (int i = 0; i < m_vAufstellung.size(); i++) {
			player = (MatchLineupPlayer) m_vAufstellung.get(i);

			if (player != null) {
				switch (player.getPosition()) {
				case IMatchRoleID.UNKNOWN:
					break;

				case IMatchRoleID.BACK:
				case IMatchRoleID.BACK_TOMID:
				case IMatchRoleID.BACK_OFF:
				case IMatchRoleID.BACK_DEF:
				case IMatchRoleID.CENTRAL_DEFENDER:
				case IMatchRoleID.CENTRAL_DEFENDER_TOWING:
				case IMatchRoleID.CENTRAL_DEFENDER_OFF:
					abw++;
					break;

				case IMatchRoleID.MIDFIELDER:
				case IMatchRoleID.MIDFIELDER_OFF:
				case IMatchRoleID.MIDFIELDER_DEF:
				case IMatchRoleID.MIDFIELDER_TOWING:
				case IMatchRoleID.WINGER:
				case IMatchRoleID.WINGER_TOMID:
				case IMatchRoleID.WINGER_OFF:
				case IMatchRoleID.WINGER_DEF:
					mf++;
					break;

				case IMatchRoleID.FORWARD:
				case IMatchRoleID.FORWARD_TOWING:
				case IMatchRoleID.FORWARD_DEF:
					st++;
					break;
				}
			}
		}

		if (abw == 2) {
			// 253
			if (mf == 5) {
				return Lineup.SYS_253;
			}
			// MURKS
			else {
				return Lineup.SYS_MURKS;
			}
		} else if (abw == 3) {
			// 343
			if (mf == 4) {
				return Lineup.SYS_343;
			} // 352
			else if ((mf == 5) && (st == 2)) {
				return Lineup.SYS_352;
			}
			// MURKS
			else {
				return Lineup.SYS_MURKS;
			}
		} else if (abw == 4) {
			// 433
			if ((mf == 3) && (st == 3)) {
				return Lineup.SYS_433;
			} // 442
			else if ((mf == 4) && (st == 2)) {
				return Lineup.SYS_442;
			} // 451
			else if ((mf == 5) && (st == 1)) {
				return Lineup.SYS_451;
			}
			// MURKS
			else {
				return Lineup.SYS_MURKS;
			}
		} else if (abw == 5) {
			// 532
			if ((mf == 3) && (st == 2)) {
				return Lineup.SYS_532;
			} // 541
			else if ((mf == 4) && (st == 1)) {
				return Lineup.SYS_541;
			} // 523
			else if ((mf == 2) && (st == 3)) {
				return Lineup.SYS_523;
			} // 550
			else if ((mf == 5) && (st == 0)) {
				return Lineup.SYS_550;
			}
			// MURKS
			else {
				return Lineup.SYS_MURKS;
			}
		} // MURKS
		else {
			return Lineup.SYS_MURKS;
		}
	}

	public MatchType getMatchType() {
		if (matchType == MatchType.NONE) {
			MatchKurzInfo info = DBManager.instance().getMatchesKurzInfoByMatchID(this.matchId);
			if ( info != null){
				matchType = info.getMatchType();
			}
		}
		return matchType;
	}

	public SourceSystem getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(SourceSystem sourceSystem) {
		this.sourceSystem = sourceSystem;
	}
}
