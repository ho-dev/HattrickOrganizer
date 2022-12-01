package core.model.player;

import core.constants.TrainingType;
import core.constants.player.PlayerSpeciality;
import core.constants.player.Speciality;
import core.db.AbstractTable;
import core.db.DBManager;
import core.model.*;
import core.model.match.MatchLineupTeam;
import core.model.enums.MatchType;
import core.model.match.Weather;
import core.net.OnlineWorker;
import core.rating.RatingPredictionManager;
import core.training.*;
import core.util.HODateTime;
import core.util.HOLogger;
import core.util.Helper;
import core.util.HelperWrapper;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;

import static java.lang.Integer.min;
import static core.constants.player.PlayerSkill.*;

public class Player extends AbstractTable.Storable {

    /**
     * Cache for player contribution (Hashtable<String, Float>)
     */
    private static final Hashtable<String, Object> PlayerAbsoluteContributionCache = new Hashtable<>();
    private static final Hashtable<String, Object> PlayerRelativeContributionCache = new Hashtable<>();
    private byte idealPos = IMatchRoleID.UNKNOWN;
    private static final String BREAK = "[br]";
    private static final String O_BRACKET = "[";
    private static final String C_BRACKET = "]";
    private static final String EMPTY = "";


    /**
     * Name
     */
    private String m_sFirstName = "";
    private String m_sNickName = "";
    private String m_sLastName = "";

    /**
     * Arrival in team
     */
    private String m_arrivalDate;

    /**
     * Download date
     */
    private HODateTime m_clhrfDate;

    /**
     * The player is no longer available in the current HRF
     */
    private boolean m_bOld;

    /**
     * Wing skill
     */
    private double m_dSubFluegelspiel;

    /**
     * Pass skill
     */
    private double m_dSubPasspiel;

    /**
     * Playmaking skill
     */
    private double m_dSubSpielaufbau;

    /**
     * Standards
     */
    private double m_dSubStandards;

    /**
     * Goal
     */
    private double m_dSubTorschuss;

    //Subskills
    private double m_dSubTorwart;

    /**
     * Verteidigung
     */
    private double m_dSubVerteidigung;

    /**
     * Agressivität
     */
    private int m_iAgressivitaet;

    /**
     * Alter
     */
    private int m_iAlter;

    /**
     * Age Days
     */
    private int m_iAgeDays;

    /**
     * Ansehen (ekel usw. )
     */
    private int m_iAnsehen = 1;

    /**
     * Bewertung
     */
    private int m_iBewertung;

    /**
     * charakter ( ehrlich)
     */
    private int m_iCharakter = 1;

    /**
     * Erfahrung
     */
    private int m_iErfahrung = 1;

    /**
     * Fluegelspiel
     */
    private int m_iFluegelspiel = 1;

    /**
     * Form
     */
    private int m_iForm = 1;

    /**
     * Führungsqualität
     */
    private int m_iFuehrung = 1;

    /**
     * Gehalt
     */
    private int m_iGehalt = 1;

    /**
     * Gelbe Karten
     */
    private int m_iCards;

    /**
     * Hattricks
     */
    private int m_iHattrick;

    private int m_iGoalsCurrentTeam;
    /**
     * Home Grown
     */
    private boolean m_bHomeGrown = false;

    /**
     * Kondition
     */
    private int m_iKondition = 1;

    /**
     * Länderspiele
     */
    private int m_iLaenderspiele;

    /**
     * Loyalty
     */
    private int m_iLoyalty = 0;

    /**
     * Markwert
     */
    private int m_iTSI;

    private String m_sNationality;

    /**
     * Aus welchem Land kommt der Player
     */
    private int m_iNationalitaet = 49;

    /**
     * Passpiel
     */
    private int m_iPasspiel = 1;

    /**
     * SpezialitätID
     */
    private int iPlayerSpecialty;

    /**
     * Spielaufbau
     */
    private int m_iSpielaufbau = 1;

    /**
     * SpielerID
     */
    private int m_iSpielerID;

    /**
     * Standards
     */
    private int m_iStandards = 1;

    /**
     * Tore Freundschaftspiel
     */
    private int m_iToreFreund;

    /**
     * Tore Gesamt
     */
    private int m_iToreGesamt;

    /**
     * Tore Liga
     */
    private int m_iToreLiga;

    /**
     * Tore Pokalspiel
     */
    private int m_iTorePokal;

    /**
     * Torschuss
     */
    private int m_iTorschuss = 1;

    /**
     * Torwart
     */
    private int m_iTorwart = 1;

    /**
     * Trainerfähigkeit
     */
    private int m_iTrainer;

    /**
     * Trainertyp
     */
    private TrainerType m_iTrainerTyp;

    /**
     * Transferlisted
     */
    private int m_iTransferlisted;

    /**
     * shirt number (can be edited in hattrick)
     */
    private int shirtNumber = -1;

    /**
     * player's category (can be edited in hattrick)
     */
    private PlayerCategory playerCategory;

    /**
     * player statement (can be edited in hattrick)
     */
    private String playerStatement;

    /**
     * Owner notes (can be edited in hattrick)
     */
    private String ownerNotes;

    /**
     * Länderspiele
     */
    private int m_iU20Laenderspiele;

    /**
     * Verletzt Wochen
     */
    private int m_iInjuryWeeks = -1;

    /**
     * Verteidigung
     */
    private int m_iVerteidigung = 1;

    /**
     * Training block
     */
    private boolean m_bTrainingBlock = false;

    /**
     * Last match
     */
    private String m_lastMatchDate;
    private Integer m_lastMatchId;
    private MatchType lastMatchType;
    private Integer lastMatchPosition;
    private Integer lastMatchMinutes;
    // Rating is number of half stars
    // real rating value is rating/2.0f
    private Integer m_lastMatchRating;
    private Integer lastMatchRatingEndOfGame;

    /**
     * specifying at what time –in minutes- that player entered the field
     * This parameter is only used by RatingPredictionManager to calculate the stamina effect
     * along the course of the game
     */
    private int GameStartingTime = 0;
    private Integer nationalTeamId;
    private double subExperience;

    /**
     * future training priorities planed by the user
     */
    private List<FuturePlayerTraining> futurePlayerTrainings;

    private Integer motherclubId;
    private String motherclubName;
    private Integer matchesCurrentTeam;
    private int hrf_id;

    public int getGameStartingTime() {
        return GameStartingTime;
    }

    public void setGameStartingTime(int gameStartingTime) {
        GameStartingTime = gameStartingTime;
    }

    //~ Constructors -------------------------------------------------------------------------------


    /**
     * Creates a new instance of Player
     */
    public Player() {
    }

