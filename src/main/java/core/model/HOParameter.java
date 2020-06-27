package core.model;

import core.db.DBManager;
import java.util.HashMap;
import java.util.Map;

/**
 * User configuration. Loaded when HO starts and saved when HO! exits.
 */
public final class HOParameter extends Configuration {
    //~ Static fields/initializers -----------------------------------------------------------------

    private static HOParameter m_clUserParameter;

	//~ Constructors -------------------------------------------------------------------------------

	/**
	 * Creates a new HOParameter object.
	 */
	private HOParameter() {
	}

	//~ Methods ------------------------------------------------------------------------------------

	/**
	 * @return singelton instance
	 */
	public static HOParameter instance() {
		if (m_clUserParameter == null) {
			m_clUserParameter = new HOParameter();
		}

		return m_clUserParameter;
	}

    //------Konstanten-----------------------------------------------
	public int lastNews = -1;
	public float EpvRelease = 1f;
	public float RatingsRelease = 1f;
    public int DBVersion = DBManager.getVersion();

    /** @deprecated since HO! 1.431 */
    @Deprecated
    public int HOTotalUsers = 0;

    /** @deprecated since HO! 1.431 */
    @Deprecated
    public int HOUsers = 0;

	@Override
	public Map<String,String> getValues() {
		Map<String,String> map = new HashMap<>();
		map.put("lastNews",String.valueOf(lastNews));
		map.put("EpvRelease",String.valueOf(EpvRelease));
		map.put("RatingsRelease",String.valueOf(RatingsRelease));
		map.put("DBVersion",String.valueOf(DBVersion));
		//map.put("HOTotalUsers",String.valueOf(HOTotalUsers));
		//map.put("HOUsers",String.valueOf(HOUsers));
		return map;
	}

	@Override
	public void setValues(Map<String,String> values) {
		DBVersion = getIntValue(values,"DBVersion");
		//HOTotalUsers = getIntValue(values,"HOTotalUsers");
		//HOUsers = getIntValue(values,"HOUsers");
		lastNews = getIntValue(values,"lastNews");
		EpvRelease = getFloatValue(values,"EpvRelease");
		RatingsRelease = getFloatValue(values,"RatingsRelease");
	}


}
