package core.db;

import core.model.match.MatchLineupPosition;
import core.model.enums.MatchType;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.Vector;
import static core.model.player.IMatchRoleID.aPositionBehaviours;

public final class MatchLineupPlayerTable extends AbstractTable {

	/**
	 * tablename
	 **/
	public final static String TABLENAME = "MATCHLINEUPPLAYER";

	MatchLineupPlayerTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
		idColumns = 3;
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchID").setGetter((o) -> ((MatchLineupPosition) o).getMatchId()).setSetter((o, v) -> ((MatchLineupPosition) o).setMatchId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchTyp").setGetter((o) -> ((MatchLineupPosition) o).getMatchType().getId()).setSetter((o, v) -> ((MatchLineupPosition) o).setMatchType(MatchType.getById((int) v))).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TeamID").setGetter((o) -> ((MatchLineupPosition) o).getTeamId()).setSetter((o, v) -> ((MatchLineupPosition) o).setTeamId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SpielerID").setGetter((o) -> ((MatchLineupPosition) o).getPlayerId()).setSetter((o, v) -> ((MatchLineupPosition) o).setPlayerId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("RoleID").setGetter((o) -> ((MatchLineupPosition) o).getRoleId()).setSetter((o, v) -> ((MatchLineupPosition) o).setRoleId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Taktik").setGetter((o) -> ((MatchLineupPosition) o).getBehaviour()).setSetter((o, v) -> ((MatchLineupPosition) o).setTaktik((byte)(int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("VName").setGetter((o) -> ((MatchLineupPosition) o).getSpielerVName()).setSetter((o, v) -> ((MatchLineupPosition) o).setSpielerVName((String) v)).setType(Types.VARCHAR).setLength(255).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("NickName").setGetter((o) -> ((MatchLineupPosition) o).getNickName()).setSetter((o, v) -> ((MatchLineupPosition) o).setNickName((String) v)).setType(Types.VARCHAR).setLength(255).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Name").setGetter((o) -> ((MatchLineupPosition) o).getSpielerName()).setSetter((o, v) -> ((MatchLineupPosition) o).setSpielerName((String) v)).setType(Types.VARCHAR).setLength(255).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Rating").setGetter((o) -> ((MatchLineupPosition) o).getRating()).setSetter((o, v) -> ((MatchLineupPosition) o).setRating((float) v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HoPosCode").setGetter((o) -> ((MatchLineupPosition) o).getHoPosCode()).setSetter((o, v) -> ((MatchLineupPosition) o).setHoPosCode((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("STATUS").setGetter((o) -> ((MatchLineupPosition) o).getStatus()).setSetter((o, v) -> ((MatchLineupPosition) o).setStatus((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("RatingStarsEndOfMatch").setGetter((o) -> ((MatchLineupPosition) o).getRatingStarsEndOfMatch()).setSetter((o, v) -> ((MatchLineupPosition) o).setRatingStarsEndOfMatch((float) v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("StartPosition").setGetter((o) -> ((MatchLineupPosition) o).getStartPosition()).setSetter((o, v) -> ((MatchLineupPosition) o).setStartPosition((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("StartBehaviour").setGetter((o) -> ((MatchLineupPosition) o).getStartBehavior()).setSetter((o, v) -> ((MatchLineupPosition) o).setStartBehavior((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("StartSetPieces").setGetter((o) -> ((MatchLineupPosition) o).isStartSetPiecesTaker()).setSetter((o, v) -> ((MatchLineupPosition) o).setStartSetPiecesTaker((Boolean) v)).setType(Types.BOOLEAN).isNullable(true).build()
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[]{
				"CREATE INDEX iMATCHLINEUPPLAYER_1 ON " + getTableName() + "(SpielerID)",
				"CREATE INDEX iMATCHLINEUPPLAYER_2 ON " + getTableName() + "(MatchID,TeamID)",
				"SET TABLE " + getTableName() + " NEW SPACE"
		};
	}

	/**
	 * Returns a list of ratings the player has played on: 0: Max,  1: Min,  2: Average,  3: posid
	 */
	Vector<float[]> getAllRatings(int playerID) {
		final Vector<float[]> ratings = new Vector<>();

		//Iterate over possible combinations of position / behaviours
		for (int i : aPositionBehaviours) {
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

	private final DBManager.PreparedStatementBuilder getBewertungen4PlayerStatementBuilder = new DBManager.PreparedStatementBuilder(
			"SELECT MatchID, Rating FROM " + getTableName() + " WHERE SpielerID=?");
	/**
	 * Gibt die beste, schlechteste und durchschnittliche Bewertung für den Player, sowie die
	 * Anzahl der Bewertungen zurück // Match
	 */
	float[] getBewertungen4Player(int spielerid) {
		//Max, Min, Durchschnitt
		final float[] bewertungen = {0f, 0f, 0f, 0f};

		try {
			final ResultSet rs = adapter.executePreparedQuery(getBewertungen4PlayerStatementBuilder.getStatement(), spielerid);
			assert rs != null;
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
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getBewertungen4Player : " + e);
		}
		return bewertungen;
	}

	private final DBManager.PreparedStatementBuilder getPlayerRatingForPositionStatementBuilder = new DBManager.PreparedStatementBuilder(
			"SELECT MatchID, Rating FROM " + getTableName() + " WHERE SpielerID=? AND HoPosCode=?" );

	/**
	 * Returns the best, worst, and average rating for the player, as well as the number of ratings // match
	 *
	 * @param spielerid Spielerid
	 * @param position  Usere positionscodierung mit taktik
	 */
	float[] getPlayerRatingForPosition(int spielerid, int position) {
		//Max, Min, average
		final float[] starsStatistics = {0f, 0f, 0f, 0f};

		try {
			final ResultSet rs = adapter.executePreparedQuery(getPlayerRatingForPositionStatementBuilder.getStatement(), spielerid, position);
			assert rs != null;
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
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getPlayerRatingForPosition : " + e);
		}
		return starsStatistics;
	}

	void storeMatchLineupPlayers(List<MatchLineupPosition> matchLineupPositions, MatchType matchType, int matchID, int teamID) {
		if (matchLineupPositions != null) {
			executePreparedDelete(matchID, matchType.getId(), teamID);
			for ( var p : matchLineupPositions){
				p.setMatchId(matchID);
				p.setMatchType(matchType);
				p.setTeamId(teamID);
				p.setIsStored(false);	// replace (if record was available in database, it has to be deleted before storing)
				store(p);
			}
		}
	}
	List<MatchLineupPosition> getMatchLineupPlayers(int matchID, MatchType matchType, int teamID)  {
		return load(MatchLineupPosition.class, matchID, matchType.getId(), teamID);
	}

	private final PreparedSelectStatementBuilder getMatchInsertsStatementBuilder = new PreparedSelectStatementBuilder(this, " WHERE SpielerID = ?");
	public List<MatchLineupPosition> getMatchInserts(int objectPlayerID) {
		return load(MatchLineupPosition.class, adapter.executePreparedQuery(getMatchInsertsStatementBuilder.getStatement(), objectPlayerID));
	}
}
