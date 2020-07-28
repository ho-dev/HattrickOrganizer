package module.series.promotion;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface DataSubmitter {

    /**
     * Returns the list of the leagues supporte by the Promotion manager feature.
     *
     * @return List<Integer> — List of the league IDs supported.
     */
    List<Integer> fetchSupportedLeagues();

    /**
     * Gets Promotion data status for league with ID <code>leagueId</code>.
     *
     * @param leagueId ID of the league.
     * @param callback Callback to execute when the status has been retrieved, to which the body of the response
     *                 is passed as String.
     */
    void getLeagueStatus(int leagueId, Function<String, Void> callback);


    /**
     * Locks a block for processing for league with ID <code>leagueId</code>.
     *
     * @param leagueId ID of the league for which we are locking a block.
     * @return Optinal<BlockInfo> – Details about the locked block.  Returns empty optional if no block available.
     */
    Optional<BlockInfo> lockBlock(int leagueId);

    /**
     * Submits the data for a block whose details are held in <code>blockInfo</code> as String representing
     * the data in JSON.
     *
     * @param blockInfo {@link BlockInfo} containing the details of the block being processed.
     * @param json JSON containing the data being submitted for the block.
     */
    void submitData(BlockInfo blockInfo, String json);

    /**
     * Retrieves the promotion/demotion details to a team with id <code>teamId</code> in league <code>leagueId</code>.
     *
     * @param leagueId League ID of the team.
     * @param teamId Team Id of the team.
     * @return Optional<String> – Optional containing the status of the team, empty if not found.
     */
    Optional<String> getPromotionStatus(int leagueId, int teamId);
}
