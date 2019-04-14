package tool.pluginFeedback;

import core.model.HOVerwaltung;
import core.model.player.Player;
import core.net.MyConnector;
import core.util.HOLogger;
import module.lineup.Lineup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import module.teamAnalyzer.vo.MatchRating;

public class PluginFeedback {
    private String userAgent;
    private String getUrl;
    private String postUrl;
    private String postParams;

    public PluginFeedback() {
        userAgent = "Hattrick Organizer!/v1.436(r1246)"; //example
        postUrl = "http://mistermax80.max:8080/ws-user-feedbacks";
        getUrl = "http://mistermax80.max:8080/ws-user-feedbacks";
    }

    // SendFeedbackToServer (Lineup lineup, PredictionRating HTPredictionRating)
    public String sendFeedbackToServer(Lineup lineup, MatchRating HTPredictionRating) {

        String result = "";

        // Input Checks
        if (lineup != null && HTPredictionRating != null) {
            List<Player> playerList = HOVerwaltung.instance().getModel().getAllSpieler();
            Feedback feedback = new Feedback(lineup.getPositionen(), HTPredictionRating, playerList);

            // Create a JsonObject
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gsonFeedback = builder.create();

            try {
                this.doPOST("{\"JSON_OBJECTS\": " + gsonFeedback.toJson(feedback) + "}");
            } catch (Exception e) {
                HOLogger.instance()
                        .log(getClass(), "Unable to connect to the update server (HO): " + e);
                result = "error";
            }
        }
        return result;
    }

    private void doPOST(String postParams) throws IOException {
        URL obj = new URL(postUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", userAgent);
        con.setRequestProperty("Accept","application/json");
        con.setRequestProperty("Content-Type","application/json");

        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        //"{\"JSON_OBJECTS\": \"da java 1234567890 !$%&/()=?àèéìòù\"}";
        os.write(postParams.getBytes());
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        // accepted response code: success || created
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("POST request not worked");
        }
    }

    // used only to test
    private void sendGET() throws IOException {
        //URL obj = new URL(GET_URL);
        URL obj = new URL(getUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", userAgent);
        con.setRequestProperty("Accept","application/json");

        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }
    }
}