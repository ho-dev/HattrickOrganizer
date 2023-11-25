package core.db;

import core.file.hrf.HRF;
import core.model.StaffMember;
import core.model.Team;
import core.model.XtraData;
import core.model.match.MatchLineupTeam;
import core.model.misc.Basics;
import core.model.misc.Economy;
import core.model.misc.Verein;
import core.model.player.Player;
import core.model.series.Liga;
import module.series.Spielplan;
import tool.arenasizer.Stadium;

import java.sql.Timestamp;
import java.util.List;

/**
 * Defines all the operations that a persistence manager needs to implement.
 */
public interface PersistenceManager {

    // HRF

    /**
     * Returns the latest HRF, i.e. the HRF whose ID is the highest one in the persistence store.
     *
     * @return HRF – HRF with the max ID.
     */
    HRF getMaxIdHrf();

    /**
     * Loads the HRF entry with id <code>id</code>, and returns it.
     *
     * @param id ID of the HRF to load.
     * @return HRF – HRF with ID <code>id</code>, <code>null</code> if not found.
     */
    HRF loadHRF(int id);

    /**
     * Loads the last HRF entry downloaded before date <code>date</code>.
     *
     * @param date Reference date
     * @return HRF – Last HRF entry before date if any, <code>null</code> if none found.
     */
    HRF loadLatestHRFDownloadedBefore(Timestamp date);

    // General

    /**
     * Returns {@link Basics} details, as defined by HRF ID <code>hrfId</code>.
     *
     * @param hrfId ID of reference HRF
     * @return Basics – Basics details if found.  Returns an empty {@link Basics} object if not found.
     */
    Basics getBasics(int hrfId);

    /**
     * Returns the club details, as defined by HRF ID <code>hrfId</code>.
     *
     * @param hrfId ID of reference HRF.
     * @return Verein – Club details if found.  Returns an empty {@link Verein} object if not found.
     */
    Verein getVerein(int hrfId);

    /**
     * Returns the team details, as defined by HRF ID <code>hrfId</code>.
     *
     * @param hrfId ID of reference HRF.
     * @return Team – Team details if found.  Returns an empty {@link Team} object if not found.
     */
    Team getTeam(int hrfId);

    /**
     * Returns the {@link Economy} details, as defined by HRF ID <code>hrfId</code>.
     *
     * @param hrfId ID of reference HRF.
     * @return Economy – Economy details if found.  Returns <code>null</code>  if not found.
     */
    Economy getEconomy(int hrfId);

    /**
     * Returns the {@link Liga} details, as defined by HRF ID <code>hrfId</code>.
     *
     * @param hrfId ID of reference HRF.
     * @return Liga – Liga details if found.  Returns <code>null</code> if not found.
     */
    Liga getLiga(int hrfId);

    /**
     * Returns the {@link Stadium} details, as defined by HRF ID <code>hrfId</code>.
     *
     * @param hrfId ID of reference HRF.
     * @return Stadium – Stadium details if found.  Returns <code>null</code> if not found.
     */
    Stadium getStadion(int hrfId);

    /**
     * Returns the {@link XtraData} details, as defined by HRF ID <code>hrfId</code>.
     *
     * @param hrfId ID of reference HRF.
     * @return XtraData – XtraData details if found.  Returns <code>null</code> if not found.
     */
    XtraData getXtraDaten(int hrfId);


    // Players

    /**
     * Returns all the players in the persistence store, including the former ones.
     *
     * @return List<Player> – All players in the store.  Empty list if none is found.
     */
    List<Player> loadAllPlayers();

    /**
     * Returns the players present in the HRF with id <code>hrfId</code>
     *
     * @param hrfId ID of HRF to consider.
     * @return List<Player> – Players present in HRF.  Empty list if none found.
     */
    List<Player> getSpieler(int hrfId);


    // Lineup

    /**
     * Returns the lineup for the last match for team <code>teamId</code>.
     *
     * @param teamId ID of the team for which we load the last lineup
     * @return MatchLineupTeam – Last lineup of the team.  <code>null</code> if not found.
     */
    MatchLineupTeam loadPreviousMatchLineup(int teamId);

    /**
     * Returns the lineup for the next (upcoming) match for team <code>teamId</code>.
     *
     * @param teamId ID of the team for which we load the next lineup
     * @return MatchLineupTeam – Upcoming lineup of the team.  <code>null</code> if not found.
     */
    MatchLineupTeam loadNextMatchLineup(int teamId);


    /**
     * Returns the latest game schedule.
     *
     * @return Spielplan – Latest game schedule.  <code>null</code> if none found.
     */
    Spielplan getLatestSpielplan();


    // Staff

    /**
     * Returns the list of staff members for a given HRF ID <code>hrfId</code>.
     *
     * @param hrfId ID of HRF to consider.
     * @return List<StaffMember> – Staff Members for <code>hrfId</code>.  Empty list if none found.
     */
    List<StaffMember> getStaffByHrfId(int hrfId);
}
