// %2277986132:hoplugins.transfers.vo%
package module.transfer;

import core.db.AbstractTable;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.HODateTime;
import core.util.HOLogger;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.min;
import static module.transfer.XMLParser.downloadTeamTransfers;

/**
 * Value Object representing a player transfer.
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
public class PlayerTransfer extends AbstractTable.Storable {
    //~ Static fields/initializers -----------------------------------------------------------------

    /** Type to indicate a BUY transfer */
    public static final int BUY = 1;

    /** Type to indicate a SELL transfer */
    public static final int SELL = 2;

    /** Type to indicate a SELL transfer */
    public static final int REBOUGHT = 0;

    public static Integer teamId;
    public static Long totalSumOfBuys;
    public static Long totalSumOfSales;
    public static Long numberOfBuys;
    public static Long numberOfSales;
    public static HODateTime activatedDate;

    //~ Instance fields ----------------------------------------------------------------------------

    /** Player info on tranfer date */
    private Player playerInfo;

    /** Name of the buyer team */
    private String buyerName = ""; //$NON-NLS-1$

    /** Name of the transfered player */
    private String playerName = ""; //$NON-NLS-1$

    /** Name of the seller team */
    private String sellerName = ""; //$NON-NLS-1$

    /** Tranfer date */
    private HODateTime date;

    /** Id of the buyer team */
    private int buyerid = 0;

    /** Id of the transferred player */
    private int playerId;

    /** Transfer price */
    private int price = 0;

    /** Season */
    private int season;

    /** Id of the seller team */
    private int sellerid = 0;

    /** Id of the transfer */
    private int transferId;

    /** TSI value of the player at transfer date */
    private int tsi = 0;
    
    private Integer motherClubFee;
    private Integer previousClubFee;

    public void setType(int type) {
        this.type = type;
    }

    /** Transfer type */
    private int type;

    /** Week */
    private int week;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates an instance of PlayerTransfer representing a player transfer.
     *
     * @param transferid Id of the transfer
     * @param playerid Id of the transferred player
     */
    public PlayerTransfer(int transferid, int playerid) {
        this.transferId = transferid;
        this.playerId = playerid;
    }

    /**
     * constructor is used by AbstractTable (TransfersTable)
     */
    public PlayerTransfer(){}

    /**
     * Download team's missing player transfers
     * If downloaded transfer is not stored in db or differs from stored version, the database is updated.
     * @param teamId Team id
     */
    public static void downloadMissingTransfers(int teamId) {
        var db = DBManager.instance();
        var isSold = true;
        var sold = db.getSumTransferPrices(teamId, isSold);
        var bought = db.getSumTransferPrices(teamId, !isSold);
        int page = 0;
        do {
            var transfers = downloadTeamTransfers(teamId, page++);
            if (transfers.isEmpty()) break;
            for (var transfer : transfers) {
                var inDB = db.loadPlayerTransfer(transfer.transferId);
                if (inDB == null || transfer.getPrice() != inDB.getPrice() || inDB.getPlayerId()==0) {
                    updatePlayerTransfer(transfer);
                    var priceCorrection = transfer.getPrice();
                    if (inDB != null) priceCorrection -= inDB.getPrice();
                    if (transfer.buyerid == teamId) bought += priceCorrection;
                    else sold += priceCorrection;
                }
            }
        } while (sold != PlayerTransfer.totalSumOfSales || bought != PlayerTransfer.totalSumOfBuys);
    }

    /**
     * Update player transfer in database.
     * If transfer's player is a deleted/fired player (player id is 0), the correct player id is tried to load from
     * previously stored version of the transfer. If this is not available, the player info is loaded from player table (SPIELER).
     * @param transfer Player transfer
     */
    private static void updatePlayerTransfer(PlayerTransfer transfer) {
        var db = DBManager.instance();
        if (transfer.getPlayerId() == 0){
            // find player id
            var transferInDB = db.loadPlayerTransfer(transfer.getTransferId());
            if ( transferInDB != null && transferInDB.getPlayerId()!=0){
                transfer.setPlayerId(transferInDB.getPlayerId());
            }
            else {
                var player = transfer.getPlayerInfo();
                if (player != null){
                    transfer.setPlayerId(player.getPlayerID());
                }
            }
        }
        db.storePlayerTransfer(transfer);
    }

    /**
     * Get the mother club fee
     * If not available, the value is calculated.
     * @return int
     */
    public int getMotherClubFee(){
        if ( this.motherClubFee == null) {
            var teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
            this.motherClubFee = calcMotherClubFee(teamId);
        }
        return this.motherClubFee;
    }

    /**
     * Set the mother club fee
     * @param motherClubFee Integer
     */
    public void setMotherClubFee(Integer motherClubFee) {
        this.motherClubFee = motherClubFee;
    }

    /**
     * Calculate the mother club fee
     * @param teamId Team id
     * @return int
     */
    private int calcMotherClubFee(int teamId){
        var player = PlayerRetriever.getPlayer(this.playerId);
        if (player != null && player.isHomeGrown() && this.sellerid != teamId) return (int)(this.price * .02);    // 2%
        return 0;
    }

    /**
     * Get the income of transfer's previous club fees
     * It is calculated, if not available yet.
     * @return int The income in locale currency
     */
    public int getPreviousClubFee(){
        if ( this.previousClubFee == null) {
            var teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
            this.previousClubFee = calcPreviousClubFee(teamId);
        }
        return this.previousClubFee;
    }

    public void setPreviousClubFee(Integer motherClubFee) {
        this.previousClubFee = motherClubFee;
    }

    /**
     * Calculate the income of transfer's previous club fees
     * see <a href="https://www85.hattrick.org/Help/Rules/AppTransferFees.aspx">...</a>
     * @param teamId Team ID
     * @return Income in locale currency
     */
    private int calcPreviousClubFee(int teamId) {
        var player = getPlayerInfo();
        if (player != null) {
            var matchCount = player.getMatchesCurrentTeam();
            if (matchCount==null){
                matchCount = DBManager.instance().getCountOfPlayedMatches(playerId, true);
                player.setMatchesCurrentTeam(matchCount);
            }
            if (matchCount > 0) {
                // get previous transfer
                PlayerTransfer previous = null;
                var transfers = DBManager.instance().getTransfers(getPlayerId(), true);
                for (var transfer : transfers) {
                    if (transfer.getDate().isBefore(this.getDate())) {
                        if (previous == null || previous.getDate().isBefore(transfer.getDate())) {
                            previous = transfer;
                        }
                    }
                }
                if (previous != null && previous.getSellerid() == teamId) {
                    var percentage = switch (matchCount) {
                        case 1 -> 0.0025;
                        case 2 -> 0.005;
                        case 3 -> 0.01;
                        case 4 -> 0.015;
                        case 5, 6 -> 0.02;
                        case 7, 8, 9 -> 0.025;
                        default -> min(0.04, 0.025 + matchCount / 10 * 0.005);
                    };
                    return (int) (percentage * getPrice());
                }
            }
        }
        return 0;
    }

    /**
     * Download transfers that were responsible for commission income of given ht week
     * Methods examines sum of already stored player transfers. If income is missing,
     * the list of sold players is loaded and beginning with the latest sold player, their
     * transfers are downloaded from hattrick. If a missing transfer is found,
     * it is stored and it's commission is added to the sum of db entries.
     * The loop is exited, when reaching the commission value.
     * @param teamId Team id
     * @param commission  Commission income of the week from economy download
     * @param htweek HT week of that commission income
     */
    public static void downloadMissingTransferCommissions(int teamId, int commission, HODateTime.HTWeek htweek) {
        var startWeek = HODateTime.fromHTWeek(htweek);
        var sum = DBManager.instance().getSumTransferCommissions(startWeek);
        if(sum != commission){
            var soldPlayers = DBManager.instance().loadTeamTransfers(teamId, true);
            for (var player : soldPlayers){
                var transfers = XMLParser.getAllPlayerTransfers(player.getPlayerId());
                for (var transfer : transfers){
                    var inDB = DBManager.instance().loadPlayerTransfer(transfer.getTransferId());
                    if (inDB == null){
                        DBManager.instance().storePlayerTransfer(transfer);
                        if (!transfer.getDate().isBefore(startWeek) && !transfer.getDate().isAfter(startWeek.plus(7, ChronoUnit.DAYS))) {
                            sum += transfer.getMotherClubFee();
                            sum += transfer.getPreviousClubFee();
                            if (sum >= commission) return;
                        }
                    }
                }
            }
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Sets the name of the buyer team.
     *
     * @param name name of the buyer team
     */
    public final void setBuyerName(String name) {
        this.buyerName = name;
    }

    /**
     * Gets the name of the buyer team.
     *
     * @return Name of the buyer team
     */
    public final String getBuyerName() {
        return buyerName;
    }

    /**
     * Sets the id of the buyer team.
     *
     * @param id Id of the buyer team
     */
    public final void setBuyerid(int id) {
        this.buyerid = id;

        if (buyerid == HOVerwaltung.instance().getModel().getBasics().getTeamId()) {
            if (sellerid !=  HOVerwaltung.instance().getModel().getBasics().getTeamId()) {
                this.type = BUY;
            } else {
                this.type = REBOUGHT;
            }
        }
    }

    /**
     * Gets the id of the buyer team.
     *
     * @return Id of the buyer team
     */
    public final int getBuyerid() {
        return buyerid;
    }

    /**
     * Sets the transfer date
     *
     * @param date Transfer date
     */
    public final void setDate(HODateTime date) {
        this.date = date;
    }

    /**
     * Gets the transfer date
     *
     * @return Transfer date
     */
    public final HODateTime getDate() {
        return date;
    }

    /**
     * Sets the id of the transfered player.
     *
     * @param id of the transfered player.
     */
    public final void setPlayerId(int id) {
        this.playerId = id;
    }

     /**
     * Gets the id of the transfered player.
     *
     * @return Id of the transfered player.
     */
    public final int getPlayerId() {
        return playerId;
    }

    /**
     * Gets the information about the player on transfer date.
     *
     * @return Player information if available. else <code>null</code>
     */
    public final Player getPlayerInfo() {
        if (playerInfo == null) {
            if (playerId == 0 && !this.isStored()) {
                // Try to find correct player id in previously stored transfer
                var transferInDb = DBManager.instance().loadPlayerTransfer(this.transferId);
                if (transferInDb != null) {
                    playerId = transferInDb.getPlayerId();
                }
            }
            var isPurchase = this.buyerid == HOVerwaltung.instance().getModel().getBasics().getTeamId();
            if (playerId > 0) {
                if (isPurchase) {
                    playerInfo = DBManager.instance().getFirstPlayerDownloadAfter(this.getPlayerId(), this.getDate().toDbTimestamp());
                } else {
                    // transfers of sold players (sellerid must not be equal to teamid)
                    playerInfo = DBManager.instance().getLatestPlayerDownloadBefore(this.getPlayerId(), this.getDate().toDbTimestamp());
                }
            } else if (playerId != -1) {
                HODateTime start = HODateTime.now();
                List<Player> playerInfos;
                if (isPurchase) {
                    playerInfos = DBManager.instance().getFirstPlayerDownloadAfter(this.getPlayerName(), this.getDate().toDbTimestamp());
                } else {
                    playerInfos = DBManager.instance().getLatestPlayerDownloadBefore(this.getPlayerName(), this.getDate().toDbTimestamp());
                }
                HOLogger.instance().debug(getClass(), this.getPlayerName() + " loaded candidate count: " + playerInfos.size() + " Started: " + start.toLocaleDateTime() + " Duration: " + HODateTime.between(start, HODateTime.now()).toString());
                if (playerInfos.size() > 1) {
                    // find most probable candidate
                    if (isPurchase) {
                        playerInfo = playerInfos.stream().min(Comparator.comparing(Player::getHrfDate)).get();
                    } else {
                        playerInfo = playerInfos.stream().max(Comparator.comparing(Player::getHrfDate)).get();
                    }
                } else if (playerInfos.size() == 1) {
                    playerInfo = playerInfos.get(0);
                }

                if (playerInfo != null) {
                    this.playerId = playerInfo.getPlayerID();
                    updatePlayerTransfer(this);
                }
            }
            if (playerInfo == null) {
                playerInfo = new Player();
                playerInfo.setLastName(this.getPlayerName());
                playerInfo.setTSI(this.getTsi());
                playerInfo.setPlayerID(-1); // non-existing player
                if (playerId != -1) {
                    setPlayerId(-1);
                    updatePlayerTransfer(this);
                }
            }
        }
        return playerInfo;
    }

    /**
     * Sets the name of the transferred player.
     *
     * @param name Name of the transferred player.
     */
    public final void setPlayerName(String name) {
        this.playerName = name;
    }

    /**
     * Gets the name of the transfered player.
     *
     * @return Name of the transfered player.
     */
    public final String getPlayerName() {
        return playerName;
    }

    /**
     * Sets the transfer price.
     * Hattrick currency (swedish crone)
     * @param price Transfer price.
     */
    public final void setPrice(Integer price) {
        if ( price != null ) this.price = price;
    }

    /**
     * Gets the tranfer price.
     *
     * @return Transfer price in Hattrick currency (swedish crone)
     */
    public final int getPrice() {
        return price;
    }

    /**
     * Gets the season number.
     *
     * @param season number on transfer date
     */
    public final void setSeason(int season) {
        this.season = season;
    }

    /**
     * Gets the season number.
     *
     * @return Season number on transfer date
     */
    public final int getSeason() {
        return season;
    }

    /**
     * Sets the name of the seller team.
     *
     * @param name Name of the seller team.
     */
    public final void setSellerName(String name) {
        this.sellerName = name;
    }

    /**
     * Gets the name of the seller team.
     *
     * @return Name of the seller team.
     */
    public final String getSellerName() {
        return sellerName;
    }

    /**
     * Sets the id of the seller team.
     *
     * @param id Id of the seller team.
     */
    public final void setSellerid(int id) {
        this.sellerid = id;

        if (sellerid ==  HOVerwaltung.instance().getModel().getBasics().getTeamId()) {
            if (buyerid !=  HOVerwaltung.instance().getModel().getBasics().getTeamId()) {
                this.type = SELL;
            } else {
                this.type = REBOUGHT;
            }
        }
    }

    /**
     * Gets the id of the seller team.
     *
     * @return Id of the seller team.
     */
    public final int getSellerid() {
        return sellerid;
    }

    /**
     * Gets the id of the transfer.
     *
     * @return Id of the transfer
     */
    public final int getTransferId() {
        return transferId;
    }
    public void setTransferId(int v){
        this.transferId = v;
    }

    /**
     * Sets the TSI value when player was tranfered.
     *
     * @param tsi TSI value on transfer date.
     */
    public final void setTsi(int tsi) {
        this.tsi = tsi;
    }

    /**
     * Gets the TSI value when player was tranfered.
     *
     * @return TSI value on transfer date
     */
    public final int getTsi() {
        return tsi;
    }

    /**
     * Gets the transfer type.
     *
     * @return Type
     */
    public final int getType() {
        return type;
    }

    /**
     * Gets the week number.
     *
     * @param week number on transfer date
     */
    public final void setWeek(int week) {
        this.week = week;
    }

    /**
     * Gets the week number.
     *
     * @return Week number on transfer date
     */
    public final int getWeek() {
        return week;
    }
}
