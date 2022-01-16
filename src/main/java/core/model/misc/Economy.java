package core.model.misc;

import java.sql.Timestamp;

/**
 * Model for ECONOMY
 */
public class Economy {

    //~ Static fields -----------------------------------------------------------------
    public static final int LV_FANS_SENDING_LOVE_POEMS_TO_YOU = 11;
    public static final int LV_FANS_DANCING_IN_THE_STREETS = 10;
    public static final int LV_FANS_HIGH_ON_LIFE = 9;
    public static final int LV_FANS_DELIRIOUS = 8;
    public static final int LV_FANS_SATISFIED = 7;
    public static final int LV_FANS_CONTENT = 6;
    public static final int LV_FANS_CALM = 5;
    public static final int LV_FANS_DISAPPOINTED = 4;
    public static final int LV_FANS_IRRITATED = 3;
    public static final int LV_FANS_ANGRY = 2;
    public static final int LV_FANS_FURIOUS = 1;
    public static final int LV_FANS_MURDEROUS = 0;
    
    private static Timestamp DATE_NEW_FANLEVELS = new Timestamp(1203897600000L); // 25.02.2008

    // level constants for the sponsor mood
    public static final int LV_SPONSORS_SENDING_LOVE_POEMS_TO_YOU = 9;
    public static final int LV_SPONSORS_DANCING_IN_THE_STREETS = 8;
    public static final int LV_SPONSORS_HIGH_ON_LIFE = 7;
    public static final int LV_SPONSORS_DELIRIOUS = 6;
    public static final int LV_SPONSORS_SATISFIED = 5;
    public static final int LV_SPONSORS_CONTENT = 4;
    public static final int LV_SPONSORS_CALM = 3;
    public static final int LV_SPONSORS_IRRITATED = 2;
    public static final int LV_SPONSORS_FURIOUS = 1;
    public static final int LV_SPONSORS_MURDEROUS = 0;


    //~ Instance fields ----------------------------------------------------------------------------
    protected int m_iIncomeSum;
    protected int m_iIncomeTemporary;
    protected int m_iIncomeSponsors;
    protected int m_iIncomeSponsorsBonus;
    protected int m_iIncomeFinancial;
    protected int m_iIncomeSpectators;
    protected int m_iCash;
    protected int m_iExpectedWeeksTotal;
    protected int m_iCostsSum;
    protected int m_iCostsYouth;
    protected int m_iCostsTemporary;
    protected int m_iCostsPlayers;
    protected int m_iCostsArena;
    protected int m_iCostsStaff;
    protected int m_iCostsFinancial;
    protected int m_iLastIncomeSum;
    protected int m_iLastIncomeTemporary;
    protected int m_iLastIncomeSponsors;
    protected int m_iLastIncomeSponsorsBonus;
    protected int m_iLastIncomeFinancial;
    protected int m_iLastIncomeSpectators;
    protected int m_iLastWeeksTotal;
    protected int m_iLastCostsSum;
    protected int m_iLastCostsYouth;
    protected int m_iLastCostsTemporary;
    protected int m_iLastCostsPlayers;
    protected int m_iLastCostsArena;
    protected int m_iLastCostsStaff;
    protected int m_iLastCostsFinancial;
    protected int m_iSponsorsPopularity;
    protected  int m_iExpectedCash;
    protected  int m_iIncomeSoldPlayers;
    protected  int m_iIncomeSoldPlayersCommission;
    protected  int m_iCostsBoughtPlayers;
    protected  int m_iCostsArenaBuilding;
    protected  int m_iLastIncomeSoldPlayers;
    protected  int m_iLastIncomeSoldPlayersCommission;
    protected  int m_iLastCostsBoughtPlayers;
    protected  int m_iLastCostsArenaBuilding;
    protected int m_iSupportersPopularity;

