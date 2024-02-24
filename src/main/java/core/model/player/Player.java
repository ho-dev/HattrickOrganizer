package core.model.player;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.constants.player.Specialty;
import core.db.AbstractTable;
import core.db.DBManager;
import core.model.*;
import core.model.match.MatchLineupPosition;
import core.model.match.MatchLineupTeam;
import core.model.enums.MatchType;
import core.net.MyConnector;
import core.net.OnlineWorker;
import core.rating.RatingPredictionModel;
import core.training.*;
import core.util.*;
import java.time.Duration;
import java.util.*;

import static core.constants.player.PlayerSkill.KEEPER;
import static core.constants.player.PlayerSkill.WINGER;
import static core.model.player.MatchRoleID.*;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.min;
import static core.constants.player.PlayerSkill.*;

public class Player extends AbstractTable.Storable {

    private byte idealPos = IMatchRoleID.UNKNOWN;

    private static final PlayerSkill[] trainingSkills = {STAMINA, KEEPER, SETPIECES, DEFENDING, SCORING, WINGER, PASSING, PLAYMAKING};
    private static final String BREAK = "[br]";
    private static final String O_BRACKET = "[";
    private static final String C_BRACKET = "]";
    private static final String EMPTY = "";

    private int spielerId;

    /**
     * Name
     */
    private String firstName = "";
    private String nickName = "";
    private String lastName = "";

    /**
     * Arrival in team
     */
    private HODateTime arrivalDate = null;

    /**
     * Trainer contract date
     */
    private String contractDate;

    public String getContractDate() {
        return contractDate;
    }

    public void setContractDate(String contractDate) {
        this.contractDate = contractDate;
    }


    /**
     * Download date
     */
    private HODateTime hrfDate;

    /**
     * The player is no longer available in the current HRF
     */
    private boolean goner;

    /**
     * Wing skill
     */
    private double subWingerSkill;

    /**
     * Pass skill
     */
    private double subPassingSkill;

    /**
     * Playmaking skill
     */
    private double subPlaymakingSkill;

    /**
     * Standards
     */
    private double subSetPiecesSkill;

    /**
     * Goal
     */
    private double subScoringSkill;

    //Subskills
    private double subGoalkeeperSkill;

    /**
     * Verteidigung
     */
    private double subDefendingSkill;
    private double subStamina;

    /**
     * Agressivität
     */
    private int aggressivity;

    /**
     * Alter
     */
    private int age;

    /**
     * Age Days
     */
    private int ageDays;

    /**
     * Ansehen (ekel usw. )
     */
    private int honesty = 1;

    /**
     * Bewertung
     */
    private int rating;

    /**
     * charakter ( ehrlich)
     */
    private int gentleness = 1;

    /**
     * Erfahrung
     */
    private int experience = 1;
    /**
     * Form
     */
    private int form = 1;

    /**
     * Führungsqualität
     */
    private int leadership = 1;

    /**
     * Gehalt
     */
    private int wage = 1;

    /**
     * Gelbe Karten
     */
    private int totalCards;

    /**
     * Hattricks
     */
    private int hatTricks;

    private int currentTeamGoals;
    /**
     * Home Grown
     */
    private boolean homeGrown = false;

    /**
     * Kondition
     */
    private int stamina = 1;

    /**
     * Länderspiele
     */
    private int internationalMatches;

    /**
     * Loyalty
     */
    private int loyalty = 0;

    /**
     * Markwert
     */
    private int tsi;

    private String nationality;

    /**
     * Aus welchem Land kommt der Player
     */
    private int nationalityId = 49;

    /**
     * SpezialitätID
     */
    private int specialty;


    private int wingerSkill = 1;
    private int passingSkill = 1;
    private int playmakingSkill = 1;

    private int setPiecesSkill = 1;
    private int scoringSkill = 1;

    private int goalkeeperSkill = 1;

    private int defendingSkill = 1;

    /**
     * Tore Freundschaftspiel
     */
    private int friendlyGoals;

    /**
     * Tore Gesamt
     */
    private int totalGoals;

    /**
     * Tore Liga
     */
    private int leagueGoals;

    /**
     * Tore Pokalspiel
     */
    private int cupGameGoals;

    /**
     * Trainerfähigkeit
     */
    private int coachSkill;

    /**
     * Trainertyp
     */
    private TrainerType trainerType;

    /**
     * Transferlisted
     */
    private int transferListed;

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
    private int u20InternationalMatches;

    /**
     * Verletzt Wochen
     */
    private int injuryWeeks = -1;

    /**
     * Training block
     */
    private boolean trainingBlock = false;

    /**
     * Last match
     */
    private String lastMatchDate;
    private Integer lastMatchId;
    private MatchType lastMatchType;
    private Integer lastMatchPosition;
    private Integer lastMatchMinutes;
    // Rating is number of half stars
    // real rating value is rating/2.0f
    private Integer lastMatchRating;
    private Integer lastMatchRatingEndOfGame;
    private Integer nationalTeamId;
    private double subExperience;

    /**
     * future training priorities planed by the user
     */
    private List<FuturePlayerTraining> futurePlayerTrainings;
    private List<FuturePlayerSkillTraining> futurePlayerSkillTrainings;

    private Integer motherClubId;
    private String motherClubName;

    /**
     * Number of matches with the current team.
     */
    private Integer currentTeamMatches;
    private int hrfId = -1;
    private Integer htms = null;
    private Integer htms28 = null;

    /**
     * Externally recruited coaches are no longer allowed to be part of the lineup
     */
    private boolean lineupDisabled = false;

    private List<SkillChange> skillChanges;


    //~ Constructors -------------------------------------------------------------------------------


    /**
     * Creates a new instance of Player
     */
    public Player() {
    }

