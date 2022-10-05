// %214693493:de.hattrickorganizer.model%
package core.file.hrf;

import core.db.AbstractTable;
import core.util.HODateTime;

/**
 * hattrick/HO file information
 */
public final class HRF extends AbstractTable.Storable {
	
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

	public void setHrfId(int v) {
		this.hrfId = v;
	}

	public void setDatum(HODateTime v) {
		this.datum=v;
	}
}
