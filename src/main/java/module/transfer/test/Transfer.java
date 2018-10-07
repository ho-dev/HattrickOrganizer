package module.transfer.test;

import core.db.DBManager;
import core.model.HOVerwaltung;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Transfer {

	Date purchaseDate;
	Date sellingDate;
	int purchasePrice;
	int sellingPrice;
	
	
	public static Transfer getTransfer(int playerId) {
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		String query = "Select price, BUYERID, sellerid, date from transfer WHERE PLAYERID=" + playerId
				+ " and (BUYERID=" + teamId + " OR SELLERID=" + teamId + ")";

		ResultSet rs = DBManager.instance().getAdapter().executeQuery(query);
		Transfer t = new Transfer();
		try {
			while (rs.next()) {
				// TODO player might by bought/sold multiple times by the same team
				if (rs.getInt("BUYERID") == teamId) {
					t.purchasePrice = rs.getInt("price");
					t.purchaseDate = rs.getDate("date");
				}
				if (rs.getInt("sellerid") == teamId) {
					t.sellingPrice = rs.getInt("price");
					t.sellingDate = rs.getDate("date");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return t;
	}
}
