package core.db;

import core.model.misc.Basics;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.Timestamp;
import java.sql.Types;

final class BasicsTable extends AbstractTable {
	final static String TABLENAME = "BASICS";

	BasicsTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((b)->((Basics)b).getHrfId()).setSetter((b, v)->((Basics)b).setHrfId((int)v)).setType(Types.INTEGER).isNullable(false).isPrimaryKey(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Manager").setGetter((b)->((Basics)b).getManager()).setSetter((b, v)->((Basics)b).setManager((String)v)).setType(Types.VARCHAR).setLength(127).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TeamID").setGetter((b)->((Basics)b).getTeamId()).setSetter((b, v)->((Basics)b).setTeamId((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TeamName").setGetter((b)->((Basics)b).getTeamName()).setSetter((b, v)->((Basics)b).setTeamName((String)v)).setType(Types.VARCHAR).setLength(127).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Land").setGetter((b)->((Basics)b).getLand()).setSetter((b, v)->((Basics)b).setLand((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Liga").setGetter((b)->((Basics)b).getLiga()).setSetter((b, v)->((Basics)b).setLiga((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Saison").setGetter((b)->((Basics)b).getSeason()).setSetter((b, v)->((Basics)b).setSeason((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Spieltag").setGetter((b)->((Basics)b).getSpieltag()).setSetter((b, v)->((Basics)b).setSpieltag((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Datum").setGetter((b)->((Basics)b).getDatum().toDbTimestamp()).setSetter((b, v)->((Basics)b).setDatum((HODateTime)v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Region").setGetter((b)->((Basics)b).getRegionId()).setSetter((b, v)->((Basics)b).setRegionId((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HasSupporter").setGetter((b)->((Basics)b).isHasSupporter()).setSetter((b, v)->((Basics)b).setHasSupporter((boolean)v)).setType(Types.BOOLEAN).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ActivationDate").setGetter((b)-> HODateTime.toDbTimestamp (((Basics)b).getActivationDate())).setSetter((b, v)->((Basics)b).setActivationDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SeasonOffset").setGetter((b)->((Basics)b).getSeasonOffset()).setSetter((b, v)->((Basics)b).setSeasonOffset((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("YouthTeamName").setGetter((b)->((Basics)b).getYouthTeamName()).setSetter((b, v)->((Basics)b).setYouthTeamName((String)v)).setType(Types.VARCHAR).setLength(127).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("YouthTeamID").setGetter((b)->((Basics)b).getYouthTeamId()).setSetter((b, v)->((Basics)b).setYouthTeamId((Integer) v)).setType(Types.INTEGER).isNullable(true).build()
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[]{
				"CREATE INDEX IBASICS_2 ON " + getTableName() + "(Datum)"
		};
	}

	/**
	 * save Basics
	 */
	void saveBasics(int hrfId, Basics basics) {
		basics.setHrfId(hrfId);
		store(basics);
	}

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 */
	Basics loadBasics(int hrfID) {
		var ret = loadOne(Basics.class, hrfID);
		if ( ret == null ) ret = new Basics();
		else if (ret.getSeasonOffset() == 0) {
			var season0 = ret.getDatum().toHTWeek().season;
			if (season0 != ret.getSeason()) {
				ret.setSeasonOffset( ret.getSeason() - season0 ) ;
			}
		}
		return ret;
	}

	private final DBManager.PreparedStatementBuilder getHrfIDSameTrainingStatementBuilder =  new DBManager.PreparedStatementBuilder(
			"SELECT HRF_ID, Datum FROM " + getTableName() + " WHERE Datum<= ? ORDER BY Datum DESC LIMIT 1");

	/**
	 * Gibt die HRFId vor dem Datum zurï¿½ck, wenn mï¿½glich
	 */
	int getHrfIDSameTraining(Timestamp time) {

		int hrfID = -1;
		Timestamp hrfDate = null;
		var rs = adapter.executePreparedQuery(getHrfIDSameTrainingStatementBuilder.getStatement(), time);
		try {
			if (rs != null) {
				//HRF vorher vorhanden?
				if (rs.next()) {
					hrfID = rs.getInt("HRF_ID");
					hrfDate = rs.getTimestamp("Datum");
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "XMLExporter.getHRFID4Time: " + e);
		}
		if (hrfID != -1) {
			//todo sicherstellen das kein Trainingsdatum zwischen matchdate und hrfdate liegt
			var training4Hrf = DBManager.instance().getXtraDaten(hrfID).getNextTrainingDate().toDbTimestamp();
			if ((training4Hrf.after(hrfDate)) && (training4Hrf.before(time))) //wenn hrfDate vor TrainingsDate und Matchdate nach Trainigsdate ->Abbruch!
			{
				hrfID = -1;
			}
		}
		return hrfID;
	}
}