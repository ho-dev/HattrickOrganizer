package core.db;

import core.model.player.IMatchRoleID;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Types;


final class SpielerNotizenTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "SPIELERNOTIZ";
	
	SpielerNotizenTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[7];
		columns[0]= new ColumnDescriptor("SpielerID",Types.INTEGER,false,true);
		columns[1]= new ColumnDescriptor("Notiz",Types.VARCHAR,false,2048);
		columns[2]= new ColumnDescriptor("Spielberechtigt",Types.BOOLEAN,false);
		columns[3]= new ColumnDescriptor("TeamInfoSmilie",Types.VARCHAR,false,127);
		columns[4]= new ColumnDescriptor("ManuellerSmilie",Types.VARCHAR,false,127);
		columns[5]= new ColumnDescriptor("userPos",Types.INTEGER,false);
		columns[6]= new ColumnDescriptor("isFired",Types.BOOLEAN,false);
	}

	byte getSpielerUserPosFlag(int spielerId) {
		if (spielerId <= 0) {
			return IMatchRoleID.UNKNOWN;
		}

		var sql = "SELECT userPos FROM "+getTableName()+" WHERE SpielerID = " + spielerId;
		var rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				if (rs.first()) {
					return rs.getByte("userPos");
				}
				
				return IMatchRoleID.UNKNOWN;
				
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getSpielerUserPosFlag: " + e);
		}

		return IMatchRoleID.UNKNOWN;
	}
	
	String getManuellerSmilie(int spielerId) {

		var sql = "SELECT ManuellerSmilie FROM "+getTableName()+" WHERE SpielerID = " + spielerId;
		var rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				if (rs.first()) {
					return rs.getString("ManuellerSmilie");
				} else {
					return "";
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getManuellerSmilie: " + e);
		}

		return "";
	}

	String getSpielerNotiz(int spielerId) {

		var sql = "SELECT Notiz FROM "+getTableName()+" WHERE SpielerID = " + spielerId;
		var rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				if (rs.first()) {
					return core.db.DBManager.deleteEscapeSequences(rs.getString("Notiz"));
				} else {
					return "";
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getSpielerNotiz: " + e);
		}

		return "";
	}
	
	boolean getSpielerSpielberechtigt(int spielerId) {

		var sql = "SELECT Spielberechtigt FROM "+getTableName()+" WHERE SpielerID = " + spielerId;
		var rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				if (rs.first()) {
					return rs.getBoolean("Spielberechtigt");
				} else {
					return true;
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getSpielberechtigt: " + e);
		}

		return true;
	}

	String getTeamInfoSmilie(int spielerId) {

		var sql = "SELECT TeamInfoSmilie FROM "+getTableName()+" WHERE SpielerID = " + spielerId;
		var rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				if (rs.first()) {
					return rs.getString("TeamInfoSmilie");
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getTeamInfoSmilie: " + e);
		}

		return "";
	}

	boolean getIsSpielerFired(int spielerId) {

		var sql = "SELECT isFired FROM " + getTableName() + " WHERE SpielerID = " + spielerId;
		var rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				if (rs.first()) {
					return rs.getBoolean("isFired");
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getIsSpielerFired: " + e);
		}

		return false;
	}
	
	void saveManuellerSmilie(int spielerId, String smilie) {

		//        String[]                awhereS     =   { "SpielerID" };
		//        String[]                awhereV     =   { "" + spielerId };
		if (spielerId > 0) {
			//erst UPdate versuchen
			try {
				var statement = "UPDATE "+getTableName()+" SET ManuellerSmilie='" + smilie + "' WHERE SpielerID = " + spielerId;

				//Insert falls kein passender Eintrag gefunden wurde
				if (adapter.executeUpdate(statement) < 1) {
					//erst Vorhandene Aufstellung löschen
					//deleteSpielerNotizTabelle( awhereS, awhereV );
					//insert vorbereiten
					createEntry(spielerId,"","",smilie,true,(byte) -1, false);
				}
			} catch (Exception e) {
				createEntry(spielerId,"","",smilie,true,(byte) -1, false);
			}
		}
	}

	void saveSpielerNotiz(int spielerId, String notiz) {

		if (spielerId > 0) {
			//erst UPdate versuchen
			try {
				var statement = "UPDATE " + getTableName() + " SET Notiz='" + core.db.DBManager.insertEscapeSequences(notiz) + "' WHERE SpielerID = " + spielerId;

				//Insert falls kein passender Eintrag gefunden wurde
				if (adapter.executeUpdate(statement) < 1) {
					//erst Vorhandene Aufstellung löschen
					//deleteSpielerNotizTabelle( awhereS, awhereV );
					//insert vorbereiten
					createEntry(spielerId,notiz,"","",true,(byte) -1, false);
				}
			} catch (Exception e) {
				createEntry(spielerId,notiz,"","",true,(byte) -1, false);
			}
		}
	}
	
	void saveSpielerSpielberechtigt(int spielerId, boolean spielberechtigt) {

		//       String[]                awhereS     =   { "SpielerID" };
		//       String[]                awhereV     =   { "" + spielerId };
		if (spielerId > 0) {
			try {
				//erst UPdate versuchen
				var statement = "UPDATE " + getTableName() + " SET Spielberechtigt=" + spielberechtigt + " WHERE SpielerID = " + spielerId;

				//Insert falls kein passender Eintrag gefunden wurde
				if (adapter.executeUpdate(statement) < 1) {
					//erst Vorhandene Aufstellung löschen
					//deleteSpielerNotizTabelle( awhereS, awhereV );
					//insert vorbereiten
					createEntry(spielerId,"","","",spielberechtigt,(byte) -1, false);
				}
			} catch (Exception e) {
				createEntry(spielerId,"","","",spielberechtigt,(byte) -1, false);
			}
		}
	}

	void saveSpielerUserPosFlag(int spielerId, byte flag) {

		if (spielerId > 0) {
			//erst UPdate versuchen
			try {
				var statement = "UPDATE " + getTableName() + " SET userPos=" + flag + " WHERE SpielerID = " + spielerId;

				//Insert falls kein passender Eintrag gefunden wurde
				if (adapter.executeUpdate(statement) < 1) {
					createEntry(spielerId,"","","",true, flag, false);
				}
			} catch (Exception e) {
			}
		}
	}

	void saveTeamInfoSmilie(int spielerId, String smilie) {

		if (spielerId > 0) {
			//erst UPdate versuchen
			try {
				var statement = "UPDATE " + getTableName() + " SET TeamInfoSmilie='" + smilie + "' WHERE SpielerID = " + spielerId;

				//Insert falls kein passender Eintrag gefunden wurde
				if (adapter.executeUpdate(statement) < 1) {
					//erst Vorhandene Aufstellung löschen
					//deleteSpielerNotizTabelle( awhereS, awhereV );
					//insert vorbereiten
					createEntry(spielerId,"",smilie,"",true,(byte) -1, false);
				}
			} catch (Exception e) {
				createEntry(spielerId,"",smilie,"",true,(byte) -1, false);
			}
		}
	}

	void saveIsSpielerFired(int spielerId, boolean isFired) {

		if (spielerId > 0) {
			try {
				//erst UPdate versuchen
				var statement = "UPDATE " + getTableName() + " SET isFired=" + isFired + " WHERE SpielerID = " + spielerId;

				//Insert falls kein passender Eintrag gefunden wurde
				if (adapter.executeUpdate(statement) < 1) {
					//erst Vorhandene Aufstellung löschen
					//deleteSpielerNotizTabelle( awhereS, awhereV );
					//insert vorbereiten
					createEntry(spielerId,"","","",true,(byte) -1, isFired);
				}
			} catch (Exception e) {
				createEntry(spielerId,"","","",true,(byte) -1, isFired);
			}
		}
	}

	private void createEntry(int spielerId, String notiz, String teamSmilie, String manualSmilie, boolean spielberechtigt, byte flag, boolean isFired) {
		String statement = "INSERT INTO " + getTableName() + " ( SpielerID, Notiz, TeamInfoSmilie, ManuellerSmilie, Spielberechtigt, userPos, isFired ) VALUES(";
		statement += ("" + spielerId + ",'" + core.db.DBManager.insertEscapeSequences(notiz) + "' ," + "'" + teamSmilie + "'," + "'" + manualSmilie + "'," + spielberechtigt + "," + flag + "," + isFired + ")");
		adapter.executeUpdate(statement);
	}
	
}
