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

    @Override
    protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder(){
        return new PreparedSelectStatementBuilder(this, "WHERE PLAYER_ID=?");
    }
    protected PreparedDeleteStatementBuilder  createPreparedDeleteStatementBuilder(){
        return new PreparedDeleteStatementBuilder(this, "WHERE PLAYER_ID=?");
    }

    void setTransferType(int playerId, int type) {
        executePreparedDelete(playerId);
        executePreparedInsert(
                playerId,
                type
        );
    }
    
    int getTransferType(int playerId) {
        final ResultSet rs = executePreparedSelect(playerId);
        try {
            rs.next();
            return rs.getInt("TYPE"); 
        } catch (SQLException e) {
            return -2;
        }
    }

}
