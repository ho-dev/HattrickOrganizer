package core

import core.constants.player.PlayerSkill
import core.db.PersistenceManager
import core.file.hrf.HRF
import core.model.HOModel
import core.model.StaffMember
import core.model.StaffType
import core.model.Team
import core.model.XtraData
import core.model.match.MatchLineupTeam
import core.model.misc.Basics
import core.model.misc.Economy
import core.model.misc.Verein
import core.model.player.Player
import core.model.series.Liga
import core.util.HODateTime
import module.series.MatchFixtures
import tool.arenasizer.Stadium
import java.sql.Timestamp
import java.time.temporal.ChronoUnit

private const val referenceDate = "2026-06-30 14:00:00"

private var hrfs = mutableMapOf(
    42 to HRF(42,HODateTime.fromHT(referenceDate) ),
    43 to HRF(43, HODateTime.fromHT(referenceDate).plus(4, ChronoUnit.DAYS) ),
)

private const val DAYS_PER_SEASON = 112

class PlayerBuilder {
    private var birthday = HODateTime.fromHT(referenceDate).minus(17 * DAYS_PER_SEASON, ChronoUnit.DAYS)
    private var injuryLevel: Int = -1
    private var tsi:Int = 1000
    private var hrfId:Int = 42
    private var playerId:Int = 1
    private var skills = mutableMapOf<PlayerSkill, Double>()

    fun hrfId(id: Int): PlayerBuilder {
        hrfId = id
        return this
    }

    fun playerId(id: Int): PlayerBuilder {
        playerId = id
        return this
    }

    fun skill(type: PlayerSkill, value: Double): PlayerBuilder {
        skills[type] = value
        return this
    }

    fun tsi(tsi: Int): PlayerBuilder {
        this.tsi = tsi
        return this
    }

    fun age(years:Int, days:Int): PlayerBuilder {
        this.birthday = HODateTime.fromHT(referenceDate).minus(years * DAYS_PER_SEASON + days, ChronoUnit.DAYS)
        return this
    }

    fun injuryLevel(injuryLevel: Int): PlayerBuilder {
        this.injuryLevel = injuryLevel
        return this
    }

    fun build(): Player {
        val ret = Player()
        ret.hrfId = hrfId
        val hrfDate = hrfs[hrfId]?.datum
        val age = HODateTime.HODuration.between(this.birthday, hrfDate)
        ret.age = age.seasons
        ret.ageDays = age.days
        ret.injuryWeeks = injuryLevel
        ret.tsi = tsi
        ret.playerId = playerId
        skills.forEach {(skillType, skillValue)->ret.setSkillValue(skillType, skillValue)}
        return ret
    }
}

class TestPersistenceManager : PersistenceManager {

    private var hoConfig = mutableMapOf(
        "CurrencyCountryId" to "3"
    )

    private var clubs = mutableMapOf(
        42 to TestClub(42),
        43 to TestClub(43),
    )
    private var teams = mutableMapOf(
        42 to Team()
    )
    private var basics = mutableMapOf(42 to Basic(42), 43 to Basic(43))
    private var players = mutableMapOf(
        42 to TestPlayers42(),
        43 to TestPlayers43(),
    )
    private var leagues = mutableMapOf(42 to Liga())
    private var stadiums = mutableMapOf(42 to Stadium())
    private var economies = mutableMapOf(42 to Economy())
    private var xtras = mutableMapOf(42 to XtraData(42), 43 to XtraData(43))
    private var staffMembers = mutableMapOf(42 to StaffMember(42))

    private fun Basic(hrfId: Int): Basics {
        val ret = Basics()
        ret.hrfId = hrfId
        ret.teamId = 4711
        ret.datum = hrfs[hrfId]?.datum
        return ret
    }

    private fun XtraData(hrfId:Int): XtraData {
        val ret = XtraData()
        ret.hrfId = hrfId
        val date = hrfs[hrfId]?.datum
        for (i in 0..4) {
            ret.setDailyUpdate(i, date?.plus(1 + i, ChronoUnit.DAYS))
        }
        return ret
    }

    private fun TestClub(hrfId: Int): Verein {
        val ret = Verein()
        ret.aerzte = 5
        ret.hrfId = hrfId
        ret.date = hrfs[hrfId]?.datum
        return ret
    }

