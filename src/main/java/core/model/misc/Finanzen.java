// %1497023504:de.hattrickorganizer.model%
package core.model.misc;

import core.util.HOLogger;
import java.sql.Timestamp;

/**
 * Enthält alle Informationen zu den Finanzen
 */
public class Finanzen  {
    //~ Static fields/initializers -----------------------------------------------------------------
	/*
	 * level constants for the fan mood (SupportersPopularity)
	 * 11 'Sending love poems to you
	 * 10 'dancing in the streets
	 * 9 'high on life
	 * 8 'delirious
	 * 7 'satisfied
	 * 6 'content
	 * 5 'calm
	 * 4 'disappointed
	 * 3 'irritated
	 * 2 'angry
	 * 1 'furious
	 * 0 'murderous
	 */
    public static final int LV_fans_vergoettern_Dich = 11;
    public static final int LV_fans_im_siebten_Himmel = 10;
    public static final int LV_fans_euphorisch = 9;
    public static final int LV_fans_uebergluecklich = 8;
    public static final int LV_fans_gluecklich = 7;
    public static final int LV_fans_zufrieden = 6;
    public static final int LV_fans_ruhig = 5;
    public static final int LV_fans_disappointed = 4; 
    public static final int LV_fans_irritiert = 3;
    public static final int LV_fans_angry = 2;
    public static final int LV_fans_wuetend = 1;
    public static final int LV_fans_blutduerstig = 0;
    
    private static Timestamp DATE_NEW_FANLEVELS = new Timestamp(1203897600000L); // 25.02.2008

    // level constants for the sponsor mood
    public static final int LV_spons_vergoettern_Dich = 9;
    public static final int LV_spons_im_siebten_Himmel = 8;
    public static final int LV_spons_euphorisch = 7;
    public static final int LV_spons_uebergluecklich = 6;
    public static final int LV_spons_gluecklich = 5;
    public static final int LV_spons_zufrieden = 4;
    public static final int LV_spons_ruhig = 3;
    public static final int LV_spons_irritiert = 2;
    public static final int LV_spons_wuetend = 1;
    public static final int LV_spons_blutduerstig = 0;

    
    //~ Instance fields ----------------------------------------------------------------------------

    /** Einnahmen Gesamt */
    protected int m_iEinnahmenGesamt;

    /** Einnahmen Sonstige */
    protected int m_iEinnahmenSonstige;

    /** Einnahmen Sponsoren */
    protected int m_iEinnahmenSponsoren;

    /** Einnahmen Zinserträge */
    protected int m_iEinnahmenZinsen;

    /** Einnahmen Zuschauer */
    protected int m_iEinnahmenZuschauer;

    /** Aktuelle Finanzen */
    protected int m_iFinanzen;

    /** Gewinn/Verlust */
    protected int m_iGewinnVerlust;

    /** Kosten Gesamt */
    protected int m_iKostenGesamt;

    /** Kosten Jugend */
    protected int m_iKostenJugend;

    /** Kosten Sonstige */
    protected int m_iKostenSonstige;

    /** Kosten Spieler */
    protected int m_iKostenSpieler;

    /** Kosten Stadion */
    protected int m_iKostenStadion;

    /** Kosten Trainerstab */
    protected int m_iKostenTrainerstab;

    /** Kosten Zinsaufwendungen */
    protected int m_iKostenZinsen;

    /** Einnahmen Gesamt */
    protected int m_iLetzteEinnahmenGesamt;

    /** Einnahmen Sonstige */
    protected int m_iLetzteEinnahmenSonstige;

    /** Einnahmen Sponsoren */
    protected int m_iLetzteEinnahmenSponsoren;

    /** Einnahmen Zinserträge */
    protected int m_iLetzteEinnahmenZinsen;

    /** Einnahmen Zuschauer */
    protected int m_iLetzteEinnahmenZuschauer;

    /** Gewinn/Verlust */
    protected int m_iLetzteGewinnVerlust;

    /** Kosten Gesamt */
    protected int m_iLetzteKostenGesamt;

    /** Kosten Jugend */
    protected int m_iLetzteKostenJugend;

    /** Kosten Sonstige */
    protected int m_iLetzteKostenSonstige;

