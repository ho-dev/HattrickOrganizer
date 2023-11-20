package core.model.player

import core.constants.TrainingType
import core.constants.player.PlayerSkill
import core.constants.player.Specialty
import core.db.AbstractTable.Storable
import core.db.DBManager.TSIDATE
import core.db.DBManager.getAllLevelUp
import core.db.DBManager.getFuturePlayerTrainings
import core.db.DBManager.getLastLevelUp
import core.db.DBManager.getSpieler
import core.db.DBManager.loadLatestTSIInjured
import core.db.DBManager.loadLatestTSINotInjured
import core.db.DBManager.loadMatchLineupTeam
import core.db.DBManager.loadPlayerNotes
import core.db.DBManager.storeFuturePlayerTrainings
import core.db.DBManager.storePlayerNotes
import core.db.DBManager.storeSkillup
import core.model.HOVerwaltung
import core.model.UserParameter
import core.model.WorldDetailsManager
import core.model.enums.MatchType
import core.model.match.MatchLineupPosition
import core.model.player.PlayerCategory.Companion.valueOf
import core.model.player.TrainerType.Companion.fromInt
import core.net.MyConnector
import core.net.OnlineWorker.downloadPlayerDetails
import core.rating.RatingPredictionModel
import core.training.*
import core.util.HODateTime
import core.util.HODateTime.HODuration
import core.util.HOLogger
import core.util.HelperWrapper
import core.util.Htms
import org.apache.commons.lang3.math.NumberUtils
import java.time.Duration
import java.util.*
import java.util.function.Predicate
import kotlin.math.min

open class Player : Storable {
    private var idealPos = IMatchRoleID.UNKNOWN

    /**
     * Name
     */
    @JvmField
    var firstName: String? = ""

    @JvmField
    var nickName: String? = ""

    @JvmField
    var lastName: String? = ""

    /**
     * Arrival in team
     */
    @JvmField
    var arrivalDate: String? = null

    /**
     * Trainer contract date
     */
    var contractDate: String? = null

    /**
     * Download date
     */
    var hrfDate: HODateTime? = null
        get() {
            if (field == null) {
                field = HOVerwaltung.instance().model.getBasics().datum
            }
            return field
        }

    /**
     * The player is no longer available in the current HRF
     */
    var isGoner = false

    /**
     * Wing skill
     */
    private var subWingerSkill = 0.0

    /**
     * Pass skill
     */
    private var subPassingSkill = 0.0

    /**
     * Playmaking skill
     */
    private var subPlaymakingSkill = 0.0

    /**
     * Standards
     */
    private var subSetPiecesSkill = 0.0

    /**
     * Goal
     */
    private var subScoringSkill = 0.0

    //Subskills
    private var subKeeperSkill = 0.0

    /**
     * Verteidigung
     */
    private var subDefendingSkill = 0.0

    var agressivity = 0

    /**
     * Alter
     */
    var age = 0
        set(a) {
            schumRankBenchmark = null
            field = a
        }

    /**
     * Age Days
     */
    var ageDays = 0
        set(age) {
            schumRankBenchmark = null
            field = age
        }

    var honesty = 1

    var rating = 0

    @JvmField
    var gentleness = 1

    @JvmField
    var experience = 1

    @JvmField
    var form = 1

    @JvmField
    var leadership = 1

    @JvmField
    var wage = 1

    @JvmField
    var totalCards = 0

    /**
     * Hattricks
     */
    @JvmField
    var hatTricks = 0

    @JvmField
    var currentTeamGoals = 0

    var homeGrown = false

    @JvmField
    var stamina = 1

    var internationalMatches = 0

    @JvmField
    var loyalty = 0

    @JvmField
    var tsi = 0

    var nationality: String? = null
        get() {
            if (field != null) {
                return field
            }
            val leagueDetail = WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(nationalityId)
            field = if (leagueDetail != null) {
                leagueDetail.countryName
            } else {
                ""
            }
            return field
        }
        private set

    @JvmField
    var nationalityId = 49

    var passingSkill = 1
        set(skill) {
            schumRank = null
            field = skill
        }

    @JvmField
    var specialty = 0

    /**
     * Spielaufbau
     */
    var playmakingSkill = 1
        set(skill) {
            schumRank = null
            field = skill
        }

    @JvmField
    var playerId = 0

    /**
     * Standards
     */
    var setPiecesSkill = 1
        set(skill) {
            schumRank = null
            field = skill
        }

    @JvmField
    var friendlyGoals = 0

    @JvmField
    var totalGoals = 0

    @JvmField
    var leagueGoals = 0

    @JvmField
    var cupGameGoals = 0

    /**
     * Torschuss
     */
    var scoringSkill = 1
        set(skill) {
            schumRank = null
            field = skill
        }

    /**
     * Torwart
     */
    var goalkeeperSkill = 1
        set(skill) {
            schumRank = null
            field = skill
        }

    /**
     * Fluegelspiel
     */
    var wingerSkill = 1
        set(skill) {
            schumRank = null
            field = skill
        }

    /**
     * Verteidigung
     */
    var defendingSkill = 1
        set(skill) {
            schumRank = null
            field = skill
        }

    @JvmField
    var coachSkill = 0

    @JvmField
    var trainerType: TrainerType? = null

    @JvmField
    var transferListed = 0

    @JvmField
    var shirtNumber = -1

    /**
     * player's category (can be edited in hattrick)
     */
    @JvmField
    var playerCategory: PlayerCategory? = null

    /**
     * player statement (can be edited in hattrick)
     */
    @JvmField
    var playerStatement: String? = null

    /**
     * Owner notes (can be edited in hattrick)
     */
    @JvmField
    var ownerNotes: String? = null

    var u20InternationalMatches = 0

    @JvmField
    var injuryWeeks = -1

    /**
     * Training block
     */
    private var trainingBlock = false
    /**
     * Last match
     * @return date
     */
    /**
     * Last match
     */
    @JvmField
    var lastMatchDate: String? = null

    /**
     * Last match id
     * @return id
     */
    @JvmField
    var lastMatchId: Int? = null
    /**
     * Returns the [MatchType] of the last match.
     */
    /**
     * Sets the value of `lastMatchType` to `matchType`.
     */
    @JvmField
    var lastMatchType: MatchType? = null

    @JvmField
    var lastMatchPosition: Int? = null

    @JvmField
    var lastMatchMinutes: Int? = null

    /**
     * Last match
     * @return rating
     */
    // Rating is number of half stars
    // real rating value is rating/2.0f
    @JvmField
    var lastMatchRating: Int? = null
    /**
     * Rating at end of game
     * @return Integer number of half rating stars
     */
    /**
     * Rating at end of game
     * @param lastMatchRatingEndOfGame number of half rating stars
     */
    @JvmField
    var lastMatchRatingEndOfGame: Int? = null

    @JvmField
    var nationalTeamId: Int? = null
    var subExperience: Double = 0.0

