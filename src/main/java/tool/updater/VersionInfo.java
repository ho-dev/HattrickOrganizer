package tool.updater;

import core.HO;
import core.util.HOLogger;

import java.text.*;
import java.util.Date;
import java.util.Locale;


/**
 * Simple data class to store version related information.
 */
public class VersionInfo {

	private double version;
	private int build;
	private Date released;
	private String versionType;
	final static private DecimalFormat DECF = new DecimalFormat("0.000##");
	final static private DateFormat DATF = new SimpleDateFormat("dd.MM.yyyy");

	
	static {
		DecimalFormatSymbols ds = new DecimalFormatSymbols();
		ds.setDecimalSeparator('.');
		DECF.setDecimalFormatSymbols(ds);
		DECF.setGroupingUsed(false);
	}

	/**
	 * Get a human readable version info string.
	 */
	public String getVersionString() {
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		nf.setMinimumFractionDigits(3);
		String txt = nf.format(HO.VERSION);

		if (versionType=="BETA") {
			txt += " BETA (r" + build + ")";
		}

		else if (versionType=="DEV") {
			txt += " DEV (r" + build + ")";
		}

		return txt;
	}





	public double getVersion() {
		return version;
	}

	public String getversionType() {
		return versionType;
	}

	public void setAllButReleaseDate(String sVERSION) {
		String[] aVersion = sVERSION.split("\\.");

		this.version = Double.parseDouble(aVersion[0] + "." + aVersion[1]);
		this.build = Integer.parseInt(aVersion[3]);
		switch (aVersion[2]) {
			case "0":
				this.versionType = "DEV";
				break;
			case "1":
				this.versionType = "BETA";
				break;
			default:
				this.versionType = "RELEASE";
				break;
		}
	}


	public int getBuild() {
		return build;
	}


	public String getReleaseDate() {
		return released != null ? DATF.format(released) : "";
	}

	
	public void setReleasedDate(Date released) {
		this.released = released;
	}


	// generic setter, example:
	// version=1.436.0.43
	// released=31.05.2018
	public void setValue(final String key, final String val) {
		try {
			if ("version".equals(key)) {
				setAllButReleaseDate(val);
			}
			else if ("released".equals(key)) {
				setReleasedDate(DATF.parse(val));
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "Error parsing " + key + " / " + val + " : " + e);
		}
	}

	public boolean isValid() {
		return (version > 0d);
	}

	@Override
	public String toString() {
		return "VersionInfo [version=" + version + ",  version type=" + versionType + ", build=" + build + ", released=" + released +"]";
	}
}
