package core.db;

import core.model.player.Player;
import java.sql.Types;

final class SpielerNotizenTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "SPIELERNOTIZ";
	
	SpielerNotizenTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("SpielerID").setGetter((o) -> ((Player.Notes) o).getPlayerId()).setSetter((o, v) -> ((Player.Notes) o).setPlayerId((int) v)).setType(Types.INTEGER).isNullable(false).isPrimaryKey(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Notiz").setGetter((o) -> ((Player.Notes) o).getNote()).setSetter((o, v) -> ((Player.Notes) o).setNote((String) v)).setType(Types.VARCHAR).setLength(2048).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Spielberechtigt").setGetter((o) -> ((Player.Notes) o).isEligibleToPlay()).setSetter((o, v) -> ((Player.Notes) o).setEligibleToPlay((boolean) v)).setType(Types.BOOLEAN).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TeamInfoSmilie").setGetter((o) -> ((Player.Notes) o).getTeamInfoSmilie()).setSetter((o, v) -> ((Player.Notes) o).setTeamInfoSmilie((String) v)).setType(Types.VARCHAR).setLength(127).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ManuellerSmilie").setGetter((o) -> ((Player.Notes) o).getManuelSmilie()).setSetter((o, v) -> ((Player.Notes) o).setManuelSmilie((String) v)).setType(Types.VARCHAR).setLength(127).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("userPos").setGetter((o) -> ((Player.Notes) o).getUserPos()).setSetter((o, v) -> ((Player.Notes) o).setUserPos((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("isFired").setGetter((o) -> ((Player.Notes) o).isFired()).setSetter((o, v) -> ((Player.Notes) o).setIsFired((boolean) v)).setType(Types.BOOLEAN).isNullable(false).build()
		};
	}

	void storeNotes(Player.Notes notes) {
		notes.setIsStored(isStored(notes.getPlayerId()));
		store(notes);
	}
	public Player.Notes load(int playerId) {
		var ret =  loadOne(Player.Notes.class, playerId);
		if ( ret == null) ret = new Player.Notes();
		return ret;
	}
}