    /**
     * future training priorities planed by the user
     */
    private var futurePlayerTrainings: List<FuturePlayerTraining?>? = null

    var motherClubId: Int? = null
        set
    var motherClubName: String? = null

    @JvmField
    var currentTeamMatches: Int? = null
    var hrfId = 0
    var htms: Int? = null
        get() {
            if (field == null) {
                field = Htms.htms(getSkills())
            }
            return field
        }
        private set
    var htms28: Int? = null
        get() {
            if (field == null) {
                field = Htms.htms28(getSkills(), age, ageDays)
            }
            return field
        }
        private set

    /**
     * Externally recruited coaches are no longer allowed to be part of the lineup
     */
    private var lineupDisabled = false
    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new instance of Player
     */
    constructor()

    /**
     * Erstellt einen Player aus den Properties einer HRF Datei
     */
    constructor(properties: Properties, hoDateTime: HODateTime?, hrfId: Int) {
        // Separate first, nick and last names are available. Utilize them?
        this.hrfId = hrfId
        playerId = properties.getProperty("id", "0").toInt()
        firstName = properties.getProperty("firstname", "")
        nickName = properties.getProperty("nickname", "")
        lastName = properties.getProperty("lastname", "")
        arrivalDate = properties.getProperty("arrivaldate")
        age = properties.getProperty("ald", "0").toInt()
        ageDays = properties.getProperty("agedays", "0").toInt()
        stamina = properties.getProperty("uth", "0").toInt()
        form = properties.getProperty("for", "0").toInt()
        goalkeeperSkill = properties.getProperty("mlv", "0").toInt()
        defendingSkill = properties.getProperty("bac", "0").toInt()
        playmakingSkill = properties.getProperty("spe", "0").toInt()
        passingSkill = properties.getProperty("fra", "0").toInt()
        wingerSkill = properties.getProperty("ytt", "0").toInt()
        scoringSkill = properties.getProperty("mal", "0").toInt()
        setPiecesSkill = properties.getProperty("fas", "0").toInt()
        specialty = properties.getProperty("speciality", "0").toInt()
        gentleness = properties.getProperty("gentleness", "0").toInt()
        honesty = properties.getProperty("honesty", "0").toInt()
        agressivity = properties.getProperty("aggressiveness", "0").toInt()
        experience = properties.getProperty("rut", "0").toInt()
        homeGrown = properties.getProperty("homegr", "FALSE").toBoolean()
        loyalty = properties.getProperty("loy", "0").toInt()
        leadership = properties.getProperty("led", "0").toInt()
        wage = properties.getProperty("sal", "0").toInt()
        nationalityId = properties.getProperty("countryid", "0").toInt()
        tsi = properties.getProperty("mkt", "0").toInt()

        // also read subskills when importing hrf from hattrickportal.pro/ useful for U20/NT
        subWingerSkill = properties.getProperty("yttsub", "0").toDouble()
        subPassingSkill = properties.getProperty("frasub", "0").toDouble()
        subPlaymakingSkill = properties.getProperty("spesub", "0").toDouble()
        subSetPiecesSkill = properties.getProperty("fassub", "0").toDouble()
        subScoringSkill = properties.getProperty("malsub", "0").toDouble()
        subKeeperSkill = properties.getProperty("mlvsub", "0").toDouble()
        subDefendingSkill = properties.getProperty("bacsub", "0").toDouble()
        subExperience = properties.getProperty("experiencesub", "0").toDouble()

        //TSI, alles vorher durch 1000 teilen
        hrfDate = hoDateTime
        if (hoDateTime != null && hoDateTime.isBefore(HODateTime.fromDbTimestamp(TSIDATE))) {
            tsi = (tsi / 1000.0).toInt()
        }
        totalCards = properties.getProperty("warnings", "0").toInt()
        injuryWeeks = properties.getProperty("ska", "0").toInt()
        friendlyGoals = properties.getProperty("gtt", "0").toInt()
        leagueGoals = properties.getProperty("gtl", "0").toInt()
        cupGameGoals = properties.getProperty("gtc", "0").toInt()
        totalGoals = properties.getProperty("gev", "0").toInt()
        hatTricks = properties.getProperty("hat", "0").toInt()
        currentTeamGoals = properties.getProperty("goalscurrentteam", "0").toInt()
        currentTeamMatches = properties.getProperty("matchescurrentteam", "0").toInt()
        lineupDisabled = getBooleanIfNotNull(properties, "lineupdisabled")
        rating = getIntegerIfNotNull(properties, "rating", 0)
        trainerType = fromInt(getIntegerIfNotNull(properties, "trainertype", -1))
        coachSkill = getIntegerIfNotNull(properties, "trainerskilllevel", 0)
        if (coachSkill > 0) {
            coachSkill += 3 // trainer level 5 is an excellent (8) trainer
            wage = properties.getProperty("cost", "0").toInt()
            contractDate = properties.getProperty("contractdate")
        }
        val temp = properties.getProperty("playernumber", "")
        if (temp != null && !temp.isEmpty() && temp != "null") {
            shirtNumber = temp.toInt()
        }
        transferListed = if (properties.getProperty("transferlisted", "False").toBoolean()) 1 else 0
        internationalMatches = properties.getProperty("caps", "0").toInt()
        u20InternationalMatches = properties.getProperty("capsu20", "0").toInt()
        nationalTeamId = getIntegerIfNotNull(properties, "nationalteamid", 0)

        // #461-lastmatch
        lastMatchDate = properties.getProperty("lastmatch_date")
        if (lastMatchDate != null && !lastMatchDate!!.isEmpty()) {
            lastMatchId = properties.getProperty("lastmatch_id", "0").toInt()
            val pos = properties.getProperty("lastmatch_positioncode", "-1").toInt()
            if (MatchRoleID.isFieldMatchRoleId(pos)) {
                lastMatchPosition = pos
            }
            lastMatchMinutes = properties.getProperty("lastmatch_playedminutes", "0").toInt()
            // rating is stored as number of half stars
            lastMatchRating = (2 * properties.getProperty("lastmatch_rating", "0").toDouble()).toInt()
            lastMatchRatingEndOfGame = (2 * properties.getProperty("lastmatch_ratingendofgame", "0").toDouble()).toInt()
        }
        lastMatchType = MatchType.getById(properties.getProperty("lastmatch_type", "0").toInt())
        playerCategory = valueOf(NumberUtils.toInt(properties.getProperty("playercategoryid"), 0))
        playerStatement = properties.getProperty("statement", "")
        ownerNotes = properties.getProperty("ownernotes", "")

        //Subskills calculation
        //Called when saving the HRF because the necessary data is not available here
        val oldmodel = HOVerwaltung.instance().model
        val oldPlayer = oldmodel.getCurrentPlayer(playerId)
        if (oldPlayer != null) {
            // Training blocked (could be done in the past)
            trainingBlock = oldPlayer.hasTrainingBlock()
            motherClubId = oldPlayer.getOrDownloadMotherclubId()
            motherClubName = oldPlayer.getOrDownloadMotherClubName()
        }
    }

