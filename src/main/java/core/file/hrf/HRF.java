// %214693493:de.hattrickorganizer.model%
package core.file.hrf;

import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;


/**
 * Benutzerdaten
 */
public final class HRF {
	
    //~ Instance fields ----------------------------------------------------------------------------

	private int hrfId = -1;
	private HODateTime datum = HODateTime.now();
	private String name = null;

    //~ Constructors -------------------------------------------------------------------------------

	/**
	 * Creates a new Hrf object.
	 *
	 */
	public HRF() {						
	}
	
	/**
	 * Creates a new Hrf object.
	 * 
	 * @param _hrfId
	 * @param _name
	 * @param _datum
	 */
	public HRF(int _hrfId, String _name, HODateTime _datum) {
		this.hrfId = _hrfId;
		this.name = _name;
		this.datum = _datum;
	}

	public HRF(int hrfId, HODateTime fetchDate){
		this.hrfId=hrfId;
		this.datum = fetchDate;
		this.name = datum.toString();
	}

    /**
     * Creates a new Hrf object.
     */
    public HRF(java.sql.ResultSet rs) throws Exception {
        try {
            hrfId = rs.getInt("HRF_ID");
            name = core.db.DBManager.deleteEscapeSequences(rs.getString("Name"));
            datum = HODateTime.fromDbTimestamp(rs.getTimestamp("Datum"));
        } catch (Exception e) {
            HOLogger.instance().error(getClass(),"Konstruktor HRF: " + e);
        }
    }

    //~ Methods ------------------------------------------------------------------------------------


	public HODateTime getDatum() {
		return datum;
	}

	public int getHrfId() {
		return hrfId;
	}

	public boolean isOK(){
    	return hrfId>=0;
	}

	public String getName() {
		return name;
	}

}
