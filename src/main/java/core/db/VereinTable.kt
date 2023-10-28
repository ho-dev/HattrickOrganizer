package core.db;

import core.model.misc.Verein;

import java.sql.Types;

final class VereinTable extends AbstractTable {
	final static String TABLENAME = "VEREIN";

	VereinTable(JDBCAdapter adapter){
		super( TABLENAME, adapter );
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((p) -> ((Verein) p).getHrfId()).setSetter((p, v) -> ((Verein) p).setHrfId((int) v)).setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("COTrainer").setGetter((p) -> ((Verein) p).getCoTrainer()).setSetter((p, v) -> ((Verein) p).setCoTrainer((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Pschyologen").setGetter((p) -> ((Verein) p).getPsychologen()).setSetter((p, v) -> ((Verein) p).setPsychologen((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Finanzberater").setGetter((p) -> ((Verein) p).getFinancialDirectorLevels()).setSetter((p, v) -> ((Verein) p).setFinancialDirectorLevels((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("PRManager").setGetter((p) -> ((Verein) p).getPRManager()).setSetter((p, v) -> ((Verein) p).setPRManager((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Aerzte").setGetter((p) -> ((Verein) p).getAerzte()).setSetter((p, v) -> ((Verein) p).setAerzte((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Jugend").setGetter((p) -> ((Verein) p).getJugend()).setSetter((p, v) -> ((Verein) p).setJugend((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Siege").setGetter((p) -> ((Verein) p).getSiege()).setSetter((p, v) -> ((Verein) p).setSiege((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Ungeschlagen").setGetter((p) -> ((Verein) p).getUngeschlagen()).setSetter((p, v) -> ((Verein) p).setUngeschlagen((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Fans").setGetter((p) -> ((Verein) p).getFans()).setSetter((p, v) -> ((Verein) p).setFans((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TacticAssist").setGetter((p) -> ((Verein) p).getTacticalAssistantLevels()).setSetter((p, v) -> ((Verein) p).setTacticalAssistantLevels((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("FormAssist").setGetter((p) -> ((Verein) p).getFormCoachLevels()).setSetter((p, v) -> ((Verein) p).setFormCoachLevels((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GlobalRanking").setGetter((p) -> ((Verein) p).getGlobalRanking()).setSetter((p, v) -> ((Verein) p).setGlobalRanking((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LeagueRanking").setGetter((p) -> ((Verein) p).getLeagueRanking()).setSetter((p, v) -> ((Verein) p).setLeagueRanking((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("RegionRanking").setGetter((p) -> ((Verein) p).getRegionRanking()).setSetter((p, v) -> ((Verein) p).setRegionRanking((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("PowerRating").setGetter((p) -> ((Verein) p).getPowerRating()).setSetter((p, v) -> ((Verein) p).setPowerRating((int) v)).setType(Types.INTEGER).isNullable(false).build()
		};
	}

	/**
	 * store team info
	 */
	void saveVerein(int hrfId, Verein verein) {
		if (verein != null) {
			verein.setHrfId(hrfId);
			verein.setIsStored(isStored(hrfId));
			store(verein);
		}
	}

	/**
	 * load team basic information
	 */
	Verein loadVerein(int hrfID) {
		var ret = loadOne(Verein.class, hrfID);
		if ( ret == null ) ret =  new Verein();
		return ret;
	}
}