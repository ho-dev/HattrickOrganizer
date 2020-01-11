package core.model.match;

import core.model.HOVerwaltung;

public class MatchHighlight implements core.model.match.IMatchHighlight {

    private String m_sEventText = "";

    private String m_sGehilfeName = "";

    private String m_sSpielerName = "";

    private boolean m_sGehilfeHeim = true;

    private boolean m_sSpielerHeim = true;

    private int m_iGastTore;

     private int m_iGehilfeID;

    private int m_iHeimTore;

    private int m_iMatchEventCategory;

    private int m_iMatchEventID;

    private int m_iMinute;

    private int m_iSpielerID;

    private int m_iTeamID;

    private int matchId;


    /**
     * Creates a new instance of MatchHighlight
     */
    public MatchHighlight() {
    }

    //~ Methods ------------------------------------------------------------------------------------




    /**
     * Setter for property m_sEventText.
     *
     * @param m_sEventText New value of property m_sEventText.
     */
    public final void setEventText(String m_sEventText) {
        this.m_sEventText = m_sEventText;
    }

    public final int getMatchId() {
		return matchId;
	}

	public final void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	/**
     * Getter for property m_sEventText.
     *
     * @return Value of property m_sEventText.
     */
    public final String getEventText() {
        return m_sEventText;
    }

    /**
     * Setter for property m_iGastTore.
     *
     * @param m_iGastTore New value of property m_iGastTore.
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
     * Setter for property m_sGehilfeHeim.
     *
     * @param m_sGehilfeHeim New value of property m_sGehilfeHeim.
     */
    public final void setGehilfeHeim(boolean m_sGehilfeHeim) {
        this.m_sGehilfeHeim = m_sGehilfeHeim;
    }

    /**
     * Getter for property m_sGehilfeHeim.
     *
     * @return Value of property m_sGehilfeHeim.
     */
    public final boolean getGehilfeHeim() {
        return m_sGehilfeHeim;
    }

    /**
     * Setter for property m_iGehilfeID.
     *
     * @param m_iGehilfeID New value of property m_iGehilfeID.
     */
    public final void setGehilfeID(int m_iGehilfeID) {
        this.m_iGehilfeID = m_iGehilfeID;
    }

    /**
     * Getter for property m_iGehilfeID.
     *
     * @return Value of property m_iGehilfeID.
     */
    public final int getGehilfeID() {
        return m_iGehilfeID;
    }

    /**
     * Setter for property m_sGehilfeName.
     *
     * @param m_sGehilfeName New value of property m_sGehilfeName.
     */
    public final void setGehilfeName(String m_sGehilfeName) {
        this.m_sGehilfeName = m_sGehilfeName;
    }

    /**
     * Getter for property m_sGehilfeName.
     *
     * @return Value of property m_sGehilfeName.
     */
    public final String getGehilfeName() {
        return m_sGehilfeName;
    }

    /**
     * Setter for property m_iHeimTore.
     *
     * @param m_iHeimTore New value of property m_iHeimTore.
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


    public final void setMatchEventID(int m_iMatchEventID) {
        this.m_iMatchEventID = m_iMatchEventID;
    }

    public final int getMatchEventID() {
        return m_iMatchEventID;
    }

    public final void setMatchEventCategory(int m_iMatchEventCategory) {this.m_iMatchEventCategory = m_iMatchEventCategory;}

    public final int getMatchEventCategory() {return m_iMatchEventCategory;}

    /**
     * Setter for property m_iMinute.
     *
     * @param m_iMinute New value of property m_iMinute.
     */
    public final void setMinute(int m_iMinute) {
        this.m_iMinute = m_iMinute;
    }

    /**
     * Getter for property m_iMinute.
     *
     * @return Value of property m_iMinute.
     */
    public final int getMinute() {
        return m_iMinute;
    }

    /**
     * Setter for property m_sSpielerHeim.
     *
     * @param m_sSpielerHeim New value of property m_sSpielerHeim.
     */
    public final void setSpielerHeim(boolean m_sSpielerHeim) {
        this.m_sSpielerHeim = m_sSpielerHeim;
    }

    /**
     * Getter for property m_sSpielerHeim.
     *
     * @return Value of property m_sSpielerHeim.
     */
    public final boolean getSpielerHeim() {
        return m_sSpielerHeim;
    }

    /**
     * Setter for property m_iSpielerID.
     *
     * @param m_iSpielerID New value of property m_iSpielerID.
     */
    public final void setSpielerID(int m_iSpielerID) {
        this.m_iSpielerID = m_iSpielerID;
    }

    /**
     * Getter for property m_iSpielerID.
     *
     * @return Value of property m_iSpielerID.
     */
    public final int getSpielerID() {
        return m_iSpielerID;
    }

    /**
     * Setter for property m_sSpielerName.
     *
     * @param m_sSpielerName New value of property m_sSpielerName.
     */
    public final void setSpielerName(String m_sSpielerName) {
        this.m_sSpielerName = m_sSpielerName;
    }

    /**
     * Getter for property m_sSpielerName.
     *
     * @return Value of property m_sSpielerName.
     */
    public final String getSpielerName() {
        return m_sSpielerName;
    }

    /**
     * Setter for property m_iTeamID.
     *
     * @param m_iTeamID New value of property m_iTeamID.
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
     * Check, if the highlight is a weather SE highlight.
     */
    public boolean isWeatherSEHighlight() {
    	return
    			(m_iMatchEventCategory ==  MATCH_EVENT_SE_TECHNICAL_SUFFERS_FROM_RAIN ||
    					m_iMatchEventCategory == MATCH_EVENT_SE_POWERFUL_THRIVES_IN_RAIN ||
    					m_iMatchEventCategory == MATCH_EVENT_SE_TECHNICAL_THRIVES_IN_SUN ||
    					m_iMatchEventCategory == MATCH_EVENT_SE_POWERFUL_SUFFERS_FROM_SUN ||
    					m_iMatchEventCategory == MATCH_EVENT_SE_QUICK_LOSES_IN_RAIN ||
    					m_iMatchEventCategory == MATCH_EVENT_SE_QUICK_LOSES_IN_SUN);
    }

    public static String getTooltiptext(int subtyp) {
        

                default:
                    return "";
        }

    }
}
