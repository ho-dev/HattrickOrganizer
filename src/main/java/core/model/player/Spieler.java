package core.model.player;

import core.constants.player.PlayerSkill;
import core.constants.player.PlayerSpeciality;
import core.constants.player.Speciality;
import core.db.DBManager;
//import core.epv.EPVData;
import core.model.FactorObject;
import core.model.FormulaFactors;
import core.model.HOVerwaltung;
import core.model.StaffMember;
import core.model.match.Weather;
import core.rating.RatingPredictionManager;
import core.training.SkillDrops;
import core.training.TrainingManager;
import core.training.TrainingPerPlayer;
import core.training.TrainingPerWeek;
import core.training.TrainingPoints;
import core.training.WeeklyTrainingType;
import core.util.Helper;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class Spieler {
    //~ Class fields -------------------------------------------------------------------------------

	/** Cache for star ratings (Hashtable<String, Float>) */
    private static Hashtable<String,Object> starRatingCache = new Hashtable<String,Object>();
	// constants for lineup HT-ML export
    private static final String BREAK = "[br]";
	private static final String O_BRACKET = "[";
	private static final String C_BRACKET = "]";
	private static final String EMPTY = "";

    //~ Instance fields ----------------------------------------------------------------------------

    /** Spielberechtigt */
    private Boolean m_bSpielberechtigt;

    /** ManuellerSmilie Dateiname */
    private String m_sManuellerSmilie;

    /** Name */
    private String m_sName = "";

    /** TeamInfoSmilie Dateiname */
    private String m_sTeamInfoSmilie;
    private java.sql.Timestamp m_clhrfDate;

    /** Datum des ersten HRFs mit dem Spieler */
    private Timestamp m_tsTime4FirstHRF;

    /** Der Spieler ist nicht mehr im aktuellen HRF vorhanden */
    private boolean m_bOld;
    private byte m_bUserPosFlag = -2;

    /** Fluegelspiel */
    private double m_dSubFluegelspiel;

    /** Passpiel */
    private double m_dSubPasspiel;

    /** Spielaufbau */
    private double m_dSubSpielaufbau;

    /** Standards */
    private double m_dSubStandards;

    /** Torschuss */
    private double m_dSubTorschuss;

    //Subskills
    private double m_dSubTorwart;

    /** Verteidigung */
    private double m_dSubVerteidigung;

    /** Agressivität */
    private int m_iAgressivitaet;

    /** Alter */
    private int m_iAlter;

    /** Age Days */
    private int m_iAgeDays;

    /** Ansehen (ekel usw. ) */
    private int m_iAnsehen = 1;

    /** Bewertung */
    private int m_iBewertung;

    /** charakter ( ehrlich) */
    private int m_iCharakter = 1;

    /** Erfahrung */
    private int m_iErfahrung = 1;

    /** Fluegelspiel */
    private int m_iFluegelspiel = 1;

    /** Form */
    private int m_iForm = 1;

    /** Führungsqualität */
    private int m_iFuehrung = 1;

    /** Gehalt */
    private int m_iGehalt = 1;

    /** Gelbe Karten */
    private int m_iGelbeKarten;

    /** Hattricks */
    private int m_iHattrick;

    /** Home Grown */
    private boolean m_bHomeGrown = false;

    /** Kondition */
    private int m_iKondition = 1;

    /** Länderspiele */
    private int m_iLaenderspiele;

    //Cache

    /** Letzte Bewertung */
    private int m_iLastBewertung = -1;

    /** Loyalty */
    private int m_iLoyalty = 0;

    /** Markwert */
    private int m_iTSI;

    /** bonus in Prozent */

    //protected int       m_iBonus            =   0;

    /** Aus welchem Land kommt der Spieler */
    private int m_iNationalitaet = 49;

    /** Passpiel */
    private int m_iPasspiel = 1;

    /** SpezialitätID */
    private int m_iSpezialitaet;

    /** Spielaufbau */
    private int m_iSpielaufbau = 1;

    ////////////////////////////////////////////////////////////////////////////////
    //Member
    ////////////////////////////////////////////////////////////////////////////////

    /** SpielerID */
    private int m_iSpielerID;

    /** Standards */
    private int m_iStandards = 1;

    /** Tore Freundschaftspiel */
    private int m_iToreFreund;

    /** Tore Gesamt */
    private int m_iToreGesamt;

    /** Tore Liga */
    private int m_iToreLiga;

    /** Tore Pokalspiel */
    private int m_iTorePokal;

    /** Torschuss */
    private int m_iTorschuss = 1;

    /** Torwart */
    private int m_iTorwart = 1;

    /** Trainerfähigkeit */
    private int m_iTrainer;

    /** Trainertyp */
    private int m_iTrainerTyp = -1;

    /** Transferlisted */
    private int m_iTransferlisted;

    //TODO Noch in DB adden

    /** Fetchdate */

    //    protected Timestamp m_clFetchDate       =   new Timestamp( System.currentTimeMillis () );

    /*TrikotNummer*/
    private int m_iTrikotnummer = -1;

    /** Länderspiele */
    private int m_iU20Laenderspiele;

    /** Verletzt Wochen */
    private int m_iVerletzt = -1;

    /** Verteidigung */
    private int m_iVerteidigung = 1;

    /** Training block */
    private boolean m_bTrainingBlock = false;

    //~ Constructors -------------------------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////////////////
    //Konstruktor
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new instance of Spieler
     */
    public Spieler() {
    }

    /**
     * Erstellt einen Spieler aus den Properties einer HRF Datei
     */
    public Spieler(java.util.Properties properties, Timestamp hrfdate)
      throws Exception
    {
    	// Separate first, nick and last names are available. Utilize them?

        m_iSpielerID = Integer.parseInt(properties.getProperty("id", "0"));
        m_sName = properties.getProperty("name", "");
        m_iAlter = Integer.parseInt(properties.getProperty("ald", "0"));
        m_iAgeDays = Integer.parseInt(properties.getProperty("agedays", "0"));
        m_iKondition = Integer.parseInt(properties.getProperty("uth", "0"));
        m_iForm = Integer.parseInt(properties.getProperty("for", "0"));
        m_iTorwart = Integer.parseInt(properties.getProperty("mlv", "0"));
        m_iVerteidigung = Integer.parseInt(properties.getProperty("bac", "0"));
        m_iSpielaufbau = Integer.parseInt(properties.getProperty("spe", "0"));
        m_iPasspiel = Integer.parseInt(properties.getProperty("fra", "0"));
        m_iFluegelspiel = Integer.parseInt(properties.getProperty("ytt", "0"));
        m_iTorschuss = Integer.parseInt(properties.getProperty("mal", "0"));
        m_iStandards = Integer.parseInt(properties.getProperty("fas", "0"));
        m_iSpezialitaet = Integer.parseInt(properties.getProperty("speciality", "0"));
        m_iCharakter = Integer.parseInt(properties.getProperty("gentleness", "0"));
        m_iAnsehen = Integer.parseInt(properties.getProperty("honesty", "0"));
        m_iAgressivitaet = Integer.parseInt(properties.getProperty("aggressiveness", "0"));
        m_iErfahrung = Integer.parseInt(properties.getProperty("rut", "0"));
        m_bHomeGrown = Boolean.parseBoolean(properties.getProperty("homegr", "FALSE"));
        m_iLoyalty = Integer.parseInt(properties.getProperty("loy", "0"));
        m_iFuehrung = Integer.parseInt(properties.getProperty("led", "0"));
        m_iGehalt = Integer.parseInt(properties.getProperty("sal", "0"));
        m_iNationalitaet = Integer.parseInt(properties.getProperty("countryid", "0"));
        m_iTSI = Integer.parseInt(properties.getProperty("mkt", "0"));
        
     // also read subskills when importing hrf from hattrickportal.pro/ useful for U20/NT
        m_dSubFluegelspiel = Double.parseDouble(properties.getProperty("yttsub", "0"));
        m_dSubPasspiel = Double.parseDouble(properties.getProperty("frasub", "0"));
        m_dSubSpielaufbau = Double.parseDouble(properties.getProperty("spesub", "0"));
        m_dSubStandards = Double.parseDouble(properties.getProperty("fassub", "0"));
        m_dSubTorschuss = Double.parseDouble(properties.getProperty("malsub", "0"));
        m_dSubTorwart = Double.parseDouble(properties.getProperty("mlvsub", "0"));
        m_dSubVerteidigung = Double.parseDouble(properties.getProperty("bacsub", "0"));

        //TSI, alles vorher durch 1000 teilen
        m_clhrfDate = hrfdate;

        if (hrfdate.before(DBManager.TSIDATE)) {
            m_iTSI /= 1000d;
        }

        m_iGelbeKarten = Integer.parseInt(properties.getProperty("warnings", "0"));
        m_iVerletzt = Integer.parseInt(properties.getProperty("ska", "0"));
        m_iToreFreund = Integer.parseInt(properties.getProperty("gtt", "0"));
        m_iToreLiga = Integer.parseInt(properties.getProperty("gtl", "0"));
        m_iTorePokal = Integer.parseInt(properties.getProperty("gtc", "0"));
        m_iToreGesamt = Integer.parseInt(properties.getProperty("gev", "0"));
        m_iHattrick = Integer.parseInt(properties.getProperty("hat", "0"));

        if (properties.get("rating") != null) {
            m_iBewertung = Integer.parseInt(properties.getProperty("rating", "0"));
        }

        String temp = properties.getProperty("trainertype", "-1");

        if ((temp != null) && !temp.equals("")) {
            m_iTrainerTyp = Integer.parseInt(temp);
        }

        temp = properties.getProperty("trainerskill", "0");

        if ((temp != null) && !temp.equals("")) {
            m_iTrainer = Integer.parseInt(temp);
        }

        temp = properties.getProperty("playernumber", "");

        if ((temp != null) && !temp.equals("") && !temp.equals("null")) {
            m_iTrikotnummer = Integer.parseInt(temp);
        }

        m_iTransferlisted = Integer.parseInt(properties.getProperty("transferlisted", "0"));
        m_iLaenderspiele = Integer.parseInt(properties.getProperty("caps", "0"));
        m_iU20Laenderspiele = Integer.parseInt(properties.getProperty("capsU20", "0"));

        //Subskills berechnen
        //Wird beim Speichern des HRFs aufgerufen, da hier nicht unbedingt die notwendigen Daten vorhanden sind
        //Alte Offsets holen!
        //Offsets aus dem aktuellen HRF holen
        final core.model.HOModel oldmodel = core.model.HOVerwaltung.instance()
                                                                                                   .getModel();
        final Spieler oldSpieler = oldmodel.getSpieler(m_iSpielerID);

        if (oldSpieler != null) {
            // Training block
            m_bTrainingBlock = oldSpieler.hasTrainingBlock();

        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_iAgressivitaet.
     *
     * @param m_iAgressivitaet New value of property m_iAgressivitaet.
     */
    public void setAgressivitaet(int m_iAgressivitaet) {
        this.m_iAgressivitaet = m_iAgressivitaet;
    }

    /**
     * Getter for property m_iAgressivitaet.
     *
     * @return Value of property m_iAgressivitaet.
     */
    public int getAgressivitaet() {
        return m_iAgressivitaet;
    }

    /**
     * liefert das Datum des letzen LevelAufstiegs für den angeforderten Skill Vector filled with
     * object[] [0] = Time der Änderung [1] = Boolean: false=Keine Änderung gefunden
     */
    public Vector<Object[]> getAllLevelUp(int skill) {
        return DBManager.instance().getAllLevelUp(skill,m_iSpielerID);
    }

    /**
     * Setter for property m_iAlter.
     *
     * @param m_iAlter New value of property m_iAlter.
     */
    public void setAlter(int m_iAlter) {
        this.m_iAlter = m_iAlter;
    }

    /**
     * Getter for property m_iAlter.
     *
     * @return Value of property m_iAlter.
     */
    public int getAlter() {
        return m_iAlter;
    }

    /**
     * Setter for property m_iAgeDays.
     *
     * @param m_iAlter New value of property m_iAgeDays.
     */
    public void setAgeDays(int m_iAgeDays) {
    	this.m_iAgeDays = m_iAgeDays;
    }

    /**
     * Getter for property m_iAgeDays.
     *
     * @return Value of property m_iAgeDays.
     */
    public int getAgeDays() {
    	return m_iAgeDays;
    }
    
    /**
     * Compute the number of calendar days between two Calendar objects. 
     * The desired value is the number of days of the month between the
     * two Calendars, not the number of milliseconds' worth of days.
     * @param startCal The earlier calendar
     * @param endCal The later calendar
     * @return the number of calendar days of the month between startCal and endCal
     */
    public static long calendarDaysBetween(Calendar startCal, Calendar endCal) {

        // Create copies so we don't update the original calendars.

        Calendar start = Calendar.getInstance();
        start.setTimeZone(startCal.getTimeZone());
        start.setTimeInMillis(startCal.getTimeInMillis());

        Calendar end = Calendar.getInstance();
        end.setTimeZone(endCal.getTimeZone());
        end.setTimeInMillis(endCal.getTimeInMillis());

        // Set the copies to be at midnight, but keep the day information.

        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);

        // At this point, each calendar is set to midnight on 
        // their respective days. Now use TimeUnit.MILLISECONDS to
        // compute the number of full days between the two of them.

        return TimeUnit.MILLISECONDS.toDays(
                Math.abs(end.getTimeInMillis() - start.getTimeInMillis()));
    }

    /**
     * Calculates full age with days and offset
     *
     * @return Double value of age & agedays & offset combined,
     * 			i.e. age + (agedays+offset)/112
     */
    public double getAlterWithAgeDays() {
      	long hrftime = HOVerwaltung.instance().getModel().getBasics().getDatum().getTime();
    	long now = new Date().getTime();
    	long diff = (now - hrftime) / (1000*60*60*24);
    	int years = getAlter();
    	int days = getAgeDays();
    	double retVal = years + (double)(days+diff)/112;
    	return retVal;
    }
    

    /**
     * Calculates full age with days and offset for a given timestamp
     * used to sort columns
     * pay attention that it takes the hour and minute of the matchtime into account
     * if you only want the days between two days use method calendarDaysBetween(Calendar start, Calendar end)
     * @return Double value of age & agedays & offset combined,
     * 			i.e. age + (agedays+offset)/112
     */
    public double getDoubleAgeFromDate(Timestamp t) {
    	long hrftime = HOVerwaltung.instance().getModel().getBasics().getDatum().getTime();
    	long time = t.getTime();
    	long diff = Math.abs((hrftime - time)) / (1000*60*60*24);
    	int years = getAlter();
    	int days = getAgeDays();
    	double retVal = years + (double)(days-diff)/112;
    	return retVal;
    }

    /**
     * Calculates String for full age with days and offset
     * @return String of age & agedays & offset combined,
     * 			format is "YY.DDD"
     */
    public String getAlterWithAgeDaysAsString() {
    	// format = yy.ddd
      	long hrftime = HOVerwaltung.instance().getModel().getBasics().getDatum().getTime();
    	long now = new Date().getTime();
    	long diff = (now - hrftime) / (1000*60*60*24);
    	int years = getAlter();
    	int days = getAgeDays();
    	days += diff;
    	while (days > 111) {
    		days -= 112;
    		years++;
    	}
    	String retVal = years + "." + days;
    	return retVal;
    }
    
    /**
     * Calculates String for full age with days and offset for a given timestamp
     * only takes days between dates into account
     * used for the age column in player analysis tab
     * @return String of age & agedays & offset combined,
     * 			format is "YY.DDD"
     */
    public String getAdjustedAgeFromDate(Timestamp t) {
    	// format = yy.ddd
    	Calendar hrftime = Calendar.getInstance();
    	Calendar date = Calendar.getInstance();
    	hrftime.setTime(HOVerwaltung.instance().getModel().getBasics().getDatum());
    	date.setTime(t);
    	long diff = calendarDaysBetween(hrftime, date);
    	int years = getAlter();
    	int days = getAgeDays();
    	days -= diff;
    	while (days < 0) {
    		days = 112 + days;
    		years--;
    	}
    	String retVal = years + "." + days;
    	return retVal;
    }

    /**
     * Get the full i18n'd string represention the players age. Includes
     * the birthay indicator as well.
     * @return the full i18n'd string represention the players age
     */
    public String getAgeStringFull() {
      	long hrftime = HOVerwaltung.instance().getModel().getBasics().getDatum().getTime();
    	long now = new Date().getTime();
    	long diff = (now - hrftime) / (1000*60*60*24);
    	int years = getAlter();
    	int days = getAgeDays();
    	days += diff;
    	boolean birthday = false;
    	while (days > 111) {
    		days -= 112;
    		years++;
    		birthday = true;
    	}
    	StringBuffer ret = new StringBuffer();
    	ret.append(years);
    	ret.append(" ");
    	ret.append(HOVerwaltung.instance().getLanguageString("ls.player.age.years"));
    	ret.append(" ");
    	ret.append(days);
    	ret.append(" ");
    	ret.append(HOVerwaltung.instance().getLanguageString("ls.player.age.days"));
    	if (birthday) {
    		ret.append(" (");
    		ret.append(HOVerwaltung.instance().getLanguageString("ls.player.age.birthday"));
    		ret.append(")");
    	}
    	return ret.toString();
    }

    /**
     * Setter for property m_iAnsehen.
     *
     * @param m_iAnsehen New value of property m_iAnsehen.
     */
    public void setAnsehen(int m_iAnsehen) {
        this.m_iAnsehen = m_iAnsehen;
    }

    /**
     * Getter for property m_iAnsehen.
     *
     * @return Value of property m_iAnsehen.
     */
    public int getAnsehen() {
        return m_iAnsehen;
    }

    /**
     * Setter for property m_iBewertung.
     *
     * @param m_iBewertung New value of property m_iBewertung.
     */
    public void setBewertung(int m_iBewertung) {
        this.m_iBewertung = m_iBewertung;
    }

    /**
     * Getter for property m_iBewertung.
     *
     * @return Value of property m_iBewertung.
     */
    public int getBewertung() {
        return m_iBewertung;
    }

    /**
     * Getter for property m_iBonus.
     *
     * @return Value of property m_iBonus.
     */
    public int getBonus() {
        int bonus = 0;

        if (m_iNationalitaet != HOVerwaltung.instance().getModel().getBasics().getLand()) {
            bonus = 20;
        }

        return bonus;
    }

    /**
     * Setter for property m_iCharakter.
     *
     * @param m_iCharakter New value of property m_iCharakter.
     */
    public void setCharakter(int m_iCharakter) {
        this.m_iCharakter = m_iCharakter;
    }

    /**
     * Getter for property m_iCharackter.
     *
     * @return Value of property m_iCharackter.
     */
    public int getCharakter() {
        return m_iCharakter;
    }

    /**
     * Setter for property m_iErfahrung.
     *
     * @param m_iErfahrung New value of property m_iErfahrung.
     */
    public void setErfahrung(int m_iErfahrung) {
        this.m_iErfahrung = m_iErfahrung;
    }

    /**
     * Getter for property m_iErfahrung.
     *
     * @return Value of property m_iErfahrung.
     */
    public int getErfahrung() {
        return m_iErfahrung;
    }

    /**
     * get the experience bonus
     *
     * @param experience effective experience to calculate the bonus, use the xp from the player if set to 0
     *
     * @return experience bonus in percent
     */
    public float getErfahrungsBonus(float experience) {
        if (experience <= 0)
        	// take xp from player (use medium xp sub, i.e. add 0.5)
        	experience = m_iErfahrung + 0.5f;

        // normalize xp [1,20] -> [0,19]
        experience -= 1;

		if ( experience <= 0 )
			return 0; /*If experience is non-existent, the bonus is zero!*/

		// Use hardcorded values here,
		// make sure to apply the same values as in prediction/*/playerStrength.dat
		//
		// We return the experience bonus in percent (0% = no bonus, 100% = doubled player strength...)
		float bonus = (float) (0.0716 * Math.sqrt(experience));

        return bonus;
    }

    /**
     * Setter for property m_iFluegelspiel.
     *
     * @param m_iFluegelspiel New value of property m_iFluegelspiel.
     */
    public void setFluegelspiel(int m_iFluegelspiel) {
        this.m_iFluegelspiel = m_iFluegelspiel;
    }

    /**
     * Getter for property m_iFluegelspiel.
     *
     * @return Value of property m_iFluegelspiel.
     */
    public int getFluegelspiel() {
        return m_iFluegelspiel;
    }

    /**
     * Setter for property m_iForm.
     *
     * @param m_iForm New value of property m_iForm.
     */
    public void setForm(int m_iForm) {
        this.m_iForm = m_iForm;
    }

    /**
     * Getter for property m_iForm.
     *
     * @return Value of property m_iForm.
     */
    public int getForm() {
        return m_iForm;
    }

    /**
     * Setter for property m_iFuehrung.
     *
     * @param m_iFuehrung New value of property m_iFuehrung.
     */
    public void setFuehrung(int m_iFuehrung) {
        this.m_iFuehrung = m_iFuehrung;
    }

    /**
     * Getter for property m_iFuehrung.
     *
     * @return Value of property m_iFuehrung.
     */
    public int getFuehrung() {
        return m_iFuehrung;
    }

    /**
     * Setter for property m_iGehalt.
     *
     * @param m_iGehalt New value of property m_iGehalt.
     */
    public void setGehalt(int m_iGehalt) {
        this.m_iGehalt = m_iGehalt;
    }

    /**
     * Getter for property m_iGehalt.
     *
     * @return Value of property m_iGehalt.
     */
    public int getGehalt() {
        return m_iGehalt;
    }

    /**
     * Setter for property m_iGelbeKarten.
     *
     * @param m_iGelbeKarten New value of property m_iGelbeKarten.
     */
    public void setGelbeKarten(int m_iGelbeKarten) {
        this.m_iGelbeKarten = m_iGelbeKarten;
    }

    /**
     * Getter for property m_iGelbeKarten.
     *
     * @return Value of property m_iGelbeKarten.
     */
    public int getGelbeKarten() {
        return m_iGelbeKarten;
    }

    /**
     * gibt an ob der spieler gesperrt ist
     */
    public boolean isGesperrt() {
        return (m_iGelbeKarten > 2);
    }

    /**
     * Setter for property m_iHattrick.
     *
     * @param m_iHattrick New value of property m_iHattrick.
     */
    public void setHattrick(int m_iHattrick) {
        this.m_iHattrick = m_iHattrick;
    }

    /**
     * Getter for property m_iHattrick.
     *
     * @return Value of property m_iHattrick.
     */
    public int getHattrick() {
        return m_iHattrick;
    }

    /**
     * Setter for m_bHomeGrown
     *
     * @return Value of property m_bHomeGrown.
     */
    public void setHomeGrown(boolean hg) {
    	m_bHomeGrown = hg;
    }

    /**
     * Getter for m_bHomeGrown
     *
     * @return Value of property m_bHomeGrown
     */
    public boolean isHomeGrown() {
    	return m_bHomeGrown;
    }

    public Timestamp getHrfDate() {
        return m_clhrfDate;
    }

    public void setHrfDate(Timestamp timestamp){
    	m_clhrfDate = timestamp;
    }
    /**
     * liefert die Stärke für die IdealPosition
     */
    public float getIdealPosStaerke(boolean mitForm) {
        return calcPosValue(getIdealPosition(), mitForm);
    }

    /**
     * liefert die IdealPosition
     */
    public byte getIdealPosition() {
        //Usr Vorgabe aus DB holen
        final byte flag = getUserPosFlag();

        if (flag == ISpielerPosition.UNKNOWN) {
            final FactorObject[] allPos = FormulaFactors.instance().getAllObj();
            byte idealPos = ISpielerPosition.UNKNOWN;
            float maxStk = -1.0f;

            for (int i = 0; (allPos != null) && (i < allPos.length); i++) {
                if (calcPosValue(allPos[i].getPosition(),true) > maxStk) {
                	maxStk = calcPosValue(allPos[i].getPosition(),true);
                    idealPos = allPos[i].getPosition();
                }
            }

            return idealPos;
        }

        return flag;
    }

    /**
     * Setter for property m_iKondition.
     *
     * @param m_iKondition New value of property m_iKondition.
     */
    public void setKondition(int m_iKondition) {
        this.m_iKondition = m_iKondition;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //Accessor
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter for property m_iKondition.
     *
     * @return Value of property m_iKondition.
     */
    public int getKondition() {
        return m_iKondition;
    }

    /**
     * Setter for property m_iLaenderspiele.
     *
     * @param m_iLaenderspiele New value of property m_iLaenderspiele.
     */
    public void setLaenderspiele(int m_iLaenderspiele) {
        this.m_iLaenderspiele = m_iLaenderspiele;
    }

    /**
     * Getter for property m_iLaenderspiele.
     *
     * @return Value of property m_iLaenderspiele.
     */
    public int getLaenderspiele() {
        return m_iLaenderspiele;
    }

    /**
     * liefert das Datum des letzen LevelAufstiegs für den angeforderten Skill [0] = Time der
     * Änderung [1] = Boolean: false=Keine Änderung gefunden
     */
    public Object[] getLastLevelUp(int skill) {
        return DBManager.instance().getLastLevelUp(skill, m_iSpielerID);
    }

    /**
     * liefert die vergangenen Tage seit dem letzem LevelAufstieg für den angeforderten Skill
     *
     * @return anzahl Tage seit dem letzen Aufstieg
     */
    public int getLastLevelUpInTage(int skill) {
        int tage = 0;
        final Timestamp datum = (Timestamp) getLastLevelUp(skill)[0];
        final Timestamp heute = new Timestamp(System.currentTimeMillis());
        long diff = 0;

        if (datum != null) {
            diff = heute.getTime() - datum.getTime();

            //In Tage umrechnen
            tage = (int) (diff / 86400000);
        }

        return tage;
    }

    /**
     * Gibt die Letzte Bewertung zurück, die der Spieler bekommen hat
     */
    public int getLetzteBewertung() {
        if (m_iLastBewertung < 0) {
            m_iLastBewertung = DBManager.instance().getLetzteBewertung4Spieler(m_iSpielerID);
        }

        return m_iLastBewertung;
    }


    /**
     * Returns the loyalty stat
     */
    public int getLoyalty() {
    	return m_iLoyalty;
    }

    /**
     * Sets the loyalty stat
     */
    public void  setLoyalty(int loy) {
    	m_iLoyalty = loy;
    }


    /**
     * Setter for property m_sManuellerSmilie.
     *
     * @param manuellerSmilie New value of property m_sManuellerSmilie.
     */
    public void setManuellerSmilie(java.lang.String manuellerSmilie) {
        if (manuellerSmilie == null) {
            manuellerSmilie = "";
        }

        m_sManuellerSmilie = manuellerSmilie;
        DBManager.instance().saveManuellerSmilie(m_iSpielerID, manuellerSmilie);
    }

    /**
     * Getter for property m_sManuellerSmilie.
     *
     * @return Value of property m_sManuellerSmilie.
     */
    public java.lang.String getManuellerSmilie() {
        if (m_sManuellerSmilie == null) {
            m_sManuellerSmilie = DBManager.instance().getManuellerSmilie(m_iSpielerID);

            //Steht null in der DB?
            if (m_sManuellerSmilie == null) {
                m_sManuellerSmilie = "";
            }
        }

        //database.DBZugriff.instance ().getManuellerSmilie( m_iSpielerID );
        return m_sManuellerSmilie;
    }

     /**
     * Sets the TSI
     *
     * @param m_iTSI New value of property m_iMarkwert.
     */
    public void setTSI(int m_iTSI) {
        this.m_iTSI = m_iTSI;
    }

    /**
     * Returns the TSI
     *
     * @return Value of property m_iMarkwert.
     */
    public int getTSI() {
        return m_iTSI;
    }

    /**
     * Returns the estimated value of this player (EPV)
     *
     * @return EPV
     */
//    public double getEPV() {
//		EPVData data = HOVerwaltung.instance().getModel().getEPV().getEPVData(this);
//		return HOVerwaltung.instance().getModel().getEPV().getPrice(data);
//    }

    /**
     * Setter for property m_sName.
     *
     * @param m_sName New value of property m_sName.
     */
    public void setName(java.lang.String m_sName) {
        this.m_sName = m_sName;
    }

    /**
     * Getter for property m_sName.
     *
     * @return Value of property m_sName.
     */
    public java.lang.String getName() {
        return DBManager.deleteEscapeSequences(m_sName);
    }
    
    /**
     * Getter for shortName
     * @return returns the fist letter of the first Name + a "." and the last name
     * eg: James Bond = J. Bond
     */
    public String getShortName() {
    	String fullName = getName();
    	return fullName.substring(0, 1) + ". " + fullName.substring(fullName.indexOf(" ") + 1);
    	
    }

    /**
     * Setter for property m_iNationalitaet.
     *
     * @param m_iNationalitaet New value of property m_iNationalitaet.
     */
    public void setNationalitaet(int m_iNationalitaet) {
        this.m_iNationalitaet = m_iNationalitaet;
    }

    /**
     * Getter for property m_iNationalitaet.
     *
     * @return Value of property m_iNationalitaet.
     */
    public int getNationalitaet() {
        return m_iNationalitaet;
    }

    /**
     * Setter for property m_bOld.
     *
     * @param m_bOld New value of property m_bOld.
     */
    public void setOld(boolean m_bOld) {
        this.m_bOld = m_bOld;
    }

    /**
     * Getter for property m_bOld.
     *
     * @return Value of property m_bOld.
     */
    public boolean isOld() {
        return m_bOld;
    }

    /**
     * Setter for property m_iPasspiel.
     *
     * @param m_iPasspiel New value of property m_iPasspiel.
     */
    public void setPasspiel(int m_iPasspiel) {
        this.m_iPasspiel = m_iPasspiel;
    }

    /**
     * Getter for property m_iPasspiel.
     *
     * @return Value of property m_iPasspiel.
     */
    public int getPasspiel() {
        return m_iPasspiel;
    }

    /**
     * Zum speichern! Die Reduzierung des Marktwerts auf TSI wird rückgängig gemacht
     */
    public int getSaveMarktwert() {
        if (m_clhrfDate == null || m_clhrfDate.before(DBManager.TSIDATE)) {
            //Echter Marktwert
            return m_iTSI * 1000;
        }

        //TSI
        return m_iTSI;
    }

    /**
     * Setter for property m_iSpezialitaet.
     *
     * @param m_iSpezialitaet New value of property m_iSpezialitaet.
     */
    public void setSpezialitaet(int m_iSpezialitaet) {
        this.m_iSpezialitaet = m_iSpezialitaet;
    }

    /**
     * Getter for property m_iSpezialitaet.
     *
     * @return Value of property m_iSpezialitaet.
     */
    public int getSpezialitaet() {
        return m_iSpezialitaet;
    }
    

    // returns the name of the speciality in the used language
    public String getSpecialityName() {
    	Speciality s = Speciality.values()[m_iSpezialitaet];
		if (s.equals(Speciality.NO_SPECIALITY)) {
			return EMPTY;
		} else {
			return HOVerwaltung.instance().getLanguageString("ls.player.speciality." + s.toString().toLowerCase(Locale.ROOT));
		}
    }
    
    // return the name of the speciality with a break before and in brackets
    // e.g. [br][quick], used for HT-ML export
    public String getSpecialityExportName() {
		Speciality s = Speciality.values()[m_iSpezialitaet];
		if (s.equals(Speciality.NO_SPECIALITY)) {
			return EMPTY;
		} else {
			return BREAK + O_BRACKET + getSpecialityName() + C_BRACKET;
		}
	}
    
    // no break so that the export looks better
    public String getSpecialityExportNameForKeeper() {
		Speciality s = Speciality.values()[m_iSpezialitaet];
		if (s.equals(Speciality.NO_SPECIALITY)) {
			return EMPTY;
		} else {
			return O_BRACKET + getSpecialityName() + C_BRACKET;
		}
	}
    
    
    

    /**
     * Setter for property m_iSpielaufbau.
     *
     * @param m_iSpielaufbau New value of property m_iSpielaufbau.
     */
    public void setSpielaufbau(int m_iSpielaufbau) {
        this.m_iSpielaufbau = m_iSpielaufbau;
    }

    /**
     * Getter for property m_iSpielaufbau.
     *
     * @return Value of property m_iSpielaufbau.
     */
    public int getSpielaufbau() {
        return m_iSpielaufbau;
    }

    /**
     * setzt ob der User den Spieler zum Spiel zulässt
     */
    public void setSpielberechtigt(boolean flag) {
        m_bSpielberechtigt = Boolean.valueOf(flag);
        DBManager.instance().saveSpielerSpielberechtigt(m_iSpielerID,
                                                                                      flag);
    }

    /**
     * gibt an ob der User den Spieler zum Spiel zulässt
     */
    public boolean isSpielberechtigt() {
        //Nur pr�fen, wenn nicht Spielberechtigt: Reduziert Zugriffe!
        if (m_bSpielberechtigt == null) {
            m_bSpielberechtigt = Boolean.valueOf(DBManager.instance().getSpielerSpielberechtigt(m_iSpielerID));
        }

        return m_bSpielberechtigt.booleanValue();

    }

    /**
     * Setter for property m_iSpielerID.
     *
     * @param m_iSpielerID New value of property m_iSpielerID.
     */
    public void setSpielerID(int m_iSpielerID) {
        this.m_iSpielerID = m_iSpielerID;
    }

    /**
     * Getter for property m_iSpielerID.
     *
     * @return Value of property m_iSpielerID.
     */
    public int getSpielerID() {
        return m_iSpielerID;
    }

    /**
     * Setter for property m_iStandards.
     *
     * @param m_iStandards New value of property m_iStandards.
     */
    public void setStandards(int m_iStandards) {
        this.m_iStandards = m_iStandards;
    }

    /**
     * Getter for property m_iStandards.
     *
     * @return Value of property m_iStandards.
     */
    public int getStandards() {
        return m_iStandards;
    }

    /**
     * berechnet den Subskill pro position
     */
    public float getSubskill4Pos(int skill) {
    	return Math.min(0.99f, Helper.round(getSubskill4PosAccurate(skill), 2));
    }

    /**
     * Returns accurate subskill number. If you need subskill for UI
     * purpose it is better to use getSubskill4Pos()
     *
     * @param skill
     * @return subskill between 0.0-0.999
     */
    public float getSubskill4PosAccurate(int skill) {
        double value = 0;

        switch (skill) {
            case PlayerSkill.KEEPER:
                value = m_dSubTorwart;
                break;

            case PlayerSkill.PLAYMAKING:
                value =  m_dSubSpielaufbau;
                break;

            case PlayerSkill.DEFENDING:
                value =  m_dSubVerteidigung;
                break;

            case PlayerSkill.PASSING:
                value =  m_dSubPasspiel;
                break;

            case PlayerSkill.WINGER:
                value =  m_dSubFluegelspiel;
                break;

            case PlayerSkill.SCORING:
                value =  m_dSubTorschuss;
                break;

            case PlayerSkill.SET_PIECES:
                value =  m_dSubStandards;
                break;
        }
        return (float) Math.min(0.999, value);
    }

    public void setSubskill4Pos(int skill, float value) {
        switch (skill) {
            case PlayerSkill.KEEPER:
                 m_dSubTorwart= value;
                 break;
            case PlayerSkill.PLAYMAKING:
                m_dSubSpielaufbau= value;
                break;
            case PlayerSkill.DEFENDING:
                m_dSubVerteidigung= value;
                break;
            case PlayerSkill.PASSING:
                m_dSubPasspiel= value;
                break;
            case PlayerSkill.WINGER:
                m_dSubFluegelspiel= value;
                break;
            case PlayerSkill.SCORING:
                m_dSubTorschuss= value;
                break;
            case PlayerSkill.SET_PIECES:
                m_dSubStandards= value;
                break;
        }
    }

    /**
     * Setter for property m_sTeamInfoSmilie.
     *
     * @param teamInfoSmilie New value of property m_sTeamInfoSmilie.
     */
    public void setTeamInfoSmilie(String teamInfoSmilie) {
        if (teamInfoSmilie == null) {
            teamInfoSmilie = "";
        }

        m_sTeamInfoSmilie = teamInfoSmilie;
        DBManager.instance().saveTeamInfoSmilie(m_iSpielerID, teamInfoSmilie);
    }

    /**
     * Getter for property m_sTeamInfoSmilie.
     *
     * @return Value of property m_sTeamInfoSmilie.
     */
    public String getTeamInfoSmilie() {
        if (m_sTeamInfoSmilie == null) {
            m_sTeamInfoSmilie = DBManager.instance().getTeamInfoSmilie(m_iSpielerID);

            //Steht null in der DB?
            if (m_sTeamInfoSmilie == null) {
                m_sTeamInfoSmilie = "";
            }
        }

        //database.DBZugriff.instance ().getTeamInfoSmilie( m_iSpielerID );
        return m_sTeamInfoSmilie;
    }

    /**
     * Gibt das Datum des ersten HRFs mit dem Spieler zurück
     */
    public Timestamp getTimestamp4FirstPlayerHRF() {
        if (m_tsTime4FirstHRF == null) {
            m_tsTime4FirstHRF = DBManager.instance().getTimestamp4FirstPlayerHRF(m_iSpielerID);
        }

        return m_tsTime4FirstHRF;
    }

    /**
     * Setter for property m_iToreFreund.
     *
     * @param m_iToreFreund New value of property m_iToreFreund.
     */
    public void setToreFreund(int m_iToreFreund) {
        this.m_iToreFreund = m_iToreFreund;
    }

    /**
     * Getter for property m_iToreFreund.
     *
     * @return Value of property m_iToreFreund.
     */
    public int getToreFreund() {
        return m_iToreFreund;
    }

    /**
     * Setter for property m_iToreGesamt.
     *
     * @param m_iToreGesamt New value of property m_iToreGesamt.
     */
    public void setToreGesamt(int m_iToreGesamt) {
        this.m_iToreGesamt = m_iToreGesamt;
    }

    /**
     * Getter for property m_iToreGesamt.
     *
     * @return Value of property m_iToreGesamt.
     */
    public int getToreGesamt() {
        return m_iToreGesamt;
    }

    /**
     * Setter for property m_iToreLiga.
     *
     * @param m_iToreLiga New value of property m_iToreLiga.
     */
    public void setToreLiga(int m_iToreLiga) {
        this.m_iToreLiga = m_iToreLiga;
    }

    /**
     * Getter for property m_iToreLiga.
     *
     * @return Value of property m_iToreLiga.
     */
    public int getToreLiga() {
        return m_iToreLiga;
    }

    /**
     * Setter for property m_iTorePokal.
     *
     * @param m_iTorePokal New value of property m_iTorePokal.
     */
    public void setTorePokal(int m_iTorePokal) {
        this.m_iTorePokal = m_iTorePokal;
    }

    /**
     * Getter for property m_iTorePokal.
     *
     * @return Value of property m_iTorePokal.
     */
    public int getTorePokal() {
        return m_iTorePokal;
    }

    /**
     * Setter for property m_iTorschuss.
     *
     * @param m_iTorschuss New value of property m_iTorschuss.
     */
    public void setTorschuss(int m_iTorschuss) {
        this.m_iTorschuss = m_iTorschuss;
    }

    /**
     * Getter for property m_iTorschuss.
     *
     * @return Value of property m_iTorschuss.
     */
    public int getTorschuss() {
        return m_iTorschuss;
    }

    /**
     * Setter for property m_iTorwart.
     *
     * @param m_iTorwart New value of property m_iTorwart.
     */
    public void setTorwart(int m_iTorwart) {
        this.m_iTorwart = m_iTorwart;
    }

    /**
     * Getter for property m_iTorwart.
     *
     * @return Value of property m_iTorwart.
     */
    public int getTorwart() {
        return m_iTorwart;
    }

    /**
     * Setter for property m_iTrainer.
     *
     * @param m_iTrainer New value of property m_iTrainer.
     */
    public void setTrainer(int m_iTrainer) {
        this.m_iTrainer = m_iTrainer;
    }

    /**
     * Getter for property m_iTrainer.
     *
     * @return Value of property m_iTrainer.
     */
    public int getTrainer() {
        return m_iTrainer;
    }

    /**
     * gibt an ob der Spieler Trainer ist
     */
    public boolean isTrainer() {
        return ((m_iTrainer > 0) && (m_iTrainerTyp >= 0));
    }

    /**
     * Setter for property m_iTrainerTyp.
     *
     * @param m_iTrainerTyp New value of property m_iTrainerTyp.
     */
    public void setTrainerTyp(int m_iTrainerTyp) {
        this.m_iTrainerTyp = m_iTrainerTyp;
    }

    /**
     * Getter for property m_iTrainerTyp.
     *
     * @return Value of property m_iTrainerTyp.
     */
    public int getTrainerTyp() {
        return m_iTrainerTyp;
    }

    /**
     * Setter for property m_iTransferlisted.
     *
     * @param m_iTransferlisted New value of property m_iTransferlisted.
     */
    public void setTransferlisted(int m_iTransferlisted) {
        this.m_iTransferlisted = m_iTransferlisted;
    }

    /**
     * Getter for property m_iTransferlisted.
     *
     * @return Value of property m_iTransferlisted.
     */
    public int getTransferlisted() {
        return m_iTransferlisted;
    }

    /**
     * Setter for property m_iTrikotnummer.
     *
     * @param m_iTrikotnummer New value of property m_iTrikotnummer.
     */
    public void setTrikotnummer(int m_iTrikotnummer) {
        this.m_iTrikotnummer = m_iTrikotnummer;
    }

    /**
     * Getter for property m_iTrikotnummer.
     *
     * @return Value of property m_iTrikotnummer.
     */
    public int getTrikotnummer() {
        return m_iTrikotnummer;
    }

    /**
     * Setter for property m_iU20Laenderspiele.
     *
     * @param m_iU20Laenderspiele New value of property m_iU20Laenderspiele.
     */
    public void setU20Laenderspiele(int m_iU20Laenderspiele) {
        this.m_iU20Laenderspiele = m_iU20Laenderspiele;
    }

    /**
     * Getter for property m_iU20Laenderspiele.
     *
     * @return Value of property m_iU20Laenderspiele.
     */
    public int getU20Laenderspiele() {
        return m_iU20Laenderspiele;
    }

    public void setUserPosFlag(byte flag) {
        m_bUserPosFlag = flag;
        DBManager.instance().saveSpielerUserPosFlag(m_iSpielerID, m_bUserPosFlag);
    }

    /**
     * liefert User Notiz zum Spieler
     */
    public byte getUserPosFlag() {
        if (m_bUserPosFlag < SpielerPosition.UNKNOWN) {
            m_bUserPosFlag = DBManager.instance().getSpielerUserPosFlag(m_iSpielerID);
        }

        //database.DBZugriff.instance ().getSpielerNotiz ( m_iSpielerID );
        return m_bUserPosFlag;
    }

    /**
     * get Skillvalue 4 skill
     */
    public int getValue4Skill4(int skill) {
        switch (skill) {
            case PlayerSkill.KEEPER:
                return m_iTorwart;

            case PlayerSkill.PLAYMAKING:
                return m_iSpielaufbau;

            case PlayerSkill.DEFENDING:
                return m_iVerteidigung;

            case PlayerSkill.PASSING:
                return m_iPasspiel;

            case PlayerSkill.WINGER:
                return m_iFluegelspiel;

            case PlayerSkill.SCORING:
                return m_iTorschuss;

            case PlayerSkill.SET_PIECES:
                return m_iStandards;

            case PlayerSkill.STAMINA:
                return m_iKondition;

            case PlayerSkill.EXPERIENCE:
                return m_iErfahrung;

            case PlayerSkill.FORM:
                return m_iForm;

            case PlayerSkill.LEADERSHIP:
                return m_iFuehrung;

            case PlayerSkill.LOYALTY:
            	return m_iLoyalty;

            default:
                return 0;
        }
    }

    /**
     * set Skillvalue 4 skill
     *
     * @param skill the skill to change
     * @param value the new skill value
     */
	public void setValue4Skill4(int skill, int value) {
		switch (skill) {
			case PlayerSkill.KEEPER :
				setTorwart(value);
				break;

			case PlayerSkill.PLAYMAKING :
				setSpielaufbau(value);
				break;

			case PlayerSkill.PASSING :
				setPasspiel(value);
				break;

			case PlayerSkill.WINGER :
				setFluegelspiel(value);
				break;

			case PlayerSkill.DEFENDING :
				setVerteidigung(value);
				break;

			case PlayerSkill.SCORING :
				setTorschuss(value);
				break;

			case PlayerSkill.SET_PIECES :
				setStandards(value);
				break;

			case PlayerSkill.STAMINA :
				setKondition(value);
				break;

			case PlayerSkill.EXPERIENCE:
				setErfahrung(value);
				break;

			case PlayerSkill.FORM:
				setForm(value);
				break;

			case PlayerSkill.LEADERSHIP:
				setFuehrung(value);
				break;

			case PlayerSkill.LOYALTY:
				setLoyalty(value);
		}
	}


    /**
     * Setter for property m_iVerletzt.
     *
     * @param m_iVerletzt New value of property m_iVerletzt.
     */
    public void setVerletzt(int m_iVerletzt) {
        this.m_iVerletzt = m_iVerletzt;
    }

    /**
     * Getter for property m_iVerletzt.
     *
     * @return Value of property m_iVerletzt.
     */
    public int getVerletzt() {
        return m_iVerletzt;
    }

    /**
     * Setter for property m_iVerteidigung.
     *
     * @param m_iVerteidigung New value of property m_iVerteidigung.
     */
    public void setVerteidigung(int m_iVerteidigung) {
        this.m_iVerteidigung = m_iVerteidigung;
    }

    /**
     * Getter for property m_iVerteidigung.
     *
     * @return Value of property m_iVerteidigung.
     */
    public int getVerteidigung() {
        return m_iVerteidigung;
    }

    /*
       Wetterabhängige Sonderereignisse
       Bestimmte Spezialfähigkeiten können in Zusammenhang mit einem bestimmten Wetter
        zu Sonderereignissen führen. Die Auswirkung dieses Sonderereignisses tritt
        von dem Zeitpunkt in Kraft, an dem es im Spielbericht erwähnt wird,
        und hat bis zum Spielende Einfluß auf die Leistung des Spielers.
        Diese Auswirkung wird nach dem Spiel an der Spielerbewertung (Anzahl Sterne) sichtbar.
       Die Torschuß- und die Spielaufbau-Fähigkeit von Ballzauberern kann sich bei Regen verschlechtern,
        während sich die gleichen Fähigkeiten bei Sonnenschein verbessern können.
       Bei Regen gibt es die Möglichkeit, daß sich die Torschuß-, Verteidigungs- und Spielaufbau-Fähigkeit
        von durchsetzungsstarken Spielern verbessert.
        Auf der anderen Seite kann sich die Torschußfähigkeit bei Sonnenschein verschlechtern.
       Schnelle Spieler laufen bei Regen Gefahr, daß sich ihre Torschuß- und
        Verteidigungsfähigkeiten verschlechtern. Bei Sonnenschein besteht das Risiko
        , daß ihre Torschußfähigkeit unter dem Wetter leidet.
     */
    /*
       Liefert die mögliche Auswirkung des Wetters auf den Spieler
       return 0 bei keine auswirkung
       1 bei positiv
       -1 bei negativ
     */
    public int getWetterEffekt(Weather weather) {
        return PlayerSpeciality.getWeatherEffect(weather, m_iSpezialitaet);
    }

    private void incrementSubskills(Spieler originalPlayer, int assistants, int trainerlevel, int intensity,
            int stamina, int skill, double points, WeeklyTrainingType wt, List<StaffMember> staff)
    {
        if (skill < PlayerSkill.KEEPER || points <= 0)
            return;

        float gain = (float)Helper.round(points / wt.getTrainingLength(
                        this, assistants, trainerlevel, intensity, stamina, staff), 3);

        if (gain <= 0)
            return;

        /* Limit training to one full level max */
        gain = Math.min(gain, 1.0f);

        if (check4SkillUp(skill, originalPlayer)) {
            /* Carry subskill over skillup */
            gain = 0.0f;
        }
        setSubskill4Pos(skill, Math.min(0.99f, getSubskill4PosAccurate(skill) + gain));
    }

    /**
     * Calculates training benefit, and updates subskill for the player.
     * Used when there is only 1 week of training to be calculated.
     *
     * @param originalPlayer - The player to calculate subskills on
     * @param assistants - The number of assistants
     * @param trainerlevel - The trainer level
     * @param intensity - Training intensity
     * @param hrfID - the ID of the HRF
     */
    public void calcIncrementalSubskills(Spieler originalPlayer, int assistants, int trainerlevel, int intensity,
    		int stamina, TrainingPerWeek trainingWeek, List<StaffMember> staff) {

    	if (this.hasTrainingBlock()) {
    		return;
    	}
    	
        if (trainingWeek == null)
            return;

        TrainingPerPlayer trForPlayer = TrainingManager.instance().calculateWeeklyTrainingForPlayer(this, trainingWeek, null);

        if (trForPlayer == null)
            return;

        TrainingPoints tp = trForPlayer.getTrainingPair();

        if (tp == null)
            return;

        /* Time to perform skill drop */
        if (SkillDrops.instance().isActive()) {
            performSkilldrop(originalPlayer, 1);
        }

        WeeklyTrainingType wt = WeeklyTrainingType.instance(trainingWeek.getTrainingType());

        incrementSubskills(originalPlayer, assistants, trainerlevel, intensity, stamina,
                wt.getPrimaryTrainingSkill(), tp.getPrimary(), wt, staff);

        incrementSubskills(originalPlayer, assistants, trainerlevel, intensity, stamina,
                wt.getSecondaryTrainingSkill(), tp.getSecondary(), wt, staff);
    }


    /**
     * Performs skill drops on the player based on age and skills
     *
     * @param originalPlayer The player as he was before this week. Used to find a subskill to drop from.
     * @param weeks The number of weeks to drop in case of missing info.
     */

    public void performSkilldrop(Spieler originalPlayer, int weeks) {

    	if (originalPlayer == null) {
    		return;
    	}

    	for (int skillType=0; skillType < PlayerSkill.EXPERIENCE; skillType++) {

    		if ((skillType == PlayerSkill.FORM) || (skillType == PlayerSkill.STAMINA)) {
    			continue;
    		}

   			if (getValue4Skill4(skillType) >= 1) {
    			float drop = weeks * SkillDrops.instance().getSkillDrop(getValue4Skill4(skillType), originalPlayer.getAlter(), skillType);

    			// Only bother if there is drop, there is something to drop from,
    			//and check that the player has not popped
	    		if ((drop > 0) && (originalPlayer.getSubskill4PosAccurate(skillType) >0)
	    				&& (getValue4Skill4(skillType) == originalPlayer.getValue4Skill4(skillType))) {
	    			setSubskill4Pos(skillType, Math.max(0, getSubskill4PosAccurate(skillType) - drop/100));
	    		}
    		}
    	}
    }

    ////////////////////////////////////////////////////////////////////////////////
    //Helper
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Calculate the player strength on a specific lineup position
     * with or without form
     *
     * @param fo 		FactorObject with the skill weights for this position
     * @param useForm	consider form?
     *
     * @return 			the player strength on this position
     */
    float calcPosValue(FactorObject fo, boolean useForm) {
        if ((fo == null) || (fo.getSum() == 0.0f)) {
            return -1.0f;
        }

        // The stars formulas are changed by the user -> clear the cache
        if (!starRatingCache.containsKey("lastChange") || ((Date)starRatingCache.get("lastChange")).before(FormulaFactors.getLastChange())) {
//    		System.out.println ("Clearing stars cache");
        	starRatingCache.clear();
        	starRatingCache.put("lastChange", new Date());
        }
        /**
         * Create a key for the Hashtable cache
         * We cache every star rating to speed up calculation
         * (calling RPM.calcPlayerStrength() is quite expensive and
         * this method is used very often)
         */

        float loy = RatingPredictionManager.getLoyaltyHomegrownBonus(this);

        String key = fo.getPosition() + ":"
        					+ Helper.round(getTorwart() + getSubskill4Pos(PlayerSkill.KEEPER) + loy, 2) + "|"
        					+ Helper.round(getSpielaufbau() + getSubskill4Pos(PlayerSkill.PLAYMAKING) + loy, 2) + "|"
        					+ Helper.round(getVerteidigung() + getSubskill4Pos(PlayerSkill.DEFENDING) + loy, 2) + "|"
        					+ Helper.round(getFluegelspiel() + getSubskill4Pos(PlayerSkill.WINGER) + loy, 2) + "|"
        					+ Helper.round(getPasspiel() + getSubskill4Pos(PlayerSkill.PASSING) + loy, 2) + "|"
        					+ Helper.round(getStandards() + getSubskill4Pos(PlayerSkill.SET_PIECES) + loy, 2) + "|"
        					+ Helper.round(getTorschuss() + getSubskill4Pos(PlayerSkill.SCORING) + loy, 2) + "|"
        					+ getForm() + "|"
        					+ getKondition() + "|"
        					+ getErfahrung() + "|"
        					// We need to add the specialty, because of Technical DefFW
        					+ getSpezialitaet();

        // Check if the key already exists in cache
        if (starRatingCache.containsKey(key)) {
//        	System.out.println ("Using star rating from cache, key="+key+", tablesize="+starRatingCache.size());
        	return ((Float)starRatingCache.get(key)).floatValue();
        }
    	final boolean normalized = false;

        float gkValue = fo.getTorwartScaled(normalized) * RatingPredictionManager.calcPlayerStrength(this, PlayerSkill.KEEPER, useForm);

        float pmValue = fo.getSpielaufbauScaled(normalized) * RatingPredictionManager.calcPlayerStrength(this, PlayerSkill.PLAYMAKING, useForm);

        float deValue = fo.getVerteidigungScaled(normalized) * RatingPredictionManager.calcPlayerStrength(this, PlayerSkill.DEFENDING, useForm);

        float wiValue = fo.getFluegelspielScaled(normalized) * RatingPredictionManager.calcPlayerStrength(this, PlayerSkill.WINGER, useForm);

        float psValue = fo.getPasspielScaled(normalized) * RatingPredictionManager.calcPlayerStrength(this, PlayerSkill.PASSING, useForm);

        // Fix for new Defensive Attacker position
		if (fo.getPosition()==ISpielerPosition.FORWARD_DEF && getSpezialitaet()==PlayerSpeciality.TECHNICAL) {
			psValue *= 1.30f;
		}

        float spValue = fo.getStandardsScaled(normalized) * RatingPredictionManager.calcPlayerStrength(this, PlayerSkill.SET_PIECES, useForm);

        float scValue = fo.getTorschussScaled(normalized) * RatingPredictionManager.calcPlayerStrength(this, PlayerSkill.SCORING, useForm);

        float val = gkValue + pmValue + deValue + wiValue + psValue + spValue + scValue;

        // Put to cache
        starRatingCache.put(key, new Float(val));
//    	System.out.println ("Star rating put to cache, key="+key+", val="+val+", tablesize="+starRatingCache.size());
        return val;
    }

    /**
     * Calculate the player strength on a specific lineup position
     * with or without form
     *
     * @param pos		position from ISpielerPosition (TORWART.. POS_ZUS_INNENV)
     * @param useForm	consider form?
     *
     * @return 			the player strength on this position
     */
    public float calcPosValue(byte pos, boolean useForm) {
    	float es = -1.0f;
    	final FactorObject factor = FormulaFactors.instance().getPositionFactor(pos);

    	if(factor != null)
    		es = calcPosValue(factor, useForm);
    	 else{
    		 //	For Coach or factor not found return 0
    		 return 0.0f;
    	 }

        return core.util.Helper.round(es / 2.0f,
                                                       core.model.UserParameter.instance().anzahlNachkommastellen);
    }

    /**
     * Copy old player offset status.
     * Used by training, checks for skillup and resets subskill in that case
     *
     * @param old
     */
    public void copySubSkills(Spieler old) {
    	for (int skillType=0; skillType < PlayerSkill.EXPERIENCE; skillType++) {

    		if ((skillType == PlayerSkill.FORM) || (skillType == PlayerSkill.STAMINA)) {
    			continue;
    		}

    		if (!check4SkillUp(skillType, old)) {
    			setSubskill4Pos(skillType, old.getSubskill4PosAccurate(skillType));
    		} else {
    			setSubskill4Pos(skillType, 0);
    		}
    	}
    }

    /**
     * Copy the skills of old player.
     * Used by training
     *
     * @param old
     */
    public void copySkills(Spieler old) {

    	for (int skillType=0; skillType <= PlayerSkill.LOYALTY; skillType++) {
    		setValue4Skill4(skillType, old.getValue4Skill4(skillType));
    	}
    }


    /**
     * Performs the subskill reset needed at skill drop.
     *
     * @param SkillType The ID of the skill to perform drop on.
     */
    public void dropSubskills(int skillType) {
    	if (getValue4Skill4(skillType) > 0) {
			// non-existent has no subskill.
			setSubskill4Pos(skillType, 0.999f);

		} else {
			setSubskill4Pos(skillType, 0);
		}
	}


    //////////////////////////////////////////////////////////////////////////////////
    //equals
    /////////////////////////////////////////////////////////////////////////////////
    @Override
	public boolean equals(Object obj) {
        boolean equals = false;

        if (obj instanceof Spieler) {
            equals = ((Spieler) obj).getSpielerID() == m_iSpielerID;
        }

        return equals;
    }

    /**
     * prüft ob Skillup vorliegt
     */
    protected boolean check4SkillUp(int skill, Spieler oldPlayer) {
    	if ((oldPlayer != null) && (oldPlayer.getSpielerID() > 0))
        	return oldPlayer.getValue4Skill4(skill) < getValue4Skill4(skill);
        return false;
    }

    /**
     * Test for whether skilldown has occurred
     */
    public boolean check4SkillDown(int skill, Spieler oldPlayer) {
    	if (skill < PlayerSkill.EXPERIENCE)
    	if ((oldPlayer != null) && (oldPlayer.getSpielerID() > 0))
        	return oldPlayer.getValue4Skill4(skill) > getValue4Skill4(skill);
        return false;
    }

    /**
     * Does this player have a training block?
     * @return training block
     */
    public boolean hasTrainingBlock () {
    	return m_bTrainingBlock;
    }

    /**
     * Set the training block of this player (true/false)
     * @param isBlocked	new value
     */
    public void setTrainingBlock (boolean isBlocked) {
    	this.m_bTrainingBlock = isBlocked;
    }

}
