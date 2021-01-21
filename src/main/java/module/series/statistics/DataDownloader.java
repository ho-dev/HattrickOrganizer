package module.series.statistics;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import core.model.enums.RatingsStatistics;
import core.module.config.ModuleConfig;
import core.util.HOLogger;
import module.series.promotion.HttpDataSubmitter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.*;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyStore;
import java.util.*;

public class DataDownloader {

    private final static String ALLTID_SERVER_BASEURL = "https://hattid.com/api";
    private final static String POWERRATING_ENDPOINT  = "/leagueUnit/%s/teamPowerRatings?page=0&pageSize=8&sortBy=power_rating&sortDirection=asc&statType=statRound&statRoundNumber=%s&season=%s";
    private final static String HATSTATS_ENDPOINT     = "/leagueUnit/%s/teamHatstats?page=0&pageSize=8&sortBy=hatstats&sortDirection=asc&statType=%s&season=%s";

    // Singleton.
    private DataDownloader() {
    }

    private static DataDownloader instance = null;

    public static DataDownloader instance() {
        if (instance == null) {
            instance = new DataDownloader();
        }

        return instance;
    }


    /**
     * Fetch league statistics (power rating and HatStats) from Alltid website for display in League panel
     */

    public Map<Integer, Map<RatingsStatistics, Integer>> fetchLeagueStatistics(int iLeagueID, int iHTWeek, int iHTSeason){

        Map<Integer, Map<RatingsStatistics, Integer>> resultsMap = new HashMap<>();

        Map<Integer, Integer> powerRatings = fetchLeagueTeamPowerRatings(iLeagueID, iHTWeek, iHTSeason);
        if (powerRatings == null) {
            return resultsMap;
        }
        Map<Integer, Map<RatingsStatistics, Integer>> hatStatsMax = fetchLeagueTeamHatStats(iLeagueID, iHTSeason, "max");
        Map<Integer, Map<RatingsStatistics, Integer>> hatStatsAvg = fetchLeagueTeamHatStats(iLeagueID, iHTSeason, "avg");

        Set<Integer> teamIDs = new HashSet(powerRatings.keySet());
        teamIDs.addAll(hatStatsMax.keySet());
        teamIDs.addAll(hatStatsAvg.keySet());

        for(Integer teamID : teamIDs){
            Map<RatingsStatistics, Integer> teamStats = new HashMap<>();

            if (powerRatings.containsKey(teamID)) {
                teamStats.put(RatingsStatistics.POWER_RATINGS, powerRatings.get(teamID));
            }

            if (hatStatsMax.containsKey(teamID)) {
                hatStatsMax.get(teamID).forEach(
                        (key, value) -> teamStats.merge(key, value, (v1, v2) -> v1));
            }

            if (hatStatsAvg.containsKey(teamID)) {
                hatStatsAvg.get(teamID).forEach(
                        (key, value) -> teamStats.merge(key, value, (v1, v2) -> v1));
            }

            resultsMap.put(teamID, teamStats);
        }


        return resultsMap;
}

    public Map<Integer, Map<RatingsStatistics, Integer>> fetchLeagueTeamHatStats(int iLeagueID, int iHTSeason, String dataType) {

        Map<Integer, Map<RatingsStatistics, Integer>> result = new HashMap<>();

        try {
            final OkHttpClient client = initializeHttpsClient();

            String url = ALLTID_SERVER_BASEURL + String.format(HATSTATS_ENDPOINT, iLeagueID, dataType, iHTSeason);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();


            if (response.isSuccessful()) {
                String bodyAsString = response.body().string();
                Gson gson = new Gson();
                JsonObject output = gson.fromJson(bodyAsString, JsonObject.class);

                response.close();

                for (var entity : output.getAsJsonArray("entities")){
                    Map<RatingsStatistics, Integer> teamStat = new HashMap<>();
                    int iTeamID = ((JsonObject) entity).get("teamId").getAsInt();

                    int rating = ((JsonObject) entity).get("hatStats").getAsInt();
                    teamStat.put(RatingsStatistics.getCode("total", dataType), rating);

                    rating = ((JsonObject) entity).get("midfield").getAsInt();
                    teamStat.put(RatingsStatistics.getCode("mid", dataType), rating*3);

                    rating = ((JsonObject) entity).get("defense").getAsInt();
                    teamStat.put(RatingsStatistics.getCode("def", dataType), rating);

                    rating = ((JsonObject) entity).get("attack").getAsInt();
                    teamStat.put(RatingsStatistics.getCode("off", dataType), rating);

                    result.put(iTeamID, teamStat);
                }

            }

        } catch (Exception e) {
            HOLogger.instance().error(
                    HttpDataSubmitter.class,
                    "Error fetching data from Alltid for league team hatstats: " + e.getMessage()
            );
            return null;
        }


        return result;
    }


    public Map<Integer, Integer> fetchLeagueTeamPowerRatings(int iLeagueID, int iHTWeek, int iHTSeason) {

        Map<Integer, Integer> result = new HashMap<>();

        try {
            final OkHttpClient client = initializeHttpsClient();

            String url = ALLTID_SERVER_BASEURL + String.format(POWERRATING_ENDPOINT, iLeagueID, iHTWeek, iHTSeason);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();


            if (response.isSuccessful()) {
                String bodyAsString = response.body().string();
                Gson gson = new Gson();
                JsonObject output = gson.fromJson(bodyAsString, JsonObject.class);

                response.close();

                for (var entity : output.getAsJsonArray("entities")){

                    int iTeamID = ((JsonObject)((JsonObject) entity).get("teamSortingKey")).get("teamId").getAsInt();
                    int iPowerRating = ((JsonObject) entity).get("powerRating").getAsInt();
                    result.put(iTeamID, iPowerRating);
                }

            }

        } catch (Exception e) {
            HOLogger.instance().error(
                    HttpDataSubmitter.class,
                    "Error fetching data from Alltid for league team power ratings: " + e.getMessage()
            );
            return null;
        }


        return result;
    }

    private OkHttpClient initializeHttpsClient() throws Exception {
        char[] keystoreCred = new String(Base64.getDecoder().decode("aGVsbG9oYXR0cmljaw==")).toCharArray();
        final InputStream trustStoreStream = this.getClass().getClassLoader().getResourceAsStream("truststore.jks");

        final KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(trustStoreStream, keystoreCred);

        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, keystoreCred);
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);

        final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        final X509TrustManager trustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];

        int proxyPort = 3000;
        String proxyHost = "localhost";

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager);

        if (ModuleConfig.instance().getBoolean("PromotionStatus_DebugProxy", false)) {
            builder = builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
        }

        return builder.build();
    }
}