    /** Kosten Spieler */
    protected int m_iLetzteKostenSpieler;

    /** Kosten Stadion */
    protected int m_iLetzteKostenStadion;

    /** Kosten Trainerstab */
    protected int m_iLetzteKostenTrainerstab;

    /** Kosten Zinsaufwendungen */
    protected int m_iLetzteKostenZinsen;

    /** Sponsoren */
    protected int m_iSponsoren;

    ////////////////////////////////////////////////////////////////////////////////
    //Member
    ////////////////////////////////////////////////////////////////////////////////    

    /** Supporter */
    protected int m_iSupporter;

    //~ Constructors -------------------------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////////////////
    //Konstruktor
    ////////////////////////////////////////////////////////////////////////////////    
    public Finanzen(java.util.Properties properties) {
        m_iSupporter = Integer.parseInt(properties.getProperty("supporters", "0"));
        m_iSponsoren = Integer.parseInt(properties.getProperty("sponsors", "0"));
        m_iFinanzen = Integer.parseInt(properties.getProperty("cash", "0"));

        m_iEinnahmenSponsoren = Integer.parseInt(properties.getProperty("incomesponsorer", "0"));
        m_iEinnahmenZuschauer = Integer.parseInt(properties.getProperty("incomepublik", "0"));
        m_iEinnahmenZinsen = Integer.parseInt(properties.getProperty("incomefinansiella", "0"));
        m_iEinnahmenSonstige = Integer.parseInt(properties.getProperty("incometillfalliga", "0"));
        m_iEinnahmenGesamt = Integer.parseInt(properties.getProperty("incomesumma", "0"));
        m_iKostenSpieler = Integer.parseInt(properties.getProperty("costsspelare", "0"));
        m_iKostenTrainerstab = Integer.parseInt(properties.getProperty("costspersonal", "0"));
        m_iKostenStadion = Integer.parseInt(properties.getProperty("costsarena", "0"));
        m_iKostenJugend = Integer.parseInt(properties.getProperty("costsjuniorverksamhet", "0"));
        m_iKostenZinsen = Integer.parseInt(properties.getProperty("costsrantor", "0"));
        m_iKostenSonstige = Integer.parseInt(properties.getProperty("coststillfalliga", "0"));
        m_iKostenGesamt = Integer.parseInt(properties.getProperty("costssumma", "0"));
        m_iGewinnVerlust = Integer.parseInt(properties.getProperty("total", "0"));

        m_iLetzteEinnahmenSponsoren = Integer.parseInt(properties.getProperty("lastincomesponsorer",
                                                                              "0"));
        m_iLetzteEinnahmenZuschauer = Integer.parseInt(properties.getProperty("lastincomepublik",
                                                                              "0"));
        m_iLetzteEinnahmenZinsen = Integer.parseInt(properties.getProperty("lastincomefinansiella",
                                                                           "0"));
        m_iLetzteEinnahmenSonstige = Integer.parseInt(properties.getProperty("lastincometillfalliga",
                                                                             "0"));
        m_iLetzteEinnahmenGesamt = Integer.parseInt(properties.getProperty("lastincomesumma", "0"));
        m_iLetzteKostenSpieler = Integer.parseInt(properties.getProperty("lastcostsspelare", "0"));
        m_iLetzteKostenTrainerstab = Integer.parseInt(properties.getProperty("lastcostspersonal",
                                                                             "0"));
        m_iLetzteKostenStadion = Integer.parseInt(properties.getProperty("lastcostsarena", "0"));
        m_iLetzteKostenJugend = Integer.parseInt(properties.getProperty("lastcostsjuniorverksamhet",
                                                                        "0"));
        m_iLetzteKostenZinsen = Integer.parseInt(properties.getProperty("lastcostsrantor", "0"));
        m_iLetzteKostenSonstige = Integer.parseInt(properties.getProperty("lastcoststillfalliga",
                                                                          "0"));
        m_iLetzteKostenGesamt = Integer.parseInt(properties.getProperty("lastcostssumma", "0"));
        m_iLetzteGewinnVerlust = Integer.parseInt(properties.getProperty("lasttotal", "0"));
    }