    private fun TestPlayers42(): List<Player> {
        val ret = mutableListOf(
            PlayerBuilder().hrfId(42)
                .skill(PlayerSkill.PLAYMAKING, 7.0)
                .skill(PlayerSkill.DEFENDING, 5.45)
                .skill(PlayerSkill.FORM, 5.0)
                .skill(PlayerSkill.KEEPER, 1.0)
                .skill(PlayerSkill.PASSING, 5.0)
                .skill(PlayerSkill.SCORING, 5.0)
                .skill(PlayerSkill.WINGER, 5.0)
                .skill(PlayerSkill.STAMINA, 5.0)
                .build(),
            PlayerBuilder().hrfId(42).age(27, 0).playerId(2)
                .skill(PlayerSkill.PLAYMAKING, 7.0)
                .skill(PlayerSkill.DEFENDING, 5.45)
                .skill(PlayerSkill.FORM, 5.0)
                .skill(PlayerSkill.KEEPER, 1.0)
                .skill(PlayerSkill.PASSING, 5.0)
                .skill(PlayerSkill.SCORING, 5.0)
                .skill(PlayerSkill.WINGER, 5.0)
                .skill(PlayerSkill.STAMINA, 5.0)
                .build(),
            PlayerBuilder().hrfId(42).age(37, 0).playerId(3)
                .skill(PlayerSkill.PLAYMAKING, 11.0)
                .skill(PlayerSkill.DEFENDING, 6.75)
                .skill(PlayerSkill.FORM, 5.0)
                .skill(PlayerSkill.KEEPER, 1.0)
                .skill(PlayerSkill.PASSING, 5.0)
                .skill(PlayerSkill.SCORING, 5.0)
                .skill(PlayerSkill.WINGER, 5.0)
                .skill(PlayerSkill.STAMINA, 5.0)
                .build(),
            PlayerBuilder().hrfId(42).age(41, 0).tsi(500).playerId(4)
                .skill(PlayerSkill.PLAYMAKING, 10.0)
                .skill(PlayerSkill.DEFENDING, 5.0)
                .skill(PlayerSkill.FORM, 5.0)
                .skill(PlayerSkill.KEEPER, 1.0)
                .skill(PlayerSkill.PASSING, 5.0)
                .skill(PlayerSkill.SCORING, 5.0)
                .skill(PlayerSkill.WINGER, 5.0)
                .skill(PlayerSkill.STAMINA, 5.0)
                .build(),
        )
        return ret
    }

    private fun TestPlayers43(): List<Player> {
        val ret = TestPlayers42()
        val downloadTimeInterval = HODateTime.HODuration.between(hrfs[42]?.datum, hrfs[43]?.datum)
        var injuryLevel = 0
        ret.forEach { p ->
            run {
                val age = HODateTime.HODuration(p.age, p.ageDays).plus(downloadTimeInterval)
                p.injuryWeeks = injuryLevel++
                p.tsi = (p.tsi * (1.0 - 0.19 * p.injuryWeeks)).toInt()
                p.hrfId = 43
                p.age = age.seasons
                p.ageDays = age.days
            }
        }
        return ret
    }

    private fun StaffMember(hrfId: Int): List<StaffMember> {
        val doctor = StaffMember()
        doctor.hrfId = hrfId
        doctor.level = 5
        doctor.staffType = StaffType.MEDIC
        val ret = mutableListOf(
            doctor
        )
        return ret
    }

    override fun getMaxIdHrf(): HRF? {
        TODO("Not yet implemented")
    }

    override fun loadHRF(id: Int): HRF? {
        return hrfs[id]
    }

    override fun loadLatestHRFDownloadedBefore(date: Timestamp?): HRF? {
        return null
    }

    override fun getBasics(hrfId: Int): Basics? {
        return basics[hrfId]
    }

    override fun getVerein(hrfId: Int): Verein? {
        return clubs[hrfId]
    }

    override fun getTeam(hrfId: Int): Team? {
        return teams[hrfId]
    }

    override fun getEconomy(hrfId: Int): Economy? {
        return economies[hrfId]
    }

    override fun getLiga(hrfId: Int): Liga? {
        return leagues[hrfId]
    }

    override fun getStadion(hrfId: Int): Stadium? {
        return stadiums[hrfId]
    }

    override fun getXtraDaten(hrfId: Int): XtraData? {
        return xtras[hrfId]
    }

    override fun loadAllPlayers(): List<Player?>? {
        return getSpieler(42)
    }

    override fun getSpieler(hrfId: Int): List<Player?>? {
        return players[hrfId]
    }

    override fun loadHOConfigurationParameter(key: String?): String? {
        return hoConfig[key]
    }

    override fun loadPreviousMatchLineup(teamId: Int): MatchLineupTeam? {
        return null
    }

    override fun loadNextMatchLineup(teamId: Int): MatchLineupTeam? {
        return null
    }

    override fun getLatestSpielplan(): MatchFixtures? {
        return null
    }

    override fun getStaffByHrfId(hrfId: Int): List<StaffMember?>? {
        return staffMembers[hrfId]
    }

    override fun getLatestPlayerDownloadBefore(
        playerId: Int,
        beforeTimestamp: Timestamp?
    ): Player? {

        var ret: Player? = null
        val before = HODateTime.fromDbTimestamp(beforeTimestamp)

        this.players.forEach { l ->
            run {
                val date = hrfs[l.key]?.datum
                if (date == null || !date.isBefore(before)) {
                    return ret
                }
                l.value.forEach { player -> if (player.playerId == playerId) ret = player }
            }
        }
        return ret
    }
}

class HOModelBuilder {

    private var hrfId: Int = -1
    private var persistenceManager: PersistenceManager = TestPersistenceManager()

    fun hrfId(id: Int): HOModelBuilder {
        hrfId = id
        return this
    }

    fun persistenceManager(persistenceManager: PersistenceManager): HOModelBuilder {
        this.persistenceManager = persistenceManager
        return this
    }

    fun build(): HOModel {
        return HOModel(hrfId, persistenceManager)
    }
}
