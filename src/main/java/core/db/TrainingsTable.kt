package core.db

import core.model.enums.DBDataSource
import core.training.TrainingPerWeek
import core.util.HODateTime
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

/**
 * This table is different from others because it does not hold data from XML/HRFs but is a mixed of computed data and data entered
 * directly by Users. Hence, there is a method recalculateEntries() that will force refresh of entries.
 * This method will be called automatically after table creation and during upgrade to v5.0
 */
internal class TrainingsTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
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
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as TrainingPerWeek?)!!.setStaminaShare(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
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

    /**
     * save provided training in database (trainings still in the future will be skipped)
     * @param training training to be saved
     */
    fun saveTraining(training: TrainingPerWeek?, lastTrainingDate: HODateTime?) {
        if (training != null) {
            val trainingDate = training.trainingDate
            if (trainingDate.isAfter(lastTrainingDate)) {
                return
            }
            store(training)
        }
    }

    /**
     * apply the function saveTraining() to all elements of the provided vector
     */
    fun saveTrainings(trainings: List<TrainingPerWeek?>, lastTrainingDate: HODateTime?) {
        for (training in trainings) {
            saveTraining(training, lastTrainingDate)
        }
    }

    private val getTrainingListStatements = HashMap<Int, PreparedStatement?>()
    private fun getTrainingListStatement(
        from: Timestamp?,
        to: Timestamp?,
        values: MutableList<Any?>
    ): PreparedStatement? {
        var ret: PreparedStatement?
        if (from == null && to == null) {
            ret = getTrainingListStatements[0]
            if (ret == null) {
                ret = PreparedSelectStatementBuilder(this, "ORDER  BY TRAINING_DATE DESC").getStatement()
                getTrainingListStatements[0] = ret
            }
        } else if (from == null) {
            ret = getTrainingListStatements[1]
            if (ret == null) {
                ret = PreparedSelectStatementBuilder(this, "WHERE TRAINING_DATE<?").getStatement()
                getTrainingListStatements[1] = ret
            }
            values.add(to)
        } else if (to == null) {
            ret = getTrainingListStatements[2]
            if (ret == null) {
                ret = PreparedSelectStatementBuilder(this, "WHERE TRAINING_DATE>=?").getStatement()
                getTrainingListStatements[2] = ret
            }
            values.add(from)
        } else {
            ret = getTrainingListStatements[3]
            if (ret == null) {
                ret = PreparedSelectStatementBuilder(this, "WHERE TRAINING_DATE>=? AND TRAINING_DATE<?").getStatement()
            }
            values.add(from)
            values.add(to)
        }
        return ret
    }

    fun getTrainingList():List<TrainingPerWeek?> = getTrainingList(null, null)

    fun getTrainingList(fromDate: Timestamp?, toDate: Timestamp?): List<TrainingPerWeek?> {
        val values = ArrayList<Any?>()
        val statement = getTrainingListStatement(fromDate, toDate, values)
        return load(TrainingPerWeek::class.java, adapter.executePreparedQuery(statement, *values.toTypedArray()))
    }

    companion object {
        const val TABLENAME = "TRAINING"
    }
}
