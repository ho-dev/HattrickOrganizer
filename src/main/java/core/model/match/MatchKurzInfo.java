package core.model.match;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.util.HOLogger;
import core.util.StringUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static core.util.StringUtils.getResultString;

public class MatchKurzInfo implements Comparable<Object> {
	// ~ Instance fields
	// ----------------------------------------------------------------------------

	/** Name des Teams zu dem die Matchinfo gehört */
	private String m_sGastName = "";

	/** Name des Teams zu dem die Matchinfo gehört */
	private String m_sHeimName = "";

	/** Datum des spiels */
	private String m_sMatchDate = "";

	/** orders given for this match? */
	private boolean ordersGiven = true;

	/** ID des Teams zu dem die info gehört */
	private int m_iGastID = -1;

	/** Gast Tore */
	private int m_iGastTore = -1;

	/** ID des Teams zu dem die info gehört */
	private int m_iHeimID = -1;

	/** Heim Tore */
	private int m_iHeimTore = -1;

	/** ID des Matches */
	private int m_iMatchID = -1;

	/** MatchContextId */
	private int iMatchContextId;
	public int getMatchContextId() {
		return iMatchContextId;
	}
	public void setMatchContextId(int iMatchContextId) {
		this.iMatchContextId = iMatchContextId;
	}

	/** TournamentTypeID */
	private int iTournamentTypeID;
	public int getTournamentTypeID() {
		return iTournamentTypeID;
	}
	public void setTournamentTypeID(int iTournamentTypeID) {
		this.iTournamentTypeID = iTournamentTypeID;
	}

	/** Status des Spiels */
	private int m_iMatchStatus = -1;
	
	/** HO user team ID */
	public static int user_team_id = HOVerwaltung.instance().getModel().getBasics().getTeamId();

	// TODO comments
	private int m_iArenaId = -1;
	private int m_iRegionId = -1;
	private Boolean m_iIsDerby = null;
	private Boolean m_iIsNeutral = null;
	private Weather m_iWeather = Weather.NULL;
	private Weather.Forecast m_iWeatherForecast = Weather.Forecast.NULL;

	// Duration of the match (null if unknown or not played yet)
	private Integer duration;

	// Details of the match, if available
	private Matchdetails matchdetails;

	public final void setArenaId(int id) {this.m_iArenaId=id;}
	public final int getArenaId() {return this.m_iArenaId;}
	public final void setRegionId(int id) {this.m_iRegionId=id;}
	public final int getRegionId(){return this.m_iRegionId;}
	public final void setIsDerby(Boolean is) {this.m_iIsDerby=is;}
	public final boolean isDerby(){return this.m_iIsDerby != null && this.m_iIsDerby.equals(Boolean.TRUE);}
	public final Boolean getIsDerby(){return this.m_iIsDerby;}
	public final void setIsNeutral(Boolean is) {this.m_iIsNeutral=is;}
	public final Boolean getIsNeutral(){return this.m_iIsNeutral;}
	public final boolean isNeutral(){return this.m_iIsNeutral != null && this.m_iIsNeutral.equals(Boolean.TRUE);}
	public final void setWeather( Weather w){this.m_iWeather=w;}
	public final Weather getWeather(){return m_iWeather;}
	public final void setWeatherForecast( Weather.Forecast wf){this.m_iWeatherForecast=wf;}
	public final Weather.Forecast getWeatherForecast(){return this.m_iWeatherForecast;}

	/** Typ des Spiels */
	private MatchType m_mtMatchTyp = MatchType.NONE;
	private CupLevel m_mtCupLevel = CupLevel.NONE;
	private CupLevelIndex m_mtCupLevelIndex = CupLevelIndex.NONE;
	private Timestamp matchDateTimestamp;
	public static final int ONGOING = 3;
	public static final int UPCOMING = 2;
	public static final int FINISHED = 1;

	/**
	 * Setter for the ordersGiven property which indicates if orders for this
	 * match are given or not.
	 * 
	 * @param ordersGiven
	 *            <code>true</code> if orders for this match are given,
	 *            <code>false</code> otherwise.
	 */
	public final void setOrdersGiven(boolean ordersGiven) {
		this.ordersGiven = ordersGiven;
	}

	/**
	 * Indicates if orders are given for this match. From HT only supplied for
	 * upcoming matches (haven't started yet).
	 * 
	 * @return <code>true</code> if orders for this match are given,
	 *         <code>false</code> otherwise.
	 */
	public final boolean isOrdersGiven() {
		return ordersGiven;
	}

	/**
	 * Setter for property m_iGastID.
	 * 
	 * @param m_iGastID
	 *            New value of property m_iGastID.
	 */
	public final void setGastID(int m_iGastID) {
		this.m_iGastID = m_iGastID;
	}

