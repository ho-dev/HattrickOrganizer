package module.series.promotion;

import com.google.gson.Gson;
import core.file.xml.TeamStats;
import core.file.xml.XMLLeagueDetailsParser;
import core.file.xml.XMLTeamDetailsParser;
import core.model.HOVerwaltung;
import core.net.MyConnector;
import core.util.HOLogger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This class gets all the teams in a given country, calculates the ranking for each one of them,
 * and submits the data back to the HO server.
 *
 * <p>The ranking for a team is calculated as follows:</p>
 *
 * <ul>
 *     <li>The score is a 15-digit integer; the higher the score, the better the ranking.</li>
 *     <li>Digit 1: 10 - Series Level</li>
 *     <li>Digit 2: 8 - Position in the Series</li>
 *     <li>Digits 3-4: Number of points in the Series, between 0 and 42</li>
 *     <li>Digits 5-6-7: 500 + Goal Difference</li>
 *     <li>Digits 8-9-10: 500 + Goals for</li>
 *     <li>Digits 11-12-13-14-15: Initialized at 00000.  If no team has duplicated score, then we are done.
 *     Otherwise for the teams with duplicated score calculate 99,999 - Visible Rank, with Visible Rank = 99,999
 *     if the team is a bot.</li>
 * </ul>
 *
 * <p>When a user wants to check what league he or she will be promoted/demoted to the following season,
 * HO checks whether the ranking calculation has already been done for his/her country.  If it has not been
 * done already, HO downloads the data for his/her country, calculates the ranking, and submits the
 * results back to the HO server: this first user is responsible for seeding the data for his/her country.</p>
 *
 * <p>when the data is being downloaded for the country, an endpoint on HO server is called to mark
 * the data retrieval for that country as pending.  While the data retrieval is pending, no one else can
 * download the data for this country.  This means that if the user interrupts the download (for example
 * by shutting down HO prematurely, or because of a crash), the status of data retrieval for this country
 * needs to be reset to “unavailable” after a certain period of time.</p>
 *
 * <p>Once the data for a country is available, the data request to the HO server will return the pre-calculated
 * info without the need for downloading more data from HT.</p>
 */
public class DownloadCountryDetails {

    final MyConnector mc = MyConnector.instance();
    final DataSubmitter submitter = HttpDataSubmitter.instance();

    private Map<String, TeamStats> getTeamsInfoInSeries(int seriesId) {
        String details = mc.getLeagueDetails(String.valueOf(seriesId));
        return XMLLeagueDetailsParser.parseLeagueDetails(details);
    }

    private int getTeamRank(int teamId) {
        Map<String, String> teamInfo = getTeamSeries(teamId);
        return Integer.parseInt(teamInfo.getOrDefault("TeamRank", "-1"));
    }

    public Map<String, String> getTeamSeries(int teamId) {
        HOLogger.instance().info(DownloadCountryDetails.class, String.format("Retrieving Team details for team %d.", teamId));

        try {
            String details = mc.getTeamdetails(teamId);
            return XMLTeamDetailsParser.parseTeamdetailsFromString(details, teamId);
        } catch (IOException e) {
            HOLogger.instance().log(DownloadCountryDetails.class, e);
        }

        return Collections.EMPTY_MAP;
    }

    private void handleDuplicateRankings(CountryTeamInfo countryTeamInfo, Map<Integer, CountryTeamInfo.TeamRank> teamRankMap) {
        // Find ranks for which we have duplicate ranks.
        List<CountryTeamInfo.TeamRank> duplicateRanks = countryTeamInfo.data
                .stream()
                .collect(Collectors.groupingBy(CountryTeamInfo.TeamRank::getScore))
                .entrySet()
                .stream()
                .filter(longListEntry -> longListEntry.getValue().size() > 1) // filter out ranks that appear once
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .entrySet()
                .stream()
                .flatMap(longListEntry -> longListEntry.getValue().stream())
                .collect(Collectors.toList()); // merge all the lists of team ranks

        HOLogger.instance().info(DownloadCountryDetails.class, String.format("Found %d team with duplicate ranks.", duplicateRanks.size()));
        countryTeamInfo.data.clear();

        ProcessAsynchronousTask<Integer> processAsynchronousTask = new ProcessAsynchronousTask<>();

        for (CountryTeamInfo.TeamRank rank : duplicateRanks) {
            processAsynchronousTask.addToQueue(rank.teamId);
        }

        ProcessAsynchronousTask.ProcessTask<Integer> task = (val) -> {
            int observedRank = getTeamRank(val);

            // Re-enqueue if error, unless error count has become too high.
            if (observedRank < 0 && processAsynchronousTask.getErrorCount() < 100) {
                processAsynchronousTask.incErrorCount();
                processAsynchronousTask.addToQueue(val);
            } else {
                CountryTeamInfo.TeamRank teamRank = teamRankMap.get(val);

                // Bots' rank is 0
                if (observedRank != 0) {
                    teamRank.setScore(teamRank.getScore() + (99_999 - observedRank));
                }
                teamRankMap.put(val, teamRank);
            }
        };
        processAsynchronousTask.execute(task);

        countryTeamInfo.data.addAll(new ArrayList<>(teamRankMap.values()));
        countryTeamInfo.data.sort(Comparator.comparingLong(o -> -o.Score));
    }

    public void processSeries(BlockInfo blockInfo) {
        int season = HOVerwaltung.instance().getModel().getBasics().getSeason();
        String username = HOVerwaltung.instance().getModel().getBasics().getManager();

        CountryTeamInfo countryTeamInfo = new CountryTeamInfo();
        countryTeamInfo.leagueId = blockInfo.leagueId;
        countryTeamInfo.season = season;
        countryTeamInfo.username = username;

        ProcessAsynchronousTask<Integer> processAsynchronousTask = new ProcessAsynchronousTask<>();

        for (Integer seriesId: blockInfo.series) {
            processAsynchronousTask.addToQueue(seriesId);
        }

        // Get the teams in each series queued up
        Map<String, TeamStats> teamsInfo = new ConcurrentHashMap<>();
        ProcessAsynchronousTask.ProcessTask<Integer> task = (val) -> {
            Map<String, TeamStats> teamsInfoInSeries = getTeamsInfoInSeries(val);
            HOLogger.instance().info(HttpDataSubmitter.class, teamsInfoInSeries);
            teamsInfo.putAll(teamsInfoInSeries);
        };
        processAsynchronousTask.execute(task);

        // For each team, compute the ranking
        Map<Integer, CountryTeamInfo.TeamRank> teamRankMap = teamsInfo.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getValue().getTeamId(),
                        e -> new CountryTeamInfo.TeamRank(e.getValue().getTeamId(), e.getValue().rankingScore())));

        // sort ranks, it will be used to find ex-aequo teams.
        countryTeamInfo.data.addAll(new ArrayList<>(teamRankMap.values()));
        countryTeamInfo.data.sort(Comparator.comparingLong(o -> -o.Score));

        handleDuplicateRankings(countryTeamInfo, teamRankMap);
        createJson(blockInfo, countryTeamInfo);
    }

    private void createJson(BlockInfo blockInfo, CountryTeamInfo countryTeamInfo) {
        Gson gson = new Gson();
        String json = gson.toJson(countryTeamInfo);
        HOLogger.instance().info(DownloadCountryDetails.class, json);
        submitter.submitData(blockInfo, json);
    }
}
