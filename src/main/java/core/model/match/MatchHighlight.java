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

    private int m_iHighlightSubTyp;

    private int m_iHighlightTyp;

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

    /**
     * Setter for property m_iHighlightSubTyp.
     *
     * @param m_iHighlightSubTyp New value of property m_iHighlightSubTyp.
     */
    public final void setHighlightSubTyp(int m_iHighlightSubTyp) {
        this.m_iHighlightSubTyp = m_iHighlightSubTyp;
    }

    /**
     * Getter for property m_iHighlightSubTyp.
     *
     * @return Value of property m_iHighlightSubTyp.
     */
    public final int getHighlightSubTyp() {
        return m_iHighlightSubTyp;
    }

    /**
     * Setter for property m_iHighlightTyp.
     *
     * @param m_iHighlightTyp New value of property m_iHighlightTyp.
     */
    public final void setHighlightTyp(int m_iHighlightTyp) {
        this.m_iHighlightTyp = m_iHighlightTyp;
    }

    /**
     * Getter for property m_iHighlightTyp.
     *
     * @return Value of property m_iHighlightTyp.
     */
    public final int getHighlightTyp() {
        return m_iHighlightTyp;
    }

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
    	return (m_iHighlightTyp == HIGHLIGHT_SPEZIAL &&
    			(m_iHighlightSubTyp == HIGHLIGHT_SUB_PLAYER_POWERFUL_RAINY ||
    					m_iHighlightSubTyp == HIGHLIGHT_SUB_PLAYER_TECHNICAL_SUNNY ||
    					m_iHighlightSubTyp == HIGHLIGHT_SUB_PLAYER_POWERFUL_SUNNY ||
    					m_iHighlightSubTyp == HIGHLIGHT_SUB_PLAYER_QUICK_SUNNY ||
    					m_iHighlightSubTyp == HIGHLIGHT_SUB_PLAYER_QUICK_RAINY ||
    					m_iHighlightSubTyp == HIGHLIGHT_SUB_PLAYER_TECHNICAL_RAINY));
    }

    public static String getTooltiptext(int typ, int subtyp) {
        if (typ == HIGHLIGHT_KARTEN) {
            if ((subtyp == HIGHLIGHT_SUB_GELB_HARTER_EINSATZ)
                || (subtyp == HIGHLIGHT_SUB_GELB_UNFAIR)) {
                return HOVerwaltung.instance().getLanguageString("highlight_yellowcard");
            } else if ((subtyp == HIGHLIGHT_SUB_ROT)
                       || (subtyp == HIGHLIGHT_SUB_GELB_ROT_HARTER_EINSATZ)
                       || (subtyp == HIGHLIGHT_SUB_GELB_ROT_UNFAIR)) {
                return HOVerwaltung.instance().getLanguageString("highlight_redcard");
            }
        } else if (typ == HIGHLIGHT_INFORMATION) {
        	if ((subtyp == HIGHLIGHT_SUB_PFLASTER)
        			|| (subtyp == HIGHLIGHT_SUB_PFLASTER_BEHANDLUNG)) {
        		return HOVerwaltung.instance().getLanguageString("ls.player.injurystatus.bruised");
        	} else if ((subtyp == HIGHLIGHT_SUB_VERLETZT_LEICHT)
                   || (subtyp == HIGHLIGHT_SUB_VERLETZT_SCHWER)
                   || (subtyp == HIGHLIGHT_SUB_VERLETZT_KEIN_ERSATZ_EINS)
                   || (subtyp == HIGHLIGHT_SUB_VERLETZT)
                   || (subtyp == HIGHLIGHT_SUB_VERLETZT_KEIN_ERSATZ_ZWEI)) {
            return HOVerwaltung.instance().getLanguageString("ls.player.injurystatus.injured");
        	}
        }

        switch (subtyp) {
                case HIGHLIGHT_SUB_FREISTOSS:
                case HIGHLIGHT_SUB_FREISTOSS_2:
                case HIGHLIGHT_SUB_FREISTOSS_3:
                case HIGHLIGHT_SUB_FREISTOSS_4:
                case HIGHLIGHT_SUB_FREISTOSS_5:
                case HIGHLIGHT_SUB_FREISTOSS_6:
                case HIGHLIGHT_SUB_FREISTOSS_7:
                case HIGHLIGHT_SUB_FREISTOSS_8:
                    return HOVerwaltung.instance().getLanguageString("highlight_freekick");

                case HIGHLIGHT_SUB_DURCH_MITTE:
                case HIGHLIGHT_SUB_DURCH_MITTE_2:
                case HIGHLIGHT_SUB_DURCH_MITTE_3:
                case HIGHLIGHT_SUB_DURCH_MITTE_4:
                case HIGHLIGHT_SUB_DURCH_MITTE_5:
                case HIGHLIGHT_SUB_DURCH_MITTE_6:
                case HIGHLIGHT_SUB_DURCH_MITTE_7:
                case HIGHLIGHT_SUB_DURCH_MITTE_8:
                    return HOVerwaltung.instance().getLanguageString("highlight_middle");

                case HIGHLIGHT_SUB_UEBER_LINKS:
                case HIGHLIGHT_SUB_UEBER_LINKS_2:
                case HIGHLIGHT_SUB_UEBER_LINKS_3:
                case HIGHLIGHT_SUB_UEBER_LINKS_4:
                case HIGHLIGHT_SUB_UEBER_LINKS_5:
                case HIGHLIGHT_SUB_UEBER_LINKS_6:
                case HIGHLIGHT_SUB_UEBER_LINKS_7:
                case HIGHLIGHT_SUB_UEBER_LINKS_8:
                    return HOVerwaltung.instance().getLanguageString("highlight_links");

                case HIGHLIGHT_SUB_UEBER_RECHTS:
                case HIGHLIGHT_SUB_UEBER_RECHTS_2:
                case HIGHLIGHT_SUB_UEBER_RECHTS_3:
                case HIGHLIGHT_SUB_UEBER_RECHTS_4:
                case HIGHLIGHT_SUB_UEBER_RECHTS_5:
                case HIGHLIGHT_SUB_UEBER_RECHTS_6:
                case HIGHLIGHT_SUB_UEBER_RECHTS_7:
                case HIGHLIGHT_SUB_UEBER_RECHTS_8:
                    return HOVerwaltung.instance().getLanguageString("highlight_rechts");

                case HIGHLIGHT_SUB_ELFMETER:
                case HIGHLIGHT_SUB_ELFMETER_2:
                case HIGHLIGHT_SUB_ELFMETER_3:
                case HIGHLIGHT_SUB_ELFMETER_4:
                case HIGHLIGHT_SUB_ELFMETER_5:
                case HIGHLIGHT_SUB_ELFMETER_6:
                case HIGHLIGHT_SUB_ELFMETER_7:
                case HIGHLIGHT_SUB_ELFMETER_8:
                    return HOVerwaltung.instance().getLanguageString("highlight_penalty");

                case HIGHLIGHT_SUB_INDIRECT_FREEKICK_1:
                case HIGHLIGHT_SUB_INDIRECT_FREEKICK_2:
                	return HOVerwaltung.instance().getLanguageString("IFK");

                case HIGHLIGHT_SUB_LONGHSHOT_1:
                	return HOVerwaltung.instance().getLanguageString("ls.match.event.longshot");

                case HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_VORLAGE_TOR:
                case HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_ABGEFANGEN_TOR:
                case HIGHLIGHT_SUB_WEITSCHUSS_TOR:
                case HIGHLIGHT_SUB_UNVORHERSEHBAR_BALL_ERKAEMPFT_TOR:
                case HIGHLIGHT_SUB_UNVORHERSEHBAR_BALLVERLUST_TOR:
                case HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_TOR:
                case HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_PASS_TOR:
                case HIGHLIGHT_SUB_SCHLECHTE_KONDITION_BALLVERLUST_TOR:
                case HIGHLIGHT_SUB_ECKBALL_TOR:
                case HIGHLIGHT_SUB_ECKBALL_KOPFTOR:
                case HIGHLIGHT_SUB_ERFAHRENER_ANGREIFER_TOR:
                case HIGHLIGHT_SUB_UNERFAHREN_TOR:
                case HIGHLIGHT_SUB_QUERPASS_TOR:
                case HIGHLIGHT_SUB_AUSSERGEWOEHNLICHER_PASS_TOR:
                case HIGHLIGHT_SUB_TECHNIKER_ANGREIFER_TOR:
                    return HOVerwaltung.instance().getLanguageString("highlight_special");

                case HIGHLIGHT_SUB_KONTERANGRIFF_EINS:
                case HIGHLIGHT_SUB_KONTERANGRIFF_ZWEI:
                case HIGHLIGHT_SUB_KONTERANGRIFF_DREI:
                case HIGHLIGHT_SUB_KONTERANGRIFF_VIER:
                case HIGHLIGHT_SUB_KONTERANGRIFF_FUENF:
                    return HOVerwaltung.instance().getLanguageString("highlight_counter");

                default:
                    return "";
        }

    }
}
