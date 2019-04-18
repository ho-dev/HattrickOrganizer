package tool.pluginFeedback;

import core.HO;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.HOLogger;
import module.lineup.Lineup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import module.teamAnalyzer.vo.MatchRating;

import javax.swing.*;

public class PluginFeedback {
    /*
you do an enum for status
like serverStatusUpload.ok

serverStatusUpload.error
serverStatusUpload.blabla
in english properties you enter value for
ls.serverStatusUpload = e.g. "cant connect server"
ls.serverStatusUpload.ok = all good .
ect
     */

    // Return Codes

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private String hoToken;
    private String userAgent;
    private String getUrl;
    private String postUrl;

    public PluginFeedback() {
        // HO Token, useful to error analysis between HO and server
        hoToken = "ho_token-" + PluginFeedback.randomAlphaNumeric(10);
        // userAgent real example: "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36"
        userAgent = "HO/" + HO.getVersionString() + " (" + System.getProperty("os.name") + " on "
                + System.getProperty("os.arch") + " (" + System.getProperty("os.version")
                + "))/" + System.getProperty("java.version") + " ("
                + System.getProperty("java.vendor") + ")";
        postUrl = "http://mistermax80.max:8080/ws-user-feedbacks";
        getUrl = "http://mistermax80.max:8080/ws-user-feedbacks";
    }

    // SendFeedbackToServer (Lineup lineup, PredictionRating rating)
    public int sendFeedbackToServer(Lineup lineup, MatchRating rating) throws IOException, IllegalArgumentException {
        int result = -1;

        // Input Checks
        if (lineup != null && rating != null) {
            List<Player> playerList = HOVerwaltung.instance().getModel().getAllSpieler();
            for (int i = 0; i < playerList.size() - 3; i++) {
                playerList.remove(i);
            }

            Feedback feedback = new Feedback(lineup.getPositionen(), rating, playerList, hoToken);
            // Create a JsonObject
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gsonFeedback = builder.create();
            result = this.doPOST("{\"JSON_OBJECTS\": " + gsonFeedback.toJson(feedback) + "}");
        } else {
            throw new IllegalArgumentException("The inputs (Lineup lineup or MatchRating rating) are null value! (" + hoToken + ")");
        }
        return result;
    }

    private int doPOST(String postParams) throws IOException {
        int result = 0;
        URL obj = new URL(postUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", userAgent);
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("HO-Token", hoToken);

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
        //if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
        BufferedReader in = null;
        String inputLine;
        StringBuffer response = new StringBuffer();
        try {
            in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
        } catch (IOException e) {
            in = new BufferedReader(new InputStreamReader(
                    con.getErrorStream()));
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            HOLogger.instance().error(getClass(), "Error Response:" + response);
            System.out.println("Error Response:" + response.toString());
            throw e;
        }

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // print result
        System.out.println(response.toString());

        return result;
    }

    private static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    // used only to test
    private void sendGET() throws IOException {
        //URL obj = new URL(GET_URL);
        URL obj = new URL(getUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", userAgent);
        con.setRequestProperty("Accept", "application/json");

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

    public String getHoToken() {
        return hoToken;
    }
}