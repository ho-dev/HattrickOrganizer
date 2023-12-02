package core.db;

import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.vo.PlayerInfo;
import java.sql.Types;

/**
 * The Table UserConfiguration contain all User properties. CONFIG_KEY = Primary
 * Key, fieldname of the class CONFIG_VALUE = value of the field, save as
 * VARCHAR. Convert to right datatype if loaded
 * 
 * @since 1.36
 * 
 */
final class TAPlayerTable extends AbstractTable {
	final static String TABLENAME = "TA_PLAYER";

	TAPlayerTable(ConnectionManager adapter) {
		super(TABLENAME, adapter);
		idColumns = 2;
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("PLAYERID").setGetter((p) -> ((PlayerInfo) p).getPlayerId()).setSetter((p, v) -> ((PlayerInfo) p).setPlayerId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("WEEK").setGetter((p) -> ((PlayerInfo) p).getWeek()).setSetter((p, v) -> ((PlayerInfo) p).setWeek((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TEAMID").setGetter((p) -> ((PlayerInfo) p).getTeamId()).setSetter((p, v) -> ((PlayerInfo) p).setTeamId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("STATUS").setGetter((p) -> ((PlayerInfo) p).getStatus()).setSetter((p, v) -> ((PlayerInfo) p).setStatus((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SPECIALEVENT").setGetter((p) -> ((PlayerInfo) p).getSpecialEvent()).setSetter((p, v) -> ((PlayerInfo) p).setSpecialEvent((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TSI").setGetter((p) -> ((PlayerInfo) p).getTSI()).setSetter((p, v) -> ((PlayerInfo) p).setTSI((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("FORM").setGetter((p) -> ((PlayerInfo) p).getForm()).setSetter((p, v) -> ((PlayerInfo) p).setForm((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AGE").setGetter((p) -> ((PlayerInfo) p).getAge()).setSetter((p, v) -> ((PlayerInfo) p).setAge((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("EXPERIENCE").setGetter((p) -> ((PlayerInfo) p).getExperience()).setSetter((p, v) -> ((PlayerInfo) p).setExperience((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SALARY").setGetter((p) -> ((PlayerInfo) p).getSalary()).setSetter((p, v) -> ((PlayerInfo) p).setSalary((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("STAMINA").setGetter((p) -> ((PlayerInfo) p).getStamina()).setSetter((p, v) -> ((PlayerInfo) p).setStamina((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MOTHERCLUBBONUS").setGetter((p) -> ((PlayerInfo) p).getMotherClubBonus()).setSetter((p, v) -> ((PlayerInfo) p).setMotherClubBonus((Boolean) v)).setType(Types.BOOLEAN).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LOYALTY").setGetter((p) -> ((PlayerInfo) p).getLoyalty()).setSetter((p, v) -> ((PlayerInfo) p).setLoyalty((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("NAME").setGetter((p) -> ((PlayerInfo) p).getName()).setSetter((p, v) -> ((PlayerInfo) p).setName((String) v)).setType(Types.VARCHAR).setLength(100).isNullable(true).build()
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] { "CREATE INDEX ITA_PLAYER_PLAYERID_WEEK ON " + TABLENAME
				+ " (playerid, week)" };
	}

	PlayerInfo getPlayerInfo(int playerId, int week, int season) {
		var ret =  loadOne(PlayerInfo.class, playerId, PlayerDataManager.calcWeekNumber(season, week));
		if ( ret == null ) ret = new PlayerInfo();
		return ret;
	}

	private final String loadLatestPlayerInfoSql = createSelectStatement(" WHERE PLAYERID = ? ORDER BY WEEK DESC LIMIT 1");

	/**
	 * Returns the specialEvent code for a player
	 * 
	 * @param playerId
	 *            the playerId
	 * 
	 * @return a numeric code
	 */
	PlayerInfo getLatestPlayerInfo(int playerId) {
		var ret = loadOne(PlayerInfo.class, this.connectionManager.executePreparedQuery(loadLatestPlayerInfoSql, playerId));
		if ( ret == null ) ret = new PlayerInfo();
		return ret;
	}

	/**
	 * Add a player to a team
	 * 
	 * @param info PlayerInfo
	 */
	void storePlayer(PlayerInfo info) {
		var week = PlayerDataManager.calcCurrentWeekNumber();
		info.setIsStored(isStored(info.getPlayerId(), week));
		info.setWeek(week);
		store(info);
	}
}
