package core.db

import core.model.enums.MatchType
import module.youth.YouthTraining
import module.youth.YouthTrainingType
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class YouthTrainingTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MATCHID")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthTraining?)!!.getYouthMatchId() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as YouthTraining?)!!.setYouthMatchId(v as Int) })
            ).setType(
                Types.INTEGER
            ).isNullable(false).isPrimaryKey(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchTyp")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthTraining?)!!.getMatchType().getId() }))
                .setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any ->
                        (p as YouthTraining?)!!.setYouthMatchType(
                            MatchType.getById(
                                v as Int
                            )
                        )
                    })
                ).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TRAINING1")
                .setGetter(Function<Any?, Any?>({ p: Any? ->
                    YouthTrainingType.getValue(
                        (p as YouthTraining?)!!.getTraining(YouthTraining.Priority.Primary)
                    )
                })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                    (p as YouthTraining?)!!.setTraining(
                        YouthTraining.Priority.Primary,
                        YouthTrainingType.valueOf(v as Int?)
                    )
                })
            ).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TRAINING2")
                .setGetter(Function<Any?, Any?>({ p: Any? ->
                    YouthTrainingType.getValue(
                        (p as YouthTraining?)!!.getTraining(YouthTraining.Priority.Secondary)
                    )
                })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                    (p as YouthTraining?)!!.setTraining(
                        YouthTraining.Priority.Secondary,
                        YouthTrainingType.valueOf(v as Int?)
                    )
                })
            ).setType(
                Types.INTEGER
            ).isNullable(true).build()
        )
    }

    override fun createPreparedSelectStatementBuilder(): PreparedSelectStatementBuilder {
        return PreparedSelectStatementBuilder(this, "")
    }

    fun loadYouthTrainings(): List<YouthTraining?> {
        return load(YouthTraining::class.java)
    }

    fun storeYouthTraining(youthTraining: YouthTraining?) {
        store(youthTraining)
    }

    companion object {
        /** tablename  */
        val TABLENAME: String = "YOUTHTRAINING"
    }
}
