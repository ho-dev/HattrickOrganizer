package core.db;

import core.util.HOLogger;
import module.lineup.Lineup;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

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
				new ColumnDescriptor("StyleOfPlay", Types.INTEGER, false)
		};
	}


	Lineup getLineup(int hrfID, String name) {

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


	/**
	 * speichert die Aufstellung und die aktuelle Aufstellung als STANDARD
	 * 
	 * @throws SQLException 
	 */
	void saveAufstellung(int iMatchType, int hrfId, Lineup lineup, String name) throws SQLException {
		String statement;

		if (lineup != null) {

			DBManager.instance().deleteAufstellung(hrfId, name);

			statement = "INSERT INTO " + getTableName()
					+ " (HRF_ID, Kicker, Kapitaen, Attitude, Tactic, StyleOfPlay, Aufstellungsname ) VALUES("
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
					.storeMatchSubstitutionsByHrf(iMatchType, hrfId,lineup.getSubstitutionList(), name);
			
			// save penalty takers
			((PenaltyTakersTable)(DBManager.instance().getTable(PenaltyTakersTable.TABLENAME)))
				.storePenaltyTakers(name, lineup.getPenaltyTakers());
		}
	}
}