    /**
     * Creates a new Finanzen object.
     *
     * @param rs ResultSet with the data from the DB query
     */
    public Finanzen(java.sql.ResultSet rs) {
        try {
            m_iSupporter = rs.getInt("Supporter");
            m_iSponsoren = rs.getInt("Sponsoren");
            m_iFinanzen = rs.getInt("Finanzen");

            m_iEinnahmenSponsoren = rs.getInt("EinSponsoren");
            m_iEinnahmenZuschauer = rs.getInt("EinZuschauer");
            m_iEinnahmenZinsen = rs.getInt("EinZinsen");
            m_iEinnahmenSonstige = rs.getInt("EinSonstiges");
            m_iEinnahmenGesamt = rs.getInt("EinGesamt");
            m_iKostenSpieler = rs.getInt("KostSpieler");
            m_iKostenTrainerstab = rs.getInt("KostTrainer");
            m_iKostenStadion = rs.getInt("KostStadion");
            m_iKostenJugend = rs.getInt("KostJugend");
            m_iKostenZinsen = rs.getInt("KostZinsen");
            m_iKostenSonstige = rs.getInt("KostSonstiges");
            m_iKostenGesamt = rs.getInt("KostGesamt");
            m_iGewinnVerlust = rs.getInt("GewinnVerlust");

            m_iLetzteEinnahmenSponsoren = rs.getInt("LetzteEinSponsoren");
            m_iLetzteEinnahmenZuschauer = rs.getInt("LetzteEinZuschauer");
            m_iLetzteEinnahmenZinsen = rs.getInt("LetzteEinZinsen");
            m_iLetzteEinnahmenSonstige = rs.getInt("LetzteEinSonstiges");
            m_iLetzteEinnahmenGesamt = rs.getInt("LetzteEinGesamt");
            m_iLetzteKostenSpieler = rs.getInt("LetzteKostSpieler");
            m_iLetzteKostenTrainerstab = rs.getInt("LetzteKostTrainer");
            m_iLetzteKostenStadion = rs.getInt("LetzteKostStadion");
            m_iLetzteKostenJugend = rs.getInt("LetzteKostJugend");
            m_iLetzteKostenZinsen = rs.getInt("LetzteKostZinsen");
            m_iLetzteKostenSonstige = rs.getInt("LetzteKostSonstiges");
            m_iLetzteKostenGesamt = rs.getInt("LetzteKostGesamt");
            m_iLetzteGewinnVerlust = rs.getInt("LetzteGewinnVerlust");
        } catch (Exception e) {
        	HOLogger.instance().debug(Finanzen.class, e);
        }
    }

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
            case LV_fans_vergoettern_Dich:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.sendinglovepoemstoyou");

            case LV_fans_im_siebten_Himmel:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.dancinginthestreets");

            case LV_fans_euphorisch:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.highonlife");

            case LV_fans_uebergluecklich:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.delirious");

            case LV_fans_gluecklich:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.satisfied");

            case LV_fans_zufrieden:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.content");

            case LV_fans_ruhig:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.calm");

            case LV_fans_disappointed:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.fans.disappointed");
                
            case LV_fans_irritiert:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.irritated");
                
            case LV_fans_angry:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.fans.angry");

            case LV_fans_wuetend:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.furious");

            case LV_fans_blutduerstig:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.murderous");

            default: {
                if (level > LV_fans_vergoettern_Dich) {
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
            case LV_spons_vergoettern_Dich:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.sendinglovepoemstoyou");

            case LV_spons_im_siebten_Himmel:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.dancinginthestreets");

            case LV_spons_euphorisch:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.highonlife");

            case LV_spons_uebergluecklich:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.delirious");

            case LV_spons_gluecklich:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.satisfied");

            case LV_spons_zufrieden:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.content");

            case LV_spons_ruhig:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.calm");

            case LV_spons_irritiert:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.irritated");
                
            case LV_spons_wuetend:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.furious");

            case LV_spons_blutduerstig:
                return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.murderous");

            default: {
                if (level > LV_spons_vergoettern_Dich) {
                    return core.model.HOVerwaltung.instance().getLanguageString("ls.club.sponsors_fans.sendinglovepoemstoyou");
                }

                return core.model.HOVerwaltung.instance().getLanguageString("Unbestimmt");
            }
        }
    }
    
