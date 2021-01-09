package module.series.promotion;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import core.module.config.ModuleConfig;
import core.util.HOLogger;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyStore;
import java.util.*;
import java.util.function.Function;

/**
 * Implementation of {@link DataSubmitter} based on OkHttp.
 */
public class HttpDataSubmitter implements DataSubmitter {

    private final static String HOSERVER_BASEURL = ModuleConfig.instance()
            .getString("PromotionStatus_HoServer",
                    "https://UNF6X7OJB7PFLVEQ.anvil.app/_/private_api/HN4JZ6UMWUM7I4PTILWZTJFD");

    // Singleton.
    private HttpDataSubmitter() {}

    private static HttpDataSubmitter instance = null;

    public static HttpDataSubmitter instance() {
        if (instance == null) {
            instance = new HttpDataSubmitter();
        }

        return instance;
    }

    public List<Integer> fetchSupportedLeagues() {
        try {
            final OkHttpClient client = initializeHttpsClient();

            Request request = new Request.Builder()
                    .url(HOSERVER_BASEURL + "/league/supported")
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
                    "Error fetching data for league supported: " + e.getMessage()
            );
        }

        return Collections.emptyList();
    }

    @Override
    public void getLeagueStatus(int leagueId, Function<String, Void> callback) {

        try {
            final OkHttpClient client = initializeHttpsClient();

            Request request = new Request.Builder()
                    .url(String.format(HOSERVER_BASEURL + "/league/%s/status", leagueId))
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String body = response.body().string();

                response.close();
                callback.apply(body);
            } else {
                response.close();
            }
        } catch (Exception e) {
            HOLogger.instance().error(
                    HttpDataSubmitter.class,
                    "Error getting data for league status: " + e.getMessage()
            );
        }
    }

    @Override
    public void submitData(BlockInfo blockInfo, String json) {
        HOLogger.instance().info(HttpDataSubmitter.class, "Sending data to HO Server...");

        try {
            final OkHttpClient client = initializeHttpsClient();
            // Send bytes (rather than String) to avoid charset being appended to Content-Type
            // which messes up with server.
            final RequestBody body = RequestBody.create(json.getBytes(), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(String.format(HOSERVER_BASEURL + "/league/%s/block/%s/push", blockInfo.leagueId, blockInfo.blockId))
                    .post(body)
                    .build();

            Call call = client.newCall(request);
            Response response = call.execute();

            if (response.isSuccessful()) {
                HOLogger.instance().info(HttpDataSubmitter.class,
                        "Got HTTP response with status " + response.code() + " " + response.message());
            } else {
                HOLogger.instance().error(HttpDataSubmitter.class, "Timestamp: " + System.currentTimeMillis());
                HOLogger.instance().error(HttpDataSubmitter.class, "Error submitting data to HO Server: " + response.body().string());
            }

            response.close();

        } catch (Exception e) {
            e.printStackTrace();
            HOLogger.instance().error(HttpDataSubmitter.class, e.getMessage());
        }
    }

    @Override
    public Optional<BlockInfo> lockBlock(int leagueId) {
        HOLogger.instance().info(HttpDataSubmitter.class, String.format("Lock block for league %s...", leagueId));

        try {
            final OkHttpClient client = initializeHttpsClient();

            Request request = new Request.Builder()
                    .url(String.format(HOSERVER_BASEURL + "/league/%s/next-block?accept-job=true", leagueId))
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            List<Integer> series = new ArrayList<>();
            if (response.isSuccessful()) {
                String body = response.body().string();

                Gson gson = new Gson();
                JsonObject obj = gson.fromJson(body, JsonObject.class);

                HOLogger.instance().debug(HttpDataSubmitter.class, obj.toString());

                /*

                201 => "No more data required for league {leagueID}"
                202 => "Currently, league {leagueID} has no block available for treatment"
                200 => "Job awarded for league {leagueID} / Block ID {blockID}", "BlockID":
                203 => Job NOT awarded for league {leagueID} / Block ID {blockID}", "BlockID"

                 */
                if (obj.get("HTTP Status Code").getAsInt() == 200) {
                    JsonArray array = obj.get("BlockContent").getAsJsonArray();

                    for (JsonElement elt : array) {
                        series.add(elt.getAsInt());
                    }

                    BlockInfo blockInfo = new BlockInfo();
                    blockInfo.blockId = obj.get("BlockID").getAsInt();
                    blockInfo.series = series;
                    blockInfo.leagueId = leagueId;
                    blockInfo.status = 200;

                    HOLogger.instance().info(HttpDataSubmitter.class, "Block locked: " + blockInfo.blockId);

                    response.close();
                    return Optional.of(blockInfo);
                } else {
                    BlockInfo blockInfo = new BlockInfo();
                    blockInfo.status = obj.get("HTTP Status Code").getAsInt();

                    response.close();
                    return Optional.of(blockInfo);
                }
            }

        } catch (Exception e) {
            HOLogger.instance().error(
                    HttpDataSubmitter.class,
                    "Error locking block: " + e.getMessage()
            );
        }

        return Optional.empty();
    }

    @Override
    public Optional<String> getPromotionStatus(int leagueId, int teamId) {

        try {
            final OkHttpClient client = initializeHttpsClient();

            final String requestUrl = String.format(HOSERVER_BASEURL + "/league/%s/team/%s/pd-status", leagueId, teamId);
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String promotionStatus = response.body().string();
                HOLogger.instance().info(HttpDataSubmitter.class, "Status: " + promotionStatus);

                response.close();
                return Optional.of(promotionStatus);
            } else {
                response.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            HOLogger.instance().error(
                    HttpDataSubmitter.class,
                    "Error retrieving promotion status: " + e.getMessage()
            );
        }

        return Optional.empty();
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
