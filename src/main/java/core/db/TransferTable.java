package core.db;

import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.HODateTime;
import module.transfer.PlayerRetriever;
import module.transfer.PlayerTransfer;
import module.transfer.XMLParser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

public class TransferTable extends AbstractTable {
	final static String TABLENAME = "TRANSFER";
	
	TransferTable(JDBCAdapter adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[13];
		columns[0]= new ColumnDescriptor("transferid",Types.INTEGER,false,true);
		columns[1]= new ColumnDescriptor("date",Types.TIMESTAMP,true);
		columns[2]= new ColumnDescriptor("week",Types.INTEGER,true);
		columns[3]= new ColumnDescriptor("season",Types.INTEGER,true);
		columns[4]= new ColumnDescriptor("playerid",Types.INTEGER,false);
		columns[5]= new ColumnDescriptor("playername",Types.VARCHAR,true,127);
		columns[6]= new ColumnDescriptor("buyerid",Types.INTEGER,true);
		columns[7]= new ColumnDescriptor("buyername",Types.VARCHAR,true,256);
		columns[8]= new ColumnDescriptor("sellerid",Types.INTEGER,true);
		columns[9]= new ColumnDescriptor("sellername",Types.VARCHAR,true,256);
		columns[10]= new ColumnDescriptor("price",Types.INTEGER,true);
		columns[11]= new ColumnDescriptor("marketvalue",Types.INTEGER,true);
		columns[12]= new ColumnDescriptor("tsi",Types.INTEGER,true);
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
			"CREATE INDEX pl_id ON " + getTableName() + "(" + columns[4].getColumnName() + ")",
			"CREATE INDEX buy_id ON " + getTableName() + "(" + columns[6].getColumnName() + ")",
			"CREATE INDEX sell_id ON " + getTableName() + "(" + columns[8].getColumnName() + ")"};
	}

    /**
     * Remove a transfer from the HO database
     *
     * @param transferId Transfer ID
     */
    public void removeTransfer(int transferId) {
    	try {
            adapter.executeUpdate("DELETE FROM " + getTableName() + " WHERE transferid= "+transferId);
    	} catch (Exception e) {
    		// ignore
    	}
    }

    /**
     * Gets requested transfer
     *
     * @param transferId Transfer ID
     */
    public PlayerTransfer getTransfer(int transferId) {
        List<PlayerTransfer> result = loadTransfers("SELECT * FROM " + getTableName() + " WHERE transferid = "+ transferId);
        if (result.size() > 0) return result.get(0);
        return null;
    }
	
    /**
     * Gets a list of transfers.
     *
     * @param playerid Player id for selecting transfers.
     * @param allTransfers If <code>false</code> this method will only return transfers for your
     *        own team, otherwise it will return all transfers for the player.
     *
     * @return List of transfers.
     */
    public List<PlayerTransfer> getTransfers(int playerid, boolean allTransfers) {
        final StringBuilder sqlStmt = new StringBuilder("SELECT * FROM " + getTableName());
        sqlStmt.append(" WHERE playerid = ").append(playerid);

        if (!allTransfers) {
            final int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
            sqlStmt.append(" AND (buyerid = ").append(teamid);
            sqlStmt.append(" OR sellerid = ").append(teamid).append(")");
        }

        sqlStmt.append(" ORDER BY date DESC"); 

        return loadTransfers(sqlStmt.toString());
    }
    
    /**
     * Gets a list of transfers for your own team.
     *
     * @param season Season number for selecting transfers.
     * @param bought <code>true</code> to include BUY transfers.
     * @param sold <code>true</code> to include SELL transfers.
     *
     * @return List of transfers.
     */
    public List<PlayerTransfer> getTransfers(int season, boolean bought, boolean sold) {
        final int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
        return getTransfers(teamid, season, bought, sold);
    }
    
    /**
     * Update transfer data for a team from the HT xml.
     *
     * Returns false if this fails
     *
     * @param teamid Team id to update data for
     */
    public boolean updateTeamTransfers(int teamid) {
        try {
            final List<Player> players = new Vector<>();
            final List<PlayerTransfer> transfers = XMLParser.getAllTeamTransfers(teamid, HODateTime.now().plus(1, ChronoUnit.DAYS));

            for (PlayerTransfer transfer : transfers) {
                final Player player = PlayerRetriever.getPlayer(transfer);

                if (player != null) {
                    if (!players.contains(player)) players.add(player);
                    if (transfer.getPlayerId() == 0) {
                        int playerIdFound = player.getPlayerID();
                        transfer.setPlayerId(playerIdFound);
                        DBManager.instance().saveIsSpielerFired(playerIdFound, true);
                    }
                } else {
                    PlayerTransfer alreadyInDB = getTransfer(transfer.getTransferID());
                    if (alreadyInDB != null) {
                        if (transfer.getPlayerId() == 0) {
                            DBManager.instance().saveIsSpielerFired(alreadyInDB.getPlayerId(), true);
//                            continue;
                        } else {
                            Player dummy = new Player();
                            dummy.setPlayerID(transfer.getPlayerId());
                            if (!players.contains(dummy)) players.add(dummy);
                        }
                    }
                }

                addTransfer(transfer);
            }

            for (Player player : players) {
                int playerID = player.getPlayerID();
                if (!DBManager.instance().getIsSpielerFired(playerID)) {
                    updatePlayerTransfers(playerID);
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
          }
    }
    
    /**
     * Update transfer data for a player.
     *
     * @param playerId Player
     */
    public void updatePlayerTransfers(int playerId) {
        try {
            final List<PlayerTransfer> transfers = XMLParser.getAllPlayerTransfers(playerId);

            if (transfers.size() > 0) {
                for (final PlayerTransfer transfer : transfers) {
                    addTransfer(transfer);
                }
            } else DBManager.instance().saveIsSpielerFired(playerId, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * Gets a list of transfers.
     *
     * @param teamid Team id to select transfers for.
     * @param season Season number for selecting transfers.
     * @param bought <code>true</code> to include BUY transfers.
     * @param sold <code>true</code> to include SELL transfers.
     *
     * @return List of transfers.
     */
    public List<PlayerTransfer> getTransfers(int teamid, int season, boolean bought, boolean sold) {
        final StringBuilder sqlStmt = new StringBuilder("SELECT * FROM " + getTableName()); //$NON-NLS-1$
        sqlStmt.append(" WHERE 1=1"); //$NON-NLS-1$

        if (season != 0) {
            sqlStmt.append(" AND season = ").append(season); //$NON-NLS-1$
        }

        if (bought || sold) {
            sqlStmt.append(" AND ("); //$NON-NLS-1$

            if (bought) {
                sqlStmt.append(" buyerid = ").append(teamid); //$NON-NLS-1$
            }

            if (bought && sold) {
                sqlStmt.append(" OR"); //$NON-NLS-1$
            }

            if (sold) {
                sqlStmt.append(" sellerid = ").append(teamid); //$NON-NLS-1$
            }

            sqlStmt.append(")"); //$NON-NLS-1$
        }

        sqlStmt.append(" ORDER BY date DESC"); //$NON-NLS-1$

        return loadTransfers(sqlStmt.toString());
    }
	/**
     * Adds a tranfer to the HO database
     *
     * @param transfer Transfer information
     *
     * @return Boolean to indicate if the transfer is sucessfully added.
     */
    private boolean addTransfer(PlayerTransfer transfer) {
    	removeTransfer(transfer.getTransferID());
        final StringBuilder sqlStmt = new StringBuilder("INSERT INTO " + getTableName());
        sqlStmt.append("(transferid, date, week, season, playerid, playername, buyerid, buyername, sellerid, sellername, price, marketvalue, tsi)"); 
        sqlStmt.append(" VALUES ("); 
        sqlStmt.append(transfer.getTransferID()).append(",");
        sqlStmt.append("'").append(transfer.getDate().toDbTimestamp()).append("',");
        sqlStmt.append(transfer.getWeek()).append(",");
        sqlStmt.append(transfer.getSeason()).append(",");
        sqlStmt.append(transfer.getPlayerId()).append(",");
        sqlStmt.append("'").append(DBManager.insertEscapeSequences(transfer.getPlayerName())).append("',");
        sqlStmt.append(transfer.getBuyerid()).append(",");
        sqlStmt.append("'").append(DBManager.insertEscapeSequences(transfer.getBuyerName())).append("',");
        sqlStmt.append(transfer.getSellerid()).append(",");
        sqlStmt.append("'").append(DBManager.insertEscapeSequences(transfer.getSellerName())).append("',");
        sqlStmt.append(transfer.getPrice()).append(",");
        sqlStmt.append(transfer.getMarketvalue()).append(",");
        sqlStmt.append(transfer.getTsi());
        sqlStmt.append(" )"); 

        try {
            DBManager.instance().getAdapter().executeUpdate(sqlStmt.toString());
            return true;
        } catch (Exception inore) {
            return false;
        }
    }
	
    /**
     * Loads a list of transfers from the HO database.
     *
     * @param sqlStmt SQL statement.
     *
     * @return List of transfers
     */
    private List<PlayerTransfer> loadTransfers(String sqlStmt) {
        final double curr_rate = HOVerwaltung.instance().getModel().getXtraDaten().getCurrencyRate();

        final List<PlayerTransfer> results = new Vector<>();
        final ResultSet rs = DBManager.instance().getAdapter().executeQuery(sqlStmt);

        if (rs == null) {
            return new Vector<>();
        }

        try {
            while (rs.next()) {
                PlayerTransfer transfer = new PlayerTransfer(rs.getInt("transferid"),rs.getInt("playerid"));  
                transfer.setPlayerName( DBManager.deleteEscapeSequences(rs.getString("playername"))); 
                transfer.setDate(HODateTime.fromDbTimestamp(rs.getTimestamp("date")));
                transfer.setWeek(rs.getInt("week")); 
                transfer.setSeason(rs.getInt("season")); 

                transfer.setBuyerid(rs.getInt("buyerid")); 
                transfer.setBuyerName( DBManager.deleteEscapeSequences(rs.getString("buyername"))); 
                transfer.setSellerid(rs.getInt("sellerid")); 
                transfer.setSellerName( DBManager.deleteEscapeSequences(rs.getString("sellername"))); 

                transfer.setPrice((int) (rs.getInt("price") / curr_rate)); 
                transfer.setMarketvalue((int) (rs.getInt("marketvalue") / curr_rate)); 
                transfer.setTsi(rs.getInt("tsi")); 

                results.add(transfer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (PlayerTransfer transfer : results) {
            final Player player = DBManager.instance().getSpielerAtDate(transfer.getPlayerId(), transfer.getDate().toDbTimestamp());

            if (player != null) {
                transfer.setPlayerInfo(player);
            }
        }

        return results;
    }
}