    //Constructors -------------------------------------------------------------------------------
    public Economy(java.util.Properties properties) {

        boolean bBefore500 = Integer.parseInt(properties.getProperty("lastincomesponsorer", "0")) != 0;

        // @see updateDBv500
        if (bBefore500) {
            m_iCash = Integer.parseInt(properties.getProperty("cash", "0"));
            m_iExpectedCash = 0;
            m_iSponsorsPopularity = Integer.parseInt(properties.getProperty("sponsors", "0"));
            m_iSupportersPopularity = Integer.parseInt(properties.getProperty("supporters", "0"));
            m_iIncomeSpectators = Integer.parseInt(properties.getProperty("incomepublik", "0"));
            m_iIncomeSponsors = Integer.parseInt(properties.getProperty("incomesponsorer", "0"));
            m_iIncomeSponsorsBonus = Integer.parseInt(properties.getProperty("incomesponsorsbonus", "0"));
            m_iIncomeFinancial = Integer.parseInt(properties.getProperty("incomefinansiella", "0"));
            m_iIncomeSoldPlayers = 0;
            m_iIncomeSoldPlayersCommission = 0;
            m_iIncomeTemporary = Integer.parseInt(properties.getProperty("incometillfalliga", "0"));
            m_iIncomeSum = Integer.parseInt(properties.getProperty("incomesumma", "0"));
            m_iCostsArena = Integer.parseInt(properties.getProperty("costsarena", "0"));
            m_iCostsPlayers = Integer.parseInt(properties.getProperty("costsspelare", "0"));
            m_iCostsFinancial = Integer.parseInt(properties.getProperty("costsrantor", "0"));
            m_iCostsStaff = Integer.parseInt(properties.getProperty("costspersonal", "0"));
            m_iCostsBoughtPlayers = 0;
            m_iCostsArenaBuilding = 0;
            m_iCostsTemporary = Integer.parseInt(properties.getProperty("coststillfalliga", "0"));
            m_iCostsYouth = Integer.parseInt(properties.getProperty("costsjuniorverksamhet", "0"));
            m_iCostsSum = Integer.parseInt(properties.getProperty("costssumma", "0"));
            m_iExpectedWeeksTotal = Integer.parseInt(properties.getProperty("total", "0"));
            m_iLastIncomeSpectators = Integer.parseInt(properties.getProperty("lastincomepublik", "0"));
            m_iLastIncomeSponsors = Integer.parseInt(properties.getProperty("lastincomesponsorer", "0"));
            m_iLastIncomeSponsorsBonus = Integer.parseInt(properties.getProperty("lastincomesponsorsbonus", "0"));
            m_iLastIncomeFinancial = Integer.parseInt(properties.getProperty("lastincomefinansiella", "0"));
            m_iLastIncomeSoldPlayers = 0;
            m_iLastIncomeSoldPlayersCommission = 0;
            m_iLastIncomeTemporary = Integer.parseInt(properties.getProperty("lastincometillfalliga",   "0"));
            m_iLastIncomeSum = Integer.parseInt(properties.getProperty("lastincomesumma", "0"));
            m_iLastCostsArena = Integer.parseInt(properties.getProperty("lastcostsarena", "0"));
            m_iLastCostsPlayers = Integer.parseInt(properties.getProperty("lastcostsspelare", "0"));
            m_iLastCostsFinancial = Integer.parseInt(properties.getProperty("lastcostsrantor", "0"));
            m_iLastCostsStaff = Integer.parseInt(properties.getProperty("lastcostspersonal", "0"));
            m_iLastCostsTemporary = Integer.parseInt(properties.getProperty("lastcoststillfalliga",  "0"));
            m_iLastCostsYouth = Integer.parseInt(properties.getProperty("lastCostsjuniorverksamhet",  "0"));
            m_iLastCostsSum = Integer.parseInt(properties.getProperty("lastcostssumma", "0"));
            m_iLastWeeksTotal = Integer.parseInt(properties.getProperty("lasttotal", "0"));
            m_iLastCostsBoughtPlayers = 0;
            m_iLastCostsArenaBuilding = 0;
        }
        else{
            m_iCash = Integer.parseInt(properties.getProperty("cash", "0"));
            m_iExpectedCash = Integer.parseInt(properties.getProperty("expectedcash", "0"));
            m_iSponsorsPopularity = Integer.parseInt(properties.getProperty("sponsorspopularity", "0"));
            m_iSupportersPopularity = Integer.parseInt(properties.getProperty("supporterspopularity", "0"));
            m_iIncomeSpectators = Integer.parseInt(properties.getProperty("incomespectators", "0"));
            m_iIncomeSponsors = Integer.parseInt(properties.getProperty("incomesponsors", "0"));
            m_iIncomeSponsorsBonus = Integer.parseInt(properties.getProperty("incomesponsorsbonus", "0"));
            m_iIncomeFinancial = Integer.parseInt(properties.getProperty("incomefinancial", "0"));
            m_iIncomeSoldPlayers = Integer.parseInt(properties.getProperty("incomesoldplayers", "0"));
            m_iIncomeSoldPlayersCommission = Integer.parseInt(properties.getProperty("incomesoldplayerscommission", "0"));
            m_iIncomeTemporary = Integer.parseInt(properties.getProperty("incometemporary", "0"));
            m_iIncomeSum = Integer.parseInt(properties.getProperty("incomesum", "0"));
            m_iCostsArena = Integer.parseInt(properties.getProperty("costsarena", "0"));
            m_iCostsPlayers = Integer.parseInt(properties.getProperty("costsplayers", "0"));
            m_iCostsFinancial = Integer.parseInt(properties.getProperty("costsfinancial", "0"));
            m_iCostsStaff = Integer.parseInt(properties.getProperty("costsstaff", "0"));
            m_iCostsBoughtPlayers = Integer.parseInt(properties.getProperty("costsboughtplayers", "0"));
            m_iCostsArenaBuilding = Integer.parseInt(properties.getProperty("costsarenabuilding", "0"));
            m_iCostsTemporary = Integer.parseInt(properties.getProperty("coststemporary", "0"));
            m_iCostsYouth = Integer.parseInt(properties.getProperty("costsyouth", "0"));
            m_iCostsSum = Integer.parseInt(properties.getProperty("costssum", "0"));
            m_iExpectedWeeksTotal = Integer.parseInt(properties.getProperty("expectedweekstotal", "0"));
            m_iLastIncomeSpectators = Integer.parseInt(properties.getProperty("lastincomespectators", "0"));
            m_iLastIncomeSponsors = Integer.parseInt(properties.getProperty("lastincomesponsors", "0"));
            m_iLastIncomeSponsorsBonus = Integer.parseInt(properties.getProperty("lastincomesponsorsbonus", "0"));
            m_iLastIncomeFinancial = Integer.parseInt(properties.getProperty("lastincomefinancial", "0"));
            m_iLastIncomeSoldPlayers = Integer.parseInt(properties.getProperty("lastincomesoldplayers", "0"));
            m_iLastIncomeSoldPlayersCommission = Integer.parseInt(properties.getProperty("lastincomesoldplayerscommission", "0"));
            m_iLastIncomeTemporary = Integer.parseInt(properties.getProperty("lastincometemporary",   "0"));
            m_iLastIncomeSum = Integer.parseInt(properties.getProperty("lastincomesum", "0"));
            m_iLastCostsArena = Integer.parseInt(properties.getProperty("lastcostsarena", "0"));
            m_iLastCostsPlayers = Integer.parseInt(properties.getProperty("lastcostsplayers", "0"));
            m_iLastCostsFinancial = Integer.parseInt(properties.getProperty("lastcostsfinancial", "0"));
            m_iLastCostsStaff = Integer.parseInt(properties.getProperty("lastcostsstaff", "0"));
            m_iLastCostsTemporary = Integer.parseInt(properties.getProperty("lastcoststemporary",  "0"));
            m_iLastCostsYouth = Integer.parseInt(properties.getProperty("lastcostsyouth",  "0"));
            m_iLastCostsSum = Integer.parseInt(properties.getProperty("lastcostssum", "0"));
            m_iLastWeeksTotal = Integer.parseInt(properties.getProperty("lastweekstotal", "0"));
            m_iLastCostsBoughtPlayers = Integer.parseInt(properties.getProperty("lastcostsboughtplayers","0"));
            m_iLastCostsArenaBuilding = Integer.parseInt(properties.getProperty("lastcostsarenabuilding","0"));
        }
    }

