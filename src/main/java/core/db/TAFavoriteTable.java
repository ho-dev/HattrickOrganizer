package core.db;

import module.teamAnalyzer.vo.Team;
import java.sql.Types;
import java.util.List;

/**
 * The Table UserConfiguration contain all User properties.
 * CONFIG_KEY = Primary Key, fieldname of the class
 * CONFIG_VALUE = value of the field, save as VARCHAR. Convert to right datatype if loaded
 * 
 * @since 1.36
 *
 */
final class TAFavoriteTable extends AbstractTable {
	final static String TABLENAME = "TA_FAVORITE";

	TAFavoriteTable(ConnectionManager adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {

        columns = new ColumnDescriptor[]{
                ColumnDescriptor.Builder.newInstance().setColumnName("TEAMID").setGetter((p) -> ((Team) p).getTeamId()).setSetter((p, v) -> ((Team) p).setTeamId((int) v)).setType(Types.INTEGER).isNullable(false).isPrimaryKey(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("NAME").setGetter((p) -> ((Team) p).getName()).setSetter((p, v) -> ((Team) p).setName((String) v)).setType(Types.VARCHAR).isNullable(true).setLength(20).build()
        };
	}

    void removeTeam(int teamId) {
        executePreparedDelete(teamId);
    }
    
    void addTeam(Team team) {
        store(team);
    }

    boolean isTAFavourite(int teamId) {
        return isStored(teamId);
    }

    private final String getTAFavoriteTeamsSql = createSelectStatement("");
    /**
     * Returns all favourite teams
     *
     * @return List of Teams Object
     */
    List<Team> getTAFavoriteTeams() {
        return load(Team.class, connectionManager.executePreparedQuery(getTAFavoriteTeamsSql));
    }
}
