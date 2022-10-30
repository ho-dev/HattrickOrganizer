package core.db;

import core.model.HOVerwaltung;
import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.match.MatchKurzInfo;
import core.model.enums.MatchType;
import core.model.match.Weather;
import core.util.HODateTime;
import core.util.HOLogger;
import module.matches.MatchLocation;
import module.matches.MatchesPanel;
import module.matches.statistics.MatchesOverviewCommonPanel;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Class used to store in DB, [Matches] Table fetched via CHPP
 */
final class MatchesKurzInfoTable extends AbstractTable {
	final static String TABLENAME = "MATCHESKURZINFO";

	MatchesKurzInfoTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
		idColumns = 2;
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchID").setGetter((o) -> ((MatchKurzInfo) o).getMatchID()).setSetter((o, v) -> ((MatchKurzInfo) o).setMatchID((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchTyp").setGetter((o) -> ((MatchKurzInfo) o).getMatchType().getId()).setSetter((o, v) -> ((MatchKurzInfo) o).setMatchType(MatchType.getById((int) v))).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimName").setGetter((o) -> ((MatchKurzInfo) o).getHomeTeamName()).setSetter((o, v) -> ((MatchKurzInfo) o).setHomeTeamName((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimID").setGetter((o) -> ((MatchKurzInfo) o).getHomeTeamID()).setSetter((o, v) -> ((MatchKurzInfo) o).setHomeTeamID((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastName").setGetter((o) -> ((MatchKurzInfo) o).getGuestTeamName()).setSetter((o, v) -> ((MatchKurzInfo) o).setGuestTeamName((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastID").setGetter((o) -> ((MatchKurzInfo) o).getGuestTeamID()).setSetter((o, v) -> ((MatchKurzInfo) o).setGuestTeamID((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchDate").setGetter((o) -> ((MatchKurzInfo) o).getMatchSchedule().toDbTimestamp()).setSetter((o, v) -> ((MatchKurzInfo) o).setMatchSchedule((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimTore").setGetter((o) -> ((MatchKurzInfo) o).getHomeTeamGoals()).setSetter((o, v) -> ((MatchKurzInfo) o).setHomeTeamGoals((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastTore").setGetter((o) -> ((MatchKurzInfo) o).getGuestTeamGoals()).setSetter((o, v) -> ((MatchKurzInfo) o).setGuestTeamGoals((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Aufstellung").setGetter((o) -> ((MatchKurzInfo) o).isOrdersGiven()).setSetter((o, v) -> ((MatchKurzInfo) o).setOrdersGiven((boolean) v)).setType(Types.BOOLEAN).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Status").setGetter((o) -> ((MatchKurzInfo) o).getMatchStatus()).setSetter((o, v) -> ((MatchKurzInfo) o).setMatchStatus((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CupLevel").setGetter((o) -> ((MatchKurzInfo) o).getCupLevel().getId()).setSetter((o, v) -> ((MatchKurzInfo) o).setCupLevel(CupLevel.fromInt((int) v))).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CupLevelIndex").setGetter((o) -> ((MatchKurzInfo) o).getCupLevelIndex().getId()).setSetter((o, v) -> ((MatchKurzInfo) o).setCupLevelIndex(CupLevelIndex.fromInt((int) v))).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchContextId").setGetter((o) -> ((MatchKurzInfo) o).getMatchContextId()).setSetter((o, v) -> ((MatchKurzInfo) o).setMatchContextId((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TournamentTypeID").setGetter((o) -> ((MatchKurzInfo) o).getTournamentTypeID()).setSetter((o, v) -> ((MatchKurzInfo) o).setTournamentTypeID((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ArenaId").setGetter((o) -> ((MatchKurzInfo) o).getArenaId()).setSetter((o, v) -> ((MatchKurzInfo) o).setArenaId((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("RegionId").setGetter((o) -> ((MatchKurzInfo) o).getRegionId()).setSetter((o, v) -> ((MatchKurzInfo) o).setRegionId((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("isDerby").setGetter((o) -> ((MatchKurzInfo) o).isDerby()).setSetter((o, v) -> ((MatchKurzInfo) o).setIsDerby((Boolean) v)).setType(Types.BOOLEAN).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("isNeutral").setGetter((o) -> ((MatchKurzInfo) o).isNeutral()).setSetter((o, v) -> ((MatchKurzInfo) o).setIsNeutral((Boolean) v)).setType(Types.BOOLEAN).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Weather").setGetter((o) -> ((MatchKurzInfo) o).getWeather().getId()).setSetter((o, v) -> ((MatchKurzInfo) o).setWeather(Weather.getById((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("WeatherForecast").setGetter((o) -> ((MatchKurzInfo) o).getWeatherForecast().getId()).setSetter((o, v) -> ((MatchKurzInfo) o).setWeatherForecast(Weather.Forecast.getById((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Duration").setGetter((o) -> ((MatchKurzInfo) o).getDuration()).setSetter((o, v) -> ((MatchKurzInfo) o).setDuration((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("isObsolete").setGetter((o) -> ((MatchKurzInfo) o).isObsolet()).setSetter((o, v) -> ((MatchKurzInfo) o).setisObsolet((Boolean) v)).setType(Types.BOOLEAN).isNullable(true).build()
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
		if (matches == null) return;
		for (var match : matches) {
			match.setIsStored(isStored(match.getMatchID(), match.getMatchType().getId()));
			store(match);
		}
	}
	void update(MatchKurzInfo match) {
		store(match);
	}

	private final HashMap<String, PreparedStatement> preparedStatements = new HashMap<>();
//	private PreparedStatement getPreparedStatement(String sql){
//		var ret = preparedStatements.get(sql);
//		if ( ret == null ){
//			ret = this.adapter.createPreparedStatement(sql);
//			preparedStatements.put(sql, ret);
//		}
//		return ret;
//	}
	MatchKurzInfo getMatchesKurzInfo(int teamId, int matchtyp, int statistic, boolean home) {
		StringBuilder sql = new StringBuilder(200);
		String column = "";
		String column2 = "";

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
		sql.append(" WHERE ").append(home ? "HEIMID=?" : "GASTID=?");
		sql.append(" AND HEIMTORE ").append(column2).append(" GASTTORE ");
		sql.append(getMatchTypWhereClause(matchtyp));
		sql.append(" ORDER BY DIFF DESC ");
		return loadOne(MatchKurzInfo.class, adapter.executePreparedQuery(DBManager.instance().getPreparedStatement(sql.toString()), teamId));

	}

	/**
	 * Return the list of n latest played matches (own team)
	 */
	List<MatchKurzInfo> getPlayedMatchInfo(@Nullable Integer iNbGames, boolean bOfficialOnly, boolean ownTeam) {
		final int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();

		var params = new ArrayList<>();
		var where = new StringBuilder(" WHERE Status=? ");
		params.add(MatchKurzInfo.FINISHED);
		if(ownTeam) {
			where.append(" AND ( GastID = ? OR HeimID = ?)");
			params.add(teamId);
			params.add(teamId);
		}
		if(bOfficialOnly) {
			where.append(getOfficialMatchTypWhereClause());
		}
		where.append(" ORDER BY MatchDate DESC");
		if(iNbGames != null) {
			where.append(" LIMIT ").append(iNbGames);
		}
		return getMatchesKurzInfo(where.toString(), params.toArray());
	}

	/**
	 * Important: If the teamid = -1 the match type must be ALL_GAMES!
	 * @param teamId The Teamid or -1 for all
	 * @param matchType Which matches? Constants in the GamesPanel!
	 * @param matchLocation Home, Away, Neutral
	 * @param from filter match schedule date
	 * @param includeUpcoming if false filter finished matches only
	 * @return MatchKurzInfo[] – Array of match info.
	 */
	List<MatchKurzInfo> getMatchesKurzInfo(int teamId, int matchType, MatchLocation matchLocation, Timestamp from, boolean includeUpcoming) {

		final ArrayList<MatchKurzInfo> liste = new ArrayList<>();
		// Without TeamID ino only All_Match possible
		if ((teamId < 0) && (matchType != MatchesPanel.ALL_GAMES)) {
			return liste;
		}

		var params = new ArrayList<>();
		// filter time
		var sql = new StringBuilder(" WHERE MatchDate>= ? ");
		params.add(from);

		if(matchType != MatchesPanel.ALL_GAMES){
			// OTHER TEAM GAMES =============================================
			if(matchType == MatchesPanel.OTHER_TEAM_GAMES){
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
		if (matchType >= 10 ) {
			matchType -= 10;
			includeUpcoming = false;
		}

		if( !includeUpcoming) {
			sql.append(" AND Status=?");
			params.add(MatchKurzInfo.FINISHED);
		}

		sql.append(getMatchTypWhereClause(matchType));
		sql.append(" ORDER BY MatchDate DESC");

		return getMatchesKurzInfo(sql.toString(), params.toArray());
	}

	List<MatchKurzInfo> getMatchesKurzInfoUpComing(int teamId) {
		// Ohne Matchid nur AlleSpiele möglich!
		if ((teamId < 0)) {
			return new ArrayList<>();
		}
		return getMatchesKurzInfo(" WHERE ( GastID = ? OR HeimID = ? ) AND Status= ? AND MatchTyp!= ? ORDER BY MatchDate DESC",
					teamId,
					teamId,
					MatchKurzInfo.UPCOMING,
					MatchType.LEAGUE.getId());
	}

	public MatchKurzInfo loadLastMatchesKurzInfo(int teamId) {
		var matches = getMatchesKurzInfo(" WHERE ( GastID = ? OR HeimID = ? ) AND Status = ? ORDER BY MatchDate DESC LIMIT 1",
					teamId,
					teamId,
					MatchKurzInfo.FINISHED);
		return matches.stream().findFirst().orElse(null);
	}

	public MatchKurzInfo loadNextMatchesKurzInfo(int teamId) {
		var matches = getMatchesKurzInfo(" WHERE ( GastID = ? OR HeimID = ? ) AND Status=? ORDER BY MatchDate ASC LIMIT 1",
					teamId,
					teamId,
					MatchKurzInfo.UPCOMING);
		return matches.stream().findFirst().orElse(null);
	}

	private StringBuilder getOfficialMatchTypWhereClause() {
		StringBuilder sql = new StringBuilder(100);
		var officialTypes = MatchType.getOfficialMatchTypes();
		sql.append(" AND MatchTyp IN (");
		char sep = ' ';
		for (var t : officialTypes) {
			sql.append(sep).append(t.getId());
			sep = ',';
		}
		sql.append(" )");
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

	/**
	 * Check if a match is already in the database.
	 */
	boolean isMatchInDB(int matchid, MatchType matchType) {
		return isStored(matchid, matchType.getId());
	}

	private final DBManager.PreparedStatementBuilder hasUnsureWeatherForecastStatementBuilder = new DBManager.PreparedStatementBuilder(
			"SELECT WeatherForecast FROM " + getTableName() + " WHERE MatchId=?");
	boolean hasUnsureWeatherForecast(int matchId)
	{
		try{
			final ResultSet rs = adapter.executePreparedQuery(hasUnsureWeatherForecastStatementBuilder.getStatement(),matchId);
			assert rs != null;
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
	List<MatchKurzInfo> getMatchesKurzInfo(final int teamId, final int matchStatus) {

		var sql = new StringBuilder();
		var params = new ArrayList<>();

		var sep = "WHERE";
		if (teamId > -1) {
			sql.append(sep).append(" GastID=? OR HeimID=?");
			sep = " AND";
			params.add(teamId);
			params.add(teamId);
		}

		if (matchStatus > -1) {
			sql.append(sep).append(" Status=?");
			params.add(matchStatus);
		}

		sql.append( " ORDER BY MatchDate DESC");
		return getMatchesKurzInfo(sql.toString(), params.toArray());
	}

	public List<MatchKurzInfo> getMatchesKurzInfo(String sql, Object ... params) {
		return load(MatchKurzInfo.class, adapter.executePreparedQuery(getMatchKurzInfoStatement(sql), params));
	}

	/**
	 * Get all matches for the given team from the database.
	 * 
	 * @param teamId
	 *            the teamid or -1 for all matches
	 */
	List<MatchKurzInfo> getMatchesKurzInfo(final int teamId) {
		return getMatchesKurzInfo(teamId, -1);
	}


	/**
	 * Returns the MatchKurzInfo for the match. Returns null if not found.
	 * 
	 * @param matchid the ID for the match
	 * @return The kurz info object or null
	 */
	MatchKurzInfo getMatchesKurzInfoByMatchID(int matchid, MatchType matchType) {
		if ( matchType != null&& matchType != MatchType.NONE)
			return loadOne(MatchKurzInfo.class, matchid, matchType.getId());

		var matches = getMatchesKurzInfo(" WHERE MATCHID=? ", matchid);
		return matches.stream().findFirst().orElse(null);
	}

	public MatchKurzInfo getLastMatchWithMatchId(int matchId) {
		// Find latest match with id = matchId
		// Here we order by MatchDate, which happens to be string, which is somehow risky,
		// but it seems to be done in other places.
		var matches = getMatchesKurzInfo(" WHERE MATCHID=? AND Status=? ORDER BY MATCHDATE DESC LIMIT 1", matchId, MatchKurzInfo.FINISHED);
		return matches.stream().findFirst().orElse(null);
	}

	/**
	 * Gets first upcoming match with team id.
	 *
	 * @param teamId the team id
	 * @return the first upcoming match with team id
	 */
	public MatchKurzInfo getFirstUpcomingMatchWithTeamId(int teamId) {
		var matches = getMatchesKurzInfo(" WHERE (GastID=? OR HeimID=?) AND Status=? ORDER BY MATCHDATE ASC LIMIT 1",
				teamId,
				teamId,
				MatchKurzInfo.UPCOMING);
		return matches.stream().findFirst().orElse(null);
	}
	private PreparedStatement getMatchKurzInfoStatement(String where) {
		PreparedStatement ret = preparedStatements.get(where);
		if ( ret == null){
			ret = createSelectStatement(where);
			preparedStatements.put(where, ret);
		}
		return ret;
	}

	private PreparedStatement createSelectStatement(String where) {
		return  new PreparedSelectStatementBuilder(this, where).getStatement();
	}

	public List<MatchKurzInfo> getMatchesKurzInfo(int teamId, int status, Timestamp from, List<Integer> matchTypes) {
		var params = new ArrayList<>();
		StringBuilder whereClause = new StringBuilder(" WHERE (GastID=? OR HeimID=?) AND Status=?");
		params.add(teamId);
		params.add(teamId);
		params.add(status);
		if ( from != null) {
			whereClause.append(" AND MATCHDATE > ?");
			params.add(from);
		}
		params.addAll(matchTypes);
		var placeholders =matchTypes.stream().map(i->"?").collect(Collectors.joining(","));
		whereClause.append(" AND MATCHTYP IN (").append(placeholders).append(") ORDER BY MatchDate DESC");
		return getMatchesKurzInfo(whereClause.toString(), params.toArray());
	}

	public List<MatchKurzInfo> getMatchesKurzInfo(int teamId, Timestamp  from, Timestamp to, List<MatchType> matchTypes) {
		var typeListAsInt = matchTypes.stream().map(MatchType::getId).toList();
		var placeholders =matchTypes.stream().map(i->"?").collect(Collectors.joining(","));
		var params = new ArrayList<>();
		params.add(teamId);
		params.add(teamId);
		params.add(from);
		params.add(to);
		params.addAll(typeListAsInt);
		params.add(MatchKurzInfo.UPCOMING);
		params.add(MatchKurzInfo.FINISHED);
		return getMatchesKurzInfo("WHERE (HEIMID = ? OR GASTID = ?) AND MATCHDATE BETWEEN ? AND ? AND MATCHTYP in ("+ placeholders +") AND STATUS in (?, ?) ORDER BY MatchDate DESC",
				params.toArray()
		);
	}
}
