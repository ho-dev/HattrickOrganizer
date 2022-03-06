// %3080703537:de.hattrickorganizer.gui.model%
package core.gui.model;

import core.model.enums.MatchType;
import core.model.match.IMatchType;
import core.util.HODateTime;
import tool.arenasizer.ArenaSizer;


/**
 * Hält die Daten für ein Spiel für die Arenastatistik
 */
public class ArenaStatistikModel {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Name des Teams zu dem die Matchinfo gehört */
    protected String m_sGastName = "";

    /** Name des Teams zu dem die Matchinfo gehört */
    protected String m_sHeimName = "";

    /** Datum des spiels */
    protected HODateTime matchDate;

    /** Gast Tore */
    protected int m_iGastTore = -1;

    /** Heim Tore */
    protected int m_iHeimTore = -1;

    /** ID des MAtches */
    protected int m_iMatchID = -1;

    /** Status des Spiels */
    protected int m_iMatchStatus = -1;

    /** Typ des Spiels */
    protected IMatchType m_mtMatchTyp = MatchType.NONE;
    private int m_iArenaGroesse;
    private int m_iFanZufriedenheit;
    private int m_iFans;
    private int m_iLigaPlatz;
    private int m_iWetter;
    private int m_iZuschaueranzahl;
    private int terraces;
    private int basics;
    private int roof;
    private int vip;
    private int maxTerraces;
    private int maxBasic;
    private int maxRoof;
    private int maxVip;

    private float currencyFactor;
    private float terracesIncome;
    private float basicSeatIncome;
    private float seatRoofIncome;
    private float vipIncome;
    private float totalIncome;
    private float matchTypeFactor;

    //~ Methods ------------------------------------------------------------------------------------


    public ArenaStatistikModel() {
        currencyFactor = core.model.UserParameter.instance().FXrate;
    }

    /**
     * Setter for property m_iArenaGroesse.
     *
     * @param m_iArenaGroesse New value of property m_iArenaGroesse.
     */
    public final void setArenaGroesse(int m_iArenaGroesse) {
        this.m_iArenaGroesse = m_iArenaGroesse;
    }

    /**
     * Getter for property m_iArenaGroesse.
     *
     * @return Value of property m_iArenaGroesse.
     */
    public final int getArenaGroesse() {
        return m_iArenaGroesse;
    }

    /**
     * Setter for property m_iFanZufriedenheit.
     *
     * @param m_iFanZufriedenheit New value of property m_iFanZufriedenheit.
     */
    public final void setFanZufriedenheit(int m_iFanZufriedenheit) {
        this.m_iFanZufriedenheit = m_iFanZufriedenheit;
    }

    /**
     * Getter for property m_iFanZufriedenheit.
     *
     * @return Value of property m_iFanZufriedenheit.
     */
    public final int getFanZufriedenheit() {
        return m_iFanZufriedenheit;
    }

    /**
     * Setter for property m_iFans.
     *
     * @param m_iFans New value of property m_iFans.
     */
    public final void setFans(int m_iFans) {
        this.m_iFans = m_iFans;
    }

    /**
     * Getter for property m_iFans.
     *
     * @return Value of property m_iFans.
     */
    public final int getFans() {
        return m_iFans;
    }

    /**
     * Setter for property m_sGastName.
     *
     * @param m_sGastName New value of property m_sGastName.
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
     * Setter for property m_sHeimName.
     *
     * @param m_sHeimName New value of property m_sHeimName.
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
     * Setter for property m_iLigaPlatz.
     *
     * @param m_iLigaPlatz New value of property m_iLigaPlatz.
     */
    public final void setLigaPlatz(int m_iLigaPlatz) {
        this.m_iLigaPlatz = m_iLigaPlatz;
    }

    /**
     * Getter for property m_iLigaPlatz.
     *
     * @return Value of property m_iLigaPlatz.
     */
    public final int getLigaPlatz() {
        return m_iLigaPlatz;
    }

    /**
     * Setter for property m_sMatchDate.
     *
     * @param m_sMatchDate New value of property m_sMatchDate.
     */
    public final void setMatchDate(HODateTime m_sMatchDate) {
        this.matchDate = m_sMatchDate;
    }

    /**
     * Getter for property m_sMatchDate.
     *
     * @return Value of property m_sMatchDate.
     */
    public final HODateTime getMatchDate() {
        return matchDate;
    }

    /**
     * Setter for property m_iMatchID.
     *
     * @param m_iMatchID New value of property m_iMatchID.
     */
    public final void setMatchID(int m_iMatchID) {
        this.m_iMatchID = m_iMatchID;
    }

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
     * @param m_iMatchStatus New value of property m_iMatchStatus.
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
     * Setter for property m_mtMatchTyp.
     *
     * @param m_mtMatchTyp New value of property m_iMatchTyp.
     */
    public final void setMatchTyp(IMatchType m_mtMatchTyp) {
        this.m_mtMatchTyp = m_mtMatchTyp;
    }

