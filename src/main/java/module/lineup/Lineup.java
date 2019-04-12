package module.lineup;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.IMatchDetails;
import core.model.match.MatchKurzInfo;
import core.model.match.Weather;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.rating.RatingPredictionConfig;
import core.rating.RatingPredictionManager;
import core.util.HOLogger;
import core.util.Helper;
import core.util.StringUtils;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.RedCardCriteria;
import module.lineup.substitution.model.Substitution;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.*;

/**
 * 
 * Blaghaid moves it to a 553 model. Lots of changes. This is the model
 * responsible for holding the lineup used in predictions and other things.
 * 
 * @author thomas.werth
 */
public class Lineup {

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

	/** Aufstellungsassistent */
	private LineupAssistant m_clAssi = new LineupAssistant();

	/** positions */
	private Vector<IMatchRoleID> m_vPositionen = new Vector<IMatchRoleID>();
	private List<Substitution> substitutions = new ArrayList<Substitution>();
	private List<MatchRoleID> penaltyTakers = new ArrayList<MatchRoleID>();

	/** Attitude */
	private int m_iAttitude;

	/** captain */
	private int m_iKapitaen = -1;

	/** set pieces take */
	private int m_iKicker = -1;

	/** TacticType */
	private int m_iTacticType;
	
	/** Style of play */
	private int m_iStyleOfPlay;
	
	/** PullBackMinute **/
	private int pullBackMinute = 90; // no pull back

	/** Home/Away/AwayDerby */
	private short m_sLocation = -1;