	/**
	 * Getter for property m_iGastID.
	 * 
	 * @return Value of property m_iGastID.
	 */
	public final int getGastID() {
		return m_iGastID;
	}

	/**
	 * Setter for property m_sGastName.
	 * 
	 * @param m_sGastName
	 *            New value of property m_sGastName.
	 */
	public final void setGastName(java.lang.String m_sGastName) {
		this.m_sGastName = m_sGastName;
	}

	/**
	 * Getter for property m_sGastName.
	 * 
	 * @return Value of property m_sGastName.
	 */
	public final java.lang.String getGastName() {
		return m_sGastName;
	}

	/**
	 * Setter for property m_iGastTore.
	 * 
	 * @param m_iGastTore
	 *            New value of property m_iGastTore.
	 */
	public final void setGastTore(int m_iGastTore) {
		this.m_iGastTore = m_iGastTore;
	}

	/**
	 * Getter for property m_iGastTore.
	 * 
	 * @return Value of property m_iGastTore.
	 */
	public final int getGastTore() {
		return m_iGastTore;
	}

	/**
	 * Setter for property m_iHeimID.
	 * 
	 * @param m_iHeimID
	 *            New value of property m_iHeimID.
	 */
	public final void setHeimID(int m_iHeimID) {
		this.m_iHeimID = m_iHeimID;
	}

	/**
	 * Getter for property m_iHeimID.
	 * 
	 * @return Value of property m_iHeimID.
	 */
	public final int getHeimID() {
		return m_iHeimID;
	}

	/**
	 * Setter for property m_sHeimName.
	 * 
	 * @param m_sHeimName
	 *            New value of property m_sHeimName.
	 */
	public final void setHeimName(java.lang.String m_sHeimName) {
		this.m_sHeimName = m_sHeimName;
	}

	/**
	 * Getter for property m_sHeimName.
	 * 
	 * @return Value of property m_sHeimName.
	 */
	public final java.lang.String getHeimName() {
		return m_sHeimName;
	}

	/**
	 * Setter for property m_iHeimTore.
	 * 
	 * @param m_iHeimTore
	 *            New value of property m_iHeimTore.
	 */
	public final void setHeimTore(int m_iHeimTore) {
		this.m_iHeimTore = m_iHeimTore;
	}

	/**
	 * Getter for property m_iHeimTore.
	 * 
	 * @return Value of property m_iHeimTore.
	 */
	public final int getHeimTore() {
		return m_iHeimTore;
	}

	/**
	 * Setter for property m_sMatchDate.
	 * 
	 * @param m_sMatchDate
	 *            New value of property m_sMatchDate.
	 */
	public final void setMatchDate(java.lang.String m_sMatchDate) {
		this.m_sMatchDate = m_sMatchDate;
		// ensures that getMatchDateAsTimestamp() will regenerate the timestamp
		this.matchDateTimestamp = null;
	}

	/**
	 * Getter for property m_sMatchDate.
	 * 
	 * @return Value of property m_sMatchDate.
	 */
	public final java.lang.String getMatchDate() {
		return m_sMatchDate;
	}

	/**
	 * Getter for property m_lDatum.
	 * 
	 * @return Value of property m_lDatum.
	 */
	public final java.sql.Timestamp getMatchDateAsTimestamp() {
		if (this.matchDateTimestamp == null) {
			if (!StringUtils.isEmpty(this.m_sMatchDate)) {
				try {
					SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					final Long duration = UserParameter.instance().TimeZoneDifference * 3600 * 1000L;
					this.matchDateTimestamp = new Timestamp(simpleFormat.parse(m_sMatchDate).getTime() + duration);
				  }
				catch (Exception e) {
					try {
						// Hattrick
						SimpleDateFormat simpleFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						this.matchDateTimestamp = new Timestamp(simpleFormat
								.parse(m_sMatchDate).getTime());
					} catch (Exception ex) {
						HOLogger.instance().log(getClass(), ex);
					}
				}
			}
		}
		return this.matchDateTimestamp;
	}

