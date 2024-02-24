package core.db;

import core.model.HOVerwaltung;
import core.util.HODateTime;
import core.util.HOLogger;
import module.transfer.PlayerTransfer;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TransferTable extends AbstractTable {
	final static String TABLENAME = "TRANSFER";

    TransferTable(ConnectionManager adapter){
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
        columns = new ColumnDescriptor[]{
                ColumnDescriptor.Builder.newInstance().setColumnName("transferid").setGetter((p) -> ((PlayerTransfer) p).getTransferId()).setSetter((p, v) -> ((PlayerTransfer) p).setTransferId((int) v)).setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("date").setGetter((p) -> ((PlayerTransfer) p).getDate().toDbTimestamp()).setSetter((p, v) -> ((PlayerTransfer) p).setDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("week").setGetter((p) -> ((PlayerTransfer) p).getWeek()).setSetter((p, v) -> ((PlayerTransfer) p).setWeek((int) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("season").setGetter((p) -> ((PlayerTransfer) p).getSeason()).setSetter((p, v) -> ((PlayerTransfer) p).setSeason((int) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("playerid").setGetter((p) -> ((PlayerTransfer) p).getPlayerId()).setSetter((p, v) -> ((PlayerTransfer) p).setPlayerId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("playername").setGetter((p) -> ((PlayerTransfer) p).getPlayerName()).setSetter((p, v) -> ((PlayerTransfer) p).setPlayerName((String) v)).setType(Types.VARCHAR).setLength(127).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("buyerid").setGetter((p) -> ((PlayerTransfer) p).getBuyerid()).setSetter((p, v) -> ((PlayerTransfer) p).setBuyerid((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("buyername").setGetter((p) -> ((PlayerTransfer) p).getBuyerName()).setSetter((p, v) -> ((PlayerTransfer) p).setBuyerName((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("sellerid").setGetter((p) -> ((PlayerTransfer) p).getSellerid()).setSetter((p, v) -> ((PlayerTransfer) p).setSellerid((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("sellername").setGetter((p) -> ((PlayerTransfer) p).getSellerName()).setSetter((p, v) -> ((PlayerTransfer) p).setSellerName((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("price").setGetter((p) -> ((PlayerTransfer) p).getPrice()).setSetter((p, v) -> ((PlayerTransfer) p).setPrice((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("tsi").setGetter((p) -> ((PlayerTransfer) p).getTsi()).setSetter((p, v) -> ((PlayerTransfer) p).setTsi((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("motherclubfee").setGetter((p) -> ((PlayerTransfer) p).getMotherClubFee()).setSetter((p, v) -> ((PlayerTransfer) p).setMotherClubFee((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("previousclubcommission").setGetter((p) -> ((PlayerTransfer) p).getPreviousClubFee()).setSetter((p, v) -> ((PlayerTransfer) p).setPreviousClubFee((Integer) v)).setType(Types.INTEGER).isNullable(true).build()
        };
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
        return loadOne(PlayerTransfer.class, transferId);
    }

    private final String getAllTransfersSql = createSelectStatement(" WHERE playerid = ? ORDER BY date DESC");
    private final String getTransfersSql = createSelectStatement(" WHERE playerid = ? AND (buyerid=? OR sellerid=?) ORDER BY date DESC");

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
            return load(PlayerTransfer.class, this.connectionManager.executePreparedQuery(getTransfersSql, playerid, teamid, teamid));
        }
        return load(PlayerTransfer.class, this.connectionManager.executePreparedQuery(getAllTransfersSql, playerid));
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
     * Gets a list of transfers.
     *
     * @param teamId Team id to select transfers for.
     * @param season Season number for selecting transfers.
     * @param bought <code>true</code> to include BUY transfers.
     * @param sold <code>true</code> to include SELL transfers.
     *
     * @return List of transfers.
     */
    public List<PlayerTransfer> getTransfers(int teamId, int season, boolean bought, boolean sold) {
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
                params.add(teamId); //$NON-NLS-1$
            }

            if (bought && sold) {
                sqlStmt.append(" OR"); //$NON-NLS-1$
            }

            if (sold) {
                sqlStmt.append(" sellerid = ?");
                params.add(teamId); //$NON-NLS-1$
            }

            sqlStmt.append(")"); //$NON-NLS-1$
        }

        sqlStmt.append(" ORDER BY date DESC"); //$NON-NLS-1$

        var sql = sqlStmt.toString();
        return load(PlayerTransfer.class, this.connectionManager.executePreparedQuery(createSelectStatement(sql), params.toArray()));
    }

	/**
     * Adds a transfer to the HO database
     *
     * @param transfer Transfer information
     */
    public void storeTransfer(PlayerTransfer transfer) {
        if (!transfer.isStored()) {
            var isStored = isStored(transfer.getTransferId());
            transfer.setIsStored(isStored);
        }
        store(transfer);
    }

    private String transferIncomeSumSql = "SELECT SUM(PRICE) FROM " + getTableName() + " WHERE SELLERID=?";
    private String transferCostSumSql = "SELECT SUM(PRICE) FROM " + getTableName() + " WHERE BUYERID=?";
    public long getTransferIncomeSum(int teamId, boolean isSold) {
        var statement = isSold? transferIncomeSumSql : transferCostSumSql;

        try (var rs = this.connectionManager.executePreparedQuery(statement, teamId)) {
            if (rs != null) {
                rs.next();
                return rs.getLong(1);
            }
        }
        catch (SQLException sqlException){
            HOLogger.instance().error(getClass(), sqlException);
        }
        return 0;
    }

    public List<PlayerTransfer> getTeamTransfers(int teamId, boolean isSold) {
        final StringBuilder sqlStmt = new StringBuilder("WHERE ");
        if (isSold) {
            sqlStmt.append("SELLERID");
        } else {
            sqlStmt.append("BUYERID");
        }
        sqlStmt.append("=? ORDER BY DATE DESC");
        var sql = sqlStmt.toString();
        return load(PlayerTransfer.class, this.connectionManager.executePreparedQuery(createSelectStatement(sql), teamId));
    }

    private String getSumTransferCommissionsSql = "SELECT SUM(motherclubfee+previousclubcommission) FROM " + getTableName() + " WHERE date>=? AND date<?";

    public int getSumTransferCommissions(HODateTime startOfWeek) {
        var from = startOfWeek.toDbTimestamp();
        var to = startOfWeek.plus(7, ChronoUnit.DAYS).toDbTimestamp();

        try (var rs = this.connectionManager.executePreparedQuery(getSumTransferCommissionsSql, from, to)) {
            if (rs != null) {
                rs.next();
                return rs.getInt(1);
            }
        }
        catch (SQLException sqlException){
            HOLogger.instance().error(getClass(), sqlException);
        }
        return 0;
    }

    private final String getTransfersSinceSql = createSelectStatement(" WHERE Date >= ?");

    public List<PlayerTransfer> getTransfersSince(Timestamp dbTimestamp) {
        return load(PlayerTransfer.class, this.connectionManager.executePreparedQuery(getTransfersSinceSql, dbTimestamp));
    }
}
