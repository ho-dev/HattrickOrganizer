package core.db;

import core.model.WorldDetailLeague;
import java.sql.Types;
import java.util.List;

class WorldDetailsTable extends AbstractTable {

	final static String TABLENAME = "HT_WORLDDETAILS";
	
	WorldDetailsTable(ConnectionManager adapter){
		super(TABLENAME,adapter);
	}
	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("LEAGUE_ID").setGetter((p) -> ((WorldDetailLeague) p).getLeagueId()).setSetter((p, v) -> ((WorldDetailLeague) p).setLeagueId((int) v)).setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("COUNTRY_ID").setGetter((p) -> ((WorldDetailLeague) p).getCountryId()).setSetter((p, v) -> ((WorldDetailLeague) p).setCountryId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("COUNTRYNAME").setGetter((p) -> ((WorldDetailLeague) p).getCountryName()).setSetter((p, v) -> ((WorldDetailLeague) p).setCountryName((String) v)).setType(Types.VARCHAR).setLength(128).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ACTIVE_USER").setGetter((p) -> ((WorldDetailLeague) p).getActiveUsers()).setSetter((p, v) -> ((WorldDetailLeague) p).setActiveUsers((int) v)).setType(Types.INTEGER).isNullable(false).build()
		};
	}

	@Override
	protected String createSelectStatement() {
		return createSelectStatement("");
	}

	void insertWorldDetailsLeague(WorldDetailLeague league){
		if(league == null)
			return;
		store(league);
	}

	List<WorldDetailLeague> getAllWorldDetailLeagues(){
		var ret = load(WorldDetailLeague.class);
		if (ret.isEmpty()) {
			insertDefaultValues();
			ret = load(WorldDetailLeague.class);
		}
		return ret;
	}
	
	@Override
	protected void insertDefaultValues(){
		for ( var league : WorldDetailLeague.allLeagues){
			insertWorldDetailsLeague(league);
		}
	}
}
