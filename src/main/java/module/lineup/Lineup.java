package module.lineup;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.IMatchDetails;
import core.model.match.MatchKurzInfo;
import core.model.match.Weather;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.model.player.SpielerPosition;
import core.rating.RatingPredictionConfig;
import core.rating.RatingPredictionManager;
import core.util.HOLogger;
import core.util.Helper;
import core.util.StringUtils;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.RedCardCriteria;
import module.lineup.substitution.model.Substitution;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

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
	private Vector<ISpielerPosition> m_vPositionen = new Vector<ISpielerPosition>();
	private List<Substitution> substitutions = new ArrayList<Substitution>();
	private List<SpielerPosition> penaltyTakers = new ArrayList<SpielerPosition>();

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
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.keeper, Integer
					.parseInt(properties.getProperty("keeper", "0")), (byte) 0));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.rightBack, Integer
					.parseInt(properties.getProperty("rightback", "0")), Byte.parseByte(properties
					.getProperty("behrightback", "0"))));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.rightCentralDefender, Integer
					.parseInt(properties.getProperty("insideback1", "0")), Byte
					.parseByte(properties.getProperty("behinsideback1", "0"))));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.leftCentralDefender, Integer
					.parseInt(properties.getProperty("insideback2", "0")), Byte
					.parseByte(properties.getProperty("behinsideback2", "0"))));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.middleCentralDefender, Integer
					.parseInt(properties.getProperty("insideback3", "0")), Byte
					.parseByte(properties.getProperty("behinsideback3", "0"))));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.leftBack, Integer
					.parseInt(properties.getProperty("leftback", "0")), Byte.parseByte(properties
					.getProperty("behleftback", "0"))));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.rightWinger, Integer
					.parseInt(properties.getProperty("rightwinger", "0")), Byte
					.parseByte(properties.getProperty("behrightwinger", "0"))));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.rightInnerMidfield, Integer
					.parseInt(properties.getProperty("insidemid1", "0")), Byte.parseByte(properties
					.getProperty("behinsidemid1", "0"))));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.leftInnerMidfield, Integer
					.parseInt(properties.getProperty("insidemid2", "0")), Byte.parseByte(properties
					.getProperty("behinsidemid2", "0"))));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.centralInnerMidfield, Integer
					.parseInt(properties.getProperty("insidemid3", "0")), Byte.parseByte(properties
					.getProperty("behinsidemid3", "0"))));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.leftWinger, Integer
					.parseInt(properties.getProperty("leftwinger", "0")), Byte.parseByte(properties
					.getProperty("behleftwinger", "0"))));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.rightForward, Integer
					.parseInt(properties.getProperty("forward1", "0")), Byte.parseByte(properties
					.getProperty("behforward1", "0"))));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.leftForward, Integer
					.parseInt(properties.getProperty("forward2", "0")), Byte.parseByte(properties
					.getProperty("behforward2", "0"))));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.centralForward, Integer
					.parseInt(properties.getProperty("forward3", "0")), Byte.parseByte(properties
					.getProperty("behforward3", "0"))));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.substDefender, Integer
					.parseInt(properties.getProperty("substback", "0")), (byte) 0));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.substInnerMidfield, Integer
					.parseInt(properties.getProperty("substinsidemid", "0")), (byte) 0));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.substWinger, Integer
					.parseInt(properties.getProperty("substwinger", "0")), (byte) 0));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.substKeeper, Integer
					.parseInt(properties.getProperty("substkeeper", "0")), (byte) 0));
			m_vPositionen.add(new SpielerPosition(ISpielerPosition.substForward, Integer
					.parseInt(properties.getProperty("substforward", "0")), (byte) 0));
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
				penaltyTakers.add(new SpielerPosition(i + ISpielerPosition.penaltyTaker1, Integer
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
	public final float getAWTeamStk(List<Spieler> spieler, boolean mitForm) {
		float stk = 0.0f;
		stk += calcTeamStk(spieler, ISpielerPosition.CENTRAL_DEFENDER, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.CENTRAL_DEFENDER_OFF, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.CENTRAL_DEFENDER_TOWING, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.BACK, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.BACK_OFF, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.BACK_DEF, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.BACK_TOMID, mitForm);

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
	public final void setAutoKapitaen(List<Spieler> players) {
		float maxValue = -1;

		if (players == null) {
			players = HOVerwaltung.instance().getModel().getAllSpieler();
		}

		if (players != null) {
			for (Spieler player : players) {
				if (m_clAssi.isSpielerInAnfangsElf(player.getSpielerID(), m_vPositionen)) {
					int curPlayerId = player.getSpielerID();
					float curCaptainsValue = HOVerwaltung.instance().getModel().getAufstellung()
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
	public final void setAutoKicker(List<Spieler> players) {
		double maxStandard = -1;
		int form = -1;

		if (players == null) {
			players = HOVerwaltung.instance().getModel().getAllSpieler();
		}

		Vector<ISpielerPosition> noKeeper = new Vector<ISpielerPosition>(m_vPositionen);

		for (ISpielerPosition pos : noKeeper) {
			SpielerPosition p = (SpielerPosition) pos;
			if (p.getId() == ISpielerPosition.keeper) {
				noKeeper.remove(pos);
				break;
			}
		}

		if (players != null) {
			for (Spieler player : players) {
				if (m_clAssi.isSpielerInAnfangsElf(player.getSpielerID(), noKeeper)) {
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

		Spieler captain = null;
		List<Spieler> players = HOVerwaltung.instance().getModel().getAllSpieler();

		if (players != null) {
			for (Spieler player : players) {
				if (m_clAssi.isSpielerInAnfangsElf(player.getSpielerID(), m_vPositionen)) {
					value += player.getErfahrung();
					if (captainsId > 0) {
						if (captainsId == player.getSpielerID()) {
							captain = (Spieler) player;
						}
					} else if (m_iKapitaen == player.getSpielerID()) {
						captain = (Spieler) player;
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
			// ruft konvertiertes Plugin ( in Manager ) auf und returned den
			// Wert
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
	public final float getGesamtStaerke(List<Spieler> spieler, boolean useForm) {
		return Helper.round(getTWTeamStk(spieler, useForm) + getAWTeamStk(spieler, useForm) //
				+ getMFTeamStk(spieler, useForm) + getSTTeamStk(spieler, useForm), 1);
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
	public final float getMFTeamStk(List<Spieler> spieler, boolean mitForm) {
		float stk = 0.0f;
		stk += calcTeamStk(spieler, ISpielerPosition.MIDFIELDER, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.WINGER, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.MIDFIELDER_OFF, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.WINGER_OFF, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.MIDFIELDER_DEF, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.WINGER_DEF, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.MIDFIELDER_TOWING, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.WINGER_TOMID, mitForm);

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
	 * Get the position type (byte in ISpielerPosition).
	 */
	public final byte getEffectivePos4PositionID(int positionsid) {
		try {
			return getPositionById(positionsid).getPosition();
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "getEffectivePos4PositionID: " + e);
			return ISpielerPosition.UNKNOWN;
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
				for (int i = 0; i < matches.length; i++) {
					match = sMatches.get(i);
					if (match.getMatchTyp().isOfficial()) {
						break;
					}
				}

				if (match == null) {
					m_sLocation = IMatchDetails.LOCATION_AWAY;
					HOLogger.instance().error(getClass(), "no match to determine location");
					return m_sLocation;
				}

				m_sLocation = (match.getHeimID() == teamId) ? IMatchDetails.LOCATION_HOME
						: IMatchDetails.LOCATION_AWAY;

				// To decide away derby we need hold of the region.
				// I think this is very annoying to get hold of
				// (possible both download and db work needed) and
				// probably why aik has it as TODO (blaghaid).

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
	public Spieler getPlayerByPositionID(int positionId) {
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
	public final SpielerPosition getPositionById(int id) {
		for (ISpielerPosition position : m_vPositionen) {
			SpielerPosition spielerPosition = (SpielerPosition) position;
			if (spielerPosition.getId() == id) {
				return spielerPosition;
			}
		}
		return null;
	}

	/**
	 * Get the position object by player id.
	 */
	public final SpielerPosition getPositionBySpielerId(int playerid) {
		for (ISpielerPosition position : m_vPositionen) {
			SpielerPosition spielerPosition = (SpielerPosition) position;
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
	public final void setPositionen(List<ISpielerPosition> positions) {
		// Replace the existing positions with the incoming on a one by one
		// basis. Otherwise we will miss 3 positions when loading
		// an old style lineup.
		// We need to avoid the regular methods, as some required stuff like the
		// Model may not be created yet.

		if (positions != null) {
			initPositionen553();
			for (ISpielerPosition pos : positions) {
				SpielerPosition spos = (SpielerPosition) pos;
				for (int j = 0; j < m_vPositionen.size(); j++) {
					if (((SpielerPosition) m_vPositionen.get(j)).getId() == spos.getId()) {
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
	public final Vector<ISpielerPosition> getPositionen() {
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
	public final float getSTTeamStk(List<Spieler> spieler, boolean mitForm) {
		float stk = 0.0f;
		stk += calcTeamStk(spieler, ISpielerPosition.FORWARD, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.FORWARD_DEF, mitForm);
		stk += calcTeamStk(spieler, ISpielerPosition.FORWARD_TOWING, mitForm);

		return Helper.round(stk, 1);
	}

	/**
	 * Place a player to a certain position and check/solve dependencies.
	 */
	public final byte setSpielerAtPosition(int positionsid, int spielerid, byte tactic) {
		final SpielerPosition pos = getPositionById(positionsid);

		if (pos != null) {
			setSpielerAtPosition(positionsid, spielerid);
			pos.setTaktik(tactic);

			return pos.getPosition();
		}

		return ISpielerPosition.UNKNOWN;
	}

	/**
	 * Place a player to a certain position and check/solve dependencies.
	 */
	public final void setSpielerAtPosition(int positionsid, int spielerid) {
		if (this.isSpielerAufgestellt(spielerid)) {
			for (int i = 0; i < m_vPositionen.size(); i++) {
				if (((SpielerPosition) m_vPositionen.get(i)).getSpielerId() == spielerid) {
					((SpielerPosition) m_vPositionen.get(i)).setSpielerId(0, this);
				}
			}
		}
		final SpielerPosition position = getPositionById(positionsid);
		position.setSpielerId(spielerid, this);
	}

	/**
	 * Check, if the player is in the lineup.
	 */
	public final boolean isSpielerAufgestellt(int spielerId) {
		return m_clAssi.isSpielerAufgestellt(spielerId, m_vPositionen);
	}

	/**
	 * Check, if the player is in the starting 11.
	 */
	public final boolean isSpielerInAnfangsElf(int spielerId) {
		return m_clAssi.isSpielerInAnfangsElf(spielerId, m_vPositionen);
	}

	/**
	 * Check, if the player is a substitute.
	 */
	public final boolean isSpielerInReserve(int spielerId) {
		return (m_clAssi.isSpielerAufgestellt(spielerId, m_vPositionen) && !m_clAssi
				.isSpielerInAnfangsElf(spielerId, m_vPositionen));
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

	public List<SpielerPosition> getPenaltyTakers() {
		return this.penaltyTakers;
	}

	public void setPenaltyTakers(List<SpielerPosition> positions) {
		this.penaltyTakers = new ArrayList<SpielerPosition>(positions);
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
	public final float getTWTeamStk(List<Spieler> spieler, boolean mitForm) {
		return calcTeamStk(spieler, ISpielerPosition.KEEPER, mitForm);
	}

	/**
	 * Get tactic type for a position-id.
	 */
	public final byte getTactic4PositionID(int positionsid) {
		try {
			return getPositionById(positionsid).getTaktik();
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "getTactic4PositionID: " + e);
			return core.model.player.ISpielerPosition.UNKNOWN;
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
			for (ISpielerPosition pos : m_vPositionen) {
				SpielerPosition position = (SpielerPosition) pos;
				// existiert Spieler noch ?
				if ((HOVerwaltung.instance().getModel() != null)
						&& (HOVerwaltung.instance().getModel().getSpieler(position.getSpielerId()) == null)) {
					// nein dann zuweisung aufheben
					position.setSpielerId(0, this);
				}
			}
		}
	}

	/**
	 * erstellt die automatische Aufstellung
	 */
	public final void doAufstellung(List<Spieler> spieler, byte reihenfolge, boolean mitForm,
			boolean idealPosFirst, boolean ignoreVerletzung, boolean ignoreSperren,
			float wetterBonus, Weather weather) {
		m_clAssi.doAufstellung(m_vPositionen, spieler, reihenfolge, mitForm, idealPosFirst,
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
					String.valueOf(getPositionById(ISpielerPosition.keeper).getSpielerId()));
			properties.setProperty("rightback",
					String.valueOf(getPositionById(ISpielerPosition.rightBack).getSpielerId()));
			properties.setProperty("insideback1", String.valueOf(getPositionById(
					ISpielerPosition.rightCentralDefender).getSpielerId()));
			properties.setProperty("insideback2", String.valueOf(getPositionById(
					ISpielerPosition.leftCentralDefender).getSpielerId()));
			properties.setProperty("insideback3", String.valueOf(getPositionById(
					ISpielerPosition.middleCentralDefender).getSpielerId()));
			properties.setProperty("leftback",
					String.valueOf(getPositionById(ISpielerPosition.leftBack).getSpielerId()));
			properties.setProperty("rightwinger",
					String.valueOf(getPositionById(ISpielerPosition.rightWinger).getSpielerId()));
			properties.setProperty("insidemid1", String.valueOf(getPositionById(
					ISpielerPosition.rightInnerMidfield).getSpielerId()));
			properties.setProperty("insidemid2", String.valueOf(getPositionById(
					ISpielerPosition.leftInnerMidfield).getSpielerId()));
			properties.setProperty("insidemid3", String.valueOf(getPositionById(
					ISpielerPosition.centralInnerMidfield).getSpielerId()));
			properties.setProperty("leftwinger",
					String.valueOf(getPositionById(ISpielerPosition.leftWinger).getSpielerId()));
			properties.setProperty("forward1",
					String.valueOf(getPositionById(ISpielerPosition.rightForward).getSpielerId()));
			properties.setProperty("forward2",
					String.valueOf(getPositionById(ISpielerPosition.leftForward).getSpielerId()));
			properties
					.setProperty("forward3", String.valueOf(getPositionById(
							ISpielerPosition.centralForward).getSpielerId()));
			properties.setProperty("substback",
					String.valueOf(getPositionById(ISpielerPosition.substDefender).getSpielerId()));
			properties.setProperty("substinsidemid", String.valueOf(getPositionById(
					ISpielerPosition.substInnerMidfield).getSpielerId()));
			properties.setProperty("substwinger",
					String.valueOf(getPositionById(ISpielerPosition.substWinger).getSpielerId()));
			properties.setProperty("substkeeper",
					String.valueOf(getPositionById(ISpielerPosition.substKeeper).getSpielerId()));
			properties.setProperty("substforward",
					String.valueOf(getPositionById(ISpielerPosition.substForward).getSpielerId()));
			properties.setProperty("behrightback",
					String.valueOf(getPositionById(ISpielerPosition.rightBack).getTaktik()));
			properties.setProperty("behinsideback1", String.valueOf(getPositionById(
					ISpielerPosition.rightCentralDefender).getTaktik()));
			properties.setProperty("behinsideback2", String.valueOf(getPositionById(
					ISpielerPosition.leftCentralDefender).getTaktik()));
			properties.setProperty("behinsideback3", String.valueOf(getPositionById(
					ISpielerPosition.middleCentralDefender).getTaktik()));
			properties.setProperty("behleftback",
					String.valueOf(getPositionById(ISpielerPosition.leftBack).getTaktik()));
			properties.setProperty("behrightwinger",
					String.valueOf(getPositionById(ISpielerPosition.rightWinger).getTaktik()));
			properties.setProperty("behinsidemid1", String.valueOf(getPositionById(
					ISpielerPosition.rightInnerMidfield).getTaktik()));
			properties
					.setProperty("behinsidemid2", String.valueOf(getPositionById(
							ISpielerPosition.leftInnerMidfield).getTaktik()));
			properties.setProperty("behinsidemid3", String.valueOf(getPositionById(
					ISpielerPosition.centralInnerMidfield).getTaktik()));
			properties.setProperty("behleftwinger",
					String.valueOf(getPositionById(ISpielerPosition.leftWinger).getTaktik()));
			properties.setProperty("behforward1",
					String.valueOf(getPositionById(ISpielerPosition.rightForward).getTaktik()));
			properties.setProperty("behforward2",
					String.valueOf(getPositionById(ISpielerPosition.leftForward).getTaktik()));
			properties.setProperty("behforward3",
					String.valueOf(getPositionById(ISpielerPosition.centralForward).getTaktik()));
			properties.setProperty("kicker1", String.valueOf(getKicker()));
			properties.setProperty("captain", String.valueOf(getKapitaen()));
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
		swapContentAtPositions(ISpielerPosition.rightBack, ISpielerPosition.leftBack);
		swapContentAtPositions(ISpielerPosition.rightCentralDefender,
				ISpielerPosition.leftCentralDefender);
		swapContentAtPositions(ISpielerPosition.rightWinger, ISpielerPosition.leftWinger);
		swapContentAtPositions(ISpielerPosition.rightInnerMidfield,
				ISpielerPosition.leftInnerMidfield);
		swapContentAtPositions(ISpielerPosition.rightForward, ISpielerPosition.leftForward);
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
		final Vector<ISpielerPosition> vReserve = new Vector<ISpielerPosition>();
		for (ISpielerPosition pos : m_vPositionen) {
			if (((SpielerPosition) pos).getId() >= ISpielerPosition.startReserves) {
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
		anzahl += getAnzPosImSystem(ISpielerPosition.BACK);
		anzahl += getAnzPosImSystem(ISpielerPosition.BACK_TOMID);
		anzahl += getAnzPosImSystem(ISpielerPosition.BACK_OFF);
		anzahl += getAnzPosImSystem(ISpielerPosition.BACK_DEF);
		return anzahl + getAnzInnenverteidiger();
	}

	/**
	 * Calculate the amount of central defenders.
	 */
	private int getAnzInnenverteidiger() {
		int anzahl = 0;
		anzahl += getAnzPosImSystem(ISpielerPosition.CENTRAL_DEFENDER);
		anzahl += getAnzPosImSystem(ISpielerPosition.CENTRAL_DEFENDER_TOWING);
		anzahl += getAnzPosImSystem(ISpielerPosition.CENTRAL_DEFENDER_OFF);
		return anzahl;
	}

	/**
	 * Get the total amount of midfielders in the lineup.
	 */
	private int getAnzMittelfeld() {
		int anzahl = 0;
		anzahl += getAnzPosImSystem(ISpielerPosition.WINGER);
		anzahl += getAnzPosImSystem(ISpielerPosition.WINGER_TOMID);
		anzahl += getAnzPosImSystem(ISpielerPosition.WINGER_OFF);
		anzahl += getAnzPosImSystem(ISpielerPosition.WINGER_DEF);
		return anzahl + getAnzInneresMittelfeld();
	}

	/**
	 * Get the amount of inner midfielders in the lineup.
	 */
	private int getAnzInneresMittelfeld() {
		int anzahl = 0;
		anzahl += getAnzPosImSystem(ISpielerPosition.MIDFIELDER);
		anzahl += getAnzPosImSystem(ISpielerPosition.MIDFIELDER_OFF);
		anzahl += getAnzPosImSystem(ISpielerPosition.MIDFIELDER_DEF);
		anzahl += getAnzPosImSystem(ISpielerPosition.MIDFIELDER_TOWING);
		return anzahl;
	}

	/**
	 * Get the amount of strikers in the lineup.
	 */
	private int getAnzSturm() {
		int anzahl = 0;
		anzahl += getAnzPosImSystem(ISpielerPosition.FORWARD);
		anzahl += getAnzPosImSystem(ISpielerPosition.FORWARD_DEF);
		anzahl += getAnzPosImSystem(ISpielerPosition.FORWARD_TOWING);
		return anzahl;
	}

	/**
	 * Generic "counter" for the given position in the current lineup.
	 */
	private int getAnzPosImSystem(byte positionId) {
		int anzahl = 0;

		for (ISpielerPosition pos : m_vPositionen) {
			SpielerPosition position = (SpielerPosition) pos;
			if ((positionId == position.getPosition())
					&& (position.getId() < ISpielerPosition.startReserves)
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
		for (ISpielerPosition pos : m_vPositionen) {
			SpielerPosition position = (SpielerPosition) pos;

			if ((position.getId() < ISpielerPosition.KEEPER)
					|| (position.getId() >= ISpielerPosition.startReserves)) {
				// We are not interested in reserves, captain, set piece taker.
				continue;
			}

			// SpielerID of 0 indicates the position is empty. -1 is the first
			// temp player. This is bug prone.
			// At some time, clean up by adding some boolean value to Spieler
			// instead (isTemp or something).

			if (position.getSpielerId() != 0) {
				numPlayers++;
				if (numPlayers == 11) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Calculate player strength for the given position.
	 */
	private float calcPlayerStk(List<Spieler> spieler, int spielerId, byte position, boolean mitForm) {
		if (spieler != null) {
			for (Spieler current : spieler) {
				Spieler player = (Spieler) current;
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
	private float calcTeamStk(List<Spieler> spieler, byte positionId, boolean useForm) {
		float stk = 0.0f;
		if (spieler != null) {
			for (ISpielerPosition pos : m_vPositionen) {
				SpielerPosition position = (SpielerPosition) pos;
				if ((position.getPosition() == positionId)
						&& (position.getId() < ISpielerPosition.startReserves)) {
					stk += calcPlayerStk(spieler, position.getSpielerId(), positionId, useForm);
				}
			}
		}
		return Helper.round(stk, 1);
	}

	/**
	 * Debug log lineup.
	 */
	private void dumpValues() {
		if (m_vPositionen != null) {
			for (ISpielerPosition pos : m_vPositionen) {
				final Spieler temp = HOVerwaltung.instance().getModel()
						.getSpieler(((SpielerPosition) pos).getSpielerId());
				String name = "";
				float stk = 0.0f;

				if (temp != null) {
					name = temp.getName();
					stk = temp.calcPosValue(((SpielerPosition) pos).getPosition(), true);
				}

				HOLogger.instance().log(getClass(),
						"PosID: " + SpielerPosition.getNameForID(((SpielerPosition) pos).getId()) //
								+ ", Player :" + name + " , Stk : " + stk);
			}
		}
		if (m_iKapitaen > 0) {
			HOLogger.instance().log(
					getClass(),
					"Captain: "
							+ HOVerwaltung.instance().getModel().getSpieler(m_iKapitaen).getName());
		}

		if (m_iKicker > 0) {
			HOLogger.instance().log(
					getClass(),
					"SetPieces: "
							+ HOVerwaltung.instance().getModel().getSpieler(m_iKicker).getName());
		}

		if (m_sLocation > -1) {
			HOLogger.instance().log(getClass(), "Location: " + m_sLocation);
		}

		HOLogger.instance().log(
				getClass(),
				"GK: " + getTWTeamStk(HOVerwaltung.instance().getModel().getAllSpieler(), true)
						+ " DF: "
						+ getAWTeamStk(HOVerwaltung.instance().getModel().getAllSpieler(), true)
						+ " MF : "
						+ getMFTeamStk(HOVerwaltung.instance().getModel().getAllSpieler(), true)
						+ " ST : "
						+ getSTTeamStk(HOVerwaltung.instance().getModel().getAllSpieler(), true));
	}

	/**
	 * Initializes the 553 lineup
	 */
	private void initPositionen553() {
		if (m_vPositionen != null) {
			m_vPositionen.removeAllElements();
		} else {
			m_vPositionen = new Vector<ISpielerPosition>();
		}

		m_vPositionen.add(new SpielerPosition(ISpielerPosition.keeper, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.rightBack, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.rightCentralDefender, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.middleCentralDefender, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.leftCentralDefender, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.leftBack, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.rightWinger, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.rightInnerMidfield, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.centralInnerMidfield, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.leftInnerMidfield, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.leftWinger, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.rightForward, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.centralForward, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.leftForward, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.substDefender, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.substInnerMidfield, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.substWinger, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.substKeeper, 0, (byte) 0));
		m_vPositionen.add(new SpielerPosition(ISpielerPosition.substForward, 0, (byte) 0));

		for (int i = 0; i < 10; i++) {
			penaltyTakers.add(new SpielerPosition(ISpielerPosition.penaltyTaker1 + i, 0, (byte) 0));
		}
	}

	/**
	 * Swap 2 players.
	 */
	private SpielerPosition swap(Object object, Object object2) {
		final SpielerPosition sp = (SpielerPosition) object;
		final SpielerPosition sp2 = (SpielerPosition) object2;
		return new SpielerPosition(sp.getId(), sp2.getSpielerId(), sp2.getTaktik());
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
