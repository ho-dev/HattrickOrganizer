package core.net;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class HattrickLink {

    /**
     * Show match at hattrick org
     * @param matchId
     * @param isOfficial
     */
    public static void showMatch(String matchId, boolean isOfficial){
        URI url;
        if (isOfficial) {
            url = URI.create(String.format("http://www.hattrick.org/Club/Matches/Match.aspx?matchID=%s", matchId));
        }else
            url= URI.create(String.format("https://www.hattrick.org/Club/Matches/Match.aspx?matchID=%s&SourceSystem=HTOIntegrated", matchId));

        showLink(url);
    }

    public static void showTeam(String teamID){

        URI url = URI.create(String.format("http://www.hattrick.org/Club/?TeamID=%s", teamID));

        showLink(url);
    }

    /**
     * Show player at hattrick org
     * @param playerId
     */
    public static void showPlayer(int playerId){
        URI url = URI.create(String.format("https://www.hattrick.org/Club/Players/Player.aspx?playerId=%s", playerId));
        showLink(url);
    }

    public static void showLink(URI url){
        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(url);
            } catch (IOException e) {}
        }else{
            String os = System.getProperty("os.name").toLowerCase();
            Runtime runtime = Runtime.getRuntime();
            try {
                if(os.indexOf("win") >= 0)
                    runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
                else if(os.indexOf("mac") >= 0)
                    runtime.exec("open " + url);
                else
                    runtime.exec("firefox " + url);
            } catch (IOException e) {}
        }
    }

}