    /**
     * Getter for property m_iMatchTyp.
     *
     * @return Value of property m_iMatchTyp.
     */
    public final IMatchType getMatchTyp() {
        return m_mtMatchTyp;
    }

    /**
     * Setter for property m_iWetter.
     *
     * @param m_iWetter New value of property m_iWetter.
     */
    public final void setWetter(int m_iWetter) {
        this.m_iWetter = m_iWetter;
    }

    /**
     * Getter for property m_iWetter.
     *
     * @return Value of property m_iWetter.
     */
    public final int getWetter() {
        return m_iWetter;
    }

    /**
     * Setter for property m_iZuschaueranzahl.
     *
     * @param m_iZuschaueranzahl New value of property m_iZuschaueranzahl.
     */
    public final void setZuschaueranzahl(int m_iZuschaueranzahl) {
        this.m_iZuschaueranzahl = m_iZuschaueranzahl;
    }

    /**
     * Getter for property m_iZuschaueranzahl.
     *
     * @return Value of property m_iZuschaueranzahl.
     */
    public final int getZuschaueranzahl() {
        return m_iZuschaueranzahl;
    }

    public int getSoldTerraces() {
		return terraces;
	}

	public void setTerraces(int terraces) {
		this.terraces = terraces;
	}

	public int getSoldBasics() {
		return basics;
	}

	public void setBasics(int basics) {
		this.basics = basics;
	}

	public int getSoldRoof() {
		return roof;
	}

	public void setRoof(int roof) {
		this.roof = roof;
	}

	public int getSoldVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

    public int getMaxTerraces() {
        return maxTerraces;
    }

    public void setMaxTerraces(int maxTerraces) {
        this.maxTerraces = maxTerraces;
    }

    public int getMaxBasic() {
        return maxBasic;
    }

    public void setMaxBasic(int maxBasic) {
        this.maxBasic = maxBasic;
    }

    public int getMaxRoof() {
        return maxRoof;
    }

    public void setMaxRoof(int maxRoof) {
        this.maxRoof = maxRoof;
    }

    public int getMaxVip() {
        return maxVip;
    }

    public void setMaxVip(int maxVip) {
        this.maxVip = maxVip;
    }

    public float getTerracesIncome() {
        terracesIncome = getSoldTerraces() * (getMatchTypeFactor() * ArenaSizer.ADMISSION_PRICE_TERRACES / currencyFactor);
        return terracesIncome;
    }

    public float getBasicSeatIncome() {
        basicSeatIncome = getSoldBasics() * (getMatchTypeFactor() * ArenaSizer.ADMISSION_PRICE_BASICS / currencyFactor);
        return basicSeatIncome;
    }

    public float getSeatRoofIncome() {
        seatRoofIncome = getSoldRoof() * (getMatchTypeFactor() * ArenaSizer.ADMISSION_PRICE_ROOF / currencyFactor);
        return seatRoofIncome;
    }

    public float getVipIncome() {
        vipIncome = getSoldVip() * (getMatchTypeFactor() * ArenaSizer.ADMISSION_PRICE_VIP / currencyFactor);
        return vipIncome;
    }

    public float getTotalIncome() {
        totalIncome = getTerracesIncome() + getBasicSeatIncome() + getSeatRoofIncome() + getVipIncome();
        return totalIncome;
    }

    /*
            From HT Manual
            League matches: The home team takes all the income.
            Cup matches: The home team takes 2/3 of the income and away team gets 1/3.
            Friendlies and qualifiers: Income is split evenly
    */
    public float getMatchTypeFactor() {
        switch (MatchType.getById(m_mtMatchTyp.getMatchTypeId())) {
            case LEAGUE -> matchTypeFactor = 1;
            case CUP -> matchTypeFactor = (float) 2 / 3;
            case FRIENDLYCUPRULES, INTFRIENDLYCUPRULES, FRIENDLYNORMAL, INTFRIENDLYNORMAL, MASTERS, QUALIFICATION -> matchTypeFactor = (float) 1 / 2;
            default -> matchTypeFactor = 0;
        }
        return matchTypeFactor;
    }

    //--------------------------------------------------------------
    public final int compareTo(Object obj) {
        if (obj instanceof final ArenaStatistikModel info) {

            if (info.getMatchDate().isBefore(this.getMatchDate())) {
                return -1;
            } else if (info.getMatchDate().isAfter(this.getMatchDate())) {
                return 1;
            } else {
                return 0;
            }
        }

        return 0;
    }

}
