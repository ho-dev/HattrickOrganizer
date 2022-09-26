package core.db;

import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.vo.PlayerInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
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

	TAPlayerTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[14];
		columns[0] = new ColumnDescriptor("TEAMID", Types.INTEGER, false);
		columns[1] = new ColumnDescriptor("PLAYERID", Types.INTEGER, true);
		columns[2] = new ColumnDescriptor("STATUS", Types.INTEGER, true);
		columns[3] = new ColumnDescriptor("SPECIALEVENT", Types.INTEGER, true);
		columns[4] = new ColumnDescriptor("TSI", Types.INTEGER, true);
		columns[5] = new ColumnDescriptor("FORM", Types.INTEGER, true);
		columns[6] = new ColumnDescriptor("AGE", Types.INTEGER, true);
		columns[7] = new ColumnDescriptor("EXPERIENCE", Types.INTEGER, true);
		columns[8] = new ColumnDescriptor("WEEK", Types.INTEGER, true);
		columns[9] = new ColumnDescriptor("SALARY", Types.INTEGER, true);
		columns[10] = new ColumnDescriptor("STAMINA", Types.INTEGER, true);
		columns[11] = new ColumnDescriptor("MOTHERCLUBBONUS", Types.BOOLEAN, true);
		columns[12] = new ColumnDescriptor("LOYALTY", Types.INTEGER, true);
		columns[13] = new ColumnDescriptor("NAME", Types.VARCHAR, true, 100);
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] { "CREATE INDEX ITA_PLAYER_PLAYERID_WEEK ON " + TABLENAME
				+ " (playerid, week)" };
	}

	/**
	 * Calculate a number from current season and week numbers
	 * @return number
	 */
	public int calcCurrentWeekNumber(){
		return calcWeekNumber(PlayerDataManager.getCurrentHTSeason(), PlayerDataManager.getCurrentHTWeek());
	}

	/**
	 * Calculate a number from season and week numbers
	 * @param season season number [1..]
	 * @param week week number [1..16]
	 * @return number
	 */
	private int calcWeekNumber(int season, int week) {
		return season*16 + week - 1;
	}

	/**
	 * Get number of week from weekNumber
	 * @param weekNumber int
	 * @return number of week [1..16]
	 */
	private int getWeekFromWeekNumber(int weekNumber){
		return weekNumber%16 + 1;
	}

	/**
	 * Get number of season from weekNumber
	 * @param weekNumber int
	 * @return number of season [1..]
	 */
	private int getSeasonFromWeekNumber(int weekNumber){
		return weekNumber/16;
	}

	@Override
	protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder(){
		return new PreparedSelectStatementBuilder(this, " where PLAYERID=? and week=?");
	}
	PlayerInfo getPlayerInfo(int playerId, int week, int season) {
		ResultSet rs = executePreparedSelect(playerId, calcWeekNumber(season, week));
		try {
			if (rs.next()) {
				PlayerInfo info = new PlayerInfo();
				info.setPlayerId(playerId);
				info.setAge(rs.getInt("AGE"));
				info.setForm(rs.getInt("FORM"));
				info.setTSI(rs.getInt("TSI"));
				info.setSpecialEvent(rs.getInt("SPECIALEVENT"));
				info.setTeamId(rs.getInt("TEAMID"));
				info.setExperience(rs.getInt("EXPERIENCE"));
				info.setStatus(rs.getInt("STATUS"));
				info.setSalary(rs.getInt("SALARY"));
				info.setStamina(rs.getInt("STAMINA"));
				info.setMotherClubBonus(rs.getBoolean("MOTHERCLUBBONUS"));
				info.setMotherClubBonus(rs.getBoolean("LOYALTY"));
				info.setName(rs.getString("NAME"));
				return info;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return new PlayerInfo();
	}


	DBManager.PreparedStatementBuilder getLatestPlayerInfoBuilder = new DBManager.PreparedStatementBuilder(
			"SELECT max(WEEK) FROM " + TABLENAME + " WHERE PLAYERID=? AND WEEK<=?" );

	/**
	 * Returns the specialEvent code for a player
	 * 
	 * @param playerId
	 *            the playerId
	 * 
	 * @return a numeric code
	 */
	PlayerInfo getLatestPlayerInfo(int playerId) {
		ResultSet rs = this.adapter.executePreparedQuery(getLatestPlayerInfoBuilder.getStatement(), playerId, calcCurrentWeekNumber()); // because of an error corrupt numbers may be in the database

		try {
			if (rs.next()) {
				int week = rs.getInt(1);
				return getPlayerInfo(playerId, getWeekFromWeekNumber(week), getSeasonFromWeekNumber(week));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return new PlayerInfo();
	}

	/**
	 * Add a player to a team
	 * 
	 * @param info PlayerInfo
	 */
	void addPlayer(PlayerInfo info) {
		executePreparedInsert(
				info.getTeamId(),
				info.getPlayerId(),
				info.getStatus(),
				info.getSpecialEvent(),
				info.getTSI(),
				info.getForm(),
				info.getAge(),
				info.getExperience(),
				calcCurrentWeekNumber(),
				info.getSalary(),
				info.getStamina(),
				info.getMotherClubBonus(),
				info.getLoyalty(),
				info.getName()
		);
	}

	@Override
	protected PreparedUpdateStatementBuilder createPreparedUpdateStatementBuilder(){
		return new PreparedUpdateStatementBuilder(this,
				" set SPECIALEVENT=?, TSI=?, FORM=?, AGE=?, EXPERIENCE=?, STATUS=?, SALARY=?, STAMINA=?" +
				", MOTHERCLUBBONUS=?, LOYALTY =?, NAME =? where PLAYERID=? and WEEK=?");
	}
	void updatePlayer(PlayerInfo info) {
		executePreparedUpdate(
				info.getSpecialEvent(),
				info.getTSI(),
				info.getForm(),
				info.getAge(),
				info.getExperience(),
				info.getStatus(),
				info.getSalary(),
				info.getStamina(),
				info.getMotherClubBonus(),
				info.getLoyalty(),
				info.getName(),
				info.getPlayerId(),
				calcCurrentWeekNumber());
	}

}
