package core.db;

import core.model.misc.Basics;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.ResultSet;
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
				new ColumnDescriptor("HRF_ID", Types.INTEGER, false, true),
				new ColumnDescriptor("Manager", Types.VARCHAR, false, 127),
				new ColumnDescriptor("TeamID", Types.INTEGER, false),
				new ColumnDescriptor("TeamName", Types.VARCHAR, false, 127),
				new ColumnDescriptor("Land", Types.INTEGER, false),
				new ColumnDescriptor("Liga", Types.INTEGER, false),
				new ColumnDescriptor("Saison", Types.INTEGER, false),
				new ColumnDescriptor("Spieltag", Types.INTEGER, false),
				new ColumnDescriptor("Datum", Types.TIMESTAMP, false),
				new ColumnDescriptor("Region", Types.INTEGER, false),
				new ColumnDescriptor("HasSupporter", Types.BOOLEAN, false),
				new ColumnDescriptor("ActivationDate", Types.TIMESTAMP, true),
				new ColumnDescriptor("SeasonOffset", Types.INTEGER, true),
				new ColumnDescriptor("YouthTeamName", Types.VARCHAR, true, 127),
				new ColumnDescriptor("YouthTeamID", Types.INTEGER, true)
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
	void saveBasics(int hrfId, core.model.misc.Basics basics) {
		if (basics != null) {
			executePreparedDelete(hrfId);
			executePreparedInsert(
					hrfId,
					basics.getManager(),
					basics.getTeamId(),
					basics.getTeamName(),
					basics.getLand(),
					basics.getLiga(),
					basics.getSeason(),
					basics.getSpieltag(),
					HODateTime.toDbTimestamp(basics.getDatum()),
					basics.getRegionId(),
					basics.isHasSupporter(),
					HODateTime.toDbTimestamp(basics.getActivationDate()),
					basics.getSeasonOffset(),
					basics.getYouthTeamName(),
					basics.getYouthTeamId()
			);
		}
	}

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 */
	Basics getBasics(int hrfID) {
		ResultSet rs;
		Basics basics = new Basics();

		if (hrfID != -1) {

			rs = executePreparedSelect(hrfID);
			try {
				if (rs != null) {
					rs.next();
					basics = new Basics(rs);
					rs.close();
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "DatenbankZugriff.getBasic: " + e);
			}
		}

		return basics;
	}

	private final DBManager.PreparedStatementBuilder getHrfIDSameTrainingStatementBuilder =  new DBManager.PreparedStatementBuilder(this.adapter, "SELECT HRF_ID, Datum FROM " + getTableName() + " WHERE Datum<= ? ORDER BY Datum DESC LIMIT 1");

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