package core.db;

import core.model.misc.Economy;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;


public final class EconomyTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "ECONOMY";
	
	protected EconomyTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	
	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[31];
		columns[0]= new ColumnDescriptor("HRF_ID",Types.INTEGER,false,true);
		columns[1]= new ColumnDescriptor("Datum",Types.TIMESTAMP,false);
		columns[2]= new ColumnDescriptor("Supporter",Types.INTEGER,false);
		columns[3]= new ColumnDescriptor("Sponsoren",Types.INTEGER,false);
		columns[4]= new ColumnDescriptor("Finanzen",Types.INTEGER,false);
		columns[5]= new ColumnDescriptor("EinSponsoren",Types.INTEGER,false);
		columns[6]= new ColumnDescriptor("EinZuschauer",Types.INTEGER,false);
		columns[7]= new ColumnDescriptor("EinZinsen",Types.INTEGER,false);
		columns[8]= new ColumnDescriptor("EinSonstiges",Types.INTEGER,false);
		columns[9]= new ColumnDescriptor("EinGesamt",Types.INTEGER,false);
		columns[10]= new ColumnDescriptor("KostSpieler",Types.INTEGER,false);
		columns[11]= new ColumnDescriptor("KostTrainer",Types.INTEGER,false);
		columns[12]= new ColumnDescriptor("KostStadion",Types.INTEGER,false);
		columns[13]= new ColumnDescriptor("KostJugend",Types.INTEGER,false);
		columns[14]= new ColumnDescriptor("KostZinsen",Types.INTEGER,false);
		columns[15]= new ColumnDescriptor("KostSonstiges",Types.INTEGER,false);
		columns[16]= new ColumnDescriptor("KostGesamt",Types.INTEGER,false);
		columns[17]= new ColumnDescriptor("GewinnVerlust",Types.INTEGER,false);
		columns[18]= new ColumnDescriptor("LetzteEinSponsoren",Types.INTEGER,false);
		columns[19]= new ColumnDescriptor("LetzteEinZuschauer",Types.INTEGER,false);
		columns[20]= new ColumnDescriptor("LetzteEinZinsen",Types.INTEGER,false);
		columns[21]= new ColumnDescriptor("LetzteEinSonstiges",Types.INTEGER,false);
		columns[22]= new ColumnDescriptor("LetzteEinGesamt",Types.INTEGER,false);
		columns[23]= new ColumnDescriptor("LetzteKostSpieler",Types.INTEGER,false);
		columns[24]= new ColumnDescriptor("LetzteKostTrainer",Types.INTEGER,false);
		columns[25]= new ColumnDescriptor("LetzteKostStadion",Types.INTEGER,false);
		columns[26]= new ColumnDescriptor("LetzteKostJugend",Types.INTEGER,false);
		columns[27]= new ColumnDescriptor("LetzteKostZinsen",Types.INTEGER,false);
		columns[28]= new ColumnDescriptor("LetzteKostSonstiges",Types.INTEGER,false);
		columns[29]= new ColumnDescriptor("LetzteKostGesamt",Types.INTEGER,false);
		columns[30]= new ColumnDescriptor("LetzteGewinnVerlust",Types.INTEGER,false);
		
	}

	@Override
	protected String[] getCreateIndizeStatements() {
		return new String[] {
			"CREATE INDEX ECONOMY_1 ON " + getTableName() + "(" + columns[0].getColumnName() + "," + columns[1].getColumnName() + ")",
			"CREATE INDEX ECONOMY_2 ON " + getTableName() + "(" + columns[0].getColumnName() + ")" };
	}
	
	/**
	 * store the economy info in the database
	 */
	void saveEconomyInDB(int hrfId, Economy economy, Timestamp date) {
		String statement = null;
		final String[] awhereS = { columns[0].getColumnName() };
		final String[] awhereV = { "" + hrfId };

		if (economy != null) {
			//erst Vorhandene Aufstellung löschen
			delete( awhereS, awhereV );
			//insert vorbereiten
			statement =
				"INSERT INTO "+getTableName()+" ( HRF_ID, Supporter , Sponsoren , Finanzen , EinSponsoren , EinZuschauer , EinZinsen , EinSonstiges , EinGesamt , KostSpieler , KostTrainer , KostStadion, KostJugend , KostZinsen , KostSonstiges , KostGesamt , GewinnVerlust , LetzteEinSponsoren , LetzteEinZuschauer , LetzteEinZinsen , LetzteEinSonstiges , LetzteEinGesamt , LetzteKostSpieler , LetzteKostTrainer , LetzteKostStadion, LetzteKostJugend , LetzteKostZinsen , LetzteKostSonstiges , LetzteKostGesamt , LetzteGewinnVerlust, Datum ) VALUES(";
			statement
				+= (""
					+ hrfId
					+ ","
					+ economy.getSupporter()
					+ ","
					+ economy.getSponsoren()
					+ ","
					+ economy.getFinanzen()
					+ ","
					+ economy.getEinnahmenSponsoren()
					+ ","
					+ economy.getEinnahmenZuschauer()
					+ ","
					+ economy.getEinnahmenZinsen()
					+ ","
					+ economy.getEinnahmenSonstige()
					+ ","
					+ economy.getEinnahmenGesamt()
					+ ","
					+ economy.getKostenSpieler()
					+ ","
					+ economy.getKostenTrainerstab()
					+ ","
					+ economy.getKostenStadion()
					+ ","
					+ economy.getKostenJugend()
					+ ","
					+ economy.getKostenZinsen()
					+ ","
					+ economy.getKostenSonstige()
					+ ","
					+ economy.getKostenGesamt()
					+ ","
					+ economy.getGewinnVerlust()
					+ ","
					+ economy.getLetzteEinnahmenSponsoren()
					+ ","
					+ economy.getLetzteEinnahmenZuschauer()
					+ ","
					+ economy.getLetzteEinnahmenZinsen()
					+ ","
					+ economy.getLetzteEinnahmenSonstige()
					+ ","
					+ economy.getLetzteEinnahmenGesamt()
					+ ","
					+ economy.getLetzteKostenSpieler()
					+ ","
					+ economy.getLetzteKostenTrainerstab()
					+ ","
					+ economy.getLetzteKostenStadion()
					+ ","
					+ economy.getLetzteKostenJugend()
					+ ","
					+ economy.getLetzteKostenZinsen()
					+ ","
					+ economy.getLetzteKostenSonstige()
					+ ","
					+ economy.getLetzteKostenGesamt()
					+ ","
					+ economy.getLetzteGewinnVerlust()
					+ ",'"
					+ date.toString()
					+ "' )");
			adapter.executeUpdate(statement);
		}
	}
	
	/**
	 * lädt die Finanzen zum angegeben HRF file ein
	 */
	Economy getEconomy(int hrfID) {
		ResultSet rs = null;
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
