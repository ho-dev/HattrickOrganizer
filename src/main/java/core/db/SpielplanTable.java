package core.db;

import core.util.HOLogger;
import core.util.Helper;
import module.series.Spielplan;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;


final class SpielplanTable extends AbstractTable {
	final static String TABLENAME = "SPIELPLAN";
	
	protected SpielplanTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}
	

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[4];
		columns[0]= new ColumnDescriptor("LigaID",Types.INTEGER,false);
		columns[1]= new ColumnDescriptor("LigaName",Types.VARCHAR,true,256);
		columns[2]= new ColumnDescriptor("Saison",Types.INTEGER,false);
		columns[3]= new ColumnDescriptor("FetchDate",Types.TIMESTAMP,false);
	}

	/**
	 * lädt alle Spielpläne aus der DB
	 *
	 * @param mitPaarungen inklusive der Paarungen ja/nein
	 */
	Spielplan[] getAllSpielplaene(boolean mitPaarungen) {
		final Vector<Spielplan> vec = new Vector<Spielplan>();
		Spielplan plan = null;
		Spielplan[] plaene = null;
		String sql = null;
		ResultSet rs = null;

		try {
			sql = "SELECT * FROM "+getTableName();
			sql += " ORDER BY Saison DESC ";

			rs = adapter.executeQuery(sql);

			rs.beforeFirst();

			while (rs.next()) {
				// Plan auslesen
				plan = new Spielplan();

				plan.setFetchDate(rs.getTimestamp("FetchDate"));
				plan.setLigaId(rs.getInt("LigaID"));
				plan.setLigaName(rs.getString("LigaName"));
				plan.setSaison(rs.getInt("Saison"));

				vec.add(plan);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getSpielplan Error" + e);

			//HOLogger.instance().log(getClass(),e);
		}

		if (mitPaarungen) {
			for (int i = 0; i < vec.size(); i++) {
				//Einträge holen
				DBManager.instance().getPaarungen(((Spielplan) vec.get(i)));
			}
		}

		plaene = new Spielplan[vec.size()];
		Helper.copyVector2Array(vec, plaene);

		return plaene;
	}

	/**
	 * holt einen Spielplan aus der DB, -1 bei den params holt den zuletzt gesavten Spielplan
	 *
	 * @param ligaId Id der Liga
	 * @param saison die Saison
	 */
	Spielplan getSpielplan(int ligaId, int saison) {
		Spielplan plan = null;
		String sql = null;
		ResultSet rs = null;

		try {
			sql = "SELECT * FROM "+getTableName();

			if ((ligaId > -1) && (saison > -1)) {
				sql += (" WHERE LigaID = " + ligaId + " AND Saison = " + saison);
			}

			sql += " ORDER BY FetchDate DESC ";

			rs = adapter.executeQuery(sql);

			if(rs.first()){

			// Plan auslesen
			plan = new Spielplan();

			plan.setFetchDate(rs.getTimestamp("FetchDate"));
			plan.setLigaId(rs.getInt("LigaID"));
			plan.setLigaName(rs.getString("LigaName"));
			plan.setSaison(rs.getInt("Saison"));

			//Einträge holen
			DBManager.instance().getPaarungen(plan);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getSpielplan Error" + e);

			HOLogger.instance().log(getClass(),e);
			plan = null;
		}

		return plan;
	}

	/**
	 * Gibt eine Ligaid zu einer Seasonid zurück, oder -1, wenn kein Eintrag in der DB gefunden
	 * wurde
	 */
	int getLigaID4SaisonID(int seasonid) {
		int ligaid = -1;

		try {
			// in the event of league changes, there may be several entries per season=> select the newest
			final String sql = "SELECT LigaID FROM "+getTableName()+" WHERE Saison=" + seasonid + " ORDER BY FETCHDATE DESC";
			final ResultSet rs = adapter.executeQuery(sql);

			if (rs.first()) {
				ligaid = rs.getInt("LigaID");
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getLigaID4SeasonID : " + e);
		}

		return ligaid;
	}
	
	/**
	 * speichert einen Spielplan mitsamt Paarungen
	 */
	void storeSpielplan(Spielplan plan) {
		if (plan != null) {
			try {
				boolean update = false;

				try {
					// at the end of a season the league could be changed in case of
					// league relegation, rise or manager in lower leagues could select new leagues

					// check if update or create
					// first we have to check if there is a fixture of the current season
					String sql = "SELECT LigaID FROM "+getTableName()+" WHERE Saison = " + plan.getSaison();
					ResultSet result = adapter.executeQuery(sql);;
					result.first();
					int currentLeague = result.getInt("LigaID");
					if ( currentLeague != plan.getLigaId()){
						// if so, and the league id does NOT match, then this plan is not a plan of our team. ignore it!
						return;
					}
					// if the league id matches do an update
					update = true;
				} catch (Exception e) {
					// if no entry of the current season is found, create a new one
					update = false;
				}

				//Plan aktualisieren
				if (update) {
					String sql =
						"UPDATE "+getTableName()+" SET LigaName='"
							+ plan.getLigaName()
							+ "', FetchDate='"
							+ plan.getFetchDate().toString()
							+ "'"
							+ " WHERE LigaID="
							+ plan.getLigaId()
							+ " AND Saison="
							+ plan.getSaison();
					adapter.executeUpdate(sql);

					//neueintrag
				} else {
					//Erstmal Spielplan
					//insert vorbereiten
					String sql = "INSERT INTO "+getTableName()+" ( LigaID , LigaName , Saison, FetchDate ) VALUES(";
					sql += (plan.getLigaId() + "," + "'" + plan.getLigaName() + "'," + plan.getSaison() + ",'" + plan.getFetchDate().toString() + "'" + " )");
					adapter.executeUpdate(sql);
				}

				//Einträge noch saven
				DBManager.instance().storePaarung(plan.getEintraege(), plan.getLigaId(), plan.getSaison());
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),"DB.storeSpielplan Error" + e);
				HOLogger.instance().log(getClass(),e);
			}
		}
	}	
}
