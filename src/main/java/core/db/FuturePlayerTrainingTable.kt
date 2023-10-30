package core.db

import core.training.FuturePlayerTraining
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class FuturePlayerTrainingTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("playerId")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FuturePlayerTraining?)!!.playerId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as FuturePlayerTraining?)!!.playerId = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("fromWeek")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FuturePlayerTraining?)!!.fromWeek }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as FuturePlayerTraining?)!!.fromWeek = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("fromSeason")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FuturePlayerTraining?)!!.fromSeason }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as FuturePlayerTraining?)!!.fromSeason = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("toWeek")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FuturePlayerTraining?)!!.toWeek }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as FuturePlayerTraining?)!!.toWeek = v as Int? })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("toSeason")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FuturePlayerTraining?)!!.toSeason }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as FuturePlayerTraining?)!!.toSeason = v as Int? })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("prio")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FuturePlayerTraining?)!!.priority.value }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as FuturePlayerTraining?)!!.priority = FuturePlayerTraining.Priority.valueOf(v as Int)
                }).setType(
                Types.INTEGER
            ).isNullable(false).build()
        )
    }

    fun getFuturePlayerTrainingPlan(playerId: Int): List<FuturePlayerTraining?>? {
        return load(FuturePlayerTraining::class.java, playerId)
    }

    fun storeFuturePlayerTrainings(futurePlayerTrainings: List<FuturePlayerTraining?>) {
        for (t in futurePlayerTrainings) {
            store(t)
        }
    }

    companion object {
        const val TABLENAME = "FUTUREPLAYERTRAINING"
    }
}