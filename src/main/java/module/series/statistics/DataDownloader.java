package module.series.statistics;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import core.net.Connector;
import core.net.MyConnector;
import core.util.HOLogger;
import module.series.promotion.HttpDataSubmitter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataDownloader {

    private final static String ALLTID_SERVER_BASEURL = "https://hattid.com/api";
    private final static String POWERRATING_ENDPOINT = "/leagueUnit/%s/teamPowerRatings?page=0&pageSize=8&sortBy=power_rating&sortDirection=asc&statType=statRound&statRoundNumber=%s&season=%s";

    // Singleton.
    private DataDownloader() {}

    private static DataDownloader instance = null;

    public static DataDownloader instance() {
        if (instance == null) {
            instance = new DataDownloader();
        }

        return instance;
    }

    public List<Integer> fetchLeagueTeamPowerRatings(int iLeagueID, int iHTWeek, int iHTSeason) {
        try {
            final OkHttpClient client = new OkHttpClient();

            String url = ALLTID_SERVER_BASEURL + String.format(POWERRATING_ENDPOINT, iLeagueID, iHTWeek, iHTSeason);


            System.out.println(url);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            List<Integer> supportedLeagues = new ArrayList<>();

            if (response.isSuccessful()) {
                String bodyAsString = response.body().string();
                Gson gson = new Gson();
                JsonArray array = gson.fromJson(bodyAsString, JsonArray.class);

                for (JsonElement arr : array) {
                    supportedLeagues.add(arr.getAsJsonArray().get(0).getAsInt());
                }

                response.close();
                return supportedLeagues;
            }

            response.close();
        } catch (Exception e) {
            HOLogger.instance().error(
                    HttpDataSubmitter.class,
                    "Error fetching data from Alltid for league team power ratings: " + e.getMessage()
            );
        }

        return Collections.emptyList();
    }


}
