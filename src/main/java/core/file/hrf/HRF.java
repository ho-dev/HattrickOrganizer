// %214693493:de.hattrickorganizer.model%
package core.file.hrf;

import core.util.HODateTime;
import core.util.HOLogger;

/**
 * hattrick/HO file information
 */
public final class HRF {
	
    //~ Instance fields ----------------------------------------------------------------------------

	private int hrfId = -1;
	private HODateTime datum = HODateTime.now();

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
	 * @param _hrfId database key
	 * @param _datum creation date is used as filename
	 */
	public HRF(int _hrfId,  HODateTime _datum) {
		this.hrfId = _hrfId;
		this.datum = _datum;
	}

    /**
     * Creates a new Hrf object.
     */
    public HRF(java.sql.ResultSet rs) throws Exception {
        try {
            hrfId = rs.getInt("HRF_ID");
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
		return this.datum.toLocaleDateTime();
	}

}