    private fun getIntegerIfNotNull(properties: Properties, key: String, defaultValue: Int): Int {
        val value = properties.getProperty(key)
        return if (value == null || value.isEmpty()) {
            defaultValue
        } else value.toInt()
    }

    private fun getBooleanIfNotNull(properties: Properties, key: String): Boolean {
        val value = properties.getProperty(key)
        return if (value == null || value.isEmpty()) {
            false
        } else value.toBoolean()
    }

    fun getOrDownloadMotherClubName(): String? {
        downloadMotherclubInfoIfMissing()
        return motherClubName
    }

    fun getOrDownloadMotherclubId(): Int? {
        downloadMotherclubInfoIfMissing()
        return motherClubId
    }

    private fun downloadMotherclubInfoIfMissing() {
        if (motherClubId == null) {
            val connection = MyConnector.instance()
            val isSilentDownload = connection.isSilentDownload
            try {
                connection.setSilentDownload(true)
                // try to download missing mother club info
                val playerDetails = downloadPlayerDetails(playerId.toString())
                if (playerDetails != null) {
                    motherClubId = playerDetails.motherClubId
                    motherClubName = playerDetails.motherClubName
                }
            } catch (e: Exception) {
                HOLogger.instance().warning(javaClass, "mother club not available for player " + getFullName())
            } finally {
                connection.setSilentDownload(isSilentDownload) // reset
            }
        }
    }