    public Economy() {}

    //~ Methods ------------------------------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////////////////
    //Static
    ////////////////////////////////////////////////////////////////////////////////    

    /**
     * Get the name string for a numerical level.
     *
     * @param level the numerical value (e.g. from CHPP interface)
     * @return the i18n'ed name for the level
     */
    public static String getNameForLevelFans(int level) {
    	return getNameForLevelFans(level, null);
    }
    
    /**
     * Get the name string for a numerical level.
     *
     * @param level the numerical value (e.g. from CHPP interface)
     * @param date time of the match (importand cause the fanlevels changed)
     * @return the i18n'ed name for the level
     */
    public static String getNameForLevelFans(int level, Timestamp date) {
    	// previously, fan and sponsor levels where identical, 
    	//   thats why we can simply use sponsor values
    	if (date != null && date.before(DATE_NEW_FANLEVELS)) {
    		return getNameForLevelSponsors(level);
    	}
        switch (level) {
            case LV_FANS_SENDING_LOVE_POEMS_TO_YOU:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.sendinglovepoemstoyou");

            case LV_FANS_DANCING_IN_THE_STREETS:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.dancinginthestreets");

            case LV_FANS_HIGH_ON_LIFE:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.highonlife");

            case LV_FANS_DELIRIOUS:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.delirious");

            case LV_FANS_SATISFIED:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.satisfied");

            case LV_FANS_CONTENT:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.content");

            case LV_FANS_CALM:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.calm");

            case LV_FANS_DISAPPOINTED:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.fans.disappointed");
                
            case LV_FANS_IRRITATED:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.irritated");
                
            case LV_FANS_ANGRY:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.fans.angry");

            case LV_FANS_FURIOUS:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.furious");

            case LV_FANS_MURDEROUS:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.murderous");

            default: {
                if (level > LV_FANS_SENDING_LOVE_POEMS_TO_YOU) {
                    return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.sendinglovepoemstoyou");
                }

                return core.model.HOVerwaltung.instance().getLanguageString("Unbestimmt");
            }
        }
    }