    /**
     * Setter for property m_iEinnahmenGesamt.
     *
     * @param m_iEinnahmenGesamt New value of property m_iEinnahmenGesamt.
     */
    public final void setEinnahmenGesamt(int m_iEinnahmenGesamt) {
        this.m_iEinnahmenGesamt = m_iEinnahmenGesamt;
    }

    /**
     * Getter for property m_iEinnahmenGesamt.
     *
     * @return Value of property m_iEinnahmenGesamt.
     */
    public final int getEinnahmenGesamt() {
        return m_iEinnahmenGesamt;
    }

    /**
     * Setter for property m_iEinnahmenSonstige.
     *
     * @param m_iEinnahmenSonstige New value of property m_iEinnahmenSonstige.
     */
    public final void setEinnahmenSonstige(int m_iEinnahmenSonstige) {
        this.m_iEinnahmenSonstige = m_iEinnahmenSonstige;
    }

    /**
     * Getter for property m_iEinnahmenSonstige.
     *
     * @return Value of property m_iEinnahmenSonstige.
     */
    public final int getEinnahmenSonstige() {
        return m_iEinnahmenSonstige;
    }

    /**
     * Setter for property m_iEinnahmenSponsoren.
     *
     * @param m_iEinnahmenSponsoren New value of property m_iEinnahmenSponsoren.
     */
    public final void setEinnahmenSponsoren(int m_iEinnahmenSponsoren) {
        this.m_iEinnahmenSponsoren = m_iEinnahmenSponsoren;
    }

    /**
     * Getter for property m_iEinnahmenSponsoren.
     *
     * @return Value of property m_iEinnahmenSponsoren.
     */
    public final int getEinnahmenSponsoren() {
        return m_iEinnahmenSponsoren;
    }

    /**
     * Setter for property m_iEinnahmenZinsen.
     *
     * @param m_iEinnahmenZinsen New value of property m_iEinnahmenZinsen.
     */
    public final void setEinnahmenZinsen(int m_iEinnahmenZinsen) {
        this.m_iEinnahmenZinsen = m_iEinnahmenZinsen;
    }

    /**
     * Getter for property m_iEinnahmenZinsen.
     *
     * @return Value of property m_iEinnahmenZinsen.
     */
    public final int getEinnahmenZinsen() {
        return m_iEinnahmenZinsen;
    }

    /**
     * Setter for property m_iEinnahmenZuschauer.
     *
     * @param m_iEinnahmenZuschauer New value of property m_iEinnahmenZuschauer.
     */
    public final void setEinnahmenZuschauer(int m_iEinnahmenZuschauer) {
        this.m_iEinnahmenZuschauer = m_iEinnahmenZuschauer;
    }

    /**
     * Getter for property m_iEinnahmenZuschauer.
     *
     * @return Value of property m_iEinnahmenZuschauer.
     */
    public final int getEinnahmenZuschauer() {
        return m_iEinnahmenZuschauer;
    }

    /**
     * Setter for property m_iFinanzen.
     *
     * @param m_iFinanzen New value of property m_iFinanzen.
     */
    public final void setFinanzen(int m_iFinanzen) {
        this.m_iFinanzen = m_iFinanzen;
    }

    /**
     * Getter for property m_iFinanzen.
     *
     * @return Value of property m_iFinanzen.
     */
    public final int getFinanzen() {
        return m_iFinanzen;
    }

    /**
     * Setter for property m_iGewinnVerlust.
     *
     * @param m_iGewinnVerlust New value of property m_iGewinnVerlust.
     */
    public final void setGewinnVerlust(int m_iGewinnVerlust) {
        this.m_iGewinnVerlust = m_iGewinnVerlust;
    }

    /**
     * Getter for property m_iGewinnVerlust.
     *
     * @return Value of property m_iGewinnVerlust.
     */
    public final int getGewinnVerlust() {
        return m_iGewinnVerlust;
    }

    /**
     * Setter for property m_iKostenGesamt.
     *
     * @param m_iKostenGesamt New value of property m_iKostenGesamt.
     */
    public final void setKostenGesamt(int m_iKostenGesamt) {
        this.m_iKostenGesamt = m_iKostenGesamt;
    }

