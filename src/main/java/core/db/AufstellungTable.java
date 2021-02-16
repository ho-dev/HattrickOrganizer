package core.db;

import core.module.IModule;
import core.util.HOLogger;
import module.lineup.Lineup;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Vector;

final class AufstellungTable extends AbstractTable {
	final static String TABLENAME = "AUFSTELLUNG";

	protected AufstellungTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				new ColumnDescriptor("HRF_ID", Types.INTEGER, false),
				new ColumnDescriptor("Kicker", Types.INTEGER, false),
				new ColumnDescriptor("Kapitaen", Types.INTEGER, false),
				new ColumnDescriptor("Attitude", Types.INTEGER, false),
				new ColumnDescriptor("Tactic", Types.INTEGER, false),
				new ColumnDescriptor("Aufstellungsname", Types.VARCHAR, false, 256),
				new ColumnDescriptor("StyleOfPlay", Types.INTEGER, false),
				new ColumnDescriptor("SourceSystem", Types.INTEGER, false)
		};
	}

	/**
	 * lädt System Positionen
	 */
	Lineup getAufstellung(int hrfID, String name) {

		module.lineup.Lineup lineup = new module.lineup.Lineup();

		if (hrfID == -1){
			return lineup;
		}

		ResultSet rs;
		String sql;

		sql = "SELECT * FROM " + getTableName() + " WHERE HRF_ID = " + hrfID
				+ " and Aufstellungsname ='" + name + "'";
		rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				rs.first();
				lineup.setCaptain(rs.getInt("Kapitaen"));
				lineup.setKicker(rs.getInt("Kicker"));
				lineup.setTacticType(rs.getInt("Tactic"));
				lineup.setAttitude(rs.getInt("Attitude"));
				lineup.setStyleOfPlay(rs.getInt("StyleOfPlay"));

				lineup.setPositionen(DBManager.instance().getSystemPositionen(hrfID, name));
				lineup.setSubstitionList(new ArrayList<>(DBManager.instance()
						.getMatchSubstitutionsByHrf(hrfID, name)));
				lineup.setPenaltyTakers(DBManager.instance().getPenaltyTakers(name));
				lineup.setRatings(hrfID);
			}
		}
			catch (Exception e) {
			HOLogger.instance().log(getClass(), "Error while loading lineup: " + e);
		}

		return lineup;
	}

//	/**
//	 * gibt liste für Aufstellungen
//	 *
//	 * @param hrfID
//	 *            -1 für default = hrf unabhängig
//	 */
//	Vector<String> getAufstellungsListe(int hrfID) {
//		final Vector<String> ret = new Vector<String>();
//		ResultSet rs = null;
//		String sql = null;
//
//		sql = "SELECT Aufstellungsname FROM " + getTableName() + "";
//		rs = adapter.executeQuery(sql);
//
//		try {
//			if (rs != null) {
//				rs.beforeFirst();
//
//				while (rs.next()) {
//					ret.add(rs.getString("Aufstellungsname"));
//				}
//			}
//		} catch (Exception e) {
//			HOLogger.instance().log(getClass(), "DatenbankZugriff.getAufstellungsListe: " + e);
//		}
//
//		return ret;
//	}
//
//	/**
//	 * Gibt eine Liste aller Usergespeicherten Aufstellungsnamen zurück
//	 */
//	Vector<String> getUserAufstellungsListe() {
//		ResultSet rs = null;
//		final String statement = "SELECT Aufstellungsname FROM " + getTableName()
//				+ " WHERE HRF_ID=" + Lineup.NO_HRF_VERBINDUNG;
//		final Vector<String> ret = new Vector<String>();
//
//		try {
//			rs = adapter.executeQuery(statement);
//
//			if (rs != null) {
//				rs.beforeFirst();
//
//				while (rs.next()) {
//					ret.add(rs.getString("Aufstellungsname"));
//				}
//			}
//		} catch (Exception e) {
//			HOLogger.instance().log(getClass(), "DatenbankZugriff.getUserAufstellungsListe: " + e);
//		}
//
//		return ret;
//	}

	/**
	 * speichert die Aufstellung und die aktuelle Aufstellung als STANDARD
	 * 
	 * @throws SQLException 
	 */
	void saveAufstellung(int sourceSystem, int hrfId, Lineup lineup, String name) throws SQLException {
		String statement = null;

		if (lineup != null) {

			DBManager.instance().deleteAufstellung(hrfId, name);

			// insert vorbereiten
			statement = "INSERT INTO " + getTableName()
					+ " ( SourceSystem, HRF_ID, Kicker, Kapitaen, Attitude, Tactic, StyleOfPlay, Aufstellungsname ) VALUES("
					+ sourceSystem + ","
					+ hrfId + ","
					+ lineup.getKicker() + ","
					+ lineup.getCaptain() + ","
					+ lineup.getAttitude() + ","
					+ lineup.getTacticType() + ","
					+ lineup.getStyleOfPlay() + ",'"
					+ name + "' )";

			adapter.executeUpdate(statement);

			// Standard sys saven
			DBManager.instance().saveSystemPositionen(hrfId, lineup.getPositionen(), name);

			// Save Substitutions
			((MatchSubstitutionTable)(DBManager.instance().getTable(MatchSubstitutionTable.TABLENAME)))
					.storeMatchSubstitutionsByHrf(sourceSystem, hrfId,lineup.getSubstitutionList(), name);
			
			// save penalty takers
			((PenaltyTakersTable)(DBManager.instance().getTable(PenaltyTakersTable.TABLENAME)))
				.storePenaltyTakers(name, lineup.getPenaltyTakers());
		}
	}
}
