package core.model.misc;

import core.db.AbstractTable;
import core.model.TranslationFacility;
import core.util.AmountOfMoney;
import core.util.HODateTime;

/**
 * Model for ECONOMY
 */
public class Economy extends AbstractTable.Storable {

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
    
    private static final HODateTime DATE_NEW_FANLEVELS = HODateTime.fromHT("2008-02-25 00:00:00"); // 25.02.2008

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
    protected AmountOfMoney m_iIncomeSum;
    protected AmountOfMoney m_iIncomeTemporary;
    protected AmountOfMoney m_iIncomeSponsors;
    protected AmountOfMoney m_iIncomeSponsorsBonus;
    protected AmountOfMoney m_iIncomeFinancial;
    protected AmountOfMoney m_iIncomeSpectators;
    protected AmountOfMoney m_iCash;
    protected AmountOfMoney m_iExpectedWeeksTotal;
    protected AmountOfMoney m_iCostsSum;
    protected AmountOfMoney m_iCostsYouth;
    protected AmountOfMoney m_iCostsTemporary;
    protected AmountOfMoney m_iCostsPlayers;
    protected AmountOfMoney m_iCostsArena;
    protected AmountOfMoney m_iCostsStaff;
    protected AmountOfMoney m_iCostsFinancial;
    protected AmountOfMoney m_iLastIncomeSum;
    protected AmountOfMoney m_iLastIncomeTemporary;
    protected AmountOfMoney m_iLastIncomeSponsors;
    protected AmountOfMoney m_iLastIncomeSponsorsBonus;
    protected AmountOfMoney m_iLastIncomeFinancial;
    protected AmountOfMoney m_iLastIncomeSpectators;
    protected AmountOfMoney m_iLastWeeksTotal;
    protected AmountOfMoney m_iLastCostsSum;
    protected AmountOfMoney m_iLastCostsYouth;
    protected AmountOfMoney m_iLastCostsTemporary;
    protected AmountOfMoney m_iLastCostsPlayers;
    protected AmountOfMoney m_iLastCostsArena;
    protected AmountOfMoney m_iLastCostsStaff;
    protected AmountOfMoney m_iLastCostsFinancial;
    protected int m_iSponsorsPopularity;
    protected  AmountOfMoney m_iExpectedCash;
    protected  AmountOfMoney m_iIncomeSoldPlayers;
    protected  AmountOfMoney m_iIncomeSoldPlayersCommission;
    protected  AmountOfMoney m_iCostsBoughtPlayers;
    protected  AmountOfMoney m_iCostsArenaBuilding;
    protected  AmountOfMoney m_iLastIncomeSoldPlayers;
    protected  AmountOfMoney m_iLastIncomeSoldPlayersCommission;
    protected  AmountOfMoney m_iLastCostsBoughtPlayers;
    protected  AmountOfMoney m_iLastCostsArenaBuilding;
    protected int m_iSupportersPopularity;
    private int hrfId;
    private HODateTime fetchedDate;

