package core.db;

import core.model.HOVerwaltung;
import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.enums.MatchTypeExtended;
import core.model.match.MatchKurzInfo;
import core.model.enums.MatchType;
import core.model.match.Weather;
import core.util.HODateTime;
import core.util.HOLogger;
import module.matches.MatchLocation;
import module.matches.MatchesPanel;
import module.matches.statistics.MatchesOverviewCommonPanel;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * Class used to store in DB, [Matches] Table fetched via CHPP
 */
final class MatchesKurzInfoTable extends AbstractTable {
	final static String TABLENAME = "MATCHESKURZINFO";

	protected MatchesKurzInfoTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				new ColumnDescriptor("MatchID", Types.INTEGER, false), //The globally unique identifier of the match
				new ColumnDescriptor("MatchTyp", Types.INTEGER, false), //Integer defining the type of match
				new ColumnDescriptor("HeimName", Types.VARCHAR, false, 256), // HomeTeamName
				new ColumnDescriptor("HeimID", Types.INTEGER, false),  //HomeTeamID
				new ColumnDescriptor("GastName", Types.VARCHAR, false, 256), // AwayTeamName
				new ColumnDescriptor("GastID", Types.INTEGER, false),  //AwayTeamID
				new ColumnDescriptor("MatchDate", Types.VARCHAR, false, 256), // The start date and time (kick-off) of the match.
				new ColumnDescriptor("HeimTore", Types.INTEGER, false), // The current number of goals in the match for the home team.
				new ColumnDescriptor("GastTore", Types.INTEGER, false), // The current number of goals in the match for the away team
				new ColumnDescriptor("Aufstellung", Types.BOOLEAN, false), // List of boolean value only supplied for upcoming matches of your own team that signifies whether you have given orders or not
				new ColumnDescriptor("Status", Types.INTEGER, false), // Specifying whether the match is FINISHED, ONGOING or UPCOMING
				new ColumnDescriptor("CupLevel", Types.INTEGER, false), // 1 = National/Divisional cup, 2 = Challenger cup, 3 = Consolation cup. 0 if MatchType is not 3
				new ColumnDescriptor("CupLevelIndex", Types.INTEGER, false), // In Challenger cups: 1 = Emerald (start week 2), 2 = Ruby (start week 3), 3 = Sapphire (start week 4). Always 1 for National/Divisional (main cups) and Consolation cups. 0 if MatchType is not 3.
				new ColumnDescriptor("MatchContextId", Types.INTEGER, true), // This will be either LeagueLevelUnitId (for League), CupId (Cup, Hattrick Masters, World Cup and U-20 World Cup), LadderId, TournamentId, or 0 for friendly, qualification, single matches and preparation matches.
				new ColumnDescriptor("TournamentTypeID", Types.INTEGER, true), // 3 = League with playoffs , 4 = Cup
				new ColumnDescriptor("ArenaId", Types.INTEGER, true),//  arena id
				new ColumnDescriptor("RegionId", Types.INTEGER, true), // region id
				new ColumnDescriptor("isDerby", Types.BOOLEAN, true), //
				new ColumnDescriptor("isNeutral", Types.BOOLEAN, true), // 0=false, 1=true, -1=unknown
				new ColumnDescriptor("Weather", Types.INTEGER, true), // 0=rainy, ...
				new ColumnDescriptor("WeatherForecast", Types.INTEGER, true), // 0=happened, ...
				new ColumnDescriptor("Duration", Types.INTEGER, true), // match duration in minutes
				new ColumnDescriptor("isObsolete", Types.BOOLEAN, true)
		};
	}

	@Override
	protected String[] getConstraintStatements() {
		return new String[] {" PRIMARY KEY (MATCHID, MATCHTYP)"};
	}

    @Override
    protected String[] getCreateIndexStatement() {
        return new String[]{
                "CREATE INDEX IMATCHKURZINFO_1 ON " + getTableName() + "(" + columns[0].getColumnName() + ")",
                "CREATE INDEX matchkurzinfo_heimid_idx ON "+ getTableName() + "(" + columns[3].getColumnName() + ")",
                "CREATE INDEX matchkurzinfo_gastid_idx ON "+ getTableName() + "(" + columns[5].getColumnName() + ")",
        };
    }

	/**
	 * Saves matches into storeMatchKurzInfo table
	 */
	void storeMatchKurzInfos(List<MatchKurzInfo> matches) {
		if ( matches == null)return;
		final String[] where = { "MATCHTYP", "MatchID"  };
		final String[] werte = new String[2];

		for ( var match : matches){

			werte[0] = "" + match.getMatchType().getId();
			werte[1] = "" + match.getMatchID();
			delete(where, werte);

			try {
				var sql =  createInsertStatement();
				adapter.executePreparedUpdate(sql,
						match.getMatchID(),
						match.getMatchType().getId(),
						match.getHomeTeamName(),
						match.getHomeTeamID(),
						match.getGuestTeamName(),
						match.getGuestTeamID(),
						match.getMatchSchedule().toDbTimestamp(),
						match.getHomeTeamGoals(),
						match.getGuestTeamGoals(),
						match.isOrdersGiven(),
						match.getMatchStatus(),
						match.getCupLevel().getId(),
						match.getCupLevelIndex().getId(),
						match.getMatchContextId(),
						match.getTournamentTypeID(),
						match.getArenaId(),
						match.getRegionId(),
						match.getIsDerby(),
						match.getIsNeutral(),
						match.getWeather().getId(),
						match.getWeatherForecast().getId(),
						match.getDuration(),
						match.isObsolet()
						);
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),
						"DB.storeMatchKurzInfos Error" + e);
				HOLogger.instance().log(getClass(), e);
			}
		}
	}

	MatchKurzInfo getMatchesKurzInfo(int teamId, int matchtyp, int statistic,
			boolean home) {

		MatchKurzInfo match = null;
		StringBuilder sql = new StringBuilder(200);
		String column = "";
		String column2 = "";
		try {
			switch (statistic) {
				case MatchesOverviewCommonPanel.HighestVictory -> {
					column = home ? "(HEIMTORE-GASTTORE) AS DIFF "
							: "(GASTTORE-HEIMTORE) AS DIFF ";
					column2 = home ? ">" : "<";
				}
				case MatchesOverviewCommonPanel.HighestDefeat -> {
					column = home ? "(GASTTORE-HEIMTORE) AS DIFF "
							: "(HEIMTORE-GASTTORE) AS DIFF ";
					column2 = home ? "<" : ">";
				}
			}
			sql.append("SELECT ").append(getTableName()).append(".*,");
			sql.append(column);
			sql.append(" FROM ").append(getTableName());
			sql.append(" WHERE ").append(home ? "HEIMID" : "GASTID")
					.append(" = ?");
			//sql.append(teamId);
			sql.append(" AND HEIMTORE ").append(column2).append(" GASTTORE ");
			sql.append(getMatchTypWhereClause(matchtyp));

			sql.append(" ORDER BY DIFF DESC ");
			var rs = adapter.executePreparedQuery(sql.toString(), teamId);

			assert rs != null;
			rs.beforeFirst();

			if (rs.next()) {
				match = createMatchKurzInfo(rs);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DB.getMatchesKurzInfo Error" + e);
		}
		return match;
	}

	/**
	 * Return the list of n latest played matches (own team)
	 */
	ArrayList<MatchKurzInfo> getPlayedMatchInfo(@Nullable Integer iNbGames, boolean bOfficialOnly, boolean ownTeam) {
		final ArrayList<MatchKurzInfo> playedMatches = new ArrayList<>();

		StringBuilder sql = new StringBuilder(100);
		ResultSet rs;

		sql.append("SELECT * FROM ").append(getTableName()).append(" WHERE Status=" + MatchKurzInfo.FINISHED);

		if(ownTeam) {
			sql.append(" AND ( GastID = ? OR HeimID = ?)");
		}

		if(bOfficialOnly) {
			sql.append(getMatchTypWhereClause(MatchTypeExtended.GROUP_OFFICIAL));
		}

		sql.append(" ORDER BY MatchDate DESC");

		if(iNbGames != null) {
			sql.append(" LIMIT ").append(iNbGames);
		}

		try{
			if ( ownTeam) {
				final int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
				rs = adapter.executePreparedQuery(sql.toString(), teamId, teamId);
			}
			else {
				rs = adapter.executePreparedQuery(sql.toString());
			}

			assert rs != null;
			rs.beforeFirst();

			while (rs.next()) {
				playedMatches.add(createMatchKurzInfo(rs));
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.getMatchesKurzInfo Error" + e);
		}

		return playedMatches;
	}

	/**
	 * Important: If the teamid = -1 the match type must be ALL_GAMES!
	 * @param teamId The Teamid or -1 for all
	 * @param matchtyp Which matches? Constants in the GamesPanel!
	 * @param matchLocation Home, Away, Neutral
	 * @param from filter match schedule date
	 * @param includeUpcoming if false filter finished matches only
	 * @return MatchKurzInfo[] – Array of match info.
	 */
	MatchKurzInfo[] getMatchesKurzInfo(int teamId, int matchtyp, MatchLocation matchLocation, Timestamp from, boolean includeUpcoming) {
		StringBuilder sql = new StringBuilder(100);
		ResultSet rs;
		final ArrayList<MatchKurzInfo> liste = new ArrayList<>();

		// Without TeamID ino only All_Match possible
		if ((teamId < 0) && (matchtyp != MatchesPanel.ALL_GAMES)) {
			return new MatchKurzInfo[0];
		}

		List<Object> params = new ArrayList<>();
		sql.append("SELECT * FROM ").append(getTableName());

		// filter time
		sql.append(" WHERE MatchDate>=?");
		params.add(from);

		if(matchtyp != MatchesPanel.ALL_GAMES){
			// OTHER TEAM GAMES =============================================
			if(matchtyp == MatchesPanel.OTHER_TEAM_GAMES){
				sql.append(" AND ( GastID != ? AND HeimID != ? )");
				params.add(teamId);
				params.add(teamId);
			}
			// MY GAMES =============================================
			else{
				switch (matchLocation) {
					case HOME -> {
						sql.append(" AND HeimID = ? AND (isNeutral is NULL OR isNeutral=false) ");
						params.add(teamId);
					}
					case AWAY -> {
						sql.append(" AND GastID = ? AND (is Neutral is NULL OR isNeutral=false) ");
						params.add(teamId);
					}
					case NEUTRAL -> {
						sql.append(" AND (HeimID = ? OR GastID = ?) AND (isNeutral=true) ");
						params.add(teamId);
						params.add(teamId);
					}
					case ALL -> {
						sql.append(" AND (HeimID = ? OR GastID= ?) ");
						params.add(teamId);
						params.add(teamId);
					}
				}
			}
		}

		// Filter matchType
		if (matchtyp >= 10 ) {
			matchtyp = matchtyp - 10;
			includeUpcoming = false;
		}

		if( !includeUpcoming) {
			sql.append(" AND Status=" + MatchKurzInfo.FINISHED);
		}

		sql.append(getMatchTypWhereClause(matchtyp));
		sql.append(" ORDER BY MatchDate DESC");

		try {
			rs = adapter.executePreparedQuery(sql.toString(), params.toArray());
			assert rs != null;
			rs.beforeFirst();

			while (rs.next()) {
				liste.add(createMatchKurzInfo(rs));
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DB.getMatchesKurzInfo Error" + e);
		}

		return liste.toArray(new MatchKurzInfo[0]);
	}

	MatchKurzInfo[] getMatchesKurzInfoUpComing(int teamId) {
		StringBuilder sql = new StringBuilder(100);
		ResultSet rs;
		final ArrayList<MatchKurzInfo> liste = new ArrayList<>();

		// Ohne Matchid nur AlleSpiele möglich!
		if ((teamId < 0)) {
			return new MatchKurzInfo[0];
		}

		try {
			sql.append("SELECT * FROM ").append(getTableName());
			sql.append(" WHERE ( GastID = ? OR HeimID = ? ) AND Status= ? AND MatchTyp!= ? ORDER BY MatchDate DESC");
			rs = adapter.executePreparedQuery(sql.toString(), teamId, teamId, MatchKurzInfo.UPCOMING, MatchType.LEAGUE.getId());
			assert rs != null;
			rs.beforeFirst();

			while (rs.next()) {
				liste.add(createMatchKurzInfo(rs));
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DB.getMatchesKurzInfo Error" + e);
		}

		return liste.toArray(new MatchKurzInfo[0]);
	}

	public MatchKurzInfo loadLastMatchesKurzInfo(int teamId) {
		StringBuilder sql = new StringBuilder(100);
		ResultSet rs;
		try {
			sql.append("SELECT * FROM ").append(getTableName());
			sql.append(" WHERE ( GastID = ? OR HeimID = ? ) AND Status = ? ORDER BY MatchDate DESC LIMIT 1");
			rs = adapter.executePreparedQuery(sql.toString(), teamId, teamId, MatchKurzInfo.FINISHED);
			assert rs != null;
			rs.beforeFirst();
			if (rs.next()) {
				return createMatchKurzInfo(rs);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DB.getMatchesKurzInfo Error" + e);
		}
		return null;
	}

	public MatchKurzInfo loadNextMatchesKurzInfo(int teamId) {
		StringBuilder sql = new StringBuilder(100);
		ResultSet rs;
		try {
			sql.append("SELECT * FROM ").append(getTableName());
			sql.append(" WHERE ( GastID = ? OR HeimID = ? ) AND Status=? ORDER BY MatchDate ASC LIMIT 1");
			rs = adapter.executePreparedQuery(sql.toString(), teamId, teamId, MatchKurzInfo.UPCOMING);
			assert rs != null;
			rs.beforeFirst();
			if (rs.next()) {
				return createMatchKurzInfo(rs);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DB.getMatchesKurzInfo Error" + e);
		}
		return null;
	}

	private StringBuilder getMatchTypWhereClause(MatchTypeExtended matchType) {
		StringBuilder sql = new StringBuilder(100);
		if (matchType == MatchTypeExtended.GROUP_OFFICIAL) {
			var officialTypes = MatchType.getOfficialMatchTypes();
			sql.append(" AND MatchTyp IN (");
			char sep = ' ';
			for (var t : officialTypes) {
				sql.append(sep).append(t.getId());
				sep = ',';
			}
			sql.append(" )");
		}
		return sql;
	}

	public static StringBuilder getMatchTypWhereClause(int iMatchType) {
		StringBuilder sql = new StringBuilder(100);
		switch (iMatchType) {
			case MatchesPanel.OWN_OFFICIAL_GAMES -> sql.append(" AND ( MatchTyp=").append(MatchType.QUALIFICATION.getId())
													   .append(" OR MatchTyp=").append(MatchType.LEAGUE.getId()).append(" OR MatchTyp=")
					                                   .append(MatchType.CUP.getId()).append(" )");
			case MatchesPanel.OWN_NATIONAL_CUP_GAMES -> sql.append(" AND MatchTyp = ").append(MatchType.CUP.getId())
													  .append(" AND CUPLEVEL = ").append(CupLevel.NATIONALorDIVISIONAL.getId());
			case MatchesPanel.OWN_LEAGUE_GAMES -> sql.append(" AND MatchTyp=").append(MatchType.LEAGUE.getId());
			case MatchesPanel.OWN_FRIENDLY_GAMES -> sql.append(" AND ( MatchTyp=").append(MatchType.FRIENDLYNORMAL.getId())
													.append(" OR MatchTyp=").append(MatchType.FRIENDLYCUPRULES.getId())
													.append(" OR MatchTyp=").append(MatchType.INTFRIENDLYCUPRULES.getId())
													.append(" OR MatchTyp=").append(MatchType.INTFRIENDLYNORMAL.getId()).append(" )");
			case MatchesPanel.OWN_TOURNAMENT_GAMES -> sql.append(" AND ( MatchTyp=").append(MatchType.TOURNAMENTGROUP.getId())
													.append(" OR MatchTyp=").append(MatchType.TOURNAMENTPLAYOFF.getId()).append(" )");
			case MatchesPanel.OWN_SECONDARY_CUP_GAMES -> sql.append(" AND MatchTyp = ").append(MatchType.CUP.getId())
													   .append(" AND CUPLEVEL != ").append(CupLevel.NATIONALorDIVISIONAL.getId());
			case MatchesPanel.OWN_QUALIF_GAMES -> sql.append(" AND MatchTyp=").append(MatchType.QUALIFICATION.getId());
			case MatchesPanel.OWN_CUP_GAMES -> sql.append(" AND (MatchTyp=").append(MatchType.CUP.getId()).append(")");
		}

		return sql;
	}

	private MatchKurzInfo createMatchKurzInfo(ResultSet rs) throws SQLException {
		MatchKurzInfo match = new MatchKurzInfo();
		match.setMatchSchedule(HODateTime.fromDbTimestamp(rs.getTimestamp("MatchDate")));
		match.setGuestTeamID(rs.getInt("GastID"));
		match.setGuestTeamName(DBManager.deleteEscapeSequences(rs
				.getString("GastName")));
		match.setHomeTeamID(rs.getInt("HeimID"));
		match.setHomeTeamName(DBManager.deleteEscapeSequences(rs
				.getString("HeimName")));
		match.setMatchID(rs.getInt("MatchID"));
		match.setMatchContextId(rs.getInt("MatchContextId"));
		match.setTournamentTypeID(rs.getInt("TournamentTypeID"));
		match.setGuestTeamGoals(rs.getInt("GastTore"));
		match.setHomeTeamGoals(rs.getInt("HeimTore"));
		match.setMatchType(MatchType.getById(rs.getInt("MatchTyp")));
		match.setCupLevel(CupLevel.fromInt(rs.getInt("CupLevel")));
		match.setCupLevelIndex(CupLevelIndex.fromInt(rs.getInt("CupLevelIndex")));
		match.setMatchStatus(rs.getInt("Status"));
		match.setOrdersGiven(rs.getBoolean("Aufstellung"));
		match.setArenaId(rs.getInt("ArenaId"));
		match.setRegionId(rs.getInt("RegionId"));
		match.setIsDerby(DBManager.getBoolean(rs, "isDerby"));
		match.setIsNeutral(DBManager.getBoolean(rs, "isNeutral"));
		match.setWeather(Weather.getById(DBManager.getInteger(rs,"Weather")));
		match.setWeatherForecast(Weather.Forecast.getById(DBManager.getInteger(rs,"WeatherForecast")));
		match.setDuration(DBManager.getInteger(rs, "Duration"));
		match.setisObsolet(DBManager.getBoolean(rs, "isObsolete", false));
		return match;
	}

	/**
	 * Check if a match is already in the database.
	 */
	boolean isMatchInDB(int matchid, MatchType matchType) {
		boolean vorhanden = false;

		try {
			List<Object> params = new ArrayList<>();
			String sql = "SELECT MatchId FROM " + getTableName()
					+ " WHERE MatchId= ?";
			params.add(matchid);
			if ( matchType != null){
				sql += " AND MatchTyp=?";
				params.add(matchType.getId());
			}

			final ResultSet rs = adapter.executePreparedQuery(sql,params.toArray());

			assert rs != null;
			rs.beforeFirst();

			if (rs.next()) {
				vorhanden = true;
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DatenbankZugriff.isMatchVorhanden : " + e);
		}

		return vorhanden;
	}

	boolean hasUnsureWeatherForecast(int matchId)
	{
		try{
			final String sql = "SELECT WeatherForecast FROM " + getTableName() + " WHERE MatchId=?";
			final ResultSet rs = adapter.executePreparedQuery(sql,matchId);
			assert rs != null;
			rs.beforeFirst();
			if (rs.next()) {
				Weather.Forecast forecast = Weather.Forecast.getById(rs.getInt(1));
				if (rs.wasNull()) return true;
				return !forecast.isSure();
			}
		}
		catch(Exception e){
			HOLogger.instance().log(getClass(), "DatenbankZugriff.hasUnsureWeatherForecast : " + e);
		}
		return false;
	}
	// ///////////////////////////////////////////////////////////////////////////////
	// MatchesASP MatchKurzInfo
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Get all matches with a certain status for the given team from the
	 * database.
	 * 
	 * @param teamId
	 *            the teamid or -1 for all matches
	 * @param matchStatus
	 *            the match status (e.g. IMatchKurzInfo.UPCOMING) or -1 to
	 *            ignore this parameter
	 */
	MatchKurzInfo[] getMatchesKurzInfo(final int teamId, final int matchStatus) {

		String sql = "";
		var params = new ArrayList<Object>();

		if (teamId > -1) {
			sql += " WHERE GastID=? OR HeimID=?";
			params.add(teamId);
			params.add(teamId);
		}

		if (matchStatus > -1) {
			sql += (params.size()>0 ? " WHERE" : " AND")
				+ " Status=?";
			params.add(matchStatus);
		}

		sql += " ORDER BY MatchDate DESC";
		return getMatchesKurzInfo(sql, params);
	}

	MatchKurzInfo[] getMatchesKurzInfo(final String where, List<Object> params) {
		var liste = new Vector<MatchKurzInfo>();

		try {
			var sql = "SELECT * FROM " + getTableName() + " " + where;
			var rs = adapter.executePreparedQuery(sql,params.toArray());
			assert rs != null;
			rs.beforeFirst();
			while (rs.next()) {
				liste.add(createMatchKurzInfo(rs));
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DB.getMatchesKurzInfo Error" + e);
		}

		var matches = new MatchKurzInfo[liste.size()];
		matches = liste.toArray(matches);
		return matches;
	}

	/**
	 * Get all matches for the given team from the database.
	 * 
	 * @param teamId
	 *            the teamid or -1 for all matches
	 */
	MatchKurzInfo[] getMatchesKurzInfo(final int teamId) {
		return getMatchesKurzInfo(teamId, -1);
	}


	/**
	 * Returns the MatchKurzInfo for the match. Returns null if not found.
	 * 
	 * @param matchid the ID for the match
	 * @return The kurz info object or null
	 */
	MatchKurzInfo getMatchesKurzInfoByMatchID(int matchid, MatchType matchType) {

		try {

			var params = new ArrayList<Object>();
			String sql = "SELECT * FROM " + getTableName() + " WHERE MatchId=?";
			params.add(matchid);

			if ( matchType != null) {
				sql += " AND MatchTyp=?";
				params.add( matchType.getId());
			}

			final ResultSet rs = adapter.executePreparedQuery(sql, params);

			assert rs != null;
			rs.beforeFirst();
			if (rs.next()) {
				return createMatchKurzInfo(rs);
			}
		} catch (SQLException e) {

			HOLogger.instance().error(getClass(),
					"DB.getMatchesKurzInfo Error" + e);
		}

		return null;
	}

	public MatchKurzInfo getLastMatchWithMatchId(int matchId) {

		// Find latest match with id = matchId
		// Here we order by MatchDate, which happens to be string, which is somehow risky,
		// but it seems to be done in other places.
		String sql = "SELECT * FROM " + getTableName() + " WHERE MATCHID=? AND Status=? ORDER BY MATCHDATE DESC LIMIT 1";
		try {
			final ResultSet rs = adapter.executePreparedQuery(sql,matchId, MatchKurzInfo.FINISHED );
			assert rs != null;
			rs.beforeFirst();
			if (rs.next()) {
				return createMatchKurzInfo(rs);
			}
		} catch (SQLException e) {
			HOLogger.instance().error(getClass(), "getLastMatchWithMatchId error: " + e);
		}

		return null;
	}

	/**
	 * Gets first upcoming match with team id.
	 *
	 * @param teamId the team id
	 * @return the first upcoming match with team id
	 */
	public MatchKurzInfo getFirstUpcomingMatchWithTeamId(int teamId) {
		String sql = "SELECT * FROM " + getTableName() + " WHERE (GastID=? OR HeimID=?) AND Status=? ORDER BY MATCHDATE ASC LIMIT 1";
		try {
			final ResultSet rs = adapter.executePreparedQuery(sql, teamId, teamId, MatchKurzInfo.UPCOMING);
			assert rs != null;
			rs.beforeFirst();
			if (rs.next()) {
				return createMatchKurzInfo(rs);
			}
		}
		catch (SQLException e) {
			HOLogger.instance().error(getClass(), "getFirstUpcomingMatchWithTeamId error: " + e);
		}

		return null;
	}

	void update(MatchKurzInfo match) {
		String sql = "UPDATE " + getTableName() + " SET HeimName=?, HeimID=?, GastName=?, GastID=?, MatchDate=?," +
				" HeimTore=?, GastTore=?, Aufstellung=?, Status=?, MatchContextId=?, TournamentTypeID=?, CupLevel=?," +
				" CupLevelIndex=?, ArenaId=?, RegionId=?, isDerby=?, isObsolete=?, isNeutral=?, Weather=?," +
				" WeatherForecast=?, Duration=?" +
				" WHERE MatchID=? AND MatchTyp=?";
		adapter.executePreparedUpdate(sql,
				match.getHomeTeamName(),
				match.getHomeTeamID(),
				match.getGuestTeamName(),
				match.getGuestTeamID(),
				match.getMatchSchedule().toDbTimestamp(),
				match.getHomeTeamGoals(),
				match.getGuestTeamGoals(),
				match.isOrdersGiven(),
				match.getMatchStatus(),
				match.getMatchContextId(),
				match.getTournamentTypeID(),
				match.getCupLevel().getId(),
				match.getCupLevelIndex().getId(),
				match.getArenaId(),
				match.getRegionId(),
				match.getIsDerby(),
				match.isObsolet(),
				match.isNeutral(),
				match.getWeather().getId(),
				match.getWeatherForecast().getId(),
				match.getDuration(),
				match.getMatchID(),
				match.getMatchType().getId()
		);
	}

}
