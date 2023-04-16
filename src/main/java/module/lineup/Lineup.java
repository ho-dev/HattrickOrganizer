package module.lineup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.Ratings;
import core.model.Team;
import core.model.match.*;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.rating.RatingPredictionManager;
import core.util.HOLogger;
import core.util.StringUtils;
import module.lineup.assistant.LineupAssistant;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.RedCardCriteria;
import module.lineup.substitution.model.Substitution;

import java.util.*;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class Lineup{

	public static final byte SYS_433 = 0;
	public static final byte SYS_442 = 1;
	public static final byte SYS_532 = 2;
	public static final byte SYS_541 = 3;
	public static final byte SYS_352 = 4;
	public static final byte SYS_343 = 5;
	public static final byte SYS_451 = 6;
	public static final byte SYS_MURKS = 7; // unknown / invalid system
	public static final byte SYS_523 = 8;
	public static final byte SYS_550 = 9;
	public static final byte SYS_253 = 10;

	// TODO: remove assistant from Lineup class
	/** Aufstellungsassistent */
	private final LineupAssistant m_clAssi = new LineupAssistant();

	/** positions */
	@SerializedName("positions")
	@Expose
	private Vector<MatchLineupPosition> m_vFieldPositions = new Vector<>();
	/** bench */
	@SerializedName("bench")
	@Expose
	private Vector<MatchLineupPosition> m_vBenchPositions = new Vector<>();
	@SerializedName("substitutions")
	@Expose
	private List<Substitution> substitutions = new ArrayList<>();
	@SerializedName("kickers")
	@Expose
	private Vector<MatchLineupPosition> penaltyTakers = new Vector<>();

	/** captain */
	@SerializedName("captain")
	@Expose
	private int m_iKapitaen = -1;

	/** set pieces take */
	@SerializedName("setPieces")
	@Expose
	private int m_iKicker = -1;

	private Vector<MatchLineupPosition> replacedPositions = new Vector<>();
	MatchLineupPosition captain;
	MatchLineupPosition setPiecesTaker;
	private Player.ManMarkingPosition manMarkingPosition;

	private void setCaptain(MatchLineupPosition position) {
		this.captain = position;
		this.m_iKapitaen = position.getPlayerId();
	}

	private void setSetPiecesTaker(MatchLineupPosition position) {
		this.setPiecesTaker = position;
		this.m_iKicker = position.getPlayerId();
	}

	public Player.ManMarkingPosition getManMarkingPosition() {
		return this.manMarkingPosition;
	}

	public void setManMarkingPosition(Player.ManMarkingPosition manMarkingPosition) {
		this.manMarkingPosition = manMarkingPosition;
	}

	public void setPlayers(List<MatchLineupPosition> matchLineupPositions) {
		initPositionen553(); // reset all
		for (var position : matchLineupPositions){
			setPosition(position);
		}
	}

	private static class Settings {
		/** Attitude */
		@SerializedName("speechLevel")
		@Expose
		private int m_iAttitude;

		/** TacticType */
		@SerializedName("tactic")
		@Expose
		private int m_iTacticType;

		/** Style of play */
		@SerializedName("coachModifier")
		@Expose
		private int m_iStyleOfPlay;

		//NOTE: newLineup is required by HT - do not delete even if it seems unused !
		@SerializedName("newLineup")
		@Expose
		private String newLineup = "";

	}

	@SerializedName("settings")
	@Expose
	Settings settings = new Settings();

	/** PullBackMinute **/
	private int pullBackMinute = 90; // no pull back

	// TODO -> MatchKurzInfo
	/** Home/Away/AwayDerby */
	private short m_sLocation = -1;

	private int m_iArenaId = -1;
	private int m_iRegionId = -1;
	private Weather m_cWeather = Weather.NULL;
	private Weather.Forecast m_cWeatherForecast = Weather.Forecast.NULL;


	private Ratings oRatings;

	// ~ Constructors
	// -------------------------------------------------------------------------------

	/**
	 * Creates a new Aufstellung object.
	 */
	public Lineup() {
		initPositionen553();
	}

	/**
	 * Creates a new instance of Lineup
	 * <p>
	 * Probably up for change with new XML?
	 */
	public Lineup(Properties properties) {
		try {
			// Positionen erzeugen
			m_vFieldPositions.add(new MatchLineupPosition(IMatchRoleID.keeper, Integer
					.parseInt(properties.getProperty("keeper", "0")), (byte) 0));
			m_vFieldPositions.add(new MatchLineupPosition(IMatchRoleID.rightBack, Integer
					.parseInt(properties.getProperty("rightback", "0")), Byte.parseByte(properties
					.getProperty("order_rightback", "0"))));
			m_vFieldPositions.add(new MatchLineupPosition(IMatchRoleID.rightCentralDefender, Integer
					.parseInt(properties.getProperty("rightcentraldefender", "0")), Byte
					.parseByte(properties.getProperty("order_rightcentraldefender", "0"))));
			m_vFieldPositions.add(new MatchLineupPosition(IMatchRoleID.middleCentralDefender, Integer
					.parseInt(properties.getProperty("middlecentraldefender", "0")), Byte
					.parseByte(properties.getProperty("order_middlecentraldefender", "0"))));
			m_vFieldPositions.add(new MatchLineupPosition(IMatchRoleID.leftCentralDefender, Integer
					.parseInt(properties.getProperty("leftcentraldefender", "0")), Byte
					.parseByte(properties.getProperty("order_leftcentraldefender", "0"))));
			m_vFieldPositions.add(new MatchLineupPosition(IMatchRoleID.leftBack, Integer
					.parseInt(properties.getProperty("leftback", "0")), Byte.parseByte(properties
					.getProperty("order_leftback", "0"))));
			m_vFieldPositions.add(new MatchLineupPosition(IMatchRoleID.rightWinger, Integer
					.parseInt(properties.getProperty("rightwinger", "0")), Byte
					.parseByte(properties.getProperty("order_rightwinger", "0"))));
			m_vFieldPositions.add(new MatchLineupPosition(IMatchRoleID.rightInnerMidfield, Integer
					.parseInt(properties.getProperty("rightinnermidfield", "0")), Byte.parseByte(properties
					.getProperty("order_rightinnermidfield", "0"))));
			m_vFieldPositions.add(new MatchLineupPosition(IMatchRoleID.centralInnerMidfield, Integer
					.parseInt(properties.getProperty("middleinnermidfield", "0")), Byte.parseByte(properties
					.getProperty("order_centralinnermidfield", "0"))));
			m_vFieldPositions.add(new MatchLineupPosition(IMatchRoleID.leftInnerMidfield, Integer
					.parseInt(properties.getProperty("leftinnermidfield", "0")), Byte.parseByte(properties
					.getProperty("order_leftinnermidfield", "0"))));
			m_vFieldPositions.add(new MatchLineupPosition(IMatchRoleID.leftWinger, Integer
					.parseInt(properties.getProperty("leftwinger", "0")), Byte.parseByte(properties
					.getProperty("order_leftwinger", "0"))));
			m_vFieldPositions.add(new MatchLineupPosition(IMatchRoleID.rightForward, Integer
					.parseInt(properties.getProperty("rightforward", "0")), Byte.parseByte(properties
					.getProperty("order_rightforward", "0"))));
			m_vFieldPositions.add(new MatchLineupPosition(IMatchRoleID.centralForward, Integer
					.parseInt(properties.getProperty("centralforward", "0")), Byte.parseByte(properties
					.getProperty("order_centralforward", "0"))));
			m_vFieldPositions.add(new MatchLineupPosition(IMatchRoleID.leftForward, Integer
					.parseInt(properties.getProperty("leftforward", "0")), Byte.parseByte(properties
					.getProperty("order_leftforward", "0"))));

			m_vBenchPositions.add(new MatchLineupPosition(IMatchRoleID.substGK1, Integer.parseInt(properties.getProperty("substgk1", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchLineupPosition(IMatchRoleID.substCD1, Integer.parseInt(properties.getProperty("substcd1", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchLineupPosition(IMatchRoleID.substWB1, Integer.parseInt(properties.getProperty("substwb1", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchLineupPosition(IMatchRoleID.substIM1, Integer.parseInt(properties.getProperty("substim1", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchLineupPosition(IMatchRoleID.substFW1, Integer.parseInt(properties.getProperty("substfw1", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchLineupPosition(IMatchRoleID.substWI1, Integer.parseInt(properties.getProperty("substwi1", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchLineupPosition(IMatchRoleID.substXT1, Integer.parseInt(properties.getProperty("substxt1", "0")), (byte) 0));

			m_vBenchPositions.add(new MatchLineupPosition(IMatchRoleID.substGK2, Integer.parseInt(properties.getProperty("substgk2", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchLineupPosition(IMatchRoleID.substCD2, Integer.parseInt(properties.getProperty("substcd2", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchLineupPosition(IMatchRoleID.substWB2, Integer.parseInt(properties.getProperty("substwb2", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchLineupPosition(IMatchRoleID.substIM2, Integer.parseInt(properties.getProperty("substim2", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchLineupPosition(IMatchRoleID.substFW2, Integer.parseInt(properties.getProperty("substfw2", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchLineupPosition(IMatchRoleID.substWI2, Integer.parseInt(properties.getProperty("substwi2", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchLineupPosition(IMatchRoleID.substXT2, Integer.parseInt(properties.getProperty("substxt2", "0")), (byte) 0));

			var tactic = properties.getProperty("tactictype");
			if (StringUtils.isEmpty(tactic)|| tactic.equals("null")) // to avoid exception when match is finish
				settings.m_iTacticType = 0;
			else
				settings.m_iTacticType = Integer.parseInt(tactic);

			String attitude = properties.getProperty("installning", "0");
			if (StringUtils.isEmpty(attitude) || attitude.equals("null") ) // to avoid exception when match is finish
				settings.m_iAttitude = 0;
			else
				settings.m_iAttitude = Integer.parseInt(attitude);

			var propStyleOfPlay = properties.getProperty("styleofplay");
			if (StringUtils.isEmpty(propStyleOfPlay) || propStyleOfPlay.equals("null")) // to avoid exception when match is finish
				settings.m_iStyleOfPlay = 0;
			else
				settings.m_iStyleOfPlay = Integer.parseInt(propStyleOfPlay);

			// and read the sub contents
			int iSub = 0;
			while(true){
				var subs = getSubstitution(properties, iSub++);
				if (subs != null) {
					this.substitutions.add(subs);
				}
				else {
					break;
				}
			}

			// Add the penalty takers

			for (int i = 0; i < 11; i++) {
				penaltyTakers.add(new MatchLineupPosition(i + IMatchRoleID.penaltyTaker1, Integer
						.parseInt(properties.getProperty("penalty" + i, "0")), (byte) 0));
			}

		} catch (Exception e) {
			HOLogger.instance().warning(getClass(), "Aufstellung.<init1>: " + e);
			HOLogger.instance().log(getClass(), e);
			m_vFieldPositions.removeAllElements();
			m_vBenchPositions.removeAllElements();
			initPositionen553();
		}

		try { // captain + set pieces taker
			setKicker(Integer.parseInt(properties.getProperty("kicker1", "0")));
			setCaptain(Integer.parseInt(properties.getProperty("captain", "0")));
		} catch (Exception e) {
			HOLogger.instance().warning(getClass(), "Aufstellung.<init2>: " + e);
			HOLogger.instance().log(getClass(), e);
		}
	}

	private Substitution getSubstitution(Properties properties, int i) {
		var prefix = "subst" + i;
		var playerorderString = properties.getProperty(prefix + "playerorderid");
		if (playerorderString == null) return null;
		var playerorderid = Integer.parseInt(playerorderString);
		if (playerorderid < 0) return null;

		return  new Substitution(
				Integer.parseInt(properties.getProperty(prefix + "playerorderid")),
				Integer.parseInt(properties.getProperty(prefix + "playerin")),
				Integer.parseInt(properties.getProperty(prefix + "playerout")),
				Byte.parseByte(properties.getProperty(prefix + "ordertype")),
				Byte.parseByte(properties.getProperty(prefix + "matchminutecriteria")),
				Byte.parseByte(properties.getProperty(prefix + "pos")),
				Byte.parseByte(properties.getProperty(prefix + "behaviour")),
				RedCardCriteria.parse(properties.getProperty(prefix + "card")),
				GoalDiffCriteria.parse(properties.getProperty(prefix + "standing")));
	}

	/**
	 * get the tactic level for AiM/AoW
	 * 
	 * @return tactic level
	 */
	public final float getTacticLevelAimAow() {
		return Math.max(1, new RatingPredictionManager(this, HOVerwaltung.instance().getModel().getTeam()).getTacticLevelAowAim());
	}

	/**
	 * get the tactic level for counter
	 * 
	 * @return tactic level
	 */
	public final float getTacticLevelCounter() {
		return (new RatingPredictionManager(this, HOVerwaltung.instance().getModel().getTeam())).getTacticLevelCounter();
	}

	/**
	 * get the tactic level for pressing
	 * 
	 * @return tactic level
	 */
	public final float getTacticLevelPressing() {
		return Math.max(1, new RatingPredictionManager(this, HOVerwaltung.instance().getModel().getTeam()).getTacticLevelPressing());
	}

	/**
	 * get the tactic level for Long Shots
	 * 
	 * @return tactic level
	 */
	public final float getTacticLevelLongShots() {
		return Math.max(1, new RatingPredictionManager(this, HOVerwaltung.instance().getModel().getTeam()).getTacticLevelLongShots());
	}
	public final float getTacticLevelCreative() {
		return Math.max(1, new RatingPredictionManager(this, HOVerwaltung.instance().getModel().getTeam()).getTacticLevelCreative());
	}

	/**
	 * Setter for property m_iAttitude.
	 * 
	 * @param m_iAttitude
	 *            New value of property m_iAttitude.
	 */
	public final void setAttitude(int m_iAttitude) {
		this.settings.m_iAttitude = m_iAttitude;
	}

	/**
	 * Getter for property m_iAttitude.
	 * 
	 * @return Value of property m_iAttitude.
	 */
	public final int getAttitude() {
		return settings.m_iAttitude;
	}
	
	public String getAttitudeName(int attitude) {
		HOVerwaltung hov = HOVerwaltung.instance();
		return switch (attitude) {
			case IMatchDetails.EINSTELLUNG_NORMAL -> hov.getLanguageString("ls.team.teamattitude_short.normal");
			case IMatchDetails.EINSTELLUNG_PIC -> hov.getLanguageString("ls.team.teamattitude_short.playitcool");
			case IMatchDetails.EINSTELLUNG_MOTS -> hov.getLanguageString("ls.team.teamattitude_short.matchoftheseason");
			default -> HOVerwaltung.instance().getLanguageString("Unbestimmt");
		};
	}

	public void setStyleOfPlay(int style) {
		settings.m_iStyleOfPlay = style;
	}
	
	public int getStyleOfPlay() {
		return settings.m_iStyleOfPlay;
	}

	/**
	 * Auto-select the set best captain.
	 */
	public final void setAutoKapitaen(@Nullable List<Player> players) {
		float maxValue = -1;

		if (players == null) {
			players = HOVerwaltung.instance().getModel().getCurrentPlayers();
		}

		if (players != null) {
			for (Player player : players) {
				if (m_clAssi.isPlayerInStartingEleven(player.getPlayerID(), m_vFieldPositions)) {
					int curPlayerId = player.getPlayerID();
					float curCaptainsValue = HOVerwaltung.instance().getModel().getCurrentLineupTeamRecalculated().getLineup()
							.getAverageExperience(curPlayerId);
					if (maxValue < curCaptainsValue) {
						maxValue = curCaptainsValue;
						m_iKapitaen = curPlayerId;
					}
				}
			}
		}
	}

	/**
	 * Auto-select the set best pieces taker.
	 */
	public final void setAutoKicker(@Nullable List<Player> players) {
		double maxStandard = -1;
		int form = -1;

		if (players == null) {
			players = HOVerwaltung.instance().getModel().getCurrentPlayers();
		}

		Vector<MatchLineupPosition> noKeeper = new Vector<>(m_vFieldPositions);
		for (var pos : noKeeper) {
			if (pos.getId() == IMatchRoleID.keeper) {
				noKeeper.remove(pos);
				break;
			}
		}

		if (players != null) {
			for (Player player : players) {
				if (m_clAssi.isPlayerInStartingEleven(player.getPlayerID(), noKeeper)) {
					double sp = (double) player.getSPskill()
							+ player.getSub4Skill(PlayerSkill.SET_PIECES)
							+ RatingPredictionManager.getLoyaltyHomegrownBonus(player);
					if (sp > maxStandard) {
						maxStandard = sp;
						form = player.getForm();
						m_iKicker = player.getPlayerID();
					} else if ((sp == maxStandard) && (form < player.getForm())) {
						maxStandard = sp;
						form = player.getForm();
						m_iKicker = player.getPlayerID();
					}
				}
			}
		}
	}

	/**
	 * Get the average experience of all players in lineup using the formula
	 * from kopsterkespits: teamxp = ((sum of teamxp + xp of
	 * captain)/12)*(1-(7-leadership of captain)*5%)
	 *
	 * @param captainsId use this player as captain (<= 0 for current captain)
	 * @return float
	 */
	public final float getAverageExperience(int captainsId) {
		float value = 0;

		Player captain = null;
		List<Player> players = HOVerwaltung.instance().getModel().getCurrentPlayers();

		if (players != null) {
			for (Player player : players) {
				if (m_clAssi.isPlayerInStartingEleven(player.getPlayerID(), m_vFieldPositions)) {
					value += player.getExperience();
					if (captainsId > 0) {
						if (captainsId == player.getPlayerID()) {
							captain = player;
						}
					} else if (m_iKapitaen == player.getPlayerID()) {
						captain = player;
					}
				}
			}
		}
		if (captain != null) {
			value = ((value + captain.getExperience()) / 12)
					* (1f - (float) (7 - captain.getLeadership()) * 0.05f);
		} else {
			// HOLogger.instance().log(getClass(),
			// "Can't calc average experience, captain not set.");
			value = -1f;
		}
		return value;
	}

	public void setRatings() {
		final RatingPredictionManager rpManager;
		Ratings oRatings = new Ratings();
		boolean bForm = true;

		if ((HOVerwaltung.instance().getModel() != null) && HOVerwaltung.instance().getModel().getID() != -1) {
			rpManager = new RatingPredictionManager(this, HOVerwaltung.instance().getModel().getTeam());
			oRatings.setLeftDefense(rpManager.getLeftDefenseRatings(bForm, true));
			oRatings.setCentralDefense(rpManager.getCentralDefenseRatings(bForm, true));
			oRatings.setRightDefense(rpManager.getRightDefenseRatings(bForm, true));
			oRatings.setMidfield(rpManager.getMFRatings(bForm, true));
			oRatings.setLeftAttack(rpManager.getLeftAttackRatings(bForm, true));
			oRatings.setCentralAttack(rpManager.getCentralAttackRatings(bForm, true));
			oRatings.setRightAttack(rpManager.getRightAttackRatings(bForm, true));
			oRatings.computeHatStats();
			oRatings.computeLoddarStats();
			this.oRatings = oRatings;
		} else {
			this.oRatings = new Ratings();
		}
	}

	/**
	 * This version of the function is called during HOModel creation to avoid back looping
	 */
	 public void setRatings(int hrfID) {
		 final RatingPredictionManager rpManager;
		 Ratings oRatings = new Ratings();
		 boolean bForm = true;

		if ((HOVerwaltung.instance().getModel() != null) && HOVerwaltung.instance().getModel().getID() != -1) {
			Team _team = DBManager.instance().getTeam(hrfID);
			rpManager = new RatingPredictionManager(this, _team);
			oRatings.setLeftDefense(rpManager.getLeftDefenseRatings(bForm, true));
			oRatings.setCentralDefense(rpManager.getCentralDefenseRatings(bForm, true));
			oRatings.setRightDefense(rpManager.getRightDefenseRatings(bForm, true));
			oRatings.setMidfield(rpManager.getMFRatings(bForm, true));
			oRatings.setLeftAttack(rpManager.getLeftAttackRatings(bForm, true));
			oRatings.setCentralAttack(rpManager.getCentralAttackRatings(bForm, true));
			oRatings.setRightAttack(rpManager.getRightAttackRatings(bForm, true));
			oRatings.computeHatStats();
			oRatings.computeLoddarStats();
			this.oRatings = oRatings;
		}
		else {
			this.oRatings = new Ratings(); }
	}



	public Ratings getRatings() {
		    if(oRatings == null)
			{
				setRatings();
			}
			return oRatings;
		}



	/**
	 * Setter for property m_iKapitaen.
	 * 
	 * @param m_iKapitaen
	 *            New value of property m_iKapitaen.
	 */
	public final void setCaptain(int m_iKapitaen) {
		this.setCaptain(new MatchLineupPosition(IMatchRoleID.captain, m_iKapitaen,0));
	}

	/**
	 * Getter for property m_iKapitaen.
	 * 
	 * @return Value of property m_iKapitaen.
	 */
	public final int getCaptain() {
		return m_iKapitaen;
	}

	/**
	 * Setter for property m_iKicker.
	 * 
	 * @param m_iKicker
	 *            New value of property m_iKicker.
	 */
	public final void setKicker(int m_iKicker) {
		this.setSetPiecesTaker(new MatchLineupPosition(IMatchRoleID.setPieces, m_iKicker,0));
	}

	/**
	 * Getter for property m_iKicker.
	 * 
	 * @return Value of property m_iKicker.
	 */
	public final int getKicker() {
		return m_iKicker;
	}



	/**
	 * convert reduced float rating (1.00....20.99) to original integer HT
	 * rating (1...80) one +0.5 is because of correct rounding to integer
	 */
	public static int HTfloat2int(double x) {
		return (int) (((x - 1.0f) * 4.0f) + 1.0f);
	}


	/**
	 * Get the short name for a formation constant.
	 */
	public static String getNameForSystem(byte system) {
		return switch (system) {
			case SYS_451 -> "4-5-1";
			case SYS_352 -> "3-5-2";
			case SYS_442 -> "4-4-2";
			case SYS_343 -> "3-4-3";
			case SYS_433 -> "4-3-3";
			case SYS_532 -> "5-3-2";
			case SYS_541 -> "5-4-1";
			case SYS_523 -> "5-2-3";
			case SYS_550 -> "5-5-0";
			case SYS_253 -> "2-5-3";
			default -> HOVerwaltung.instance().getLanguageString("Unbestimmt");
		};
	}

	/**
	 * Get the position type (byte in IMatchRoleID).
	 */
	public final byte getEffectivePos4PositionID(int positionsid) {
		try {
			var pos =getPositionById(positionsid);
			if ( pos != null ) return pos.getPosition();
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "getEffectivePos4PositionID: " + e);
		}
		return IMatchRoleID.UNKNOWN;
	}

	/**
	 * Setter for property m_sHeimspiel.
	 * 
	 * @param location
	 *            New value of property m_sHeimspiel.
	 */
	public final void setLocation(short location) {
		this.m_sLocation = location;
	}

	/**
	 * Get the location constant for the match (home/away/awayderby)
	 * 
	 * @return the location constant for the match
	 */
	public final short getLocation() {
		if ( m_sLocation == -1 && !isUpcomingMatchLoaded()) {	getUpcomingMatch();	}
		return m_sLocation;
	}

	public final void setArenaId(int id){
		this.m_iArenaId=id;
	}
	public final int getArenaId() {
		if (m_iArenaId == -1) {
			getUpcomingMatch();
		}
		return m_iArenaId;
	}

	public void setRegionId(int id){
		this.m_iRegionId=id;
	}
	public final int getRegionId()
	{
		if (m_iRegionId == -1 && !isUpcomingMatchLoaded()) {	getUpcomingMatch();	}
		return  m_iRegionId;
	}

	public final  void setWeather( Weather weather)
	{
		this.m_cWeather = weather;
	}

	public final Weather getWeather()
	{
		if (m_cWeather == null || (m_cWeather == Weather.NULL && !isUpcomingMatchLoaded())) {getUpcomingMatch();	}
		return  m_cWeather;
	}

	public final void setWeatherForecast(Weather.Forecast weatherForecast){
		this.m_cWeatherForecast = weatherForecast;
	}

	public final Weather.Forecast getWeatherForecast()
	{
		if (this.m_cWeatherForecast == Weather.Forecast.NULL &&  !isUpcomingMatchLoaded()) {	getUpcomingMatch();	}
		return  m_cWeatherForecast;
	}

	private boolean isUpcomingMatchLoaded() { return m_iArenaId>=0; }

	private void getUpcomingMatch() {
		try {
			final int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();

			MatchKurzInfo match = DBManager.instance().getFirstUpcomingMatchWithTeamId(teamId);

			if (match == null) {
				m_sLocation = 0;
				m_iArenaId = 0;
				m_iRegionId = 0;
				m_cWeather = Weather.NULL;
				m_cWeatherForecast = Weather.Forecast.NULL;
				HOLogger.instance().warning(getClass(), "no match to determine location");
				return;
			}

			if (match.getMatchType().isOfficial()) {
				if (match.isNeutral()) {
					m_sLocation = IMatchDetails.LOCATION_NEUTRAL;
				}
				if (match.isDerby()) {
					m_sLocation = IMatchDetails.LOCATION_AWAYDERBY;
				}
				if (!match.isNeutral() && !match.isDerby()) {
					if (match.isHomeMatch()) {
						m_sLocation = IMatchDetails.LOCATION_HOME;
					} else {
						m_sLocation = IMatchDetails.LOCATION_AWAY;
					}
				}
			} else {
				m_sLocation = IMatchDetails.LOCATION_TOURNAMENT;
			}

			m_iArenaId = match.getArenaId();
			m_iRegionId = match.getRegionId();
			m_cWeather = match.getWeather();
			if (m_cWeather == null) m_cWeather = Weather.NULL;
			m_cWeatherForecast = match.getWeatherForecast();
			if (m_cWeatherForecast == null) m_cWeatherForecast = Weather.Forecast.NULL;


		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "getUpcomingMatch: " + e);
			m_sLocation = 0;
		}

	}


	/**
	 * Umrechnung von double auf 1-80 int
	 * 
	 * @deprecated use RatingUtil.getIntValue4Rating(double rating) instead
	 */
	@Deprecated
	public final int getIntValue4Rating(double rating) {
		return (int) (((float) (rating - 1) * 4f) + 1);
	}

	/**
	 * Get the player object by position id.
	 */
	public Player getPlayerByPositionID(int positionId) {
		try {
			var pos = getPositionById(positionId);
			if ( pos != null ) return HOVerwaltung.instance().getModel().getCurrentPlayer(pos.getPlayerId());
		} catch (Exception e) {
			HOLogger.instance()
					.error(getClass(), "getPlayerByPositionID(" + positionId + "): " + e);
		}
		return null;
	}

	public String tryGetPlayerNameByPositionID(int positionId) {
		try {
			var player = getPlayerByPositionID(positionId);
			if ( player != null) return player.getShortName();
		} catch (Exception ignored) {
		}
		return "           ";
	}

	public void printLineup() {
		try {
			String res = "*******************************************************************************************";
			res += "\n" + "           " + "           " + this.tryGetPlayerNameByPositionID(100);
			res += "\n" + this.tryGetPlayerNameByPositionID(101) + "   " + this.tryGetPlayerNameByPositionID(102) + "   " + this.tryGetPlayerNameByPositionID(103) + "   " + this.tryGetPlayerNameByPositionID(104) + "   " + this.tryGetPlayerNameByPositionID(105);
			res += "\n" + this.tryGetPlayerNameByPositionID(106) + "   " + this.tryGetPlayerNameByPositionID(107) + "   " + this.tryGetPlayerNameByPositionID(108) + "   " + this.tryGetPlayerNameByPositionID(109) + "   " + this.tryGetPlayerNameByPositionID(110);
			res += "\n" + "           " + this.tryGetPlayerNameByPositionID(111) + "   " + this.tryGetPlayerNameByPositionID(112) + "   " + this.tryGetPlayerNameByPositionID(113);
			res += "\n*******************************************************************************************";
			System.out.println(res);
		}
		catch (Exception e) {
			System.out.println("could not print lineup");
		}
	}

	/**
	 * Get the position object by position id.
	 */
	public final @Nullable MatchLineupPosition getPositionById(int iPositionID) {
		for (var position : m_vFieldPositions) {
			if (position.getId() == iPositionID) {
				return position;
			}
		}
		for (var position : m_vBenchPositions) {
			if (position.getId() == iPositionID) {
				return position;
			}
		}
		return null;
	}

	/**
	 * Get the position object by player id.
	 */
	public final MatchLineupPosition getPositionByPlayerId(int playerid) {
		return getPositionByPlayerId(playerid, true);
	}

	public final MatchLineupPosition getPositionByPlayerId(int playerid, boolean includeReplacedPlayers) {
		MatchLineupPosition ret = getPositionByPlayerId(playerid, m_vFieldPositions);
		if ( ret == null ) ret = getPositionByPlayerId(playerid, m_vBenchPositions);
		if ( ret == null & includeReplacedPlayers) ret = getPositionByPlayerId(playerid, replacedPositions);
		return ret;
	}

	private MatchLineupPosition getPositionByPlayerId(int playerid, Vector<MatchLineupPosition> positions) {
		for (MatchLineupPosition position : positions) {
			if (position.getPlayerId() == playerid) {
				return position;
			}
		}
		return null;
	}

	public final void setPosition(MatchLineupPosition position)
	{
		if ( position.isFieldMatchRoleId()){
			setPosition(this.m_vFieldPositions, position);
		}
		else if (position.isSubstitutesMatchRoleId() || position.isBackupsMatchRoleId()){
			setPosition(this.m_vBenchPositions,position);
		}
		else if ( position.isPenaltyTakerMatchRoleId()){
			setPosition(this.penaltyTakers,position);
		}
		else if ( position.getId() == IMatchRoleID.setPieces){
			setSetPiecesTaker(position);
		}
		else if ( position.getId() == IMatchRoleID.captain){
			setCaptain(position);
		}
		else if ( position.isReplacedMatchRoleId()){
			setPosition(this.replacedPositions,position);
		}
	}

	private void setPosition(Vector<MatchLineupPosition> m_vPositionen, MatchLineupPosition spos) {
		for (int j = 0; j < m_vPositionen.size(); j++) {
			if (m_vPositionen.get(j).getId() == spos.getId()) {
				m_vPositionen.setElementAt(spos, j);
				return;
			}
		}
	}

	/**
	 * Clears all positions of content by creating a new, empty lineup.
	 */
	public final void clearLineup() {
		initPositionen553();
	}

	/**
	 * Getter for property m_vPositionen.
	 * 
	 * @return Value of property m_vPositionen.
	 */
	public final Vector<MatchLineupPosition> getAllPositions() {
		Vector<MatchLineupPosition> ret = new Vector<>();
		if (m_vFieldPositions != null) ret.addAll(m_vFieldPositions);
		if (m_vBenchPositions != null) ret.addAll(m_vBenchPositions);
		if (replacedPositions != null) ret.addAll(replacedPositions);
		if (penaltyTakers != null) ret.addAll(penaltyTakers);
		if (captain != null) ret.add(captain);
		if (setPiecesTaker != null) ret.add(setPiecesTaker);
		return ret;
	}

	public final Vector<MatchLineupPosition> getFieldPositions(){
		return m_vFieldPositions;
	}

	public Vector<MatchLineupPosition> getReplacedPositions(){return replacedPositions;}

	/**
	 * Place a player to a certain position and check/solve dependencies.
	 */
	public final void setSpielerAtPosition(int positionsid, int spielerid, byte tactic) {
		final MatchLineupPosition pos = getPositionById(positionsid);
		if (pos != null) {
			setSpielerAtPosition(positionsid, spielerid);
			pos.setTaktik(tactic);

			pos.getPosition();
		}
	}

	/**
	 * Place a player to a certain position and check/solve dependencies.
	 */
	public final void setSpielerAtPosition(int positionID, int playerID) {
		final MatchRoleID position = getPositionById(positionID);
		if ( position != null) {
			if ( position.getPlayerId() != playerID) {
				if ( playerID != 0 ) {
					MatchRoleID oldPlayerRole = getPositionByPlayerId(playerID);
					if (oldPlayerRole != null) {
						if (position.isFieldMatchRoleId()) {
							//if player changed is in starting eleven it has to be remove from previous occupied positions
							oldPlayerRole.setPlayerIdIfValidForLineup(0, this);
							if (oldPlayerRole.isSubstitutesMatchRoleId()) {
								removeObjectPlayerFromSubstitutions(playerID);
								// player can occupy multiple bench positions
								oldPlayerRole = getPositionByPlayerId(playerID);
								while (oldPlayerRole != null) {
									oldPlayerRole.setPlayerIdIfValidForLineup(0, this);
									oldPlayerRole = getPositionByPlayerId(playerID);
								}
							}
						} else {
							// position is on bench (or backup), remove him from field position, but not from other bench positions
							if (oldPlayerRole.isFieldMatchRoleId()) {
								oldPlayerRole.setPlayerIdIfValidForLineup(0, this);
							}
						}
					}
				}
				position.setPlayerIdIfValidForLineup(playerID, this);
			}
		}
	}

	/**
	 * Player is no longer on the bench and must be removed from substitution list
	 * if it was planned that he should replace another player
	 */
	private void removeObjectPlayerFromSubstitutions(int playerID) {
		for(Substitution substitution: this.substitutions){
			if (substitution.getOrderType() == MatchOrderType.SUBSTITUTION &&
					substitution.getObjectPlayerID() == playerID){
				this.substitutions.remove(substitution);
				break;
			}
		}
	}

	/**
	 * Check, if the player is in the lineup.
	 */
	public final boolean isPlayerInLineup(int spielerId) {
		//return m_clAssi.isPlayerInLineup(spielerId, m_vPositionen);
		return getPositionByPlayerId(spielerId) != null;
	}

	/**
	 * Check, if the player is in the starting 11.
	 */
	public final boolean isPlayerInStartingEleven(int spielerId) {
		//return m_clAssi.isPlayerInStartingEleven(spielerId, m_vPositionen);
		return getPositionByPlayerId(spielerId, m_vFieldPositions) != null;
	}

	/**
	 * Check, if the player is a subsitute
	 */
	public final boolean isPlayerASub(int spielerId) {
		//return m_clAssi.isPlayerASub(spielerId, m_vPositionen);
		final MatchRoleID role = getPositionByPlayerId(spielerId, m_vBenchPositions);
		return role != null && !role.isBackupsMatchRoleId();
	}

	/**
	 * Check, if the player is a substitute or a backup.
	 */
	public final boolean isSpielerInReserve(int spielerId) {
		//return (m_clAssi.isPlayerInLineup(spielerId, m_vPositionen) && !m_clAssi
		//			.isPlayerInStartingEleven(spielerId, m_vPositionen));
		return getPositionByPlayerId(spielerId, m_vBenchPositions) != null;
	}


	/**
	 * Returns a list of match orders for this lineup.
	 * 
	 * @return the substitutions for this lineup. If there are no substitutions,
	 *         an empty list will be returned.
	 */
	public List<Substitution> getSubstitutionList() {
		return this.substitutions;
	}

	/**
	 * Sets the provided list of match orders as list.
	 * list may contain a maximum of three substitutions
	 * additional number of position swap and behaviour changes depends on tactic assistant level
	 * list may contain one man marking order
	 *
	 * @param subs List of match orders
	 */
	public void setSubstitionList(List<Substitution> subs) {
		if (subs == null) {
			this.substitutions = new ArrayList<>();
		} else {
			this.substitutions = new ArrayList<>(subs);
		}
	}

	/**
	 * Set a new man marking match order.
	 * An existing man marking order will be replaced by the new one.
	 *
	 * @param markerId id of own man marking player
	 * @param opponentMarkedId id of opponents man marked player
	 */
	public void setManMarkingOrder(int markerId, int opponentMarkedId){
		removeManMarkingOrder();	//there can only be one
		var newSub = new Substitution(MatchOrderType.MAN_MARKING);
		newSub.setSubjectPlayerID(markerId);
		newSub.setObjectPlayerID(opponentMarkedId);
		this.substitutions.add(newSub);
	}

	/**
	 * Remove an existing man marking order.
	 * Does nothing if no man marking order exists.
	 */
	public void removeManMarkingOrder() {
		for ( var s : this.substitutions){
			if ( s.getOrderType() == MatchOrderType.MAN_MARKING) {
				this.substitutions.remove(s);
				break;
			}
		}
	}

	public Substitution getManMarkingOrder(){
		for ( var s : this.substitutions){
			if ( s.getOrderType() == MatchOrderType.MAN_MARKING) {
				return s;
			}
		}
		return null;
	}

	public List<MatchLineupPosition> getPenaltyTakers() {
		return this.penaltyTakers;
	}

	public void setPenaltyTakers(List<MatchLineupPosition> positions) {
		this.penaltyTakers = new Vector<>(positions);
		// chpp match order requires exactly 11 penalty takers
		for ( int i=this.penaltyTakers.size(); i<11; i++){
			this.penaltyTakers.add(new MatchLineupPosition(0,0,IMatchRoleID.NORMAL));
		}
	}

	/**
	 * Get the system name.
	 */
	public final String getSystemName(byte system) {
		return getNameForSystem(system);
	}

	/**
	 * Get tactic type for a position-id.
	 */
	public final byte getTactic4PositionID(int positionsid) {
		try {
			var pos = getPositionById(positionsid);
			if (pos != null) return pos.getTactic();
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "getTactic4PositionID: " + e);
		}
		return IMatchRoleID.UNKNOWN;
	}

	public final float getTacticLevel(int type) {
		return switch (type) {
			case IMatchDetails.TAKTIK_PRESSING -> getTacticLevelPressing();
			case IMatchDetails.TAKTIK_KONTER -> getTacticLevelCounter();
			case IMatchDetails.TAKTIK_MIDDLE, IMatchDetails.TAKTIK_WINGS -> getTacticLevelAimAow();
			case IMatchDetails.TAKTIK_LONGSHOTS -> getTacticLevelLongShots();
			case IMatchDetails.TAKTIK_CREATIVE -> getTacticLevelCreative();
			default -> 0.0f;
		};
	}

	/**
	 * Setter for property m_iTacticType.
	 * 
	 * @param m_iTacticType
	 *            New value of property m_iTacticType.
	 */
	public final void setTacticType(int m_iTacticType) {
		this.settings.m_iTacticType = m_iTacticType;
	}

	/**
	 * Getter for property m_iTacticType.
	 * 
	 * @return Value of property m_iTacticType.
	 */
	public final int getTacticType() {
		return settings.m_iTacticType;
	}

	/**
	 * Get the formation xp for the current formation.
	 */
	public final int getExperienceForCurrentTeamFormation() {
		return switch (getCurrentTeamFormationCode()) {
			case SYS_MURKS -> -1;
			case SYS_451 -> HOVerwaltung.instance().getModel().getTeam().getFormationExperience451();
			case SYS_352 -> HOVerwaltung.instance().getModel().getTeam().getFormationExperience352();
			case SYS_442 -> HOVerwaltung.instance().getModel().getTeam().getFormationExperience442();
			case SYS_343 -> HOVerwaltung.instance().getModel().getTeam().getFormationExperience343();
			case SYS_433 -> HOVerwaltung.instance().getModel().getTeam().getFormationExperience433();
			case SYS_532 -> HOVerwaltung.instance().getModel().getTeam().getFormationExperience532();
			case SYS_541 -> HOVerwaltung.instance().getModel().getTeam().getFormationExperience541();
			case SYS_523 -> HOVerwaltung.instance().getModel().getTeam().getFormationExperience523();
			case SYS_550 -> HOVerwaltung.instance().getModel().getTeam().getFormationExperience550();
			case SYS_253 -> HOVerwaltung.instance().getModel().getTeam().getFormationExperience253();
			default -> -1;
		};
	}


	/**
	 * Check if the players are still in the team (not sold or fired).
	 */
	public final void checkAufgestellteSpieler() {
		for (var pos : getAllPositions()) {
			// existiert Player noch ?
			if ((HOVerwaltung.instance().getModel() != null)
					&& (HOVerwaltung.instance().getModel().getCurrentPlayer(pos.getPlayerId()) == null)) {
				// nein dann zuweisung aufheben
				pos.setPlayerIdIfValidForLineup(0, this);
			}
		}
	}

	/**
	 * Assitant to create automatically the lineup
	 */
	public final void optimizeLineup(List<Player> players, byte sectorsStrengthPriority, boolean withForm,
									 boolean idealPosFirst, boolean considerInjured, boolean considereSuspended) {
		m_clAssi.doLineup(getAllPositions(), players, sectorsStrengthPriority, withForm, idealPosFirst,
				considerInjured, considereSuspended, getWeather());
		setAutoKicker(null);
		setAutoKapitaen(null);
	}

	/**
	 * Clone this lineup, creates and returns a new Lineup object.
	 */
	public final @NotNull Lineup duplicate() {

		Lineup clone = new Lineup();
		clone.setPenaltyTakers(getPenaltyTakers());
		clone.setLocation(getLocation());
		clone.setPullBackMinute(getPullBackMinute());
		clone.setWeather(getWeather());
		clone.setWeatherForecast(getWeatherForecast());
		clone.setArenaId(getArenaId());
		clone.setRegionId(getRegionId());

		clone.m_vFieldPositions = copyPositions(m_vFieldPositions);
		clone.m_vBenchPositions = copyPositions(m_vBenchPositions);
		clone.setKicker(this.getKicker());
		clone.setCaptain(this.getCaptain());
		clone.setTacticType(this.getTacticType());
		clone.setAttitude(this.getAttitude());
		clone.setStyleOfPlay(this.getStyleOfPlay());

		clone.substitutions = copySubstitutions();
		return clone;
	}

	private Vector<MatchLineupPosition> copyPositions(Vector<MatchLineupPosition> positions) {
		Vector<MatchLineupPosition> ret = new Vector<>();
		for (var p : positions) {
			ret.add(new MatchLineupPosition(p.getRoleId(),
					p.getPlayerId(),
					p.getBehaviour(),
					p.getRating(),
					p.getSpielerVName(),
					p.getNickName(),
					p.getSpielerName(),
					p.getStatus(),
					p.getRatingStarsEndOfMatch(),
					p.getStartPosition(),
					p.getStartBehavior(),
					p.isStartSetPiecesTaker()));
		}
		return ret;
	}

	private List<Substitution> copySubstitutions() {
		var ret = new ArrayList<Substitution>();
		for ( var s: this.substitutions) {
			ret.add(new Substitution(s.getPlayerOrderId(),
					s.getObjectPlayerID(),
					s.getSubjectPlayerID(),
					s.getOrderType().getId(),
					s.getMatchMinuteCriteria(),
					s.getRoleId(),
					s.getBehaviour(),
					s.getRedCardCriteria(),
					s.getStanding()));
		}
		return ret;
	}

	public final String getCurrentTeamFormationString() {
		final int iNbDefs = getNbDefenders();
		final int iNbMids = getNbMidfields();
		final int iNbFwds = getNbForwards();
		return iNbDefs + "-" + iNbMids + "-" + iNbFwds;
	}

	public final byte getCurrentTeamFormationCode() {
		final int defenders = getNbDefenders();
		final int midfielders = getNbMidfields();
		final int forwards = getNbForwards();
		if (defenders == 2) {
			// 253
			if (midfielders == 5 && forwards == 3) {
				return SYS_253;
			}
			// MURKS
			else {
				return SYS_MURKS;
			}
		} else if (defenders == 3) {
			// 343
			if (midfielders == 4 && forwards == 3) {
				return SYS_343;
			} // 352
			else if (midfielders == 5 && forwards == 2) {
				return SYS_352;
			}
			// MURKS
			else {
				return SYS_MURKS;
			}
		} else if (defenders == 4) {
			// 433
			if (midfielders == 3 && forwards == 3) {
				return SYS_433;
			} // 442
			else if (midfielders == 4 && forwards == 2) {
				return SYS_442;
			} // 451
			else if (midfielders == 5 && forwards == 1) {
				return SYS_451;
			}
			// MURKS
			else {
				return SYS_MURKS;
			}
		} else if (defenders == 5) {
			// 532
			if (midfielders == 3 && forwards == 2) {
				return SYS_532;
			} // 541
			else if (midfielders == 4 && forwards == 1) {
				return SYS_541;
			} // 523
			else if (midfielders == 2 && forwards == 3) {
				return SYS_523;
			} // 550
			else if (midfielders == 5 && forwards == 0) {
				return SYS_550;
			}
			// MURKS
			else {
				return SYS_MURKS;
			}
		} // MURKS
		else {
			return SYS_MURKS;
		}
	}

	private void swapContentAtPositions(int pos1, int pos2) {
		int id1 = 0;
		int id2 = 0;

		var tac1 = getTactic4PositionID(pos1);
		var tac2 = getTactic4PositionID(pos2);

		if (getPlayerByPositionID(pos1) != null) {
			id1 = getPlayerByPositionID(pos1).getPlayerID();
			setSpielerAtPosition(pos1, 0);
		}
		if (getPlayerByPositionID(pos2) != null) {
			id2 = getPlayerByPositionID(pos2).getPlayerID();
			setSpielerAtPosition(pos2, 0);
		}
		setSpielerAtPosition(pos2, id1, tac1);
		setSpielerAtPosition(pos1, id2, tac2);
	}

	/**
	 * Swap corresponding right/left players and orders.
	 */
	public final void flipSide() {
		swapContentAtPositions(IMatchRoleID.rightBack, IMatchRoleID.leftBack);
		swapContentAtPositions(IMatchRoleID.rightCentralDefender,
				IMatchRoleID.leftCentralDefender);
		swapContentAtPositions(IMatchRoleID.rightWinger, IMatchRoleID.leftWinger);
		swapContentAtPositions(IMatchRoleID.rightInnerMidfield,
				IMatchRoleID.leftInnerMidfield);
		swapContentAtPositions(IMatchRoleID.rightForward, IMatchRoleID.leftForward);
	}

	/**
	 * Remove all players from all positions.
	 */
	public final void resetStartingLineup() {
		m_clAssi.resetPositionsbesetzungen(getAllPositions());
	}

	/**
	 * Remove a spare player.
	 */
	public final void resetSubstituteBench() {
		// Nur Reservespieler
		/*final Vector<IMatchRoleID> vReserve = new Vector<IMatchRoleID>();
		for (IMatchRoleID pos : m_vPositions) {
			if (((MatchRoleID) pos).getId() >= IMatchRoleID.startReserves) {
				vReserve.add(pos);
			}
		}*/
		m_clAssi.resetPositionsbesetzungen(m_vBenchPositions);
	}

	/**
	 * Resets the orders for all positions to normal
	 */
	public final void resetPositionOrders() {
		m_clAssi.resetPositionOrders(m_vFieldPositions);
	}


	/**
	 * Calculate the amount af defenders.
	 */
	private int getNbDefenders() {
		int anzahl = 0;
		anzahl += getAnzPosImSystem(IMatchRoleID.BACK);
		anzahl += getAnzPosImSystem(IMatchRoleID.BACK_TOMID);
		anzahl += getAnzPosImSystem(IMatchRoleID.BACK_OFF);
		anzahl += getAnzPosImSystem(IMatchRoleID.BACK_DEF);
		return anzahl + getAnzInnenverteidiger();
	}

	/**
	 * Calculate the amount of central defenders.
	 */
	private int getAnzInnenverteidiger() {
		int anzahl = 0;
		anzahl += getAnzPosImSystem(IMatchRoleID.CENTRAL_DEFENDER);
		anzahl += getAnzPosImSystem(IMatchRoleID.CENTRAL_DEFENDER_TOWING);
		anzahl += getAnzPosImSystem(IMatchRoleID.CENTRAL_DEFENDER_OFF);
		return anzahl;
	}

	/**
	 * Get the total amount of midfielders in the lineup.
	 */
	private int getNbMidfields() {
		int anzahl = 0;
		anzahl += getAnzPosImSystem(IMatchRoleID.WINGER);
		anzahl += getAnzPosImSystem(IMatchRoleID.WINGER_TOMID);
		anzahl += getAnzPosImSystem(IMatchRoleID.WINGER_OFF);
		anzahl += getAnzPosImSystem(IMatchRoleID.WINGER_DEF);
		return anzahl + getAnzInneresMittelfeld();
	}

	/**
	 * Get the amount of inner midfielders in the lineup.
	 */
	private int getAnzInneresMittelfeld() {
		int anzahl = 0;
		anzahl += getAnzPosImSystem(IMatchRoleID.MIDFIELDER);
		anzahl += getAnzPosImSystem(IMatchRoleID.MIDFIELDER_OFF);
		anzahl += getAnzPosImSystem(IMatchRoleID.MIDFIELDER_DEF);
		anzahl += getAnzPosImSystem(IMatchRoleID.MIDFIELDER_TOWING);
		return anzahl;
	}

	/**
	 * Get the amount of strikers in the lineup.
	 */
	private int getNbForwards() {
		int anzahl = 0;
		anzahl += getAnzPosImSystem(IMatchRoleID.FORWARD);
		anzahl += getAnzPosImSystem(IMatchRoleID.FORWARD_DEF);
		anzahl += getAnzPosImSystem(IMatchRoleID.FORWARD_TOWING);
		return anzahl;
	}

	/**
	 * Generic "counter" for the given position in the current lineup.
	 */
	private int getAnzPosImSystem(byte positionId) {
		int anzahl = 0;
		for (var pos : m_vFieldPositions) {
			if ((positionId == pos.getPosition())
					&& (pos.getId() < IMatchRoleID.startReserves)
					&& (pos.getPlayerId() > 0)) {
				++anzahl;
			}
		}
		return anzahl;
	}

	/**
	 * @return true if less than 11 players on field, false if 11 (or more) on
	 *         field
	 */
	public boolean hasFreePosition() {
		int numPlayers = 0;
		for (var pos : m_vFieldPositions) {
			if (pos.getPlayerId() != 0) numPlayers++;
		}
		return numPlayers != 11;
	}

	/**
	 * Initializes the 553 lineup
	 */
	private void initPositionen553() {

		m_vFieldPositions = new Vector<>();
		for ( int i=IMatchRoleID.keeper; i<= IMatchRoleID.leftForward; i++) {
			m_vFieldPositions.add(new MatchLineupPosition(i, 0, (byte)0 ));
		}
		m_vBenchPositions = new Vector<>();
		for ( int i=IMatchRoleID.substGK1; i<= IMatchRoleID.substXT2; i++) {
			m_vBenchPositions.add(new MatchLineupPosition(i, 0, (byte)0 ));
		}
		penaltyTakers = new Vector<>();
		for (int i = IMatchRoleID.penaltyTaker1; i <= IMatchRoleID.penaltyTaker11; i++) {
			penaltyTakers.add(new MatchLineupPosition( i, 0, (byte) 0));
		}
		replacedPositions = new Vector<>();
		for (int i = IMatchRoleID.FirstPlayerReplaced; i <= IMatchRoleID.ThirdPlayerReplaced; i++) {
			replacedPositions.add(new MatchLineupPosition( i, 0, (byte) 0));
		}
	}

	/**
	 * @return the pullBackMinute
	 */
	public int getPullBackMinute() {
		return pullBackMinute;
	}

	/**
	 * @param pullBackMinute
	 *            the pullBackMinute to set
	 */
	public void setPullBackMinute(int pullBackMinute) {
		this.pullBackMinute = pullBackMinute;
	}



	/**
	 * Amend the lineup by applying the Given MatchOrder
	 */
	public void UpdateLineupWithMatchOrder(Substitution sub) {
		MatchRoleID matchRoleIDPlayer, matchRoleIDaffectedPlayer;
		int newRoleId;
		byte tactic;

		Player ObjectPlayer;
		switch (sub.getOrderType()) {
			case SUBSTITUTION:
				matchRoleIDaffectedPlayer = this.getPositionByPlayerId(sub.getSubjectPlayerID());
				if (matchRoleIDaffectedPlayer == null) {
					HOLogger.instance().warning(Lineup.class, String.format("The player id: %s cannot do the substitution", sub.getSubjectPlayerID()));
					break;
				}

				matchRoleIDPlayer = getPositionByPlayerId(sub.getObjectPlayerID());
				if (matchRoleIDPlayer == null) {
					HOLogger.instance().warning(Lineup.class, String.format("The substitution of player id: %s has not been recognized", sub.getObjectPlayerID()));
					break;
				}
				ObjectPlayer = this.getPlayerByPositionID(matchRoleIDPlayer.getId());
				if (ObjectPlayer == null) {
					HOLogger.instance().warning(Lineup.class, String.format("The player id: %s cannot do the substitution", sub.getObjectPlayerID()));
					break;
				}
				ObjectPlayer.setGameStartingTime(sub.getMatchMinuteCriteria());
				tactic = sub.getBehaviour();
				if (tactic == -1) tactic = matchRoleIDaffectedPlayer.getTactic();
				newRoleId = sub.getRoleId();
				if (newRoleId != -1) {
					var pos = getPositionById(newRoleId);
					if (pos != null && pos.getPlayerId() == 0) {
						if (newRoleId != matchRoleIDaffectedPlayer.getId()) {
							setSpielerAtPosition(matchRoleIDaffectedPlayer.getId(), 0, MatchRoleID.NORMAL);  // clear old position
						}
					} else {
						HOLogger.instance().warning(Lineup.class, String.format("The player id: %s cannot do the substitution. Position is not free.", sub.getObjectPlayerID()));
						break;
					}
				} else {
					newRoleId = matchRoleIDaffectedPlayer.getId();
				}
				setSpielerAtPosition(newRoleId, matchRoleIDPlayer.getPlayerId(), tactic);
				break;

			case POSITION_SWAP:
				matchRoleIDaffectedPlayer = getPositionByPlayerId(sub.getSubjectPlayerID());
				matchRoleIDPlayer = getPositionByPlayerId(sub.getObjectPlayerID());
				if (matchRoleIDaffectedPlayer != null && matchRoleIDPlayer != null) {
					matchRoleIDaffectedPlayer.setPlayerIdIfValidForLineup(sub.getObjectPlayerID());
					matchRoleIDPlayer.setPlayerIdIfValidForLineup(sub.getSubjectPlayerID());
				} else {
					if (matchRoleIDaffectedPlayer == null) {
						HOLogger.instance().warning(Lineup.class, String.format("The player id: %s is (no longer) in lineup.", sub.getSubjectPlayerID()));
					}
					if (matchRoleIDPlayer == null) {
						HOLogger.instance().warning(Lineup.class, String.format("The player id: %s is (no longer) in lineup.", sub.getObjectPlayerID()));
					}
				}
				break;

			case NEW_BEHAVIOUR:
				newRoleId = sub.getRoleId();
				matchRoleIDaffectedPlayer = getPositionByPlayerId(sub.getSubjectPlayerID());
				if (matchRoleIDaffectedPlayer == null) {
					HOLogger.instance().warning(Lineup.class, String.format("The player id: %s cannot do the substitution", sub.getSubjectPlayerID()));
					break;
				}
				if (newRoleId == -1) {
					newRoleId = matchRoleIDaffectedPlayer.getId();
				} else if (newRoleId != matchRoleIDaffectedPlayer.getId()) {
					var pos = getPositionById(newRoleId);
					if (pos != null && pos.getPlayerId() > 0) {
						HOLogger.instance().warning(Lineup.class, String.format("The player id: %s cannot do the substitution. Position is not free.", sub.getObjectPlayerID()));
						break;
					}
				}
				tactic = sub.getBehaviour();
				if (tactic == -1) tactic = MatchRoleID.NORMAL;
				setSpielerAtPosition(newRoleId, sub.getSubjectPlayerID(), tactic);
				break;

			case MAN_MARKING:
				// TODO: handle man marking orders
				break;
			default:
				HOLogger.instance().error(Lineup.class, String.format("Incorrect Prediction Rating: the following match order has not been considered: %s", sub.getOrderType()));
				break;
		}
	}

	public void adjustBackupPlayers() {
		Player player = this.getPlayerByPositionID(IMatchRoleID.substGK1);
		int substGK = (player == null) ? 0 : player.getPlayerID();

		player = this.getPlayerByPositionID(IMatchRoleID.substCD1);
		int substCD = (player == null) ? 0 : player.getPlayerID();

		player = this.getPlayerByPositionID(IMatchRoleID.substWB1);
		int substWB = (player == null) ? 0 : player.getPlayerID();

		player = this.getPlayerByPositionID(IMatchRoleID.substIM1);
		int substIM = (player == null) ? 0 : player.getPlayerID();

		player = this.getPlayerByPositionID(IMatchRoleID.substFW1);
		int substFW = (player == null) ? 0 : player.getPlayerID();

		player = this.getPlayerByPositionID(IMatchRoleID.substWI1);
		int substWI = (player == null) ? 0 : player.getPlayerID();

		player = this.getPlayerByPositionID(IMatchRoleID.substXT1);
		int substXT = (player == null) ? 0 : player.getPlayerID();

		ArrayList<Integer> authorisedBackup = new ArrayList<>(Arrays.asList(substGK, substCD, substWB, substIM, substFW, substWI, substXT));

		// remove player from backup position if not listed as a sub anymore
		adjustBackupPlayer(IMatchRoleID.substGK2, substGK, authorisedBackup);
		adjustBackupPlayer(IMatchRoleID.substCD2, substCD, authorisedBackup);
		adjustBackupPlayer(IMatchRoleID.substWB2, substWB, authorisedBackup);
		adjustBackupPlayer(IMatchRoleID.substIM2, substIM, authorisedBackup);
		adjustBackupPlayer(IMatchRoleID.substFW2, substFW, authorisedBackup);
		adjustBackupPlayer(IMatchRoleID.substWI2, substWI, authorisedBackup);
		adjustBackupPlayer(IMatchRoleID.substXT2, substXT, authorisedBackup);

	}

	public void adjustBackupPlayer(int iBackupPositionID, int iPlayerIDCorrespondingSub, ArrayList<Integer> authorisedBackup) {
		Player player = this.getPlayerByPositionID(iBackupPositionID);
		if (player != null){
			int backupID = player.getPlayerID();
			if ((backupID == iPlayerIDCorrespondingSub) || !(authorisedBackup.contains(backupID)))
			{
				// need to remove that player from that backup position
				this.setSpielerAtPosition(iBackupPositionID, 0);
			}


		}
	}

	public String toJson()
	{
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return gson.toJson(this);
	}

	/**
	► IFK:
	off IFK = 0.5*mAtt + 0.3*mSP + 0.09*SPshooter
	def IFK = 0.4*mDef + .3*mSP + 0.1*SPgk + 0.08*GKgk

	mAtt: outfield players attack skill average
	mDEf: outfield players defence skill average
	 */
	public double getRatingIndirectSetPiecesAtt() {
		double teamAtt = 0; // team score sum
		double teamSP = 0;  // team set pieces sum
		double spSP = 0;    // set pieces taker set pieces
		int n = 0;

		for (var pos : m_vFieldPositions) {
			Player p = this.getPlayerByPositionID(pos.getId());
			if ( p != null){
				teamAtt += p.getSCskill();
				teamSP += p.getSPskill();
				if ( p.getPlayerID() == getKicker()){
					spSP = p.getSPskill();
				}
			}
			n++;
		}
		if ( n > 1){
			teamAtt/=n;
			teamSP /=n;
		}
		return .5*teamAtt + .3*teamSP + .09*spSP;
	}

	public double getRatingIndirectSetPiecesDef() {
		double teamDef = 0; // team defence sum
		double teamSP = 0;  // team set pieces sum
		double spGK = 0;    // keeper's set pieces
		double gkGK = 0;  	// keeper's keeper skill
		int n = 0;

		Player keeper = getPlayerByPositionID(IMatchRoleID.keeper);
		if ( keeper != null){
			spGK = keeper.getSPskill();
			gkGK = keeper.getGKskill();
		}

		for (var pos : m_vFieldPositions) {
			Player p = this.getPlayerByPositionID(pos.getId());
			if ( p != null){
				n++;
				teamDef += p.getDEFskill();
				teamSP += p.getSPskill();
			}
		}
		if ( n > 1){
			teamDef/=n;
			teamSP /=n;
		}
		return .4*teamDef + .3*teamSP + .1*spGK + .08*gkGK;
	}

}
