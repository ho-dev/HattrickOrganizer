package core.db

import core.model.misc.Economy
import core.util.HODateTime
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class EconomyTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HRF_ID")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.hrfId }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.hrfId = v as Int }).setType(Types.INTEGER)
                .isNullable(false).isPrimaryKey(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("FetchedDate")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.fetchedDate.toDbTimestamp() }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any? -> (e as Economy?)!!.fetchedDate = v as HODateTime? }).setType(
                Types.TIMESTAMP
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SupportersPopularity")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.supportersPopularity }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.setSupPopularity(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SponsorsPopularity")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.sponsorsPopularity }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.sponsorsPopularity = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Cash")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.cash }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.cash = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("IncomeSponsors")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.incomeSponsors }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.incomeSponsors = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("IncomeSpectators")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.incomeSpectators }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.incomeSpectators = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("IncomeFinancial")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.incomeFinancial }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.incomeFinancial = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("IncomeTemporary")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.incomeTemporary }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.incomeTemporary = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("IncomeSum")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.incomeSum }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.incomeSum = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CostsPlayers")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.costsPlayers }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.costsPlayers = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CostsStaff")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.costsStaff }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.costsStaff = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CostsArena")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.costsArena }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.costsArena = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CostsYouth")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.costsYouth }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.costsYouth = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CostsFinancial")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.costsFinancial }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.costsFinancial = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CostsTemporary")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.costsTemporary }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.costsTemporary = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CostsSum")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.costsSum }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.costsSum = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ExpectedWeeksTotal")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.expectedWeeksTotal }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.expectedWeeksTotal = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastIncomeSponsors")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastIncomeSponsors }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastIncomeSponsors = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastIncomeSpectators")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastIncomeSpectators }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastIncomeSpectators = v as Int }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastIncomeFinancial")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastIncomeFinancial }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastIncomeFinancial = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastIncomeTemporary")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastIncomeTemporary }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastIncomeTemporary = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastIncomeSum")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastIncomeSum }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastIncomeSum = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastCostsPlayers")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastCostsPlayers }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastCostsPlayers = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastCostsStaff")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastCostsStaff }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastCostsStaff = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastCostsArena")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastCostsArena }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastCostsArena = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastCostsYouth")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastCostsYouth }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastCostsYouth = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastCostsFinancial")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastCostsFinancial }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastCostsFinancial = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastCostsTemporary")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastCostsTemporary }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastCostsTemporary = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastCostsSum")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastCostsSum }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastCostsSum = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastWeeksTotal")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastWeeksTotal }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastWeeksTotal = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ExpectedCash")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.expectedCash }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.expectedCash = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("IncomeSoldPlayers")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.incomeSoldPlayers }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.incomeSoldPlayers = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("IncomeSoldPlayersCommission").setGetter(
                Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.incomeSoldPlayersCommission }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.incomeSoldPlayersCommission = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CostsBoughtPlayers")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.costsBoughtPlayers }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.costsBoughtPlayers = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CostsArenaBuilding")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.costsArenaBuilding }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.costsArenaBuilding = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastIncomeSoldPlayers")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastIncomeSoldPlayers }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastIncomeSoldPlayers = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastIncomeSoldPlayersCommission").setGetter(
                Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastIncomeSoldPlayersCommission }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any ->
                    (e as Economy?)!!.lastIncomeSoldPlayersCommission = v as Int
                }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastCostsBoughtPlayers")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastCostsBoughtPlayers }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastCostsBoughtPlayers = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastCostsArenaBuilding")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastCostsArenaBuilding }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastCostsArenaBuilding = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("IncomeSponsorsBonus")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.incomeSponsorsBonus }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.incomeSponsorsBonus = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastIncomeSponsorsBonus")
                .setGetter(Function<Any?, Any?> { e: Any? -> (e as Economy?)!!.lastIncomeSponsorsBonus }).setSetter(
                BiConsumer<Any?, Any> { e: Any?, v: Any -> (e as Economy?)!!.lastIncomeSponsorsBonus = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build()
        )
    }

     override val createIndexStatement: Array<String?>
         get() = arrayOf(
            "CREATE INDEX ECONOMY_1 ON " + tableName + "(" + columns[0].columnName + "," + columns[1].columnName + ")"
        )

    /**
     * store the economy info in the database
     */
    fun storeEconomyInfoIntoDB(hrfId: Int, economy: Economy?, date: HODateTime?) {
        if (economy != null) {
            economy.hrfId = hrfId
            economy.fetchedDate = date
            store(economy)
        }
    }

    // load economy model from specified hrfID
    fun getEconomy(hrfID: Int): Economy? {
        return if (hrfID > -1) {
            loadOne(Economy::class.java, hrfID)
        } else null
    }

    companion object {
        const val TABLENAME = "ECONOMY"
    }
}
