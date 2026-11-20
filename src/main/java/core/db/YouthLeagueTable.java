package core.db;

import module.youth.YouthLeague;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

import static core.db.YouthTeamDetailsTable.integer;
import static core.db.YouthTeamDetailsTable.text;

/**
 * The youth league per season (youthleaguedetails 1.1). Its standings are in YOUTHLEAGUETEAM and
 * its matches in YOUTHLEAGUEFIXTURE; all three keep the current state, replaced per download.
 */
final class YouthLeagueTable extends AbstractTable {

    static final String TABLENAME = "YOUTHLEAGUE";
    private static final String YOUTH_LEAGUE_ID = "YouthLeagueID";
    private static final String SEASON = "Season";

    YouthLeagueTable(ConnectionManager adapter) {
        super(TABLENAME, adapter);
        idColumns = 2;
    }

    static ColumnDescriptor key(String name, UnaryOperator<Object> getter, BiConsumer<Object, Object> setter) {
        return ColumnDescriptor.Builder.newInstance().setColumnName(name).setGetter(getter)
                .setSetter(setter).setType(Types.INTEGER).isNullable(false).build();
    }

    @Override
    protected void initColumns() {
        final var cols = new ArrayList<ColumnDescriptor>();
        cols.add(key(YOUTH_LEAGUE_ID, o -> row(o).getYouthLeagueId(), (o, v) -> row(o).setYouthLeagueId((int) v)));
        cols.add(key(SEASON, o -> row(o).getSeason(), (o, v) -> row(o).setSeason((int) v)));
        cols.add(text("YouthLeagueName", o -> row(o).getYouthLeagueName(), (o, v) -> row(o).setYouthLeagueName((String) v)));
        cols.add(integer("YouthLeagueType", o -> row(o).getYouthLeagueType(), (o, v) -> row(o).setYouthLeagueType((Integer) v)));
        cols.add(integer("LastMatchRound", o -> row(o).getLastMatchRound(), (o, v) -> row(o).setLastMatchRound((Integer) v)));
        cols.add(integer("NrOfTeamsInLeague", o -> row(o).getNrOfTeamsInLeague(), (o, v) -> row(o).setNrOfTeamsInLeague((Integer) v)));
        cols.add(integer("LeagueSystemID", o -> row(o).getLeagueSystemId(), (o, v) -> row(o).setLeagueSystemId((Integer) v)));
        this.columns = cols.toArray(new ColumnDescriptor[0]);
    }

    private static YouthLeague row(Object o) {
        return (YouthLeague) o;
    }

    @Override
    protected String[] getConstraintStatements() {
        return new String[]{"  PRIMARY KEY (YouthLeagueID, Season)"};
    }

    void store(YouthLeague league) {
        executePreparedDelete(league.getYouthLeagueId(), league.getSeason());
        league.setIsStored(false);
        super.store(league);
    }

    YouthLeague load(int youthLeagueId, int season) {
        return loadOne(YouthLeague.class, youthLeagueId, season);
    }

    private static final String NEWEST_SEASON_SQL = "SELECT MAX(Season) FROM " + TABLENAME + " WHERE YouthLeagueID=?";

    /** The newest season stored for the league, null when none is. */
    Integer newestSeason(int youthLeagueId) {
        try (var rs = connectionManager.executePreparedQuery(NEWEST_SEASON_SQL, youthLeagueId)) {
            if (rs != null && rs.next()) {
                final int season = rs.getInt(1);
                return rs.wasNull() ? null : season;
            }
        } catch (Exception e) {
            core.util.HOLogger.instance().log(getClass(), e);
        }
        return null;
    }

    /** The standings of one league season. */
    static final class StandingsTable extends AbstractTable {
        static final String TABLENAME = "YOUTHLEAGUETEAM";

        StandingsTable(ConnectionManager adapter) {
            super(TABLENAME, adapter);
            // loaded and replaced per league season
            idColumns = 2;
        }

        @Override
        protected void initColumns() {
            final var cols = new ArrayList<ColumnDescriptor>();
            cols.add(key(YOUTH_LEAGUE_ID, o -> team(o).getYouthLeagueId(), (o, v) -> team(o).setYouthLeagueId((int) v)));
            cols.add(key(SEASON, o -> team(o).getSeason(), (o, v) -> team(o).setSeason((int) v)));
            cols.add(key("TeamID", o -> team(o).getTeamId(), (o, v) -> team(o).setTeamId((int) v)));
            cols.add(text("TeamName", o -> team(o).getTeamName(), (o, v) -> team(o).setTeamName((String) v)));
            cols.add(integer("Position", o -> team(o).getPosition(), (o, v) -> team(o).setPosition((Integer) v)));
            cols.add(integer("PositionChange", o -> team(o).getPositionChange(), (o, v) -> team(o).setPositionChange((Integer) v)));
            cols.add(integer("Matches", o -> team(o).getMatches(), (o, v) -> team(o).setMatches((Integer) v)));
            cols.add(integer("GoalsFor", o -> team(o).getGoalsFor(), (o, v) -> team(o).setGoalsFor((Integer) v)));
            cols.add(integer("GoalsAgainst", o -> team(o).getGoalsAgainst(), (o, v) -> team(o).setGoalsAgainst((Integer) v)));
            cols.add(integer("Points", o -> team(o).getPoints(), (o, v) -> team(o).setPoints((Integer) v)));
            cols.add(integer("Won", o -> team(o).getWon(), (o, v) -> team(o).setWon((Integer) v)));
            cols.add(integer("Draws", o -> team(o).getDraws(), (o, v) -> team(o).setDraws((Integer) v)));
            cols.add(integer("Lost", o -> team(o).getLost(), (o, v) -> team(o).setLost((Integer) v)));
            this.columns = cols.toArray(new ColumnDescriptor[0]);
        }

