package core.db;

import core.constants.player.PlayerSkill;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

final class SpielerSkillupTable extends AbstractTable {

	/** tablename **/						
	final static String TABLENAME = "SPIELERSKILLUP";
	private static Map<String,Vector<Object[]>> playerSkillup = null;

	SpielerSkillupTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);					
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[5];
		columns[0] = new ColumnDescriptor("HRF_ID", Types.INTEGER, false);
		columns[1] = new ColumnDescriptor("Datum", Types.TIMESTAMP, false);
		columns[2] = new ColumnDescriptor("SpielerID", Types.INTEGER, false);
		columns[3] = new ColumnDescriptor("Skill", Types.INTEGER, false);
		columns[4] = new ColumnDescriptor("Value", Types.INTEGER, false);

	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
			"CREATE INDEX iSkillup_1 ON " + getTableName() + "(" + columns[2].getColumnName() + ")",
			"CREATE INDEX iSkillup_2 ON " + getTableName() + "(" + columns[2].getColumnName() + "," + columns[3].getColumnName() + ")"};
	}

	private void storeSkillup(int hrfId, int spielerId, Timestamp date, int skillValue, int skillCode, boolean reload) {
		String statement;

		//erst Vorhandene Aufstellung löschen
		final String[] awhereS = { "HRF_ID", "SpielerID", "Skill" };
		final String[] awhereV = { "" + hrfId, "" + spielerId, "" +skillCode};

		delete(awhereS, awhereV);

		//insert vorbereiten
		statement =
			"INSERT INTO "+getTableName()+" ( HRF_ID , Datum , SpielerID , Skill , Value ) VALUES(";
		statement
			+= (""
				+ hrfId
				+ ",'"
				+ date
				+ "', "
				+ spielerId
				+ ","
				+ skillCode
				+ ","
				+ skillValue
				+ " )");
		adapter.executeUpdate(statement);
		if (reload) {
			Vector<Object[]> data = getSpielerSkillUp(spielerId);
			data.clear();
			data.addAll(loadSpieler(spielerId));						
		}				
	}

	Object[] getLastLevelUp(int skillCode, int spielerId) {
		Vector<Object[]> data = getSpielerSkillUp(spielerId);
		for (Object[] element : data) {
			int code = (Integer) element[4];
			if (code == skillCode) {
				return new Object[]{element[2], Boolean.TRUE};
			}
		}
		return new Object[] { HODateTime.now(), Boolean.FALSE};
	}

	Vector<Object[]> getAllLevelUp(int skillCode, int spielerId) {		
		Vector<Object[]> data = getSpielerSkillUp(spielerId);
		Vector<Object[]> v = new Vector<Object[]>();
		for (Object[] element : data) {
			int code = (Integer) element[4];
			if (code == skillCode) {
				v.add(new Object[]{element[2], Boolean.TRUE, element[3]});
			}
		}
		return v;
	}

	private Vector<Object[]> getSpielerSkillUp(int spielerId) {
		if (playerSkillup==null) {
			populate();
		}
		Vector<Object[]> v = playerSkillup.computeIfAbsent("" + spielerId, k -> new Vector<Object[]>());
		return v;
	}
	
	private void populate() {

		Vector<Integer> idVector = getPlayerList();
		if (idVector.size()==0) {
			importFromSpieler();
			idVector = getPlayerList();			
		}  
		playerSkillup = new HashMap<>();
		for (Integer element : idVector) {
			playerSkillup.put("" + element, loadSpieler(element));
		}
		
	}

	private Vector<Integer> getPlayerList() {
		Vector<Integer> idVector = new Vector<>();
		ResultSet rs;
		String sql = "SELECT DISTINCT SpielerID FROM "+getTableName();
		try {
			rs = adapter.executeQuery(sql);			
			if (rs != null) {
				rs.beforeFirst();
				while (rs.next()) {
					idVector.add(rs.getInt("SpielerID"));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),e);
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getPlayer: " + e);
		}
		return idVector;
	}

	private Vector<Object[]> loadSpieler(int spielerId) {		
		Vector<Object[]> v = new Vector<>();
		try {
			String sql = "SELECT * FROM "+getTableName()+" WHERE SpielerID=" + spielerId + " Order By Datum DESC";
			ResultSet rs = adapter.executeQuery(sql);
			assert rs != null;
			rs.beforeFirst();
			while (rs.next()) {
				v.add(new Object[] {
						rs.getInt("HRF_ID"),
						spielerId,
						HODateTime.fromDbTimestamp(rs.getTimestamp("datum")),
						rs.getInt("Value"),
						rs.getInt("Skill")});
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),e);
		}
		return v;
	}

	// -------------------------------- Importing PArt ----------------------------------------------

	void importNewSkillup(HOModel homodel) {
		List<Player> players = homodel.getCurrentPlayers();
		for ( Player nPlayer : players){
			Player oPlayer = HOVerwaltung.instance().getModel().getCurrentPlayer(nPlayer.getPlayerID());
			if (oPlayer!=null) {
				checkNewSkillup(nPlayer, nPlayer.getGKskill(), oPlayer.getGKskill(), PlayerSkill.KEEPER, homodel.getID());
				checkNewSkillup(nPlayer, nPlayer.getPMskill(), oPlayer.getPMskill(), PlayerSkill.PLAYMAKING, homodel.getID());
				checkNewSkillup(nPlayer, nPlayer.getPSskill(), oPlayer.getPSskill(), PlayerSkill.PASSING, homodel.getID());
				checkNewSkillup(nPlayer, nPlayer.getWIskill(), oPlayer.getWIskill(), PlayerSkill.WINGER, homodel.getID());
				checkNewSkillup(nPlayer, nPlayer.getDEFskill(), oPlayer.getDEFskill(), PlayerSkill.DEFENDING, homodel.getID());
				checkNewSkillup(nPlayer, nPlayer.getSCskill(), oPlayer.getSCskill(), PlayerSkill.SCORING, homodel.getID());
				checkNewSkillup(nPlayer, nPlayer.getSPskill(), oPlayer.getSPskill(), PlayerSkill.SET_PIECES, homodel.getID());
				checkNewSkillup(nPlayer, nPlayer.getStamina(), oPlayer.getStamina(), PlayerSkill.STAMINA, homodel.getID());
				checkNewSkillup(nPlayer, nPlayer.getExperience(), oPlayer.getExperience(), PlayerSkill.EXPERIENCE, homodel.getID());
			}
		}
	}
		
	private void checkNewSkillup(Player nPlayer, int newValue, int oldValue, int skill, int hrf) {
		if (newValue>oldValue) {
			storeSkillup(hrf,nPlayer.getPlayerID(),nPlayer.getHrfDate().toDbTimestamp(),newValue,skill,true);
		}
		
	}

	void importFromSpieler() {
		playerSkillup = null;
		ResultSet rs;
		String sql = "SELECT DISTINCT SpielerID FROM SPIELER";
		final Vector<Integer> idVector = new Vector<>();
		try {
			rs = adapter.executeQuery(sql);			
			if (rs != null) {
				rs.beforeFirst();
				while (rs.next()) {
					idVector.add(rs.getInt("SpielerID"));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),e);
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getPlayer: " + e);
		}	
		adapter.executeUpdate("DELETE FROM "+getTableName());
		for (Integer element : idVector) {
			importSpieler(element);
		}
	}

	private void importSpieler(int spielerId) {	
		importSkillUp(PlayerSkill.PLAYMAKING,spielerId);
		importSkillUp(PlayerSkill.STAMINA,spielerId);
		importSkillUp(PlayerSkill.DEFENDING,spielerId);
		importSkillUp(PlayerSkill.KEEPER,spielerId);
		importSkillUp(PlayerSkill.WINGER,spielerId);
		importSkillUp(PlayerSkill.SCORING,spielerId);
		importSkillUp(PlayerSkill.PASSING,spielerId);
		importSkillUp(PlayerSkill.SET_PIECES,spielerId);
		importSkillUp(PlayerSkill.EXPERIENCE,spielerId);
	}
	
	private void importSkillUp(int skillCode, int spielerId) {
		try {	
			String key = getKey(skillCode);				
			String sql = "SELECT HRF_ID, datum, " + key + " FROM SPIELER WHERE SpielerID=" + spielerId + " Order By Datum ASC";
			ResultSet rs = adapter.executeQuery(sql);
			assert rs != null;
			rs.beforeFirst();
			int lastValue = -1;
			if (rs.next()) {
				lastValue = rs.getInt(key);
			}
			Vector<Object[]> v = new Vector<>();
			while (rs.next()) {
				int value = rs.getInt(key);
				if (value > lastValue) {
					v.add(new Object[] {rs.getInt("HRF_ID"), spielerId,rs.getTimestamp("datum"), value, skillCode});
				}
				lastValue = value;
			}
			for (Iterator<Object[]> iter = v.iterator(); iter.hasNext();) {
				Object[] element = iter.next();
				storeSkillup((Integer) element[0], (Integer) element[1],((Timestamp)element[2]), (Integer) element[3], (Integer) element[4],false);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),e);
		}
	}

	private String getKey(int code) {
		String key = switch (code) {
			case PlayerSkill.SET_PIECES -> "Standards";
			case PlayerSkill.PASSING -> "Passpiel";
			case PlayerSkill.SCORING -> "Torschuss";
			case PlayerSkill.PLAYMAKING -> "Spielaufbau";
			case PlayerSkill.WINGER -> "Fluegel";
			case PlayerSkill.KEEPER -> "Torwart";
			case PlayerSkill.DEFENDING -> "Verteidigung";
			case PlayerSkill.STAMINA -> "Kondition";
			case PlayerSkill.EXPERIENCE -> "Erfahrung";
			default -> "Spielaufbau";
		};

		return key;
 
	}

}
