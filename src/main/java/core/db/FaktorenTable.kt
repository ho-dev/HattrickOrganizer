package core.db

import core.model.FactorObject
import core.model.FormulaFactors
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class FaktorenTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PositionID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FactorObject?)!!.position }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as FactorObject?)!!.position = (v as Int).toByte() })
                .setType(
                    Types.INTEGER
                ).isNullable(false).isPrimaryKey(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GKfactor")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FactorObject?)!!.gKfactor }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as FactorObject?)!!.setTorwart(v as Float) })
                .setType(Types.REAL).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("DEfactor")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FactorObject?)!!.dEfactor }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as FactorObject?)!!.setDefendingFactor(v as Float) })
                .setType(
                    Types.REAL
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("WIfactor")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FactorObject?)!!.wIfactor }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as FactorObject?)!!.setWingerFactor(v as Float) })
                .setType(
                    Types.REAL
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PSfactor")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FactorObject?)!!.pSfactor }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as FactorObject?)!!.setPassingFactor(v as Float) })
                .setType(
                    Types.REAL
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SPfactor")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FactorObject?)!!.sPfactor }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as FactorObject?)!!.setSetPiecesFactor(v as Float) })
                .setType(
                    Types.REAL
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SCfactor")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FactorObject?)!!.sCfactor }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as FactorObject?)!!.setTorschuss(v as Float) })
                .setType(Types.REAL).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PMfactor")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FactorObject?)!!.pMfactor }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as FactorObject?)!!.setPlaymakingFactor(v as Float) })
                .setType(
                    Types.REAL
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("NormalisationFactor")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as FactorObject?)!!.normalizationFactor }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as FactorObject?)!!.normalizationFactor = v as Float })
                .setType(
                    Types.REAL
                ).isNullable(false).build()
        )
    }

    override fun createPreparedSelectStatementBuilder(): PreparedSelectStatementBuilder? {
        return PreparedSelectStatementBuilder(this, "")
    }

    fun pushFactorsIntoDB(fo: FactorObject?) {
        fo?.let { store(it) }
    }

    fun getFaktorenFromDB():Unit {
        val factors = load(FactorObject::class.java)
        if (factors.isNotEmpty()) {
            for (factor in factors) {
                FormulaFactors.instance().setPositionFactor(factor.position.toInt(), factor)
            }
        } else {
            // use hardcoded values
            FormulaFactors.instance().importDefaults()
        }
    }

    companion object {
        /** tablename  */
        const val TABLENAME = "FAKTOREN"
    }
}
