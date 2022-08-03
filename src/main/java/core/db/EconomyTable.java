package core.db;

import core.model.misc.Economy;
import core.util.HOLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;


public final class EconomyTable extends AbstractTable {

	public final static String TABLENAME = "ECONOMY";
	
	EconomyTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[42];
		columns[0]= new ColumnDescriptor("HRF_ID",Types.INTEGER,false,true);
		columns[1]= new ColumnDescriptor("FetchedDate",Types.TIMESTAMP,false);
		columns[2]= new ColumnDescriptor("SupportersPopularity",Types.INTEGER,false);
		columns[3]= new ColumnDescriptor("SponsorsPopularity",Types.INTEGER,false);
		columns[4]= new ColumnDescriptor("Cash",Types.INTEGER,false);
		columns[5]= new ColumnDescriptor("IncomeSponsors",Types.INTEGER,false);
		columns[6]= new ColumnDescriptor("IncomeSpectators",Types.INTEGER,false);
		columns[7]= new ColumnDescriptor("IncomeFinancial",Types.INTEGER,false);
		columns[8]= new ColumnDescriptor("IncomeTemporary",Types.INTEGER,false);
		columns[9]= new ColumnDescriptor("IncomeSum",Types.INTEGER,false);
		columns[10]= new ColumnDescriptor("CostsPlayers",Types.INTEGER,false);
		columns[11]= new ColumnDescriptor("CostsStaff",Types.INTEGER,false);
		columns[12]= new ColumnDescriptor("CostsArena",Types.INTEGER,false);
		columns[13]= new ColumnDescriptor("CostsYouth",Types.INTEGER,false);
		columns[14]= new ColumnDescriptor("CostsFinancial",Types.INTEGER,false);
		columns[15]= new ColumnDescriptor("CostsTemporary",Types.INTEGER,false);
		columns[16]= new ColumnDescriptor("CostsSum",Types.INTEGER,false);
		columns[17]= new ColumnDescriptor("ExpectedWeeksTotal",Types.INTEGER,false);
		columns[18]= new ColumnDescriptor("LastIncomeSponsors",Types.INTEGER,false);
		columns[19]= new ColumnDescriptor("LastIncomeSpectators",Types.INTEGER,false);
		columns[20]= new ColumnDescriptor("LastIncomeFinancial",Types.INTEGER,false);
		columns[21]= new ColumnDescriptor("LastIncomeTemporary",Types.INTEGER,false);
		columns[22]= new ColumnDescriptor("LastIncomeSum",Types.INTEGER,false);
		columns[23]= new ColumnDescriptor("LastCostsPlayers",Types.INTEGER,false);
		columns[24]= new ColumnDescriptor("LastCostsStaff",Types.INTEGER,false);
		columns[25]= new ColumnDescriptor("LastCostsArena",Types.INTEGER,false);
		columns[26]= new ColumnDescriptor("LastCostsYouth",Types.INTEGER,false);
		columns[27]= new ColumnDescriptor("LastCostsFinancial",Types.INTEGER,false);
		columns[28]= new ColumnDescriptor("LastCostsTemporary",Types.INTEGER,false);
		columns[29]= new ColumnDescriptor("LastCostsSum",Types.INTEGER,false);
		columns[30]= new ColumnDescriptor("LastWeeksTotal",Types.INTEGER,false);
		columns[31]= new ColumnDescriptor("ExpectedCash",Types.INTEGER,false);
		columns[32]= new ColumnDescriptor("IncomeSoldPlayers",Types.INTEGER,false);
		columns[33]= new ColumnDescriptor("IncomeSoldPlayersCommission",Types.INTEGER,false);
		columns[34]= new ColumnDescriptor("CostsBoughtPlayers",Types.INTEGER,false);
		columns[35]= new ColumnDescriptor("CostsArenaBuilding",Types.INTEGER,false);
		columns[36]= new ColumnDescriptor("LastIncomeSoldPlayers",Types.INTEGER,false);
		columns[37]= new ColumnDescriptor("LastIncomeSoldPlayersCommission",Types.INTEGER,false);
		columns[38]= new ColumnDescriptor("LastCostsBoughtPlayers",Types.INTEGER,false);
		columns[39]= new ColumnDescriptor("LastCostsArenaBuilding",Types.INTEGER,false);
		columns[40]= new ColumnDescriptor("IncomeSponsorsBonus",Types.INTEGER,false);
		columns[41]= new ColumnDescriptor("LastIncomeSponsorsBonus",Types.INTEGER,false);
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
			"CREATE INDEX ECONOMY_1 ON " + getTableName() + "(" + columns[0].getColumnName() + "," + columns[1].getColumnName() + ")"
		};
	}

	private PreparedStatement deleteStatement;
	private PreparedStatement getDeleteStatement(){
		if ( deleteStatement==null){
			final String[] where = {"HRF_ID"};
			deleteStatement = createDeleteStatement(where);
		}
		return deleteStatement;
	}
	/**
	 * store the economy info in the database
	 */
	void storeEconomyInfoIntoDB(int hrfId, Economy economy, Timestamp date) {
		if (economy != null) {
			//first delete existing entry
			delete(getDeleteStatement(), hrfId);
			adapter.executePreparedUpdate(getInsertStatement(),
					hrfId,
					date.toString(),
					economy.getSupportersPopularity(),
					economy.getSponsorsPopularity(),
					economy.getCash(),
					economy.getIncomeSponsors(),
					economy.getIncomeSpectators(),
					economy.getIncomeFinancial(),
					economy.getIncomeTemporary(),
					economy.getIncomeSum(),
					economy.getCostsPlayers(),
					economy.getCostsStaff(),
					economy.getCostsArena(),
					economy.getCostsYouth(),
					economy.getCostsFinancial(),
					economy.getCostsTemporary(),
					economy.getCostsSum(),
					economy.getExpectedWeeksTotal(),
					economy.getLastIncomeSponsors(),
					economy.getLastIncomeSpectators(),
					economy.getLastIncomeFinancial(),
					economy.getLastIncomeTemporary(),
					economy.getLastIncomeSum(),
					economy.getLastCostsPlayers(),
					economy.getLastCostsStaff(),
					economy.getLastCostsArena(),
					economy.getLastCostsYouth(),
					economy.getLastCostsFinancial(),
					economy.getLastCostsTemporary(),
					economy.getLastCostsSum(),
					economy.getLastWeeksTotal(),
					economy.getExpectedCash(),
					economy.getIncomeSoldPlayers(),
					economy.getIncomeSoldPlayersCommission(),
					economy.getCostsBoughtPlayers(),
					economy.getCostsArenaBuilding(),
					economy.getLastIncomeSoldPlayers(),
					economy.getLastIncomeSoldPlayersCommission(),
					economy.getLastCostsBoughtPlayers(),
					economy.getLastCostsArenaBuilding(),
					economy.getIncomeSponsorsBonus(),
					economy.getLastIncomeSponsorsBonus()
			);
		}
	}

	// load economy model from specified hrfID
	public Economy getEconomy(int hrfID) {
		ResultSet rs;
		Economy economy = null;
		if ( hrfID > -1) {

			rs = getSelectByHrfID(hrfID);

			try {
				if (rs != null) {
					rs.first();
					economy = new Economy();
					economy.setSupPopularity(rs.getInt("SupportersPopularity"));
					economy.setSponsorsPopularity(rs.getInt("SponsorsPopularity"));
					economy.setCash(rs.getInt("Cash"));
					economy.setIncomeSponsors(rs.getInt("IncomeSponsors"));
					economy.setIncomeSponsorsBonus(rs.getInt("IncomeSponsorsBonus"));
					economy.setIncomeSpectators(rs.getInt("IncomeSpectators"));
					economy.setIncomeFinancial(rs.getInt("IncomeFinancial"));
					economy.setIncomeTemporary(rs.getInt("IncomeTemporary"));
					economy.setIncomeSum(rs.getInt("IncomeSum"));
					economy.setCostsPlayers(rs.getInt("CostsPlayers"));
					economy.setCostsStaff(rs.getInt("CostsStaff"));
					economy.setCostsArena(rs.getInt("CostsArena"));
					economy.setCostsYouth(rs.getInt("CostsYouth"));
					economy.setCostsFinancial(rs.getInt("CostsFinancial"));
					economy.setCostsTemporary(rs.getInt("CostsTemporary"));
					economy.setCostsSum(rs.getInt("CostsSum"));
					economy.setExpectedWeeksTotal(rs.getInt("ExpectedWeeksTotal"));
					economy.setLastIncomeSponsors(rs.getInt("LastIncomeSponsors"));
					economy.setLastIncomeSponsorsBonus(rs.getInt("LastIncomeSponsorsBonus"));
					economy.setLastIncomeSpectators(rs.getInt("LastIncomeSpectators"));
					economy.setLastIncomeFinancial(rs.getInt("LastIncomeFinancial"));
					economy.setLastIncomeTemporary(rs.getInt("LastIncomeTemporary"));
					economy.setLastIncomeSum(rs.getInt("LastIncomeSum"));
					economy.setLastCostsPlayers(rs.getInt("LastCostsPlayers"));
					economy.setLastCostsStaff(rs.getInt("LastCostsStaff"));
					economy.setLastCostsArena(rs.getInt("LastCostsArena"));
					economy.setLastCostsYouth(rs.getInt("LastCostsYouth"));
					economy.setLastCostsFinancial(rs.getInt("LastCostsFinancial"));
					economy.setLastCostsTemporary(rs.getInt("LastCostsTemporary"));
					economy.setLastCostsSum(rs.getInt("LastCostsSum"));
					economy.setLastWeeksTotal(rs.getInt("LastWeeksTotal"));
					economy.setExpectedCash(rs.getInt("ExpectedCash"));
					economy.setIncomeSoldPlayers(rs.getInt("IncomeSoldPlayers"));
					economy.setIncomeSoldPlayersCommission(rs.getInt("IncomeSoldPlayersCommission"));
					economy.setCostsBoughtPlayers(rs.getInt("CostsBoughtPlayers"));
					economy.setCostsArenaBuilding(rs.getInt("CostsArenaBuilding"));
					economy.setLastIncomeSoldPlayers(rs.getInt("LastIncomeSoldPlayers"));
					economy.setLastIncomeSoldPlayersCommission(rs.getInt("LastIncomeSoldPlayersCommission"));
					economy.setLastCostsBoughtPlayers(rs.getInt("LastCostsBoughtPlayers"));
					economy.setCostsArenaBuilding(rs.getInt("LastCostsArenaBuilding"));
					rs.close();
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "DatenbankZugriff.getFinanzen: " + e);
			}
		}
		return economy;
	}
	
}