    /**
     * Erstellt einen Player aus den Properties einer HRF Datei
     */
    public Player(HOProperties properties, HODateTime hrfDate, int hrfId) {
        // Separate first, nick and last names are available. Utilize them?

        this.hrfId = hrfId;
        spielerId = properties.getInt("id", 0);
        firstName = properties.getProperty("firstname", "");
        nickName = properties.getProperty("nickname", "");
        lastName = properties.getProperty("lastname", "");
        arrivalDate = HODateTime.fromHT(properties.getProperty("arrivaldate"));
        age = properties.getInt("ald", 0);
        ageDays = properties.getInt("agedays", 0);
        stamina = properties.getInt("uth", 0);
        form = properties.getInt("for", 0);
        goalkeeperSkill = properties.getInt("mlv", 0);
        defendingSkill = properties.getInt("bac", 0);
        playmakingSkill = properties.getInt("spe", 0);
        passingSkill = properties.getInt("fra", 0);
        wingerSkill = properties.getInt("ytt", 0);
        scoringSkill = properties.getInt("mal", 0);
        setPiecesSkill = properties.getInt("fas", 0);
        specialty = properties.getInt("speciality", 0);
        gentleness = properties.getInt("gentleness", 0);
        honesty = properties.getInt("honesty", 0);
        aggressivity = properties.getInt("aggressiveness", 0);
        experience = properties.getInt("rut", 0);
        homeGrown = properties.getBoolean("homegr", false);
        loyalty = properties.getInt("loy", 0);
        leadership = properties.getInt("led", 0);
        wage = properties.getInt("sal", 0);
        nationalityId = properties.getInt("countryid", 0);
        tsi = properties.getInt("mkt", 0);

        // also read subskills when importing hrf from hattrickportal.pro/ useful for U20/NT
        subWingerSkill = properties.getDouble("yttsub", 0.);
        subPassingSkill = properties.getDouble("frasub", 0);
        subPlaymakingSkill = properties.getDouble("spesub", 0);
        subSetPiecesSkill = properties.getDouble("fassub", 0);
        subScoringSkill = properties.getDouble("malsub", 0);
        subGoalkeeperSkill = properties.getDouble("mlvsub", 0);
        subDefendingSkill = properties.getDouble("bacsub", 0);
        subExperience = properties.getDouble("experiencesub", 0);

        //TSI, alles vorher durch 1000 teilen
        this.hrfDate = hrfDate;

        if (hrfDate.isBefore(HODateTime.fromDbTimestamp(DBManager.TSIDATE))) {
            tsi /= 1000d;
        }

        totalCards = properties.getInt("warnings", 0);
        injuryWeeks = properties.getInt("ska", 0);
        friendlyGoals = properties.getInt("gtt", 0);
        leagueGoals = properties.getInt("gtl", 0);
        cupGameGoals = properties.getInt("gtc", 0);
        totalGoals = properties.getInt("gev", 0);
        hatTricks = properties.getInt("hat", 0);
        currentTeamGoals = properties.getInt("goalscurrentteam", 0);
        currentTeamMatches = properties.getInt("matchescurrentteam", 0);

        this.lineupDisabled = properties.getBoolean("lineupdisabled", false);
        this.rating = properties.getInt("rating", 0);
        this.trainerType = TrainerType.fromInt(properties.getInt("trainertype", -1));
        this.coachSkill = properties.getInt("trainerskilllevel", 0);
        if (this.coachSkill > 0) {
            this.coachSkill += 3;    // trainer level 5 is an excellent (8) trainer
            wage = properties.getInt("cost", 0);
            contractDate = properties.getProperty("contractdate");
        }

        var temp = properties.getProperty("playernumber", "");
        if ((temp != null) && !temp.isEmpty() && !temp.equals("null")) {
            shirtNumber = Integer.parseInt(temp);
        }

        transferListed = properties.getBoolean("transferlisted", false) ? 1 : 0;
        internationalMatches = properties.getInt("caps", 0);
        u20InternationalMatches = properties.getInt("capsu20", 0);
        this.nationalTeamId = properties.getInt("nationalteamid", 0);

        // #461-lastmatch
        lastMatchDate = properties.getProperty("lastmatch_date");
        if (lastMatchDate != null && !lastMatchDate.isEmpty()) {
            lastMatchId = properties.getInt("lastmatch_id", 0);
            var pos = properties.getInt("lastmatch_positioncode", -1);
            if (isFieldMatchRoleId(pos)) {
                lastMatchPosition = pos;
            }
            lastMatchMinutes = properties.getInt("lastmatch_playedminutes", 0);
            // rating is stored as number of half stars
            lastMatchRating = (int) (2 * properties.getDouble("lastmatch_rating", 0));
            lastMatchRatingEndOfGame = (int) (2 * properties.getDouble("lastmatch_ratingendofgame", 0));
        }

        setLastMatchType(MatchType.getById(properties.getInt("lastmatch_type", 0)));

        playerCategory = PlayerCategory.valueOf(properties.getInt("playercategoryid", 0));
        playerStatement = properties.getProperty("statement", "");
        ownerNotes = properties.getProperty("ownernotes", "");

        //Subskills calculation
        //Called when saving the HRF because the necessary data is not available here
        final HOModel oldmodel = HOVerwaltung.instance().getModel();
        final Player oldPlayer = oldmodel.getCurrentPlayer(spielerId);
        if (oldPlayer != null) {
            // Training blocked (could be done in the past)
            trainingBlock = oldPlayer.hasTrainingBlock();
            motherClubId = oldPlayer.getOrDownloadMotherClubId();
            motherClubName = oldPlayer.getOrDownloadMotherClubName();
        }
    }

    public String getOrDownloadMotherClubName() {
        downloadMotherClubInfoIfMissing();
        return this.motherClubName;
    }

    public Integer getOrDownloadMotherClubId() {
        downloadMotherClubInfoIfMissing();
        return this.motherClubId;
    }
    private void downloadMotherClubInfoIfMissing() {
        var isCurrentPlayer = HOVerwaltung.instance().getModel().getCurrentPlayer(this.getPlayerId()) != null;
        if (isCurrentPlayer && motherClubId == null ) {
            var connection = MyConnector.instance();
            var isSilentDownload = connection.isSilentDownload();
            try {
                connection.setSilentDownload(true);
                // try to download missing mother club info
                var playerDetails = OnlineWorker.downloadPlayerDetails(String.valueOf(this.getPlayerId()));
                if (playerDetails != null) {
                    motherClubId = playerDetails.getMotherClubId();
                    motherClubName = playerDetails.getMotherClubName();
                }
            } catch (Exception e) {
//                HOLogger.instance().warning(getClass(), "mother club not available for player " + this.getFullName());
            } finally {
                connection.setSilentDownload(isSilentDownload); // reset
            }
        }
    }

    public String getMotherClubName() {
        return this.motherClubName;
    }

    public Integer getMotherClubId() {
        return this.motherClubId;
    }

    public void setAggressivity(int m_iAgressivitaet) {
        this.aggressivity = m_iAgressivitaet;
    }

    public int getAggressivity() {
        return aggressivity;
    }

    public void setAge(int m_iAlter) {
        schumRankBenchmark = null;
        this.age = m_iAlter;
    }

    public int getAge() {
        return age;
    }

    public void setAgeDays(int m_iAgeDays) {
        schumRankBenchmark = null;
        this.ageDays = m_iAgeDays;
    }

    public int getAgeDays() {
        return ageDays;
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
        int years = getAge();
        int days = getAgeDays();
        return years + (double) (days + diff.toDays()) / 112;
    }

    /**
     * Calculates String for full age and days correcting for the difference between (now and last HRF file)
     *
     * @return String of age & age days format is "YY (DDD)"
     */
    public String getAgeWithDaysAsString() {
        return getAgeWithDaysAsString(HODateTime.now());
    }

    public String getAgeWithDaysAsString(HODateTime t) {
        if (this.hrfDate != null) return getAgeWithDaysAsString(this.getAge(), this.getAgeDays(), t, this.hrfDate);
        return "";
    }

    /**
     * Calculates the player's age at date referencing the current hrf download
     *
     * @param ageYears int player's age in years in current hrf download
     * @param ageDays  int additional days
     * @param time     HODateTime for which the player's age should be calculated
     * @return String
     */
    public static String getAgeWithDaysAsString(int ageYears, int ageDays, HODateTime time) {
        return getAgeWithDaysAsString(ageYears, ageDays, time, HOVerwaltung.instance().getModel().getBasics().getDatum());
    }

    /**
     * Calculates the player's age at date referencing the given hrf date
     *
     * @param ageYears int player's age in years at reference time
     * @param ageDays  int additional days
     * @param time     HODateTime for which the player's age should be calculated
     * @param hrfTime  HODateTime reference date, when player's age was given
     * @return String
     */
    public static String getAgeWithDaysAsString(int ageYears, int ageDays, HODateTime time, HODateTime hrfTime) {
        var age = new HODateTime.HODuration(ageYears, ageDays).plus(HODateTime.HODuration.between(hrfTime, time));
        return age.seasons + " (" + age.days + ")";
    }

    public HODateTime.HODuration getAgeAtDate(HODateTime date) {
        if (this.hrfDate != null)
            return new HODateTime.HODuration(this.getAge(), this.getAgeDays()).plus(HODateTime.HODuration.between(this.hrfDate, date));
        return null;
    }