    /**
     * Get the name string for a numerical level.
     *
     * @param level the numerical value (e.g. from CHPP interface)
     * @return the i18n'ed name for the level
     */
    public static String getNameForLevelSponsors(int level) {
        switch (level) {
            case LV_SPONSORS_SENDING_LOVE_POEMS_TO_YOU:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.sendinglovepoemstoyou");

            case LV_SPONSORS_DANCING_IN_THE_STREETS:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.dancinginthestreets");

            case LV_SPONSORS_HIGH_ON_LIFE:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.highonlife");

            case LV_SPONSORS_DELIRIOUS:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.delirious");

            case LV_SPONSORS_SATISFIED:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.satisfied");

            case LV_SPONSORS_CONTENT:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.content");

            case LV_SPONSORS_CALM:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.calm");

            case LV_SPONSORS_IRRITATED:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.irritated");
                
            case LV_SPONSORS_FURIOUS:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.furious");

            case LV_SPONSORS_MURDEROUS:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.murderous");

            default: {
                if (level > LV_SPONSORS_SENDING_LOVE_POEMS_TO_YOU) {
                    return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.sendinglovepoemstoyou");
                }

                return core.model.HOVerwaltung.instance().getLanguageString("Unbestimmt");
            }
        }
    }

    public final void setIncomeSum(int iIncomeSum) {
        this.m_iIncomeSum = iIncomeSum;
    }

    public final int getIncomeSum() {return m_iIncomeSum;}

    public final void setIncomeTemporary(int iIncomeTemporary) {
        this.m_iIncomeTemporary = iIncomeTemporary;
    }

    public final int getIncomeTemporary() {return m_iIncomeTemporary;}

    public final void setIncomeSponsors(int iIncomeSponsors) {this.m_iIncomeSponsors = iIncomeSponsors; }

    public final void setIncomeSponsorsBonus(int iIncomeSponsorsBonus) {this.m_iIncomeSponsorsBonus = iIncomeSponsorsBonus; }

    public final int getIncomeSponsors() {return m_iIncomeSponsors;}

