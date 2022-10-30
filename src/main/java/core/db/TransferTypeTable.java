package core.db;

import module.transfer.TransferType;

import java.sql.Types;

public class TransferTypeTable extends AbstractTable {

	final static String TABLENAME = "TRANSFERTYPE";
	
	TransferTypeTable(JDBCAdapter adapter){
		super(TABLENAME,adapter);
	}
	
	@Override
	protected void initColumns() {
        columns = new ColumnDescriptor[]{
                ColumnDescriptor.Builder.newInstance().setColumnName("PLAYER_ID").setGetter((p) -> ((TransferType) p).getPlayerId()).setSetter((p, v) -> ((TransferType) p).setPlayerId((int) v)).setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("TYPE").setGetter((p) -> ((TransferType) p).getTransferType()).setSetter((p, v) -> ((TransferType) p).setTransferType((Integer) v)).setType(Types.INTEGER).isNullable(true).build()
        };
	}

    void storeTransferType(TransferType type) {
        type.setIsStored(isStored(type.getPlayerId()));
        store(type);
    }
    
    TransferType loadTransferType(int playerId) {
        return loadOne(TransferType.class, playerId);
    }

}