    /**
     * Erstellt einen Player aus den Properties einer HRF Datei
     */
    public Player(java.util.Properties properties, HODateTime hrfdate, int hrf_id) {
        // Separate first, nick and last names are available. Utilize them?

        this.hrf_id=hrf_id;
        m_iSpielerID = Integer.parseInt(properties.getProperty("id", "0"));
        m_sFirstName = properties.getProperty("firstname", "");
        m_sNickName = properties.getProperty("nickname", "");
        m_sLastName = properties.getProperty("lastname", "");
        m_arrivalDate = properties.getProperty("arrivaldate");
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
        iPlayerSpecialty = Integer.parseInt(properties.getProperty("speciality", "0"));
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
        subExperience = Double.parseDouble(properties.getProperty("experiencesub", "0"));

        //TSI, alles vorher durch 1000 teilen
        m_clhrfDate = hrfdate;

        if (hrfdate.isBefore(HODateTime.fromDbTimestamp(DBManager.TSIDATE))) {
            m_iTSI /= 1000d;
        }

        m_iCards = Integer.parseInt(properties.getProperty("warnings", "0"));
        m_iInjuryWeeks = Integer.parseInt(properties.getProperty("ska", "0"));
        m_iToreFreund = Integer.parseInt(properties.getProperty("gtt", "0"));
        m_iToreLiga = Integer.parseInt(properties.getProperty("gtl", "0"));
        m_iTorePokal = Integer.parseInt(properties.getProperty("gtc", "0"));
        m_iToreGesamt = Integer.parseInt(properties.getProperty("gev", "0"));
        m_iHattrick = Integer.parseInt(properties.getProperty("hat", "0"));
        m_iGoalsCurrentTeam = Integer.parseInt(properties.getProperty("goalscurrentteam", "0"));
        matchesCurrentTeam = Integer.parseInt(properties.getProperty("matchescurrentteam", "0"));

        if (properties.get("rating") != null) {
            m_iBewertung = Integer.parseInt(properties.getProperty("rating", "0"));
        }

        String temp = properties.getProperty("trainertype", "-1");

        if ((temp != null) && !temp.equals("")) {
            m_iTrainerTyp = TrainerType.fromInt(Integer.parseInt(temp));
        }

        temp = properties.getProperty("trainerskill", "0");

        if ((temp != null) && !temp.equals("")) {
            m_iTrainer = Integer.parseInt(temp);
        }

        temp = properties.getProperty("playernumber", "");

        if ((temp != null) && !temp.equals("") && !temp.equals("null")) {
            shirtNumber = Integer.parseInt(temp);
        }

        m_iTransferlisted = Boolean.parseBoolean(properties.getProperty("transferlisted", "False")) ? 1 : 0;
        m_iLaenderspiele = Integer.parseInt(properties.getProperty("caps", "0"));
        m_iU20Laenderspiele = Integer.parseInt(properties.getProperty("capsU20", "0"));
        nationalTeamId = Integer.parseInt(properties.getProperty("nationalTeamID", "0"));

        // #461-lastmatch
        m_lastMatchDate = properties.getProperty("lastmatch_date");
        if (m_lastMatchDate != null && !m_lastMatchDate.isEmpty()) {
            m_lastMatchId = Integer.parseInt(properties.getProperty("lastmatch_id", "0"));
            lastMatchPosition = Integer.parseInt(properties.getProperty("lastmatch_positioncode", "-1"));
            lastMatchMinutes = Integer.parseInt(properties.getProperty("lastmatch_playedminutes", "0"));
            // rating is stored as number of half stars
            m_lastMatchRating = (int) (2 * Double.parseDouble(properties.getProperty("lastmatch_rating", "0")));
            lastMatchRatingEndOfGame = (int) (2 * Double.parseDouble(properties.getProperty("lastmatch_ratingendofgame", "0")));
        }

        setLastMatchType(MatchType.getById(
                Integer.parseInt(properties.getProperty("lastmatch_type", "0"))
        ));

        playerCategory = PlayerCategory.valueOf(Integer.parseInt(properties.getProperty("playercategoryid", "0")));
        playerStatement = properties.getProperty("statement", "");
        ownerNotes = properties.getProperty("ownernotes", "");

        //Subskills calculation
        //Called when saving the HRF because the necessary data is not available here
        final core.model.HOModel oldmodel = core.model.HOVerwaltung.instance().getModel();
        final Player oldPlayer = oldmodel.getCurrentPlayer(m_iSpielerID);
        if (oldPlayer != null) {
            // Training blocked (could be done in the past)
            m_bTrainingBlock = oldPlayer.hasTrainingBlock();
            motherclubId = oldPlayer.getMotherclubId();
            motherclubName = oldPlayer.getMotherclubName();
            if (motherclubId == null) {
                var playerDetails = OnlineWorker.downloadPlayerDetails(this.getPlayerID());
                if (playerDetails != null) {
                    motherclubId = playerDetails.getMotherclubId();
                    motherclubName = playerDetails.getMotherclubName();
                }
            }
        }
    }

    public String getMotherclubName() {
        return this.motherclubName;
    }