    /**
     * Getter for property m_iKostenGesamt.
     *
     * @return Value of property m_iKostenGesamt.
     */
    public final int getKostenGesamt() {
        return m_iKostenGesamt;
    }

    /**
     * Setter for property m_iKostenJugend.
     *
     * @param m_iKostenJugend New value of property m_iKostenJugend.
     */
    public final void setKostenJugend(int m_iKostenJugend) {
        this.m_iKostenJugend = m_iKostenJugend;
    }

    /**
     * Getter for property m_iKostenJugend.
     *
     * @return Value of property m_iKostenJugend.
     */
    public final int getKostenJugend() {
        return m_iKostenJugend;
    }

    /**
     * Setter for property m_iKostenSonstige.
     *
     * @param m_iKostenSonstige New value of property m_iKostenSonstige.
     */
    public final void setKostenSonstige(int m_iKostenSonstige) {
        this.m_iKostenSonstige = m_iKostenSonstige;
    }

    /**
     * Getter for property m_iKostenSonstige.
     *
     * @return Value of property m_iKostenSonstige.
     */
    public final int getKostenSonstige() {
        return m_iKostenSonstige;
    }

    /**
     * Setter for property m_iKostenSpieler.
     *
     * @param m_iKostenSpieler New value of property m_iKostenSpieler.
     */
    public final void setKostenSpieler(int m_iKostenSpieler) {
        this.m_iKostenSpieler = m_iKostenSpieler;
    }

    /**
     * Getter for property m_iKostenSpieler.
     *
     * @return Value of property m_iKostenSpieler.
     */
    public final int getKostenSpieler() {
        return m_iKostenSpieler;
    }

    /**
     * Setter for property m_iKostenStadion.
     *
     * @param m_iKostenStadion New value of property m_iKostenStadion.
     */
    public final void setKostenStadion(int m_iKostenStadion) {
        this.m_iKostenStadion = m_iKostenStadion;
    }

    /**
     * Getter for property m_iKostenStadion.
     *
     * @return Value of property m_iKostenStadion.
     */
    public final int getKostenStadion() {
        return m_iKostenStadion;
    }

    /**
     * Setter for property m_iKostenTrainerstab.
     *
     * @param m_iKostenTrainerstab New value of property m_iKostenTrainerstab.
     */
    public final void setKostenTrainerstab(int m_iKostenTrainerstab) {
        this.m_iKostenTrainerstab = m_iKostenTrainerstab;
    }

    /**
     * Getter for property m_iKostenTrainerstab.
     *
     * @return Value of property m_iKostenTrainerstab.
     */
    public final int getKostenTrainerstab() {
        return m_iKostenTrainerstab;
    }

    /**
     * Setter for property m_iKostenZinsen.
     *
     * @param m_iKostenZinsen New value of property m_iKostenZinsen.
     */
    public final void setKostenZinsen(int m_iKostenZinsen) {
        this.m_iKostenZinsen = m_iKostenZinsen;
    }

    /**
     * Getter for property m_iKostenZinsen.
     *
     * @return Value of property m_iKostenZinsen.
     */
    public final int getKostenZinsen() {
        return m_iKostenZinsen;
    }

    /**
     * Setter for property m_iLetzteEinnahmenGesamt.
     *
     * @param m_iLetzteEinnahmenGesamt New value of property m_iLetzteEinnahmenGesamt.
     */
    public final void setLetzteEinnahmenGesamt(int m_iLetzteEinnahmenGesamt) {
        this.m_iLetzteEinnahmenGesamt = m_iLetzteEinnahmenGesamt;
    }

    /**
     * Getter for property m_iLetzteEinnahmenGesamt.
     *
     * @return Value of property m_iLetzteEinnahmenGesamt.
     */
    public final int getLetzteEinnahmenGesamt() {
        return m_iLetzteEinnahmenGesamt;
    }

    /**
     * Setter for property m_iLetzteEinnahmenSonstige.
     *
     * @param m_iLetzteEinnahmenSonstige New value of property m_iLetzteEinnahmenSonstige.
     */
    public final void setLetzteEinnahmenSonstige(int m_iLetzteEinnahmenSonstige) {
        this.m_iLetzteEinnahmenSonstige = m_iLetzteEinnahmenSonstige;
    }

