package module.lineup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.Ratings;
import core.model.Team;
import core.model.match.IMatchDetails;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchLineupPosition;
import core.model.match.Weather;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.rating.RatingPredictionConfig;
import core.rating.RatingPredictionManager;
import core.util.HOLogger;
import core.util.Helper;
import core.util.StringUtils;
import module.lineup.assistant.LineupAssistant;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.RedCardCriteria;
import module.lineup.substitution.model.Substitution;

import java.util.*;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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

	public static final String DEFAULT_NAME = "HO!";
	public static final String DEFAULT_NAMELAST = "HO!LastLineup";
	public static final int NO_HRF_VERBINDUNG = -1;

	// TODO: remove assistant from Lineup class
	/** Aufstellungsassistent */
	private LineupAssistant m_clAssi = new LineupAssistant();

	/** positions */
	@SerializedName("positions")
	@Expose
	private Vector<IMatchRoleID> m_vFieldPositions = new Vector<>();
	/** bench */
	@SerializedName("bench")
	@Expose
	private Vector<IMatchRoleID> m_vBenchPositions = new Vector<>();
	@SerializedName("substitutions")
	@Expose
	private List<Substitution> substitutions = new ArrayList<>();
	@SerializedName("kickers")
	@Expose
	private List<MatchRoleID> penaltyTakers = new ArrayList<>();

	/** captain */
	@SerializedName("captain")
	@Expose
	private int m_iKapitaen = -1;

	/** set pieces take */
	@SerializedName("setPieces")
	@Expose
	private int m_iKicker = -1;

	public Lineup(Vector<MatchLineupPosition> matchLineupPositions, List<Substitution> substitutions) {
		for ( var position : matchLineupPositions){
			addPosition(position);
		}
		this.substitutions = substitutions;
	}

	public void addPosition(MatchLineupPosition position) {
		if ( position.isFieldMatchRoleId()){
			this.m_vFieldPositions.add(position);
		}
		else if (position.isSubstitutesMatchRoleId() || position.isBackupsMatchRoleId()){
			this.m_vBenchPositions.add(position);
		}
		else if ( position.isPenaltyTakerMatchRoleId()){
			this.penaltyTakers.add(position);
		}
		else if ( position.getId() == IMatchRoleID.setPieces){
			this.m_iKicker = position.getPlayerId();
		}
		else if ( position.getId() == IMatchRoleID.captain){
			this.m_iKapitaen = position.getPlayerId();
		}
	}

	private class Settings {
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
	 * 
	 * Probably up for change with new XML?
	 */
	public Lineup(Properties properties) {
		try {
			// Positionen erzeugen
			m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.keeper, Integer
					.parseInt(properties.getProperty("keeper", "0")), (byte) 0));
			m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.rightBack, Integer
					.parseInt(properties.getProperty("rightback", "0")), Byte.parseByte(properties
					.getProperty("order_rightback", "0"))));
			m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.rightCentralDefender, Integer
					.parseInt(properties.getProperty("rightcentraldefender", "0")), Byte
					.parseByte(properties.getProperty("order_rightcentraldefender", "0"))));
			m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.middleCentralDefender, Integer
					.parseInt(properties.getProperty("middlecentraldefender", "0")), Byte
					.parseByte(properties.getProperty("order_middlecentraldefender", "0"))));
			m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.leftCentralDefender, Integer
					.parseInt(properties.getProperty("leftcentraldefender", "0")), Byte
					.parseByte(properties.getProperty("order_leftcentraldefender", "0"))));
			m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.leftBack, Integer
					.parseInt(properties.getProperty("leftback", "0")), Byte.parseByte(properties
					.getProperty("order_leftback", "0"))));
			m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.rightWinger, Integer
					.parseInt(properties.getProperty("rightwinger", "0")), Byte
					.parseByte(properties.getProperty("order_rightwinger", "0"))));
			m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.rightInnerMidfield, Integer
					.parseInt(properties.getProperty("rightinnermidfield", "0")), Byte.parseByte(properties
					.getProperty("order_rightinnermidfield", "0"))));
			m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.centralInnerMidfield, Integer
					.parseInt(properties.getProperty("middleinnermidfield", "0")), Byte.parseByte(properties
					.getProperty("order_centralinnermidfield", "0"))));
			m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.leftInnerMidfield, Integer
					.parseInt(properties.getProperty("leftinnermidfield", "0")), Byte.parseByte(properties
					.getProperty("order_leftinnermidfield", "0"))));
			m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.leftWinger, Integer
					.parseInt(properties.getProperty("leftwinger", "0")), Byte.parseByte(properties
					.getProperty("order_leftwinger", "0"))));
			m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.rightForward, Integer
					.parseInt(properties.getProperty("rightforward", "0")), Byte.parseByte(properties
					.getProperty("order_rightforward", "0"))));
			m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.centralForward, Integer
					.parseInt(properties.getProperty("centralforward", "0")), Byte.parseByte(properties
					.getProperty("order_centralforward", "0"))));
			m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.leftForward, Integer
					.parseInt(properties.getProperty("leftforward", "0")), Byte.parseByte(properties
					.getProperty("order_leftforward", "0"))));

			m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substGK1, Integer.parseInt(properties.getProperty("substgk1", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substCD1, Integer.parseInt(properties.getProperty("substcd1", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substWB1, Integer.parseInt(properties.getProperty("substwb1", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substIM1, Integer.parseInt(properties.getProperty("substim1", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substFW1, Integer.parseInt(properties.getProperty("substfw1", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substWI1, Integer.parseInt(properties.getProperty("substwi1", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substXT1, Integer.parseInt(properties.getProperty("substxt1", "0")), (byte) 0));

			m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substGK2, Integer.parseInt(properties.getProperty("substgk2", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substCD2, Integer.parseInt(properties.getProperty("substcd2", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substWB2, Integer.parseInt(properties.getProperty("substwb2", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substIM2, Integer.parseInt(properties.getProperty("substim2", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substFW2, Integer.parseInt(properties.getProperty("substfw2", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substWI2, Integer.parseInt(properties.getProperty("substwi2", "0")), (byte) 0));
			m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substXT2, Integer.parseInt(properties.getProperty("substxt2", "0")), (byte) 0));

			if (properties.getProperty("tactictype").equals("null")) // to avoid exception when match is finish
				settings.m_iTacticType = 0;
			else
				settings.m_iTacticType = Integer.parseInt(properties.getProperty("tactictype", "0"));

			String attitude = properties.getProperty("installning", "0");
			if (attitude.equals("null") | attitude.equals("")) // to avoid exception when match is finish
				settings.m_iAttitude = 0;
			else
				settings.m_iAttitude = Integer.parseInt(attitude);

			if (properties.getProperty("styleofplay").equals("null")) // to avoid exception when match is finish
				settings.m_iStyleOfPlay = 0;
			else
				settings.m_iStyleOfPlay = Integer.parseInt(properties.getProperty("styleofplay", "0"));

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
				penaltyTakers.add(new MatchRoleID(i + IMatchRoleID.penaltyTaker1, Integer
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
			m_iKicker = Integer.parseInt(properties.getProperty("kicker1", "0"));
			m_iKapitaen = Integer.parseInt(properties.getProperty("captain", "0"));
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

		// bugfix: i had a HRF where subst0standing was missing
		String val = properties.getProperty(prefix + "standing");
		GoalDiffCriteria goalDiffCriteria;
		if (StringUtils.isNumeric(val)) {
			goalDiffCriteria = GoalDiffCriteria.getById(Byte.parseByte(val));
		} else {
			goalDiffCriteria = GoalDiffCriteria.ANY_STANDING;
		}

		return  new Substitution(
				Integer.parseInt(properties.getProperty(prefix + "playerorderid")),
				Integer.parseInt(properties.getProperty(prefix + "playerin")),
				Integer.parseInt(properties.getProperty(prefix + "playerout")),
				Byte.parseByte(properties.getProperty(prefix + "ordertype")),
				Byte.parseByte(properties.getProperty(prefix + "matchminutecriteria")),
				Byte.parseByte(properties.getProperty(prefix + "pos")),
				Byte.parseByte(properties.getProperty(prefix + "behaviour")),
				RedCardCriteria.getById(Byte.parseByte(properties.getProperty(prefix + "card"))),
				goalDiffCriteria);
	}

	/**
	 * get the tactic level for AiM/AoW
	 * 
	 * @return tactic level
	 */
	public final float getTacticLevelAimAow() {
		return Math.max(1, new RatingPredictionManager(this, HOVerwaltung.instance().getModel()
				.getTeam(),	settings.m_iStyleOfPlay, RatingPredictionConfig.getInstance()).getTacticLevelAowAim());
	}

	/**
	 * get the tactic level for counter
	 * 
	 * @return tactic level
	 */
	public final float getTacticLevelCounter() {
		return (new RatingPredictionManager(this, HOVerwaltung.instance().getModel().getTeam(), settings.m_iStyleOfPlay,
				RatingPredictionConfig.getInstance())).getTacticLevelCounter();
	}

	/**
	 * get the tactic level for pressing
	 * 
	 * @return tactic level
	 */
	public final float getTacticLevelPressing() {
		return Math.max(1, new RatingPredictionManager(this, HOVerwaltung.instance().getModel()
				.getTeam(),settings.m_iStyleOfPlay,
				RatingPredictionConfig.getInstance()).getTacticLevelPressing());
	}

	/**
	 * get the tactic level for Long Shots
	 * 
	 * @return tactic level
	 */
	public final float getTacticLevelLongShots() {
		return Math.max(1, new RatingPredictionManager(this, HOVerwaltung.instance().getModel()
				.getTeam(), settings.m_iStyleOfPlay,
				RatingPredictionConfig.getInstance()).getTacticLevelLongShots());
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
					float curCaptainsValue = HOVerwaltung.instance().getModel().getLineup()
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

		Vector<IMatchRoleID> noKeeper = new Vector<>(m_vFieldPositions);

		for (IMatchRoleID pos : noKeeper) {
			MatchRoleID p = (MatchRoleID) pos;
			if (p.getId() == IMatchRoleID.keeper) {
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
	 */
	public final float getAverageExperience() {
		return getAverageExperience(0);
	}

	/**
	 * Get the average experience of all players in lineup using a specific
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
					value += player.getErfahrung();
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
			value = ((value + captain.getErfahrung()) / 12)
					* (1f - (float) (7 - captain.getFuehrung()) * 0.05f);
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
			rpManager = new RatingPredictionManager(this, HOVerwaltung.instance().getModel().getTeam(), settings.m_iStyleOfPlay, RatingPredictionConfig.getInstance());
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
	 *
	 * @param hrfID
	 */
	 public void setRatings(int hrfID) {
		 final RatingPredictionManager rpManager;
		 Ratings oRatings = new Ratings();
		 boolean bForm = true;

		if ((HOVerwaltung.instance().getModel() != null) && HOVerwaltung.instance().getModel().getID() != -1) {
			Team _team = DBManager.instance().getTeam(hrfID);
			rpManager = new RatingPredictionManager(this, _team, settings.m_iStyleOfPlay, RatingPredictionConfig.getInstance());
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
		this.m_iKapitaen = m_iKapitaen;
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
		this.m_iKicker = m_iKicker;
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
			return getPositionById(positionsid).getPosition();
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "getEffectivePos4PositionID: " + e);
			return IMatchRoleID.UNKNOWN;
		}
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
	public final int getArenaId()
	{
		if ( m_iArenaId == -1  && !isUpcomingMatchLoaded()) {	getUpcomingMatch();	}
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
	 * @rating
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
			return HOVerwaltung.instance().getModel()
					.getCurrentPlayer(getPositionById(positionId).getPlayerId());
		} catch (Exception e) {
			HOLogger.instance()
					.error(getClass(), "getPlayerByPositionID(" + positionId + "): " + e);
			return null;
		}
	}

	public String tryGetPlayerNameByPositionID(int positionId) {
		String playerName;

		try {
			return HOVerwaltung.instance().getModel().getCurrentPlayer(getPositionById(positionId).getPlayerId()).getShortName();
		} catch (Exception e) {
			return "           ";
		}
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
	public final @Nullable MatchRoleID getPositionById(int iPositionID) {
		for (IMatchRoleID position : m_vFieldPositions) {
			MatchRoleID spielerPosition = (MatchRoleID) position;
			if (spielerPosition.getId() == iPositionID) {
				return spielerPosition;
			}
		}
		for (IMatchRoleID position : m_vBenchPositions) {
			MatchRoleID spielerPosition = (MatchRoleID) position;
			if (spielerPosition.getId() == iPositionID) {
				return spielerPosition;
			}
		}
		return null;
	}

	/**
	 * Get the position object by player id.
	 */
	public final MatchRoleID getPositionByPlayerId(int playerid) {
		MatchRoleID ret = getPositionByPlayerId(playerid, m_vFieldPositions);
		if ( ret == null ) ret = getPositionByPlayerId(playerid, m_vBenchPositions);
		return ret;
	}

	private MatchRoleID getPositionByPlayerId(int playerid, Vector<IMatchRoleID> positions) {
		for (IMatchRoleID position : positions) {
			MatchRoleID spielerPosition = (MatchRoleID) position;
			if (spielerPosition.getPlayerId() == playerid) {
				return spielerPosition;
			}
		}
		return null;
	}

	/**
	 * Setter for property m_vPositionen. All previous entries of the linup are
	 * cleared.
	 * 
	 * @param positions
	 *            New value of property m_vPositionen.
	 */
	public final void setPositionen(List<IMatchRoleID> positions) {
		// Replace the existing positions with the incoming on a one by one
		// basis. Otherwise we will miss 3 positions when loading
		// an old style lineup.
		// We need to avoid the regular methods, as some required stuff like the
		// Model may not be created yet.

		if (positions != null) {
			initPositionen553();
			for (IMatchRoleID pos : positions) {
				MatchRoleID spos = (MatchRoleID) pos;
				setPosition(spos);
			}
		}
	}

	public final void setPosition(MatchRoleID pos)
	{
		if ( pos.isFieldMatchRoleId() ){
			setPosition(m_vFieldPositions, pos);
		}
		else{
			setPosition(m_vBenchPositions, pos);
		}
	}

	private void setPosition(Vector<IMatchRoleID> m_vPositionen, MatchRoleID spos) {
		for (int j = 0; j < m_vPositionen.size(); j++) {
			if (((MatchRoleID) m_vPositionen.get(j)).getId() == spos.getId()) {
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
	public final Vector<IMatchRoleID> getPositionen() {
		Vector<IMatchRoleID> ret = new Vector<>();
		if (m_vFieldPositions!=null) ret.addAll(m_vFieldPositions);
		if (m_vBenchPositions!=null) ret.addAll(m_vBenchPositions);
		return ret;
	}

	public final Vector<IMatchRoleID> getFieldPositions(){
		return m_vFieldPositions;
	}

	public final Vector<IMatchRoleID> getBenchPositions(){
		return m_vBenchPositions;
	}

	/**
	 * Place a player to a certain position and check/solve dependencies.
	 */
	public final byte setSpielerAtPosition(int positionsid, int spielerid, byte tactic) {
		final MatchRoleID pos = getPositionById(positionsid);

		if (pos != null) {
			setSpielerAtPosition(positionsid, spielerid);
			pos.setTaktik(tactic);

			return pos.getPosition();
		}

		return IMatchRoleID.UNKNOWN;
	}

	/**
	 * Place a player to a certain position and check/solve dependencies.
	 */
	public final void setSpielerAtPosition(int positionID, int playerID) {
		final MatchRoleID position = getPositionById(positionID);
		if ( position != null) {
			MatchRoleID oldPlayerRole = getPositionByPlayerId(playerID);
			if (oldPlayerRole != null) {
				if (position.isFieldMatchRoleId()) {
					//if player changed is in starting eleven it has to be remove from previous occupied positions
					oldPlayerRole.setSpielerId(0, this);
					if (oldPlayerRole.isSubstitutesMatchRoleId()) {
						removeObjectPlayerFromSubstitutions(playerID);
						// player can occupy multiple bench positions
						oldPlayerRole = getPositionByPlayerId(playerID);
						while (oldPlayerRole != null) {
							oldPlayerRole.setSpielerId(0, this);
							oldPlayerRole = getPositionByPlayerId(playerID);
						}
					}
				} else {
					// position is on bench (or backup), remove him from field position, but not from other bench positions
					if (oldPlayerRole.isFieldMatchRoleId()) {
						oldPlayerRole.setSpielerId(0, this);
					}
				}
			}
			position.setSpielerId(playerID, this);
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
	 *
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

	public List<MatchRoleID> getPenaltyTakers() {
		return this.penaltyTakers;
	}

	public void setPenaltyTakers(List<MatchRoleID> positions) {
		this.penaltyTakers = new ArrayList<>(positions);
		// chpp match order requires exactly 11 penalty takers
		for ( int i=this.penaltyTakers.size(); i<11; i++){
			this.penaltyTakers.add(new MatchRoleID(0,0,IMatchRoleID.NORMAL));
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
			return getPositionById(positionsid).getTactic();
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "getTactic4PositionID: " + e);
			return IMatchRoleID.UNKNOWN;
		}
	}

	public final float getTacticLevel(int type) {
		return switch (type) {
			case IMatchDetails.TAKTIK_PRESSING -> getTacticLevelPressing();
			case IMatchDetails.TAKTIK_KONTER -> getTacticLevelCounter();
			case IMatchDetails.TAKTIK_MIDDLE, IMatchDetails.TAKTIK_WINGS -> getTacticLevelAimAow();
			case IMatchDetails.TAKTIK_LONGSHOTS -> getTacticLevelLongShots();
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
	 * Delete lineup system.
	 */
	public final void AufstellungsSystemLoeschen(String name) {
		DBManager.instance().deleteSystem(NO_HRF_VERBINDUNG, name);
	}

	/**
	 * Check if the players are still in the team (not sold or fired).
	 */
	public final void checkAufgestellteSpieler() {

		//if (m_vPositionen != null) {
			for (IMatchRoleID pos : getPositionen()) {
				MatchRoleID position = (MatchRoleID) pos;
				// existiert Player noch ?
				if ((HOVerwaltung.instance().getModel() != null)
						&& (HOVerwaltung.instance().getModel().getCurrentPlayer(position.getPlayerId()) == null)) {
					// nein dann zuweisung aufheben
					position.setSpielerId(0, this);
				}
			}
		//}
	}

	/**
	 * Assitant to create automatically the lineup
	 */
	public final void optimizeLineup(List<Player> players, byte sectorsStrengthPriority, boolean withForm,
									 boolean idealPosFirst, boolean considerInjured, boolean considereSuspended) {
		m_clAssi.doLineup(getPositionen(), players, sectorsStrengthPriority, withForm, idealPosFirst,
				considerInjured, considereSuspended, getWeather());
		setAutoKicker(null);
		setAutoKapitaen(null);
	}

	/**
	 * Clone this lineup, creates and returns a new Lineup object.
	 */
	public final @Nullable Lineup duplicate() {
		final Properties properties = new Properties();
		Lineup clone = null;

		try {
			properties.setProperty("keeper",
					String.valueOf(getPositionById(IMatchRoleID.keeper).getPlayerId()));
			properties.setProperty("rightback",
					String.valueOf(getPositionById(IMatchRoleID.rightBack).getPlayerId()));
			properties.setProperty("rightcentraldefender", String.valueOf(getPositionById(
					IMatchRoleID.rightCentralDefender).getPlayerId()));
			properties.setProperty("middlecentraldefender", String.valueOf(getPositionById(
					IMatchRoleID.middleCentralDefender).getPlayerId()));
			properties.setProperty("leftcentraldefender", String.valueOf(getPositionById(
					IMatchRoleID.leftCentralDefender).getPlayerId()));
			properties.setProperty("leftback",
					String.valueOf(getPositionById(IMatchRoleID.leftBack).getPlayerId()));
			properties.setProperty("rightwinger",
					String.valueOf(getPositionById(IMatchRoleID.rightWinger).getPlayerId()));
			properties.setProperty("rightinnermidfield", String.valueOf(getPositionById(
					IMatchRoleID.rightInnerMidfield).getPlayerId()));
			properties.setProperty("middleinnermidfield", String.valueOf(getPositionById(
					IMatchRoleID.centralInnerMidfield).getPlayerId()));
			properties.setProperty("leftinnermidfield", String.valueOf(getPositionById(
					IMatchRoleID.leftInnerMidfield).getPlayerId()));
			properties.setProperty("leftwinger",
					String.valueOf(getPositionById(IMatchRoleID.leftWinger).getPlayerId()));
			properties.setProperty("rightforward",
					String.valueOf(getPositionById(IMatchRoleID.rightForward).getPlayerId()));
			properties.setProperty("centralforward", String.valueOf(getPositionById(
					IMatchRoleID.centralForward).getPlayerId()));
			properties.setProperty("leftforward",
					String.valueOf(getPositionById(IMatchRoleID.leftForward).getPlayerId()));

			properties.setProperty("substgk1",
					String.valueOf(getPositionById(IMatchRoleID.substGK1).getPlayerId()));
			properties.setProperty("substgk2",
					String.valueOf(getPositionById(IMatchRoleID.substGK2).getPlayerId()));

			properties.setProperty("substcd1",
					String.valueOf(getPositionById(IMatchRoleID.substCD1).getPlayerId()));
			properties.setProperty("substcd2",
					String.valueOf(getPositionById(IMatchRoleID.substCD2).getPlayerId()));

			properties.setProperty("substwb1",
					String.valueOf(getPositionById(IMatchRoleID.substWB1).getPlayerId()));
			properties.setProperty("substwb2",
					String.valueOf(getPositionById(IMatchRoleID.substWB2).getPlayerId()));

			properties.setProperty("substim1", String.valueOf(getPositionById(
					IMatchRoleID.substIM1).getPlayerId()));
			properties.setProperty("substim2", String.valueOf(getPositionById(
					IMatchRoleID.substIM2).getPlayerId()));

			properties.setProperty("substfw1",
					String.valueOf(getPositionById(IMatchRoleID.substFW1).getPlayerId()));
			properties.setProperty("substfw2",
					String.valueOf(getPositionById(IMatchRoleID.substFW2).getPlayerId()));

			properties.setProperty("substwi1",
					String.valueOf(getPositionById(IMatchRoleID.substWI1).getPlayerId()));
			properties.setProperty("substwi2",
					String.valueOf(getPositionById(IMatchRoleID.substWI2).getPlayerId()));

			properties.setProperty("substxt1",
					String.valueOf(getPositionById(IMatchRoleID.substXT1).getPlayerId()));
			properties.setProperty("substxt2",
					String.valueOf(getPositionById(IMatchRoleID.substXT2).getPlayerId()));


			properties.setProperty("order_rightback",
					String.valueOf(getPositionById(IMatchRoleID.rightBack).getTactic()));
			properties.setProperty("order_rightcentraldefender", String.valueOf(getPositionById(
					IMatchRoleID.rightCentralDefender).getTactic()));
			properties.setProperty("order_leftcentraldefender", String.valueOf(getPositionById(
					IMatchRoleID.leftCentralDefender).getTactic()));
			properties.setProperty("order_middlecentraldefender", String.valueOf(getPositionById(
					IMatchRoleID.middleCentralDefender).getTactic()));
			properties.setProperty("order_leftback",
					String.valueOf(getPositionById(IMatchRoleID.leftBack).getTactic()));
			properties.setProperty("order_rightwinger",
					String.valueOf(getPositionById(IMatchRoleID.rightWinger).getTactic()));
			properties.setProperty("order_rightinnermidfield", String.valueOf(getPositionById(
					IMatchRoleID.rightInnerMidfield).getTactic()));
			properties
					.setProperty("order_leftinnermidfield", String.valueOf(getPositionById(
							IMatchRoleID.leftInnerMidfield).getTactic()));
			properties.setProperty("order_centralinnermidfield", String.valueOf(getPositionById(
					IMatchRoleID.centralInnerMidfield).getTactic()));
			properties.setProperty("order_leftwinger",
					String.valueOf(getPositionById(IMatchRoleID.leftWinger).getTactic()));
			properties.setProperty("order_rightforward",
					String.valueOf(getPositionById(IMatchRoleID.rightForward).getTactic()));
			properties.setProperty("order_leftforward",
					String.valueOf(getPositionById(IMatchRoleID.leftForward).getTactic()));
			properties.setProperty("order_centralforward",
					String.valueOf(getPositionById(IMatchRoleID.centralForward).getTactic()));


			properties.setProperty("kicker1", String.valueOf(getKicker()));
			properties.setProperty("captain", String.valueOf(getCaptain()));
			properties.setProperty("tactictype", String.valueOf(getTacticType()));
			properties.setProperty("installning", String.valueOf(getAttitude()));
			properties.setProperty("styleofplay", String.valueOf(getStyleOfPlay()));
			for (int i = 0; i < this.substitutions.size(); i++) {
				Substitution sub = this.substitutions.get(i);
				if (sub != null) {
					properties.setProperty("subst" + i + "playerorderid",
							String.valueOf(sub.getPlayerOrderId()));
					properties.setProperty("subst" + i + "playerin",
							String.valueOf(sub.getObjectPlayerID()));
					properties.setProperty("subst" + i + "playerout",
							String.valueOf(sub.getSubjectPlayerID()));
					properties.setProperty("subst" + i + "ordertype",
							String.valueOf(sub.getOrderType().getId()));
					properties.setProperty("subst" + i + "matchminutecriteria",
							String.valueOf(sub.getMatchMinuteCriteria()));
					properties.setProperty("subst" + i + "pos", String.valueOf(sub.getRoleId()));
					properties.setProperty("subst" + i + "behaviour",
							String.valueOf(sub.getBehaviour()));
					properties.setProperty("subst" + i + "card",
							String.valueOf(sub.getRedCardCriteria().getId()));
					properties.setProperty("subst" + i + "standing",
							String.valueOf(sub.getStanding().getId()));
				}
			}

			clone = new Lineup(properties);
			clone.setPenaltyTakers(getPenaltyTakers());
			clone.setLocation(getLocation());
			clone.setPullBackMinute(getPullBackMinute());
			clone.setWeather(getWeather());
			clone.setWeatherForecast(getWeatherForecast());
			clone.setArenaId(getArenaId());
			clone.setRegionId(getRegionId());

		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "Aufstellung.duplicate: " + e);
		}
		return clone;
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
	 * Load a lineup by name.
	 */
	public final void load(String name) {
		final Lineup temp = DBManager.instance().getAufstellung(NO_HRF_VERBINDUNG, name);
		m_vFieldPositions = temp.getFieldPositions();
		m_vBenchPositions = temp.getBenchPositions();
		m_iKicker = temp.getKicker();
		m_iKapitaen = temp.getCaptain();
	}

	/**
	 * Remove all players from all positions.
	 */
	public final void resetStartingLineup() {
		m_clAssi.resetPositionsbesetzungen(getPositionen());
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
	 * Save a lineup using the given name.
	 */
	public final void save(int sourceSystem, final String name) {
		DBManager.instance().saveAufstellung(sourceSystem, NO_HRF_VERBINDUNG, this, name);
	}

	/**
	 * Save a lineup.
	 */
	public final void save4HRF(int sourceSystem) {
		DBManager.instance().saveAufstellung(sourceSystem, HOVerwaltung.instance().getModel().getID(), this,
				"HRF");
	}

	/**
	 * Save the current system in the DB.
	 */
	public final void saveAufstellungsSystem(String name) {
		DBManager.instance().saveSystemPositionen(NO_HRF_VERBINDUNG, getPositionen(), name);
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

		for (IMatchRoleID pos : m_vFieldPositions) {
			MatchRoleID position = (MatchRoleID) pos;
			if ((positionId == position.getPosition())
					&& (position.getId() < IMatchRoleID.startReserves)
					&& (position.getPlayerId() > 0)) {
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

		for (IMatchRoleID pos : m_vFieldPositions) {
			MatchRoleID position = (MatchRoleID) pos;
			if (position.getPlayerId() != 0) numPlayers++;
		    }
		return numPlayers != 11;

	}

	/**
	 * Calculate player strength for the given position.
	 */
	private float calcPlayerStrength(List<Player> players, int playerID, byte position, boolean considerForm, @Nullable Weather weather, boolean useWeatherImpact) {
		if (players != null) {
			for (Player player : players) {
				if (player.getPlayerID() == playerID) {
					return player.calcPosValue(position, considerForm, weather, useWeatherImpact);
				}
			}
		}
		return 0.0f;
	}

	/**
	 * Calculate team strength for the given position.
	 */
	private float calcTeamStrength(List<Player> players, byte positionId, boolean useForm, @Nullable Weather weather, boolean useWeatherImpact) {
		float stk = 0.0f;
		if (players != null) {
			for (IMatchRoleID pos : m_vFieldPositions) {
				MatchRoleID position = (MatchRoleID) pos;
				if (position.getPosition() == positionId) {
					stk += calcPlayerStrength(players, position.getPlayerId(), positionId, useForm, weather, useWeatherImpact);
				}
			}
		}
		return Helper.round(stk, 1);
	}



	/**
	 * Initializes the 553 lineup
	 */
	private void initPositionen553() {
		if (m_vFieldPositions != null) {
			m_vFieldPositions.removeAllElements();
		} else m_vFieldPositions = new Vector<>();
		if (m_vBenchPositions != null) {
			m_vBenchPositions.removeAllElements();
		} else m_vBenchPositions = new Vector<>();

		m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.keeper, 0, (byte) 0));
		m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.rightBack, 0, (byte) 0));
		m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.rightCentralDefender, 0, (byte) 0));
		m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.middleCentralDefender, 0, (byte) 0));
		m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.leftCentralDefender, 0, (byte) 0));
		m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.leftBack, 0, (byte) 0));
		m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.rightWinger, 0, (byte) 0));
		m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.rightInnerMidfield, 0, (byte) 0));
		m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.centralInnerMidfield, 0, (byte) 0));
		m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.leftInnerMidfield, 0, (byte) 0));
		m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.leftWinger, 0, (byte) 0));
		m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.rightForward, 0, (byte) 0));
		m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.centralForward, 0, (byte) 0));
		m_vFieldPositions.add(new MatchRoleID(IMatchRoleID.leftForward, 0, (byte) 0));

		m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substGK1, 0, (byte) 0));
		m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substCD1, 0, (byte) 0));
		m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substWB1, 0, (byte) 0));
		m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substIM1, 0, (byte) 0));
		m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substFW1, 0, (byte) 0));
		m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substWI1, 0, (byte) 0));
		m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substXT1, 0, (byte) 0));

		m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substGK2, 0, (byte) 0));
		m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substCD2, 0, (byte) 0));
		m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substWB2, 0, (byte) 0));
		m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substIM2, 0, (byte) 0));
		m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substFW2, 0, (byte) 0));
		m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substWI2, 0, (byte) 0));
		m_vBenchPositions.add(new MatchRoleID(IMatchRoleID.substXT2, 0, (byte) 0));

		for (int i = 0; i < 10; i++) {
			penaltyTakers.add(new MatchRoleID(IMatchRoleID.penaltyTaker1 + i, 0, (byte) 0));
		}
	}

	/**
	 * Swap 2 players.
	 */
	private MatchRoleID swap(Object object, Object object2) {
		final MatchRoleID sp = (MatchRoleID) object;
		final MatchRoleID sp2 = (MatchRoleID) object2;
		return new MatchRoleID(sp.getId(), sp2.getPlayerId(), sp2.getTactic());
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
	public void UpdateLineupWithMatchOrder(Substitution sub){
		MatchRoleID matchRoleIDPlayer, matchRoleIDaffectedPlayer;
		int newRoleId;
		byte tactic;

		Player ObjectPlayer;
		switch (sub.getOrderType()) {
			case SUBSTITUTION:
				matchRoleIDaffectedPlayer = this.getPositionByPlayerId(sub.getSubjectPlayerID());
				if (matchRoleIDaffectedPlayer == null)
				{
					HOLogger.instance().warning(Lineup.class, String.format("The player id: %s cannot do the substitution", sub.getSubjectPlayerID()));
					break;
				}

				matchRoleIDPlayer = getPositionByPlayerId(sub.getObjectPlayerID());
				if (matchRoleIDPlayer==null)
				{
					HOLogger.instance().warning(Lineup.class, String.format("The substitution of player id: %s has not been recognized", sub.getObjectPlayerID()));
					break;
				}
				ObjectPlayer = this.getPlayerByPositionID(matchRoleIDPlayer.getId());
				if (ObjectPlayer == null)
				{
					HOLogger.instance().warning(Lineup.class, String.format("The player id: %s cannot do the substitution", sub.getObjectPlayerID()));
					break;
				}
				ObjectPlayer.setGameStartingTime(sub.getMatchMinuteCriteria());
				tactic = sub.getBehaviour();
				if (tactic == -1) tactic = matchRoleIDaffectedPlayer.getTactic();
				newRoleId = sub.getRoleId();
				if ( newRoleId != -1 ) {
					if (  getPositionById(newRoleId).getPlayerId() == 0){
						if ( newRoleId != matchRoleIDaffectedPlayer.getId() ) {
							setSpielerAtPosition(matchRoleIDaffectedPlayer.getId(), 0, MatchRoleID.NORMAL);  // clear old position
						}
					}
					else {
						HOLogger.instance().warning(Lineup.class, String.format("The player id: %s cannot do the substitution. Position is not free.", sub.getObjectPlayerID()));
						break;
					}
				}
				else {
					newRoleId = matchRoleIDaffectedPlayer.getId();
				}
				setSpielerAtPosition(newRoleId, matchRoleIDPlayer.getPlayerId(), tactic);
				break;

			case POSITION_SWAP:
				matchRoleIDaffectedPlayer = getPositionByPlayerId(sub.getSubjectPlayerID());
				matchRoleIDPlayer = getPositionByPlayerId(sub.getObjectPlayerID());
				if ( matchRoleIDaffectedPlayer != null && matchRoleIDPlayer != null ){
					matchRoleIDaffectedPlayer.setSpielerId(sub.getObjectPlayerID());
					matchRoleIDPlayer.setSpielerId(sub.getSubjectPlayerID());
				}
				else {
					if ( matchRoleIDaffectedPlayer == null ){
						HOLogger.instance().warning(Lineup.class, String.format("The player id: %s is (no longer) in lineup.", sub.getSubjectPlayerID()));
					}
					if ( matchRoleIDPlayer == null ){
						HOLogger.instance().warning(Lineup.class, String.format("The player id: %s is (no longer) in lineup.", sub.getObjectPlayerID()));
					}
				}
				break;

			case NEW_BEHAVIOUR:
				newRoleId = sub.getRoleId();
				matchRoleIDaffectedPlayer = getPositionByPlayerId(sub.getSubjectPlayerID());
				if (matchRoleIDaffectedPlayer == null)
				{
					HOLogger.instance().warning(Lineup.class, String.format("The player id: %s cannot do the substitution", sub.getSubjectPlayerID()));
					break;
				}
				if ( newRoleId == -1 )  newRoleId = matchRoleIDaffectedPlayer.getId();
				else if ( newRoleId != matchRoleIDaffectedPlayer.getId()
						&& getPositionById(newRoleId).getPlayerId() > 0 ){
					HOLogger.instance().warning(Lineup.class, String.format("The player id: %s cannot do the substitution. Position is not free.", sub.getObjectPlayerID()));
					break;
				}
				tactic = sub.getBehaviour();
				if ( tactic == -1)  tactic = MatchRoleID.NORMAL;
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

		for (IMatchRoleID pos : m_vFieldPositions) {
			MatchRoleID mid = (MatchRoleID) pos;
			Player p = this.getPlayerByPositionID(mid.getId());
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

		for (IMatchRoleID pos : m_vFieldPositions) {
			MatchRoleID mid = (MatchRoleID) pos;
			Player p = this.getPlayerByPositionID(mid.getId());
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