    public Integer getMotherclubId() {
        return this.motherclubId;
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
     * Setter for property m_iAlter.
     *
     * @param m_iAlter New value of property m_iAlter.
     */
    public void setAge(int m_iAlter) {
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
     * @param m_iAgeDays New value of property m_iAgeDays.
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
     * Calculates full age with days and offset
     *
     * @return Double value of age & agedays & offset combined,
     * i.e. age + (agedays+offset)/112
     */
    public double getAlterWithAgeDays() {
        var now = HODateTime.now();
        return getDoubleAgeFromDate(now);
    }

    /**
     * Calculates full age with days and offset for a given timestamp
     * used to sort columns
     * pay attention that it takes the hour and minute of the matchtime into account
     * if you only want the days between two days use method calendarDaysBetween(Calendar start, Calendar end)
     *
     * @return Double value of age & agedays & offset combined,
     * i.e. age + (agedays+offset)/112
     */
    public double getDoubleAgeFromDate(HODateTime t) {
        var hrfTime = HOVerwaltung.instance().getModel().getBasics().getDatum();
        var diff = Duration.between(hrfTime.instant, t.instant);
        int years = getAlter();
        int days = getAgeDays();
        return years + (double) (days + diff.toDays()) / 112;
    }

    /**
     * Calculates String for full age and days correcting for the difference between (now and last HRF file)
     *
     * @return String of age & agedays format is "YY (DDD)"
     */
    public String getAgeWithDaysAsString() {
        return getAgeWithDaysAsString(HODateTime.now());
    }
    public String getAgeWithDaysAsString(HODateTime t){
        return getAgeWithDaysAsString(this.getAlter(), this.getAgeDays(), t, this.m_clhrfDate);
    }

    /**
     * Calculates the player's age at date referencing the current hrf download
     * @param ageYears int player's age in years in current hrf download
     * @param ageDays int additional days
     * @param time HODateTime for which the player's age should be calculated
     * @return String
     */
    public static String getAgeWithDaysAsString(int ageYears, int ageDays, HODateTime time) {
        return getAgeWithDaysAsString(ageYears, ageDays,time, HOVerwaltung.instance().getModel().getBasics().getDatum());
    }

    /**
     * Calculates the player's age at date referencing the given hrf date
     * @param ageYears int player's age in years at reference time
     * @param ageDays int additional days
     * @param time HODateTime for which the player's age should be calculated
     * @param hrfTime HODateTime reference date, when player's age was given
     * @return String
     */
    public static String getAgeWithDaysAsString(int ageYears, int ageDays, HODateTime time, HODateTime hrfTime) {
        var age = new HODateTime.HODuration(ageYears, ageDays).plus(HODateTime.HODuration.between(hrfTime, time));
        return age.seasons + " (" + age.days + ")";
    }

    /**
     * Get the full i18n'd string representing the player's age. Includes
     * the birthday indicator as well.
     *
     * @return the full i18n'd string representing the player's age
     */
    public String getAgeStringFull() {
        var hrfTime = HOVerwaltung.instance().getModel().getBasics().getDatum();
        var oldAge = new HODateTime.HODuration(this.getAlter(), this.getAgeDays());
        var age = oldAge.plus(HODateTime.HODuration.between(hrfTime, HODateTime.now()));
        var birthday = oldAge.seasons != age.seasons;
        StringBuilder ret = new StringBuilder();
        ret.append(age.seasons);
        ret.append(" ");
        ret.append(HOVerwaltung.instance().getLanguageString("ls.player.age.years"));
        ret.append(" ");
        ret.append(age.days);
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
    public int getRating() {
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

    public String getArrivalDate() {
        return m_arrivalDate;
    }

    public void setArrivalDate(String m_arrivalDate) {
        this.m_arrivalDate = m_arrivalDate;
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
    public void setExperience(int m_iErfahrung) {
        this.m_iErfahrung = m_iErfahrung;
    }

    /**
     * Getter for property m_iErfahrung.
     *
     * @return Value of property m_iErfahrung.
     */
    public int getExperience() {
        return m_iErfahrung;
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
    public int getWIskill() {
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
    public void setLeadership(int m_iFuehrung) {
        this.m_iFuehrung = m_iFuehrung;
    }

    /**
     * Getter for property m_iFuehrung.
     *
     * @return Value of property m_iFuehrung.
     */
    public int getLeadership() {
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
    public int getSalary() {
        return m_iGehalt;
    }

    /**
     * Setter for property m_iGelbeKarten.
     *
     * @param m_iGelbeKarten New value of property m_iGelbeKarten.
     */
    public void setGelbeKarten(int m_iGelbeKarten) {
        this.m_iCards = m_iGelbeKarten;
    }

    /**
     * Getter for property m_iGelbeKarten.
     *
     * @return Value of property m_iGelbeKarten.
     */
    public int getCards() {
        return m_iCards;
    }

    /**
     * gibt an ob der spieler gesperrt ist
     */
    public boolean isRedCarded() {
        return (m_iCards > 2);
    }


    public void setHattrick(int m_iHattrick) {
        this.m_iHattrick = m_iHattrick;
    }


    public int getHattrick() {
        return m_iHattrick;
    }


    public int getGoalsCurrentTeam() {
        return m_iGoalsCurrentTeam;
    }

    public void setGoalsCurrentTeam(int m_iGoalsCurrentTeam) {
        this.m_iGoalsCurrentTeam = m_iGoalsCurrentTeam;
    }

    /**
     * Setter for m_bHomeGrown
     *
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

    public HODateTime getHrfDate() {
        if ( m_clhrfDate == null){
            m_clhrfDate = HOVerwaltung.instance().getModel().getBasics().getDatum();
        }
        return m_clhrfDate;
    }

    public void setHrfDate(HODateTime timestamp) {
        m_clhrfDate = timestamp;
    }

    public void setHrfDate() {
        setHrfDate(HODateTime.now());
    }

    /**
     * calculate the contribution for the ideal position
     */
    public float getIdealPositionStrength(boolean mitForm, @Nullable Weather weather, boolean useWeatherImpact) {
        return getIdealPositionStrength(mitForm, false, weather, useWeatherImpact);
    }


    /**
     * calculate the contribution for the ideal position
     */
    public float getIdealPositionStrength(boolean mitForm, boolean normalized, @Nullable Weather weather, boolean useWeatherImpact) {
        return getIdealPositionStrength(mitForm, normalized, 2, weather, useWeatherImpact);
    }

    /**
     * calculate the contribution for the ideal position
     */
    public float getIdealPositionStrength(boolean mitForm, boolean normalized, int nb_decimal, @Nullable Weather weather, boolean useWeatherImpact) {
        return calcPosValue(getIdealPosition(), mitForm, normalized, nb_decimal, weather, useWeatherImpact);
    }



    /**
     * Calculate Player Ideal Position (weather impact not relevant here)
     */
    public byte getIdealPosition() {
        //in case player best position is forced by user
        final int flag = getUserPosFlag();

        if (flag == IMatchRoleID.UNKNOWN) {
            if (idealPos == IMatchRoleID.UNKNOWN) {
                final FactorObject[] allPos = FormulaFactors.instance().getAllObj();
                float maxStk = -1.0f;
                byte currPosition;
                float contrib;

                for (int i = 0; (allPos != null) && (i < allPos.length); i++) {
                    if (allPos[i].getPosition() == IMatchRoleID.FORWARD_DEF_TECH) continue;
                    currPosition = allPos[i].getPosition();
                    contrib = calcPosValue(currPosition, true, true, null, false);
                    if (contrib > maxStk) {
                        maxStk = contrib;
                        idealPos = currPosition;
                    }
                }
            }
            return idealPos;
        }

        return (byte)flag;
    }

    /**
     * Calculate Player Alternative Best Positions (weather impact not relevant here)
     */
    public byte[] getAlternativeBestPositions() {

        List<PositionContribute> positions = new ArrayList<>();
        final FactorObject[] allPos = FormulaFactors.instance().getAllObj();
        byte currPosition;
        PositionContribute currPositionContribute;

        for (int i = 0; (allPos != null) && (i < allPos.length); i++) {
            if (allPos[i].getPosition() == IMatchRoleID.FORWARD_DEF_TECH) continue;
            currPosition = allPos[i].getPosition();
            currPositionContribute = new PositionContribute(calcPosValue(currPosition, true, true, null, false), currPosition);
            positions.add(currPositionContribute);
        }

        positions.sort((PositionContribute player1, PositionContribute player2) -> Float.compare(player2.getRating(), player1.getRating()));

        byte[] alternativePositions = new byte[positions.size()];
        float tolerance = 1f - core.model.UserParameter.instance().alternativePositionsTolerance;

        int i;
        final float threshold = positions.get(0).getRating() * tolerance;

        for (i = 0; i < positions.size(); i++) {
            if (positions.get(i).getRating() >= threshold) {
                alternativePositions[i] = positions.get(i).getClPostionID();
            } else {
                break;
            }
        }

        alternativePositions = Arrays.copyOf(alternativePositions, i);

        return alternativePositions;
    }

    /**
     * return whether or not the position is one of the best position for the player
     */
    public boolean isAnAlternativeBestPosition(byte position){
        return Arrays.asList(getAlternativeBestPositions()).contains(position);
    }


    /**
     * Setter for property m_iKondition.
     *
     * @param m_iKondition New value of property m_iKondition.
     */
    public void setStamina(int m_iKondition) {
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
    public int getStamina() {
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

    private final HashMap<Integer, Skillup> lastSkillups = new HashMap<>();
    /**
     * liefert das Datum des letzen LevelAufstiegs für den angeforderten Skill [0] = Time der
     * Änderung [1] = Boolean: false=Keine Änderung gefunden
     */
    public Skillup getLastLevelUp(int skill) {
        if ( lastSkillups.containsKey(skill)){
            return lastSkillups.get(skill);
        }
        var ret =  DBManager.instance().getLastLevelUp(skill, m_iSpielerID);
        lastSkillups.put(skill, ret);
        return ret;
    }

    private final HashMap<Integer, List<Skillup> > allSkillUps = new HashMap<>();
    /**
     * gives information of skill ups
     */
    public List<Skillup> getAllLevelUp(int skill) {
        if ( allSkillUps.containsKey(skill)) {
            return allSkillUps.get(skill);
        }
        var ret = DBManager.instance().getAllLevelUp(skill, m_iSpielerID);
        allSkillUps.put(skill, ret);
        return ret;
    }

    public void resetSkillUpInformation() {
        lastSkillups.clear();
        allSkillUps.clear();
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
    public void setLoyalty(int loy) {
        m_iLoyalty = loy;
    }


    /**
     * Setter for property m_sManuellerSmilie.
     *
     * @param manuellerSmilie New value of property m_sManuellerSmilie.
     */
    public void setManuellerSmilie(java.lang.String manuellerSmilie) {
        getNotes().setManuelSmilie(manuellerSmilie);
        DBManager.instance().storePlayerNotes(notes);
    }

    /**
     * Getter for property m_sManuellerSmilie.
     *
     * @return Value of property m_sManuellerSmilie.
     */
    public java.lang.String getInfoSmiley() {
        return getNotes().getManuelSmilie();
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

    public void setFirstName(java.lang.String m_sName) {
        if ( m_sName != null ) this.m_sFirstName = m_sName;
        else m_sFirstName = "";
    }

    public java.lang.String getFirstName() {
        return m_sFirstName;
    }

    public void setNickName(java.lang.String m_sName) {
        if ( m_sName != null ) this.m_sNickName = m_sName;
        else m_sNickName = "";
    }

    public java.lang.String getNickName() {
        return m_sNickName;
    }

    public void setLastName(java.lang.String m_sName) {
        if (m_sName != null ) this.m_sLastName = m_sName;
        else this.m_sLastName = "";
    }

    public java.lang.String getLastName() {
        return m_sLastName;
    }



    /**
     * Getter for shortName
     * eg: James Bond = J. Bond
     * Nickname are ignored
     */
    public String getShortName() {

        if (getFirstName().isEmpty())
        {
            return getLastName();
        }
        return getFirstName().charAt(0) + ". " + getLastName();

    }


    public java.lang.String getFullName() {

        if (getNickName().isEmpty())
        {
            return getFirstName() + " " +getLastName();
        }

        return getFirstName() + " '" + getNickName() + "' " +getLastName();
    }

    /**
     * Setter for property m_iNationalitaet.
     *
     * @param m_iNationalitaet New value of property m_iNationalitaet.
     */
    public void setNationalityAsInt(int m_iNationalitaet) {
        this.m_iNationalitaet = m_iNationalitaet;
    }

    /**
     * Getter for property m_iNationalitaet.
     *
     * @return Value of property m_iNationalitaet.
     */
    public int getNationalityAsInt() {
        return m_iNationalitaet;
    }


    public String getNationalityAsString() {
        if (m_sNationality != null){
            return m_sNationality;
        }
        WorldDetailLeague leagueDetail = WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(m_iNationalitaet);
        if ( leagueDetail != null ) {
            m_sNationality = leagueDetail.getCountryName();
        }
        else{
            m_sNationality = "";
        }
        return  m_sNationality;
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
    public int getPSskill() {
        return m_iPasspiel;
    }

    /**
     * Zum speichern! Die Reduzierung des Marktwerts auf TSI wird rückgängig gemacht
     */
    public int getMarktwert() {
        if (m_clhrfDate == null || m_clhrfDate.isBefore(HODateTime.fromDbTimestamp(DBManager.TSIDATE))) {
            //Echter Marktwert
            return m_iTSI * 1000;
        }

        //TSI
        return m_iTSI;
    }


    String latestTSIInjured;
    String latestTSINotInjured;
    public String getLatestTSINotInjured(){
        if (latestTSINotInjured == null){
            latestTSINotInjured = DBManager.instance().loadLatestTSINotInjured(m_iSpielerID);
        }
        return latestTSINotInjured;
    }
    public String getLatestTSIInjured(){
        if (latestTSIInjured == null){
            latestTSIInjured = DBManager.instance().loadLatestTSIInjured(m_iSpielerID);
        }
        return latestTSIInjured;
    }

    /**
     * Setter for property iPlayerSpecialty.
     *
     * @param iPlayerSpecialty New value of property iPlayerSpecialty.
     */
    public void setPlayerSpecialty(int iPlayerSpecialty) {
        this.iPlayerSpecialty = iPlayerSpecialty;
    }

    /**
     * Getter for property iPlayerSpecialty.
     *
     * @return Value of property iPlayerSpecialty.
     */
    public int getPlayerSpecialty() {
        return iPlayerSpecialty;
    }

    public boolean hasSpeciality(Speciality speciality)
    {
        Speciality s = Speciality.values()[iPlayerSpecialty];
        return s.equals(speciality);
    }

    // returns the name of the speciality in the used language
    public String getSpecialityName() {
        Speciality s = Speciality.values()[iPlayerSpecialty];
        if (s.equals(Speciality.NO_SPECIALITY)) {
            return EMPTY;
        } else {
            return HOVerwaltung.instance().getLanguageString("ls.player.speciality." + s.toString().toLowerCase(Locale.ROOT));
        }
    }

    // return the name of the speciality with a break before and in brackets
    // e.g. [br][quick], used for HT-ML export
    public String getSpecialityExportName() {
        Speciality s = Speciality.values()[iPlayerSpecialty];
        if (s.equals(Speciality.NO_SPECIALITY)) {
            return EMPTY;
        } else {
            return BREAK + O_BRACKET + getSpecialityName() + C_BRACKET;
        }
    }

    // no break so that the export looks better
    public String getSpecialityExportNameForKeeper() {
        Speciality s = Speciality.values()[iPlayerSpecialty];
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
    public int getPMskill() {
        return m_iSpielaufbau;
    }

    /**
     * set whether or not that player can be selected by the assistant
     */
    public void setCanBeSelectedByAssistant(boolean flag) {
        getNotes().setEligibleToPlay(flag);
        DBManager.instance().storePlayerNotes(notes);
    }

    /**
     * get whether or not that player can be selected by the assistant
     */
    public boolean getCanBeSelectedByAssistant() {
        return getNotes().isEligibleToPlay();
    }

    /**
     * Setter for property m_iSpielerID.
     *
     * @param m_iSpielerID New value of property m_iSpielerID.
     */
    public void setPlayerID(int m_iSpielerID) {
        this.m_iSpielerID = m_iSpielerID;
    }

    /**
     * Getter for property m_iSpielerID.
     *
     * @return Value of property m_iSpielerID.
     */
    public int getPlayerID() {
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
    public int getSPskill() {
        return m_iStandards;
    }

    /**
     * berechnet den Subskill pro position
     */
    public float getSub4Skill(int skill) {
        return Math.min(0.99f, Helper.round(getSub4SkillAccurate(skill), 2));
    }

    public float getSkill(int iSkill, boolean inclSubSkill) {
        if(inclSubSkill) {
            return getValue4Skill(iSkill) + getSub4Skill(iSkill);
        }
        else{
            return getValue4Skill(iSkill);

        }
    }


    /**
     * Returns accurate subskill number. If you need subskill for UI
     * purpose it is better to use getSubskill4Pos()
     *
     * @param skill skill number
     * @return subskill between 0.0-0.999
     */
    public float getSub4SkillAccurate(int skill) {
        double value = switch (skill) {
            case KEEPER -> m_dSubTorwart;
            case PLAYMAKING -> m_dSubSpielaufbau;
            case DEFENDING -> m_dSubVerteidigung;
            case PASSING -> m_dSubPasspiel;
            case WINGER -> m_dSubFluegelspiel;
            case SCORING -> m_dSubTorschuss;
            case SET_PIECES -> m_dSubStandards;
            case EXPERIENCE -> subExperience;
            default -> 0;
        };

        return (float) Math.min(0.999, value);
    }

    public void setSubskill4PlayerSkill(int skill, float value) {
        switch (skill) {
            case KEEPER -> m_dSubTorwart = value;
            case PLAYMAKING -> m_dSubSpielaufbau = value;
            case DEFENDING -> m_dSubVerteidigung = value;
            case PASSING -> m_dSubPasspiel = value;
            case WINGER -> m_dSubFluegelspiel = value;
            case SCORING -> m_dSubTorschuss = value;
            case SET_PIECES -> m_dSubStandards = value;
            case EXPERIENCE -> subExperience = value;
        }
    }

    /**
     * Setter for property m_sTeamInfoSmilie.
     *
     * @param teamInfoSmilie New value of property m_sTeamInfoSmilie.
     */
    public void setTeamInfoSmilie(String teamInfoSmilie) {
        getNotes().setTeamInfoSmilie(teamInfoSmilie);
        DBManager.instance().storePlayerNotes(notes);
    }

    /**
     * Getter for property m_sTeamInfoSmilie.
     *
     * @return Value of property m_sTeamInfoSmilie.
     */
    public String getTeamGroup() {
        var ret = getNotes().getTeamInfoSmilie();
        return ret.replaceAll("\\.png$", "");
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
    public void setAllOfficialGoals(int m_iToreGesamt) {
        this.m_iToreGesamt = m_iToreGesamt;
    }

    /**
     * Getter for property m_iToreGesamt.
     *
     * @return Value of property m_iToreGesamt.
     */
    public int getAllOfficialGoals() {
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
    public int getSeasonSeriesGoal() {
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
    public int getSeasonCupGoal() {
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
    public int getSCskill() {
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
    public int getGKskill() {
        return m_iTorwart;
    }

    /**
     * Setter for property m_iTrainer.
     *
     * @param m_iTrainer New value of property m_iTrainer.
     */
    public void setTrainerSkill(Integer m_iTrainer) {
        this.m_iTrainer = m_iTrainer;
    }

    /**
     * Getter for property m_iTrainer.
     *
     * @return Value of property m_iTrainer.
     */
    public int getTrainerSkill() {
        return m_iTrainer;
    }

    /**
     * gibt an ob der Player Trainer ist
     */
    public boolean isTrainer() {
        return m_iTrainer > 0 && m_iTrainerTyp != null;
    }

    /**
     * Setter for property m_iTrainerTyp.
     *
     * @param m_iTrainerTyp New value of property m_iTrainerTyp.
     */
    public void setTrainerTyp(TrainerType m_iTrainerTyp) {
        this.m_iTrainerTyp = m_iTrainerTyp;
    }

    /**
     * Getter for property m_iTrainerTyp.
     *
     * @return Value of property m_iTrainerTyp.
     */
    public TrainerType getTrainerTyp() {
        return m_iTrainerTyp;
    }

    /**
     * Last match
     * @return date
     */
    public String getLastMatchDate(){
        return m_lastMatchDate;
    }

    /**
     * Last match
     * @return rating
     */
    public Integer getLastMatchRating(){
        return m_lastMatchRating;
    }

    /**
     * Last match id
     * @return id
     */
    public Integer getLastMatchId(){
        return m_lastMatchId;
    }

    /**
     * Returns the {@link MatchType} of the last match.
     */
    public MatchType getLastMatchType() {
        return lastMatchType;
    }

    /**
     * Sets the value of <code>lastMatchType</code> to <code>matchType</code>.
     */
    public void setLastMatchType(MatchType matchType) {
        this.lastMatchType = matchType;
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
    public void setShirtNumber(int m_iTrikotnummer) {
        this.shirtNumber = m_iTrikotnummer;
    }

    /**
     * Getter for property m_iTrikotnummer.
     *
     * @return Value of property m_iTrikotnummer.
     */
    public int getTrikotnummer() {
        return shirtNumber;
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

    public void setHrfId(int hrf_id) {
        this.hrf_id=hrf_id;
    }

    public int getHrfId() {
        return this.hrf_id;
    }

    public void setLastMatchDate(String v) {
        this.m_lastMatchDate = v;
    }

    public void setLastMatchRating(Integer v) {
        this.m_lastMatchRating=v;
    }

    public void setLastMatchId(Integer v) {
        this.m_lastMatchId = v;
    }

    public static class Notes extends  AbstractTable.Storable{

        public Notes(){}

        private int playerId;

        public Notes(int playerId) {
            this.playerId = playerId;
        }

        public int getUserPos() {
            return userPos;
        }

        private int userPos = IMatchRoleID.UNKNOWN;

        public String getManuelSmilie() {
            return manuelSmilie;
        }

        public String getNote() {
            return note;
        }

        public boolean isEligibleToPlay() {
            return eligibleToPlay;
        }

        public String getTeamInfoSmilie() {
            return teamInfoSmilie;
        }

        public boolean isFired() {
            return isFired;
        }

        private String manuelSmilie="";
        private String note="";
        private boolean eligibleToPlay=true;
        private String teamInfoSmilie="";
        private boolean isFired=false;

        public void setPlayerId(int playerId) {
            this.playerId=playerId;
        }

        public void setNote(String note) {
            this.note=note;
        }

        public void setEligibleToPlay(boolean spielberechtigt) {
            this.eligibleToPlay=spielberechtigt;
        }

        public void setTeamInfoSmilie(String teamInfoSmilie) {
            this.teamInfoSmilie=teamInfoSmilie;
        }

        public void setManuelSmilie(String manuellerSmilie) {
            this.manuelSmilie=manuellerSmilie;
        }

        public void setUserPos(int userPos) {
            this.userPos=userPos;
        }

        public void setIsFired(boolean isFired) {
            this.isFired=isFired;
        }

        public int getPlayerId() {
            return this.playerId;
        }
    }
    private Notes notes;
    private Notes getNotes(){
        if ( notes==null){
            notes = DBManager.instance().loadPlayerNotes(this.getPlayerID());
        }
        return notes;
    }
    public void setUserPosFlag(byte flag) {
        getNotes().setUserPos(flag);
        DBManager.instance().storePlayerNotes(notes);
        this.setCanBeSelectedByAssistant(flag != IMatchRoleID.UNSELECTABLE);
    }
    public void setIsFired(boolean b) {
        getNotes().setIsFired(b);
        DBManager.instance().storePlayerNotes(notes);
    }

    public boolean isFired() {
        return getNotes().isFired();
    }

    /**
     * liefert User Notiz zum Player
     */
    public int getUserPosFlag() {
        return  getNotes().getUserPos();
    }

    public String getNote() {return getNotes().getNote();}
    public void setNote(String text) {
        getNotes().setNote(text);
        DBManager.instance().storePlayerNotes(notes);
    }


    /**
     * get Skillvalue 4 skill
     */
    public int getValue4Skill(int skill) {
        return switch (skill) {
            case KEEPER -> m_iTorwart;
            case PLAYMAKING -> m_iSpielaufbau;
            case DEFENDING -> m_iVerteidigung;
            case PASSING -> m_iPasspiel;
            case WINGER -> m_iFluegelspiel;
            case SCORING -> m_iTorschuss;
            case SET_PIECES -> m_iStandards;
            case STAMINA -> m_iKondition;
            case EXPERIENCE -> m_iErfahrung;
            case FORM -> m_iForm;
            case LEADERSHIP -> m_iFuehrung;
            case LOYALTY -> m_iLoyalty;
            default -> 0;
        };
    }


    public float getSkillValue(int skill){
        return getSub4Skill(skill) + getValue4Skill(skill);
    }
    public void setSkillValue(int skill, float value){
        int intVal = (int)value;
        setValue4Skill(skill, intVal);
        setSubskill4PlayerSkill(skill, value - intVal);
    }

    /**
     * set Skillvalue 4 skill
     *
     * @param skill the skill to change
     * @param value the new skill value
     */
    public void setValue4Skill(int skill, int value) {
        switch (skill) {
            case KEEPER -> setTorwart(value);
            case PLAYMAKING -> setSpielaufbau(value);
            case PASSING -> setPasspiel(value);
            case WINGER -> setFluegelspiel(value);
            case DEFENDING -> setVerteidigung(value);
            case SCORING -> setTorschuss(value);
            case SET_PIECES -> setStandards(value);
            case STAMINA -> setStamina(value);
            case EXPERIENCE -> setExperience(value);
            case FORM -> setForm(value);
            case LEADERSHIP -> setLeadership(value);
            case LOYALTY -> setLoyalty(value);
        }
    }


    /**
     * Setter for property m_iVerletzt.
     *
     * @param m_iVerletzt New value of property m_iVerletzt.
     */
    public void setInjuryWeeks(int m_iVerletzt) {
        this.m_iInjuryWeeks = m_iVerletzt;
    }

    /**
     * Getter for property m_iVerletzt.
     *
     * @return Value of property m_iVerletzt.
     */
    public int getInjuryWeeks() {
        return m_iInjuryWeeks;
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
    public int getDEFskill() {
        return m_iVerteidigung;
    }

    public float getImpactWeatherEffect(Weather weather) {
        return PlayerSpeciality.getImpactWeatherEffect(weather, iPlayerSpecialty);
    }

    /**
     * Calculates training effect for each skill
     *
     * @param train  Trainingweek giving the matches that should be calculated
     *
     * @return TrainingPerPlayer
     */
    public TrainingPerPlayer calculateWeeklyTraining(TrainingPerWeek train) {
        final int playerID = this.getPlayerID();
        TrainingPerPlayer ret = new TrainingPerPlayer(this);
        ret.setTrainingWeek(train);
        if (train == null || train.getTrainingType() < 0) {
            return ret;
        }

        WeeklyTrainingType wt = WeeklyTrainingType.instance(train.getTrainingType());
        if (wt != null) {
            try {
                var matches = train.getMatches();
                int myID = HOVerwaltung.instance().getModel().getBasics().getTeamId();
                TrainingWeekPlayer tp = new TrainingWeekPlayer(this);
                for (var match : matches) {
                    var details = match.getMatchdetails();
                    if ( details != null ) {
                        //Get the MatchLineup by id
                        MatchLineupTeam mlt = details.getOwnTeamLineup();
                        if ( mlt != null) {
                            MatchType type = mlt.getMatchType();
                            boolean walkoverWin = details.isWalkoverMatchWin(myID);
                            if (type != MatchType.MASTERS) { // MASTERS counts only for experience
                                tp.addFullTrainingMinutes(mlt.getTrainingMinutesPlayedInSectors(playerID, wt.getFullTrainingSectors(), walkoverWin));
                                tp.addBonusTrainingMinutes(mlt.getTrainingMinutesPlayedInSectors(playerID, wt.getBonusTrainingSectors(), walkoverWin));
                                tp.addPartlyTrainingMinutes(mlt.getTrainingMinutesPlayedInSectors(playerID, wt.getPartlyTrainingSectors(), walkoverWin));
                                tp.addOsmosisTrainingMinutes(mlt.getTrainingMinutesPlayedInSectors(playerID, wt.getOsmosisTrainingSectors(), walkoverWin));
                            }
                            var minutes = mlt.getTrainingMinutesPlayedInSectors(playerID, null, walkoverWin);
                            tp.addPlayedMinutes(minutes);
                            ret.addExperience(match.getExperienceIncrease(min(90, minutes)));
                        }
                        else {
                            HOLogger.instance().error(getClass(), "no lineup found in match " + match.getMatchSchedule().toLocaleDateTime() +
                                    " " + match.getHomeTeamName() + " - " + match.getGuestTeamName()
                            );
                        }
                    }
                }
                TrainingPoints trp = new TrainingPoints(wt, tp);

                // get experience increase of national team matches
                var id = this.getNationalTeamID();
                if  ( id != null && id != 0 && id != myID){
                    // TODO check if national matches are stored in database
                    var nationalMatches = train.getNTmatches();
                    for (var match : nationalMatches){
                        MatchLineupTeam mlt = DBManager.instance().loadMatchLineupTeam(match.getMatchType().getId(), match.getMatchID(), this.getNationalTeamID());
                        var minutes = mlt.getTrainingMinutesPlayedInSectors(playerID, null, false);
                        if ( minutes > 0 ) {
                            ret.addExperience(match.getExperienceIncrease(min(90,minutes)));
                        }
                    }
                }
                ret.setTrainingPair(trp);
            } catch (Exception e) {
                HOLogger.instance().log(getClass(),e);
            }
        }
        return ret;
    }

    /**
     * Calculate the player strength on a specific lineup position
     * with or without form
     *
     * @param fo         FactorObject with the skill weights for this position
     * @param useForm    consider form?
     * @param normalized absolute or normalized contribution?
     * @return the player strength on this position
     */
    float calcPosValue(FactorObject fo, boolean useForm, boolean normalized, @Nullable Weather weather, boolean useWeatherImpact) {
        if ((fo == null) || (fo.getSum() == 0.0f)) {
            return -1.0f;
        }

        // The stars formulas are changed by the user -> clear the cache
        if (!PlayerAbsoluteContributionCache.containsKey("lastChange") || ((Date) PlayerAbsoluteContributionCache.get("lastChange")).before(FormulaFactors.getLastChange())) {
//    		System.out.println ("Clearing stars cache");
            PlayerAbsoluteContributionCache.clear();
            PlayerRelativeContributionCache.clear();
            PlayerAbsoluteContributionCache.put("lastChange", new Date());
        }
        /*
         * Create a key for the Hashtable cache
         * We cache every star rating to speed up calculation
         * (calling RPM.calcPlayerStrength() is quite expensive and this method is used very often)
         */

        float loy = RatingPredictionManager.getLoyaltyHomegrownBonus(this);

        String key = fo.getPosition() + ":"
                + Helper.round(getGKskill() + getSub4Skill(KEEPER) + loy, 2) + "|"
                + Helper.round(getPMskill() + getSub4Skill(PLAYMAKING) + loy, 2) + "|"
                + Helper.round(getDEFskill() + getSub4Skill(DEFENDING) + loy, 2) + "|"
                + Helper.round(getWIskill() + getSub4Skill(WINGER) + loy, 2) + "|"
                + Helper.round(getPSskill() + getSub4Skill(PASSING) + loy, 2) + "|"
                + Helper.round(getSPskill() + getSub4Skill(SET_PIECES) + loy, 2) + "|"
                + Helper.round(getSCskill() + getSub4Skill(SCORING) + loy, 2) + "|"
                + getForm() + "|"
                + getStamina() + "|"
                + getExperience() + "|"
                + getPlayerSpecialty(); // used for Technical DefFW

        // Check if the key already exists in cache
        if (PlayerAbsoluteContributionCache.containsKey(key)) {
            // System.out.println ("Using star rating from cache, key="+key+", tablesize="+starRatingCache.size());

            float rating = normalized ? (float) PlayerRelativeContributionCache.get(key) : (Float) PlayerAbsoluteContributionCache.get(key);
            if(useWeatherImpact){
                rating *= getImpactWeatherEffect(weather);
            }

            return rating;
        }

        // Compute contribution
        float gkValue = fo.getGKfactor() * RatingPredictionManager.calcPlayerStrength(-2, this, KEEPER, useForm, false);
        float pmValue = fo.getPMfactor() * RatingPredictionManager.calcPlayerStrength(-2, this, PLAYMAKING, useForm, false);
        float deValue = fo.getDEfactor() * RatingPredictionManager.calcPlayerStrength(-2, this, DEFENDING, useForm, false);
        float wiValue = fo.getWIfactor() * RatingPredictionManager.calcPlayerStrength(-2, this, WINGER, useForm, false);
        float psValue = fo.getPSfactor() * RatingPredictionManager.calcPlayerStrength(-2, this, PASSING, useForm, false);
        float spValue = fo.getSPfactor() * RatingPredictionManager.calcPlayerStrength(-2, this, SET_PIECES, useForm, false);
        float scValue = fo.getSCfactor() * RatingPredictionManager.calcPlayerStrength(-2, this, SCORING, useForm, false);
        float val = gkValue + pmValue + deValue + wiValue + psValue + spValue + scValue;

        float absVal = val * 10; // multiplied by 10 for improved visibility
        float normVal = val / fo.getNormalizationFactor() * 100;  // scaled between 0 and 100%

        // Put to cache
        PlayerAbsoluteContributionCache.put(key, absVal);
        PlayerRelativeContributionCache.put(key, normVal);

//    	System.out.println ("Star rating put to cache, key="+key+", val="+val+", tablesize="+starRatingCache.size());
        if (normalized) {
            return normVal;
        } else {
            return absVal;
        }
    }


    public float calcPosValue(byte pos, boolean useForm, boolean normalized, @Nullable Weather weather, boolean useWeatherImpact) {
        return calcPosValue(pos, useForm, normalized, core.model.UserParameter.instance().nbDecimals, weather, useWeatherImpact);
    }

    public float calcPosValue(byte pos, boolean useForm, @Nullable Weather weather, boolean useWeatherImpact) {
        return calcPosValue(pos, useForm, false, weather, useWeatherImpact);
    }

    /**
     * Calculate the player strength on a specific lineup position
     * with or without form
     *
     * @param pos     position from IMatchRoleID (TORWART.. POS_ZUS_INNENV)
     * @param useForm consider form?
     * @return the player strength on this position
     */
    public float calcPosValue(byte pos, boolean useForm, boolean normalized, int nb_decimals,  @Nullable Weather weather, boolean useWeatherImpact) {
        float es;
        FactorObject factor = FormulaFactors.instance().getPositionFactor(pos);

        // Fix for TDF
        if (pos == IMatchRoleID.FORWARD_DEF && this.getPlayerSpecialty() == PlayerSpeciality.TECHNICAL) {
            factor = FormulaFactors.instance().getPositionFactor(IMatchRoleID.FORWARD_DEF_TECH);
        }

        if (factor != null) {
            es = calcPosValue(factor, useForm, normalized, weather, useWeatherImpact);
        } else {
            //	For Coach or factor not found return 0
            return 0.0f;
        }

        return core.util.Helper.round(es, nb_decimals);
    }


    /**
     * Copy the skills of old player.
     * Used by training
     *
     * @param old player to copy from
     */
    public void copySkills(Player old) {

        for (int skillType = 0; skillType <= LOYALTY; skillType++) {
            setValue4Skill(skillType, old.getValue4Skill(skillType));
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    //equals
    /////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;

        if (obj instanceof Player) {
            equals = ((Player) obj).getPlayerID() == m_iSpielerID;
        }

        return equals;
    }

    /**
     * Does this player have a training block?
     *
     * @return training block
     */
    public boolean hasTrainingBlock() {
        return m_bTrainingBlock;
    }

    /**
     * Set the training block of this player (true/false)
     *
     * @param isBlocked new value
     */
    public void setTrainingBlock(boolean isBlocked) {
        this.m_bTrainingBlock = isBlocked;
    }

    public Integer getNationalTeamID() {
        return nationalTeamId;
    }

    public void setNationalTeamId( Integer id){
        this.nationalTeamId=id;
    }

    public double getSubExperience() {
        return this.subExperience;
    }

    public void setSubExperience( Double experience){
        if ( experience != null ) this.subExperience = experience;
        else this.subExperience=0;
    }

    public List<FuturePlayerTraining> getFuturePlayerTrainings(){
        if ( futurePlayerTrainings == null){
            futurePlayerTrainings = DBManager.instance().getFuturePlayerTrainings(this.getPlayerID());
            if (futurePlayerTrainings.size()>0) {
                var start = HOVerwaltung.instance().getModel().getBasics().getHattrickWeek();
                var remove = new ArrayList<FuturePlayerTraining>();
                for (var t : futurePlayerTrainings) {
                    if (t.endsBefore(start)){
                        remove.add(t);
                    }
                }
                futurePlayerTrainings.removeAll(remove);
            }
        }
        return futurePlayerTrainings;
    }

    /**
     * Get the training priority of a hattrick week. If user training plan is given for the week this user selection is
     * returned. If no user plan is available, the training priority is determined by the player's best position.
     *
     * @param wt
     *  used to get priority depending from the player's best position.
     * @param trainingDate
     *  the training week
     * @return
     *  the training priority
     */
    public FuturePlayerTraining.Priority getTrainingPriority(WeeklyTrainingType wt, HODateTime trainingDate) {
        for ( var t : getFuturePlayerTrainings()) {
            if (t.contains(trainingDate)) {
                return t.getPriority();
            }
        }

        // get Prio from best position
        int position = HelperWrapper.instance().getPosition(this.getIdealPosition());

        for ( var p: wt.getTrainingSkillBonusPositions()){
            if ( p == position) return FuturePlayerTraining.Priority.FULL_TRAINING;
        }
        for ( var p: wt.getTrainingSkillPositions()){
            if ( p == position) {
                if ( wt.getTrainingType() == TrainingType.SET_PIECES) return FuturePlayerTraining.Priority.PARTIAL_TRAINING;
                return FuturePlayerTraining.Priority.FULL_TRAINING;
            }
        }
        for ( var p: wt.getTrainingSkillPartlyTrainingPositions()){
            if ( p == position) return FuturePlayerTraining.Priority.PARTIAL_TRAINING;
        }
        for ( var p: wt.getTrainingSkillOsmosisTrainingPositions()){
            if ( p == position) return FuturePlayerTraining.Priority.OSMOSIS_TRAINING;
        }

        return null; // No training
    }

    /**
     * Set training priority for a time interval.
     * Previously saved trainings of this interval are overwritten or deleted.
     *  @param prio new training priority for the given time interval
     * @param from first week with new training priority
     * @param to last week with new training priority, null means open end
     */
    public void setFutureTraining(FuturePlayerTraining.Priority prio, HODateTime from, HODateTime to) {
        var removeIntervals = new ArrayList<FuturePlayerTraining>();
        for (var t : getFuturePlayerTrainings()) {
            if (t.cut(from, to) ||
                    t.cut(HODateTime.htStart, HOVerwaltung.instance().getModel().getBasics().getHattrickWeek())) {
                removeIntervals.add(t);
            }
        }
        futurePlayerTrainings.removeAll(removeIntervals);
        if (prio != null) {
            futurePlayerTrainings.add(new FuturePlayerTraining(this.getPlayerID(), prio, from, to));
        }
        DBManager.instance().storeFuturePlayerTrainings(futurePlayerTrainings);
    }

    public String getBestPositionInfo(@Nullable Weather weather, boolean useWeatherImpact) {
        return MatchRoleID.getNameForPosition(getIdealPosition())
                + " ("
                +  getIdealPositionStrength(true, true, 1, weather, useWeatherImpact)
                + "%)";
    }

    /**
     * training priority information of the training panel
     *
     * @param nextWeek training priorities after this week will be considered
     * @return if there is one user selected priority, the name of the priority is returned
     *  if there are more than one selected priorities, "individual priorities" is returned
     *  if is no user selected priority, the best position information is returned
     */
    public String getTrainingPriorityInformation(HODateTime nextWeek) {
        String ret=null;
        for ( var t : getFuturePlayerTrainings()) {
            //
            if ( !t.endsBefore(nextWeek)){
                if ( ret != null ){
                    ret = HOVerwaltung.instance().getLanguageString("trainpre.individual.prios");
                    break;
                }
                ret = t.getPriority().toString();
            }
        }
        if ( ret != null ) return ret;
        return getBestPositionInfo(null, false);

    }

    private static final int[] trainingSkills= { KEEPER, SET_PIECES, DEFENDING, SCORING, WINGER, PASSING, PLAYMAKING };

    /**
     * Calculates skill status of the player
     *
     * @param previousID Id of the previous download. Previous player status is loaded by this id.
     * @param trainingWeeks List of training week information
     */
    public void calcSubskills(int previousID, List<TrainingPerWeek> trainingWeeks) {

        var playerBefore = DBManager.instance().getSpieler(previousID).stream()
                .filter(i -> i.getPlayerID() == this.getPlayerID()).findFirst().orElse(null);
        if (playerBefore == null) {
            playerBefore = this.CloneWithoutSubskills();
        }
        // since we don't want to work with temp player objects we calculate skill by skill
        // whereas experience is calculated within the first skill
        boolean experienceSubDone = this.getExperience() > playerBefore.getExperience(); // Do not calculate sub on experience skill up
        var experienceSub = experienceSubDone ? 0 : playerBefore.getSubExperience(); // set sub to 0 on skill up
        for (var skill : trainingSkills) {
            var sub = playerBefore.getSub4Skill(skill);
            var valueBeforeTraining = playerBefore.getValue4Skill(skill);
            var valueAfterTraining = this.getValue4Skill(skill);

            if (trainingWeeks.size() > 0) {
                for (var training : trainingWeeks) {

                    var trainingPerPlayer = calculateWeeklyTraining(training);
                    if (trainingPerPlayer != null) {
                        if (!this.hasTrainingBlock()) {// player training is not blocked (blocking is no longer possible)
                            sub += trainingPerPlayer.calcSubskillIncrement(skill, valueBeforeTraining + sub, training.getTrainingDate());
                            if (valueAfterTraining > valueBeforeTraining) {
                                if (sub > 1) {
                                    sub -= 1.;
                                } else {
                                    sub = 0.f;
                                }
                            } else if (valueAfterTraining < valueBeforeTraining) {
                                if (sub < 0) {
                                    sub += 1.f;
                                } else {
                                    sub = .99f;
                                }
                            } else {
                                if (sub > 0.99f) {
                                    sub = 0.99f;
                                } else if (sub < 0f) {
                                    sub = 0f;
                                }
                            }
                            valueBeforeTraining = valueAfterTraining;
                        }

                        if (!experienceSubDone) {
                            var inc = trainingPerPlayer.getExperienceSub();
                            experienceSub += inc;
                            if (experienceSub > 0.99) experienceSub = 0.99;

                            var minutes = 0;
                            var tp =trainingPerPlayer.getTrainingPair();
                            if  ( tp != null){
                                minutes = tp.getTrainingDuration().getPlayedMinutes();
                            }
                            else {
                                HOLogger.instance().warning(getClass(), "no training info found");
                            }
                            HOLogger.instance().info(getClass(),
                                    "Training " + training.getTrainingDate().toLocaleDateTime() +
                                            "; Minutes= " + minutes +
                                            "; Experience increment of " + this.getFullName() +
                                            "; increment: " +  inc +
                                            "; new sub value=" + experienceSub
                            );
                        }
                    }
                }
                experienceSubDone = true;
            }

            if (valueAfterTraining < valueBeforeTraining) {
                sub = .99f;
            } else if (valueAfterTraining > valueBeforeTraining) {
                sub = 0;
                HOLogger.instance().error(getClass(), "skill up without training"); // missing training in database
            }

            this.setSubskill4PlayerSkill(skill, sub);
            this.setSubExperience(experienceSub);
        }
    }
    private Player CloneWithoutSubskills() {
        var ret = new Player();
        ret.setHrfId(this.hrf_id);
        ret.copySkills(this);
        ret.setPlayerID(getPlayerID());
        ret.setAge(getAlter());
        ret.setLastName(getLastName());
        return ret;
    }

    public PlayerCategory getPlayerCategory() {
        return playerCategory;
    }

    public void setPlayerCategory(PlayerCategory playerCategory) {
        this.playerCategory = playerCategory;
    }

    public String getPlayerStatement() {
        return playerStatement;
    }

    public void setPlayerStatement(String playerStatement) {
        this.playerStatement = playerStatement;
    }

    public String getOwnerNotes() {
        return ownerNotes;
    }

    public void setOwnerNotes(String ownerNotes) {
        this.ownerNotes = ownerNotes;
    }

    public Integer getLastMatchPosition() {
        return lastMatchPosition;
    }

    public void setLastMatchPosition(Integer lastMatchPosition) {
        this.lastMatchPosition = lastMatchPosition;
    }

    public Integer getLastMatchMinutes() {
        return lastMatchMinutes;
    }

    public void setLastMatchMinutes(Integer lastMatchMinutes) {
        this.lastMatchMinutes = lastMatchMinutes;
    }

    /**
     * Rating at end of game
     * @return Integer number of half rating stars
     */
    public Integer getLastMatchRatingEndOfGame() {
        return lastMatchRatingEndOfGame;
    }

    /**
     * Rating at end of game
     * @param lastMatchRatingEndOfGame number of half rating stars
     */
    public void setLastMatchRatingEndOfGame(Integer lastMatchRatingEndOfGame) {
        this.lastMatchRatingEndOfGame = lastMatchRatingEndOfGame;
    }

    public void setMotherClubId(Integer teamID) {
        this.motherclubId = teamID;
    }

    public void setMotherClubName(String teamName) {
        this.motherclubName = teamName;
    }

    public void setMatchesCurrentTeam(Integer matchesCurrentTeam) {
        this.matchesCurrentTeam=matchesCurrentTeam;
    }

    public Integer getMatchesCurrentTeam() {
        return this.matchesCurrentTeam;
    }

    static class PositionContribute {
        private final float m_rating;
        private final byte clPositionID;

        public PositionContribute(float rating, byte clPostionID) {
            m_rating = rating;
            clPositionID = clPostionID;
        }

        public float getRating() {
            return m_rating;
        }

        public byte getClPostionID() {
            return clPositionID;
        }


    }

    /**
     * Create a clone of the player with modified skill values if man marking is switched on.
     * Values of Defending, Winger, Playmaking, Scoring and Passing are reduced depending of the distance
     * between man marker and opponent man marked player
     *
     * @param manMarkingPosition
     *          null - no man marking changes
     *          Opposite - reduce skills by 50%
     *          NotOpposite - reduce skills by 65%
     *          NotInLineup - reduce skills by 10%
     * @return
     *          this player, if no man marking changes are selected
     *          New modified player, if man marking changes are selected
     */
    public Player createManMarker(ManMarkingPosition manMarkingPosition) {
        if ( manMarkingPosition == null) return this;
        var ret = new Player();
        var skillFactor = (float)(1 - manMarkingPosition.value / 100.);
        ret.setPlayerSpecialty(this.getPlayerSpecialty());
        ret.setAgeDays(this.getAgeDays());
        ret.setAge(this.getAlter());
        ret.setAgressivitaet(this.getAgressivitaet());
        ret.setAnsehen(this.getAnsehen());
        ret.setCharakter(this.getCharakter());
        ret.setExperience(this.getExperience());
        ret.setSubExperience(this.getSubExperience());
        ret.setFirstName(this.getFirstName());
        ret.setLastName(this.getLastName());
        ret.setForm(this.getForm());
        ret.setLeadership(this.getLeadership());
        ret.setStamina(this.getStamina());
        ret.setLoyalty(this.getLoyalty());
        ret.setHomeGrown(this.isHomeGrown());
        ret.setPlayerID(this.getPlayerID());
        ret.setInjuryWeeks(this.getInjuryWeeks());

        ret.setSkillValue(KEEPER, this.getSkillValue(KEEPER));
        ret.setSkillValue(DEFENDING, skillFactor * this.getSkillValue(DEFENDING));
        ret.setSkillValue(WINGER, skillFactor * this.getSkillValue(WINGER));
        ret.setSkillValue(PLAYMAKING, skillFactor * this.getSkillValue(PLAYMAKING));
        ret.setSkillValue(SCORING, skillFactor * this.getSkillValue(SCORING));
        ret.setSkillValue(PASSING, skillFactor * this.getSkillValue(PASSING));
        ret.setSkillValue(STAMINA, this.getSkillValue(STAMINA));
        ret.setSkillValue(FORM, this.getSkillValue(FORM));
        ret.setSkillValue(SET_PIECES, this.getSkillValue(SET_PIECES));
        ret.setSkillValue(LEADERSHIP, this.getSkillValue(LEADERSHIP));
        ret.setSkillValue(LOYALTY, this.getSkillValue(LOYALTY));
        return ret;
    }

    public enum ManMarkingPosition {
        /**
         * central defender versus attack
         * wingback versus winger
         * central midfield versus central midfield
         */
        Opposite(50),
        /**
         * central defender versus winger, central midfield
         * wingback versus attack, central midfield
         * central midfield versus central attack, winger
         */
        NotOpposite(65),
        /**
         * opponent player is not in lineup or
         * any other combination
         */
        NotInLineup(10);

        private final int value;

        ManMarkingPosition(int v){this.value=v;}

        public static ManMarkingPosition fromId(int id) {
            return switch (id) {
                case 50 -> Opposite;
                case 65 -> NotOpposite;
                case 10 -> NotInLineup;
                default -> null;
            };
        }

        public int getValue() {return value;}
    }
}