    /**
     * Calculates full age with days and offset
     *
     * @return Double value of age & agedays & offset combined,
     * i.e. age + (agedays+offset)/112
     */
    fun getAgeWithAgeDays(): Double {
        val now = HODateTime.now()
        return getDoubleAgeFromDate(now)
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
    fun getDoubleAgeFromDate(t: HODateTime): Double {
        val hrfTime = HOVerwaltung.instance().model.getBasics().datum
        val diff = Duration.between(hrfTime.instant, t.instant)
        val years = age
        val days = ageDays
        return years + (days + diff.toDays()).toDouble() / 112
    }

    /**
     * Calculates String for full age and days correcting for the difference between (now and last HRF file)
     *
     * @return String of age & agedays format is "YY (DDD)"
     */
    fun getAgeWithDaysAsString(): String = getAgeWithDaysAsString(HODateTime.now())

    fun getAgeWithDaysAsString(t: HODateTime?): String {
        return getAgeWithDaysAsString(age, ageDays, t, hrfDate)
    }

    /**
     * Get the full i18n'd string representing the player's age. Includes
     * the birthday indicator as well.
     *
     * @return the full i18n'd string representing the player's age
     */
    fun getAgeStringFull(): String {
        val hrfTime = HOVerwaltung.instance().model.getBasics().datum
        val oldAge = HODuration(age, ageDays)
        val age = oldAge.plus(HODuration.between(hrfTime, HODateTime.now()))
        val birthday = oldAge.seasons != age.seasons
        val ret = StringBuilder()
        ret.append(age.seasons)
        ret.append(" ")
        ret.append(HOVerwaltung.instance().getLanguageString("ls.player.age.years"))
        ret.append(" ")
        ret.append(age.days)
        ret.append(" ")
        ret.append(HOVerwaltung.instance().getLanguageString("ls.player.age.days"))
        if (birthday) {
            ret.append(" (")
            ret.append(HOVerwaltung.instance().getLanguageString("ls.player.age.birthday"))
            ret.append(")")
        }
        return ret.toString()
    }

    fun getBonus(): Int {
        var bonus = 0
        if (nationalityId != HOVerwaltung.instance().model.getBasics().land) {
            bonus = 20
        }
        return bonus
    }

    fun isRedCarded(): Boolean = totalCards > 2

    fun setHrfDate() {
        hrfDate = HODateTime.now()
    }

    fun getIdealPositionRating(): Double {
        val maxRating = getMaxRating()
        return maxRating?.rating ?: 0.0
    }

    private fun getMaxRating(): PlayerPositionRating? {
        val maxRating =
            getAllPositionRatings().stream().max(Comparator.comparing { obj: PlayerPositionRating? -> obj!!.rating })
        return maxRating.orElse(null)
    }

    fun getIdealMatchLineupPosition(): MatchLineupPosition? {
        val r = getMaxRating()
        return if (r != null) {
            MatchLineupPosition(r.roleId, playerId, r.behaviour.toInt())
        } else null
    }

    fun getIdealPosition(): Byte {
        //in case player best position is forced by user
        val flag = getUserPosFlag()
        if (flag == IMatchRoleID.UNKNOWN.toInt()) {
            if (idealPos == IMatchRoleID.UNKNOWN) {
                val matchLineupPosition = getIdealMatchLineupPosition()
                idealPos = MatchRoleID.getPosition(matchLineupPosition!!.roleId, matchLineupPosition.behaviour)
            }
            return idealPos
        }
        return flag.toByte()
    }

    fun getPositionRating(position: Byte): Double {
        val ratingPredictionModel = HOVerwaltung.instance().model.getRatingPredictionModel()
        return ratingPredictionModel.getPlayerMatchAverageRating(this, position)
    }

    class PlayerPositionRating(var roleId: Int, var behaviour: Byte, var rating: Double, val relativeRating: Double)

    fun getAllPositionRatings(): List<PlayerPositionRating?> {
        val ret = ArrayList<PlayerPositionRating?>()
        val ratingPredictionModel = HOVerwaltung.instance().model.getRatingPredictionModel()
        for (p in RatingPredictionModel.playerRatingPositions) {
            for (behaviour in MatchRoleID.getBehaviours(p)) {
                val d = ratingPredictionModel.getPlayerMatchAverageRating(this, p, behaviour)
                ret.add(
                    PlayerPositionRating(
                        p,
                        behaviour,
                        d,
                        ratingPredictionModel.calcRelativePlayerRating(this, p, behaviour, 0)
                    )
                )
            }
        }
        return ret
    }

    /**
     * Calculate player alternative best positions (weather impact not relevant here)
     */
    fun getAlternativeBestPositions(): List<Byte> {
        var threshold: Double? = null
        val tolerance = 1f - UserParameter.instance().alternativePositionsTolerance
        val ret = ArrayList<Byte>()
        val allPositionRatings = getAllPositionRatings().stream().sorted(
            Comparator.comparing(
                { obj: PlayerPositionRating? -> obj!!.rating }, Comparator.reverseOrder()
            )
        ).toList()
        for (p in allPositionRatings) {
            if (threshold == null) {
                threshold = p!!.rating * tolerance
                ret.add(MatchRoleID.getPosition(p.roleId, p.behaviour))
            } else if (p!!.rating >= threshold) {
                ret.add(MatchRoleID.getPosition(p.roleId, p.behaviour))
            } else {
                break
            }
        }
        return ret
    }

    /**
     * return whether the position is one of the best position for the player
     */
    fun isAnAlternativeBestPosition(position: Byte): Boolean {
        return getAlternativeBestPositions().contains(position)
    }

    private val lastSkillups = HashMap<Int, Skillup?>()

    /**
     * liefert das Datum des letzen LevelAufstiegs für den angeforderten Skill [0] = Time der
     * Änderung [1] = Boolean: false=Keine Änderung gefunden
     */
    fun getLastLevelUp(skill: Int): Skillup? {
        if (lastSkillups.containsKey(skill)) {
            return lastSkillups[skill]
        }
        val ret = getLastLevelUp(skill, playerId)
        lastSkillups[skill] = ret
        return ret
    }

    private val allSkillUps = HashMap<Int, List<Skillup?>>()

    /**
     * gives information of skill ups
     */
    fun getAllLevelUp(skill: Int): List<Skillup?> {
        if (allSkillUps.containsKey(skill)) {
            return allSkillUps[skill]!!
        }
        val ret = getAllLevelUp(skill, playerId)
        allSkillUps[skill] = ret
        return ret
    }

    fun resetSkillUpInformation() {
        lastSkillups.clear()
        allSkillUps.clear()
    }

    /**
     * Setter for property m_sManuellerSmilie.
     *
     * @param manuellerSmilie New value of property m_sManuellerSmilie.
     */
    fun setManuellerSmilie(manuellerSmilie: String?) {
        getNotes().manuelSmilie = manuellerSmilie
        storePlayerNotes(notes!!)
    }

    fun getInfoSmiley(): String? = getNotes().manuelSmilie

    fun setFirstName(m_sName: String?) {
        firstName = if (m_sName != null) m_sName else ""
    }

    fun getFirstName(): String? {
        return firstName
    }

    fun setNickName(m_sName: String?) {
        if (m_sName != null) nickName = m_sName else nickName = ""
    }

    fun getNickName(): String? {
        return nickName
    }

    fun setLastName(m_sName: String?) {
        if (m_sName != null) lastName = m_sName else lastName = ""
    }

    fun getLastName(): String? {
        return lastName
    }

    /**
     * Getter for shortName
     * eg: James Bond = J. Bond
     * Nickname are ignored
     */
    fun getShortName(): String? = if (getFirstName().isNullOrEmpty()) {
        getLastName()
    } else getFirstName()?.get(0).toString() + ". " + getLastName()

    fun getFullName(): String = if (getNickName().isNullOrEmpty()) {
        getFirstName() + " " + getLastName()
    } else getFirstName() + " '" + getNickName() + "' " + getLastName()


    /**
     * Zum speichern! Die Reduzierung des Marktwerts auf TSI wird rückgängig gemacht
     */
    fun getMarketValue(): Int = if (hrfDate == null || hrfDate!!.isBefore(HODateTime.fromDbTimestamp(TSIDATE))) {
        //Echter Marktwert
        tsi * 1000
    } else tsi

    //TSI
    var latestTSIInjured: String = loadLatestTSIInjured(playerId)

    var latestTSINotInjured: String = loadLatestTSINotInjured(playerId)

    fun hasSpecialty(speciality: Specialty): Boolean {
        val s = Specialty.entries[specialty]
        return s == speciality
    }

    // returns the name of the speciality in the used language
    private fun getSpecialtyName(): String {
        val s = Specialty.entries[specialty]
        return if (s == Specialty.NO_SPECIALITY) {
            EMPTY
        } else {
            HOVerwaltung.instance().getLanguageString("ls.player.speciality." + s.toString().lowercase())
        }
    }

    // return the name of the speciality with a break before and in brackets
    fun getSpecialtyExportName(): String {
        val s = Specialty.entries[specialty]
        return if (s == Specialty.NO_SPECIALITY) {
            EMPTY
        } else {
            BREAK + O_BRACKET + getSpecialtyName() + C_BRACKET
        }
    }

    // no break so that the export looks better
    fun getSpecialtyExportNameForKeeper(): String {
        val s = Specialty.entries[specialty]
        return if (s == Specialty.NO_SPECIALITY) {
            EMPTY
        } else {
            O_BRACKET + getSpecialtyName() + C_BRACKET
        }
    }


    var canBeSelectedByAssistant: Boolean
        /**
         * get whether that player can be selected by the assistant
         */
        get() = !isLineupDisabled() && getNotes().isEligibleToPlay
        /**
         * set whether that player can be selected by the assistant
         */
        set(flag) {
            var cur = flag
            if (isLineupDisabled()) cur = false
            getNotes().isEligibleToPlay = cur
            storePlayerNotes(notes!!)
        }

    /**
     * Get skill value including subskill
     * @param iSkill skill id
     * @return double
     */
    fun getSkill(iSkill: Int): Double {
        return getValue4Skill(iSkill) + getSub4Skill(iSkill)
    }

    /**
     * Returns accurate subskill number. If you need subskill for UI
     * purpose it is better to use getSubskill4Pos()
     *
     * @param skill skill number
     * @return subskill between 0.0-0.999
     */
    fun getSub4Skill(skill: Int): Double {
        val value: Double = when (skill) {
            PlayerSkill.KEEPER -> subKeeperSkill
            PlayerSkill.PLAYMAKING -> subPlaymakingSkill
            PlayerSkill.DEFENDING -> subDefendingSkill
            PlayerSkill.PASSING -> subPassingSkill
            PlayerSkill.WINGER -> subWingerSkill
            PlayerSkill.SCORING -> subScoringSkill
            PlayerSkill.SET_PIECES -> subSetPiecesSkill
            PlayerSkill.EXPERIENCE -> subExperience
            PlayerSkill.STAMINA -> 0.5
            else -> 0.0
        }
        return min(0.999, value)
    }

    fun setSubskill4PlayerSkill(skill: Int, value: Double) {
        schumRank = null
        when (skill) {
            PlayerSkill.KEEPER -> subKeeperSkill = value
            PlayerSkill.PLAYMAKING -> subPlaymakingSkill = value
            PlayerSkill.DEFENDING -> subDefendingSkill = value
            PlayerSkill.PASSING -> subPassingSkill = value
            PlayerSkill.WINGER -> subWingerSkill = value
            PlayerSkill.SCORING -> subScoringSkill = value
            PlayerSkill.SET_PIECES -> subSetPiecesSkill = value
            PlayerSkill.EXPERIENCE -> subExperience = value
        }
    }

    /**
     * Setter for property m_sTeamInfoSmilie.
     *
     * @param teamInfoSmilie New value of property m_sTeamInfoSmilie.
     */
    fun setTeamInfoSmilie(teamInfoSmilie: String) {
        getNotes().teamInfoSmilie = teamInfoSmilie
        storePlayerNotes(notes!!)
    }

    fun getTeamGroup(): String? {
        val ret = getNotes().teamInfoSmilie
        return ret?.replace("\\.png$".toRegex(), "")
    }

    /**
     * Returns `true` if the player is a coach, `false` otherwise.
     */
    fun isCoach(): Boolean = coachSkill > 0 && trainerType != null

    private fun getSkills(): Map<Int, Int> {
        val ret = HashMap<Int, Int>()
        ret[PlayerSkill.KEEPER] = goalkeeperSkill
        ret[PlayerSkill.DEFENDING] = defendingSkill
        ret[PlayerSkill.PLAYMAKING] = playmakingSkill
        ret[PlayerSkill.WINGER] = wingerSkill
        ret[PlayerSkill.PASSING] = passingSkill
        ret[PlayerSkill.SCORING] = scoringSkill
        ret[PlayerSkill.SET_PIECES] = setPiecesSkill
        return ret
    }

    fun isLineupDisabled(): Boolean {
        return lineupDisabled
    }

    fun setLineupDisabled(lineupDisabled: Boolean?) {
        if (lineupDisabled != null) {
            this.lineupDisabled = lineupDisabled
        } else {
            this.lineupDisabled = false
        }
    }

    class Notes : Storable {
        constructor()

        var playerId = 0

        constructor(playerId: Int) {
            this.playerId = playerId
        }

        var userPos = IMatchRoleID.UNKNOWN.toInt()
        var manuelSmilie: String? = ""
        var note: String? = ""
        var isEligibleToPlay = true
        var teamInfoSmilie: String? = ""

        @JvmField
        var isFired = false
    }

    private var notes: Notes? = null
    private fun getNotes(): Notes {
        if (notes == null) {
            notes = loadPlayerNotes(playerId)
        }
        return notes!!
    }

    fun setUserPosFlag(flag: Byte) {
        schumRankBenchmark = null
        getNotes().userPos = flag.toInt()
        storePlayerNotes(notes!!)
        canBeSelectedByAssistant = flag != IMatchRoleID.UNSELECTABLE
    }

    fun isFired(): Boolean = getNotes().isFired

    fun setFired(b: Boolean) {
        getNotes().isFired = b
        storePlayerNotes(notes!!)
    }

    /**
     * liefert User Notiz zum Player
     */
    fun getUserPosFlag(): Int = getNotes().userPos
    fun getNote(): String? = getNotes().note

    fun setNote(text: String) {
        getNotes().note = text
        storePlayerNotes(notes!!)
    }

    /**
     * get Skillvalue 4 skill
     */
    fun getValue4Skill(skill: Int): Int {
        return when (skill) {
            PlayerSkill.KEEPER -> goalkeeperSkill
            PlayerSkill.PLAYMAKING -> playmakingSkill
            PlayerSkill.DEFENDING -> defendingSkill
            PlayerSkill.PASSING -> passingSkill
            PlayerSkill.WINGER -> wingerSkill
            PlayerSkill.SCORING -> scoringSkill
            PlayerSkill.SET_PIECES -> setPiecesSkill
            PlayerSkill.STAMINA -> stamina
            PlayerSkill.EXPERIENCE -> experience
            PlayerSkill.FORM -> form
            PlayerSkill.LEADERSHIP -> leadership
            PlayerSkill.LOYALTY -> loyalty
            else -> 0
        }
    }

    fun getSkillValue(skill: Int): Double {
        return getSub4Skill(skill) + getValue4Skill(skill)
    }

    fun setSkillValue(skill: Int, value: Double) {
        val intVal = value.toInt()
        setValue4Skill(skill, intVal)
        setSubskill4PlayerSkill(skill, value - intVal)
    }

    /**
     * set Skillvalue 4 skill
     *
     * @param skill the skill to change
     * @param value the new skill value
     */
    fun setValue4Skill(skill: Int, value: Int) {
        schumRank = null
        when (skill) {
            PlayerSkill.KEEPER -> goalkeeperSkill = value
            PlayerSkill.PLAYMAKING -> playmakingSkill = value
            PlayerSkill.PASSING -> passingSkill = value
            PlayerSkill.WINGER -> wingerSkill = value
            PlayerSkill.DEFENDING -> defendingSkill = value
            PlayerSkill.SCORING -> scoringSkill = value
            PlayerSkill.SET_PIECES -> setPiecesSkill = value
            PlayerSkill.STAMINA -> stamina = value
            PlayerSkill.EXPERIENCE -> experience = value
            PlayerSkill.FORM -> form = value
            PlayerSkill.LEADERSHIP -> leadership = value
            PlayerSkill.LOYALTY -> loyalty = value
        }
    }

    /**
     * Calculates training effect for each skill
     *
     * @param train  Trainingweek giving the matches that should be calculated
     *
     * @return TrainingPerPlayer
     */
    fun calculateWeeklyTraining(train: TrainingPerWeek?): TrainingPerPlayer {
        val playerID = playerId
        val ret = TrainingPerPlayer(this)
        ret.trainingWeek = train
        if (train == null || train.trainingType < 0) {
            return ret
        }
        val wt = WeeklyTrainingType.instance(train.trainingType)
        if (wt != null) {
            try {
                val matches = train.getMatches()
                val myID = HOVerwaltung.instance().model.getBasics().teamId
                val tp = TrainingWeekPlayer(this)
                for (match in matches) {
                    val details = match.getMatchdetails()
                    if (details != null) {
                        //Get the MatchLineup by id
                        val mlt = details.getOwnTeamLineup()
                        if (mlt != null) {
                            val type = mlt.getMatchType()
                            val walkoverWin = details.isWalkoverMatchWin(myID)
                            if (type != MatchType.MASTERS) { // MASTERS counts only for experience
                                tp.addFullTrainingMinutes(
                                    mlt.getTrainingMinutesPlayedInSectors(
                                        playerID,
                                        wt.fullTrainingSectors,
                                        walkoverWin
                                    )
                                )
                                tp.addBonusTrainingMinutes(
                                    mlt.getTrainingMinutesPlayedInSectors(
                                        playerID,
                                        wt.bonusTrainingSectors,
                                        walkoverWin
                                    )
                                )
                                tp.addPartlyTrainingMinutes(
                                    mlt.getTrainingMinutesPlayedInSectors(
                                        playerID,
                                        wt.partlyTrainingSectors,
                                        walkoverWin
                                    )
                                )
                                tp.addOsmosisTrainingMinutes(
                                    mlt.getTrainingMinutesPlayedInSectors(
                                        playerID,
                                        wt.osmosisTrainingSectors,
                                        walkoverWin
                                    )
                                )
                            }
                            val minutes = mlt.getTrainingMinutesPlayedInSectors(playerID, null, walkoverWin)
                            tp.addPlayedMinutes(minutes)
                            ret.addExperience(match.getExperienceIncrease(Integer.min(90, minutes)))
                        } else {
                            HOLogger.instance().error(
                                javaClass, "no lineup found in match " + match.matchSchedule.toLocaleDateTime() +
                                        " " + match.homeTeamName + " - " + match.guestTeamName
                            )
                        }
                    }
                }
                val trp = TrainingPoints(wt, tp)

                // get experience increase of national team matches
                val id = nationalTeamId
                if (id != null && id != 0 && id != myID) {
                    // TODO check if national matches are stored in database
                    val nationalMatches = train.getNTmatches()
                    for (match in nationalMatches) {
                        val mlt = loadMatchLineupTeam(match.getMatchType().id, match.matchID, nationalTeamId!!)
                        val minutes = mlt!!.getTrainingMinutesPlayedInSectors(playerID, null, false)
                        if (minutes > 0) {
                            ret.addExperience(match.getExperienceIncrease(Integer.min(90, minutes)))
                        }
                    }
                }
                ret.trainingPair = trp
            } catch (e: Exception) {
                HOLogger.instance().log(javaClass, e)
            }
        }
        return ret
    }

    /**
     * Copy the skills of old player.
     * Used by training
     *
     * @param old player to copy from
     */
    fun copySkills(old: Player) {
        for (skillType in 0..PlayerSkill.LOYALTY) {
            setValue4Skill(skillType, old.getValue4Skill(skillType))
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    //equals
    /////////////////////////////////////////////////////////////////////////////////
    override fun equals(other: Any?): Boolean {
        var equals = false
        if (other is Player) {
            equals = other.playerId == playerId
        }
        return equals
    }

    /**
     * Does this player have a training block?
     *
     * @return training block
     */
    fun hasTrainingBlock(): Boolean {
        return trainingBlock
    }

    /**
     * Set the training block of this player (true/false)
     *
     * @param isBlocked new value
     */
    fun setTrainingBlock(isBlocked: Boolean) {
        trainingBlock = isBlocked
    }

    fun setSubExperience(experience: Double?) {
        subExperience = experience ?: 0.0
    }

    private fun getFuturePlayerTrainings(): List<FuturePlayerTraining?>? {
        if (futurePlayerTrainings == null) {
            val tmpTrainingPlayer: MutableList<FuturePlayerTraining?> = mutableListOf()
            getFuturePlayerTrainings(playerId)?.let { tmpTrainingPlayer.addAll(it) }
            if (tmpTrainingPlayer.isNotEmpty()) {
                val start = HOVerwaltung.instance().model.getBasics().getHattrickWeek()
                val remove = ArrayList<FuturePlayerTraining?>()
                for (t in tmpTrainingPlayer) {
                    if (t!!.endsBefore(start)) {
                        remove.add(t)
                    }
                }
                tmpTrainingPlayer.removeAll(remove)
                futurePlayerTrainings = tmpTrainingPlayer
            }
        }
        return futurePlayerTrainings
    }

    /**
     * Get the training priority of a hattrick week. If user training plan is given for the week this user selection is
     * returned. If no user plan is available, the training priority is determined by the player's best position.
     *
     * @param wt
     * used to get priority depending from the player's best position.
     * @param trainingDate
     * the training week
     * @return
     * the training priority
     */
    fun getTrainingPriority(wt: WeeklyTrainingType, trainingDate: HODateTime?): FuturePlayerTraining.Priority? {
        for (t in getFuturePlayerTrainings()!!) {
            if (t!!.contains(trainingDate)) {
                return t.priority
            }
        }

        // get Prio from best position
        val position = HelperWrapper.instance().getPosition(getIdealPosition().toInt())
        for (p in wt.trainingSkillBonusPositions) {
            if (p == position) return FuturePlayerTraining.Priority.FULL_TRAINING
        }
        for (p in wt.trainingSkillPositions) {
            if (p == position) {
                return if (wt.trainingType == TrainingType.SET_PIECES) FuturePlayerTraining.Priority.PARTIAL_TRAINING else FuturePlayerTraining.Priority.FULL_TRAINING
            }
        }
        for (p in wt.trainingSkillPartlyTrainingPositions) {
            if (p == position) return FuturePlayerTraining.Priority.PARTIAL_TRAINING
        }
        for (p in wt.trainingSkillOsmosisTrainingPositions) {
            if (p == position) return FuturePlayerTraining.Priority.OSMOSIS_TRAINING
        }
        return null // No training
    }

    /**
     * Set training priority for a time interval.
     * Previously saved trainings of this interval are overwritten or deleted.
     * @param prio new training priority for the given time interval
     * @param from first week with new training priority
     * @param to last week with new training priority, null means open end
     */
    fun setFutureTraining(prio: FuturePlayerTraining.Priority?, from: HODateTime?, to: HODateTime?) {
        val tmpPlayerTraining = mutableListOf<FuturePlayerTraining?>()
        futurePlayerTrainings?.let { tmpPlayerTraining.addAll(it) }
        val removeIntervals = ArrayList<FuturePlayerTraining?>()
        for (t in getFuturePlayerTrainings()!!) {
            if (t!!.cut(from, to) ||
                t.cut(HODateTime.HT_START, HOVerwaltung.instance().model.getBasics().getHattrickWeek())
            ) {
                removeIntervals.add(t)
            }
        }
        tmpPlayerTraining.removeAll(removeIntervals)
        if (prio != null) {
            tmpPlayerTraining.add(FuturePlayerTraining(playerId, prio, from, to))
        }

        futurePlayerTrainings = tmpPlayerTraining
        storeFuturePlayerTrainings(futurePlayerTrainings!!)
    }

    val bestPositionInfo: String
        get() = (MatchRoleID.getNameForPosition(getIdealPosition())
                + " ("
                + getIdealPositionRating()
                + ")")

    /**
     * training priority information of the training panel
     *
     * @param nextWeek training priorities after this week will be considered
     * @return if there is one user selected priority, the name of the priority is returned
     * if there are more than one selected priorities, "individual priorities" is returned
     * if is no user selected priority, the best position information is returned
     */
    fun getTrainingPriorityInformation(nextWeek: HODateTime?): String {
        var ret: String? = null
        val playerTrainings = getFuturePlayerTrainings()
        if (playerTrainings != null) {
            for (t in playerTrainings) {
                //
                if (!t!!.endsBefore(nextWeek)) {
                    if (ret != null) {
                        ret = HOVerwaltung.instance().getLanguageString("trainpre.individual.prios")
                        break
                    }
                    ret = t.priority.toString()
                }
            }
        }
        return ret ?: bestPositionInfo
    }

    /**
     * Calculates skill status of the player
     *
     * @param previousID Id of the previous download. Previous player status is loaded by this id.
     * @param trainingWeeks List of training week information
     */
    fun calcSubskills(previousID: Int, trainingWeeks: List<TrainingPerWeek>) {
        var playerBefore = getSpieler(previousID).stream()
            .filter(Predicate { i: Player? -> i?.playerId == playerId }).findFirst().orElse(null)
        if (playerBefore == null) {
            playerBefore = CloneWithoutSubskills()
        }
        // since we don't want to work with temp player objects we calculate skill by skill
        // whereas experience is calculated within the first skill
        var experienceSubDone = experience > playerBefore.experience // Do not calculate sub on experience skill up

        fun subExperience(): Double = if (playerBefore.subExperience != null) playerBefore.subExperience else 0.0
        var experienceSub: Double = if (experienceSubDone) 0.0 else subExperience() // set sub to 0 on skill up
        for (skill in trainingSkills) {
            var sub = playerBefore.getSub4Skill(skill)
            var valueBeforeTraining = playerBefore.getValue4Skill(skill)
            val valueAfterTraining = getValue4Skill(skill)
            if (trainingWeeks.isNotEmpty()) {
                if (valueAfterTraining > valueBeforeTraining) {
                    // Check if skill up is available
                    val skillUps = this.getAllLevelUp(skill)
                    val isAvailable = skillUps.stream().anyMatch { i: Skillup? -> i!!.value == valueAfterTraining }
                    if (!isAvailable) {
                        val skillUp = Skillup()
                        skillUp.playerId = playerId
                        skillUp.skill = skill
                        skillUp.date = trainingWeeks[0].trainingDate
                        skillUp.value = valueAfterTraining
                        skillUp.hrfId = hrfId
                        storeSkillup(skillUp)
                        resetSkillUpInformation()
                    }
                }
                for (training in trainingWeeks) {
                    val trainingPerPlayer = calculateWeeklyTraining(training)
                    if (trainingPerPlayer != null) {
                        if (!hasTrainingBlock()) { // player training is not blocked (blocking is no longer possible)
                            sub += trainingPerPlayer.calcSubskillIncrement(
                                skill,
                                valueBeforeTraining + sub,
                                training.trainingDate
                            ).toDouble()
                            if (valueAfterTraining > valueBeforeTraining) {
                                if (sub > 1) {
                                    sub -= 1.0
                                } else {
                                    sub = 0.0
                                }
                            } else if (valueAfterTraining < valueBeforeTraining) {
                                if (sub < 0) {
                                    sub += 1.0
                                } else {
                                    sub = .99
                                }
                            } else {
                                if (sub > 0.99f) {
                                    sub = 0.99
                                } else if (sub < 0f) {
                                    sub = 0.0
                                }
                            }
                            valueBeforeTraining = valueAfterTraining
                        }
                        if (!experienceSubDone) {
                            val inc = trainingPerPlayer.experienceSub
                            experienceSub += inc
                            if (experienceSub > 0.99) experienceSub = 0.99
                            var minutes = 0
                            val tp = trainingPerPlayer.trainingPair
                            if (tp != null) {
                                minutes = tp.trainingDuration.playedMinutes
                            } else {
                                HOLogger.instance().warning(javaClass, "no training info found")
                            }
                            HOLogger.instance().info(
                                javaClass,
                                "Training " + training.trainingDate.toLocaleDateTime() +
                                        "; Minutes= " + minutes +
                                        "; Experience increment of " + getFullName() +
                                        "; increment: " + inc +
                                        "; new sub value=" + experienceSub
                            )
                        }
                    }
                }
                experienceSubDone = true
            }
            if (valueAfterTraining < valueBeforeTraining) {
                sub = .99
            } else if (valueAfterTraining > valueBeforeTraining) {
                sub = 0.0
                HOLogger.instance().error(javaClass, "skill up without training") // missing training in database
            }
            setSubskill4PlayerSkill(skill, sub)
            setSubExperience(experienceSub)
        }
    }

    /**
     * Schum rank is a player training assessment, created by the hattrick team manager Schum, Russia
     * The following coefficients defines a polynomial fit of the table Schum provided in [...](https://www88.hattrick.org/Forum/Read.aspx?t=17404127&n=73&v=0&mr=0)
     * SchumRank(skill) = C0 + C1*skill + C2*skill^2 + C3*skill^3 + C4*skill^4 + C5*skill^5 + C6*skill^6
     */
    var schumRankFitCoefficients: Map<Int, Array<Double>> = java.util.Map.of(
        PlayerSkill.KEEPER,
        arrayOf<Double>(-2.90430547, 2.20134952, -0.17288917, 0.01490328, 0.0, 0.0, 0.0),
        PlayerSkill.DEFENDING,
        arrayOf<Double>(8.78549747, -13.89441249, 7.20818523, -1.42920262, 0.14104285, -0.00660499, 0.00011864),
        PlayerSkill.WINGER,
        arrayOf<Double>(0.68441693, -0.63873590, 0.42587817, -0.02909820, 0.00108502, 0.0, 0.0),
        PlayerSkill.PLAYMAKING,
        arrayOf<Double>(-5.70730805, 6.57044707, -1.78506428, 0.27138439, -0.01625170, 0.00036649, 0.0),
        PlayerSkill.SCORING,
        arrayOf<Double>(-6.61486533, 7.65566042, -2.14396084, 0.32264321, -0.01935220, 0.00043442, 0.0),
        PlayerSkill.PASSING,
        arrayOf<Double>(2.61223942, -2.42601757, 0.95573380, -0.07250134, 0.00239775, 0.0, 0.0),
        PlayerSkill.SET_PIECES,
        arrayOf<Double>(-1.54485655, 1.45506372, -0.09224842, 0.00525752, 0.0, 0.0, 0.0)
    )

    /**
     * Calculated Schum rank.
     * Should be reset, if the player skills are changed
     */
    private var schumRank: Double? = null

    /**
     * Calculated Schum rank benchmark
     * Should be reset, if the player's ideal position (keeper or not) is set.
     */
    private var schumRankBenchmark: Double? = null

    /**
     * Calculate the Schum rank
     * Sum of the polynomial functions defined above.
     * @return Double
     */
    private fun calcSchumRank(): Double {
        var ret = 0.0
        for ((key, value1) in schumRankFitCoefficients) {
            val value = getSkillValue(key)
            var x = 1.0
            for (c in value1) {
                ret += c * x
                x *= value
            }
        }
        return ret
    }

    /**
     * Schum suggests highlighting values in the range 220 up to 240 (or 195 up to 215 in case of keepers)
     * @return true, if schum rank is in this optimal range
     */
    fun isExcellentSchumRank(): Boolean {
        val r = getSchumRank()
        val lower = if (getIdealPosition().toInt() == PlayerSkill.KEEPER) 195 else 220
        return r >= lower && r <= lower + 20
    }

    /**
     * Calculate a Schum rank value, that could be reached with optimal training.
     * It only depends on the player age.
     * @return double
     */
    private fun calcSchumRankBenchmark(): Double {
        var ret = if (getIdealPosition().toInt() == PlayerSkill.KEEPER) 25.0 else 50.0
        var k = 1.0
        for (age in 17 until age) {
            ret += 16.0 * k
            k = 54.0 / (age + 37)
        }
        return ret + ageDays / 112.0 * 16.0 * k
    }

    fun getSchumRank(): Double {
        if (schumRank == null) schumRank = calcSchumRank()
        return schumRank!!
    }

    fun getSchumRankBenchmark(): Double {
        if (schumRankBenchmark == null) schumRankBenchmark = calcSchumRankBenchmark()
        return schumRankBenchmark!!
    }

    private fun CloneWithoutSubskills(): Player {
        val ret = Player()
        ret.hrfId = hrfId
        ret.copySkills(this)
        ret.playerId = playerId
        ret.age = age
        ret.setLastName(getLastName())
        return ret
    }

    /**
     * Create a clone of the player with modified skill values if man marking is switched on.
     * Values of Defending, Winger, Playmaking, Scoring and Passing are reduced depending of the distance
     * between man marker and opponent man marked player
     *
     * @param manMarkingPosition
     * null - no man marking changes
     * Opposite - reduce skills by 50%
     * NotOpposite - reduce skills by 65%
     * NotInLineup - reduce skills by 10%
     * @return
     * this player, if no man marking changes are selected
     * New modified player, if man marking changes are selected
     */
    fun createManMarker(manMarkingPosition: ManMarkingPosition?): Player {
        if (manMarkingPosition == null) return this
        val ret = Player()
        val skillFactor = (1 - manMarkingPosition.value / 100.0).toFloat()
        ret.specialty = specialty
        ret.ageDays = ageDays
        ret.age = age
        ret.agressivity = agressivity
        ret.honesty = honesty
        ret.gentleness = gentleness
        ret.experience = experience
        ret.setSubExperience(subExperience)
        ret.setFirstName(getFirstName())
        ret.setLastName(getLastName())
        ret.form = form
        ret.leadership = leadership
        ret.stamina = stamina
        ret.loyalty = loyalty
        ret.homeGrown = homeGrown
        ret.playerId = playerId
        ret.injuryWeeks = injuryWeeks
        ret.setSkillValue(PlayerSkill.KEEPER, getSkillValue(PlayerSkill.KEEPER))
        ret.setSkillValue(PlayerSkill.DEFENDING, skillFactor * getSkillValue(PlayerSkill.DEFENDING))
        ret.setSkillValue(PlayerSkill.WINGER, skillFactor * getSkillValue(PlayerSkill.WINGER))
        ret.setSkillValue(PlayerSkill.PLAYMAKING, skillFactor * getSkillValue(PlayerSkill.PLAYMAKING))
        ret.setSkillValue(PlayerSkill.SCORING, skillFactor * getSkillValue(PlayerSkill.SCORING))
        ret.setSkillValue(PlayerSkill.PASSING, skillFactor * getSkillValue(PlayerSkill.PASSING))
        ret.setSkillValue(PlayerSkill.STAMINA, getSkillValue(PlayerSkill.STAMINA))
        ret.setSkillValue(PlayerSkill.FORM, getSkillValue(PlayerSkill.FORM))
        ret.setSkillValue(PlayerSkill.SET_PIECES, getSkillValue(PlayerSkill.SET_PIECES))
        ret.setSkillValue(PlayerSkill.LEADERSHIP, getSkillValue(PlayerSkill.LEADERSHIP))
        ret.setSkillValue(PlayerSkill.LOYALTY, getSkillValue(PlayerSkill.LOYALTY))
        return ret
    }

    enum class ManMarkingPosition(@JvmField val value: Int) {
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

        companion object {
            @JvmStatic
            fun fromId(id: Int): ManMarkingPosition {
                return when (id) {
                    50 -> Opposite
                    65 -> NotOpposite
                    else -> NotInLineup
                }
            }
        }
    }

    companion object {
        private val trainingSkills = intArrayOf(
            PlayerSkill.KEEPER,
            PlayerSkill.SET_PIECES,
            PlayerSkill.DEFENDING,
            PlayerSkill.SCORING,
            PlayerSkill.WINGER,
            PlayerSkill.PASSING,
            PlayerSkill.PLAYMAKING
        )
        private const val BREAK = "[br]"
        private const val O_BRACKET = "["
        private const val C_BRACKET = "]"
        private const val EMPTY = ""

        /**
         * Calculates the player's age at date referencing the current hrf download
         * @param ageYears int player's age in years in current hrf download
         * @param ageDays int additional days
         * @param time HODateTime for which the player's age should be calculated
         * @return String
         */
        @JvmStatic
        fun getAgeWithDaysAsString(ageYears: Int, ageDays: Int, time: HODateTime?): String {
            return getAgeWithDaysAsString(ageYears, ageDays, time, HOVerwaltung.instance().model.getBasics().datum)
        }

        /**
         * Calculates the player's age at date referencing the given hrf date
         * @param ageYears int player's age in years at reference time
         * @param ageDays int additional days
         * @param time HODateTime for which the player's age should be calculated
         * @param hrfTime HODateTime reference date, when player's age was given
         * @return String
         */
        fun getAgeWithDaysAsString(ageYears: Int, ageDays: Int, time: HODateTime?, hrfTime: HODateTime?): String {
            val age = HODuration(ageYears, ageDays).plus(HODuration.between(hrfTime, time))
            return age.seasons.toString() + " (" + age.days + ")"
        }

        var referencePlayer: Player? = null
            get() {
                if (field == null) {
                    field = Player()
                    field!!.age = 28
                    field!!.ageDays = 0
                    field!!.playerId = Int.MAX_VALUE
                    field!!.form = 8
                    field!!.stamina = 9
                    field!!.setSkillValue(PlayerSkill.KEEPER, 1.0)
                    field!!.setSkillValue(PlayerSkill.DEFENDING, 20.0)
                    field!!.setSkillValue(PlayerSkill.PLAYMAKING, 20.0)
                    field!!.setSkillValue(PlayerSkill.PASSING, 20.0)
                    field!!.setSkillValue(PlayerSkill.WINGER, 20.0)
                    field!!.setSkillValue(PlayerSkill.SCORING, 20.0)
                    field!!.setSkillValue(PlayerSkill.SET_PIECES, 20.0)
                    field!!.setSkillValue(PlayerSkill.EXPERIENCE, 20.0)
                }
                return field
            }

        var referenceKeeper: Player? = null
            get() {
                if (field == null) {
                    field = Player()
                    field!!.age = 28
                    field!!.ageDays = 0
                    field!!.playerId = Int.MAX_VALUE
                    field!!.form = 8
                    field!!.stamina = 9
                    field!!.setSkillValue(PlayerSkill.KEEPER, 20.0)
                    field!!.setSkillValue(PlayerSkill.DEFENDING, 20.0)
                    field!!.setSkillValue(PlayerSkill.SET_PIECES, 20.0)
                    field!!.setSkillValue(PlayerSkill.PLAYMAKING, 1.0)
                    field!!.setSkillValue(PlayerSkill.PASSING, 1.0)
                    field!!.setSkillValue(PlayerSkill.WINGER, 1.0)
                    field!!.setSkillValue(PlayerSkill.SCORING, 1.0)
                    field!!.setSkillValue(PlayerSkill.EXPERIENCE, 1.0)
                }
                return field
            }
    }
}
