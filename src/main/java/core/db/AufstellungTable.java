package core.db;

import core.model.player.MatchRoleID;
import core.util.HOLogger;
import module.lineup.Lineup;
import module.lineup.substitution.model.Substitution;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

final class AufstellungTable extends AbstractTable {
	final static String TABLENAME = "AUFSTELLUNG";

	protected AufstellungTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[7];
		columns[0] = new ColumnDescriptor("HRF_ID", Types.INTEGER, false);
		columns[1] = new ColumnDescriptor("Kicker", Types.INTEGER, false);
		columns[2] = new ColumnDescriptor("Kapitaen", Types.INTEGER, false);
		columns[3] = new ColumnDescriptor("Attitude", Types.INTEGER, false);
		columns[4] = new ColumnDescriptor("Tactic", Types.INTEGER, false);
		columns[5] = new ColumnDescriptor("Aufstellungsname", Types.VARCHAR, false, 256);
		columns[6] = new ColumnDescriptor("StyleOfPlay", Types.INTEGER, false);
	}

	/**
	 * lädt System Positionen
	 */
	Lineup getAufstellung(int hrfID, String name) {
		ResultSet rs = null;
		module.lineup.Lineup auf = null;
		String sql = null;

		sql = "SELECT * FROM " + getTableName() + " WHERE HRF_ID = " + hrfID
				+ " and Aufstellungsname ='" + name + "'";
		rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				rs.first();

				auf = new module.lineup.Lineup();
				auf.setKapitaen(rs.getInt("Kapitaen"));
				auf.setKicker(rs.getInt("Kicker"));
				auf.setTacticType(rs.getInt("Tactic"));
				auf.setAttitude(rs.getInt("Attitude"));
				auf.setStyleOfPlay(rs.getInt("StyleOfPlay"));
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getAufstellung: " + e);
		}

		if (auf != null) {
			auf.setPositionen(DBManager.instance().getSystemPositionen(hrfID, name));
			auf.setSubstitionList(new ArrayList<>(DBManager.instance()
					.getMatchSubstitutionsByHrf(hrfID, name)));
			auf.setPenaltyTakers(DBManager.instance().getPenaltyTakers(name));
			auf.setRatings();
		}

		return auf;
	}

	/**
	 * gibt liste für Aufstellungen
	 * 
	 * @param hrfID
	 *            -1 für default = hrf unabhängig
	 */
	Vector<String> getAufstellungsListe(int hrfID) {
		final Vector<String> ret = new Vector<String>();
		ResultSet rs = null;
		String sql = null;

		sql = "SELECT Aufstellungsname FROM " + getTableName() + "";
		rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				rs.beforeFirst();

				while (rs.next()) {
					ret.add(rs.getString("Aufstellungsname"));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getAufstellungsListe: " + e);
		}

		return ret;
	}

	/**
	 * Gibt eine Liste aller Usergespeicherten Aufstellungsnamen zurück
	 */
	Vector<String> getUserAufstellungsListe() {
		ResultSet rs = null;
		final String statement = "SELECT Aufstellungsname FROM " + getTableName()
				+ " WHERE HRF_ID=" + Lineup.NO_HRF_VERBINDUNG;
		final Vector<String> ret = new Vector<String>();

		try {
			rs = adapter.executeQuery(statement);

			if (rs != null) {
				rs.beforeFirst();

				while (rs.next()) {
					ret.add(rs.getString("Aufstellungsname"));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getUserAufstellungsListe: " + e);
		}

		return ret;
	}

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
					+ lineup.getKapitaen() + ","
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
