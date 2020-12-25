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
	private String teamName;
	private Vector<MatchLineupPlayer> startingLineup = new Vector<>();
	private ArrayList<Substitution> substitutions = new ArrayList<>();
	private int experience;
	private int teamId;
	private int styleOfPlay;
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
		this.teamName = teamName;
		experience = erfahrung;
		teamId = teamID;
		this.styleOfPlay = styleOfPlay;
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
	public final void setStartingLineup(Vector<MatchLineupPlayer> m_vAufstellung) {
		this.startingLineup = m_vAufstellung;
	}

	/**
	 * Getter for property m_vAufstellung.
	 * 
	 * @return Value of property m_vAufstellung.
	 */
	public final Vector<MatchLineupPlayer> getStartingLineup() {
		return startingLineup;
	}

	/**
	 * Setter for property m_vSubstitutions.
	 * 
	 * @param substitutions
	 *            New value of property m_vSubstitutions.
	 */
	public final void setSubstitutions(List<Substitution> substitutions) {

		// defensive copy
		this.substitutions = new ArrayList<Substitution>(substitutions);

		// Make sure substitutions are sorted first on minute, then by ID.
		Collections.sort(this.substitutions, new Comparator<Substitution>() {
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
		return substitutions;
	}

	/**
	 * Setter for property m_iErfahrung.
	 * 
	 * @param m_iErfahrung
	 *            New value of property m_iErfahrung.
	 */
	public final void setExperience(int m_iErfahrung) {
		this.experience = m_iErfahrung;
	}

	/**
	 * Getter for property m_iErfahrung.
	 * 
	 * @return Value of property m_iErfahrung.
	 */
	public final int getExperience() {
		return experience;
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

		for (MatchLineupPlayer player : startingLineup) {
			if (player.getPlayerId() == id) {
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

		for (int i = 0; (startingLineup != null) && (i < startingLineup.size()); i++) {
			player = (MatchLineupPlayer) startingLineup.elementAt(i);

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
		this.styleOfPlay = m_iStyleOfPlay;
	}

	/**
	 * Getter for property m_iStyleOfPlay.
	 * 
	 * @return Value of property m_iStyleOfPlay.
	 */
	public final int getStyleOfPlay() {
		return styleOfPlay;
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
		this.teamId = m_iTeamID;
	}

	/**
	 * Getter for property m_iTeamID.
	 * 
	 * @return Value of property m_iTeamID.
	 */
	public final int getTeamID() {
		return teamId;
	}

	/**
	 * Setter for property m_sTeamName.
	 * 
	 * @param m_sTeamName
	 *            New value of property m_sTeamName.
	 */
	public final void setTeamName(java.lang.String m_sTeamName) {
		this.teamName = m_sTeamName;
	}

	/**
	 * Getter for property m_sTeamName.
	 * 
	 * @return Value of property m_sTeamName.
	 */
	public final java.lang.String getTeamName() {
		return teamName;
	}

	public final void add2StartingLineup(MatchLineupPlayer player) {
		startingLineup.add(player);
	}

	/**
	 * Determines the played formation.
	 */
	public final byte determineSystem() {
		short abw = 0;
		short mf = 0;
		short st = 0;

		MatchLineupPlayer player = null;

		for (int i = 0; i < startingLineup.size(); i++) {
			player = (MatchLineupPlayer) startingLineup.get(i);

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

    public int getMinutesInPositions(int playerId, int[] positions) {
		/// TODO
		return 0;
    }
}
