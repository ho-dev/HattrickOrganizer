/*
 * Created on 10.05.2005
 */
package tool.hrfExplorer;

import core.db.DBManager;
import core.util.HODateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 * @author KickMuck
 */

class HrfDbDetails extends HrfDetails {
	String m_name;
	int m_hrf_ID;
	private ResultSet m_result;
	
	HrfDbDetails(int id) {
		super();
		
		m_result = DBManager.instance().getAdapter().executeQuery("SELECT NAME,DATUM,LIGANAME,PUNKTE,TOREFUER,TOREGEGEN,PLATZ,TEAMID,TEAMNAME,SPIELTAG,SAISON,TRAININGSINTENSITAET,TRAININGSART,ISTIMMUNG,ISELBSTVERTRAUEN,COTRAINER,FANS,HRF_ID,(SELECT COUNT(*) FROM SPIELER WHERE HRF_ID = '" + id + "') AS \"ANZAHL\" FROM HRF a, LIGA b, BASICS c, TEAM d, VEREIN e WHERE a.HRF_ID = '" + id + "' AND b.HRF_ID=a.HRF_ID AND c.HRF_ID=a.HRF_ID AND d.HRF_ID=a.HRF_ID AND e.HRF_ID=a.HRF_ID");
		try {
			while(true)	{
				assert m_result != null;
				if (!m_result.next()) break;
				m_result.getObject(1);
				
				if( m_result.wasNull())	{
					//HrfExplorer.appendText("Query war NULL");
				}
				else
				{
					HrfExplorer.appendText("Query war nicht NULL");
					setName("---");
//					setStr_Datum(m_result.getObject("DATUM").toString().substring(0,19));
					setDatum(HODateTime.fromDbTimestamp(m_result.getTimestamp("DATUM")));
					createDates();
					setLiga(m_result.getString("LIGANAME"));
					setPunkte(m_result.getInt("PUNKTE"));
					setToreFuer(m_result.getInt("TOREFUER"));
					setToreGegen(m_result.getInt("TOREGEGEN"));
					setPlatz(m_result.getInt("PLATZ"));
					setTeamID(m_result.getInt("TEAMID"));
					setTeamName(m_result.getString("TEAMNAME"));
					setSpieltag(m_result.getInt("SPIELTAG"));
					setSaison(m_result.getInt("SAISON"));
					setTrInt(m_result.getInt("TRAININGSINTENSITAET"));
					setTrArtInt(m_result.getInt("TRAININGSART"));
					setStimmung(m_result.getInt("ISTIMMUNG"));
					setSelbstvertrauen(m_result.getInt("ISELBSTVERTRAUEN"));
					setAnzCoTrainer(m_result.getInt("COTRAINER"));
					setFans(m_result.getInt("FANS"));
					setHrf_ID(m_result.getInt("HRF_ID"));
					setTrArt(getTrArtInt());
					setAnzSpieler(m_result.getInt("ANZAHL"));
				}
			}
		}
		catch(SQLException ignored)
		{
			
		}
		calcDatum();
	}
	
	/**
	 * @return Returns all needed Values as Vector
	 */
	Vector<Object> getDatenVector()
	{
		Vector<Object> daten = new Vector<>();
		daten.add(Boolean.FALSE);
		daten.add(getName());
		daten.add(getDatum());
		daten.add(getWochentag());
		daten.add(getKw());
		daten.add(getSaison());
		daten.add(getLiga());
		daten.add(getTrArt());
		daten.add(getTrInt());
		daten.add(getBild());
		daten.add(getHrf_ID());
		
		return daten;
	}
	/**
	 * @return Returns the m_hrf_ID.
	 */
	int getHrf_ID() {
		return m_hrf_ID;
	}
	/**
	 * @return Returns the m_name.
	 */
	String getName() {
		return m_name;
	}
	/**
	 * @param m_hrf_id The m_hrf_ID to set.
	 */
	void setHrf_ID(int m_hrf_id) {
		m_hrf_ID = m_hrf_id;
	}
	/**
	 * @param m_name The m_name to set.
	 */
	void setName(String m_name) {
		this.m_name = m_name;
	}
}
