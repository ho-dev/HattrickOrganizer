package core.db

import core.model.*
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

internal class TeamTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HRF_ID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.hrfId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.hrfId = v as Int }).setType(Types.INTEGER)
                .isNullable(false).isPrimaryKey(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TrainingsIntensitaet")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.trainingslevel }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.trainingslevel = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TrainingsArt")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.trainingsArtAsInt }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.trainingsArtAsInt = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iStimmung")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.teamSpiritLevel }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.setTeamSpiritLevel(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iSelbstvertrauen")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.confidence }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.setConfidence(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iErfahrung541")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.formationExperience541 }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.formationExperience541 = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iErfahrung433")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.formationExperience433 }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.formationExperience433 = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iErfahrung352")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.formationExperience352 }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.formationExperience352 = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iErfahrung451")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.formationExperience451 }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.formationExperience451 = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iErfahrung532")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.formationExperience532 }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.formationExperience532 = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iErfahrung343")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.formationExperience343 }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.formationExperience343 = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iErfahrung442")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.formationExperience442 }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.formationExperience442 = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iErfahrung523")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.formationExperience523 }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.formationExperience523 = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iErfahrung550")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.formationExperience550 }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.formationExperience550 = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iErfahrung253")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.formationExperience253 }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.formationExperience253 = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("StaminaTrainingPart")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.staminaTrainingPart }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.staminaTrainingPart = v as Int })
                .setType(Types.INTEGER).isNullable(false).build()
        )
    }

    /**
     * Save the team data for the given HRF id.
     */
    fun saveTeam(hrfId: Int, team: Team?) {
        if (team != null) {
            team.hrfId = hrfId
            team.stored = isStored(hrfId)
            store(team)
        }
    }

    /**
     * load the team data for the given HRF id
     */
    fun getTeam(hrfID: Int): Team {
        var ret = loadOne(Team::class.java, hrfID)
        if (ret == null) ret = Team()
        return ret
    }

    companion object {
        const val TABLENAME = "TEAM"
    }
}