        private static YouthLeague.Team team(Object o) {
            return (YouthLeague.Team) o;
        }

        @Override
        protected String[] getConstraintStatements() {
            return new String[]{"  PRIMARY KEY (YouthLeagueID, Season, TeamID)"};
        }

        void store(int youthLeagueId, int season, List<YouthLeague.Team> teams) {
            executePreparedDelete(youthLeagueId, season);
            for (var team : teams) {
                team.setIsStored(false);
                super.store(team);
            }
        }

        private final String loadSql = createSelectStatement("WHERE YouthLeagueID=? AND Season=? ORDER BY Position, TeamID");

        /** The standings in table order. */
        List<YouthLeague.Team> load(int youthLeagueId, int season) {
            return load(YouthLeague.Team.class, connectionManager.executePreparedQuery(loadSql, youthLeagueId, season));
        }
    }

    /** The matches of one league season. */
    static final class FixtureTable extends AbstractTable {
        static final String TABLENAME = "YOUTHLEAGUEFIXTURE";

        FixtureTable(ConnectionManager adapter) {
            super(TABLENAME, adapter);
            // loaded and replaced per league season; the match id is the primary key
            idColumns = 2;
        }

        @Override
        protected void initColumns() {
            final var cols = new ArrayList<ColumnDescriptor>();
            cols.add(key(YOUTH_LEAGUE_ID, o -> fixture(o).getYouthLeagueId(), (o, v) -> fixture(o).setYouthLeagueId((int) v)));
            cols.add(key(SEASON, o -> fixture(o).getSeason(), (o, v) -> fixture(o).setSeason((int) v)));
            cols.add(key("MatchID", o -> fixture(o).getMatchId(), (o, v) -> fixture(o).setMatchId((int) v)));
            cols.add(integer("MatchRound", o -> fixture(o).getMatchRound(), (o, v) -> fixture(o).setMatchRound((Integer) v)));
            cols.add(text("Status", o -> fixture(o).getStatus(), (o, v) -> fixture(o).setStatus((String) v)));
            cols.add(integer("HomeTeamID", o -> fixture(o).getHomeTeamId(), (o, v) -> fixture(o).setHomeTeamId((Integer) v)));
            cols.add(text("HomeTeamName", o -> fixture(o).getHomeTeamName(), (o, v) -> fixture(o).setHomeTeamName((String) v)));
            cols.add(integer("AwayTeamID", o -> fixture(o).getAwayTeamId(), (o, v) -> fixture(o).setAwayTeamId((Integer) v)));
            cols.add(text("AwayTeamName", o -> fixture(o).getAwayTeamName(), (o, v) -> fixture(o).setAwayTeamName((String) v)));
            cols.add(YouthTeamDetailsTable.date("MatchDate", o -> fixture(o).getMatchDate(),
                    (o, v) -> fixture(o).setMatchDate((core.util.HODateTime) v)));
            cols.add(integer("HomeGoals", o -> fixture(o).getHomeGoals(), (o, v) -> fixture(o).setHomeGoals((Integer) v)));
            cols.add(integer("AwayGoals", o -> fixture(o).getAwayGoals(), (o, v) -> fixture(o).setAwayGoals((Integer) v)));
            this.columns = cols.toArray(new ColumnDescriptor[0]);
        }

        private static YouthLeague.Fixture fixture(Object o) {
            return (YouthLeague.Fixture) o;
        }

        @Override
        protected String[] getConstraintStatements() {
            return new String[]{"  PRIMARY KEY (MatchID)"};
        }

        void store(int youthLeagueId, int season, List<YouthLeague.Fixture> fixtures) {
            executePreparedDelete(youthLeagueId, season);
            for (var fixture : fixtures) {
                fixture.setIsStored(false);
                super.store(fixture);
            }
        }

        private final String loadSql = createSelectStatement("WHERE YouthLeagueID=? AND Season=? ORDER BY MatchRound, MatchDate, MatchID");

        /** The matches by round and kick-off. */
        List<YouthLeague.Fixture> load(int youthLeagueId, int season) {
            return load(YouthLeague.Fixture.class, connectionManager.executePreparedQuery(loadSql, youthLeagueId, season));
        }
    }
}
