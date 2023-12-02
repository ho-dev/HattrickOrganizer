package core.db;

import core.model.series.Paarung;
import core.util.HODateTime;
import module.series.Spielplan;
import java.sql.Types;
import java.util.List;

public final class PaarungTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "PAARUNG";
	
	PaarungTable(ConnectionManager adapter){
		super(TABLENAME,adapter);
		idColumns = 2;
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("LigaID").setGetter((o) -> ((Paarung) o).getLigaId()).setSetter((o, v) -> ((Paarung) o).setLigaId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Saison").setGetter((o) -> ((Paarung) o).getSaison()).setSetter((o, v) -> ((Paarung) o).setSaison((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimName").setGetter((o) -> ((Paarung) o).getHeimName()).setSetter((o, v) -> ((Paarung) o).setHeimName((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastName").setGetter((o) -> ((Paarung) o).getGastName()).setSetter((o, v) -> ((Paarung) o).setGastName((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Datum").setGetter((o) -> ((Paarung) o).getDatum().toDbTimestamp()).setSetter((o, v) -> ((Paarung) o).setDatum((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Spieltag").setGetter((o) -> ((Paarung) o).getSpieltag()).setSetter((o, v) -> ((Paarung) o).setSpieltag((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimID").setGetter((o) -> ((Paarung) o).getHeimId()).setSetter((o, v) -> ((Paarung) o).setHeimId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastID").setGetter((o) -> ((Paarung) o).getGastId()).setSetter((o, v) -> ((Paarung) o).setGastId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimTore").setGetter((o) -> ((Paarung) o).getToreHeim()).setSetter((o, v) -> ((Paarung) o).setToreHeim((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastTore").setGetter((o) -> ((Paarung) o).getToreGast()).setSetter((o, v) -> ((Paarung) o).setToreGast((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchID").setGetter((o) -> ((Paarung) o).getMatchId()).setSetter((o, v) -> ((Paarung) o).setMatchId((int) v)).setType(Types.INTEGER).isNullable(false).build()
		};
	}

	/**
	 * Saves a list of games to a given game schedule, i.e. {@link Spielplan}.
	 */
	void storePaarung(List<Paarung> fixtures, int ligaId, int saison) {
		if (fixtures == null) {
			return;
		}
		// Remove existing fixtures for the Spielplan if any exists.
		executePreparedDelete(ligaId, saison);
		for ( var fixture : fixtures){
			fixture.setLigaId(ligaId);
			fixture.setSaison(saison);
			fixture.setIsStored(false);
			store(fixture);
		}
	}

	public List<Paarung> loadFixtures(int ligaId, int season){
		return load(Paarung.class, ligaId, season);
	}
}
