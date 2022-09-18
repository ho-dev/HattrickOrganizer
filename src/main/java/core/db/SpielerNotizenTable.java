package core.db;

import core.model.player.Player;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

final class SpielerNotizenTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "SPIELERNOTIZ";
	
	SpielerNotizenTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[7];
		columns[0]= new ColumnDescriptor("SpielerID",Types.INTEGER,false,true);
		columns[1]= new ColumnDescriptor("Notiz",Types.VARCHAR,false,2048);
		columns[2]= new ColumnDescriptor("Spielberechtigt",Types.BOOLEAN,false);
		columns[3]= new ColumnDescriptor("TeamInfoSmilie",Types.VARCHAR,false,127);
		columns[4]= new ColumnDescriptor("ManuellerSmilie",Types.VARCHAR,false,127);
		columns[5]= new ColumnDescriptor("userPos",Types.INTEGER,false);
		columns[6]= new ColumnDescriptor("isFired",Types.BOOLEAN,false);
	}

	@Override
	protected  PreparedDeleteStatementBuilder createPreparedDeleteStatementBuilder(){
		return new PreparedDeleteStatementBuilder(this,"WHERE SPIELERID=?");
	}

	void store(Player.Notes notes) {

		try {
			executePreparedDelete(notes.getPlayerId());
			executePreparedInsert(
					notes.getPlayerId(),
					notes.getNote(),
					notes.isEligibleToPlay(),
					notes.getTeamInfoSmilie(),
					notes.getManuelSmilie(),
					notes.getUserPos(),
					notes.isFired()

			);
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "cannot store player notes: " + e);
		}
	}
	public Player.Notes load(int playerId) {
		try {
			var rs = executePreparedSelect(playerId);
			if (rs != null) {
				if (rs.next()) {
					return createPlayerNotes(rs);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "Player.Notes load error");
		}
		return new Player.Notes();
	}

	private Player.Notes createPlayerNotes(ResultSet rs) throws SQLException {
		var ret = new Player.Notes();
		ret.setPlayerId(rs.getInt("SpielerID"));
		ret.setNote(rs.getString("Notiz"));
		ret.setEligibleToPlay(rs.getBoolean("Spielberechtigt"));
		ret.setTeamInfoSmilie(rs.getString("TeamInfoSmilie"));
		ret.setManuelSmilie(rs.getString("ManuellerSmilie"));
		ret.setUserPos(rs.getInt("userPos"));
		ret.setIsFired(rs.getBoolean("isFired"));

		return ret;
	}
}
