package tool.hrfExplorer;

import core.util.HODateTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author KickMuck
 */
public class HrfFileDetails extends HrfDetails
{
	private String m_Dateiname;
	private String m_Pfad;
	
	private File m_Datei;
	private File m_Ordner;
	private ResultSet m_rs = null;
	
	private static FileReader fr;
    private static BufferedReader br;
    
    
	public HrfFileDetails(String pfad)
	{
		super();
		
		setPfad(pfad);
		
		m_Ordner = new File(pfad);
		if(m_Ordner.isFile())
		{
			setName(m_Ordner.getName());
		}
		m_Datei = new File(pfad);
		try
		{
			fr = new FileReader(m_Datei);
			br = new BufferedReader(fr);
			String zeile;
			
			while((zeile=br.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(zeile,"=");
				
				if(st.hasMoreElements())
				{
					String tmp_typ = st.nextElement().toString();
					if(tmp_typ.equalsIgnoreCase("date"))
					{
						setDatum(HODateTime.fromHT(st.nextElement().toString()));
						createDates();
					}
					else if(tmp_typ.equalsIgnoreCase("season"))
					{
						setSaison(Integer.parseInt(st.nextElement().toString()));
					}
					else if(tmp_typ.equalsIgnoreCase("teamID"))
					{
						setTeamID(Integer.parseInt(st.nextElement().toString()));
					}
					else if(tmp_typ.equalsIgnoreCase("teamName"))
					{
						setTeamName(st.nextElement().toString());
					}
					else if(tmp_typ.equalsIgnoreCase("serie"))
					{
						setLiga(st.nextElement().toString());
					}
					else if(tmp_typ.equalsIgnoreCase("spelade"))
					{
						setSpieltag(Integer.parseInt(st.nextElement().toString()));
					}
					else if(tmp_typ.equalsIgnoreCase("gjorda"))
					{
						setToreFuer(Integer.parseInt(st.nextElement().toString()));
					}
					else if(tmp_typ.equalsIgnoreCase("inslappta"))
					{
						setToreGegen(Integer.parseInt(st.nextElement().toString()));
					}
					else if(tmp_typ.equalsIgnoreCase("poang"))
					{
						setPunkte(Integer.parseInt(st.nextElement().toString()));
					}
					else if(tmp_typ.equalsIgnoreCase("placering"))
					{
						setPlatz(Integer.parseInt(st.nextElement().toString()));
					}
					else if(tmp_typ.equalsIgnoreCase("mvTranare"))
					{
						setAnzTwTrainer(Integer.parseInt(st.nextElement().toString()));
					}
					else if(tmp_typ.equalsIgnoreCase("hjTranare"))
					{
						setAnzCoTrainer(Integer.parseInt(st.nextElement().toString()));
					}
					else if(tmp_typ.equalsIgnoreCase("fanclub"))
					{
						setFans(Integer.parseInt(st.nextElement().toString()));
					}
					else if(tmp_typ.equalsIgnoreCase("trLevel"))
					{
						setTrInt(Integer.parseInt(st.nextElement().toString()));
					}
					else if(tmp_typ.equalsIgnoreCase("trTypeValue"))
					{
						setTrArtInt(Integer.parseInt(st.nextElement().toString()));
					}
					else if(tmp_typ.equalsIgnoreCase("stamningValue"))
					{
						setStimmung(Integer.parseInt(st.nextElement().toString()));
					}
					else if(tmp_typ.equalsIgnoreCase("sjalvfortroendeValue"))
					{
						setSelbstvertrauen(Integer.parseInt(st.nextElement().toString()));
					}
					else if(tmp_typ.equalsIgnoreCase("ald"))
					{
						setAnzSpieler(getAnzSpieler() + 1);
					}
				}
			}
			br.close();
		}
		catch(IOException ignored)
		{
			
		}
		setTrArt(getTrArtInt());
		calcDatum();
		
	}
	/**
	 * @return Returns all needed Values as Vector
	 */
	@SuppressWarnings("unchecked")
	public Vector getDatenVector()
	{
		Vector daten = new Vector();
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
		daten.add(getPfad());
		
		return daten;
	}

	/**
	 * @return Returns the m_Pfad.
	 */
	public String getPfad()
	{
		return m_Pfad;
	}
	/**
	 * @return Returns the m_Name.
	 */
	public String getName()
	{
		return m_Dateiname;
	}
	/**
	 * @param pfad The m_Pfad to set.
	 */
	public void setPfad(String pfad) {
		m_Pfad = pfad;
	}
	/**
	 * @param dateiname The m_Dateiname to set.
	 */
	public void setName(String dateiname) {
		m_Dateiname = dateiname;
	}

	public String getFilename() {
		return getDatum().toHT().substring(0,10);
	}
}
