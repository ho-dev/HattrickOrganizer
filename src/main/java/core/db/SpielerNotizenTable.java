package core.db;

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
		columns = new ColumnDescriptor[6];
		columns[0]= new ColumnDescriptor("SpielerID",Types.INTEGER,false,true);
		columns[1]= new ColumnDescriptor("Notiz",Types.VARCHAR,false,2048);
		columns[2]= new ColumnDescriptor("Spielberechtigt",Types.BOOLEAN,false);
		columns[3]= new ColumnDescriptor("TeamInfoSmilie",Types.VARCHAR,false,127);
		columns[4]= new ColumnDescriptor("ManuellerSmilie",Types.VARCHAR,false,127);
		columns[5]= new ColumnDescriptor("userPos",Types.INTEGER,false);
	}

	byte getSpielerUserPosFlag(int spielerId) {
		if (spielerId <= 0) {
			return core.model.player.ISpielerPosition.UNKNOWN;
		}

		ResultSet rs = null;
		String sql = null;

		sql = "SELECT userPos FROM "+getTableName()+" WHERE SpielerID = " + spielerId;
		rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				if (rs.first()) {
					return rs.getByte("userPos");
				}
				
				return core.model.player.ISpielerPosition.UNKNOWN;
				
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getSpielerUserPosFlag: " + e);
		}

		return core.model.player.ISpielerPosition.UNKNOWN;
	}
	
	String getManuellerSmilie(int spielerId) {
		ResultSet rs = null;
		String sql = null;

		sql = "SELECT ManuellerSmilie FROM "+getTableName()+" WHERE SpielerID = " + spielerId;
		rs = adapter.executeQuery(sql);

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
		ResultSet rs = null;
		String sql = null;

		sql = "SELECT Notiz FROM "+getTableName()+" WHERE SpielerID = " + spielerId;
		rs = adapter.executeQuery(sql);

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
		ResultSet rs = null;
		String sql = null;

		sql = "SELECT Spielberechtigt FROM "+getTableName()+" WHERE SpielerID = " + spielerId;
		rs = adapter.executeQuery(sql);

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
		ResultSet rs = null;
		String sql = null;

		sql = "SELECT TeamInfoSmilie FROM "+getTableName()+" WHERE SpielerID = " + spielerId;
		rs = adapter.executeQuery(sql);

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
	
	void saveManuellerSmilie(int spielerId, String smilie) {
		String statement = null;

		//        String[]                awhereS     =   { "SpielerID" };
		//        String[]                awhereV     =   { "" + spielerId };
		if (spielerId > 0) {
			//erst UPdate versuchen
			try {
				statement = "UPDATE "+getTableName()+" SET ManuellerSmilie='" + smilie + "' WHERE SpielerID = " + spielerId;

				//Insert falls kein passender Eintrag gefunden wurde
				if (adapter.executeUpdate(statement) < 1) {
					//erst Vorhandene Aufstellung löschen
					//deleteSpielerNotizTabelle( awhereS, awhereV );
					//insert vorbereiten
					createEntry(spielerId,"","",smilie,true,(byte) -1);
				}
			} catch (Exception e) {
				createEntry(spielerId,"","",smilie,true,(byte) -1);
			}
		}
	}

	void saveSpielerNotiz(int spielerId, String notiz) {
		String statement = null;

		if (spielerId > 0) {
			//erst UPdate versuchen
			try {
				statement = "UPDATE " + getTableName() + " SET Notiz='" + core.db.DBManager.insertEscapeSequences(notiz) + "' WHERE SpielerID = " + spielerId;

				//Insert falls kein passender Eintrag gefunden wurde
				if (adapter.executeUpdate(statement) < 1) {
					//erst Vorhandene Aufstellung löschen
					//deleteSpielerNotizTabelle( awhereS, awhereV );
					//insert vorbereiten
					createEntry(spielerId,notiz,"","",true,(byte) -1);					
				}
			} catch (Exception e) {
				createEntry(spielerId,notiz,"","",true,(byte) -1);
			}
		}
	}
	
	void saveSpielerSpielberechtigt(int spielerId, boolean spielberechtigt) {
		String statement = null;

		//       String[]                awhereS     =   { "SpielerID" };
		//       String[]                awhereV     =   { "" + spielerId };
		if (spielerId > 0) {
			try {
				//erst UPdate versuchen
				statement = "UPDATE " + getTableName() + " SET Spielberechtigt=" + spielberechtigt + " WHERE SpielerID = " + spielerId;

				//Insert falls kein passender Eintrag gefunden wurde
				if (adapter.executeUpdate(statement) < 1) {
					//erst Vorhandene Aufstellung löschen
					//deleteSpielerNotizTabelle( awhereS, awhereV );
					//insert vorbereiten
					createEntry(spielerId,"","","",spielberechtigt,(byte) -1);
				}
			} catch (Exception e) {
				createEntry(spielerId,"","","",spielberechtigt,(byte) -1);
			}
		}
	}

	void saveSpielerUserPosFlag(int spielerId, byte flag) {
		String statement = null;

		if (spielerId > 0) {
			//erst UPdate versuchen
			try {
				statement = "UPDATE " + getTableName() + " SET userPos=" + flag + " WHERE SpielerID = " + spielerId;

				//Insert falls kein passender Eintrag gefunden wurde
				if (adapter.executeUpdate(statement) < 1) {
					//erst Vorhandene Aufstellung löschen
					//deleteSpielerNotizTabelle( awhereS, awhereV );
					//insert vorbereiten
					createEntry(spielerId,"","","",true,flag);					
				}
			} catch (Exception e) {
			}
		}
	}

	void saveTeamInfoSmilie(int spielerId, String smilie) {
		String statement = null;

		if (spielerId > 0) {
			//erst UPdate versuchen
			try {
				statement = "UPDATE " + getTableName() + " SET TeamInfoSmilie='" + smilie + "' WHERE SpielerID = " + spielerId;

				//Insert falls kein passender Eintrag gefunden wurde
				if (adapter.executeUpdate(statement) < 1) {
					//erst Vorhandene Aufstellung löschen
					//deleteSpielerNotizTabelle( awhereS, awhereV );
					//insert vorbereiten
					createEntry(spielerId,"",smilie,"",true,(byte) -1);
				}
			} catch (Exception e) {
				createEntry(spielerId,"",smilie,"",true,(byte) -1);				
			}
		}
	}
	
	private void createEntry(int spielerId, String notiz, String teamSmilie, String manualSmilie, boolean spielberechtigt, byte flag) {
		String statement = "INSERT INTO " + getTableName() + " ( SpielerID, Notiz, TeamInfoSmilie, ManuellerSmilie, Spielberechtigt, userPos ) VALUES(";
		statement += ("" + spielerId + ",'"+core.db.DBManager.insertEscapeSequences(notiz)+"' ," + "'" + teamSmilie + "'," + "'" + manualSmilie + "',"+spielberechtigt+","+ flag +")");
		adapter.executeUpdate(statement);
	}
	
	@Override
	protected String[] getCreateIndizeStatements() {
		return new String[] {
			"CREATE INDEX ISPIELERNOTIZ_1 ON " + getTableName() + "(" + columns[0].getColumnName() + ")"};
	}
	
	
}
