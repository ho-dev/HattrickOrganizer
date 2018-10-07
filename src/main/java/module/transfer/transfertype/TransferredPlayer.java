// %368918737:hoplugins.transfers.vo%
package module.transfer.transfertype;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.player.Spieler;
import module.transfer.PlayerTransfer;
import module.transfer.TransferTypes;


/**
 * Transferred Player value object
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
class TransferredPlayer {
    //~ Instance fields ----------------------------------------------------------------------------

    private String playerName;
    private boolean bought = false;
    private boolean sold = false;
    private double officialMatch = 0;
    private int age = 17;
    private int endWeek = 0;
    private int experience = 0;
    private int experienceSkillups = 0;
    private int id = 0;
    private int income;
    private int leadership = 0;
    private int skillups = 0;
    private int startWeek;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TransferredPlayer object.
     *
     * @param player PlayerId of the Transferred Player
     */
    TransferredPlayer(Spieler player) {
        age = player.getAlter();
        id = player.getSpielerID();
        experience = player.getErfahrung();
        leadership = player.getFuehrung();
        playerName = player.getName();
        skillups += player.getAllLevelUp(PlayerSkill.KEEPER).size();
        skillups += player.getAllLevelUp(PlayerSkill.PLAYMAKING).size();
        skillups += player.getAllLevelUp(PlayerSkill.PASSING).size();
        skillups += player.getAllLevelUp(PlayerSkill.WINGER).size();
        skillups += player.getAllLevelUp(PlayerSkill.DEFENDING).size();
        skillups += player.getAllLevelUp(PlayerSkill.SCORING).size();
        skillups += player.getAllLevelUp(PlayerSkill.SET_PIECES).size();

        //player.getAllLevelUp(ISpieler.SKILL_KONDITION).size();
        experienceSkillups = player.getAllLevelUp(PlayerSkill.EXPERIENCE).size();
        officialMatch = DBManager.instance().getCountOfPlayedMatches(player.getSpielerID(), true);

        //testMatch = PlayerMatchesDAO.getAppearance(player.getSpielerID(), false);
        endWeek = HOVerwaltung.instance().getModel().getBasics().getSpieltag()
                  + (HOVerwaltung.instance().getModel().getBasics().getSeason() * 16);
    }

    /**
     * Creates a new TransferredPlayer object.
     *
     * @param pt Player Transfer to Analyze
     */
    TransferredPlayer(PlayerTransfer pt) {
        id = pt.getPlayerId();
        playerName = pt.getPlayerName();
        age = 17;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns the Net Income of the Transfer
     *
     * @return income in euro
     */
    final int getIncome() {
        return income;
    }

    /**
     * Return the Id of the player
     *
     * @return id
     */
    final int getPlayerId() {
        return this.id;
    }

    /**
     * Return the name of the player
     *
     * @return Name
     */
    final String getPlayerName() {
        return playerName;
    }

    /**
     * Returns the Transfer Type
     *
     * @return transfer type code
     */
    final int getTransferType() {
        int type = DBManager.instance().getTransferType(id);

        if (type > -2) {
            return type;
        }

        if (bought && !sold) {
            return getActualPlayerType();
        }

        if (!bought && sold) {
            return getTeamPlayerType();
        }

        return getOldPlayerType();
    }

    /**
     * Add a transfer for the specified player
     *
     * @param transfer The Transfer Detail
     */
    final void addTransfer(PlayerTransfer transfer) {
        if (transfer.getPlayerId() != id) {
            return;
        }

        final int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();

        // Fix for RE-Bought players
        if ((transfer.getBuyerid() == teamid) && (transfer.getSellerid() == teamid)) {
            return;
        }

        if (transfer.getBuyerid() == teamid) {
            bought = true;
            income -= transfer.getPrice();
            startWeek = transfer.getWeek() + (transfer.getSeason() * 16);
            return;
        }

        if (transfer.getSellerid() == teamid) {
            sold = true;
            income += transfer.getPrice();
            endWeek = transfer.getWeek() + (transfer.getSeason() * 16);
            return;
        }
    }

    /**
     * Method that calculates the Transfer Type for players still on roster
     *
     * @return transfer type code
     */
    private int getActualPlayerType() {
        if (skillups > 0) {
            return TransferTypes.TRAINED_ROSTER;
        }

        if (isStarter()) {
            return TransferTypes.STARTER_ROSTER;
        }

        return TransferTypes.BACKUP_ROSTER;
    }

    /**
     * Method that calculates the Transfer Type for old players
     *
     * @return transfer type code
     */
    private int getOldPlayerType() {
        if ((getWeekOnRoster() <= 3) && (skillups == 0)) {
            return TransferTypes.DAY_TRADING;
        }

        if ((getWeekOnRoster() <= 6) && (skillups == 1)) {
            return TransferTypes.SKILL_TRADING;
        }

        if ((experienceSkillups > 0) && (experience >= 5) && (leadership >= 5)) {
            return TransferTypes.FUTURE_TRAINER;
        }

        if (skillups == 0) {
            if (isStarter()) {
                return TransferTypes.OLD_STARTER;
            }

            return TransferTypes.OLD_BACKUP;
        }

        return TransferTypes.OLD_TRAINED;
    }

    /**
     * Method the returns the type of player, starter or backup based on the number of matches and
     * the time on roster
     *
     * @return true if player is a starter, false if a backup
     */
    private boolean isStarter() {
        final double ratio = officialMatch / (getWeekOnRoster() + 1);

        if (ratio > 0.35) {
            return true;
        }

        return false;
    }

    /**
     * Method that calculates the Transfer Type for players sold but never bought
     *
     * @return transfer type code
     */
    private int getTeamPlayerType() {
        if (age > 20) {
            return TransferTypes.ORIGINAL_ROSTER;
        }

        return TransferTypes.YOUTH_PULL;
    }

    /**
     * Gets the weeks the player was/is on roster
     *
     * @return Amount of weeks on roster
     */
    private int getWeekOnRoster() {
        return endWeek - startWeek;
    }
}