    /**
     * Get the full i18n'd string representing the player's age. Includes
     * the birthday indicator as well.
     *
     * @return the full i18n'd string representing the player's age
     */
    public String getAgeStringFull() {
        var hrfTime = HOVerwaltung.instance().getModel().getBasics().getDatum();
        var oldAge = new HODateTime.HODuration(this.getAge(), this.getAgeDays());
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

    public void setHonesty(int m_iAnsehen) {
        this.honesty = m_iAnsehen;
    }

    public int getHonesty() {
        return honesty;
    }

    public void setRating(int m_iBewertung) {
        this.rating = m_iBewertung;
    }

    public int getRating() {
        return rating;
    }

    /**
     * Getter for property m_iBonus.
     *
     * @return Value of property m_iBonus.
     */
    public int getBonus() {
        int bonus = 0;

        if (nationalityId != HOVerwaltung.instance().getModel().getBasics().getLand()) {
            bonus = 20;
        }

        return bonus;
    }

    public void setGentleness(int m_iCharakter) {
        this.gentleness = m_iCharakter;
    }

    public int getGentleness() {
        return gentleness;
    }

    public HODateTime getArrivalDate() {
        if (arrivalDate == null) {
            var firstDownload = DBManager.instance().loadPlayerFirstHRF(this.getPlayerId());
            if (firstDownload != null) {
                arrivalDate = firstDownload.getHrfDate();
            } else {
                arrivalDate = this.getHrfDate();
            }
        }
        return arrivalDate;
    }

    public void setArrivalDate(HODateTime m_arrivalDate) {
        this.arrivalDate = m_arrivalDate;
    }


    public void setExperience(int m_iErfahrung) {
        this.experience = m_iErfahrung;
    }

    public int getExperience() {
        return experience;
    }


    public void setWingerSkill(int m_iFluegelspiel) {
        schumRank = null;
        this.wingerSkill = m_iFluegelspiel;
    }

    public int getWingerSkill() {
        return wingerSkill;
    }

    public void setForm(int m_iForm) {
        this.form = m_iForm;
    }

    public int getForm() {
        return form;
    }

    public void setLeadership(int m_iFuehrung) {
        this.leadership = m_iFuehrung;
    }

    public int getLeadership() {
        return leadership;
    }

    public void setWage(int m_iGehalt) {
        this.wage = m_iGehalt;
    }

    public int getWage() {
        return wage;
    }

    public void setTotalCards(int m_iGelbeKarten) {
        this.totalCards = m_iGelbeKarten;
    }

    public int getTotalCards() {
        return totalCards;
    }

    /**
     * Indicates whether the player is suspended.
     */
    public boolean isRedCarded() {
        return (totalCards > 2);
    }


    public void setHatTricks(int m_iHattrick) {
        this.hatTricks = m_iHattrick;
    }


    public int getHatTricks() {
        return hatTricks;
    }


    public int getCurrentTeamGoals() {
        return currentTeamGoals;
    }

    public void setCurrentTeamGoals(int m_iGoalsCurrentTeam) {
        this.currentTeamGoals = m_iGoalsCurrentTeam;
    }

    public void setHomeGrown(boolean hg) {
        homeGrown = hg;
    }

    public boolean isHomeGrown() {
        return homeGrown;
    }


    public HODateTime getHrfDate() {
        if (hrfDate == null) {
            hrfDate = HOVerwaltung.instance().getModel().getBasics().getDatum();
        }
        return hrfDate;
    }

    public void setHrfDate(HODateTime timestamp) {
        hrfDate = timestamp;
    }

    public void setHrfDate() {
        setHrfDate(HODateTime.now());
    }

    static Player referencePlayer;
    static Player referenceKeeper;

    public static Player getReferencePlayer() {
        if (referencePlayer == null) {
            referencePlayer = new Player();
            referencePlayer.setAge(28);
            referencePlayer.setAgeDays(0);
            referencePlayer.setPlayerId(MAX_VALUE);
            referencePlayer.setForm(8);
            referencePlayer.setStamina(9);
            referencePlayer.setSkillValue(KEEPER, 1.);
            referencePlayer.setSkillValue(DEFENDING, 20.);
            referencePlayer.setSkillValue(PLAYMAKING, 20.);
            referencePlayer.setSkillValue(PASSING, 20.);
            referencePlayer.setSkillValue(WINGER, 20.);
            referencePlayer.setSkillValue(SCORING, 20.);
            referencePlayer.setSkillValue(SETPIECES, 20.);
            referencePlayer.setSkillValue(EXPERIENCE, 20.);
        }
        return referencePlayer;
    }

    public static Player getReferenceKeeper() {
        if (referenceKeeper == null) {
            referenceKeeper = new Player();
            referenceKeeper.setAge(28);
            referenceKeeper.setAgeDays(0);
            referenceKeeper.setPlayerId(MAX_VALUE);
            referenceKeeper.setForm(8);
            referenceKeeper.setStamina(9);
            referenceKeeper.setSkillValue(KEEPER, 20.);
            referenceKeeper.setSkillValue(DEFENDING, 20.);
            referenceKeeper.setSkillValue(SETPIECES, 20.);
            referenceKeeper.setSkillValue(PLAYMAKING, 1);
            referenceKeeper.setSkillValue(PASSING, 1);
            referenceKeeper.setSkillValue(WINGER, 1);
            referenceKeeper.setSkillValue(SCORING, 1);
            referenceKeeper.setSkillValue(EXPERIENCE, 1);
        }
        return referenceKeeper;
    }

    public double getIdealPositionRating() {
        var maxRating = getMaxRating();
        if (maxRating != null) {
            return maxRating.getRating();
        }
        return 0;
    }

    private PlayerPositionRating getMaxRating() {
        var maxRating = getAllPositionRatings().stream().max(Comparator.comparing(PlayerPositionRating::getRating));
        return maxRating.orElse(null);

    }

    public MatchLineupPosition getIdealMatchLineupPosition() {
        var r = getMaxRating();
        if (r != null) {
            return new MatchLineupPosition(r.roleId, this.getPlayerId(), r.behaviour);
        }
        return null;
    }

    public byte getIdealPosition() {
        //in case player best position is forced by user
        final int flag = getUserPosFlag();

        if (flag == IMatchRoleID.UNKNOWN) {
            if (idealPos == IMatchRoleID.UNKNOWN) {
                var matchLineupPosition = getIdealMatchLineupPosition();
                idealPos = getPosition(matchLineupPosition.getRoleId(), matchLineupPosition.getBehaviour());
            }
            return idealPos;
        }

        return (byte) flag;
    }

    public double getPositionRating(byte position) {
        var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
        return ratingPredictionModel.getPlayerMatchAverageRating(this, position);
    }

    private Map<Integer, Integer> wagesHistory = null; // age->wage

    private Integer getWageAtAge(int age) {
        if (wagesHistory == null) {
            wagesHistory = DBManager.instance().loadWageHistory(this.getPlayerId());
        }
        return wagesHistory.get(age);
    }

    public int getSumOfWage(HODateTime from, HODateTime to) {
        var economyDate = HOVerwaltung.instance().getModel().getXtraDaten().getEconomyDate();
        while (!economyDate.isBefore(to)) economyDate = economyDate.plusDaysAtSameLocalTime(-7);
        var sum = 0;
        while (economyDate.isAfter(from)) {
            var wageAtDate = getWageAtAge(this.getAgeAtDate(economyDate).seasons);
            if (wageAtDate != null) {
                sum += wageAtDate;
            }
            economyDate = economyDate.plusDaysAtSameLocalTime(-7);
        }
        return sum;
    }

    private Player latestPlayerInformation = null;

    public Player getLatestPlayerInfo() {
        if (latestPlayerInformation == null) {
            latestPlayerInformation = DBManager.instance().loadLatestPlayerInfo(this.getPlayerId());
        }
        return latestPlayerInformation;
    }

    static class PlayerPositionRating {

        public PlayerPositionRating(Integer p, Byte behaviour, double rating) {
            this.roleId = p;
            this.behaviour = behaviour;
            this.rating = rating;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        double rating;

        public int getRoleId() {
            return roleId;
        }

        public void setRoleId(int roleId) {
            this.roleId = roleId;
        }

        int roleId;

        public byte getBehaviour() {
            return behaviour;
        }

        public void setBehaviour(byte behaviour) {
            this.behaviour = behaviour;
        }

        byte behaviour;
    }

    List<PlayerPositionRating> getAllPositionRatings() {
        var ret = new ArrayList<PlayerPositionRating>();
        var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
        for (var p : RatingPredictionModel.playerRatingPositions) {
            for (var behaviour : MatchRoleID.getBehaviours(p)) {
                var d = ratingPredictionModel.getPlayerMatchAverageRating(this, p, behaviour);
                ret.add(new PlayerPositionRating(p, behaviour, d));
            }
        }
        return ret;
    }

    /**
     * Calculate player alternative best positions (weather impact not relevant here)
     */
    public List<Byte> getAlternativeBestPositions() {
        Double threshold = null;
        float tolerance = 1f - UserParameter.instance().alternativePositionsTolerance;
        var ret = new ArrayList<Byte>();
        var allPositionRatings = getAllPositionRatings().stream().sorted(Comparator.comparing(PlayerPositionRating::getRating, Comparator.reverseOrder())).toList();
        for (var p : allPositionRatings) {
            if (threshold == null) {
                threshold = p.getRating() * tolerance;
                ret.add(MatchRoleID.getPosition(p.getRoleId(), p.getBehaviour()));
            } else if (p.getRating() >= threshold) {
                ret.add(MatchRoleID.getPosition(p.getRoleId(), p.getBehaviour()));
            } else {
                break;
            }
        }
        return ret;
    }

    /**
     * return whether the position is one of the best position for the player
     */
    public boolean isAnAlternativeBestPosition(byte position) {
        return getAlternativeBestPositions().contains(position);
    }

    public void setStamina(int m_iKondition) {
        this.stamina = m_iKondition;
    }

    public int getStamina() {
        return stamina;
    }

    public void setInternationalMatches(int m_iLaenderspiele) {
        this.internationalMatches = m_iLaenderspiele;
    }

    public int getInternalMatches() {
        return internationalMatches;
    }

    /**
     * gives information of skill ups
     */
    public List<SkillChange> getAllLevelUp(PlayerSkill skill) {
        return getSkillChanges().stream().filter(e -> e.getType().equals(skill) && e.getChange() > 0).toList();
    }

    /**
     * Get all skill changes of one type
     *
     * @param skill Skill type
     * @return List of skill changes
     */
    public List<SkillChange> getAllSkillChanges(PlayerSkill skill) {
        return getSkillChanges().stream().filter(e -> e.getType().equals(skill)).toList();
    }

    /**
     * Returns the loyalty stat
     */
    public int getLoyalty() {
        return loyalty;
    }

    /**
     * Sets the loyalty stat
     */
    public void setLoyalty(int loy) {
        loyalty = loy;
    }

    public void setManuellerSmilie(String manuellerSmilie) {
        getNotes().setManuelSmilie(manuellerSmilie);
        DBManager.instance().storePlayerNotes(notes);
    }

    public String getInfoSmiley() {
        return getNotes().getManuelSmilie();
    }

    public void setTsi(int m_iTSI) {
        this.tsi = m_iTSI;
    }

    public int getTsi() {
        return tsi;
    }

    public void setFirstName(String m_sName) {
        if (m_sName != null) this.firstName = m_sName;
        else firstName = "";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setNickName(String m_sName) {
        if (m_sName != null) this.nickName = m_sName;
        else nickName = "";
    }

    public String getNickName() {
        return nickName;
    }

    public void setLastName(String m_sName) {
        if (m_sName != null) this.lastName = m_sName;
        else this.lastName = "";
    }

    public String getLastName() {
        return lastName;
    }


    /**
     * Getter for shortName
     * eg: James Bond = J. Bond
     * Nickname are ignored
     */
    public String getShortName() {
        if (getFirstName().isEmpty()) {
            return getLastName();
        }
        return getFirstName().charAt(0) + ". " + getLastName();

    }

    public String getFullName() {
        if (getNickName().isEmpty()) {
            if (!getFirstName().isEmpty()) {
                return getFirstName() + " " + getLastName();
            }
            return getLastName();
        }

        return getFirstName() + " '" + getNickName() + "' " + getLastName();
    }

    public void setNationalityId(int m_iNationalitaet) {
        this.nationalityId = m_iNationalitaet;
    }

    public int getNationalityId() {
        return nationalityId;
    }


    public String getNationality() {
        if (nationality != null) {
            return nationality;
        }
        WorldDetailLeague leagueDetail = WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(nationalityId);
        if (leagueDetail != null) {
            nationality = leagueDetail.getCountryName();
        } else {
            nationality = "";
        }
        return nationality;
    }

    public void setGoner(boolean m_bOld) {
        this.goner = m_bOld;
    }

    public boolean isGoner() {
        return goner;
    }

    public void setPassingSkill(int m_iPasspiel) {
        schumRank = null;
        this.passingSkill = m_iPasspiel;
    }

    public int getPassingSkill() {
        return passingSkill;
    }

    /**
     * Zum speichern! Die Reduzierung des Marktwerts auf TSI wird rückgängig gemacht
     */
    public int getMarktwert() {
        if (hrfDate == null || hrfDate.isBefore(HODateTime.fromDbTimestamp(DBManager.TSIDATE))) {
            //Echter Marktwert
            return tsi * 1000;
        }

        //TSI
        return tsi;
    }


    String latestTSIInjured;
    String latestTSINotInjured;

    public String getLatestTSINotInjured() {
        if (latestTSINotInjured == null) {
            latestTSINotInjured = DBManager.instance().loadLatestTSINotInjured(spielerId);
        }
        return latestTSINotInjured;
    }

    public String getLatestTSIInjured() {
        if (latestTSIInjured == null) {
            latestTSIInjured = DBManager.instance().loadLatestTSIInjured(spielerId);
        }
        return latestTSIInjured;
    }

    public void setSpecialty(int iPlayerSpecialty) {
        this.specialty = iPlayerSpecialty;
    }

    public int getSpecialty() {
        return specialty;
    }

    public boolean hasSpecialty(Specialty speciality) {
        Specialty s = Specialty.values()[specialty];
        return s.equals(speciality);
    }

    // returns the name of the speciality in the used language
    public String getSpecialtyName() {
        Specialty s = Specialty.values()[specialty];
        if (s.equals(Specialty.NO_SPECIALITY)) {
            return EMPTY;
        } else {
            return HOVerwaltung.instance().getLanguageString("ls.player.speciality." + s.toString().toLowerCase(Locale.ROOT));
        }
    }

    // return the name of the speciality with a break before and in brackets
    // e.g. [br][quick], used for HT-ML export
    public String getSpecialtyExportName() {
        Specialty s = Specialty.values()[specialty];
        if (s.equals(Specialty.NO_SPECIALITY)) {
            return EMPTY;
        } else {
            return BREAK + O_BRACKET + getSpecialtyName() + C_BRACKET;
        }
    }

    // no break so that the export looks better
    public String getSpecialtyExportNameForKeeper() {
        Specialty s = Specialty.values()[specialty];
        if (s.equals(Specialty.NO_SPECIALITY)) {
            return EMPTY;
        } else {
            return O_BRACKET + getSpecialtyName() + C_BRACKET;
        }
    }

    public void setPlaymakingSkill(int m_iSpielaufbau) {
        schumRank = null;
        this.playmakingSkill = m_iSpielaufbau;
    }

    public int getPlaymakingSkill() {
        return playmakingSkill;
    }

    /**
     * set whether that player can be selected by the assistant
     */
    public void setCanBeSelectedByAssistant(boolean flag) {
        if (this.isLineupDisabled()) flag = false;
        getNotes().setEligibleToPlay(flag);
        DBManager.instance().storePlayerNotes(notes);
    }

    /**
     * get whether that player can be selected by the assistant
     */
    public boolean getCanBeSelectedByAssistant() {
        return !this.isLineupDisabled() && getNotes().isEligibleToPlay();
    }

    public void setPlayerId(int m_iSpielerID) {
        this.spielerId = m_iSpielerID;
    }

    public int getPlayerId() {
        return spielerId;
    }

    public void setSetPiecesSkill(int m_iStandards) {
        schumRank = null;
        this.setPiecesSkill = m_iStandards;
    }

    public int getSetPiecesSkill() {
        return setPiecesSkill;
    }

    /**
     * Get skill value including subskill
     *
     * @param iSkill skill id
     * @return double
     */
    public double getSkill(PlayerSkill iSkill) {
        return getValue4Skill(iSkill) + getSub4Skill(iSkill);
    }

    /**
     * Returns accurate subskill number. If you need subskill for UI
     * purpose it is better to use getSubskill4Pos()
     *
     * @param skill skill number
     * @return subskill between 0.0-0.999
     */
    public double getSub4Skill(PlayerSkill skill) {
        double value = switch (skill) {
            case KEEPER -> subGoalkeeperSkill;
            case PLAYMAKING -> subPlaymakingSkill;
            case DEFENDING -> subDefendingSkill;
            case PASSING -> subPassingSkill;
            case WINGER -> subWingerSkill;
            case SCORING -> subScoringSkill;
            case SETPIECES -> subSetPiecesSkill;
            case EXPERIENCE -> subExperience;
            case STAMINA -> subStamina;
            default -> 0;
        };

        return (float) Math.min(0.999, value);
    }

    public void setSubskill4PlayerSkill(PlayerSkill skill, double value) {
        schumRank = null;
        switch (skill) {
            case KEEPER -> subGoalkeeperSkill = value;
            case PLAYMAKING -> subPlaymakingSkill = value;
            case DEFENDING -> subDefendingSkill = value;
            case PASSING -> subPassingSkill = value;
            case WINGER -> subWingerSkill = value;
            case SCORING -> subScoringSkill = value;
            case SETPIECES -> subSetPiecesSkill = value;
            case EXPERIENCE -> subExperience = value;
            case STAMINA -> subStamina = value;
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

    public void setFriendlyGoals(int m_iToreFreund) {
        this.friendlyGoals = m_iToreFreund;
    }

    public int getFriendlyGoals() {
        return friendlyGoals;
    }

    public void setTotalGoals(int m_iToreGesamt) {
        this.totalGoals = m_iToreGesamt;
    }

    public int getTotalGoals() {
        return totalGoals;
    }

    public void setLeagueGoals(int m_iToreLiga) {
        this.leagueGoals = m_iToreLiga;
    }

    public int getLeagueGoals() {
        return leagueGoals;
    }

    public void setCupGameGoals(int m_iTorePokal) {
        this.cupGameGoals = m_iTorePokal;
    }

    public int getCupGameGoals() {
        return cupGameGoals;
    }

    public void setScoringSkill(int m_iTorschuss) {
        schumRank = null;
        this.scoringSkill = m_iTorschuss;
    }

    public int getScoringSkill() {
        return scoringSkill;
    }

    public void setGoalkeeperSkill(int m_iTorwart) {
        schumRank = null;
        this.goalkeeperSkill = m_iTorwart;
    }

    public int getGoalkeeperSkill() {
        return goalkeeperSkill;
    }

    public void setCoachSkill(Integer m_iTrainer) {
        this.coachSkill = m_iTrainer;
    }

    public int getCoachSkill() {
        return coachSkill;
    }

    /**
     * indicates whether a player is a coach.
     */
    public boolean isCoach() {
        return coachSkill > 0 && trainerType != null;
    }

    public void setTrainerType(TrainerType m_iTrainerTyp) {
        this.trainerType = m_iTrainerTyp;
    }

    public TrainerType getTrainerType() {
        return trainerType;
    }

    /**
     * Last match
     *
     * @return date
     */
    public String getLastMatchDate() {
        return lastMatchDate;
    }

    /**
     * Last match
     *
     * @return rating
     */
    public Integer getLastMatchRating() {
        return lastMatchRating;
    }

    /**
     * Last match id
     *
     * @return id
     */
    public Integer getLastMatchId() {
        return lastMatchId;
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

    public void setTransferListed(int m_iTransferlisted) {
        this.transferListed = m_iTransferlisted;
    }

    public int getTransferListed() {
        return transferListed;
    }

    public void setShirtNumber(int m_iTrikotnummer) {
        this.shirtNumber = m_iTrikotnummer;
    }

    public int getShirtNumber() {
        return shirtNumber;
    }

    public void setU20InternationalMatches(int m_iU20Laenderspiele) {
        this.u20InternationalMatches = m_iU20Laenderspiele;
    }

    public int getU20InternationalMatches() {
        return u20InternationalMatches;
    }

    public void setHrfId(int hrf_id) {
        this.hrfId = hrf_id;
    }

    public int getHrfId() {
        return this.hrfId;
    }

    public boolean isTemporary() {
        return this.hrfId == -1;
    }

    public void setLastMatchDate(String v) {
        this.lastMatchDate = v;
    }

    public void setLastMatchRating(Integer v) {
        this.lastMatchRating = v;
    }

    public void setLastMatchId(Integer v) {
        this.lastMatchId = v;
    }

    public Integer getHtms28() {
        if (htms28 == null) {
            htms28 = Htms.htms28(getSkills(), this.age, this.ageDays);
        }
        return htms28;
    }

    public Integer getHtms() {
        if (htms == null) {
            htms = Htms.htms(getSkills());
        }
        return htms;
    }

    private Map<PlayerSkill, Integer> getSkills() {
        var ret = new HashMap<PlayerSkill, Integer>();
        ret.put(KEEPER, getGoalkeeperSkill());
        ret.put(DEFENDING, getDefendingSkill());
        ret.put(PLAYMAKING, getPlaymakingSkill());
        ret.put(WINGER, getWingerSkill());
        ret.put(PASSING, getPassingSkill());
        ret.put(SCORING, getScoringSkill());
        ret.put(SETPIECES, getSetPiecesSkill());

        return ret;
    }

    public boolean isLineupDisabled() {
        return lineupDisabled;
    }

    public void setLineupDisabled(Boolean lineupDisabled) {
        this.lineupDisabled = Objects.requireNonNullElse(lineupDisabled, false);
    }

    public static class Notes extends AbstractTable.Storable {

        public Notes() {
        }

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

        private String manuelSmilie = "";
        private String note = "";
        private boolean eligibleToPlay = true;
        private String teamInfoSmilie = "";
        private boolean isFired = false;

        public void setPlayerId(int playerId) {
            this.playerId = playerId;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public void setEligibleToPlay(boolean spielberechtigt) {
            this.eligibleToPlay = spielberechtigt;
        }

        public void setTeamInfoSmilie(String teamInfoSmilie) {
            this.teamInfoSmilie = teamInfoSmilie;
        }

        public void setManuelSmilie(String manuellerSmilie) {
            this.manuelSmilie = manuellerSmilie;
        }

        public void setUserPos(int userPos) {
            this.userPos = userPos;
        }

        public void setIsFired(boolean isFired) {
            this.isFired = isFired;
        }

        public int getPlayerId() {
            return this.playerId;
        }
    }

    private Notes notes;

    private Notes getNotes() {
        if (notes == null) {
            notes = DBManager.instance().loadPlayerNotes(this.getPlayerId());
        }
        return notes;
    }

    public void setUserPosFlag(byte flag) {
        schumRankBenchmark = null;
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
        return getNotes().getUserPos();
    }

    public String getNote() {
        return getNotes().getNote();
    }

    public void setNote(String text) {
        getNotes().setNote(text);
        DBManager.instance().storePlayerNotes(notes);
    }

    /**
     * get Skillvalue 4 skill
     */
    public int getValue4Skill(PlayerSkill skill) {
        return switch (skill) {
            case KEEPER -> goalkeeperSkill;
            case PLAYMAKING -> playmakingSkill;
            case DEFENDING -> defendingSkill;
            case PASSING -> passingSkill;
            case WINGER -> wingerSkill;
            case SCORING -> scoringSkill;
            case SETPIECES -> setPiecesSkill;
            case STAMINA -> stamina;
            case EXPERIENCE -> experience;
            case FORM -> form;
            case LEADERSHIP -> leadership;
            case LOYALTY -> loyalty;
        };
    }

    public double getSkillValue(PlayerSkill skill) {
        return getSub4Skill(skill) + getValue4Skill(skill);
    }

    public void setSkillValue(PlayerSkill skill, double value) {
        int intVal = (int) value;
        setValue4Skill(skill, intVal);
        setSubskill4PlayerSkill(skill, value - intVal);
    }

    /**
     * set Skillvalue 4 skill
     *
     * @param skill the skill to change
     * @param value the new skill value
     */
    public void setValue4Skill(PlayerSkill skill, int value) {
        schumRank = null;
        switch (skill) {
            case KEEPER -> setGoalkeeperSkill(value);
            case PLAYMAKING -> setPlaymakingSkill(value);
            case PASSING -> setPassingSkill(value);
            case WINGER -> setWingerSkill(value);
            case DEFENDING -> setDefendingSkill(value);
            case SCORING -> setScoringSkill(value);
            case SETPIECES -> setSetPiecesSkill(value);
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
        this.injuryWeeks = m_iVerletzt;
    }

    /**
     * Getter for property m_iVerletzt.
     *
     * @return Value of property m_iVerletzt.
     */
    public int getInjuryWeeks() {
        return injuryWeeks;
    }

    public void setDefendingSkill(int m_iVerteidigung) {
        schumRank = null;
        this.defendingSkill = m_iVerteidigung;
    }

    public int getDefendingSkill() {
        return defendingSkill;
    }

    /**
     * Calculates training effect for each skill
     *
     * @param train Trainingweek giving the matches that should be calculated
     * @return TrainingPerPlayer
     */
    public TrainingPerPlayer calculateWeeklyTraining(TrainingPerWeek train) {
        final int playerID = this.getPlayerId();
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
                    if (details != null) {
                        //Get the MatchLineup by id
                        MatchLineupTeam mlt = details.getOwnTeamLineup();
                        if (mlt != null) {
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
                        } else {
                            HOLogger.instance().error(getClass(), "no lineup found in match " + match.getMatchSchedule().toLocaleDateTime() +
                                    " " + match.getHomeTeamName() + " - " + match.getGuestTeamName()
                            );
                        }
                    }
                }
                TrainingPoints trp = new TrainingPoints(wt, tp);

                // get experience increase of national team matches
                var id = this.getNationalTeamId();
                if (id != null && id != 0 && id != myID) {
                    // TODO check if national matches are stored in database
                    var nationalMatches = train.getNTmatches();
                    for (var match : nationalMatches) {
                        MatchLineupTeam mlt = DBManager.instance().loadMatchLineupTeam(match.getMatchType().getId(), match.getMatchID(), this.getNationalTeamId());
                        var minutes = mlt.getTrainingMinutesPlayedInSectors(playerID, null, false);
                        if (minutes > 0) {
                            ret.addExperience(match.getExperienceIncrease(min(90, minutes)));
                        }
                    }
                }
                ret.setTrainingPair(trp);
            } catch (Exception e) {
                HOLogger.instance().log(getClass(), e);
            }
        }
        return ret;
    }

    /**
     * Copy the skills of old player.
     * Used by training
     *
     * @param old player to copy from
     */
    public void copySkills(Player old) {
        for (var s : PlayerSkill.values()) {
            setValue4Skill(s, old.getValue4Skill(s));
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    //equals
    /////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean equals(Object other) {
        boolean equals = false;

        if (other instanceof Player p) {
            equals = p.getPlayerId() == spielerId;
        }

        return equals;
    }

    /**
     * Does this player have a training block?
     *
     * @return training block
     */
    public boolean hasTrainingBlock() {
        return trainingBlock;
    }

    /**
     * Set the training block of this player (true/false)
     *
     * @param isBlocked new value
     */
    public void setTrainingBlock(boolean isBlocked) {
        this.trainingBlock = isBlocked;
    }

    public Integer getNationalTeamId() {
        return nationalTeamId;
    }

    public void setNationalTeamId(Integer id) {
        this.nationalTeamId = id;
    }

    public double getSubExperience() {
        return this.subExperience;
    }

    public void setSubExperience(Double experience) {
        if (experience != null) this.subExperience = experience;
        else this.subExperience = 0;
    }

    public List<FuturePlayerTraining> getFuturePlayerTrainings() {
        if (futurePlayerTrainings == null) {
            futurePlayerTrainings = DBManager.instance().getFuturePlayerTrainings(this.getPlayerId());
            if (!futurePlayerTrainings.isEmpty()) {
                var start = HOVerwaltung.instance().getModel().getBasics().getHattrickWeek();
                var remove = new ArrayList<FuturePlayerTraining>();
                for (var t : futurePlayerTrainings) {
                    if (t.endsBefore(start)) {
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
     * @param wt           used to get priority depending from the player's best position.
     * @param trainingDate the training week
     * @return the training priority
     */
    public FuturePlayerTraining.Priority getFuturePlayerTrainingPriority(WeeklyTrainingType wt, HODateTime trainingDate) {
        for (var t : getFuturePlayerTrainings()) {
            if (t.contains(trainingDate)) {
                return t.getPriority();
            }
        }

        // get training from skill settings
        for (var futureSkillTraining : getFuturePlayerSkillTrainings()) {
            if (wt.isTraining(futureSkillTraining.getSkillId())) {
                return futureSkillTraining.getPriority();
            }
        }

        // get Prio from best position
        int position = HelperWrapper.instance().getPosition(this.getIdealPosition());

        for (var p : wt.getTrainingSkillBonusPositions()) {
            if (p == position) return FuturePlayerTraining.Priority.FULL_TRAINING;
        }
        for (var p : wt.getTrainingSkillPositions()) {
            if (p == position) {
                if (wt.getTrainingType() == TrainingType.SET_PIECES)
                    return FuturePlayerTraining.Priority.PARTIAL_TRAINING;
                return FuturePlayerTraining.Priority.FULL_TRAINING;
            }
        }
        for (var p : wt.getTrainingSkillPartlyTrainingPositions()) {
            if (p == position) return FuturePlayerTraining.Priority.PARTIAL_TRAINING;
        }
        for (var p : wt.getTrainingSkillOsmosisTrainingPositions()) {
            if (p == position) return FuturePlayerTraining.Priority.OSMOSIS_TRAINING;
        }

        return null; // No training
    }

    /**
     * Set training priority for a time interval.
     * Previously saved trainings of this interval are overwritten or deleted.
     *
     * @param prio new training priority for the given time interval
     * @param from first week with new training priority
     * @param to   last week with new training priority, null means open end
     */
    public void setFutureTraining(FuturePlayerTraining.Priority prio, HODateTime from, HODateTime to) {
        var newFuturePlayerTrainings = new ArrayList<FuturePlayerTraining>();
        for (var t : getFuturePlayerTrainings()) {
            var tmpList = t.cut(from, to);
            for (var ft : tmpList) {
                // cut the past
                newFuturePlayerTrainings.addAll(ft.cut(HODateTime.HT_START, HOVerwaltung.instance().getModel().getBasics().getHattrickWeek()));
            }
        }
        if (prio != null) {
            newFuturePlayerTrainings.add(new FuturePlayerTraining(this.getPlayerId(), prio, from, to));
        }
        futurePlayerTrainings = newFuturePlayerTrainings;
        DBManager.instance().storeFuturePlayerTrainings(getPlayerId(), futurePlayerTrainings);
    }

    public String getBestPositionInfo() {
        return MatchRoleID.getNameForPosition(getIdealPosition())
                + " ("
                + getIdealPositionRating()
                + ")";
    }

    public FuturePlayerTraining.Priority getFuturePlayerSkillTrainingPriority(PlayerSkill skillIndex) {
        var s = getFuturePlayerSkillTraining(skillIndex);
        if (s != null) {
            return s.getPriority();
        }
        return null;
    }

    public List<FuturePlayerSkillTraining> getFuturePlayerSkillTrainings() {
        if (futurePlayerSkillTrainings == null) {
            futurePlayerSkillTrainings = DBManager.instance().loadFuturePlayerSkillTrainings(getPlayerId());
        }
        return futurePlayerSkillTrainings;
    }

    public boolean setFutureSkillTrainingPriority(int playerId, PlayerSkill skillIndex, FuturePlayerTraining.Priority prio) {
        var futureSkillTraining = getFuturePlayerSkillTraining(skillIndex);
        if (futureSkillTraining == null) {
            if (prio != null) {
                futureSkillTraining = new FuturePlayerSkillTraining(getPlayerId(), prio, skillIndex);
                futurePlayerSkillTrainings.add(futureSkillTraining);
            } else {
                return false; // nothing changed
            }
        } else if (prio == null) {
            futurePlayerSkillTrainings.remove(futureSkillTraining);
        } else if (!prio.equals(futureSkillTraining.getPriority())) {
            futureSkillTraining.setPriority(prio);
        } else {
            return false; // nothing changed
        }
        DBManager.instance().storeFuturePlayerSkillTrainings(playerId, futurePlayerSkillTrainings);
        return true;
    }

    private FuturePlayerSkillTraining getFuturePlayerSkillTraining(PlayerSkill skillIndex) {
        var skillTrainingPlans = getFuturePlayerSkillTrainings();
        return skillTrainingPlans.stream().filter(e -> e.getSkillId() == skillIndex).findAny().orElse(null);
    }


    /**
     * training priority information of the training panel
     *
     * @param nextWeek training priorities after this week will be considered
     * @return if there is one user selected priority, the name of the priority is returned
     * if there are more than one selected priorities, "individual priorities" is returned
     * if is no user selected priority, the best position information is returned
     */
    public String getTrainingPriorityInformation(HODateTime nextWeek) {
        String ret = null;
        for (var t : getFuturePlayerTrainings()) {
            //
            if (!t.endsBefore(nextWeek)) {
                if (ret != null) {
                    ret = HOVerwaltung.instance().getLanguageString("trainpre.individual.prios");
                    break;
                }
                ret = t.getPriority().toString();
            }
        }
        if (ret != null) return ret;
        return getBestPositionInfo();

    }

    /**
     * Calculates skill status of the player
     *
     * @param previousID    Id of the previous download. Previous player status is loaded by this id.
     * @param trainingWeeks List of training week information
     */
    public void calcSubSkills(int previousID, List<TrainingPerWeek> trainingWeeks) {

        var playerBefore = DBManager.instance().getSpieler(previousID).stream()
                .filter(i -> i.getPlayerId() == this.getPlayerId()).findFirst().orElse(null);
        if (playerBefore == null) {
            playerBefore = this.cloneWithoutSubSkills();
        }
        // since we don't want to work with temp player objects we calculate skill by skill
        // whereas experience is calculated within the first skill
        boolean experienceSubDone = this.getExperience() > playerBefore.getExperience(); // Do not calculate sub on experience skill up
        var experienceSub = experienceSubDone ? 0 : playerBefore.getSubExperience(); // set sub to 0 on skill up
        for (var skill : trainingSkills) {
            var sub = playerBefore.getSub4Skill(skill);
            var valueBeforeTraining = playerBefore.getValue4Skill(skill);
            var valueAfterTraining = this.getValue4Skill(skill);

            if (!trainingWeeks.isEmpty()) {
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
                                if (sub >= 1f) {
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
                            var tp = trainingPerPlayer.getTrainingPair();
                            if (tp != null) {
                                minutes = tp.getTrainingDuration().getPlayedMinutes();
                            } else {
                                HOLogger.instance().warning(getClass(), "no training info found");
                            }
//                            HOLogger.instance().info(getClass(),
//                                    "Training " + training.getTrainingDate().toLocaleDateTime() +
//                                            "; Minutes= " + minutes +
//                                            "; Experience increment of " + this.getFullName() +
//                                            "; increment: " + inc +
//                                            "; new sub value=" + experienceSub
//                            );
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

    /**
     * Schum rank is a player training assessment, created by the hattrick team manager Schum, Russia
     * The following coefficients defines a polynomial fit of the table Schum provided in <a href="https://www88.hattrick.org/Forum/Read.aspx?t=17404127&n=73&v=0&mr=0">...</a>
     * SchumRank(skill) = C0 + C1*skill + C2*skill^2 + C3*skill^3 + C4*skill^4 + C5*skill^5 + C6*skill^6
     */
    Map<PlayerSkill, Double[]> schumRankFitCoefficients = Map.of(
            KEEPER, new Double[]{-2.90430547, 2.20134952, -0.17288917, 0.01490328, 0., 0., 0.},
            DEFENDING, new Double[]{8.78549747, -13.89441249, 7.20818523, -1.42920262, 0.14104285, -0.00660499, 0.00011864},
            WINGER, new Double[]{0.68441693, -0.63873590, 0.42587817, -0.02909820, 0.00108502, 0., 0.},
            PLAYMAKING, new Double[]{-5.70730805, 6.57044707, -1.78506428, 0.27138439, -0.01625170, 0.00036649, 0.},
            SCORING, new Double[]{-6.61486533, 7.65566042, -2.14396084, 0.32264321, -0.01935220, 0.00043442, 0.},
            PASSING, new Double[]{2.61223942, -2.42601757, 0.95573380, -0.07250134, 0.00239775, 0., 0.},
            SETPIECES, new Double[]{-1.54485655, 1.45506372, -0.09224842, 0.00525752, 0., 0., 0.}
    );

    /**
     * Calculated Schum rank.
     * Should be reset, if the player skills are changed
     */
    private Double schumRank = null;

    /**
     * Calculated Schum rank benchmark
     * Should be reset, if the player's ideal position (keeper or not) is set.
     */
    private Double schumRankBenchmark = null;

    /**
     * Calculate the Schum rank
     * Sum of the polynomial functions defined above.
     *
     * @return Double
     */
    private double calcSchumRank() {
        double ret = 0.;
        for (var entry : schumRankFitCoefficients.entrySet()) {
            var value = getSkillValue(entry.getKey());
            var x = 1.;
            for (var c : entry.getValue()) {
                ret += c * x;
                x *= value;
            }
        }
        return ret;
    }

    /**
     * Schum suggests highlighting values in the range 220 up to 240 (or 195 up to 215 in case of keepers)
     *
     * @return true, if schum rank is in this optimal range
     */
    public boolean isExcellentSchumRank() {
        var r = getSchumRank();
        var lower = this.getIdealPosition() == IMatchRoleID.KEEPER ? 195 : 220;
        return r >= lower && r <= lower + 20;
    }

    /**
     * Calculate a Schum rank value, that could be reached with optimal training.
     * It only depends on the player age.
     *
     * @return double
     */
    private double calcSchumRankBenchmark() {
        var ret = getIdealPosition() == IMatchRoleID.KEEPER ? 25. : 50.;
        var k = 1.0;
        for (int age = 17; age < this.getAge(); age++) {
            ret += 16. * k;
            k = 54. / (age + 37);
        }

        return ret + getAgeDays() / 112. * 16. * k;
    }

    public double getSchumRank() {
        if (schumRank == null) schumRank = calcSchumRank();
        return schumRank;
    }

    public double getSchumRankBenchmark() {
        if (schumRankBenchmark == null) schumRankBenchmark = calcSchumRankBenchmark();
        return schumRankBenchmark;
    }

    private Player cloneWithoutSubSkills() {
        var ret = new Player();
        ret.setHrfId(this.hrfId);
        ret.copySkills(this);
        ret.setPlayerId(getPlayerId());
        ret.setAge(getAge());
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
     *
     * @return Integer number of half rating stars
     */
    public Integer getLastMatchRatingEndOfGame() {
        return lastMatchRatingEndOfGame;
    }

    /**
     * Rating at end of game
     *
     * @param lastMatchRatingEndOfGame number of half rating stars
     */
    public void setLastMatchRatingEndOfGame(Integer lastMatchRatingEndOfGame) {
        this.lastMatchRatingEndOfGame = lastMatchRatingEndOfGame;
    }

    public void setMotherClubId(Integer teamID) {
        this.motherClubId = teamID;
    }

    public void setMotherClubName(String teamName) {
        this.motherClubName = teamName;
    }

    public void setCurrentTeamMatches(Integer currentTeamMatches) {
        this.currentTeamMatches = currentTeamMatches;
    }

    public Integer getCurrentTeamMatches() {
        return this.currentTeamMatches;
    }

    private final Player playerAsManMarker = null;
    private ManMarkingPosition manMarkingPosition = null;

    /**
     * Create a clone of the player with modified skill values.
     * Values of Defending, Winger, Playmaking, Scoring and Passing are reduced depending on the distance
     * between man marker and opponent man marked player
     *
     * @param manMarkingPosition null - no man marking changes
     *                           Opposite - reduce skills by 50%
     *                           NotOpposite - reduce skills by 65%
     *                           NotInLineup - reduce skills by 10%
     * @return this player, if no man marking changes are selected
     * New modified player, if man marking changes are selected
     */
    public Player getPlayerAsManMarker(ManMarkingPosition manMarkingPosition) {
        if (manMarkingPosition == null) return this;
        else if (manMarkingPosition == this.manMarkingPosition) return playerAsManMarker;
        this.manMarkingPosition = manMarkingPosition;
        var playerAsManMarker = new Player();
        var skillFactor = (float) (1 - manMarkingPosition.value / 100.);
        playerAsManMarker.setSpecialty(this.getSpecialty());
        playerAsManMarker.setAgeDays(this.getAgeDays());
        playerAsManMarker.setAge(this.getAge());
        playerAsManMarker.setAggressivity(this.getAggressivity());
        playerAsManMarker.setHonesty(this.getHonesty());
        playerAsManMarker.setGentleness(this.getGentleness());
        playerAsManMarker.setExperience(this.getExperience());
        playerAsManMarker.setSubExperience(this.getSubExperience());
        playerAsManMarker.setFirstName(this.getFirstName());
        playerAsManMarker.setLastName(this.getLastName());
        playerAsManMarker.setForm(this.getForm());
        playerAsManMarker.setLeadership(this.getLeadership());
        playerAsManMarker.setStamina(this.getStamina());
        playerAsManMarker.setLoyalty(this.getLoyalty());
        playerAsManMarker.setHomeGrown(this.isHomeGrown());
        playerAsManMarker.setPlayerId(this.getPlayerId());
        playerAsManMarker.setInjuryWeeks(this.getInjuryWeeks());

        playerAsManMarker.setSkillValue(KEEPER, this.getSkillValue(KEEPER));
        playerAsManMarker.setSkillValue(DEFENDING, skillFactor * this.getSkillValue(DEFENDING));
        playerAsManMarker.setSkillValue(WINGER, skillFactor * this.getSkillValue(WINGER));
        playerAsManMarker.setSkillValue(PLAYMAKING, skillFactor * this.getSkillValue(PLAYMAKING));
        playerAsManMarker.setSkillValue(SCORING, skillFactor * this.getSkillValue(SCORING));
        playerAsManMarker.setSkillValue(PASSING, skillFactor * this.getSkillValue(PASSING));
        playerAsManMarker.setSkillValue(STAMINA, this.getSkillValue(STAMINA));
        playerAsManMarker.setSkillValue(FORM, this.getSkillValue(FORM));
        playerAsManMarker.setSkillValue(SETPIECES, this.getSkillValue(SETPIECES));
        playerAsManMarker.setSkillValue(LEADERSHIP, this.getSkillValue(LEADERSHIP));
        playerAsManMarker.setSkillValue(LOYALTY, this.getSkillValue(LOYALTY));
        return playerAsManMarker;
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

        ManMarkingPosition(int v) {
            this.value = v;
        }

        public static ManMarkingPosition fromId(int id) {
            return switch (id) {
                case 50 -> Opposite;
                case 65 -> NotOpposite;
                case 10 -> NotInLineup;
                default -> null;
            };
        }

        public int getValue() {
            return value;
        }
    }

    List<SkillChange> getSkillChanges() {
        if (skillChanges == null) {
            skillChanges = new ArrayList<>();
            Player previousPlayer = null;
            for (var p : DBManager.instance().loadPlayerHistory(this.getPlayerId())) {
                if (previousPlayer != null) {
                    for (var skillType : PlayerSkill.values()) {
                        var newValue = p.getValue4Skill(skillType);
                        var change = newValue - previousPlayer.getValue4Skill(skillType);
                        if (change != 0) {
                            var skillChange = new SkillChange();
                            skillChange.setChange(change);
                            skillChange.setDate(p.getHrfDate());
                            skillChange.setType(skillType);
                            skillChange.setValue(newValue);
                            skillChanges.add(skillChange);
                        }
                    }
                }
                previousPlayer = p;
            }
        }
        return skillChanges;
    }
}