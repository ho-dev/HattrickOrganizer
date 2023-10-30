package core.db

import core.model.enums.DBDataSource
import core.training.TrainingPerWeek
import core.util.HODateTime
import core.util.HOLogger
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class FutureTrainingTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TRAINING_DATE")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TrainingPerWeek?)!!.trainingDate.toDbTimestamp() })
                .setSetter(
                    BiConsumer<Any?, Any> { p: Any?, v: Any? ->
                        (p as TrainingPerWeek?)!!.trainingDate = v as HODateTime?
                    }).setType(
                Types.TIMESTAMP
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TRAINING_TYPE")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TrainingPerWeek?)!!.trainingType }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as TrainingPerWeek?)!!.trainingType = v as Int }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TRAINING_INTENSITY")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TrainingPerWeek?)!!.trainingIntensity }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as TrainingPerWeek?)!!.trainingIntensity = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("STAMINA_SHARE")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TrainingPerWeek?)!!.staminaShare }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as TrainingPerWeek?)!!.setStaminaShare(v as Int) })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("COACH_LEVEL")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TrainingPerWeek?)!!.coachLevel }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as TrainingPerWeek?)!!.coachLevel = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TRAINING_ASSISTANTS_LEVEL").setGetter(
                Function<Any?, Any?> { p: Any? -> (p as TrainingPerWeek?)!!.trainingAssistantsLevel }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as TrainingPerWeek?)!!.setTrainingAssistantLevel(v as Int) })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SOURCE")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TrainingPerWeek?)!!.source.value }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any ->
                    (p as TrainingPerWeek?)!!.source = DBDataSource.getCode(v as Int)
                }).setType(
                Types.INTEGER
            ).isNullable(false).build()
        )
    }

    private val loadAllFutureTrainingStatementBuilder = PreparedSelectStatementBuilder(
        this,
        " ORDER BY TRAINING_DATE"
    )
    fun getFutureTrainingsVector(): List<TrainingPerWeek?>? {
        return load(
            TrainingPerWeek::class.java,
            adapter.executePreparedQuery(loadAllFutureTrainingStatementBuilder.getStatement())
        )
    }

    fun loadFutureTrainings(trainingDate: Timestamp?): TrainingPerWeek? {
        return loadOne(TrainingPerWeek::class.java, trainingDate)
    }

    fun storeFutureTraining(training: TrainingPerWeek?) {
        store(training)
    }

    fun storeFutureTrainings(trainings: List<TrainingPerWeek>) {
        clearFutureTrainingsTable()
        for (futureTraining in trainings) {
            futureTraining.stored = false
            storeFutureTraining(futureTraining)
        }
    }

    override fun createPreparedDeleteStatementBuilder(): PreparedDeleteStatementBuilder? {
        return PreparedDeleteStatementBuilder(this, " WHERE TRUE")
    }

    fun clearFutureTrainingsTable() {
        executePreparedDelete()
        HOLogger.instance().debug(javaClass, "FutureTraining table has been cleared !")
    }

    companion object {
        /**
         * tablename
         */
        const val TABLENAME = "FUTURETRAINING"
    }
}
