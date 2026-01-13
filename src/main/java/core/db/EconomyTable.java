package core.db;

import core.model.misc.Economy;
import core.util.AmountOfMoney;
import core.util.HODateTime;
import java.sql.Types;


public final class EconomyTable extends AbstractTable {

	public static final String TABLENAME = "ECONOMY";

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
				ColumnDescriptor.Builder.newInstance().setColumnName("Cash").setGetter((e) -> ((Economy) e).getCash().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setCash((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeSponsors").setGetter((e) -> ((Economy) e).getIncomeSponsors().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setIncomeSponsors((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeSpectators").setGetter((e) -> ((Economy) e).getIncomeSpectators().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setIncomeSpectators((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeFinancial").setGetter((e) -> ((Economy) e).getIncomeFinancial().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setIncomeFinancial((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeTemporary").setGetter((e) -> ((Economy) e).getIncomeTemporary().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setIncomeTemporary((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeSum").setGetter((e) -> ((Economy) e).getIncomeSum().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setIncomeSum((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsPlayers").setGetter((e) -> ((Economy) e).getCostsPlayers().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setCostsPlayers((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsStaff").setGetter((e) -> ((Economy) e).getCostsStaff().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setCostsStaff((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsArena").setGetter((e) -> ((Economy) e).getCostsArena().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setCostsArena((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsYouth").setGetter((e) -> ((Economy) e).getCostsYouth().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setCostsYouth((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsFinancial").setGetter((e) -> ((Economy) e).getCostsFinancial().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setCostsFinancial((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsTemporary").setGetter((e) -> ((Economy) e).getCostsTemporary().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setCostsTemporary((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsSum").setGetter((e) -> ((Economy) e).getCostsSum().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setCostsSum((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ExpectedWeeksTotal").setGetter((e) -> ((Economy) e).getExpectedWeeksTotal().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setExpectedWeeksTotal((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeSponsors").setGetter((e) -> ((Economy) e).getLastIncomeSponsors().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastIncomeSponsors((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeSpectators").setGetter((e) -> ((Economy) e).getLastIncomeSpectators().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastIncomeSpectators((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeFinancial").setGetter((e) -> ((Economy) e).getLastIncomeFinancial().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastIncomeFinancial((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeTemporary").setGetter((e) -> ((Economy) e).getLastIncomeTemporary().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastIncomeTemporary((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeSum").setGetter((e) -> ((Economy) e).getLastIncomeSum().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastIncomeSum((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsPlayers").setGetter((e) -> ((Economy) e).getLastCostsPlayers().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastCostsPlayers((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsStaff").setGetter((e) -> ((Economy) e).getLastCostsStaff().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastCostsStaff((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsArena").setGetter((e) -> ((Economy) e).getLastCostsArena().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastCostsArena((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsYouth").setGetter((e) -> ((Economy) e).getLastCostsYouth().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastCostsYouth((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsFinancial").setGetter((e) -> ((Economy) e).getLastCostsFinancial().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastCostsFinancial((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsTemporary").setGetter((e) -> ((Economy) e).getLastCostsTemporary().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastCostsTemporary((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsSum").setGetter((e) -> ((Economy) e).getLastCostsSum().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastCostsSum((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastWeeksTotal").setGetter((e) -> ((Economy) e).getLastWeeksTotal().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastWeeksTotal((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ExpectedCash").setGetter((e) -> ((Economy) e).getExpectedCash().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setExpectedCash((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeSoldPlayers").setGetter((e) -> ((Economy) e).getIncomeSoldPlayers().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setIncomeSoldPlayers((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeSoldPlayersCommission").setGetter((e) -> ((Economy) e).getIncomeSoldPlayersCommission().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setIncomeSoldPlayersCommission((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsBoughtPlayers").setGetter((e) -> ((Economy) e).getCostsBoughtPlayers().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setCostsBoughtPlayers((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CostsArenaBuilding").setGetter((e) -> ((Economy) e).getCostsArenaBuilding().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setCostsArenaBuilding((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeSoldPlayers").setGetter((e) -> ((Economy) e).getLastIncomeSoldPlayers().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastIncomeSoldPlayers((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeSoldPlayersCommission").setGetter((e) -> ((Economy) e).getLastIncomeSoldPlayersCommission().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastIncomeSoldPlayersCommission((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsBoughtPlayers").setGetter((e) -> ((Economy) e).getLastCostsBoughtPlayers().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastCostsBoughtPlayers((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastCostsArenaBuilding").setGetter((e) -> ((Economy) e).getLastCostsArenaBuilding().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastCostsArenaBuilding((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("IncomeSponsorsBonus").setGetter((e) -> ((Economy) e).getIncomeSponsorsBonus().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setIncomeSponsorsBonus((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastIncomeSponsorsBonus").setGetter((e) -> ((Economy) e).getLastIncomeSponsorsBonus().getSwedishKrona()).setSetter((e, v) -> ((Economy) e).setLastIncomeSponsorsBonus((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(false).build()
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
