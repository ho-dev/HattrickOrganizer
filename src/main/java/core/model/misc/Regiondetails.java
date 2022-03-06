package core.model.misc;

import core.model.match.Weather;
import core.util.HODateTime;
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

    private HODateTime m_clFetchDatum;
    private int m_iNationId;
    private String m_sNation;
    private int m_iRegionId;
    private String m_sRegionName;
    private int m_iNumberOfUsers;
    private int m_iNumberOfOnlineUsers;
    private Weather m_iWeather;
    private Weather m_iWeatherTomorrow;

    public Regiondetails(Map<String, String> regionDetails) {
        String fDate = regionDetails.get("FetchedDate");
        this.m_clFetchDatum = HODateTime.fromHT(fDate);
        this.m_iNationId =  Integer.parseInt(regionDetails.get("LeagueID"));
        this.m_iNumberOfOnlineUsers = Integer.parseInt(regionDetails.get("NumberOfOnline"));
        this.m_iNumberOfUsers = Integer.parseInt(regionDetails.get("NumberOfUsers"));
        this.m_iRegionId = Integer.parseInt(regionDetails.get("RegionID"));
        this.m_iWeatherTomorrow = Weather.getById(Integer.parseInt(regionDetails.get("TomorrowWeatherID")));
        this.m_iWeather = Weather.getById(Integer.parseInt(regionDetails.get("WeatherID")));
        this.m_sNation = regionDetails.get("LeagueName");
        this.m_sRegionName = regionDetails.get("RegionName");
    }

    public HODateTime getFetchDatum(){return this.m_clFetchDatum;}
    public Weather getWeather(){return this.m_iWeather;}
    public Weather getWeatherTomorrow(){return this.m_iWeatherTomorrow;}
}
