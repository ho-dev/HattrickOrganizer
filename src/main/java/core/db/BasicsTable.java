package core.db;

import core.datatype.CBItem;
import core.model.HOVerwaltung;
import core.model.misc.Basics;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


final class BasicsTable extends AbstractTable {
	final static String TABLENAME = "BASICS";
	
	protected BasicsTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
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
		return new String[] {
			"CREATE INDEX IBASICS_2 ON " + getTableName() + "(Datum)"
		};
	}
	
	/**
	 * save Basics
	 *
	 */
	void saveBasics(int hrfId, core.model.misc.Basics basics) {

		final String[] awhereS = { "HRF_ID" };
		final String[] awhereV = { "" + hrfId };

		if (basics != null) {
			//erst Vorhandene Aufstellung löschen
			delete( awhereS, awhereV );

			//insert vorbereiten
			var statement = "INSERT INTO "+getTableName()+" ( TeamID , Manager , TeamName , Land , Liga , Saison, SeasonOffset , Spieltag , HRF_ID, Datum, Region, HasSupporter, YouthTeamName , YouthTeamID, ActivationDate) VALUES(";
			statement
				+= (""
					+ basics.getTeamId()
					+ ",'"
					+ core.db.DBManager.insertEscapeSequences(basics.getManager())
					+ "','"
					+ core.db.DBManager.insertEscapeSequences(basics.getTeamName())
					+ "',"
					+ basics.getLand()
					+ ","
					+ basics.getLiga()
					+ ","
					+ basics.getSeason()
					+ ","
					+ basics.getSeasonOffset()
					+ ","
					+ basics.getSpieltag()
					+ ","
					+ hrfId
					+ ",'"
					+ basics.getDatum().toDbTimestamp()
					+ "',"
					+ basics.getRegionId()
					+ ",'"
					+ basics.isHasSupporter()
					+ "','"
					+ core.db.DBManager.insertEscapeSequences(basics.getYouthTeamName() )
					+ "',"
					+ basics.getYouthTeamId()
					+ ","
					+ (basics.getActivationDate() == null ? "NULL" : "'" + basics.getActivationDate().toDbTimestamp() + "'")
					+ " )");
			adapter.executeUpdate(statement);
		}
	}
	
	/**
	 * lädt die Basics zum angegeben HRF file ein
	 *
	 */
	Basics getBasics(int hrfID) {
		ResultSet rs;
		Basics basics = new Basics();

		if (hrfID != -1) {

			rs = getSelectByHrfID(hrfID);
			try {
				if (rs != null) {
					rs.first();
					basics = new Basics(rs);
					rs.close();
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "DatenbankZugriff.getBasic: " + e);
			}
		}

		return basics;
	}

	/**
	 * load all hrf file entries and creates a list of combo box items
	 */
	List<CBItem> loadCBItemHRFList() {

		final String statement = "SELECT * FROM " + getTableName() + " ORDER BY Datum DESC";
		var hrfs = new ArrayList<CBItem>();

		try {
			var rs = adapter.executeQuery(statement);

			if (rs != null) {
				rs.beforeFirst();

				while (rs.next()) {
					var date = HODateTime.fromDbTimestamp(rs.getTimestamp("Datum"));
					var trainingWeek = date.toTrainingWeek();
					hrfs.add(
							new core.datatype.CBItem(
									date.toLocaleDateTime()
											+ " ( "
											+ core.model.HOVerwaltung.instance().getLanguageString("Season")
											+ " "
											+ trainingWeek.season
											+ "  "
											+ core.model.HOVerwaltung.instance().getLanguageString("ls.training.week")
											+ " "
											+ trainingWeek.week
											+ " )",
									rs.getInt("HRF_ID")));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getCBItemHRFListe " + e);
		}

		return hrfs;
	}

	/**
	 * Gibt die HRFId vor dem Datum zurï¿½ck, wenn mï¿½glich
	 */
	int getHrfIDSameTraining(Timestamp time) {

		int hrfID = -1;
//		Timestamp mintime = new Timestamp(time.getTime() - 259200000); //72 Std
		Timestamp hrfDate = null;

		//Die passende HRF-ID besorgen
//		sql = "SELECT HRF_ID, Datum FROM "+getTableName()+" WHERE Datum<='" + time.toString() + "' AND Datum>='" + mintime.toString() + "' ORDER BY Datum DESC";
		var sql = "SELECT HRF_ID, Datum FROM "+getTableName()+" WHERE Datum<='" + time.toString() + "' ORDER BY Datum DESC LIMIT 1";
		var rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				//HRF vorher vorhanden?
				if (rs.first()) {
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