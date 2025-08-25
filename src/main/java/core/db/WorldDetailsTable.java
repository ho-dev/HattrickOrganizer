package core.db;

import core.model.WorldDetailLeague;
import java.sql.Types;
import java.util.List;

class WorldDetailsTable extends AbstractTable {

	static final String TABLENAME = "HT_WORLDDETAILS";

	WorldDetailsTable(ConnectionManager adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("LEAGUE_ID").setGetter((p) -> ((WorldDetailLeague) p).getLeagueId()).setSetter((p, v) -> ((WorldDetailLeague) p).setLeagueId((int) v)).setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("COUNTRY_ID").setGetter((p) -> ((WorldDetailLeague) p).getCountryId()).setSetter((p, v) -> ((WorldDetailLeague) p).setCountryId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("COUNTRYNAME").setGetter((p) -> ((WorldDetailLeague) p).getCountryName()).setSetter((p, v) -> ((WorldDetailLeague) p).setCountryName((String) v)).setType(Types.VARCHAR).setLength(128).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ACTIVE_USER").setGetter((p) -> ((WorldDetailLeague) p).getActiveUsers()).setSetter((p, v) -> ((WorldDetailLeague) p).setActiveUsers((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("COUNTRY_CODE").setGetter((p) -> ((WorldDetailLeague) p).getCountryCode()).setSetter((p, v) -> ((WorldDetailLeague) p).setCountryCode((String) v)).setType(Types.VARCHAR).setLength(128).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CURRENCY_NAME").setGetter((p) -> ((WorldDetailLeague) p).getCurrencyName()).setSetter((p, v) -> ((WorldDetailLeague) p).setCurrencyName((String) v)).setType(Types.VARCHAR).setLength(128).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CURRENCY_RATE").setGetter((p) -> ((WorldDetailLeague) p).getCurrencyRate()).setSetter((p, v) -> ((WorldDetailLeague) p).setCurrencyRate((String) v)).setType(Types.VARCHAR).setLength(128).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("DATE_FORMAT").setGetter((p) -> ((WorldDetailLeague) p).getDateFormat()).setSetter((p, v) -> ((WorldDetailLeague) p).setDateFormat((String) v)).setType(Types.VARCHAR).setLength(128).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TIME_FORMAT").setGetter((p) -> ((WorldDetailLeague) p).getTimeFormat()).setSetter((p, v) -> ((WorldDetailLeague) p).setTimeFormat((String) v)).setType(Types.VARCHAR).setLength(128).isNullable(true).build(),
		};
	}

	@Override
	protected String createSelectStatement() {
		return createSelectStatement("");
	}

	void storeWorldDetailsLeague(WorldDetailLeague league) {
		if (league == null)
			return;
		store(league);
	}

	List<WorldDetailLeague> getAllWorldDetailLeagues() {
		var ret = load(WorldDetailLeague.class);
		if (ret.isEmpty()) {
			insertDefaultValues();
			ret = load(WorldDetailLeague.class);
		}
		return ret;
	}
}
