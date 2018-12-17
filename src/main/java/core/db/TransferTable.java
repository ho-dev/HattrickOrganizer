package core.db;

import core.model.HOVerwaltung;
import core.model.player.Spieler;
import core.util.HTCalendar;
import core.util.HTCalendarFactory;
import module.transfer.PlayerRetriever;
import module.transfer.PlayerTransfer;
import module.transfer.XMLParser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Iterator;
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
	protected String[] getCreateIndizeStatements() {
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
     * Gets a list of transfers.
     *
     * @param playerid Player id for selecting transfers.
     * @param allTransfers If <code>false</code> this method will only return transfers for your
     *        own team, otherwise it will return all transfers for the player.
     *
     * @return List of transfers.
     */
    public List<PlayerTransfer> getTransfers(int playerid, boolean allTransfers) {
        final StringBuffer sqlStmt = new StringBuffer("SELECT * FROM " + getTableName()); 
        sqlStmt.append(" WHERE playerid = " + playerid); 

        if (!allTransfers) {
            final int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
            sqlStmt.append(" AND (buyerid = " + teamid); 
            sqlStmt.append(" OR sellerid = " + teamid + ")"); 
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
     * Reload transfer data for a team from the HT xml.
     *
     * @param teamid Team id to reload data for
     *
     * @throws Exception If an error occurs.
     */
    public void reloadTeamTransfers(int teamid) {
        DBManager.instance().getAdapter().executeUpdate("DELETE FROM " + getTableName()
                                                      + " WHERE buyerid = " + teamid
                                                      + " OR sellerid = " + teamid);
        updateTeamTransfers(teamid);
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
            final List<Spieler> players = new Vector<Spieler>();

            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1);

            final List<PlayerTransfer> transfers = XMLParser.getAllTeamTransfers(teamid, HTCalendar.resetDay(cal.getTime()));

            for (Iterator<PlayerTransfer> iter = transfers.iterator(); iter.hasNext();) {
                PlayerTransfer transfer = iter.next();
                addTransfer(transfer);

                final Spieler player = PlayerRetriever.getPlayer(transfer.getPlayerId());

                if (player != null) {
                    players.add(player);
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
                for (Iterator<PlayerTransfer> iter = transfers.iterator(); iter.hasNext();) {
                    final PlayerTransfer transfer = iter.next();
                    addTransfer(transfer);
                }
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
        final StringBuffer sqlStmt = new StringBuffer("SELECT * FROM " + getTableName()); //$NON-NLS-1$
        sqlStmt.append(" WHERE 1=1"); //$NON-NLS-1$

        if (season != 0) {
            sqlStmt.append(" AND season = " + season); //$NON-NLS-1$
        }

        if (bought || sold) {
            sqlStmt.append(" AND ("); //$NON-NLS-1$

            if (bought) {
                sqlStmt.append(" buyerid = " + teamid); //$NON-NLS-1$
            }

            if (bought && sold) {
                sqlStmt.append(" OR"); //$NON-NLS-1$
            }

            if (sold) {
                sqlStmt.append(" sellerid = " + teamid); //$NON-NLS-1$
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
        final StringBuffer sqlStmt = new StringBuffer("INSERT INTO " + getTableName()); 
        sqlStmt.append("(transferid, date, week, season, playerid, playername, buyerid, buyername, sellerid, sellername, price, marketvalue, tsi)"); 
        sqlStmt.append(" VALUES ("); 
        sqlStmt.append(transfer.getTransferID() + ","); 
        sqlStmt.append("'" + transfer.getDate().toString() + "',"); 
        sqlStmt.append(transfer.getWeek() + ","); 
        sqlStmt.append(transfer.getSeason() + ","); 
        sqlStmt.append(transfer.getPlayerId() + ",");
        sqlStmt.append("'"+ DBManager.insertEscapeSequences(transfer.getPlayerName())+ "',"); 
        sqlStmt.append(transfer.getBuyerid() + ","); 
        sqlStmt.append("'"+ DBManager.insertEscapeSequences(transfer.getBuyerName())+ "',"); 
        sqlStmt.append(transfer.getSellerid() + ","); 
        sqlStmt.append("'"+ DBManager.insertEscapeSequences(transfer.getSellerName())+ "',"); 
        sqlStmt.append(transfer.getPrice() + ","); 
        sqlStmt.append(transfer.getMarketvalue() + ","); 
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

        final List<PlayerTransfer> results = new Vector<PlayerTransfer>();
        final ResultSet rs = DBManager.instance().getAdapter().executeQuery(sqlStmt.toString());

        if (rs == null) {
            return new Vector<PlayerTransfer>();
        }

        try {
            while (rs.next()) {
                PlayerTransfer transfer = new PlayerTransfer(rs.getInt("transferid"),rs.getInt("playerid"));  
                transfer.setPlayerName( DBManager.deleteEscapeSequences(rs.getString("playername"))); 
                transfer.setDate(rs.getTimestamp("date")); 
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

        for (Iterator<PlayerTransfer> iter = results.iterator(); iter.hasNext();) {
            PlayerTransfer transfer = iter.next();
            final Spieler spieler = DBManager.instance().getSpielerAtDate(transfer.getPlayerId(),transfer.getDate());

            if (spieler != null) {
            	int transferSeason = HTCalendarFactory.getHTSeason(transfer.getDate());
                int transferWeek = HTCalendarFactory.getHTWeek(transfer.getDate());
                int spielerSeason = HTCalendarFactory.getHTSeason(spieler.getHrfDate());
                int spielerWeek = HTCalendarFactory.getHTWeek(spieler.getHrfDate());

                // Not in the same week, possible skillup so skip it
                if (((transferSeason * 16) + transferWeek) == ((spielerSeason * 16) + spielerWeek)) {
                    transfer.setPlayerInfo(spieler);
                }
            }
        }

        return results;
    }
}