    //Constructors -------------------------------------------------------------------------------
    public Economy(java.util.Properties properties) {

        boolean bBefore500 = Integer.parseInt(properties.getProperty("lastincomesponsorer", "0")) != 0;

        // @see updateDBv500
        if (bBefore500) {
            m_iCash = AmountOfMoney.Companion.parse(properties.getProperty("cash", "0"));
            m_iExpectedCash = new AmountOfMoney(0);
            m_iSponsorsPopularity = Integer.parseInt(properties.getProperty("sponsors", "0"));
            m_iSupportersPopularity = Integer.parseInt(properties.getProperty("supporters", "0"));
            m_iIncomeSpectators = AmountOfMoney.Companion.parse(properties.getProperty("incomepublik", "0"));
            m_iIncomeSponsors = AmountOfMoney.Companion.parse(properties.getProperty("incomesponsorer", "0"));
            m_iIncomeSponsorsBonus = AmountOfMoney.Companion.parse(properties.getProperty("incomesponsorsbonus", "0"));
            m_iIncomeFinancial = AmountOfMoney.Companion.parse(properties.getProperty("incomefinansiella", "0"));
            m_iIncomeSoldPlayers = new AmountOfMoney(0);
            m_iIncomeSoldPlayersCommission = new AmountOfMoney(0);
            m_iIncomeTemporary = AmountOfMoney.Companion.parse(properties.getProperty("incometillfalliga", "0"));
            m_iIncomeSum = AmountOfMoney.Companion.parse(properties.getProperty("incomesumma", "0"));
            m_iCostsArena = AmountOfMoney.Companion.parse(properties.getProperty("costsarena", "0"));
            m_iCostsPlayers = AmountOfMoney.Companion.parse(properties.getProperty("costsspelare", "0"));
            m_iCostsFinancial = AmountOfMoney.Companion.parse(properties.getProperty("costsrantor", "0"));
            m_iCostsStaff = AmountOfMoney.Companion.parse(properties.getProperty("costspersonal", "0"));
            m_iCostsBoughtPlayers = new AmountOfMoney(0);
            m_iCostsArenaBuilding = new AmountOfMoney(0);
            m_iCostsTemporary = AmountOfMoney.Companion.parse(properties.getProperty("coststillfalliga", "0"));
            m_iCostsYouth = AmountOfMoney.Companion.parse(properties.getProperty("costsjuniorverksamhet", "0"));
            m_iCostsSum = AmountOfMoney.Companion.parse(properties.getProperty("costssumma", "0"));
            m_iExpectedWeeksTotal =AmountOfMoney.Companion.parse(properties.getProperty("total", "0"));
            m_iLastIncomeSpectators = AmountOfMoney.Companion.parse(properties.getProperty("lastincomepublik", "0"));
            m_iLastIncomeSponsors = AmountOfMoney.Companion.parse(properties.getProperty("lastincomesponsorer", "0"));
            m_iLastIncomeSponsorsBonus = AmountOfMoney.Companion.parse(properties.getProperty("lastincomesponsorsbonus", "0"));
            m_iLastIncomeFinancial = AmountOfMoney.Companion.parse(properties.getProperty("lastincomefinansiella", "0"));
            m_iLastIncomeSoldPlayers = new AmountOfMoney(0);
            m_iLastIncomeSoldPlayersCommission = new AmountOfMoney(0);
            m_iLastIncomeTemporary = AmountOfMoney.Companion.parse(properties.getProperty("lastincometillfalliga",   "0"));
            m_iLastIncomeSum = AmountOfMoney.Companion.parse(properties.getProperty("lastincomesumma", "0"));
            m_iLastCostsArena = AmountOfMoney.Companion.parse(properties.getProperty("lastcostsarena", "0"));
            m_iLastCostsPlayers = AmountOfMoney.Companion.parse(properties.getProperty("lastcostsspelare", "0"));
            m_iLastCostsFinancial = AmountOfMoney.Companion.parse(properties.getProperty("lastcostsrantor", "0"));
            m_iLastCostsStaff = AmountOfMoney.Companion.parse(properties.getProperty("lastcostspersonal", "0"));
            m_iLastCostsTemporary =AmountOfMoney.Companion.parse(properties.getProperty("lastcoststillfalliga",  "0"));
            m_iLastCostsYouth = AmountOfMoney.Companion.parse(properties.getProperty("lastCostsjuniorverksamhet",  "0"));
            m_iLastCostsSum =AmountOfMoney.Companion.parse(properties.getProperty("lastcostssumma", "0"));
            m_iLastWeeksTotal =AmountOfMoney.Companion.parse(properties.getProperty("lasttotal", "0"));
            m_iLastCostsBoughtPlayers = new AmountOfMoney(0);
            m_iLastCostsArenaBuilding = new AmountOfMoney(0);
        }
        else{
            m_iCash = AmountOfMoney.Companion.parse(properties.getProperty("cash", "0"));
            m_iExpectedCash = AmountOfMoney.Companion.parse(properties.getProperty("expectedcash", "0"));
            m_iSponsorsPopularity = Integer.parseInt(properties.getProperty("sponsorspopularity", "0"));
            m_iSupportersPopularity = Integer.parseInt(properties.getProperty("supporterspopularity", "0"));
            m_iIncomeSpectators = AmountOfMoney.Companion.parse(properties.getProperty("incomespectators", "0"));
            m_iIncomeSponsors = AmountOfMoney.Companion.parse(properties.getProperty("incomesponsors", "0"));
            m_iIncomeSponsorsBonus = AmountOfMoney.Companion.parse(properties.getProperty("incomesponsorsbonus", "0"));
            m_iIncomeFinancial =AmountOfMoney.Companion.parse(properties.getProperty("incomefinancial", "0"));
            m_iIncomeSoldPlayers = AmountOfMoney.Companion.parse(properties.getProperty("incomesoldplayers", "0"));
            m_iIncomeSoldPlayersCommission = AmountOfMoney.Companion.parse(properties.getProperty("incomesoldplayerscommission", "0"));
            m_iIncomeTemporary = AmountOfMoney.Companion.parse(properties.getProperty("incometemporary", "0"));
            m_iIncomeSum = AmountOfMoney.Companion.parse(properties.getProperty("incomesum", "0"));
            m_iCostsArena = AmountOfMoney.Companion.parse(properties.getProperty("costsarena", "0"));
            m_iCostsPlayers = AmountOfMoney.Companion.parse(properties.getProperty("costsplayers", "0"));
            m_iCostsFinancial = AmountOfMoney.Companion.parse(properties.getProperty("costsfinancial", "0"));
            m_iCostsStaff = AmountOfMoney.Companion.parse(properties.getProperty("costsstaff", "0"));
            m_iCostsBoughtPlayers = AmountOfMoney.Companion.parse(properties.getProperty("costsboughtplayers", "0"));
            m_iCostsArenaBuilding = AmountOfMoney.Companion.parse(properties.getProperty("costsarenabuilding", "0"));
            m_iCostsTemporary = AmountOfMoney.Companion.parse(properties.getProperty("coststemporary", "0"));
            m_iCostsYouth = AmountOfMoney.Companion.parse(properties.getProperty("costsyouth", "0"));
            m_iCostsSum = AmountOfMoney.Companion.parse(properties.getProperty("costssum", "0"));
            m_iExpectedWeeksTotal = AmountOfMoney.Companion.parse(properties.getProperty("expectedweekstotal", "0"));
            m_iLastIncomeSpectators = AmountOfMoney.Companion.parse(properties.getProperty("lastincomespectators", "0"));
            m_iLastIncomeSponsors = AmountOfMoney.Companion.parse(properties.getProperty("lastincomesponsors", "0"));
            m_iLastIncomeSponsorsBonus = AmountOfMoney.Companion.parse(properties.getProperty("lastincomesponsorsbonus", "0"));
            m_iLastIncomeFinancial = AmountOfMoney.Companion.parse(properties.getProperty("lastincomefinancial", "0"));
            m_iLastIncomeSoldPlayers = AmountOfMoney.Companion.parse(properties.getProperty("lastincomesoldplayers", "0"));
            m_iLastIncomeSoldPlayersCommission = AmountOfMoney.Companion.parse(properties.getProperty("lastincomesoldplayerscommission", "0"));
            m_iLastIncomeTemporary = AmountOfMoney.Companion.parse(properties.getProperty("lastincometemporary",   "0"));
            m_iLastIncomeSum = AmountOfMoney.Companion.parse(properties.getProperty("lastincomesum", "0"));
            m_iLastCostsArena = AmountOfMoney.Companion.parse(properties.getProperty("lastcostsarena", "0"));
            m_iLastCostsPlayers = AmountOfMoney.Companion.parse(properties.getProperty("lastcostsplayers", "0"));
            m_iLastCostsFinancial = AmountOfMoney.Companion.parse(properties.getProperty("lastcostsfinancial", "0"));
            m_iLastCostsStaff = AmountOfMoney.Companion.parse(properties.getProperty("lastcostsstaff", "0"));
            m_iLastCostsTemporary = AmountOfMoney.Companion.parse(properties.getProperty("lastcoststemporary",  "0"));
            m_iLastCostsYouth = AmountOfMoney.Companion.parse(properties.getProperty("lastcostsyouth",  "0"));
            m_iLastCostsSum = AmountOfMoney.Companion.parse(properties.getProperty("lastcostssum", "0"));
            m_iLastWeeksTotal = AmountOfMoney.Companion.parse(properties.getProperty("lastweekstotal", "0"));
            m_iLastCostsBoughtPlayers = AmountOfMoney.Companion.parse(properties.getProperty("lastcostsboughtplayers","0"));
            m_iLastCostsArenaBuilding = AmountOfMoney.Companion.parse(properties.getProperty("lastcostsarenabuilding","0"));
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
    public static String getNameForLevelFans(int level, HODateTime date) {
    	// previously, fan and sponsor levels where identical, 
    	//   thats why we can simply use sponsor values
    	if (date != null && date.isBefore(DATE_NEW_FANLEVELS)) {
    		return getNameForLevelSponsors(level);
    	}
        return switch (level) {
            case LV_FANS_SENDING_LOVE_POEMS_TO_YOU ->
                    TranslationFacility.tr("ls.club.sponsors_fans.sendinglovepoemstoyou");
            case LV_FANS_DANCING_IN_THE_STREETS -> TranslationFacility.tr("ls.club.sponsors_fans.dancinginthestreets");
            case LV_FANS_HIGH_ON_LIFE -> TranslationFacility.tr("ls.club.sponsors_fans.highonlife");
            case LV_FANS_DELIRIOUS -> TranslationFacility.tr("ls.club.sponsors_fans.delirious");
            case LV_FANS_SATISFIED -> TranslationFacility.tr("ls.club.sponsors_fans.satisfied");
            case LV_FANS_CONTENT -> TranslationFacility.tr("ls.club.sponsors_fans.content");
            case LV_FANS_CALM -> TranslationFacility.tr("ls.club.sponsors_fans.calm");
            case LV_FANS_DISAPPOINTED -> TranslationFacility.tr("ls.club.fans.disappointed");
            case LV_FANS_IRRITATED -> TranslationFacility.tr("ls.club.sponsors_fans.irritated");
            case LV_FANS_ANGRY -> TranslationFacility.tr("ls.club.fans.angry");
            case LV_FANS_FURIOUS -> TranslationFacility.tr("ls.club.sponsors_fans.furious");
            case LV_FANS_MURDEROUS -> TranslationFacility.tr("ls.club.sponsors_fans.murderous");
            default -> {
                if (level > LV_FANS_SENDING_LOVE_POEMS_TO_YOU) {
                    yield TranslationFacility.tr("ls.club.sponsors_fans.sendinglovepoemstoyou");
                }

                yield TranslationFacility.tr("Unbestimmt");
            }
        };
    }

    /**
     * Get the name string for a numerical level.
     *
     * @param level the numerical value (e.g. from CHPP interface)
     * @return the i18n'ed name for the level
     */
    public static String getNameForLevelSponsors(int level) {
        return switch (level) {
            case LV_SPONSORS_SENDING_LOVE_POEMS_TO_YOU ->
                    TranslationFacility.tr("ls.club.sponsors_fans.sendinglovepoemstoyou");
            case LV_SPONSORS_DANCING_IN_THE_STREETS ->
                    TranslationFacility.tr("ls.club.sponsors_fans.dancinginthestreets");
            case LV_SPONSORS_HIGH_ON_LIFE -> TranslationFacility.tr("ls.club.sponsors_fans.highonlife");
            case LV_SPONSORS_DELIRIOUS -> TranslationFacility.tr("ls.club.sponsors_fans.delirious");
            case LV_SPONSORS_SATISFIED -> TranslationFacility.tr("ls.club.sponsors_fans.satisfied");
            case LV_SPONSORS_CONTENT -> TranslationFacility.tr("ls.club.sponsors_fans.content");
            case LV_SPONSORS_CALM -> TranslationFacility.tr("ls.club.sponsors_fans.calm");
            case LV_SPONSORS_IRRITATED -> TranslationFacility.tr("ls.club.sponsors_fans.irritated");
            case LV_SPONSORS_FURIOUS -> TranslationFacility.tr("ls.club.sponsors_fans.furious");
            case LV_SPONSORS_MURDEROUS -> TranslationFacility.tr("ls.club.sponsors_fans.murderous");
            default -> {
                if (level > LV_SPONSORS_SENDING_LOVE_POEMS_TO_YOU) {
                    yield TranslationFacility.tr("ls.club.sponsors_fans.sendinglovepoemstoyou");
                }

                yield TranslationFacility.tr("Unbestimmt");
            }
        };
    }

    public final void setIncomeSum(AmountOfMoney iIncomeSum) {
        this.m_iIncomeSum = iIncomeSum;
    }

    public final AmountOfMoney getIncomeSum() {return m_iIncomeSum;}

    public final void setIncomeTemporary(AmountOfMoney iIncomeTemporary) {
        this.m_iIncomeTemporary = iIncomeTemporary;
    }

    public final AmountOfMoney getIncomeTemporary() {return m_iIncomeTemporary;}

    public final void setIncomeSponsors(AmountOfMoney iIncomeSponsors) {this.m_iIncomeSponsors = iIncomeSponsors; }

    public final void setIncomeSponsorsBonus(AmountOfMoney iIncomeSponsorsBonus) {this.m_iIncomeSponsorsBonus = iIncomeSponsorsBonus; }

    public final AmountOfMoney getIncomeSponsors() {return m_iIncomeSponsors;}

    public final AmountOfMoney getIncomeSponsorsBonus() {return m_iIncomeSponsorsBonus;}

    public final void setIncomeFinancial(AmountOfMoney iIncomeFinancial) {
        this.m_iIncomeFinancial = iIncomeFinancial;
    }

    public final AmountOfMoney getIncomeFinancial() {return m_iIncomeFinancial;}

    public final void setIncomeSpectators(AmountOfMoney iIncomeSpectators) {this.m_iIncomeSpectators = iIncomeSpectators;}

    public final AmountOfMoney getIncomeSpectators() {return m_iIncomeSpectators;}

    public final void setCash(AmountOfMoney m_iCash) {
        this.m_iCash = m_iCash;
    }

    public final AmountOfMoney getCash() {return m_iCash;}

    public final void setExpectedWeeksTotal(AmountOfMoney iExpectedWeeksTotal) {this.m_iExpectedWeeksTotal = iExpectedWeeksTotal;}

    public final AmountOfMoney getExpectedWeeksTotal() {return m_iExpectedWeeksTotal;}

    public final void setCostsSum(AmountOfMoney iCostsSum) {
        this.m_iCostsSum = iCostsSum;
    }

    public final AmountOfMoney getCostsSum() {return m_iCostsSum;}

    public final void setCostsYouth(AmountOfMoney iCostsYouth) {
        this.m_iCostsYouth = iCostsYouth;
    }

    public final AmountOfMoney getCostsYouth() {return m_iCostsYouth;}

    public final void setCostsTemporary(AmountOfMoney iCostsTemporary) {
        this.m_iCostsTemporary = iCostsTemporary;
    }

    public final AmountOfMoney getCostsTemporary() {return m_iCostsTemporary;}

    public final void setCostsPlayers(AmountOfMoney iCostsPlayers) {
        this.m_iCostsPlayers = iCostsPlayers;
    }

    public final AmountOfMoney getCostsPlayers() {return m_iCostsPlayers;}

    public final void setCostsArena(AmountOfMoney iCostsArena) {
        this.m_iCostsArena = iCostsArena;
    }

    public final AmountOfMoney getCostsArena() {return m_iCostsArena;}

    public final void setCostsStaff(AmountOfMoney iCostsStaff) {
        this.m_iCostsStaff = iCostsStaff;
    }

    public final AmountOfMoney getCostsStaff() {return m_iCostsStaff;}

    public final void setCostsFinancial(AmountOfMoney iCostsFinancial) {
        this.m_iCostsFinancial = iCostsFinancial;
    }

    public final AmountOfMoney getCostsFinancial() {return m_iCostsFinancial;}

    public final void setLastIncomeSum(AmountOfMoney iLastIncomeSum) {this.m_iLastIncomeSum = iLastIncomeSum;}

    public final AmountOfMoney getLastIncomeSum() {return m_iLastIncomeSum;}

    public final void setLastIncomeTemporary(AmountOfMoney iLastIncomeTemporary) {this.m_iLastIncomeTemporary = iLastIncomeTemporary;}

    public final AmountOfMoney getLastIncomeTemporary() {return m_iLastIncomeTemporary;}

    public final void setLastIncomeSponsors(AmountOfMoney iLastIncomeSponsors) {this.m_iLastIncomeSponsors = iLastIncomeSponsors;}

    public final void setLastIncomeSponsorsBonus(AmountOfMoney iLastIncomeSponsorsBonus) {this.m_iLastIncomeSponsorsBonus = iLastIncomeSponsorsBonus;}

    public final AmountOfMoney getLastIncomeSponsors() {return m_iLastIncomeSponsors;}

    public final AmountOfMoney getLastIncomeSponsorsBonus() {return m_iLastIncomeSponsorsBonus;}

    public final void setLastIncomeFinancial(AmountOfMoney iLastIncomeFinancial) {this.m_iLastIncomeFinancial = iLastIncomeFinancial;}

    public final AmountOfMoney getLastIncomeFinancial() {return m_iLastIncomeFinancial;}

    public final void setLastIncomeSpectators(AmountOfMoney iLastIncomeSpectators) {this.m_iLastIncomeSpectators = iLastIncomeSpectators;}

    public final AmountOfMoney getLastIncomeSpectators() {return m_iLastIncomeSpectators;}

    public final void setLastWeeksTotal(AmountOfMoney iLastWeeksTotal) {this.m_iLastWeeksTotal = iLastWeeksTotal;}

    public final AmountOfMoney getLastWeeksTotal() {return m_iLastWeeksTotal;}

    public final void setLastCostsSum(AmountOfMoney iLastCostsSum) {
        this.m_iLastCostsSum = iLastCostsSum;
    }

    public final AmountOfMoney getLastCostsSum() {return m_iLastCostsSum;}

    public final void setLastCostsYouth(AmountOfMoney iLastCostsYouth) {this.m_iLastCostsYouth = iLastCostsYouth;}

    public final AmountOfMoney getLastCostsYouth() {return m_iLastCostsYouth;}

    public final void setLastCostsTemporary(AmountOfMoney iLastCostsTemporary) {this.m_iLastCostsTemporary = iLastCostsTemporary;}

    public final AmountOfMoney getLastCostsTemporary() {return m_iLastCostsTemporary;}

    public final void setLastCostsPlayers(AmountOfMoney iLastCostsPlayers) {this.m_iLastCostsPlayers = iLastCostsPlayers;}

    public final AmountOfMoney getLastCostsPlayers() {return m_iLastCostsPlayers;}

    public final void setLastCostsArena(AmountOfMoney iLastCostsArena) {this.m_iLastCostsArena = iLastCostsArena;}

    public final AmountOfMoney getLastCostsArena() {return m_iLastCostsArena;}

    public final void setLastCostsStaff(AmountOfMoney iLastCostsStaff) {this.m_iLastCostsStaff = iLastCostsStaff;}

    public final AmountOfMoney getLastCostsStaff() {return m_iLastCostsStaff;}

    public final void setLastCostsFinancial(AmountOfMoney iLastCostsFinancial) {this.m_iLastCostsFinancial = iLastCostsFinancial;}

    public final AmountOfMoney getLastCostsFinancial() {return m_iLastCostsFinancial;}

    public final void setSponsorsPopularity(int iSponsorsPopularity) {this.m_iSponsorsPopularity = iSponsorsPopularity;}

    public final int getSponsorsPopularity() {
        return m_iSponsorsPopularity;
    }

    public final void setSupPopularity(int iSupportersPopularity) {this.m_iSupportersPopularity = iSupportersPopularity;}

    public final int getSupportersPopularity() {
        return m_iSupportersPopularity;
    }

    public final void setExpectedCash(AmountOfMoney iExpectedCash) {
        this.m_iExpectedCash = iExpectedCash;
    }

    public final AmountOfMoney getExpectedCash() {
        return m_iExpectedCash;
    }

    public final void setIncomeSoldPlayers(AmountOfMoney iIncomeSoldPlayers) {this.m_iIncomeSoldPlayers = iIncomeSoldPlayers;}

    public final AmountOfMoney getIncomeSoldPlayers() {
        return m_iIncomeSoldPlayers;
    }

    public final void setIncomeSoldPlayersCommission(AmountOfMoney iIncomeSoldPlayersCommission) {this.m_iIncomeSoldPlayersCommission = iIncomeSoldPlayersCommission;}

    public final AmountOfMoney getIncomeSoldPlayersCommission() {
        return m_iIncomeSoldPlayersCommission;
    }

    public final void setCostsBoughtPlayers(AmountOfMoney iCostsBoughtPlayers) {this.m_iCostsBoughtPlayers = iCostsBoughtPlayers;}

    public final AmountOfMoney getCostsBoughtPlayers() {
        return m_iCostsBoughtPlayers;
    }

    public final void setCostsArenaBuilding(AmountOfMoney iCostsArenaBuilding) {this.m_iCostsArenaBuilding = iCostsArenaBuilding;}

    public final AmountOfMoney getCostsArenaBuilding() {
        return m_iCostsArenaBuilding;
    }

    public final void setLastIncomeSoldPlayers(AmountOfMoney iLastIncomeSoldPlayers) {this.m_iLastIncomeSoldPlayers = iLastIncomeSoldPlayers;}

    public final AmountOfMoney getLastIncomeSoldPlayers() {return m_iLastIncomeSoldPlayers;}

    public final void setLastIncomeSoldPlayersCommission(AmountOfMoney iLastIncomeSoldPlayersCommission) {
        this.m_iLastIncomeSoldPlayersCommission = iLastIncomeSoldPlayersCommission;
    }

    public final AmountOfMoney getLastIncomeSoldPlayersCommission() {
        return m_iLastIncomeSoldPlayersCommission;
    }

    public final void setLastCostsBoughtPlayers(AmountOfMoney iLastCostsBoughtPlayers) {
        this.m_iLastCostsBoughtPlayers = iLastCostsBoughtPlayers;
    }

    public final AmountOfMoney getLastCostsBoughtPlayers() {
        return m_iLastCostsBoughtPlayers;
    }

    public final void setLastCostsArenaBuilding(AmountOfMoney iLastCostsArenaBuilding) {
        this.m_iLastCostsArenaBuilding = iLastCostsArenaBuilding;
    }

    public final AmountOfMoney getLastCostsArenaBuilding() {
        return m_iLastCostsArenaBuilding;
    }

    public int getHrfId() {
        return this.hrfId;
    }

    public void setHrfId(int id){
        this.hrfId=id;
    }

    public HODateTime getFetchedDate() {
        return this.fetchedDate;
    }

    public void setFetchedDate(HODateTime fetchedDate){
        this.fetchedDate = fetchedDate;
    }
}
