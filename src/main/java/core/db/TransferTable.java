package core.db;

import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.HODateTime;
import module.transfer.PlayerRetriever;
import module.transfer.PlayerTransfer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class TransferTable extends AbstractTable {
	final static String TABLENAME = "TRANSFER";
    private final HashMap<String, PreparedStatement> getTransferStatements = new HashMap<>();

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
            executePreparedDelete(transferId);
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
        List<PlayerTransfer> result = loadTransfers(executePreparedSelect( transferId));
        if (result.size() > 0) return result.get(0);
        return null;
    }

    private final PreparedSelectStatementBuilder getAllTransfersStatementBuilder = new PreparedSelectStatementBuilder(this, " WHERE playerid = ? ORDER BY date DESC");
    private final PreparedSelectStatementBuilder getTransfersStatementBuilder = new PreparedSelectStatementBuilder(this, " WHERE playerid = ? AND (buyerid=? OR sellerid=?) ORDER BY date DESC");

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
        if (!allTransfers) {
            final int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
            return loadTransfers(this.adapter.executePreparedQuery(getTransfersStatementBuilder.getStatement(), playerid, teamid, teamid));
        }
        return loadTransfers(this.adapter.executePreparedQuery(getAllTransfersStatementBuilder.getStatement(), playerid));
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
     * Returns false if this fails
     *
     * @param transfers player transfers
     */
    public List<Player> updateTeamTransfers(List<PlayerTransfer> transfers) {
        try {
            final List<Player> players = new ArrayList<>();
            for (PlayerTransfer transfer : transfers) {
                final Player player = PlayerRetriever.getPlayer(transfer);

                if (player != null) {
                    if (!players.contains(player)) players.add(player);
                    if (transfer.getPlayerId() == 0) {
                        int playerIdFound = player.getPlayerID();
                        transfer.setPlayerId(playerIdFound);
                        player.setIsFired(true);
                    }
                } else {
                    PlayerTransfer alreadyInDB = getTransfer(transfer.getTransferID());
                    if (alreadyInDB != null) {
                        if (transfer.getPlayerId() == 0) {
                            var pl = PlayerRetriever.getPlayer(alreadyInDB);
                            if ( pl != null) pl.setIsFired(true);
//                            DBManager.instance().saveIsSpielerFired(alreadyInDB.getPlayerId(), true);
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

            return players.stream().filter(i->!i.isFired()).toList();

//            for (Player player : players) {
//                int playerID = player.getPlayerID();
//                if (!player.isFired()) {
//                    updatePlayerTransfers(playerID);
//                }
//            }
//
//            return true;
        } catch (Exception e) {
            e.printStackTrace();
//            return false;
          }

        return null;
    }
    
    /**
     * Update transfer data for a player.
     *
     * @param transfers Player
     */
    public void updatePlayerTransfers(List<PlayerTransfer> transfers) {
        try {
            for (final PlayerTransfer transfer : transfers) {
                addTransfer(transfer);
            }
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
        final StringBuilder sqlStmt = new StringBuilder(); //$NON-NLS-1$

        var params = new ArrayList<>();
        var sep = " WHERE";
        if (season != 0) {
            sqlStmt.append(sep).append(" season = ?");
            params.add(season); //$NON-NLS-1$
            sep = " AND";
        }

        if (bought || sold) {
            sqlStmt.append(sep).append(" ("); //$NON-NLS-1$

            if (bought) {
                sqlStmt.append(" buyerid = ?");
                params.add(teamid); //$NON-NLS-1$
            }

            if (bought && sold) {
                sqlStmt.append(" OR"); //$NON-NLS-1$
            }

            if (sold) {
                sqlStmt.append(" sellerid = ?");
                params.add(teamid); //$NON-NLS-1$
            }

            sqlStmt.append(")"); //$NON-NLS-1$
        }

        sqlStmt.append(" ORDER BY date DESC"); //$NON-NLS-1$

        var sql = sqlStmt.toString();
        var statement = getTransferStatements.get(sql);
        if ( statement == null){
            statement = new PreparedSelectStatementBuilder(this, sql).getStatement();
            getTransferStatements.put(sql, statement);
        }
        return loadTransfers(this.adapter.executePreparedQuery(statement, params.toArray()));
    }
	/**
     * Adds a transfer to the HO database
     *
     * @param transfer Transfer information
     */
    private void addTransfer(PlayerTransfer transfer) {
    	removeTransfer(transfer.getTransferID());
        try {
            executePreparedInsert(
                    transfer.getTransferID(),
                    transfer.getDate().toDbTimestamp(),
                    transfer.getWeek(),
                    transfer.getSeason(),
                    transfer.getPlayerId(),
                    transfer.getPlayerName(),
                    transfer.getBuyerid(),
                    transfer.getBuyerName(),
                    transfer.getSellerid(),
                    transfer.getSellerName(),
                    transfer.getPrice(),
                    transfer.getMarketvalue(),
                    transfer.getTsi()
            );
        } catch (Exception ignored) {
        }
    }
	
    /**
     * Loads a list of transfers from the HO database.
     *
     * @param rs ResultSet
     *
     * @return List of transfers
     */
    private List<PlayerTransfer> loadTransfers(ResultSet rs) {
        double curr_rate=1.;
        var xtra = HOVerwaltung.instance().getModel().getXtraDaten();
        if ( xtra != null ){
            curr_rate = xtra.getCurrencyRate();
        }
        final List<PlayerTransfer> results = new Vector<>();
        if (rs == null) {
            return new Vector<>();
        }
        try {
            while (rs.next()) {
                PlayerTransfer transfer = new PlayerTransfer(rs.getInt("transferid"),rs.getInt("playerid"));  
                transfer.setPlayerName( rs.getString("playername"));
                transfer.setDate(HODateTime.fromDbTimestamp(rs.getTimestamp("date")));
                transfer.setWeek(rs.getInt("week")); 
                transfer.setSeason(rs.getInt("season")); 

                transfer.setBuyerid(rs.getInt("buyerid")); 
                transfer.setBuyerName(rs.getString("buyername"));
                transfer.setSellerid(rs.getInt("sellerid")); 
                transfer.setSellerName( rs.getString("sellername"));

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
