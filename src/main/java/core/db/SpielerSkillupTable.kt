package core.db

import core.constants.player.PlayerSkill
import core.model.HOModel
import core.model.HOVerwaltung
import core.model.player.*
import core.util.HODateTime
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

internal class SpielerSkillupTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SpielerID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Skillup?)!!.playerId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Skillup?)!!.playerId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Skill")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Skillup?)!!.skill }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Skillup?)!!.skill = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HRF_ID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Skillup?)!!.hrfId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Skillup?)!!.hrfId = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Datum")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Skillup?)!!.date?.toDbTimestamp() }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Skillup?)!!.date = v as HODateTime? })
                .setType(Types.TIMESTAMP).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Value")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Skillup?)!!.value }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Skillup?)!!.value = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build()
        )
    }

    override val createIndexStatement: Array<String?>
        get() = arrayOf(
            "CREATE INDEX iSkillup_1 ON " + tableName + "(" + columns[2].columnName + ")",
            "CREATE INDEX iSkillup_2 ON " + tableName + "(" + columns[2].columnName + "," + columns[3].columnName + ")"
        )

    override fun createPreparedDeleteStatementBuilder(): PreparedDeleteStatementBuilder? {
        return PreparedDeleteStatementBuilder(this, "WHERE HRF_ID=?")
    }

    private fun storeSkillup(skillup: Skillup) {
        store(skillup)
    }

    private val loadLastLevelUpStatementBuilder = PreparedSelectStatementBuilder(
        this,
        "WHERE SPIELERID=? AND SKILL = ? ORDER BY Datum DESC LIMIT 1"
    )

    //	private static Map<String, Vector<Object[]>> playerSkillup = null;
    init {
        idColumns = 2
    }

    fun getLastLevelUp(skillCode: Int, spielerId: Int): Skillup? {
        return loadOne(
            Skillup::class.java,
            adapter.executePreparedQuery(loadLastLevelUpStatementBuilder.getStatement(), spielerId, skillCode)
        )
    }

    fun getAllLevelUp(skillCode: Int, spielerId: Int): List<Skillup?> {
        return load(Skillup::class.java, spielerId, skillCode)
    }

    fun importNewSkillup(homodel: HOModel) {
        val players = homodel.getCurrentPlayers()
        for (nPlayer in players) {
            val oPlayer = HOVerwaltung.instance().model.getCurrentPlayer(nPlayer.playerId)
            if (oPlayer != null) {
                checkNewSkillup(nPlayer, nPlayer.goalkeeperSkill, oPlayer.goalkeeperSkill, PlayerSkill.KEEPER, homodel.id)
                checkNewSkillup(nPlayer, nPlayer.playmakingSkill, oPlayer.playmakingSkill, PlayerSkill.PLAYMAKING, homodel.id)
                checkNewSkillup(nPlayer, nPlayer.passingSkill, oPlayer.passingSkill, PlayerSkill.PASSING, homodel.id)
                checkNewSkillup(nPlayer, nPlayer.wingerSkill, oPlayer.wingerSkill, PlayerSkill.WINGER, homodel.id)
                checkNewSkillup(nPlayer, nPlayer.defendingSkill, oPlayer.defendingSkill, PlayerSkill.DEFENDING, homodel.id)
                checkNewSkillup(nPlayer, nPlayer.scoringSkill, oPlayer.scoringSkill, PlayerSkill.SCORING, homodel.id)
                checkNewSkillup(nPlayer, nPlayer.setPiecesSkill, oPlayer.setPiecesSkill, PlayerSkill.SET_PIECES, homodel.id)
                checkNewSkillup(nPlayer, nPlayer.stamina, oPlayer.stamina, PlayerSkill.STAMINA, homodel.id)
                checkNewSkillup(nPlayer, nPlayer.experience, oPlayer.experience, PlayerSkill.EXPERIENCE, homodel.id)
            }
        }
    }

    private fun checkNewSkillup(nPlayer: Player, newValue: Int, oldValue: Int, skill: Int, hrf: Int) {
        if (newValue > oldValue) {
            val skillup = Skillup()
            skillup.hrfId = hrf
            skillup.date = nPlayer.hrfDate
            skillup.skill = skill
            skillup.playerId = nPlayer.playerId
            skillup.value = newValue
            storeSkillup(skillup)
            nPlayer.resetSkillUpInformation()
        }
    }

    companion object {
        /**
         * tablename
         */
        const val TABLENAME = "SPIELERSKILLUP"
    }
}