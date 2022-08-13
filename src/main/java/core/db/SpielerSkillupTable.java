package core.db;

import core.constants.player.PlayerSkill;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.*;
import java.util.*;

final class SpielerSkillupTable extends AbstractTable {

	/**
	 * tablename
	 **/
	final static String TABLENAME = "SPIELERSKILLUP";
	private static Map<String, Vector<Object[]>> playerSkillup = null;

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
		return new String[]{
				"CREATE INDEX iSkillup_1 ON " + getTableName() + "(" + columns[2].getColumnName() + ")",
				"CREATE INDEX iSkillup_2 ON " + getTableName() + "(" + columns[2].getColumnName() + "," + columns[3].getColumnName() + ")"};
	}

	@Override
	protected PreparedStatement createDeleteStatement() {
		return createDeleteStatement("WHERE HORF_ID=? AND SpielerID=? AND Skill=?");
	}

	private void storeSkillup(int hrfId, int spielerId, Timestamp date, int skillValue, int skillCode, boolean reload) {
		executePreparedDelete(hrfId, spielerId, skillCode);
		executePreparedInsert(
				hrfId,
				date,
				spielerId,
				skillCode,
				skillValue
		);

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
		return new Object[]{HODateTime.now(), Boolean.FALSE};
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
		if (playerSkillup == null) {
			populate();
		}
		Vector<Object[]> v = playerSkillup.computeIfAbsent("" + spielerId, k -> new Vector<Object[]>());
		return v;
	}

	private void populate() {

		Vector<Integer> idVector = getPlayerList();
		if (idVector.size() == 0) {
			importFromSpieler();
			idVector = getPlayerList();
		}
		playerSkillup = new HashMap<>();
		for (Integer element : idVector) {
			playerSkillup.put("" + element, loadSpieler(element));
		}

	}

	private PreparedStatement getPlayerListStatement;

	private PreparedStatement getGetPlayerListStatement() {
		if (getPlayerListStatement == null) {
			getPlayerListStatement = adapter.createPreparedStatement("SELECT DISTINCT SpielerID FROM " + getTableName());
		}
		return getPlayerListStatement;
	}

	private Vector<Integer> getPlayerList() {
		Vector<Integer> idVector = new Vector<>();

		try {
			var rs = adapter.executePreparedQuery(getGetPlayerListStatement());
			if (rs != null) {
				rs.beforeFirst();
				while (rs.next()) {
					idVector.add(rs.getInt("SpielerID"));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getPlayer: " + e);
		}
		return idVector;
	}

	@Override
	protected PreparedStatement createSelectStatement() {
		return createSelectStatement(" WHERE SpielerID=? Order By Datum DESC");
	}

	private Vector<Object[]> loadSpieler(int spielerId) {
		Vector<Object[]> v = new Vector<>();
		try {
			ResultSet rs = executePreparedSelect(spielerId);
			assert rs != null;
			rs.beforeFirst();
			while (rs.next()) {
				v.add(new Object[]{
						rs.getInt("HRF_ID"),
						spielerId,
						HODateTime.fromDbTimestamp(rs.getTimestamp("datum")),
						rs.getInt("Value"),
						rs.getInt("Skill")});
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
		return v;
	}

	// -------------------------------- Importing PArt ----------------------------------------------

	void importNewSkillup(HOModel homodel) {
		List<Player> players = homodel.getCurrentPlayers();
		for (Player nPlayer : players) {
			Player oPlayer = HOVerwaltung.instance().getModel().getCurrentPlayer(nPlayer.getPlayerID());
			if (oPlayer != null) {
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
		if (newValue > oldValue) {
			storeSkillup(hrf, nPlayer.getPlayerID(), nPlayer.getHrfDate().toDbTimestamp(), newValue, skill, true);
		}

	}

	private PreparedStatement importFromSpielerStatement;

	private PreparedStatement getImportFromSpielerStatement() {
		if (importFromSpielerStatement == null) {
			importFromSpielerStatement = adapter.createPreparedStatement("SELECT DISTINCT SpielerID FROM SPIELER");
		}
		return importFromSpielerStatement;
	}

	void importFromSpieler() {
		playerSkillup = null;
		final Vector<Integer> idVector = new Vector<>();
		try {
			var rs = adapter.executePreparedQuery(getImportFromSpielerStatement());
			if (rs != null) {
				rs.beforeFirst();
				while (rs.next()) {
					idVector.add(rs.getInt("SpielerID"));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getPlayer: " + e);
		}
		adapter._executeUpdate("DELETE FROM " + getTableName());
		for (Integer element : idVector) {
			importSpieler(element);
		}
	}

	private int[] skills = {
			PlayerSkill.PLAYMAKING,
			PlayerSkill.STAMINA,
			PlayerSkill.DEFENDING,
			PlayerSkill.KEEPER,
			PlayerSkill.WINGER,
			PlayerSkill.SCORING,
			PlayerSkill.PASSING,
			PlayerSkill.SET_PIECES,
			PlayerSkill.EXPERIENCE
	};

	private void importSpieler(int spielerId) {
		var playerHistory = DBManager.instance().loadPlayerHistory(spielerId);
		try {
			for (var skill : skills) {
				int lastValue = -1;
				for (var player : playerHistory) {
					var value = player.getValue4Skill(skill);
					if (value > lastValue && lastValue >= 0) {
						storeSkillup(player.getHrfId(), player.getPlayerID(), player.getHrfDate().toDbTimestamp(), value, skill, false);
					}
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}
}