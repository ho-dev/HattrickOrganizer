package module.series.promotion;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import core.db.DBManager;
import core.gui.event.ChangeEventHandler;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.misc.Basics;
import core.module.config.ModuleConfig;
import core.util.HOLogger;

import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main class for the League Promotion/Demotion prediction tool.
 *
 * <p>The data for each league (i.e. country) is submitted by clients.  As this is a lengthy process
 * that can fail at any time, league details are processed in blocks.  A client needs to lock a block
 * for processing by requesting it from the HO server.  Once locked, the client can then process that
 * block, and once all the data is retrieved and computed, the result is submitted to the HO server.</p>
 */
public class LeaguePromotionHandler extends ChangeEventHandler {

    static class DownloadDetails {
        int blockNumber;
        int blockNumberReady;
        int blockNumberInProgress;
    }


    private LeagueStatus leagueStatus;
    private DownloadDetails downloadDetails;
    private boolean continueProcessing;


    /**
     * Promotion Manager is active only in weeks 14 and 15, and for the supported leagues.
     *
     * @param seriesId ID of the series to check.
     * @return boolean – true if promotion manager can be used, false otherwise.
     */
    public boolean isActive(int seriesId) {
        List<Integer> supportedLeagues = HttpDataSubmitter.instance().fetchSupportedLeagues();
        int[] activeWeeks = ModuleConfig.instance().getIntArray("PromotionStatus_ActiveWeeks", new int[] { 14, 15 });
        int week = HOVerwaltung.instance().getModel().getBasics().getSpieltag();
        return UserParameter.instance().promotionManagerTest ||
                (Arrays.stream(activeWeeks).boxed().collect(Collectors.toList()).contains(week) &&
                        supportedLeagues.contains(seriesId));
    }

    public LeagueStatus getLeagueStatus() {
        if (leagueStatus == null) {
            leagueStatus = fetchLeagueStatus();
        }

        return leagueStatus;
    }

    private LeagueStatus fetchLeagueStatus() {
        final Basics basics = DBManager.instance().getBasics(HOVerwaltung.instance().getId());
        int leagueId = basics.getLiga();

        HttpDataSubmitter submitter = HttpDataSubmitter.instance();
        submitter.getLeagueStatus(leagueId, s -> {
            HOLogger.instance().info(LeaguePromotionHandler.class, "Status of league: " + leagueId + " : " + s);
            Gson gson = new Gson();
            JsonObject obj = gson.fromJson(s, JsonObject.class);
            leagueStatus = LeagueStatus.valueOf(obj.get("status_desc").getAsString());

            if (leagueStatus == LeagueStatus.NOT_AVAILABLE) {
                downloadDetails = new DownloadDetails();
                downloadDetails.blockNumber = obj.get("nbBlocks").getAsInt();
                downloadDetails.blockNumberReady = obj.get("nbBlocksReady").getAsInt();
                downloadDetails.blockNumberInProgress = obj.get("nbBlocksInProgress").getAsInt();
            }

            return null;
        });

        return leagueStatus;
    }

    public void downloadLeagueData(int leagueId) {
        final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                continueProcessing = (leagueStatus == LeagueStatus.NOT_AVAILABLE);
                do {
                    final BlockInfo blockInfo = lockBlock(leagueId);

                    if (blockInfo.status == 200) {
                        DownloadCountryDetails downloadCountryDetails = new DownloadCountryDetails();
                        downloadCountryDetails.processSeries(blockInfo);

                        LeagueStatus status = fetchLeagueStatus();
                        continueProcessing = (status == LeagueStatus.NOT_AVAILABLE);
                    } else {
                        HOLogger.instance().warning(LeaguePromotionHandler.class,
                                "Block locking status: " + blockInfo.status);
                        continueProcessing = false;
                    }
                } while (continueProcessing);

                fireChangeEvent(new ChangeEvent(LeaguePromotionHandler.this));

                return null;
            }
        };

        worker.execute();
    }

    public void pollPromotionStatus() {
        final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                HOLogger.instance().info(LeaguePromotionHandler.class, "Polling until status available");

                boolean polling;
                do {
                    LeagueStatus status = fetchLeagueStatus();
                    polling = (status != LeagueStatus.AVAILABLE);

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        HOLogger.instance().warning(LeaguePromotionHandler.class,
                                "Interrupted Thread: " + e.getMessage());
                    }
                } while (polling);
                fireChangeEvent(new ChangeEvent(LeaguePromotionHandler.this));
                return null;
            }
        };

        worker.execute();
    }

    public BlockInfo lockBlock(int leagueId) {
        DataSubmitter submitter = HttpDataSubmitter.instance();
        return submitter.lockBlock(leagueId);
    }

    public LeaguePromotionInfo getPromotionStatus(int leagueId, int teamId) {
        DataSubmitter submitter = HttpDataSubmitter.instance();

        String promotionInfo = submitter.getPromotionStatus(leagueId, teamId);

        if (promotionInfo == null) {
            LeaguePromotionInfo leaguePromotionInfo = new LeaguePromotionInfo();
            leaguePromotionInfo.status = LeaguePromotionStatus.UNKNOWN;

            return leaguePromotionInfo;
        }

        final Gson gson = new Gson();
        final JsonObject obj = gson.fromJson(promotionInfo, JsonObject.class);

        LeaguePromotionInfo leaguePromotionInfo = new LeaguePromotionInfo();
        leaguePromotionInfo.status = LeaguePromotionStatus.codeToStatus(obj.get("status_desc").getAsString());

        List<Integer> teams = new ArrayList<>();
        for (JsonElement o: obj.get("oppTeamIDs").getAsJsonArray()) {
            teams.add(o.getAsInt());
        }
        leaguePromotionInfo.teams = teams;

        return leaguePromotionInfo;
    }
}
