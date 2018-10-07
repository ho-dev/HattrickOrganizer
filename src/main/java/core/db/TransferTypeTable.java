package core.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class TransferTypeTable extends AbstractTable {

	final static String TABLENAME = "TRANSFERTYPE";
	
	TransferTypeTable(JDBCAdapter adapter){
		super(TABLENAME,adapter);
	}
	
	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[2];
		columns[0]= new ColumnDescriptor("PLAYER_ID",Types.INTEGER,false,true);
		columns[1]= new ColumnDescriptor("TYPE",Types.INTEGER,true);

	}
	
    void setTransferType(int playerId, int type) {
        final String query = "update " + getTableName() + " set TYPE = " + type + " where PLAYER_ID ="
                       + playerId; 
        final int count = adapter.executeUpdate(query);

        if (count == 0) {
        	adapter.executeUpdate("insert into " + getTableName()
                                                          + " (PLAYER_ID, TYPE) values ("
                                                          + playerId + "," + type + ")"); 
        }
    }
    
    int getTransferType(int playerId) {
        final String query = "select TYPE from " + getTableName() + " where PLAYER_ID=" + playerId; 
        final ResultSet rs = adapter.executeQuery(query);

        try {
            rs.next();
            return rs.getInt("TYPE"); 
        } catch (SQLException e) {
            return -2;
        }
    }

}
