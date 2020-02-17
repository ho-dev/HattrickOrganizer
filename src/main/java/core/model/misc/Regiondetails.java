package core.model.misc;

import core.model.match.Weather;
import core.util.Helper;

import java.sql.Time;
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
    private Weather m_iWeather = Weather.NULL;
    private Weather m_iWeatherTomorrow = Weather.NULL;

    public Regiondetails(Map<String, String> regionDetails) {
        String fDate = regionDetails.get("FetchedDate");
        this.m_clFetchDatum = Helper.parseDate(fDate);
        this.m_iNationId =  Integer.parseInt(regionDetails.get("LeagueID"));
        this.m_iNumberOfOnlineUsers = Integer.parseInt(regionDetails.get("NumberOfOnline"));
        this.m_iNumberOfUsers = Integer.parseInt(regionDetails.get("NumberOfUsers"));
        this.m_iRegionId = Integer.parseInt(regionDetails.get("RegionID"));
        this.m_iWeatherTomorrow = Weather.getById(Integer.parseInt(regionDetails.get("TomorrowWeatherID")));
        this.m_iWeather = Weather.getById(Integer.parseInt(regionDetails.get("WeatherID")));
        this.m_sNation = regionDetails.get("LeagueName");
        this.m_sRegionName = regionDetails.get("RegionName");
    }

    public Timestamp getFetchDatum(){return this.m_clFetchDatum;}
    public Weather getWeather(){return this.m_iWeather;}
    public Weather getWeatherTomorrow(){return this.m_iWeatherTomorrow;}
}
