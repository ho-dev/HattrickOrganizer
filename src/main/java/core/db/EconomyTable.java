package core.db;

import core.model.misc.Economy;
import core.util.HODateTime;
import java.sql.Types;


public final class EconomyTable extends AbstractTable {

	public final static String TABLENAME = "ECONOMY";
	
	EconomyTable(ConnectionManager adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((e) -> ((Economy) e).getHrfId()).setSetter((e, v) -> ((Economy) e).setHrfId((int) v)).setType(Types.INTEGER).isNullable(false).isPrimaryKey(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("FetchedDate").setGetter((e) -> ((Economy) e).getFetchedDate().toDbTimestamp()).setSetter((e, v) -> ((Economy) e).setFetchedDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SupportersPopularity").setGetter((e) -> ((Economy) e).getSupportersPopularity()).setSetter((e, v) -> ((Economy) e).setSupPopularity((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SponsorsPopularity").setGetter((e) -> ((Economy) e).getSponsorsPopularity()).setSetter((e, v) -> ((Economy) e).setSponsorsPopularity((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Cash").setGetter((e) -> ((Economy) e).getCash()).setSetter((e, v) -> ((Economy) e).setCash((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeSponsors").setGetter((e) -> ((Economy) e).getIncomeSponsors()).setSetter((e, v) -> ((Economy) e).setIncomeSponsors((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeSpectators").setGetter((e) -> ((Economy) e).getIncomeSpectators()).setSetter((e, v) -> ((Economy) e).setIncomeSpectators((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeFinancial").setGetter((e) -> ((Economy) e).getIncomeFinancial()).setSetter((e, v) -> ((Economy) e).setIncomeFinancial((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeTemporary").setGetter((e) -> ((Economy) e).getIncomeTemporary()).setSetter((e, v) -> ((Economy) e).setIncomeTemporary((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeSum").setGetter((e) -> ((Economy) e).getIncomeSum()).setSetter((e, v) -> ((Economy) e).setIncomeSum((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsPlayers").setGetter((e) -> ((Economy) e).getCostsPlayers()).setSetter((e, v) -> ((Economy) e).setCostsPlayers((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsStaff").setGetter((e) -> ((Economy) e).getCostsStaff()).setSetter((e, v) -> ((Economy) e).setCostsStaff((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsArena").setGetter((e) -> ((Economy) e).getCostsArena()).setSetter((e, v) -> ((Economy) e).setCostsArena((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsYouth").setGetter((e) -> ((Economy) e).getCostsYouth()).setSetter((e, v) -> ((Economy) e).setCostsYouth((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsFinancial").setGetter((e) -> ((Economy) e).getCostsFinancial()).setSetter((e, v) -> ((Economy) e).setCostsFinancial((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsTemporary").setGetter((e) -> ((Economy) e).getCostsTemporary()).setSetter((e, v) -> ((Economy) e).setCostsTemporary((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsSum").setGetter((e) -> ((Economy) e).getCostsSum()).setSetter((e, v) -> ((Economy) e).setCostsSum((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ExpectedWeeksTotal").setGetter((e) -> ((Economy) e).getExpectedWeeksTotal()).setSetter((e, v) -> ((Economy) e).setExpectedWeeksTotal((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeSponsors").setGetter((e) -> ((Economy) e).getLastIncomeSponsors()).setSetter((e, v) -> ((Economy) e).setLastIncomeSponsors((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeSpectators").setGetter((e) -> ((Economy) e).getLastIncomeSpectators()).setSetter((e, v) -> ((Economy) e).setLastIncomeSpectators((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeFinancial").setGetter((e) -> ((Economy) e).getLastIncomeFinancial()).setSetter((e, v) -> ((Economy) e).setLastIncomeFinancial((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeTemporary").setGetter((e) -> ((Economy) e).getLastIncomeTemporary()).setSetter((e, v) -> ((Economy) e).setLastIncomeTemporary((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeSum").setGetter((e) -> ((Economy) e).getLastIncomeSum()).setSetter((e, v) -> ((Economy) e).setLastIncomeSum((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsPlayers").setGetter((e) -> ((Economy) e).getLastCostsPlayers()).setSetter((e, v) -> ((Economy) e).setLastCostsPlayers((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsStaff").setGetter((e) -> ((Economy) e).getLastCostsStaff()).setSetter((e, v) -> ((Economy) e).setLastCostsStaff((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsArena").setGetter((e) -> ((Economy) e).getLastCostsArena()).setSetter((e, v) -> ((Economy) e).setLastCostsArena((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsYouth").setGetter((e) -> ((Economy) e).getLastCostsYouth()).setSetter((e, v) -> ((Economy) e).setLastCostsYouth((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsFinancial").setGetter((e) -> ((Economy) e).getLastCostsFinancial()).setSetter((e, v) -> ((Economy) e).setLastCostsFinancial((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsTemporary").setGetter((e) -> ((Economy) e).getLastCostsTemporary()).setSetter((e, v) -> ((Economy) e).setLastCostsTemporary((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsSum").setGetter((e) -> ((Economy) e).getLastCostsSum()).setSetter((e, v) -> ((Economy) e).setLastCostsSum((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastWeeksTotal").setGetter((e) -> ((Economy) e).getLastWeeksTotal()).setSetter((e, v) -> ((Economy) e).setLastWeeksTotal((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ExpectedCash").setGetter((e) -> ((Economy) e).getExpectedCash()).setSetter((e, v) -> ((Economy) e).setExpectedCash((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeSoldPlayers").setGetter((e) -> ((Economy) e).getIncomeSoldPlayers()).setSetter((e, v) -> ((Economy) e).setIncomeSoldPlayers((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeSoldPlayersCommission").setGetter((e) -> ((Economy) e).getIncomeSoldPlayersCommission()).setSetter((e, v) -> ((Economy) e).setIncomeSoldPlayersCommission((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsBoughtPlayers").setGetter((e) -> ((Economy) e).getCostsBoughtPlayers()).setSetter((e, v) -> ((Economy) e).setCostsBoughtPlayers((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsArenaBuilding").setGetter((e) -> ((Economy) e).getCostsArenaBuilding()).setSetter((e, v) -> ((Economy) e).setCostsArenaBuilding((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeSoldPlayers").setGetter((e) -> ((Economy) e).getLastIncomeSoldPlayers()).setSetter((e, v) -> ((Economy) e).setLastIncomeSoldPlayers((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeSoldPlayersCommission").setGetter((e) -> ((Economy) e).getLastIncomeSoldPlayersCommission()).setSetter((e, v) -> ((Economy) e).setLastIncomeSoldPlayersCommission((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsBoughtPlayers").setGetter((e) -> ((Economy) e).getLastCostsBoughtPlayers()).setSetter((e, v) -> ((Economy) e).setLastCostsBoughtPlayers((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsArenaBuilding").setGetter((e) -> ((Economy) e).getLastCostsArenaBuilding()).setSetter((e, v) -> ((Economy) e).setLastCostsArenaBuilding((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeSponsorsBonus").setGetter((e) -> ((Economy) e).getIncomeSponsorsBonus()).setSetter((e, v) -> ((Economy) e).setIncomeSponsorsBonus((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeSponsorsBonus").setGetter((e) -> ((Economy) e).getLastIncomeSponsorsBonus()).setSetter((e, v) -> ((Economy) e).setLastIncomeSponsorsBonus((int) v)).setType(Types.INTEGER).isNullable(false).build()
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
			"CREATE INDEX ECONOMY_1 ON " + getTableName() + "(" + columns[0].getColumnName() + "," + columns[1].getColumnName() + ")"
		};
	}

	/**
	 * store the economy info in the database
	 */
	void storeEconomyInfoIntoDB(int hrfId, Economy economy, HODateTime date) {
		if (economy != null) {
			economy.setHrfId(hrfId);
			economy.setFetchedDate(date);
			store(economy);
		}
	}

	// load economy model from specified hrfID
	public Economy getEconomy(int hrfID) {
		if ( hrfID > -1) {
			return loadOne(Economy.class, hrfID);
		}
		return null;
	}
}
