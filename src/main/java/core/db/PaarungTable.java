package core.db;

import core.model.series.Paarung;
import core.util.HODateTime;
import core.util.HOLogger;
import module.series.Spielplan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;


public final class PaarungTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "PAARUNG";
	
	protected PaarungTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[11];
		columns[0]= new ColumnDescriptor("LigaID",Types.INTEGER,false);
		columns[1]= new ColumnDescriptor("Saison",Types.INTEGER,false);
		columns[2]= new ColumnDescriptor("HeimName",Types.VARCHAR,false,256);
		columns[3]= new ColumnDescriptor("GastName",Types.VARCHAR,false,256);
		columns[4]= new ColumnDescriptor("Datum",Types.VARCHAR,false,256);
		columns[5]= new ColumnDescriptor("Spieltag",Types.INTEGER,false);
		columns[6]= new ColumnDescriptor("HeimID",Types.INTEGER,false);
		columns[7]= new ColumnDescriptor("GastID",Types.INTEGER,false);
		columns[8]= new ColumnDescriptor("HeimTore",Types.INTEGER,false);
		columns[9]= new ColumnDescriptor("GastTore",Types.INTEGER,false);
		columns[10]= new ColumnDescriptor("MatchID",Types.INTEGER,false);
	}

	@Override
	protected PreparedDeleteStatementBuilder createPreparedDeleteStatementBuilder(){
		return new PreparedDeleteStatementBuilder(this,"WHERE SAISON=? AND LigaId=?");
	}


	/**
	 * Saves a list of games to a given game schedule, i.e. {@link Spielplan}.
	 */
	void storePaarung(List<Paarung> fixtures, int ligaId, int saison) {
		Paarung match = null;
		String sql = null;

		// Remove existing fixtures for the Spielplan if any exists.
		if (fixtures != null) {
			executePreparedDelete(saison, ligaId);
		}

		for (int i = 0;(fixtures != null) && (i < fixtures.size()); i++) {
			match = fixtures.get(i);

			try {
				executePreparedInsert(
						ligaId,
						saison,
						match.getHeimName(),
						match.getGastName(),
						match.getDatum().toDbTimestamp(),
						match.getSpieltag(),
						match.getHeimId(),
						match.getGastId(),
						match.getToreHeim(),
						match.getToreGast(),
						match.getMatchId()
				);
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),"DB.storePaarung Error" + e);
				HOLogger.instance().log(getClass(),e);
			}
		}
	}

	@Override
	protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder(){
		return new PreparedSelectStatementBuilder(this," WHERE LigaID = ? AND Saison = ?");
	}

	/**
	 * holt die Paarungen zum Plan aus der DB und added sie
	 */
	void getPaarungen(Spielplan plan) {
		Paarung match = null;
		String sql = null;
		ResultSet rs = null;

		try {
			rs = executePreparedSelect(plan.getLigaId(), plan.getSaison());
			assert rs != null;
			while (rs.next()) {
				//Paarung auslesen
				match = new Paarung();
				match.setDatum(HODateTime.fromDbTimestamp(rs.getTimestamp("Datum")));
				match.setGastId(rs.getInt("GastID"));
				match.setGastName(rs.getString("GastName"));
				match.setHeimId(rs.getInt("HeimID"));
				match.setHeimName(rs.getString("HeimName"));
				match.setMatchId(rs.getInt("MatchID"));
				match.setSpieltag(rs.getInt("Spieltag"));
				match.setToreGast(rs.getInt("GastTore"));
				match.setToreHeim(rs.getInt("HeimTore"));
				match.setLigaId(plan.getLigaId());
				match.setSaison(plan.getSaison());

				//Adden
				plan.addEintrag(match);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getPaarungen Error" + e);
			HOLogger.instance().log(getClass(),e);
		}
	}

}