    public final int getIncomeSponsorsBonus() {return m_iIncomeSponsorsBonus;}

    public final void setIncomeFinancial(int iIncomeFinancial) {
        this.m_iIncomeFinancial = iIncomeFinancial;
    }

    public final int getIncomeFinancial() {return m_iIncomeFinancial;}

    public final void setIncomeSpectators(int iIncomeSpectators) {this.m_iIncomeSpectators = iIncomeSpectators;}

    public final int getIncomeSpectators() {return m_iIncomeSpectators;}

    public final void setCash(int m_iCash) {
        this.m_iCash = m_iCash;
    }

    public final int getCash() {return m_iCash;}

    public final void setExpectedWeeksTotal(int iExpectedWeeksTotal) {this.m_iExpectedWeeksTotal = iExpectedWeeksTotal;}

    public final int getExpectedWeeksTotal() {return m_iExpectedWeeksTotal;}

    public final void setCostsSum(int iCostsSum) {
        this.m_iCostsSum = iCostsSum;
    }

    public final int getCostsSum() {return m_iCostsSum;}

    public final void setCostsYouth(int iCostsYouth) {
        this.m_iCostsYouth = iCostsYouth;
    }

    public final int getCostsYouth() {return m_iCostsYouth;}

    public final void setCostsTemporary(int iCostsTemporary) {
        this.m_iCostsTemporary = iCostsTemporary;
    }

    public final int getCostsTemporary() {return m_iCostsTemporary;}

    public final void setCostsPlayers(int iCostsPlayers) {
        this.m_iCostsPlayers = iCostsPlayers;
    }

    public final int getCostsPlayers() {return m_iCostsPlayers;}

    public final void setCostsArena(int iCostsArena) {
        this.m_iCostsArena = iCostsArena;
    }

    public final int getCostsArena() {return m_iCostsArena;}

    public final void setCostsStaff(int iCostsStaff) {
        this.m_iCostsStaff = iCostsStaff;
    }

    public final int getCostsStaff() {return m_iCostsStaff;}

    public final void setCostsFinancial(int iCostsFinancial) {
        this.m_iCostsFinancial = iCostsFinancial;
    }

    public final int getCostsFinancial() {return m_iCostsFinancial;}

    public final void setLastIncomeSum(int iLastIncomeSum) {this.m_iLastIncomeSum = iLastIncomeSum;}

    public final int getLastIncomeSum() {return m_iLastIncomeSum;}

    public final void setLastIncomeTemporary(int iLastIncomeTemporary) {this.m_iLastIncomeTemporary = iLastIncomeTemporary;}

    public final int getLastIncomeTemporary() {return m_iLastIncomeTemporary;}

    public final void setLastIncomeSponsors(int iLastIncomeSponsors) {this.m_iLastIncomeSponsors = iLastIncomeSponsors;}

    public final void setLastIncomeSponsorsBonus(int iLastIncomeSponsorsBonus) {this.m_iLastIncomeSponsorsBonus = iLastIncomeSponsorsBonus;}

    public final int getLastIncomeSponsors() {return m_iLastIncomeSponsors;}

    public final int getLastIncomeSponsorsBonus() {return m_iLastIncomeSponsorsBonus;}

    public final void setLastIncomeFinancial(int iLastIncomeFinancial) {this.m_iLastIncomeFinancial = iLastIncomeFinancial;}

    public final int getLastIncomeFinancial() {return m_iLastIncomeFinancial;}

    public final void setLastIncomeSpectators(int iLastIncomeSpectators) {this.m_iLastIncomeSpectators = iLastIncomeSpectators;}

    public final int getLastIncomeSpectators() {return m_iLastIncomeSpectators;}

    public final void setLastWeeksTotal(int iLastWeeksTotal) {this.m_iLastWeeksTotal = iLastWeeksTotal;}

    public final int getLastWeeksTotal() {return m_iLastWeeksTotal;}

    public final void setLastCostsSum(int iLastCostsSum) {
        this.m_iLastCostsSum = iLastCostsSum;
    }

    public final int getLastCostsSum() {return m_iLastCostsSum;}

    public final void setLastCostsYouth(int iLastCostsYouth) {this.m_iLastCostsYouth = iLastCostsYouth;}