	private boolean pullBackOverride;
	
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
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.keeper, Integer
					.parseInt(properties.getProperty("keeper", "0")), (byte) 0));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.rightBack, Integer
					.parseInt(properties.getProperty("rightback", "0")), Byte.parseByte(properties
					.getProperty("behrightback", "0"))));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.rightCentralDefender, Integer
					.parseInt(properties.getProperty("insideback1", "0")), Byte
					.parseByte(properties.getProperty("behinsideback1", "0"))));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.leftCentralDefender, Integer
					.parseInt(properties.getProperty("insideback2", "0")), Byte
					.parseByte(properties.getProperty("behinsideback2", "0"))));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.middleCentralDefender, Integer
					.parseInt(properties.getProperty("insideback3", "0")), Byte
					.parseByte(properties.getProperty("behinsideback3", "0"))));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.leftBack, Integer
					.parseInt(properties.getProperty("leftback", "0")), Byte.parseByte(properties
					.getProperty("behleftback", "0"))));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.rightWinger, Integer
					.parseInt(properties.getProperty("rightwinger", "0")), Byte
					.parseByte(properties.getProperty("behrightwinger", "0"))));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.rightInnerMidfield, Integer
					.parseInt(properties.getProperty("insidemid1", "0")), Byte.parseByte(properties
					.getProperty("behinsidemid1", "0"))));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.leftInnerMidfield, Integer
					.parseInt(properties.getProperty("insidemid2", "0")), Byte.parseByte(properties
					.getProperty("behinsidemid2", "0"))));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.centralInnerMidfield, Integer
					.parseInt(properties.getProperty("insidemid3", "0")), Byte.parseByte(properties
					.getProperty("behinsidemid3", "0"))));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.leftWinger, Integer
					.parseInt(properties.getProperty("leftwinger", "0")), Byte.parseByte(properties
					.getProperty("behleftwinger", "0"))));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.rightForward, Integer
					.parseInt(properties.getProperty("forward1", "0")), Byte.parseByte(properties
					.getProperty("behforward1", "0"))));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.leftForward, Integer
					.parseInt(properties.getProperty("forward2", "0")), Byte.parseByte(properties
					.getProperty("behforward2", "0"))));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.centralForward, Integer
					.parseInt(properties.getProperty("forward3", "0")), Byte.parseByte(properties
					.getProperty("behforward3", "0"))));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.substGK1, Integer.parseInt(properties.getProperty("substgk1", "0")), (byte) 0));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.substGK2, Integer.parseInt(properties.getProperty("substgk2", "0")), (byte) 0));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.substCD1, Integer.parseInt(properties.getProperty("substcd1", "0")), (byte) 0));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.substCD2, Integer.parseInt(properties.getProperty("substcd2", "0")), (byte) 0));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.substWB1, Integer.parseInt(properties.getProperty("substwb1", "0")), (byte) 0));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.substWB2, Integer.parseInt(properties.getProperty("substwb2", "0")), (byte) 0));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.substIM1, Integer.parseInt(properties.getProperty("substim1", "0")), (byte) 0));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.substIM2, Integer.parseInt(properties.getProperty("substim2", "0")), (byte) 0));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.substWI1, Integer.parseInt(properties.getProperty("substwi1", "0")), (byte) 0));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.substWI2, Integer.parseInt(properties.getProperty("substwi2", "0")), (byte) 0));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.substFW1, Integer.parseInt(properties.getProperty("substfw1", "0")), (byte) 0));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.substFW2, Integer.parseInt(properties.getProperty("substfw2", "0")), (byte) 0));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.substXT1, Integer.parseInt(properties.getProperty("substxt1", "0")), (byte) 0));
			m_vPositionen.add(new MatchRoleID(IMatchRoleID.substXT2, Integer.parseInt(properties.getProperty("substxt2", "0")), (byte) 0));

			m_iTacticType = Integer.parseInt(properties.getProperty("tactictype", "0"));
			// bugfix: i had a HRF with installning=null (the string null)
			String installning = properties.getProperty("installning");
			
			try {
				m_iAttitude = Integer.parseInt(installning);
			} catch (Exception e) {
				HOLogger.instance().warning(getClass(), "Failed to parse attitude: " + installning);
				m_iAttitude = IMatchDetails.EINSTELLUNG_NORMAL;
			}
			
			m_iStyleOfPlay = Integer.parseInt(properties.getProperty("styleofplay", "0"));
			// and read the sub contents
			for (int i = 0; i < 5; i++) {

				if ((properties.getProperty("subst" + i + "playerorderid") != null)
						&& (Integer.parseInt(properties.getProperty("subst" + i + "playerorderid")) >= 0)) {

					byte orderTypeId = Byte.parseByte(properties.getProperty("subst" + i
							+ "ordertype"));
					int playerIn = Integer.parseInt(properties
							.getProperty("subst" + i + "playerin"));
					int playerOut = Integer.parseInt(properties.getProperty("subst" + i
							+ "playerout"));
					MatchOrderType matchOrderType;
					if (orderTypeId == 3) {
						matchOrderType = MatchOrderType.POSITION_SWAP;
					} else {
						if (playerIn == playerOut) {
							matchOrderType = MatchOrderType.NEW_BEHAVIOUR;
						} else if ((playerIn <= 0 || playerOut <= 0)) {
							// allows the correct retrieval of some cases
							matchOrderType = MatchOrderType.NEW_BEHAVIOUR;
						} else {
							matchOrderType = MatchOrderType.SUBSTITUTION;
						}
					}

					Substitution sub = new Substitution();
					sub.setPlayerOrderId(Integer.parseInt(properties.getProperty("subst" + i
							+ "playerorderid")));
					sub.setObjectPlayerID(playerIn);
					sub.setSubjectPlayerID(playerOut);
					sub.setOrderType(matchOrderType);
					sub.setMatchMinuteCriteria(Byte.parseByte(properties.getProperty("subst" + i
							+ "matchminutecriteria")));
					sub.setRoleId(Byte.parseByte(properties.getProperty("subst" + i + "pos")));
					sub.setBehaviour(Byte.parseByte(properties.getProperty("subst" + i
							+ "behaviour")));
					sub.setRedCardCriteria(RedCardCriteria.getById(Byte.parseByte(properties
							.getProperty("subst" + i + "card"))));
					
					// bugfix: i had a HRF where subst0standing was missing
					String val = properties.getProperty("subst" + i + "standing");
					if (StringUtils.isNumeric(val)) {
						sub.setStanding(GoalDiffCriteria.getById(Byte.parseByte(val)));
					} else {
						sub.setStanding(GoalDiffCriteria.ANY_STANDING);
					}
					
					this.substitutions.add(sub);
				} else {
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
			m_vPositionen.removeAllElements();
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

	/**
	 * get the tactic level for AiM/AoW
	 * 
	 * @return tactic level
	 */
	public final float getTacticLevelAimAow() {
		return Math.max(1, new RatingPredictionManager(this, HOVerwaltung.instance().getModel()
				.getTeam(),
				(short) HOVerwaltung.instance().getModel().getTrainer().getTrainerTyp(),
				m_iStyleOfPlay, RatingPredictionConfig.getInstance()).getTacticLevelAowAim());
	}

	/**
	 * get the tactic level for counter
	 * 
	 * @return tactic level
	 */
	public final float getTacticLevelCounter() {
		return Math.max(1, new RatingPredictionManager(this, HOVerwaltung.instance().getModel()
				.getTeam(),
				(short) HOVerwaltung.instance().getModel().getTrainer().getTrainerTyp(), m_iStyleOfPlay,
				RatingPredictionConfig.getInstance()).getTacticLevelCounter());
	}

	/**
	 * get the tactic level for pressing
	 * 
	 * @return tactic level
	 */
	public final float getTacticLevelPressing() {
		return Math.max(1, new RatingPredictionManager(this, HOVerwaltung.instance().getModel()
				.getTeam(),
				(short) HOVerwaltung.instance().getModel().getTrainer().getTrainerTyp(), m_iStyleOfPlay,
				RatingPredictionConfig.getInstance()).getTacticLevelPressing());
	}

	/**
	 * get the tactic level for Long Shots
	 * 
	 * @return tactic level
	 */
	public final float getTacticLevelLongShots() {
		return Math.max(1, new RatingPredictionManager(this, HOVerwaltung.instance().getModel()
				.getTeam(),
				(short) HOVerwaltung.instance().getModel().getTrainer().getTrainerTyp(), m_iStyleOfPlay,
				RatingPredictionConfig.getInstance()).getTacticLevelLongShots());
	}

	/**
	 * Calculates the total star rating for defense This is CA-rating?
	 */
	public final float getAWTeamStk(List<Player> player, boolean mitForm) {
		float stk = 0.0f;
		stk += calcTeamStk(player, IMatchRoleID.CENTRAL_DEFENDER, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.CENTRAL_DEFENDER_OFF, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.CENTRAL_DEFENDER_TOWING, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.BACK, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.BACK_OFF, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.BACK_DEF, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.BACK_TOMID, mitForm);

		return Helper.round(stk, 1);
	}

	/**
	 * Setter for property m_iAttitude.
	 * 
	 * @param m_iAttitude
	 *            New value of property m_iAttitude.
	 */
	public final void setAttitude(int m_iAttitude) {
		this.m_iAttitude = m_iAttitude;
	}

	/**
	 * Getter for property m_iAttitude.
	 * 
	 * @return Value of property m_iAttitude.
	 */
	public final int getAttitude() {
		return m_iAttitude;
	}
	
	public String getAttitudeName(int attitude) {
		HOVerwaltung hov = HOVerwaltung.instance();
		String attitudeName;
		
		switch(attitude) {
		case core.model.match.IMatchDetails.EINSTELLUNG_NORMAL:
			attitudeName = hov.getLanguageString("ls.team.teamattitude_short.normal");
			break;
		case core.model.match.IMatchDetails.EINSTELLUNG_PIC:
			attitudeName = hov.getLanguageString("ls.team.teamattitude_short.playitcool");
			break;
		case core.model.match.IMatchDetails.EINSTELLUNG_MOTS:
			attitudeName = hov.getLanguageString("ls.team.teamattitude_short.matchoftheseason");
			break;
		default:
			attitudeName = HOVerwaltung.instance().getLanguageString("Unbestimmt");
			break;
		}
		return attitudeName;
	}

	public void setStyleOfPlay(int style) {
		m_iStyleOfPlay = style;
	}
	
	public int getStyleOfPlay() {
		return m_iStyleOfPlay;
	}

	/**
	 * Auto-select the set best captain.
	 */
	public final void setAutoKapitaen(List<Player> players) {
		float maxValue = -1;

		if (players == null) {
			players = HOVerwaltung.instance().getModel().getAllSpieler();
		}

		if (players != null) {
			for (Player player : players) {
				if (m_clAssi.isPlayerInStartingEleven(player.getSpielerID(), m_vPositionen)) {
					int curPlayerId = player.getSpielerID();
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
	public final void setAutoKicker(List<Player> players) {
		double maxStandard = -1;
		int form = -1;

		if (players == null) {
			players = HOVerwaltung.instance().getModel().getAllSpieler();
		}

		Vector<IMatchRoleID> noKeeper = new Vector<IMatchRoleID>(m_vPositionen);

		for (IMatchRoleID pos : noKeeper) {
			MatchRoleID p = (MatchRoleID) pos;
			if (p.getId() == IMatchRoleID.keeper) {
				noKeeper.remove(pos);
				break;
			}
		}

		if (players != null) {
			for (Player player : players) {
				if (m_clAssi.isPlayerInStartingEleven(player.getSpielerID(), noKeeper)) {
					double sp = (double) player.getStandards()
							+ player.getSubskill4Pos(PlayerSkill.SET_PIECES)
							+ RatingPredictionManager.getLoyaltyHomegrownBonus(player);
					if (sp > maxStandard) {
						maxStandard = sp;
						form = player.getForm();
						m_iKicker = player.getSpielerID();
					} else if ((sp == maxStandard) && (form < player.getForm())) {
						maxStandard = sp;
						form = player.getForm();
						m_iKicker = player.getSpielerID();
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
	 * captain
	 * 
	 * @param captainsId
	 *            use this player as captain (<= 0 for current captain)
	 */
	public final float getAverageExperience(int captainsId) {
		float value = 0;

		Player captain = null;
		List<Player> players = HOVerwaltung.instance().getModel().getAllSpieler();

		if (players != null) {
			for (Player player : players) {
				if (m_clAssi.isPlayerInStartingEleven(player.getSpielerID(), m_vPositionen)) {
					value += player.getErfahrung();
					if (captainsId > 0) {
						if (captainsId == player.getSpielerID()) {
							captain = (Player) player;
						}
					} else if (m_iKapitaen == player.getSpielerID()) {
						captain = (Player) player;
					}
				}
			}
		}
		if (captain != null) {
			value = ((float) (value + captain.getErfahrung()) / 12)
					* (1f - (float) (7 - captain.getFuehrung()) * 0.05f);
		} else {
			// HOLogger.instance().log(getClass(),
			// "Can't calc average experience, captain not set.");
			value = -1f;
		}
		return value;
	}

	/**
	 * Predicts Central Attack-Rating
	 */
	public final double getCentralAttackRating() {
		if (HOVerwaltung.instance().getModel() != null
				&& HOVerwaltung.instance().getModel().getID() != -1) {
			final RatingPredictionManager rpManager = new RatingPredictionManager(this,
					HOVerwaltung.instance().getModel().getTeam(), (short) HOVerwaltung.instance()
							.getModel().getTrainer().getTrainerTyp(), m_iStyleOfPlay,
					RatingPredictionConfig.getInstance());
			// ruft konvertiertes Plugin ( in Manager ) auf und returned den
			// Wert
			double value = Math.max(1, rpManager.getCentralAttackRatings());
			if (value > 1) {
				value += UserParameter.instance().middleAttackOffset;
			}
			return value;
		} else {
			return 0.0d;
		}
	}

	/**
	 * Predicts cd-Rating
	 */
	public final double getCentralDefenseRating() {
		if (HOVerwaltung.instance().getModel() != null
				&& HOVerwaltung.instance().getModel().getID() != -1) {
			final RatingPredictionManager rpManager = new RatingPredictionManager(this,
					HOVerwaltung.instance().getModel().getTeam(), (short) HOVerwaltung.instance()
							.getModel().getTrainer().getTrainerTyp(), m_iStyleOfPlay,
					RatingPredictionConfig.getInstance());

			double value = Math.max(1, rpManager.getCentralDefenseRatings());
			if (value > 1) {
				value += UserParameter.instance().middleDefenceOffset;
			}
			return value;
		} else {
			return 0.0d;
		}
	}

	/**
	 * Total strength.
	 */
	public final float getGesamtStaerke(List<Player> player, boolean useForm) {
		return Helper.round(getTWTeamStk(player, useForm) + getAWTeamStk(player, useForm) //
				+ getMFTeamStk(player, useForm) + getSTTeamStk(player, useForm), 1);
	}

	/**
	 * Get the HT stats value for the lineup.
	 */
	public final int getHATStats() {
		int sum;
		final int MFfactor = 3;

		sum = HTfloat2int(getMidfieldRating()) * MFfactor;

		sum += HTfloat2int(getLeftDefenseRating());
		sum += HTfloat2int(getCentralDefenseRating());
		sum += HTfloat2int(getRightDefenseRating());

		sum += HTfloat2int(getLeftAttackRating());
		sum += HTfloat2int(getCentralAttackRating());
		sum += HTfloat2int(getRightAttackRating());

		return sum;
	}

	public void updateRatingPredictionConfig() {
		int vt = atLeastOne(getAnzInnenverteidiger());
		int im = atLeastOne(getAnzInneresMittelfeld());
		int st = atLeastOne(getAnzSturm());
		String predictionName = vt + "D+" + im + "M+" + st + "F";
		RatingPredictionConfig.setInstancePredictionName(predictionName);
	}

	private int atLeastOne(int count) {
		return count == 0 ? 1 : count;
	}

	/**
	 * Setter for property m_iKapitaen.
	 * 
	 * @param m_iKapitaen
	 *            New value of property m_iKapitaen.
	 */
	public final void setKapitaen(int m_iKapitaen) {
		this.m_iKapitaen = m_iKapitaen;
	}

	/**
	 * Getter for property m_iKapitaen.
	 * 
	 * @return Value of property m_iKapitaen.
	 */
	public final int getKapitaen() {
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
	 * Predicts LeftAttack-Rating
	 */
	public final double getLeftAttackRating() {
		if (HOVerwaltung.instance().getModel() != null
				&& HOVerwaltung.instance().getModel().getID() != -1) {
			final RatingPredictionManager rpManager = new RatingPredictionManager(this,
					HOVerwaltung.instance().getModel().getTeam(), (short) HOVerwaltung.instance()
							.getModel().getTrainer().getTrainerTyp(), m_iStyleOfPlay,
					RatingPredictionConfig.getInstance());

			// ruft konvertiertes Plugin ( in Manager ) auf und returned den
			// Wert
			double value = Math.max(1, rpManager.getLeftAttackRatings());
			if (value > 1) {
				value += UserParameter.instance().leftAttackOffset;
			}
			return value;
		} else {
			return 0.0d;
		}
	}

	/**
	 * Predicts LeftDefense-Rating
	 */
	public final double getLeftDefenseRating() {
		if (HOVerwaltung.instance().getModel() != null
				&& HOVerwaltung.instance().getModel().getID() != -1) {
			final RatingPredictionManager rpManager = new RatingPredictionManager(this,
					HOVerwaltung.instance().getModel().getTeam(), (short) HOVerwaltung.instance()
							.getModel().getTrainer().getTrainerTyp(), m_iStyleOfPlay,
					RatingPredictionConfig.getInstance());

			// ruft konvertiertes Plugin ( in Manager ) auf und returned den
			// Wert
			double value = Math.max(1, rpManager.getLeftDefenseRatings());
			if (value > 1) {
				value += UserParameter.instance().leftDefenceOffset;
			}
			return value;
		} else {
			return 0.0d;
		}
	}

	/**
	 * Get the Loddar stats value for the lineup.
	 */
	public final float getLoddarStats() {
		LoddarStatsCalculator calculator = new LoddarStatsCalculator();
		calculator.setRatings(getMidfieldRating(), getRightDefenseRating(),
				getCentralDefenseRating(), getLeftDefenseRating(), getRightAttackRating(),
				getCentralAttackRating(), getLeftAttackRating());
		calculator.setTactics(getTacticType(), getTacticLevelAimAow(), getTacticLevelCounter());
		return calculator.calculate();
	}

	/**
	 * convert reduced float rating (1.00....20.99) to original integer HT
	 * rating (1...80) one +0.5 is because of correct rounding to integer
	 */
	public static final int HTfloat2int(double x) {
		return (int) (((x - 1.0f) * 4.0f) + 1.0f);
	}

	/**
	 * Midfield and winger total star rating.
	 */
	public final float getMFTeamStk(List<Player> player, boolean mitForm) {
		float stk = 0.0f;
		stk += calcTeamStk(player, IMatchRoleID.MIDFIELDER, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.WINGER, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.MIDFIELDER_OFF, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.WINGER_OFF, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.MIDFIELDER_DEF, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.WINGER_DEF, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.MIDFIELDER_TOWING, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.WINGER_TOMID, mitForm);

		return Helper.round(stk, 1);
	}

	// ///////////////////////////////////////////////////////////////////////////////
	// Ratings
	// ///////////////////////////////////////////////////////////////////////////////

	/**
	 * Predicts MF-Rating
	 */
	public final double getMidfieldRating() {
		if (HOVerwaltung.instance().getModel() != null
				&& HOVerwaltung.instance().getModel().getID() != -1) {
			final RatingPredictionManager rpManager = new RatingPredictionManager(this,
					HOVerwaltung.instance().getModel().getTeam(), (short) HOVerwaltung.instance()
							.getModel().getTrainer().getTrainerTyp(), m_iStyleOfPlay,
					RatingPredictionConfig.getInstance());
			// ruft konvertiertes Plugin ( in Manager ) auf und returned den
			// Wert
			double value = Math.max(1, rpManager.getMFRatings());
			if (value > 1) {
				value += UserParameter.instance().midfieldOffset;
			}
			return value;
		} else {
			return 0.0d;
		}
	}

	/**
	 * Get the short name for a fomation constant.
	 */
	public static String getNameForSystem(byte system) {
		String name;

		switch (system) {
		case SYS_451:
			name = "4-5-1";
			break;

		case SYS_352:
			name = "3-5-2";
			break;

		case SYS_442:
			name = "4-4-2";
			break;

		case SYS_343:
			name = "3-4-3";
			break;

		case SYS_433:
			name = "4-3-3";
			break;

		case SYS_532:
			name = "5-3-2";
			break;

		case SYS_541:
			name = "5-4-1";
			break;

		case SYS_523:
			name = "5-2-3";
			break;

		case SYS_550:
			name = "5-5-0";
			break;

		case SYS_253:
			name = "2-5-3";
			break;

		default:
			name = HOVerwaltung.instance().getLanguageString("Unbestimmt");
			break;
		}

		return name;
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
	 * @param m_sHeimspiel
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
		if (m_sLocation < 0) {
			try {
				final int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
				final MatchKurzInfo[] matches = DBManager.instance().getMatchesKurzInfo(teamId,
						MatchKurzInfo.UPCOMING);
				MatchKurzInfo match = null;

				if ((matches == null) || (matches.length < 1)) {
					m_sLocation = IMatchDetails.LOCATION_AWAY;
					HOLogger.instance().error(getClass(), "no match to determine location");
					return m_sLocation;
				}

				final List<MatchKurzInfo> sMatches = orderMatches(matches);
				match = sMatches.get(0);

				if (match == null) {
					m_sLocation = IMatchDetails.LOCATION_AWAY;
					HOLogger.instance().error(getClass(), "no match to determine location");
					return m_sLocation;
				}

				if (match.getMatchTyp().isOfficial()) {m_sLocation = (match.getHeimID() == teamId) ? IMatchDetails.LOCATION_HOME: IMatchDetails.LOCATION_AWAY;}
				else {m_sLocation = IMatchDetails.LOCATION_TOURNAMENT;}

				// TODO: Manage away derby : To decide away derby we need hold of the region (likely both download and db work needed)

			} catch (Exception e) {
				HOLogger.instance().error(getClass(), "getHeimspiel: " + e);
				m_sLocation = 0;
			}
		}
		// HOLogger.instance().debug(getClass(), "getHeimspiel: " +
		// m_sLocation);
		return m_sLocation;
	}

	/**
	 * For some reason we have users with old "upcoming" matches which break the
	 * location determination. This method removes all matches that are more
	 * than 8 days older than the previous 'upcoming' match.
	 * 
	 * This method also returns the matches in order of the newest first.
	 */
	private List<MatchKurzInfo> orderMatches(MatchKurzInfo[] inMatches) {
		final List<MatchKurzInfo> matches = new ArrayList<MatchKurzInfo>();
		if (inMatches != null && inMatches.length > 0) {
			for (MatchKurzInfo m : inMatches) {
				matches.add(m);
			}

			// Flipped the sign of the return value to get newest first -
			// blaghaid
			Collections.sort(matches, new Comparator<MatchKurzInfo>() {
				@Override
				public int compare(MatchKurzInfo o1, MatchKurzInfo o2) {
					return (o1.getMatchDateAsTimestamp().compareTo(o2.getMatchDateAsTimestamp()));
				}
			});

			Timestamp checkDate = null;
			for (Iterator<MatchKurzInfo> i = matches.iterator(); i.hasNext();) {
				MatchKurzInfo m = i.next();
				if (checkDate == null) {
					checkDate = m.getMatchDateAsTimestamp();
					continue;
				}
				if (m.getMatchDateAsTimestamp().getTime() < (checkDate.getTime() - 8 * 24 * 60 * 60
						* 1000L)) { // older
									// than
									// 8
									// days
					HOLogger.instance().warning(
							getClass(),
							"Old match with status UPCOMING! " + m.getMatchID() + " from "
									+ m.getMatchDate());
					i.remove();
				} else {
					checkDate = m.getMatchDateAsTimestamp();
				}
			}
		}
		return matches;
	}

	/**
	 * Umrechnung von double auf 1-80 int
	 * 
	 * @deprecated use RatingUtil.getIntValue4Rating(double rating) instead
	 * @param rating
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
					.getSpieler(getPositionById(positionId).getSpielerId());
		} catch (Exception e) {
			HOLogger.instance()
					.error(getClass(), "getPlayerByPositionID(" + positionId + "): " + e);
			return null;
		}
	}

	/**
	 * Get the position object by position id.
	 */
	public final MatchRoleID getPositionById(int id) {
		for (IMatchRoleID position : m_vPositionen) {
			MatchRoleID spielerPosition = (MatchRoleID) position;
			if (spielerPosition.getId() == id) {
				return spielerPosition;
			}
		}
		return null;
	}

	/**
	 * Get the position object by player id.
	 */
	public final MatchRoleID getPositionBySpielerId(int playerid) {
		for (IMatchRoleID position : m_vPositionen) {
			MatchRoleID spielerPosition = (MatchRoleID) position;
			if (spielerPosition.getSpielerId() == playerid) {
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
				for (int j = 0; j < m_vPositionen.size(); j++) {
					if (((MatchRoleID) m_vPositionen.get(j)).getId() == spos.getId()) {
						m_vPositionen.setElementAt(spos, j);
					}
				}
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
		return m_vPositionen;
	}

	/**
	 * Predicts Right-Attack-Rating
	 */
	public final double getRightAttackRating() {
		if (HOVerwaltung.instance().getModel() != null
				&& HOVerwaltung.instance().getModel().getID() != -1) {
			final RatingPredictionManager rpManager = new RatingPredictionManager(this,
					HOVerwaltung.instance().getModel().getTeam(), (short) HOVerwaltung.instance()
							.getModel().getTrainer().getTrainerTyp(), m_iStyleOfPlay,
					RatingPredictionConfig.getInstance());
			// ruft konvertiertes Plugin ( in Manager ) auf und returned den
			// Wert
			double value = Math.max(1, rpManager.getRightAttackRatings());
			if (value > 1) {
				value += UserParameter.instance().rightAttackOffset;
			}
			return value;
		} else {
			return 0.0d;
		}
	}

	/**
	 * Predicts rd-Rating
	 */
	public final double getRightDefenseRating() {
		if (HOVerwaltung.instance().getModel() != null
				&& HOVerwaltung.instance().getModel().getID() != -1) {
			final RatingPredictionManager rpManager = new RatingPredictionManager(this,
					HOVerwaltung.instance().getModel().getTeam(), (short) HOVerwaltung.instance()
							.getModel().getTrainer().getTrainerTyp(), m_iStyleOfPlay,
					RatingPredictionConfig.getInstance());
			// ruft konvertiertes Plugin ( in Manager ) auf und returned den
			// Wert
			double value = Math.max(1, rpManager.getRightDefenseRatings());
			if (value > 1) {
				value += UserParameter.instance().rightDefenceOffset;
			}
			return value;
		} else {
			return 0.0d;
		}
	}

	/**
	 * Team star rating for attackers
	 */
	public final float getSTTeamStk(List<Player> player, boolean mitForm) {
		float stk = 0.0f;
		stk += calcTeamStk(player, IMatchRoleID.FORWARD, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.FORWARD_DEF, mitForm);
		stk += calcTeamStk(player, IMatchRoleID.FORWARD_TOWING, mitForm);

		return Helper.round(stk, 1);
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
	public final void setSpielerAtPosition(int positionsid, int spielerid) {
		//if player changed in starting eleven or subsitute it has to be remove from previous occupied place in starting eleven or substitute
		if(!IMatchRoleID.aBackupssMatchRoleID.contains(positionsid)){
		MatchRoleID iRole;
		int iPlayerID;
		if (this.isPlayerInLineup(spielerid)) {
			for (int i = 0; i < m_vPositionen.size(); i++) {
				iRole = (MatchRoleID) m_vPositionen.get(i);
				iPlayerID = iRole.getSpielerId();
				// player is removed from previous position as player on the field or as substitute
				if (iPlayerID == spielerid) {iRole.setSpielerId(0, this);}
				}
			}
		}

		final MatchRoleID position = getPositionById(positionsid);
		position.setSpielerId(spielerid, this);

	}

	/**
	 * Check, if the player is in the lineup.
	 */
	public final boolean isPlayerInLineup(int spielerId) {
		return m_clAssi.isPlayerInLineup(spielerId, m_vPositionen);
	}

	public final boolean isPlayerInLineupExcludingBackup(int spielerId) {
		return m_clAssi.isPlayerInLineupExcludingBackup(spielerId, m_vPositionen);
	}

	/**
	 * Check, if the player is in the starting 11.
	 */
	public final boolean isPlayerInStartingEleven(int spielerId) {
		return m_clAssi.isPlayerInStartingEleven(spielerId, m_vPositionen);
	}

	/**
	 * Check, if the player is a substitute or a backup.
	 */
	public final boolean isSpielerInReserve(int spielerId) {
		return (m_clAssi.isPlayerInLineup(spielerId, m_vPositionen) && !m_clAssi
				.isPlayerInStartingEleven(spielerId, m_vPositionen));
	}

	/**
	 * check if the player is a sub (backup players are excluded)
	 */
	public final boolean isPlayerAsubstitute(int playerId) {
		for (int i = 0; (m_vPositionen != null) && (i < m_vPositionen.size()); i++) {
			if ((((MatchRoleID)m_vPositionen.elementAt(i)).getSpielerId() == playerId) &&
					(IMatchRoleID.aSubstitutesMatchRoleID.contains(((MatchRoleID)m_vPositionen.elementAt(i)).getId()))){
				return true;
			}
		}
		return false;
		}



	/**
	 * Returns a list with the substitutions for this lineup.
	 * 
	 * @return the substitutions for this lineup. If there are no substitutions,
	 *         an empty list will be returned.
	 */
	public List<Substitution> getSubstitutionList() {
		return this.substitutions;
	}

	/**
	 * Sets the provided list of substitutions as the substitution list. A
	 * proper list got max 5 positions.
	 */

	public void setSubstitionList(List<Substitution> subs) {
		if (subs == null) {
			this.substitutions = new ArrayList<Substitution>();
		} else {
			this.substitutions = new ArrayList<Substitution>(subs);
		}
	}

	public List<MatchRoleID> getPenaltyTakers() {
		return this.penaltyTakers;
	}

	public void setPenaltyTakers(List<MatchRoleID> positions) {
		this.penaltyTakers = new ArrayList<MatchRoleID>(positions);
	}

	/**
	 * Get the system name.
	 */
	public final String getSystemName(byte system) {
		return getNameForSystem(system);
	}

	/**
	 * Star rating for the keeper.
	 */
	public final float getTWTeamStk(List<Player> player, boolean mitForm) {
		return calcTeamStk(player, IMatchRoleID.KEEPER, mitForm);
	}

	/**
	 * Get tactic type for a position-id.
	 */
	public final byte getTactic4PositionID(int positionsid) {
		try {
			return getPositionById(positionsid).getTaktik();
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "getTactic4PositionID: " + e);
			return IMatchRoleID.UNKNOWN;
		}
	}

	public final float getTacticLevel(int type) {
		float value = 0.0f;

		switch (type) {
		case core.model.match.IMatchDetails.TAKTIK_PRESSING:
			value = getTacticLevelPressing();
			break;
		case core.model.match.IMatchDetails.TAKTIK_KONTER:
			value = getTacticLevelCounter();
			break;
		case core.model.match.IMatchDetails.TAKTIK_MIDDLE:
		case core.model.match.IMatchDetails.TAKTIK_WINGS:
			value = getTacticLevelAimAow();
			break;
		case core.model.match.IMatchDetails.TAKTIK_LONGSHOTS:
			value = getTacticLevelLongShots();
			break;
		}

		return value;
	}

	/**
	 * Setter for property m_iTacticType.
	 * 
	 * @param m_iTacticType
	 *            New value of property m_iTacticType.
	 */
	public final void setTacticType(int m_iTacticType) {
		this.m_iTacticType = m_iTacticType;
	}

	/**
	 * Getter for property m_iTacticType.
	 * 
	 * @return Value of property m_iTacticType.
	 */
	public final int getTacticType() {
		return m_iTacticType;
	}

	/**
	 * Get the formation xp for the current formation.
	 */
	public final int getTeamErfahrung4AktuellesSystem() {
		int erfahrung = -1;

		switch (ermittelSystem()) {
		case SYS_MURKS:
			erfahrung = -1;
			break;
		case SYS_451:
			erfahrung = HOVerwaltung.instance().getModel().getTeam().getFormationExperience451();
			break;
		case SYS_352:
			erfahrung = HOVerwaltung.instance().getModel().getTeam().getFormationExperience352();
			break;
		case SYS_442:
			erfahrung = HOVerwaltung.instance().getModel().getTeam().getFormationExperience442();
			break;
		case SYS_343:
			erfahrung = HOVerwaltung.instance().getModel().getTeam().getFormationExperience343();
			break;
		case SYS_433:
			erfahrung = HOVerwaltung.instance().getModel().getTeam().getFormationExperience433();
			break;
		case SYS_532:
			erfahrung = HOVerwaltung.instance().getModel().getTeam().getFormationExperience532();
			break;
		case SYS_541:
			erfahrung = HOVerwaltung.instance().getModel().getTeam().getFormationExperience541();
			break;
		case SYS_523:
			erfahrung = HOVerwaltung.instance().getModel().getTeam().getFormationExperience523();
			break;
		case SYS_550:
			erfahrung = HOVerwaltung.instance().getModel().getTeam().getFormationExperience550();
			break;
		case SYS_253:
			erfahrung = HOVerwaltung.instance().getModel().getTeam().getFormationExperience253();
			break;
		}

		return erfahrung;
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
		if (m_vPositionen != null) {
			for (IMatchRoleID pos : m_vPositionen) {
				MatchRoleID position = (MatchRoleID) pos;
				// existiert Player noch ?
				if ((HOVerwaltung.instance().getModel() != null)
						&& (HOVerwaltung.instance().getModel().getSpieler(position.getSpielerId()) == null)) {
					// nein dann zuweisung aufheben
					position.setSpielerId(0, this);
				}
			}
		}
	}

	/**
	 * Assitant to create automatically the lineup
	 */
	public final void doAufstellung(List<Player> player, byte reihenfolge, boolean mitForm,
									boolean idealPosFirst, boolean ignoreVerletzung, boolean ignoreSperren,
									float wetterBonus, Weather weather) {
		m_clAssi.doAufstellung(m_vPositionen, player, reihenfolge, mitForm, idealPosFirst,
				ignoreVerletzung, ignoreSperren, wetterBonus, weather);
		setAutoKicker(null);
		setAutoKapitaen(null);
	}

	/**
	 * Clone this lineup, creates and returns a new Lineup object.
	 */
	public final Lineup duplicate() {
		final Properties properties = new Properties();
		Lineup clone = null;

		try {
			properties.setProperty("keeper",
					String.valueOf(getPositionById(IMatchRoleID.keeper).getSpielerId()));
			properties.setProperty("rightback",
					String.valueOf(getPositionById(IMatchRoleID.rightBack).getSpielerId()));
			properties.setProperty("rightCentralDefender", String.valueOf(getPositionById(
					IMatchRoleID.rightCentralDefender).getSpielerId()));
			properties.setProperty("middleCentralDefender", String.valueOf(getPositionById(
					IMatchRoleID.middleCentralDefender).getSpielerId()));
			properties.setProperty("leftCentralDefender", String.valueOf(getPositionById(
					IMatchRoleID.leftCentralDefender).getSpielerId()));
			properties.setProperty("leftback",
					String.valueOf(getPositionById(IMatchRoleID.leftBack).getSpielerId()));
			properties.setProperty("rightwinger",
					String.valueOf(getPositionById(IMatchRoleID.rightWinger).getSpielerId()));
			properties.setProperty("rightInnerMidfield", String.valueOf(getPositionById(
					IMatchRoleID.rightInnerMidfield).getSpielerId()));
			properties.setProperty("insidemid3", String.valueOf(getPositionById(
					IMatchRoleID.centralInnerMidfield).getSpielerId()));
			properties.setProperty("leftInnerMidfield", String.valueOf(getPositionById(
					IMatchRoleID.leftInnerMidfield).getSpielerId()));
			properties.setProperty("leftwinger",
					String.valueOf(getPositionById(IMatchRoleID.leftWinger).getSpielerId()));
			properties.setProperty("rightForward",
					String.valueOf(getPositionById(IMatchRoleID.rightForward).getSpielerId()));
			properties.setProperty("centralForward", String.valueOf(getPositionById(
					IMatchRoleID.centralForward).getSpielerId()));
			properties.setProperty("leftForward",
					String.valueOf(getPositionById(IMatchRoleID.leftForward).getSpielerId()));

			properties.setProperty("substGK1",
					String.valueOf(getPositionById(IMatchRoleID.substGK1).getSpielerId()));
			properties.setProperty("substGK2",
					String.valueOf(getPositionById(IMatchRoleID.substGK2).getSpielerId()));

			properties.setProperty("substCD1",
					String.valueOf(getPositionById(IMatchRoleID.substCD1).getSpielerId()));
			properties.setProperty("substCD2",
					String.valueOf(getPositionById(IMatchRoleID.substCD2).getSpielerId()));

			properties.setProperty("substWB1",
					String.valueOf(getPositionById(IMatchRoleID.substWB1).getSpielerId()));
			properties.setProperty("substWB2",
					String.valueOf(getPositionById(IMatchRoleID.substWB2).getSpielerId()));

			properties.setProperty("substIM1", String.valueOf(getPositionById(
					IMatchRoleID.substIM1).getSpielerId()));
			properties.setProperty("substIM2", String.valueOf(getPositionById(
					IMatchRoleID.substIM2).getSpielerId()));

			properties.setProperty("substFW1",
					String.valueOf(getPositionById(IMatchRoleID.substFW1).getSpielerId()));
			properties.setProperty("substFW2",
					String.valueOf(getPositionById(IMatchRoleID.substFW2).getSpielerId()));

			properties.setProperty("substWI1",
					String.valueOf(getPositionById(IMatchRoleID.substWI1).getSpielerId()));
			properties.setProperty("substWI2",
					String.valueOf(getPositionById(IMatchRoleID.substWI2).getSpielerId()));

			properties.setProperty("substXT1",
					String.valueOf(getPositionById(IMatchRoleID.substXT1).getSpielerId()));
			properties.setProperty("substXT2",
					String.valueOf(getPositionById(IMatchRoleID.substXT2).getSpielerId()));


			properties.setProperty("order_rightBack",
					String.valueOf(getPositionById(IMatchRoleID.rightBack).getTaktik()));
			properties.setProperty("order_rightCentralDefender", String.valueOf(getPositionById(
					IMatchRoleID.rightCentralDefender).getTaktik()));
			properties.setProperty("order_leftCentralDefender", String.valueOf(getPositionById(
					IMatchRoleID.leftCentralDefender).getTaktik()));
			properties.setProperty("order_middleCentralDefender", String.valueOf(getPositionById(
					IMatchRoleID.middleCentralDefender).getTaktik()));
			properties.setProperty("order_leftBack",
					String.valueOf(getPositionById(IMatchRoleID.leftBack).getTaktik()));
			properties.setProperty("order_rightWinger",
					String.valueOf(getPositionById(IMatchRoleID.rightWinger).getTaktik()));
			properties.setProperty("order_rightInnerMidfield", String.valueOf(getPositionById(
					IMatchRoleID.rightInnerMidfield).getTaktik()));
			properties
					.setProperty("order_leftInnerMidfield", String.valueOf(getPositionById(
							IMatchRoleID.leftInnerMidfield).getTaktik()));
			properties.setProperty("order_centralInnerMidfield", String.valueOf(getPositionById(
					IMatchRoleID.centralInnerMidfield).getTaktik()));
			properties.setProperty("order_leftWinger",
					String.valueOf(getPositionById(IMatchRoleID.leftWinger).getTaktik()));
			properties.setProperty("order_rightForward",
					String.valueOf(getPositionById(IMatchRoleID.rightForward).getTaktik()));
			properties.setProperty("order_leftForward",
					String.valueOf(getPositionById(IMatchRoleID.leftForward).getTaktik()));
			properties.setProperty("order_centralForward",
					String.valueOf(getPositionById(IMatchRoleID.centralForward).getTaktik()));


			properties.setProperty("set_pieces_taker", String.valueOf(getKicker()));
			properties.setProperty("captain", String.valueOf(getKapitaen()));
			properties.setProperty("tactictype", String.valueOf(getTacticType()));
			properties.setProperty("attitude", String.valueOf(getAttitude()));
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
			clone.setPullBackOverride(isPullBackOverride());
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "Aufstellung.duplicate: " + e);
		}
		return clone;
	}

	/**
	 * Determinates the current formation.
	 */
	public final byte ermittelSystem() {
		final int defenders = getAnzAbwehr();
		final int midfielders = getAnzMittelfeld();
		final int forwards = getAnzSturm();
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

	private final void swapContentAtPositions(int pos1, int pos2) {
		int id1 = 0;
		int id2 = 0;
		byte tac1 = -1;
		byte tac2 = -1;

		tac1 = getTactic4PositionID(pos1);
		tac2 = getTactic4PositionID(pos2);

		if (getPlayerByPositionID(pos1) != null) {
			id1 = getPlayerByPositionID(pos1).getSpielerID();
			setSpielerAtPosition(pos1, 0);
		}
		if (getPlayerByPositionID(pos2) != null) {
			id2 = getPlayerByPositionID(pos2).getSpielerID();
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
		m_vPositionen = null;
		m_vPositionen = temp.getPositionen();
		m_iKicker = temp.getKicker();
		m_iKapitaen = temp.getKapitaen();
	}

	/**
	 * Load a lineup from HRF.
	 */
	public final void load4HRF() {
		final Lineup temp = DBManager.instance().getAufstellung(
				HOVerwaltung.instance().getModel().getID(), "HRF");
		m_vPositionen = null;
		m_vPositionen = temp.getPositionen();
		m_iKicker = temp.getKicker();
		m_iKapitaen = temp.getKapitaen();
	}

	/**
	 * Load a system from the DB.
	 */
	public final void loadAufstellungsSystem(String name) {
		m_vPositionen = DBManager.instance().getSystemPositionen(NO_HRF_VERBINDUNG, name);
		checkAufgestellteSpieler();
	}

	/**
	 * Remove all players from all positions.
	 */
	public final void resetAufgestellteSpieler() {
		m_clAssi.resetPositionsbesetzungen(m_vPositionen);
	}

	/**
	 * Remove a spare player.
	 */
	public final void resetReserveBank() {
		// Nur Reservespieler
		final Vector<IMatchRoleID> vReserve = new Vector<IMatchRoleID>();
		for (IMatchRoleID pos : m_vPositionen) {
			if (((MatchRoleID) pos).getId() >= IMatchRoleID.startReserves) {
				vReserve.add(pos);
			}
		}
		m_clAssi.resetPositionsbesetzungen(vReserve);
	}

	/**
	 * Resets the orders for all positions to normal
	 */
	public final void resetPositionOrders() {
		m_clAssi.resetPositionOrders(m_vPositionen);
	}

	/**
	 * Save a lineup using the given name.
	 */
	public final void save(final String name) {
		DBManager.instance().saveAufstellung(NO_HRF_VERBINDUNG, this, name);
	}

	/**
	 * Save a lineup.
	 */
	public final void save4HRF() {
		DBManager.instance().saveAufstellung(HOVerwaltung.instance().getModel().getID(), this,
				"HRF");
	}

	/**
	 * Save the current system in the DB.
	 */
	public final void saveAufstellungsSystem(String name) {
		DBManager.instance().saveSystemPositionen(NO_HRF_VERBINDUNG, m_vPositionen, name);
	}

	/**
	 * Calculate the amount af defenders.
	 */
	private int getAnzAbwehr() {
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
	private int getAnzMittelfeld() {
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
	private int getAnzSturm() {
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

		for (IMatchRoleID pos : m_vPositionen) {
			MatchRoleID position = (MatchRoleID) pos;
			if ((positionId == position.getPosition())
					&& (position.getId() < IMatchRoleID.startReserves)
					&& (position.getSpielerId() > 0)) {
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

		for (IMatchRoleID pos : m_vPositionen) {
			MatchRoleID position = (MatchRoleID) pos;
			if ((IMatchRoleID.aFieldMatchRoleID.contains(position.getId())) && (position.getSpielerId() != 0)) numPlayers++;
		    }
		if (numPlayers == 11) return false;
		return true;

	}

	/**
	 * Calculate player strength for the given position.
	 */
	private float calcPlayerStk(List<Player> spieler, int spielerId, byte position, boolean mitForm) {
		if (spieler != null) {
			for (Player current : spieler) {
				Player player = (Player) current;
				if (player.getSpielerID() == spielerId) {
					return player.calcPosValue(position, mitForm);
				}
			}
		}
		return 0.0f;
	}

	/**
	 * Calculate team strength for the given position.
	 */
	private float calcTeamStk(List<Player> player, byte positionId, boolean useForm) {
		float stk = 0.0f;
		if (player != null) {
			for (IMatchRoleID pos : m_vPositionen) {
				MatchRoleID position = (MatchRoleID) pos;
				if ((position.getPosition() == positionId)
						&& (position.getId() < IMatchRoleID.startReserves)) {
					stk += calcPlayerStk(player, position.getSpielerId(), positionId, useForm);
				}
			}
		}
		return Helper.round(stk, 1);
	}

//	/**
//	 * Debug log lineup.
//	 */
//	private void dumpValues() {
//		if (m_vPositionen != null) {
//			for (IMatchRoleID pos : m_vPositionen) {
//				final Player temp = HOVerwaltung.instance().getModel()
//						.getSpieler(((MatchRoleID) pos).getSpielerId());
//				String name = "";
//				float stk = 0.0f;
//
//				if (temp != null) {
//					name = temp.getName();
//					stk = temp.calcPosValue(((MatchRoleID) pos).getPosition(), true);
//				}
//
//				HOLogger.instance().log(getClass(),
//						"PosID: " + MatchRoleID.getNameForID(((MatchRoleID) pos).getId()) //
//								+ ", Player :" + name + " , Stk : " + stk);
//			}
//		}
//		if (m_iKapitaen > 0) {
//			HOLogger.instance().log(
//					getClass(),
//					"Captain: "
//							+ HOVerwaltung.instance().getModel().getSpieler(m_iKapitaen).getName());
//		}
//
//		if (m_iKicker > 0) {
//			HOLogger.instance().log(
//					getClass(),
//					"SetPieces: "
//							+ HOVerwaltung.instance().getModel().getSpieler(m_iKicker).getName());
//		}
//
//		if (m_sLocation > -1) {
//			HOLogger.instance().log(getClass(), "Location: " + m_sLocation);
//		}
//
//		HOLogger.instance().log(
//				getClass(),
//				"GK: " + getTWTeamStk(HOVerwaltung.instance().getModel().getAllSpieler(), true)
//						+ " DF: "
//						+ getAWTeamStk(HOVerwaltung.instance().getModel().getAllSpieler(), true)
//						+ " MF : "
//						+ getMFTeamStk(HOVerwaltung.instance().getModel().getAllSpieler(), true)
//						+ " ST : "
//						+ getSTTeamStk(HOVerwaltung.instance().getModel().getAllSpieler(), true));
//	}

	/**
	 * Initializes the 553 lineup
	 */
	private void initPositionen553() {
		if (m_vPositionen != null) {
			m_vPositionen.removeAllElements();
		} else m_vPositionen = new Vector<>();

		m_vPositionen.add(new MatchRoleID(IMatchRoleID.keeper, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.rightBack, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.rightCentralDefender, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.middleCentralDefender, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.leftCentralDefender, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.leftBack, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.rightWinger, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.rightInnerMidfield, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.centralInnerMidfield, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.leftInnerMidfield, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.leftWinger, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.rightForward, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.centralForward, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.leftForward, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.substGK1, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.substGK2, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.substCD1, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.substCD2, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.substWB1, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.substWB2, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.substIM1, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.substIM2, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.substFW1, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.substFW2, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.substWI1, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.substWI2, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.substXT1, 0, (byte) 0));
		m_vPositionen.add(new MatchRoleID(IMatchRoleID.substXT2, 0, (byte) 0));

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
		return new MatchRoleID(sp.getId(), sp2.getSpielerId(), sp2.getTaktik());
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
	 * @return if the pull back should be overridden.
	 */
	public boolean isPullBackOverride() {
		return pullBackOverride;
	}

	/**
	 * @param pullBackOverride
	 *            the override flag to set.
	 */
	public void setPullBackOverride(boolean pullBackOverride) {
		this.pullBackOverride = pullBackOverride;
	}
}
