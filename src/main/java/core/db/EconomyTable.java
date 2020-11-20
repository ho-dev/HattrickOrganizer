package core.db;

import core.model.misc.Economy;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;


public final class EconomyTable extends AbstractTable {

	public final static String TABLENAME = "ECONOMY";
	
	protected EconomyTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	
	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[40];
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
		columns[30]= new ColumnDescriptor("ExpectedCash",Types.INTEGER,false);
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
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
			"CREATE INDEX ECONOMY_1 ON " + getTableName() + "(" + columns[0].getColumnName() + "," + columns[1].getColumnName() + ")",
			"CREATE INDEX ECONOMY_2 ON " + getTableName() + "(" + columns[0].getColumnName() + ")" };
	}
	
	/**
	 * store the economy info in the database
	 */
	void storeEconomyInfoIntoDB(int hrfId, Economy economy, Timestamp date) {
		String statement = null;
		final String[] whereColumns = { columns[0].getColumnName() };
		final String[] whereValues = { "" + hrfId };

		if (economy != null) {
			//first delete existing entry
			delete( whereColumns, whereValues );

			//insert new data
			statement =
				"INSERT INTO "+ getTableName()+" ( HRF_ID, FetchedDate, SupportersPopularity, SponsorsPopularity, Cash, IncomeSponsors, IncomeSpectators, IncomeFinancial, IncomeTemporary, IncomeSum, CostsPlayers, CostsStaff, CostsArena, CostsYouth, CostsFinancial, CostsTemporary, CostsSum, ExpectedWeeksTotal, LastIncomeSponsors, LastIncomeSpectators, LastIncomeFinancial, LastIncomeTemporary, LastIncomeSum, LastCostsPlayers, LastCostsStaff, LastCostsArena, LastCostsYouth, LastCostsFinancial, LastCostsTemporary, LastCostsSum, LastWeeksTotal, ExpectedCash, IncomeSoldPlayers, IncomeSoldPlayersCommission, CostsBoughtPlayers, CostsArenaBuilding, LastIncomeSoldPlayers, LastIncomeSoldPlayersCommission, LastCostsBoughtPlayers, LastCostsArenaBuilding) VALUES(";
			statement
				+= (""
					+ hrfId	+ ", '" + date.toString() + "', "	+ economy.getSupportersPopularity()	+ ", "+ economy.getSponsorsPopularity()	+ ", "
					+ economy.getCash()	+ ", " + economy.getIncomeSponsors() + ", "	+ economy.getIncomeSpectators()	+ ", " + economy.getIncomeFinancial() + ", "
					+ economy.getIncomeTemporary() + ", " + economy.getIncomeSum() + ", " + economy.getCostsPlayers() + ", " + economy.getCostsStaff() + ", "
					+ economy.getCostsArena() + ", " + economy.getCostsYouth() + ", " + economy.getCostsFinancial()	+ ", " + economy.getCostsTemporary() + ","
					+ economy.getCostsSum()	+ ", " + economy.getExpectedWeeksTotal() + ", "	+ economy.getLastIncomeSponsors() + ","	+ economy.getLastIncomeSpectators()	+ ", "
					+ economy.getLastIncomeFinancial() + ", " + economy.getLastIncomeTemporary() + ", " + economy.getLastIncomeSum() + ", "	+ economy.getLastCostsPlayers() + ","
					+ economy.getLastCostsStaff() + ", " + economy.getLastCostsArena() + ", " + economy.getLastCostsYouth() + ", " + economy.getLastCostsFinancial() + ", "
					+ economy.getLastCostsTemporary() + ", " + economy.getLastCostsSum() + ", "	+ economy.getLastWeeksTotal() + ", " + economy.getExpectedCash() + ", "
					+ economy.getIncomeSoldPlayers() + ", " + economy.getIncomeSoldPlayersCommission() + ", "	+ economy.getCostsBoughtPlayers() + ", " + economy.getCostsArenaBuilding() + ", "
					+ economy.getLastIncomeSoldPlayers() + ", " + economy.getLastIncomeSoldPlayersCommission() + ", "	+ economy.getLastCostsBoughtPlayers() + ", " + economy.getLastCostsArenaBuilding()
					+ ")");
			adapter.executeUpdate(statement);
		}
	}
	

	 // load economy model from specified hrfID
	public Economy getEconomy(int hrfID) {
		ResultSet rs;
		Economy economy = null;

		rs = getSelectByHrfID(hrfID);

		try {
			if (rs != null) {
				rs.first();
				economy = new Economy(rs);
				rs.close();
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getFinanzen: " + e);
		}

		return economy;
	}
	
}