    public final int getLastCostsYouth() {return m_iLastCostsYouth;}

    public final void setLastCostsTemporary(int iLastCostsTemporary) {this.m_iLastCostsTemporary = iLastCostsTemporary;}

    public final int getLastCostsTemporary() {return m_iLastCostsTemporary;}

    public final void setLastCostsPlayers(int iLastCostsPlayers) {this.m_iLastCostsPlayers = iLastCostsPlayers;}

    public final int getLastCostsPlayers() {return m_iLastCostsPlayers;}

    public final void setLastCostsArena(int iLastCostsArena) {this.m_iLastCostsArena = iLastCostsArena;}

    public final int getLastCostsArena() {return m_iLastCostsArena;}

    public final void setLastCostsStaff(int iLastCostsStaff) {this.m_iLastCostsStaff = iLastCostsStaff;}

    public final int getLastCostsStaff() {return m_iLastCostsStaff;}

    public final void setLastCostsFinancial(int iLastCostsFinancial) {this.m_iLastCostsFinancial = iLastCostsFinancial;}

    public final int getLastCostsFinancial() {return m_iLastCostsFinancial;}

    public final void setSponsorsPopularity(int iSponsorsPopularity) {this.m_iSponsorsPopularity = iSponsorsPopularity;}

    public final int getSponsorsPopularity() {
        return m_iSponsorsPopularity;
    }

    public final void setSupPopularity(int iSupportersPopularity) {this.m_iSupportersPopularity = iSupportersPopularity;}

    public final int getSupportersPopularity() {
        return m_iSupportersPopularity;
    }

    public final void setExpectedCash(int iExpectedCash) {
        this.m_iExpectedCash = iExpectedCash;
    }

    public final int getExpectedCash() {
        return m_iExpectedCash;
    }

    public final void setIncomeSoldPlayers(int iIncomeSoldPlayers) {this.m_iIncomeSoldPlayers = iIncomeSoldPlayers;}

    public final int getIncomeSoldPlayers() {
        return m_iIncomeSoldPlayers;
    }

    public final void setIncomeSoldPlayersCommission(int iIncomeSoldPlayersCommission) {this.m_iIncomeSoldPlayersCommission = iIncomeSoldPlayersCommission;}

    public final int getIncomeSoldPlayersCommission() {
        return m_iIncomeSoldPlayersCommission;
    }

    public final void setCostsBoughtPlayers(int iCostsBoughtPlayers) {this.m_iCostsBoughtPlayers = iCostsBoughtPlayers;}

    public final int getCostsBoughtPlayers() {
        return m_iCostsBoughtPlayers;
    }

    public final void setCostsArenaBuilding(int iCostsArenaBuilding) {this.m_iCostsArenaBuilding = iCostsArenaBuilding;}

    public final int getCostsArenaBuilding() {
        return m_iCostsArenaBuilding;
    }

    public final void setLastIncomeSoldPlayers(int iLastIncomeSoldPlayers) {this.m_iLastIncomeSoldPlayers = iLastIncomeSoldPlayers;}

    public final int getLastIncomeSoldPlayers() {return m_iLastIncomeSoldPlayers;}

    public final void setLastIncomeSoldPlayersCommission(int iLastIncomeSoldPlayersCommission) {
        this.m_iLastIncomeSoldPlayersCommission = iLastIncomeSoldPlayersCommission;
    }

    public final int getLastIncomeSoldPlayersCommission() {
        return m_iLastIncomeSoldPlayersCommission;
    }

    public final void setLastCostsBoughtPlayers(int iLastCostsBoughtPlayers) {
        this.m_iLastCostsBoughtPlayers = iLastCostsBoughtPlayers;
    }

    public final int getLastCostsBoughtPlayers() {
        return m_iLastCostsBoughtPlayers;
    }

    public final void setLastCostsArenaBuilding(int iLastCostsArenaBuilding) {
        this.m_iLastCostsArenaBuilding = iLastCostsArenaBuilding;
    }

    public final int getLastCostsArenaBuilding() {
        return m_iLastCostsArenaBuilding;
    }


}