    /**
     * Getter for property m_iLetzteEinnahmenSonstige.
     *
     * @return Value of property m_iLetzteEinnahmenSonstige.
     */
    public final int getLetzteEinnahmenSonstige() {
        return m_iLetzteEinnahmenSonstige;
    }

    /**
     * Setter for property m_iLetzteEinnahmenSponsoren.
     *
     * @param m_iLetzteEinnahmenSponsoren New value of property m_iLetzteEinnahmenSponsoren.
     */
    public final void setLetzteEinnahmenSponsoren(int m_iLetzteEinnahmenSponsoren) {
        this.m_iLetzteEinnahmenSponsoren = m_iLetzteEinnahmenSponsoren;
    }

    /**
     * Getter for property m_iLetzteEinnahmenSponsoren.
     *
     * @return Value of property m_iLetzteEinnahmenSponsoren.
     */
    public final int getLetzteEinnahmenSponsoren() {
        return m_iLetzteEinnahmenSponsoren;
    }

    /**
     * Setter for property m_iLetzteEinnahmenZinsen.
     *
     * @param m_iLetzteEinnahmenZinsen New value of property m_iLetzteEinnahmenZinsen.
     */
    public final void setLetzteEinnahmenZinsen(int m_iLetzteEinnahmenZinsen) {
        this.m_iLetzteEinnahmenZinsen = m_iLetzteEinnahmenZinsen;
    }

    /**
     * Getter for property m_iLetzteEinnahmenZinsen.
     *
     * @return Value of property m_iLetzteEinnahmenZinsen.
     */
    public final int getLetzteEinnahmenZinsen() {
        return m_iLetzteEinnahmenZinsen;
    }

    /**
     * Setter for property m_iLetzteEinnahmenZuschauer.
     *
     * @param m_iLetzteEinnahmenZuschauer New value of property m_iLetzteEinnahmenZuschauer.
     */
    public final void setLetzteEinnahmenZuschauer(int m_iLetzteEinnahmenZuschauer) {
        this.m_iLetzteEinnahmenZuschauer = m_iLetzteEinnahmenZuschauer;
    }

    /**
     * Getter for property m_iLetzteEinnahmenZuschauer.
     *
     * @return Value of property m_iLetzteEinnahmenZuschauer.
     */
    public final int getLetzteEinnahmenZuschauer() {
        return m_iLetzteEinnahmenZuschauer;
    }

    /**
     * Setter for property m_iLetzteGewinnVerlust.
     *
     * @param m_iLetzteGewinnVerlust New value of property m_iLetzteGewinnVerlust.
     */
    public final void setLetzteGewinnVerlust(int m_iLetzteGewinnVerlust) {
        this.m_iLetzteGewinnVerlust = m_iLetzteGewinnVerlust;
    }

    /**
     * Getter for property m_iLetzteGewinnVerlust.
     *
     * @return Value of property m_iLetzteGewinnVerlust.
     */
    public final int getLetzteGewinnVerlust() {
        return m_iLetzteGewinnVerlust;
    }

    /**
     * Setter for property m_iLetzteKostenGesamt.
     *
     * @param m_iLetzteKostenGesamt New value of property m_iLetzteKostenGesamt.
     */
    public final void setLetzteKostenGesamt(int m_iLetzteKostenGesamt) {
        this.m_iLetzteKostenGesamt = m_iLetzteKostenGesamt;
    }

    /**
     * Getter for property m_iLetzteKostenGesamt.
     *
     * @return Value of property m_iLetzteKostenGesamt.
     */
    public final int getLetzteKostenGesamt() {
        return m_iLetzteKostenGesamt;
    }

    /**
     * Setter for property m_iLetzteKostenJugend.
     *
     * @param m_iLetzteKostenJugend New value of property m_iLetzteKostenJugend.
     */
    public final void setLetzteKostenJugend(int m_iLetzteKostenJugend) {
        this.m_iLetzteKostenJugend = m_iLetzteKostenJugend;
    }

