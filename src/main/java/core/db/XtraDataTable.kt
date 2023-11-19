package core.db

import core.model.XtraData
import core.util.HODateTime
import java.sql.*

internal class XtraDataTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf(
            ColumnDescriptor.Builder.newInstance()
                .setColumnName("HRF_ID")
                .setGetter { p: Any? -> (p as XtraData?)!!.hrfId }
                .setSetter { p: Any?, v: Any -> (p as XtraData?)!!.hrfId = v as Int }
                .setType(Types.INTEGER)
                .isPrimaryKey(true)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.newInstance()
                .setColumnName("CurrencyRate")
                .setGetter { p: Any? -> (p as XtraData?)!!.currencyRate }
                .setSetter { p: Any?, v: Any -> (p as XtraData?)!!.currencyRate = (v as Float).toDouble() }
                .setType(Types.REAL)
                .isNullable(false)
                .build(),
            ColumnDescriptor.Builder.newInstance()
                .setColumnName("HasPromoted")
                .setGetter { p: Any? -> (p as XtraData?)!!.isHasPromoted }
                .setSetter { p: Any?, v: Any -> (p as XtraData?)!!.isHasPromoted = v as Boolean }
                .setType(Types.BOOLEAN)
                .isNullable(false)
                .build(),
            ColumnDescriptor.Builder.newInstance()
                .setColumnName("LogoURL")
                .setGetter { p: Any? -> (p as XtraData?)!!.logoURL }
                .setSetter { p: Any?, v: Any? -> (p as XtraData?)!!.logoURL = v as String? }
                .setType(Types.VARCHAR)
                .setLength(127)
                .isNullable(false)
                .build(),
            ColumnDescriptor.Builder.newInstance()
                .setColumnName("SeriesMatchDate")
                .setGetter { p: Any? -> (p as XtraData?)!!.seriesMatchDate?.toDbTimestamp() }
                .setSetter { p: Any?, v: Any? -> (p as XtraData?)!!.seriesMatchDate = v as HODateTime? }
                .setType(Types.TIMESTAMP)
                .isNullable(false)
                .build(),
            ColumnDescriptor.Builder.newInstance()
                .setColumnName("TrainingDate")
                .setGetter { p: Any? -> (p as XtraData?)!!.nextTrainingDate?.toDbTimestamp() }
                .setSetter { p: Any?, v: Any? -> (p as XtraData?)!!.setTrainingDate(v as HODateTime?) }
                .setType(Types.TIMESTAMP)
                .isNullable(false)
                .build(),
            ColumnDescriptor.Builder.newInstance()
                .setColumnName("EconomyDate")
                .setGetter { p: Any? -> (p as XtraData?)!!.economyDate?.toDbTimestamp() }
                .setSetter { p: Any?, v: Any? -> (p as XtraData?)!!.economyDate = v as HODateTime? }
                .setType(Types.TIMESTAMP)
                .isNullable(false)
                .build(),
            ColumnDescriptor.Builder.newInstance().setColumnName("LeagueLevelUnitID")
                .setGetter { p: Any? -> (p as XtraData?)!!.leagueLevelUnitID }
                .setSetter { p: Any?, v: Any -> (p as XtraData?)!!.leagueLevelUnitID = v as Int }
                .setType(Types.INTEGER)
                .isNullable(false)
                .build(),
            ColumnDescriptor.Builder.newInstance()
                .setColumnName("CountryId")
                .setGetter { p: Any? -> (p as XtraData?)!!.countryId }
                .setSetter { p: Any?, v: Any? -> (p as XtraData?)!!.countryId = v as Int? }
                .setType(Types.INTEGER)
                .isNullable(true)
                .build()
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
            xtra.hrfId = hrfId
            store(xtra)
        }
    }

    companion object {
        val TABLENAME: String = "XTRADATA"
    }
}
