package core.db;

import core.model.match.MatchLineupPlayer;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static core.model.player.IMatchRoleID.aFieldMSubsAndBackupMatchRoleID;
import static core.model.player.IMatchRoleID.aPositionBehaviours;


public final class MatchLineupPlayerTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "MATCHLINEUPPLAYER";											
	
	protected MatchLineupPlayerTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[16];
		columns[0]= new ColumnDescriptor("MatchID",Types.INTEGER,false);
		columns[1]= new ColumnDescriptor("TeamID",Types.INTEGER,false);
		columns[2]= new ColumnDescriptor("SpielerID",Types.INTEGER,false);
		columns[3]= new ColumnDescriptor("RoleID",Types.INTEGER,false);
		columns[4]= new ColumnDescriptor("Taktik",Types.INTEGER,false);
		columns[5]= new ColumnDescriptor("PositionCode",Types.INTEGER,false);
		columns[6]= new ColumnDescriptor("VName",Types.VARCHAR,false,255);
		columns[7]= new ColumnDescriptor("NickName",Types.VARCHAR,false,255);
		columns[8]= new ColumnDescriptor("Name",Types.VARCHAR,false,255);
		columns[9]= new ColumnDescriptor("Rating",Types.REAL,false);
		columns[10]= new ColumnDescriptor("HoPosCode",Types.INTEGER,false);
		columns[11]= new ColumnDescriptor("STATUS",Types.INTEGER,false);
		columns[12]= new ColumnDescriptor("FIELDPOS",Types.INTEGER,false);
		columns[13]= new ColumnDescriptor("RatingStarsEndOfMatch", Types.REAL, false);
		columns[14]= new ColumnDescriptor("StartPosition", Types.INTEGER, false);
		columns[15]= new ColumnDescriptor("StartBehaviour", Types.INTEGER, false);
	}

	@Override
	protected String[] getCreateIndexStatement(){
		return new String[]{
			"CREATE INDEX iMATCHLINEUPPLAYER_1 ON "+getTableName()+"("+columns[2].getColumnName()+")",
			"CREATE INDEX iMATCHLINEUPPLAYER_2 ON "+getTableName()+"("+columns[0].getColumnName()+","+columns[1].getColumnName()+")"
		};
	}
	
	/**
	 Returns a list of ratings the player has played on: 0: Max,  1: Min,  2: Average,  3: posid
	 */
	Vector<float[]> getAllRatings(int playerID) {
		final Vector<float[]> ratings = new Vector<>();

		//Iterate over possible combinations of position / behaviours
		for (int i: aPositionBehaviours) {
			final float[] temp = getPlayerRatingForPosition(playerID, i);

			//Min found a value for the pos -> max> 0
			if (temp[0] > 0) {
				// Fill in the first value instead of the current value with the posid
				temp[3] = i;
				ratings.add(temp);
			}
		}

		return ratings;
	}
	
	/**
	 * Gibt die beste, schlechteste und durchschnittliche Bewertung für den Player, sowie die
	 * Anzahl der Bewertungen zurück // Match
	 */
	float[] getBewertungen4Player(int spielerid) {
		//Max, Min, Durchschnitt
		final float[] bewertungen = { 0f, 0f, 0f, 0f };

		try {
			final String sql = "SELECT MatchID, Rating FROM "+getTableName()+" WHERE SpielerID=" + spielerid;
			final ResultSet rs = adapter.executeQuery(sql);

			rs.beforeFirst();

			int i = 0;

			while (rs.next()) {
				float rating = rs.getFloat("Rating");

				if (rating > -1) {
					bewertungen[0] = Math.max(bewertungen[0], rating);

					if (bewertungen[1] == 0) {
						bewertungen[1] = rating;
					}

					bewertungen[1] = Math.min(bewertungen[1], rating);
					bewertungen[2] += rating;

					//HOLogger.instance().log(getClass(),rs.getInt("MatchID") + " : " + rating);

					i++;
				}
			}

			if (i > 0) {
				bewertungen[2] = (bewertungen[2] / i);
			}

			bewertungen[3] = i;

			//HOLogger.instance().log(getClass(),"Ratings     : " + i + " - " + bewertungen[0] + " / " + bewertungen[1] + " / " + bewertungen[2] + " / / " + bewertungen[3]);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getBewertungen4Player : " + e);
		}

		return bewertungen;
	}
	
	/**
	 * Returns the best, worst, and average rating for the player, as well as the number of ratings // match
	 *  @param spielerid Spielerid
	 * @param position Usere positionscodierung mit taktik
	 */
	float[] getPlayerRatingForPosition(int spielerid, int position) {
		//Max, Min, average
		final float[] starsStatistics = { 0f, 0f, 0f, 0f };

		try {
			final String sql = "SELECT MatchID, Rating FROM "+getTableName()+" WHERE SpielerID=" + spielerid + " AND HoPosCode=" + position;
			final ResultSet rs = adapter.executeQuery(sql);

			rs.beforeFirst();

			int i = 0;

			while (rs.next()) {
				float rating = rs.getFloat("Rating");

				if (rating > -1) {
					starsStatistics[0] = Math.max(starsStatistics[0], rating);

					if (starsStatistics[1] == 0) {
						starsStatistics[1] = rating;
					}

					starsStatistics[1] = Math.min(starsStatistics[1], rating);
					starsStatistics[2] += rating;

					//HOLogger.instance().log(getClass(),rs.getInt("MatchID") + " : " + rating);

					i++;
				}
			}

			if (i > 0) {
				starsStatistics[2] = (starsStatistics[2] / i);
			}

			starsStatistics[3] = i;

			//HOLogger.instance().log(getClass(),"Ratings Pos : " + i + " - " + bewertungen[0] + " / " + bewertungen[1] + " / " + bewertungen[2] + " / / " + bewertungen[3]);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getPlayerRatingForPosition : " + e);
		}

		return starsStatistics;
	}
	
	/**
	 * Deletes the given player based on teamID and matchID.
	 * He is only deleted from the role set in the player object.
	 * 
	 * @author blaghaid
	 */
	void deleteMatchLineupPlayer(MatchLineupPlayer player, int matchID, int teamID) {
		if (player != null) {
			final String[] where = { "MatchID" , "TeamID", "RoleID", "SpielerID"};
			final String[] werte = { "" + matchID, "" + teamID, "" + player.getId(), "" + player.getSpielerId()};			
			delete(where, werte);			
		}
	}
	
	/**
	 * Deletes all players from the given match
	 * 
	 * @author blaghaid
	 */
	void deleteMatchLineupPlayers(int matchID) {
		final String[] where = { "MatchID"};
		final String[] werte = { "" + matchID};			
		delete(where, werte);			
	}
	

	@SuppressWarnings("deprecation")
	void storeMatchLineupPlayer(MatchLineupPlayer player, int matchID, int teamID) {
		if (player != null) {
			
			// Need to check for spieler, there may now be multiple players with -1 role.
			// Should we delete here, anyways? Isn't that for update?
			
			final String[] where = { "MatchID" , "TeamID", "RoleID", "SpielerID"};
			final String[] werte = { "" + matchID, "" + teamID, "" + player.getId(), "" + player.getSpielerId()};			
			delete(where, werte);

			//saven
			try {
				//insert vorbereiten
				var sql = "INSERT INTO "+getTableName()+" ( MatchID, TeamID, SpielerID, RoleID, Taktik, PositionCode, VName, NickName, Name, Rating, HoPosCode, STATUS, FIELDPOS, RatingStarsEndOfMatch, StartPosition, StartBehaviour ) VALUES(";
				sql
					+= (matchID
						+ ","
						+ teamID
						+ ","
						+ player.getSpielerId()
						+ ","
						+ player.getId()
						+ ", "
						+ player.getTaktik()
						+ ", "
						+ player.getPositionCode()
						+ ", '"
						+ DBManager.insertEscapeSequences(player.getSpielerVName())
						+ "', '"
						+ DBManager.insertEscapeSequences(player.getNickName())
						+ "', '"
						+ DBManager.insertEscapeSequences(player.getSpielerName())
						+ "',"
						+ player.getRating()
						+ ", "
						+ player.getPosition()
						+ ", 0" // Status
						+ ","
						+ player.getPositionCode()
						+ ","
						+ player.getRatingStarsEndOfMatch()
						+ ","
						+ player.getStartPosition()
						+ ","
						+ player.getStartBehavior()
						+ " )");
				adapter.executeUpdate(sql);
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),"DB.storeMatchLineupPlayer Error" + e);
				HOLogger.instance().log(getClass(),e);
			}
		}
	}	

	Vector<MatchLineupPlayer> getMatchLineupPlayers(int matchID, int teamID) {
		try {
			var sql = "SELECT * FROM "+getTableName()+" WHERE MatchID = " + matchID + " AND TeamID = " + teamID;
			return createMatchLineups(sql);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getMatchLineupTeam Error" + e);
		}
		return new Vector<>();
	}

	public List<MatchLineupPlayer> getMatchInserts(int objectPlayerID)
	{
		try {
			var sql = "SELECT * FROM "+getTableName()+" WHERE SpielerID = " + objectPlayerID;
			return createMatchLineups(sql);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getMatchLineupTeam Error" + e);
		}
		return new Vector<>();
	}

	private Vector<MatchLineupPlayer> createMatchLineups(String sql) throws SQLException {
		var vec = new Vector<MatchLineupPlayer>();
		var rs = adapter.executeQuery(sql);
		rs.beforeFirst();

		while (rs.next()) {
			var roleID = rs.getInt("RoleID");
			var behavior = rs.getInt("Taktik");
			var spielerID = rs.getInt("SpielerID");
			var rating = rs.getDouble("Rating");
			var ratingStarsEndOfMatch = rs.getDouble("RatingStarsEndOfMatch");
			var vname = DBManager.deleteEscapeSequences(rs.getString("VName"));
			var nickName = DBManager.deleteEscapeSequences(rs.getString("NickName"));
			var name = DBManager.deleteEscapeSequences(rs.getString("Name"));
			var positionsCode = rs.getInt("PositionCode");
			var startPos = rs.getInt("StartPosition");
			var startBeh = rs.getInt("StartBehaviour");
			var status = rs.getInt("STATUS");

			switch (behavior) {
				case IMatchRoleID.OLD_EXTRA_DEFENDER -> {
					roleID = IMatchRoleID.middleCentralDefender;
					behavior = IMatchRoleID.NORMAL;
				}
				case IMatchRoleID.OLD_EXTRA_MIDFIELD -> {
					roleID = IMatchRoleID.centralInnerMidfield;
					behavior = IMatchRoleID.NORMAL;
				}
				case IMatchRoleID.OLD_EXTRA_FORWARD -> {
					roleID = IMatchRoleID.centralForward;
					behavior = IMatchRoleID.NORMAL;
				}
			}

			roleID = MatchRoleID.convertOldRoleToNew(roleID);

			// Position code and field position was removed from constructor below.
			var player = new MatchLineupPlayer(roleID, behavior, spielerID, rating, vname, nickName, name, status, ratingStarsEndOfMatch, startPos, startBeh);
			vec.add(player);
		}

		return vec;
	}

}
