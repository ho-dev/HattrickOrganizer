package core.db;

import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.model.Team;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Types;



final class TeamTable extends AbstractTable {
	public final static String TABLENAME = "TEAM";
	
	protected TeamTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[19];
		columns[0]= new ColumnDescriptor("HRF_ID",Types.INTEGER,false,true);
		columns[1]= new ColumnDescriptor("TrainingsIntensitaet",Types.INTEGER,false);
		columns[2]= new ColumnDescriptor("TrainingsArt",Types.INTEGER,false);
		columns[3]= new ColumnDescriptor("sTrainingsArt",Types.VARCHAR,true,127);
		columns[4]= new ColumnDescriptor("iStimmung",Types.INTEGER,false);
		columns[5]= new ColumnDescriptor("sStimmung",Types.VARCHAR,true,127);
		columns[6]= new ColumnDescriptor("iSelbstvertrauen",Types.INTEGER,false);
		columns[7]= new ColumnDescriptor("sSelbstvertrauen",Types.VARCHAR,true,127);
		columns[8]= new ColumnDescriptor("iErfahrung541",Types.INTEGER,false);
		columns[9]= new ColumnDescriptor("iErfahrung433",Types.INTEGER,false);
		columns[10]= new ColumnDescriptor("iErfahrung352",Types.INTEGER,false);
		columns[11]= new ColumnDescriptor("iErfahrung451",Types.INTEGER,false);
		columns[12]= new ColumnDescriptor("iErfahrung532",Types.INTEGER,false);
		columns[13]= new ColumnDescriptor("iErfahrung343",Types.INTEGER,false);
		columns[14]= new ColumnDescriptor("StaminaTrainingPart",Types.INTEGER,false);
		columns[15]= new ColumnDescriptor("iErfahrung442",Types.INTEGER,false);
		columns[16]= new ColumnDescriptor("iErfahrung523",Types.INTEGER,false);
		columns[17]= new ColumnDescriptor("iErfahrung550",Types.INTEGER,false);
		columns[18]= new ColumnDescriptor("iErfahrung253",Types.INTEGER,false);
	}

	@Override
	protected String[] getCreateIndizeStatements() {
		return new String[] {
			"CREATE INDEX ITEAM_1 ON " + getTableName() + "(" + columns[0].getColumnName() + ")"};
	}
	/**
	 * Save the team data for the given HRF id.
	 */
	void saveTeam(int hrfId, Team team) {
		String statement = null;
		final String[] awhereS = { "HRF_ID" };
		final String[] awhereV = { "" + hrfId };

		if (team != null) {
			//delete existing lineup
			delete( awhereS, awhereV );
			//prepare insert statment 
			statement = "INSERT INTO " + getTableName()
					+ " ( TrainingsIntensitaet , StaminaTrainingPart, TrainingsArt, sTrainingsArt , iStimmung, sStimmung , iSelbstvertrauen, sSelbstvertrauen , iErfahrung541 , iErfahrung433 , iErfahrung352 , iErfahrung451 , iErfahrung532 , iErfahrung343, iErfahrung442, iErfahrung523, iErfahrung550, iErfahrung253, HRF_ID ) VALUES(";
			statement
				+= ("" + team.getTrainingslevel()
					+ "," + team.getStaminaTrainingPart()
					+ "," + team.getTrainingsArtAsInt()
					+ ",'" 
					+ "'," + team.getStimmungAsInt()
					+ ",'" 
					+ "'," + team.getSelbstvertrauenAsInt()
					+ ",'" 
					+ "'," + team.getFormationExperience541()
					+ "," + team.getFormationExperience433()
					+ "," + team.getFormationExperience352()
					+ "," + team.getFormationExperience451()
					+ "," + team.getFormationExperience532()
					+ "," + team.getFormationExperience343()
					+ "," + team.getFormationExperience442()
					+ "," + team.getFormationExperience523()
					+ "," + team.getFormationExperience550()
					+ "," + team.getFormationExperience253()
					+ "," + hrfId
					+ " )");
			adapter.executeUpdate(statement);
		}
	}

	/**
	 * Gibt die Teamstimmung und das Selbstvertrauen für ein HRFID zurück [0] = Stimmung [1] =
	 * Selbstvertrauen
	 */
	String[] getStimmmungSelbstvertrauen(int hrfid) {
		final int[] intvalue = new int[2];
		final String[] returnvalue = new String[2];
		final String sql = "SELECT iStimmung, iSelbstvertrauen, sStimmung, sSelbstvertrauen FROM "+getTableName()+" WHERE HRF_ID=" + hrfid;

		try {
			final ResultSet rs = adapter.executeQuery(sql);

			if (rs.first()) {
				intvalue[0] = rs.getInt("iStimmung");
				intvalue[1] = rs.getInt("iSelbstvertrauen");

				//Keine Sinnvollen Werte in der DB -> Strings holen
				if ((intvalue[0] <= 0) && (intvalue[1] <= 0)) {
					returnvalue[0] = rs.getString("sStimmung");
					returnvalue[1] = rs.getString("sSelbstvertrauen");
				} else {
					returnvalue[0] = TeamSpirit.toString(intvalue[0]);
					returnvalue[1] = TeamConfidence.toString(intvalue[1]);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getStimmmungSelbstvertrauen : " + e);
		}

		return returnvalue;
	}

	/**
	 * Gibt die Teamstimmung und das Selbstvertrauen für ein HRFID zurück [0] = Stimmung [1] =
	 * Selbstvertrauen
	 */
	int[] getStimmmungSelbstvertrauenValues(int hrfid) {
		final int[] intvalue = new int[2];
		final String sql = "SELECT iStimmung, iSelbstvertrauen, sStimmung, sSelbstvertrauen FROM "+getTableName()+" WHERE HRF_ID=" + hrfid;

		try {
			final ResultSet rs = adapter.executeQuery(sql);

			if (rs.first()) {
				intvalue[0] = rs.getInt("iStimmung");
				intvalue[1] = rs.getInt("iSelbstvertrauen");
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getStimmmungSelbstvertrauenValues : " + e);
		}

		return intvalue;
	}
	
	/**
	 * load the team data for the given HRF id
	 */
	Team getTeam(int hrfID) {
		ResultSet rs = null;
		Team team = null;

		rs = getSelectByHrfID(hrfID);

		try {
			if (rs != null) {
				rs.first();
				team = new Team(rs);
				rs.close();
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getTeam: " + e);
		}

		return team;
	}
}
