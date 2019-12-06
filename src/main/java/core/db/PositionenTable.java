package core.db;

import core.model.match.MatchLineupPlayer;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.util.HOLogger;
import org.hsqldb.types.Type;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;


public final class PositionenTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "POSITIONEN";
	
	protected PositionenTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}
	

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[5];
		columns[0]= new ColumnDescriptor("HRF_ID",Types.INTEGER,false);
		columns[1]= new ColumnDescriptor("ID",Types.INTEGER,false);
		columns[2]= new ColumnDescriptor("Aufstellungsname",Types.VARCHAR,false,256);
		columns[3]= new ColumnDescriptor("SpielerID",Types.INTEGER,false);
		columns[4]= new ColumnDescriptor("Taktik",Types.INTEGER,false);
		columns[5]= new ColumnDescriptor("MarkingPlayerId",Types.INTEGER,true);
	}
	
	/**
	 * lädt System Positionen
	 */
	Vector<IMatchRoleID> getSystemPositionen(int hrfID, String sysName) {
		ResultSet rs = null;
		MatchRoleID pos = null;
		String sql = null;
		final Vector<IMatchRoleID> ret = new Vector<IMatchRoleID>();

		sql = "SELECT * FROM "+getTableName()+" WHERE HRF_ID = " + hrfID + " AND Aufstellungsname ='" + sysName + "'";
		rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				rs.beforeFirst();

				while (rs.next()) {
					
					int roleID = rs.getInt("ID");
					int behavior = rs.getByte("Taktik");
					int playerID = rs.getInt("SpielerID");

					Integer markingPlayerId = (Integer)rs.getObject("MarkingPlayerId");
					
					switch (behavior) {
					case IMatchRoleID.OLD_EXTRA_DEFENDER :
						roleID = IMatchRoleID.middleCentralDefender;
						behavior = IMatchRoleID.NORMAL;
						break;
					case IMatchRoleID.OLD_EXTRA_MIDFIELD :
						roleID = IMatchRoleID.centralInnerMidfield;
						behavior = IMatchRoleID.NORMAL;
						break;
					case IMatchRoleID.OLD_EXTRA_FORWARD :
						roleID = IMatchRoleID.centralForward;
						behavior = IMatchRoleID.NORMAL;
						break;
					case IMatchRoleID.OLD_EXTRA_DEFENSIVE_FORWARD :
						roleID = IMatchRoleID.centralForward;
						behavior = IMatchRoleID.DEFENSIVE;
				}

				roleID = MatchRoleID.convertOldRoleToNew(roleID);
					
					if (playerID < 0) {
						playerID = 0;
					}
					
					pos = new MatchRoleID(roleID, playerID, (byte)behavior);
					pos.setMarkingPlayerId(markingPlayerId);
					ret.add(pos);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getSystemPositionen: " + e);
		}

		return ret;
	}

	/**
	 * speichert System Positionen
	 */
	void saveSystemPositionen(int hrfId, Vector<IMatchRoleID> positionen, String sysName) {
		String statement = null;
		MatchRoleID pos = null;

		//bereits vorhandenen Eintrag entdernen
		DBManager.instance().deleteSystem(hrfId, sysName);

		//speichern vorbereiten
		for (int i = 0;(positionen != null) && (sysName != null) && (i < positionen.size()); i++) {
			pos = (MatchRoleID) positionen.elementAt(i);
			statement = "INSERT INTO "+getTableName()+" ( HRF_ID, ID, Aufstellungsname, SpielerID, Taktik, MarkingPlayerId ) VALUES(";
			statement += ("" + hrfId + "," + pos.getId() + ",'" + sysName + "'," + pos.getSpielerId() + "," + pos.getTaktik() + "," + pos.getMarkingPlayerId() + " )");
			adapter.executeUpdate(statement);
		}
	}	

    }

