package module.transfer.test;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.util.HODateTime;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Transfer {

	HODateTime purchaseDate;
	HODateTime sellingDate;
	int purchasePrice;
	int sellingPrice;
	
	static private final DBManager.PreparedStatementBuilder transferStatementBuilder = new DBManager.PreparedStatementBuilder(
			"Select price, BUYERID, sellerid, date from transfer WHERE PLAYERID=? and (BUYERID=? OR SELLERID=?");
	public static Transfer getTransfer(int playerId) {
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		ResultSet rs = Objects.requireNonNull(DBManager.instance().getAdapter()).executePreparedQuery(transferStatementBuilder.getStatement(), playerId, teamId, teamId);
		Transfer t = new Transfer();
		try {
			while (true) {
				assert rs != null;
				if (!rs.next()) break;
				// TODO player might by bought/sold multiple times by the same team
				if (rs.getInt("BUYERID") == teamId) {
					t.purchasePrice = rs.getInt("price");
					t.purchaseDate = HODateTime.fromDbTimestamp(rs.getTimestamp("date"));
				}
				if (rs.getInt("sellerid") == teamId) {
					t.sellingPrice = rs.getInt("price");
					t.sellingDate = HODateTime.fromDbTimestamp(rs.getTimestamp("date"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return t;
	}
}
