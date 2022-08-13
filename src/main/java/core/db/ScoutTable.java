package core.db;

import core.util.HOLogger;
import module.transfer.scout.ScoutEintrag;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;


final class ScoutTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "SCOUT";
	
	ScoutTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[26];
		columns[0]= new ColumnDescriptor("PlayerID",Types.INTEGER,false);
		columns[1]= new ColumnDescriptor("Name",Types.VARCHAR,true,127);
		columns[2]= new ColumnDescriptor("Info",Types.VARCHAR,false,256);
		columns[3]= new ColumnDescriptor("Age",Types.INTEGER,false);
		columns[4]= new ColumnDescriptor("Marktwert",Types.INTEGER,false);
		columns[5]= new ColumnDescriptor("Speciality",Types.INTEGER,false);
		columns[6]= new ColumnDescriptor("Kondition",Types.INTEGER,false);
		columns[7]= new ColumnDescriptor("Erfahrung",Types.INTEGER,false);
		columns[8]= new ColumnDescriptor("Form",Types.INTEGER,false);
		columns[9]= new ColumnDescriptor("Torwart",Types.INTEGER,false);
		columns[10]= new ColumnDescriptor("Verteidigung",Types.INTEGER,false);
		columns[11]= new ColumnDescriptor("Spielaufbau",Types.INTEGER,false);
		columns[12]= new ColumnDescriptor("Fluegel",Types.INTEGER,false);
		columns[13]= new ColumnDescriptor("Torschuss",Types.INTEGER,false);
		columns[14]= new ColumnDescriptor("Passpiel",Types.INTEGER,false);
		columns[15]= new ColumnDescriptor("Standards",Types.INTEGER,false);
		columns[16]= new ColumnDescriptor("Price",Types.INTEGER,false);
		columns[17]= new ColumnDescriptor("Deadline",Types.TIMESTAMP,false);
		columns[18]= new ColumnDescriptor("Wecker",Types.BOOLEAN,false);
		columns[19]= new ColumnDescriptor("AgeDays",Types.INTEGER,false);
		columns[20]= new ColumnDescriptor("Agreeability",Types.INTEGER,false);
		columns[21]= new ColumnDescriptor("baseWage",Types.INTEGER,false);
		columns[22]= new ColumnDescriptor("Nationality",Types.INTEGER,false);
		columns[23]= new ColumnDescriptor("Leadership",Types.INTEGER,false);
		columns[24]= new ColumnDescriptor("Loyalty",Types.INTEGER,false);
		columns[25]= new ColumnDescriptor("MotherClub",Types.BOOLEAN,false);
	}

	@Override
	protected PreparedStatement createDeleteStatement(){
		return createDeleteStatement(""); // delete all
	}
	/**
	 * Save players from TransferScout
	 */
	void saveScoutList(Vector<ScoutEintrag> list) {
		String sql = "";
		String bool = "0";
		String hg = "0";
		// What should be done when list = null?? jailbird.
		if (list != null) {
			// Delete already existing list

			try {
				executePreparedDelete();

				for (var scout : list) {
					executePreparedInsert(
							scout.getPlayerID(),
							scout.getName(),
							scout.getInfo(),
							scout.getAlter(),
							scout.getTSI(),
							scout.getSpeciality(),
							scout.getKondition(),
							scout.getErfahrung(),
							scout.getForm(),
							scout.getTorwart(),
							scout.getVerteidigung(),
							scout.getSpielaufbau(),
							scout.getFluegelspiel(),
							scout.getTorschuss(),
							scout.getPasspiel(),
							scout.getStandards(),
							scout.getPrice(),
							scout.getDeadline(),
							scout.isWecker(),
							scout.getAgeDays(),
							scout.getAgreeability(),
							scout.getbaseWage(),
							scout.getNationality(),
							scout.getLeadership(),
							scout.getLoyalty(),
							scout.isHomegrown()
					);
				}

			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "DBZugriff.saveScoutList: " + e);
			}
		}
	}
	
	/**
	 * Load player list for insertion into TransferScout
	 */
	Vector<ScoutEintrag> getScoutList() {
		final Vector<ScoutEintrag> ret = new Vector<ScoutEintrag>();

		try {
			final ResultSet rs = executePreparedSelect();
			rs.beforeFirst();
			while (rs.next()) {
				final ScoutEintrag scout = new ScoutEintrag(rs);
				ret.add(scout);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DBZugriff.getScoutList: " + e);
		}
		return ret;
	}
	
}
