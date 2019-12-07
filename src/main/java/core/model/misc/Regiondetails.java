package core.model.misc;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class Regiondetails {

    private Timestamp m_clFetchDatum;
    private int m_iNationId = -1;
    private String m_sNation = "";
    private int m_iRegionId = -1;
    private String m_sRegionName = "";
    private int m_iNumberOfUsers = -1;
    private int m_iNumberOfOnlineUsers = -1;
    private int m_iWeatherId = -1;
    private int m_iTomorrowWeatherId = -1;

    public Regiondetails(Map<String, String> regionDetails) {
        this.m_clFetchDatum = Timestamp.from(Instant.parse( regionDetails.get("FetchedDate") ));
        this.m_iNationId =  Integer.parseInt(regionDetails.get("LeagueID"));
        this.m_iNumberOfOnlineUsers = Integer.parseInt(regionDetails.get("NumberOfOnline"));
        this.m_iNumberOfUsers = Integer.parseInt(regionDetails.get("NumberOfUsers"));
        this.m_iRegionId = Integer.parseInt(regionDetails.get("RegionID"));
        this.m_iTomorrowWeatherId = Integer.parseInt(regionDetails.get("TomorrowWeatherID"));
        this.m_iWeatherId = Integer.parseInt(regionDetails.get("WeatherID"));

        this.m_sNation = regionDetails.get("LeagueName");
        this.m_sRegionName = regionDetails.get("RegionName");
    }
}
