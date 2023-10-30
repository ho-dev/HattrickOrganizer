package core.db

import core.model.XtraData
import core.util.HODateTime
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

internal class XtraDataTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HRF_ID")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as XtraData?)!!.getHrfId() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as XtraData?)!!.setHrfId(v as Int) })
            ).setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CurrencyRate")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as XtraData?)!!.getCurrencyRate() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as XtraData?)!!.setCurrencyRate((v as Float).toDouble()) })
            ).setType(
                Types.REAL
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HasPromoted")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as XtraData?)!!.isHasPromoted() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as XtraData?)!!.setHasPromoted(v as Boolean) })
            ).setType(Types.BOOLEAN).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LogoURL")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as XtraData?)!!.getLogoURL() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as XtraData?)!!.setLogoURL(v as String?) })
            ).setType(Types.VARCHAR).setLength(127).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SeriesMatchDate")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as XtraData?)!!.getSeriesMatchDate().toDbTimestamp() }))
                .setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as XtraData?)!!.setSeriesMatchDate(v as HODateTime?) })
                ).setType(
                Types.TIMESTAMP
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TrainingDate")
                .setGetter(Function<Any?, Any?>({ p: Any? ->
                    (p as XtraData?)!!.getNextTrainingDate().toDbTimestamp()
                })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as XtraData?)!!.setTrainingDate(v as HODateTime?) })
            ).setType(
                Types.TIMESTAMP
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("EconomyDate")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as XtraData?)!!.getEconomyDate().toDbTimestamp() }))
                .setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as XtraData?)!!.setEconomyDate(v as HODateTime?) })
                ).setType(
                Types.TIMESTAMP
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LeagueLevelUnitID")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as XtraData?)!!.getLeagueLevelUnitID() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as XtraData?)!!.setLeagueLevelUnitID(v as Int) })
            ).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CountryId")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as XtraData?)!!.getCountryId() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as XtraData?)!!.setCountryId(v as Int?) })
            ).setType(Types.INTEGER).isNullable(true).build()
        )
    }

    /**
     * load Xtra data
     */
    fun loadXtraData(hrfID: Int): XtraData? {
        return loadOne(XtraData::class.java, hrfID)
    }

    /**
     * speichert das Team
     */
    fun saveXtraDaten(hrfId: Int, xtra: XtraData?) {
        if (xtra != null) {
            xtra.setHrfId(hrfId)
            store(xtra)
        }
    }

    companion object {
        val TABLENAME: String = "XTRADATA"
    }
}