	/**
	 * Setter for property m_iMatchID.
	 * 
	 * @param m_iMatchID
	 *            New value of property m_iMatchID.
	 */
	public final void setMatchID(int m_iMatchID) {
		this.m_iMatchID = m_iMatchID;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// Accessor
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Getter for property m_iMatchID.
	 * 
	 * @return Value of property m_iMatchID.
	 */
	public final int getMatchID() {
		return m_iMatchID;
	}

	/**
	 * Setter for property m_iMatchStatus.
	 * 
	 * @param m_iMatchStatus
	 *            New value of property m_iMatchStatus.
	 */
	public final void setMatchStatus(int m_iMatchStatus) {
		this.m_iMatchStatus = m_iMatchStatus;
	}

	/**
	 * Getter for property m_iMatchStatus.
	 * 
	 * @return Value of property m_iMatchStatus.
	 */
	public final int getMatchStatus() {
		return m_iMatchStatus;
	}

	/**
	 * Setter for property m_iMatchTyp.
	 * 
	 * @param matchTyp
	 *            New value of property m_iMatchTyp.
	 */
	public final void setMatchType(MatchType matchTyp) {
		this.m_mtMatchTyp = matchTyp;
	}

	/**
	 * Getter for property m_iMatchTyp.
	 * 
	 * @return Value of property m_iMatchTyp.
	 */
	public final MatchType getMatchTyp() {
		return m_mtMatchTyp;
	}

	public CupLevel getCupLevel() {
		return m_mtCupLevel;
	}

	public void setCupLevel(CupLevel _CupLevel) {
		this.m_mtCupLevel = _CupLevel;
	}

	public CupLevelIndex getCupLevelIndex() {
		return m_mtCupLevelIndex;
	}

	public void setCupLevelIndex(CupLevelIndex _CupLevelIndex) {
		this.m_mtCupLevelIndex = _CupLevelIndex;
	}

	// --------------------------------------------------------------
	@Override
	public final int compareTo(Object obj) {
		if (obj instanceof MatchKurzInfo) {
			final MatchKurzInfo info = (MatchKurzInfo) obj;

			if (info.getMatchDateAsTimestamp().before(
					this.getMatchDateAsTimestamp())) {
				return -1;
			} else if (info.getMatchDateAsTimestamp().after(
					this.getMatchDateAsTimestamp())) {
				return 1;
			} else {
				return 0;
			}
		}

		return 0;
	}

	/**
	 * Merges the data from the given <code>MatchKurzInfo</code> into this
	 * <code>MatchKurzInfo</code>. This method should be used e.g. when a model
	 * has to be updated with data from a different <code>MatchKurzInfo</code>
	 * instance but and object identity has to be preserved.
	 * 
	 * @param match
	 *            the <code>MatchKurzInfo</code> to get the data from.
	 */
	public void merge(MatchKurzInfo match) {
		if (match.getMatchID() != getMatchID()) {
			throw new IllegalArgumentException(
					"Could not merge matches with different IDs");
		}
		setGastID(match.getGastID());
		setGastName(match.getGastName());
		setGastTore(match.getGastTore());
		setHeimID(match.getHeimID());
		setHeimName(match.getHeimName());
		setHeimTore(match.getHeimTore());
		setMatchDate(match.getMatchDate());
		setMatchStatus(match.getMatchStatus());
		setOrdersGiven(match.isOrdersGiven());
		setMatchType(match.getMatchTyp());
	}
	
	public final boolean isHomeMatch()
	{
		return m_iHeimID == user_team_id;
	}

	// Return duration of the match in minutes
	// null, if match is not finished or duration is unknown
	public Integer getDuration()
	{
		if ( duration == null && this.isFinished()){
			Matchdetails matchdetails = getMatchdetails();
			if ( matchdetails != null){
				duration = matchdetails.getLastMinute();
			}
		}
		return duration;
	}

	private boolean isFinished() {
		return this.m_iMatchStatus == FINISHED;
	}

	// Set duration of the match in minutes
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Matchdetails getMatchdetails() {
		if (matchdetails==null){
			matchdetails = Matchdetails.getMatchdetails(getMatchID(), getMatchTyp());
		}
		return matchdetails;
	}

	private Boolean isWalkoverMatch;
	// return true, if the opponent team didn't appear. The match was won by 5-0
	public boolean isWalkoverMatch() {
		if ( isWalkoverMatch == null) {
			isWalkoverMatch = false;
			if (getDuration() == 0) {
				// Duration of walk over matches is 0 minutes
				for (var e : getMatchdetails().getHighlights()) {
					if ( e.getMatchEventID() == MatchEvent.MatchEventID.AWAY_TEAM_WALKOVER ) {
						if (this.isHomeMatch()) {
							isWalkoverMatch = true;
						}
						break;
					}
					else if ( e.getMatchEventID() == MatchEvent.MatchEventID.HOME_TEAM_WALKOVER){
						if (!this.isHomeMatch()){
							isWalkoverMatch = true;
						}
						break;
					}
				}
			}
		}
		return isWalkoverMatch;
	}

	public String getResultLong() {
		if ( this.m_iMatchStatus != FINISHED){
			return getResultString(-1,-1,"");
		}
		return getMatchdetails().getResultLong();
	}
}
