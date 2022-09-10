package core.model.match;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.enums.MatchType;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.util.HOLogger;
import module.lineup.Lineup;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.Substitution;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static core.model.player.MatchRoleID.Sector.None;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class MatchLineupTeam {

	/*
	* usages:
	* 1. in match details of played matches
	* 2. match orders of upcoming matches
	* 3. user stored lineups (matchId = -1, matchType=None, teamId=count (starting with -1 and decremented by 1)
	 */

	/**
	 * match id
	 * in case of played matches or match orders
	 * -1 for user stored lineups
	 */
	private int matchId;
	/**
	 * lineup Id
	 * equal to team Id in played matches or match orders
	 * negative counter in user stored lineups
	 */
	private int teamId;
	/**
	 * match type
	 * in case of played matches or match orders
	 */
	private MatchType matchType;

	/**
	 * team name
	 * in case of played matches or match orders (own team name)
	 * lineup name edited by user in case of user stored matches
	 */
	private String teamName;
	private Lineup lineup;
	private int experience;

	// null player to fill empty spots
	private Matchdetails matchdetails;

	// ~ Constructors
	// -------------------------------------------------------------------------------

	/**
	 * Creates a new instance of MatchLineupTeam
	 */
	public MatchLineupTeam(MatchType matchType, int matchId, String teamName, int teamID, int erfahrung) {
		this.matchType = matchType;
		this.teamName = teamName;
		experience = erfahrung;
		teamId = teamID;
		this.matchId = matchId;
		this.lineup = new Lineup();
	}

	public MatchLineupTeam(){
		this(MatchType.NONE, -1, "", -1, 0);
	}

	public MatchLineupTeam(Properties properties) {
		this.matchType = MatchType.getById(Integer.parseInt(properties.getProperty("matchtyp", "0")));
		this.matchId = Integer.parseInt(properties.getProperty("matchid", "-1"));
		this.teamId = Integer.parseInt(properties.getProperty("teamid", "-1"));
		this.teamName = properties.getProperty("name", "");
		this.lineup=new Lineup(properties);
	}

	// ~ Methods
	// ------------------------------------------------------------------------------------

	/**
	 * Setter for property lineup.
	 * 
	 * @param lineup new Lineup
	 */
	public final void setLineup(Lineup lineup) {
		this.lineup = lineup;
		resetMinutesOfPlayersInSectors();
	}

	/**
	 * Getter for property m_vAufstellung.
	 * 
	 * @return Value of property m_vAufstellung.
	 */
	public final @NotNull Lineup getLineup() {
		if ( lineup == null){
			loadLineup();
			if ( lineup == null){
				lineup = new Lineup(); // create empty lineup
			}
		}
		return lineup;
	}

	/**
	 * Setter for property m_vSubstitutions.
	 * 
	 * @param substitutions
	 *            New value of property m_vSubstitutions.
	 */
	public final void setSubstitutions(List<Substitution> substitutions) {

		var ordered = new ArrayList<>(substitutions);

		// Make sure substitutions are sorted first on minute, then by ID.
		ordered.sort(new Comparator<Substitution>() {
			@Override
			public int compare(Substitution o1, Substitution o2) {

				if (o1.getMatchMinuteCriteria() == o2.getMatchMinuteCriteria()) {
					return Integer.compare(o1.getPlayerOrderId(), o2.getPlayerOrderId());

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
				return o1 == o2;
			}
		});

		this.lineup.setSubstitionList(ordered);
		resetMinutesOfPlayersInSectors();
	}

	/**
	 * Getter for property m_vSubstitutions.
	 * 
	 * @return Value of property m_vSubstitutions.
	 */
	public final List<Substitution> getSubstitutions() {
		return this.lineup.getSubstitutionList();
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
	 * @param playerId
	 *            The spielerId of the player
	 * 
	 * @return The object matching the criteria, or null if none found
	 */
	public final MatchLineupPosition getPlayerByID(int playerId, boolean includeReplacedPlayers) {
		return this.lineup.getPositionByPlayerId(playerId, includeReplacedPlayers);
	}
	public final MatchLineupPosition getPlayerByID(int playerId) {
		return getPlayerByID(playerId, true);
	}

	/**
	 * Liefert Einen Player per PositionsID aus der Aufstellung
	 */
	public final MatchLineupPosition getPlayerByPosition(int roleId) {
		return this.lineup.getPositionById(roleId);
	}

	/**
	 * Setter for property m_iStyleOfPlay.
	 * 
	 * @param m_iStyleOfPlay
	 *            New value of property m_iStyleOfPlay.
	 */
	public final void setStyleOfPlay(StyleOfPlay m_iStyleOfPlay) {
		this.lineup.setStyleOfPlay(StyleOfPlay.toInt(m_iStyleOfPlay));
	}

	public void calcStyleOfPlay () {
		var trainerType = HOVerwaltung.instance().getModel().getTrainer().getTrainerTyp();
		var tacticAssistants = HOVerwaltung.instance().getModel().getClub().getTacticalAssistantLevels();
		var styleOfPlay = this.lineup.getStyleOfPlay();
		switch (trainerType) {
			case Defensive:
				this.lineup.setStyleOfPlay(min(-10 + 2 * tacticAssistants, styleOfPlay));
				break;
			case Offensive:
				this.lineup.setStyleOfPlay(max(10 - 2 * tacticAssistants, styleOfPlay));
				break;
			default:
			case Balanced:
				if (styleOfPlay >= 0) {
					this.lineup.setStyleOfPlay(min(tacticAssistants, styleOfPlay));
				} else {
					this.lineup.setStyleOfPlay(max(tacticAssistants, styleOfPlay));
				}
				break;
		}
	}

	/**
	 * Getter for property m_iStyleOfPlay.
	 * 
	 * @return Value of property m_iStyleOfPlay.
	 */
	public final StyleOfPlay getStyleOfPlay() {
		return StyleOfPlay.fromInt(this.lineup.getStyleOfPlay());
	}
	
	// returns offensive, defensive or neutral depending on styleOfPlay
	// e.g. -3 is 30% defensive, 10 is 100% offensive
	public static String getStyleOfPlayName(StyleOfPlay styleOfPlay) {
		HOVerwaltung hov = HOVerwaltung.instance();
		String s;
		var style = StyleOfPlay.toInt(styleOfPlay);
		if (style == 0) {
			return hov.getLanguageString("ls.team.styleofplay.neutral");
		} else {
			s = (style > 0) ? hov.getLanguageString("ls.team.styleofplay.offensive") :
				hov.getLanguageString("ls.team.styleofplay.defensive"); 
		}
		return Math.abs(style * 10) + "% " + s;
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

	public final void add2Lineup(MatchLineupPosition player) {
		lineup.setPosition(player);
	}

	/**
	 * Determines the played formation.
	 */
	public final byte determineSystem() {
		short abw = 0;
		short mf = 0;
		short st = 0;

		for (var matchRoleId : getLineup().getFieldPositions()) {
			if (matchRoleId != null) {
				switch (matchRoleId.getSector()) {
					default -> {
					}
					case CentralDefence, Back -> abw++;
					case InnerMidfield, Wing -> mf++;
					case Forward -> st++;
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
			MatchKurzInfo info = DBManager.instance().getMatchesKurzInfoByMatchID(this.matchId, matchType);
			if ( info != null){
				matchType = info.getMatchType();
			}
		}
		return matchType;
	}

	public int getTrainingMinutesPlayedInSectors(int playerId, List<MatchRoleID.Sector> acceptedSectors, boolean isWalkoverMatchWin) {
		initMinutesOfPlayersInSectors();
		if (acceptedSectors != null && acceptedSectors.size() == 0) return 0; // No sectors are accepted
		var player = this.getPlayerByID(playerId);
		if (player == null) return 0;
		if (isWalkoverMatchWin) {
			if (acceptedSectors != null &&
					(acceptedSectors.contains(MatchRoleID.getSector(player.getStartPosition())) ||
							acceptedSectors.contains(MatchRoleID.Sector.SetPiecesTaker) && player.isStartSetPiecesTaker())) {
				// Normal training
				return 90;
			}
			return 0; // no experience (especially if acceptedSectors==null)
		}
		return getTrainingMinutesInAcceptedSectors(playerId, acceptedSectors);
	}

	public int getTrainMinutesPlayedInPositions(int spielerId, int[] accepted, boolean isWalkoverMatchWin) {
		if ( accepted!=null && accepted.length==0) return 0;	// NO positions are accepted
		MatchLineupPosition player = this.getPlayerByID(spielerId);
		if (player == null) {
			return 0;
		}

		boolean inPosition = false;

		int enterMin = -1;
		int minPlayed = 0;
		// Those in the starting lineup entered at minute 0
		/*
		 TODO this is not correct if calculation is called for bonus positions in case of set pieces training
		 then accepted positions are set pieces taker and goalkeeper. set pieces taker is never returned by
		 getStartPosition!
		 We should loop the other way round: test each accepted position, whether is was taken by the specified
		 player
		 */
		if (isPositionInAcceptedPositions(player.getStartPosition(), accepted)) {
			if (isWalkoverMatchWin) {
				// Opponent team did not appear
				if (accepted != null) {
					// Normal training
					return 90;
				}
				// No experience
				return 0;
			}

			enterMin = 0;
			inPosition = true;
		}

		List<Substitution> substitutions = this.getSubstitutions();
		// The substitutions are sorted on minute. Look for substitutions
		// involving the player, and check his position
		// after the substitution (on the substitution minute). Work through the
		// list, and add minutes depending on
		// entering/leaving the accepted position list.
		for (Substitution substitution : substitutions) {
			if (substitution == null) {
				HOLogger.instance().debug(getClass(),
						"getMinutesPlayedError, null in substitution list");
				break;
			}

			if (substitution.getOrderType() == MatchOrderType.SUBSTITUTION ||
					substitution.getOrderType() == MatchOrderType.POSITION_SWAP) {
				if ((substitution.getObjectPlayerID() == spielerId) || (substitution.getSubjectPlayerID() == spielerId)) {
					int newpos = getPlayerFieldPositionAtMinute(spielerId, substitution.getMatchMinuteCriteria());
					boolean newPosAccepted =  isPositionInAcceptedPositions(newpos, accepted);
					if (inPosition && !newPosAccepted) {
						// He left a counting position.
						minPlayed += substitution.getMatchMinuteCriteria() - enterMin;
						inPosition = false;
					} else if (!inPosition && newPosAccepted) {
						// He entered a counting position
						enterMin = substitution.getMatchMinuteCriteria();
						inPosition = true;
					}
				}
			}
		}
		// Done with substitutions, add end if necessary
		if (inPosition) {
			minPlayed += getMatchEndMinute(spielerId) - enterMin;
		}

		return minPlayed;
	}

	public int getPlayerFieldPositionAtMinute(int spielerId, int minute) {
		// Captain and set piece taker don't count...
		if ((minute >= getMatchEndMinute(spielerId)) || (minute < 0)) {
			// The player is at home (they travel fast)...
			return -1;
		}

		var player = this.getPlayerByID(spielerId);
		if ( player == null) {
			// Was never on the field
			return -1;
		}

		int ret = player.getStartPosition();
		HashMap<Integer, Integer> changedPositions = new HashMap<>();	// player id, new Position
		for ( var subs : this.getSubstitutions()){
			if ( subs.getMatchMinuteCriteria() > minute) break;
			if ( subs.getOrderType() == MatchOrderType.POSITION_SWAP || subs.getOrderType() == MatchOrderType.SUBSTITUTION){
				var subPos = changedPositions.get(subs.getSubjectPlayerID());
				if ( subPos == null ) {
					var matchLineupPosition = this.getPlayerByID(subs.getSubjectPlayerID());
					if ( matchLineupPosition != null) {
						subPos = matchLineupPosition.getStartPosition();
					}
				}
				var objPos = changedPositions.get(subs.getObjectPlayerID());
				if ( objPos == null){
					var matchLineupPosition = this.getPlayerByID(subs.getObjectPlayerID());
					if (matchLineupPosition != null) {
						objPos = matchLineupPosition.getStartPosition();
					}
				}
				changedPositions.put(subs.getSubjectPlayerID(), objPos);
				changedPositions.put(subs.getObjectPlayerID(), subPos);
				if ( spielerId == subs.getSubjectPlayerID()){
					if ( objPos != null ) ret = objPos;
				}
				else if ( spielerId == subs.getObjectPlayerID()){
					if ( subPos != null ) ret = subPos;
				}
			}
		}
		return ret;
	}

	public int getMatchEndMinute(int spielerId) {
		var hls = getMatchdetails().downloadHighlightsIfMissing(); // DBManager.instance().getMatchDetails(matchId).downloadHighlightsIfMissing();
		if ( hls != null ) {
			for (MatchEvent hl : hls) {
				MatchEvent.MatchEventID me = MatchEvent.MatchEventID.fromMatchEventID(hl.getiMatchEventID());
				if (me == MatchEvent.MatchEventID.MATCH_FINISHED ||
						me == MatchEvent.MatchEventID.PENALTY_CONTEST_AFTER_EXTENSION) {
					return hl.getMinute();
				} else if (hl.getPlayerId() == spielerId) {
					if (hl.isInjured() || hl.isRedCard()) {
						return hl.getMinute();
					}
				}
			}
		}
		return 0;
	}

	private Matchdetails getMatchdetails() {
		if ( matchdetails == null){
			matchdetails = DBManager.instance().loadMatchDetails(this.getMatchType().getId(), this.matchId);
		}
		return matchdetails;
	}

	private boolean isPositionInAcceptedPositions(int pos, int[] accepted) {
		if (accepted == null)
			return MatchRoleID.isFieldMatchRoleId(pos); // all positions are accepted, use an empty array if NO position should be accepted

		for (int value : accepted) {
			if (value == pos) {
				return true;
			}
		}
		return false;
	}

	public void setMatchDetails(Matchdetails details) {
		this.matchdetails = details;
	}

	public Map<MatchRoleID.Sector, Integer> getTrainMinutesPlayedInSectors(int playerId) {
		var ret = new HashMap<MatchRoleID.Sector, Integer>();
		var player = this.getPlayerByID(playerId, true);
		if (player == null) return ret;

		int startminute = 0;
		int sumMinutes = 0;

		MatchRoleID.Sector sector;
		var startPosition = player.getStartPosition();
		if (startPosition != -1) {
			sector = MatchRoleID.getSector(startPosition);
		} else {
			sector = None;
		}

		HashMap<Integer, MatchRoleID.Sector> changedPositions = new HashMap<>();    // player id, new Position
		for (var subs : this.getSubstitutions()) {
			if (subs.getOrderType() == MatchOrderType.POSITION_SWAP || subs.getOrderType() == MatchOrderType.SUBSTITUTION) {
				var subSector = changedPositions.get(subs.getSubjectPlayerID());
				if (subSector == null) {
					var sPos = this.getPlayerByID(subs.getSubjectPlayerID(),true);
					if ( sPos != null){
						subSector = MatchRoleID.getSector(sPos.getStartPosition());
					}
					else {
						continue;
					}
				}
				var objSector = changedPositions.get(subs.getObjectPlayerID());
				if (objSector == null) {
					var oPos= this.getPlayerByID(subs.getObjectPlayerID(), true);
					if ( oPos != null){
						objSector = MatchRoleID.getSector(oPos.getStartPosition());
					}
					else {
						continue;
					}
				}
				if (subSector != objSector) {
					changedPositions.put(subs.getSubjectPlayerID(), objSector);
					changedPositions.put(subs.getObjectPlayerID(), subSector);
					if (playerId == subs.getSubjectPlayerID() || playerId == subs.getObjectPlayerID()) {
						if (sector != None) {
							var minutesInSector = subs.getMatchMinuteCriteria() - startminute;
							sumMinutes += minutesInSector;
							if (sumMinutes > 90) {
								minutesInSector -= (sumMinutes - 90);
							}
							if (minutesInSector > 0) {
								ret.put(sector, minutesInSector);
							}
						}
						if (playerId == subs.getSubjectPlayerID()) {
							sector = objSector;
						} else if (playerId == subs.getObjectPlayerID()) {
							sector = subSector;
						}
						startminute = subs.getMatchMinuteCriteria();
					}
				}
				if (sumMinutes >= 90) break;
			}
		}

		if (sector != None && sumMinutes < 90) {
			var minutesInSector = this.getMatchEndMinute(playerId) - startminute;
			sumMinutes += minutesInSector;
			if (sumMinutes > 90) {
				minutesInSector -= (sumMinutes - 90);
			}
			if (minutesInSector > 0) {
				ret.put(sector, minutesInSector);
			}
		}
		return ret;
	}

	public boolean hasPlayerPlayed(int playerId) {
		return this.getTrainMinutesPlayedInSectors(playerId).size() > 0;
	}

	/*
	 * Mapping of role id to match appearance, consisting of player and minute
	 * Setting lineup or substitutions will initialize last match appearances
	 */
	private HashMap<Integer, MatchAppearance> lastMatchAppearances;
	private void resetMinutesOfPlayersInSectors() {
		lastMatchAppearances = null;
	}

	private HashMap<Integer, HashMap<MatchRoleID.Sector, Integer>> playersMinutesInSector;

	private void addPlayersMinutesInSector(int playerId, MatchRoleID.Sector sector, int minutes){
		if (playersMinutesInSector == null) {
			playersMinutesInSector = new HashMap<>();
		}

		var minutesInSector = playersMinutesInSector.get(playerId);
		if ( minutesInSector == null){
			minutesInSector = new HashMap<>();
			minutesInSector.put(sector, minutes);
			playersMinutesInSector.put(playerId, minutesInSector);
		}
		else {
			minutesInSector.merge(sector, minutes, Integer::sum);
		}
	}

	private void initMinutesOfPlayersInSectors() {
		if (this.lineup == null) return;    // init in progress
		if (lastMatchAppearances != null) return; /// already done

		// Start init
		this.playersMinutesInSector = new HashMap<>();
		lastMatchAppearances = new HashMap<>();
		// get the starting positions
		var allActivePlayers = new Vector<MatchLineupPosition>();
		allActivePlayers.addAll(this.lineup.getFieldPositions());
		// add start positions of replaced players
		allActivePlayers.addAll(this.lineup.getReplacedPositions());
		for (var iMatchRole : allActivePlayers) {
			if (iMatchRole.getStartPosition() >= 0) {
				lastMatchAppearances.put(iMatchRole.getStartPosition(), new MatchAppearance(iMatchRole, 0));
			}
			if (iMatchRole.isStartSetPiecesTaker()) {
				lastMatchAppearances.put(IMatchRoleID.setPieces, new MatchAppearance(iMatchRole, 0));
			}
		}

		// examine the substitutions
		for (Substitution i : this.getSubstitutions()) {
			if (i.getOrderType() != MatchOrderType.MAN_MARKING) {
				examineSubstitution(i);
			}
		}

		// examine last minutes
		for (var app : lastMatchAppearances.entrySet()) {
			var player = app.getValue().player;
			//player.addMinutesInSector(getMatchEndMinute(player.getPlayerId())-app.getValue().minute, app.getKey());
			addPlayersMinutesInSector(player.getPlayerId(),
					app.getValue().getSector(),
					getMatchEndMinute(player.getPlayerId()) - app.getValue().minute);
		}
	}

	private void examineSubstitution(Substitution substitution) {
		var leavingplayer = this.getPlayerByID(substitution.getSubjectPlayerID(),true);
		if ( leavingplayer == null){
			leavingplayer = new MatchLineupPosition(substitution.getRoleId(), substitution.getSubjectPlayerID(), substitution.getBehaviour());
		}
		switch (substitution.getOrderType()) {
			case NEW_BEHAVIOUR -> {
				removeMatchAppearance(leavingplayer, substitution.getMatchMinuteCriteria());
				if ( substitution.getRoleId() > 0) {
					lastMatchAppearances.put((int) substitution.getRoleId(), new MatchAppearance(leavingplayer, substitution.getMatchMinuteCriteria()));
				}
			}
			case SUBSTITUTION -> {
				var setPiecesTaker = lastMatchAppearances.get(MatchRoleID.setPieces);
				var leavingPlayerIsSetPiecesTaker = setPiecesTaker != null && setPiecesTaker.player == leavingplayer;
				removeMatchAppearance(leavingplayer, substitution.getMatchMinuteCriteria());
				var enteringplayer = this.getPlayerByID(substitution.getObjectPlayerID(), true);
				if ( enteringplayer == null){
					enteringplayer = new MatchLineupPosition(substitution.getRoleId(), substitution.getObjectPlayerID(), substitution.getBehaviour());
				}
				lastMatchAppearances.put((int) substitution.getRoleId(), new MatchAppearance(enteringplayer, substitution.getMatchMinuteCriteria()));
				if (leavingPlayerIsSetPiecesTaker) {
					// Find the new set pieces taker
					var matchEvents = this.matchdetails.downloadHighlightsIfMissing().stream()
							.filter(i -> i.getMatchEventID() == MatchEvent.MatchEventID.NEW_SET_PIECES_TAKER &&
									i.getMinute() == substitution.getMatchMinuteCriteria()).toList();
					for (var event : matchEvents) {
						var newSetPiecesTaker = this.getPlayerByID(event.getAssistingPlayerId(), true);
						lastMatchAppearances.put(MatchRoleID.setPieces, new MatchAppearance(newSetPiecesTaker, substitution.getMatchMinuteCriteria()));
					}
				}
			}
			case POSITION_SWAP -> {
				var player = this.getPlayerByID(substitution.getObjectPlayerID(), true);
				var leavingRole = removeMatchAppearance(leavingplayer, substitution.getMatchMinuteCriteria());
				var playerRole = removeMatchAppearance(player, substitution.getMatchMinuteCriteria());
				lastMatchAppearances.put(playerRole, new MatchAppearance(leavingplayer, substitution.getMatchMinuteCriteria()));
				lastMatchAppearances.put(leavingRole, new MatchAppearance(player, substitution.getMatchMinuteCriteria()));
			}
		}
	}

	private int removeMatchAppearance(MatchLineupPosition leavingplayer, int minute) {
		int ret = MatchRoleID.UNKNOWN;
		var entries = lastMatchAppearances.entrySet().stream()
				.filter(i -> i.getValue().getPlayerId() == leavingplayer.getPlayerId()).toList();
		for (var entry : entries) {
			var appearance = entry.getValue();
			var role = entry.getKey();
			if (role != MatchRoleID.setPieces) { // do not return set pieces taker role of leaving player
				ret = role;
			}
			//leavingplayer.addMinutesInSector(minute-appearance.minute, role);
			addPlayersMinutesInSector(leavingplayer.getPlayerId(), MatchRoleID.getSector(role), minute - appearance.minute);
			lastMatchAppearances.remove(entry.getKey());
		}
		return ret;
	}

	public MatchTacticType getMatchTacticType() {
		return MatchTacticType.fromInt(this.lineup.getTacticType());
	}

	public void setMatchTacticType(MatchTacticType matchTacticType) {
		this.lineup.setTacticType(MatchTacticType.toInt(matchTacticType));
	}

	public MatchTeamAttitude getMatchTeamAttitude() {
		return MatchTeamAttitude.fromInt(this.lineup.getAttitude());
	}

	public void setMatchTeamAttitude(MatchTeamAttitude matchTeamAttitude) {
		this.lineup.setAttitude(MatchTeamAttitude.toInt(matchTeamAttitude));
	}

	public void loadLineup() {
		var players = DBManager.instance().getMatchLineupPlayers(this.matchId, this.matchType, this.teamId);
		var substitutions = DBManager.instance().getMatchSubstitutionsByMatchTeam(this.matchId, this.matchType, this.teamId);
		this.lineup = new Lineup(players, substitutions);
		resetMinutesOfPlayersInSectors();
	}

	public int getMatchId() {
		return this.matchId;
	}

	private class MatchAppearance {
		private int minute;
		private MatchLineupPosition player;

		public MatchAppearance(MatchLineupPosition player, int i) {
			this.minute=i;
			this.player=player;
		}

		public int getPlayerId() {
			return player.getPlayerId();
		}
		public MatchRoleID.Sector getSector() {return player.getSector();}
	}

	public int getTrainingMinutesInAcceptedSectors(int playerId, List<MatchRoleID.Sector> accepted){
		int ret = 0;
		var minutesInSectors = playersMinutesInSector.get(playerId);
		if ( minutesInSectors != null) {
			for (var sector : minutesInSectors.entrySet()) {
				if (accepted==null  || accepted.contains(sector.getKey())) {
					ret += sector.getValue();
					if (ret > 90) {
						ret = 90;
						break;
					}
				}
			}
		}
		return ret;
	}


	public class SectorAppearance {
		MatchRoleID.Sector sector;
		private int minutes;

		public SectorAppearance(int minutes, MatchRoleID.Sector sector) {
			this.sector=sector;
			this.minutes=minutes;
		}

		public int getMinutes(){return minutes;}
		public MatchRoleID.Sector getSector(){return sector;}
	}

}