    /**
     * Getter for property m_iLetzteKostenJugend.
     *
     * @return Value of property m_iLetzteKostenJugend.
     */
    public final int getLetzteKostenJugend() {
        return m_iLetzteKostenJugend;
    }

    /**
     * Setter for property m_iLetzteKostenSonstige.
     *
     * @param m_iLetzteKostenSonstige New value of property m_iLetzteKostenSonstige.
     */
    public final void setLetzteKostenSonstige(int m_iLetzteKostenSonstige) {
        this.m_iLetzteKostenSonstige = m_iLetzteKostenSonstige;
    }

    /**
     * Getter for property m_iLetzteKostenSonstige.
     *
     * @return Value of property m_iLetzteKostenSonstige.
     */
    public final int getLetzteKostenSonstige() {
        return m_iLetzteKostenSonstige;
    }

    /**
     * Setter for property m_iLetzteKostenSpieler.
     *
     * @param m_iLetzteKostenSpieler New value of property m_iLetzteKostenSpieler.
     */
    public final void setLetzteKostenSpieler(int m_iLetzteKostenSpieler) {
        this.m_iLetzteKostenSpieler = m_iLetzteKostenSpieler;
    }

    /**
     * Getter for property m_iLetzteKostenSpieler.
     *
     * @return Value of property m_iLetzteKostenSpieler.
     */
    public final int getLetzteKostenSpieler() {
        return m_iLetzteKostenSpieler;
    }

    /**
     * Setter for property m_iLetzteKostenStadion.
     *
     * @param m_iLetzteKostenStadion New value of property m_iLetzteKostenStadion.
     */
    public final void setLetzteKostenStadion(int m_iLetzteKostenStadion) {
        this.m_iLetzteKostenStadion = m_iLetzteKostenStadion;
    }

    /**
     * Getter for property m_iLetzteKostenStadion.
     *
     * @return Value of property m_iLetzteKostenStadion.
     */
    public final int getLetzteKostenStadion() {
        return m_iLetzteKostenStadion;
    }

    /**
     * Setter for property m_iLetzteKostenTrainerstab.
     *
     * @param m_iLetzteKostenTrainerstab New value of property m_iLetzteKostenTrainerstab.
     */
    public final void setLetzteKostenTrainerstab(int m_iLetzteKostenTrainerstab) {
        this.m_iLetzteKostenTrainerstab = m_iLetzteKostenTrainerstab;
    }

    /**
     * Getter for property m_iLetzteKostenTrainerstab.
     *
     * @return Value of property m_iLetzteKostenTrainerstab.
     */
    public final int getLetzteKostenTrainerstab() {
        return m_iLetzteKostenTrainerstab;
    }

    /**
     * Setter for property m_iLetzteKostenZinsen.
     *
     * @param m_iLetzteKostenZinsen New value of property m_iLetzteKostenZinsen.
     */
    public final void setLetzteKostenZinsen(int m_iLetzteKostenZinsen) {
        this.m_iLetzteKostenZinsen = m_iLetzteKostenZinsen;
    }

    /**
     * Getter for property m_iLetzteKostenZinsen.
     *
     * @return Value of property m_iLetzteKostenZinsen.
     */
    public final int getLetzteKostenZinsen() {
        return m_iLetzteKostenZinsen;
    }

    /**
     * Setter for property m_iSponsoren.
     *
     * @param m_iSponsoren New value of property m_iSponsoren.
     */
    public final void setSponsoren(int m_iSponsoren) {
        this.m_iSponsoren = m_iSponsoren;
    }

    /**
     * Getter for property m_iSponsoren.
     *
     * @return Value of property m_iSponsoren.
     */
    public final int getSponsoren() {
        return m_iSponsoren;
    }

    /**
     * Setter for property m_iSupporter.
     *
     * @param m_iSupporter New value of property m_iSupporter.
     */
    public final void setSupporter(int m_iSupporter) {
        this.m_iSupporter = m_iSupporter;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //Accessor
    ////////////////////////////////////////////////////////////////////////////////     

    /**
     * Getter for property m_iSupporter.
     *
     * @return Value of property m_iSupporter.
     */
    public final int getSupporter() {
        return m_iSupporter;
    }
}
