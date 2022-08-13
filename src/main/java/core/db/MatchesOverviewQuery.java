package core.db;

import core.model.HOVerwaltung;
import core.model.cup.CupLevel;
import core.model.enums.MatchType;
import core.model.match.*;
import core.util.HOLogger;
import module.matches.MatchLocation;
import module.matches.MatchesPanel;
import module.matches.statistics.MatchesOverviewCommonPanel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static core.model.match.MatchEvent.isGoalEvent;


class MatchesOverviewQuery  {

	/**
	 *
	 * @param teamId
	 * @param matchtype
	 * @param statistic
	 * @return count of matches
	 */
	static int getMatchesKurzInfoStatisticsCount(int teamId, int matchtype, int statistic){
		int tmp = 0;
		StringBuilder sql = new StringBuilder(200);
		ResultSet rs;
		String whereHomeClause = "";
		String whereAwayClause = "";
		sql.append("SELECT COUNT(*) AS C ");
		sql.append(" FROM MATCHESKURZINFO ");
		sql.append(" WHERE ");
		switch(statistic){
			case MatchesOverviewCommonPanel.LeadingHTLosingFT:
			case MatchesOverviewCommonPanel.TrailingHTWinningFT:
				return getChangeGameStat(teamId, statistic);

			case MatchesOverviewCommonPanel.WonWithoutOppGoal:
				whereHomeClause=" AND HEIMTORE > GASTTORE AND GASTTORE = 0 )";
				whereAwayClause=" AND HEIMTORE < GASTTORE AND HEIMTORE = 0 ))";
				break;
			case MatchesOverviewCommonPanel.LostWithoutOwnGoal:
				whereHomeClause=" AND HEIMTORE < GASTTORE AND HEIMTORE = 0 )";
				whereAwayClause=" AND HEIMTORE > GASTTORE AND GASTTORE = 0 ))";
				break;
			case MatchesOverviewCommonPanel.FiveGoalsDiffWin:
				whereHomeClause=" AND HEIMTORE > GASTTORE AND (HEIMTORE - GASTTORE ) >= 5 )";
				whereAwayClause=" AND HEIMTORE < GASTTORE AND (GASTTORE - HEIMTORE ) >= 5 ))";
				break;
			case MatchesOverviewCommonPanel.FiveGoalsDiffDefeat:
				whereHomeClause=" AND HEIMTORE < GASTTORE AND (GASTTORE - HEIMTORE ) >= 5 )";
				whereAwayClause=" AND HEIMTORE > GASTTORE AND (HEIMTORE - GASTTORE ) >= 5 ))";
				break;
		}
		sql.append(" ((HEIMID = ?").append(whereHomeClause);
		sql.append(" OR (GASTID = ?").append(whereAwayClause);
		sql.append(MatchesKurzInfoTable.getMatchTypWhereClause(matchtype));

		rs = DBManager.instance().getAdapter().executePreparedQuery(getPreparedStatement(sql.toString()), teamId, teamId);
		try {
			if(rs.next()){
				tmp = rs.getInt("C");
			}
		} catch (SQLException e) {
			HOLogger.instance().log(MatchesOverviewQuery.class, e);
		}
		return tmp;
	}

	private static HashMap<String,PreparedStatement> preparedStatements = new HashMap<>();
	private static PreparedStatement getPreparedStatement(String sql) {
		PreparedStatement ret = preparedStatements.get(sql);
		if ( ret == null){
			ret = DBManager.instance().getAdapter().createPreparedStatement(sql);
			preparedStatements.put(sql, ret);
		}
		return ret;
	}

	static int getChangeGameStat(int teamId, int statistic){
		StringBuilder sql = new StringBuilder(200);
		ResultSet rs;
		int tmp = 0;
		sql.append("SELECT MK_MatchTyp, DIFFH, DIFF, MK_HEIMID, MK_GASTID, MATCHID \n");
		sql.append("FROM (SELECT (MATCHHIGHLIGHTS.HEIMTORE - MATCHHIGHLIGHTS.GASTTORE) as DIFFH, (MATCHESKURZINFO.HEIMTORE - MATCHESKURZINFO.GASTTORE) as DIFF, HEIMID, GASTID, MATCHID, TYP, MINUTE, \n" + 
				    "MATCHHIGHLIGHTS.TEAMID as MH_TEAMID, MATCHESKURZINFO.HEIMID as MK_HEIMID, MATCHESKURZINFO.GASTID as MK_GASTID, MATCHESKURZINFO.MATCHTYP as MK_MatchTyp \n" + 
				    "FROM\n" + 
				    "MATCHHIGHLIGHTS JOIN MATCHESKURZINFO ON MATCHHIGHLIGHTS.MATCHID = MATCHESKURZINFO.MATCHID)\n");
		sql.append(" WHERE TYP = 0 AND MINUTE = 45 AND MH_TEAMID = 0 ");
		switch(statistic){
		case MatchesOverviewCommonPanel.LeadingHTLosingFT:
			sql.append("AND ((MK_HEIMID = ? AND DIFFH >0 AND DIFF <0) or (MK_GASTID = ? AND DIFFH <0 AND DIFF >0)) ");
			break;
		case MatchesOverviewCommonPanel.TrailingHTWinningFT:
			sql.append("AND ((MK_HEIMID = ? AND DIFFH <0 AND DIFF >0) or (MK_GASTID = ? AND DIFFH >0 AND DIFF <0)) ");
			break;
		}
		sql.append("AND (MK_MatchTyp=2 OR MK_MatchTyp=1 OR MK_MatchTyp=3 )");

		rs = DBManager.instance().getAdapter().executePreparedQuery(getPreparedStatement(sql.toString()), teamId, teamId);
		try {
			for (int i = 0; rs.next(); i++) {
				tmp=i;
			}
		} catch (SQLException e) {
			HOLogger.instance().log(MatchesOverviewQuery.class,e);
		}
		return tmp;

	}

	public static MatchesHighlightsStat[] getGoalsByActionType(boolean ownTeam, int iMatchType, MatchLocation matchLocation){

		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();

		MatchesHighlightsStat[] rows = new MatchesHighlightsStat[9];
		rows[0] = new MatchesHighlightsStat("highlight_penalty", MatchEvent.penaltyME);
		rows[1] = new MatchesHighlightsStat("highlight_freekick",  MatchEvent.freekickME);
		rows[2] = new MatchesHighlightsStat("highlight_links", MatchEvent.leftAttackME);
		rows[3] = new MatchesHighlightsStat("highlight_middle", MatchEvent.CentralAttackME);
		rows[4] = new MatchesHighlightsStat("highlight_rechts", MatchEvent.RightAttackME);
		rows[5] = new MatchesHighlightsStat("IFK", MatchEvent.IFKME);
		rows[6] = new MatchesHighlightsStat("ls.match.event.longshot", MatchEvent.LSME);
		rows[7] = new MatchesHighlightsStat("highlight_counter", MatchEvent.CounterAttackME);
		rows[8] = new MatchesHighlightsStat("highlight_special", MatchEvent.specialME);

		for (int i = 0; i < rows.length; i++) {
			if(!rows[i].isTitle())
				fillMatchesOverviewChanceRow(ownTeam, teamId, rows[i], iMatchType, matchLocation);
		}
		return rows;
	}


	private static void fillMatchesOverviewChanceRow(boolean ownTeam, int teamId, MatchesHighlightsStat row, int iMatchType, MatchLocation matchLocation){
		StringBuilder sql = new StringBuilder(200);
		ResultSet rs;
		List<Object> params = new ArrayList<>();
		sql.append("SELECT MATCH_EVENT_ID, COUNT(*) AS C FROM MATCHHIGHLIGHTS JOIN MATCHESKURZINFO ON MATCHHIGHLIGHTS.MATCHID = MATCHESKURZINFO.MATCHID WHERE TEAMID");
		if(!ownTeam) {sql.append("!");}
		sql.append("=?").append(" AND MATCH_EVENT_ID IN(");
		params.add(teamId);
		sql.append(createSubTypePlaceholders(row.getSubtyps(), params)).append(")");
		sql.append(MatchesKurzInfoTable.getMatchTypWhereClause(iMatchType));
		sql.append(getMatchLocationWhereClause(matchLocation, teamId));
		sql.append(" GROUP BY MATCH_EVENT_ID");
		rs = DBManager.instance().getAdapter().executePreparedQuery(getPreparedStatement(sql.toString()), params);
		if(rs == null){
			HOLogger.instance().log(MatchesOverviewQuery.class, sql.toString());
		}
		else {
			try {
				int iConverted = 0;
				int iMissed = 0;
				while (rs.next()) {
					int iMatchEventID = rs.getInt("MATCH_EVENT_ID");
					if (isGoalEvent(iMatchEventID)) {
						iConverted += rs.getInt("C");
					} else {
						iMissed += rs.getInt("C");
					}
				}
				rs.close();
				row.setGoals(iConverted);
				row.setNoGoals(iMissed);
			}
			catch (SQLException e) {
				HOLogger.instance().log(MatchesOverviewQuery.class, e);
		}
		}
	}

	private static StringBuilder createSubTypePlaceholders(List<MatchEvent.MatchEventID> subtypes, List<Object> params) {
		var sep = "";
		var placeholders = new StringBuilder();
		for (var id : subtypes) {
			placeholders.append(sep).append("?");
			params.add(id.getValue());
			sep = ",";
		}
		return placeholders;
	}

	private static StringBuilder getMatchLocationWhereClause(MatchLocation matchLocation, int teamId) {
		StringBuilder sql = new StringBuilder(50);
		switch (matchLocation) {
			case HOME -> sql.append(" AND HeimID=").append(teamId).append(" AND (isNeutral is NULL OR isNeutral=false) ");
			case AWAY -> sql.append(" AND GastID=").append(teamId).append(" AND (isNeutral is NULL OR isNeutral=false) ");
			case NEUTRAL -> sql.append(" AND (HeimID=").append(teamId).append(" OR GastID=").append(teamId).append(") AND (isNeutral=true) ");
			case ALL -> sql.append(" AND (HeimID=").append(teamId).append(" OR GastID=").append(teamId).append(") ");
		}
		return sql;
	}


	static MatchesOverviewRow[] getMatchesOverviewValues(int matchtype, MatchLocation matchLocation){
		ArrayList<MatchesOverviewRow> rows = new ArrayList<>(20);
		rows.add(new MatchesOverviewRow(HOVerwaltung.instance().getLanguageString("AlleSpiele"), MatchesOverviewRow.TYPE_ALL));
		rows.add(new MatchesOverviewRow(HOVerwaltung.instance().getLanguageString("ls.team.formation"), MatchesOverviewRow.TYPE_TITLE));
		rows.add(new MatchesOverviewRow("5-5-0", MatchesOverviewRow.TYPE_SYSTEM));
		rows.add(new MatchesOverviewRow("5-4-1", MatchesOverviewRow.TYPE_SYSTEM));
		rows.add(new MatchesOverviewRow("5-3-2", MatchesOverviewRow.TYPE_SYSTEM));
		rows.add(new MatchesOverviewRow("5-2-3", MatchesOverviewRow.TYPE_SYSTEM));
		rows.add(new MatchesOverviewRow("4-5-1", MatchesOverviewRow.TYPE_SYSTEM));
		rows.add(new MatchesOverviewRow("4-4-2", MatchesOverviewRow.TYPE_SYSTEM));
		rows.add(new MatchesOverviewRow("4-3-3", MatchesOverviewRow.TYPE_SYSTEM));
		rows.add(new MatchesOverviewRow("3-5-2", MatchesOverviewRow.TYPE_SYSTEM));
		rows.add(new MatchesOverviewRow("3-4-3", MatchesOverviewRow.TYPE_SYSTEM));
		rows.add(new MatchesOverviewRow("2-5-3", MatchesOverviewRow.TYPE_SYSTEM));
		rows.add(new MatchesOverviewRow(HOVerwaltung.instance().getLanguageString("ls.team.tactic"), MatchesOverviewRow.TYPE_TITLE));
		rows.add(new MatchesOverviewRow(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_NORMAL), MatchesOverviewRow.TYPE_TACTICS, IMatchDetails.TAKTIK_NORMAL));
		rows.add(new MatchesOverviewRow(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_PRESSING), MatchesOverviewRow.TYPE_TACTICS, IMatchDetails.TAKTIK_PRESSING));
		rows.add(new MatchesOverviewRow(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_KONTER), MatchesOverviewRow.TYPE_TACTICS, IMatchDetails.TAKTIK_KONTER));
		rows.add(new MatchesOverviewRow(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_MIDDLE), MatchesOverviewRow.TYPE_TACTICS, IMatchDetails.TAKTIK_MIDDLE));
		rows.add(new MatchesOverviewRow(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_WINGS), MatchesOverviewRow.TYPE_TACTICS, IMatchDetails.TAKTIK_WINGS));
		rows.add(new MatchesOverviewRow(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_CREATIVE), MatchesOverviewRow.TYPE_TACTICS, IMatchDetails.TAKTIK_CREATIVE));
		rows.add(new MatchesOverviewRow(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_LONGSHOTS), MatchesOverviewRow.TYPE_TACTICS, IMatchDetails.TAKTIK_LONGSHOTS));
		rows.add(new MatchesOverviewRow(HOVerwaltung.instance().getLanguageString("ls.team.teamattitude"),MatchesOverviewRow.TYPE_TITLE));
		rows.add(new MatchesOverviewRow(Matchdetails.getNameForEinstellung(IMatchDetails.EINSTELLUNG_PIC), MatchesOverviewRow.TYPE_MOT, IMatchDetails.EINSTELLUNG_PIC));
		rows.add(new MatchesOverviewRow(Matchdetails.getNameForEinstellung(IMatchDetails.EINSTELLUNG_NORMAL), MatchesOverviewRow.TYPE_MOT, IMatchDetails.EINSTELLUNG_NORMAL));
		rows.add(new MatchesOverviewRow(Matchdetails.getNameForEinstellung(IMatchDetails.EINSTELLUNG_MOTS), MatchesOverviewRow.TYPE_MOT, IMatchDetails.EINSTELLUNG_MOTS));
		rows.add(new MatchesOverviewRow(HOVerwaltung.instance().getLanguageString("ls.match.weather"), MatchesOverviewRow.TYPE_TITLE));
		rows.add(new MatchesOverviewRow("IMatchDetails.WETTER_SONNE", MatchesOverviewRow.TYPE_WEATHER, Weather.SUNNY.getId()));
		rows.add(new MatchesOverviewRow("IMatchDetails.WETTER_WOLKIG",  MatchesOverviewRow.TYPE_WEATHER, Weather.PARTIALLY_CLOUDY.getId()));
		rows.add(new MatchesOverviewRow("IMatchDetails.WETTER_BEWOELKT", MatchesOverviewRow.TYPE_WEATHER, Weather.OVERCAST.getId()));
		rows.add(new MatchesOverviewRow("IMatchDetails.WETTER_REGEN",  MatchesOverviewRow.TYPE_WEATHER, Weather.RAINY.getId()));
		setMatchesOverviewValues(rows,matchtype,true, matchLocation);
		setMatchesOverviewValues(rows,matchtype,false, matchLocation);
		return rows.toArray(new MatchesOverviewRow[rows.size()]);
	}



	private static void setMatchesOverviewValues(ArrayList<MatchesOverviewRow> rows,int matchtype, boolean home, MatchLocation matchLocation){
		if ((home && matchLocation == MatchLocation.AWAY) || (!home && matchLocation == MatchLocation.HOME)) {
			return;
		}

		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		StringBuilder whereClause = new StringBuilder(100);
		whereClause.append(getMatchLocationWhereClause(matchLocation, teamId, home));
		whereClause.append(MatchesKurzInfoTable.getMatchTypWhereClause(matchtype));
		setMatchesOverviewRow(rows.get(0), whereClause.toString(),home);
		setFormationRows(rows,whereClause, home);
		setRows(rows, whereClause, home);
	}

	private static StringBuilder getMatchLocationWhereClause(MatchLocation matchLocation, int teamId, boolean home) {
		StringBuilder sql = new StringBuilder(500);
		switch (matchLocation) {
			case HOME: sql.append(" AND (isNeutral is NULL OR isNeutral=false) AND HeimID=" + teamId); break;
			case AWAY: sql.append(" AND (isNeutral is NULL OR isNeutral=false) AND GastID=" + teamId); break;
			case NEUTRAL: sql.append(" AND isNeutral=true");
			case ALL: sql.append(" AND ").append(home?"HEIMID=":"GASTID=").append(teamId); break;
		}
		return sql;
	}

	private static void setFormationRows(ArrayList<MatchesOverviewRow> rows,StringBuilder whereClause, boolean home){

		StringBuilder sql = new StringBuilder(500);
		sql.append("select MATCHID,HEIMTORE,GASTTORE, ");
		sql.append("LOCATE('5-5-0',MATCHREPORT) AS F550,");
		sql.append("LOCATE('5-4-1',MATCHREPORT) AS F541,");
		sql.append("LOCATE('5-3-2',MATCHREPORT) AS F532,");
		sql.append("LOCATE('5-2-3',MATCHREPORT) AS F523,");
		sql.append("LOCATE('4-5-1',MATCHREPORT) AS F451,");
		sql.append("LOCATE('4-4-2',MATCHREPORT) AS F442,");
		sql.append("LOCATE('4-3-3',MATCHREPORT) AS F433,");
		sql.append("LOCATE('3-5-2',MATCHREPORT) AS F352,");
		sql.append("LOCATE('3-4-3',MATCHREPORT) AS F343,");
		sql.append("LOCATE('2-5-3',MATCHREPORT) AS F253");
		sql.append(" FROM MATCHDETAILS inner join MATCHESKURZINFO ON MATCHDETAILS.MATCHID = MATCHESKURZINFO.MATCHID ");
		sql.append(" where 1=1 ");
		sql.append(whereClause);
		try{
			ResultSet rs = DBManager.instance().getAdapter().executePreparedQuery(getPreparedStatement(sql.toString()));

			while(rs.next()){
				String[] fArray = {"0","",""};
				setSystem(rs.getInt("F550"), "5-5-0", fArray);
				setSystem(rs.getInt("F541"), "5-4-1", fArray);
				setSystem(rs.getInt("F532"), "5-3-2", fArray);
				setSystem(rs.getInt("F523"), "5-2-3", fArray);
				setSystem(rs.getInt("F451"), "4-5-1", fArray);
				setSystem(rs.getInt("F442"), "4-4-2", fArray);
				setSystem(rs.getInt("F433"), "4-3-3", fArray);
				setSystem(rs.getInt("F352"), "3-5-2", fArray);
				setSystem(rs.getInt("F343"), "3-4-3", fArray);
				setSystem(rs.getInt("F253"), "2-5-3", fArray);
				for (int i = 1; i <rows.size(); i++) {
					String txt = home?fArray[1]:fArray[2].length()==0?fArray[1]:fArray[2];

					if(rows.get(i).getType() == 1 && rows.get(i).getDescription().equals(txt)){
						rows.get(i).setMatchResult(rs.getInt("HEIMTORE"), rs.getInt("GASTTORE"), home);
					}
				}
			}
			} catch(Exception e){

				HOLogger.instance().log(MatchesOverviewQuery.class,e);
			}
	}

	private static void setSystem(int column,String formation, String[] fArray){
		int max = Integer.parseInt(fArray[0]);
		if(column > 0){
			if(max == 0){
				fArray[0] = String.valueOf(column);
				fArray[1] = formation;
			} else if(max > column){
				fArray[2] = fArray[1];
				fArray[1] = formation;
			} else {
				fArray[0] = String.valueOf(column);
				fArray[2] = formation;
			}
		}
	}

	private static void setRows(ArrayList<MatchesOverviewRow> rows,StringBuilder whereClause,boolean home){
		for (int i = 1; i < rows.size(); i++) {
			if(rows.get(i).getTypeValue() > Integer.MIN_VALUE){
				String whereSpecial = " AND "+rows.get(i).getColumnName(home)+" = "+rows.get(i).getTypeValue() ;
				setMatchesOverviewRow(rows.get(i), whereClause+whereSpecial,home);
			}
		}
	}

	private static void setMatchesOverviewRow(MatchesOverviewRow row,String whereClause, boolean home){
		StringBuilder sql = new StringBuilder(500);
		String from = " FROM MATCHDETAILS inner join MATCHESKURZINFO ON MATCHDETAILS.MATCHID = MATCHESKURZINFO.MATCHID ";
		sql.append("SELECT SUM(ANZAHL) AS A1,SUM(G1) AS G,SUM(U1) AS U,SUM(V1) AS V, SUM(HTORE1) AS HEIMTORE, SUM(GTORE1) AS GASTTORE FROM (");
		sql.append("select  COUNT(*) AS ANZAHL, 0 AS G1,0 AS U1, 0 AS V1, SUM(HEIMTORE) AS HTORE1, SUM(GASTTORE) AS GTORE1 "+from+" where 1 = 1 ");
		sql.append(whereClause).append(" UNION ");
		sql.append("SELECT 0 AS ANZAHL,  COUNT(*) AS G1,0 AS U1, 0 AS V1, 0 AS HTORE1, 0 AS GTORE1 "+from+" where HEIMTORE "+(home?">":"<")+" GASTTORE ");
		sql.append(whereClause).append(" UNION ");
		sql.append("SELECT  0 AS ANZAHL,  0 AS G1,COUNT(*) AS U1, 0 AS V1, 0 AS HTORE1, 0 AS GTORE1 "+from+" where HEIMTORE = GASTTORE ");
		sql.append(whereClause).append(" UNION ");
		sql.append("select  0 AS ANZAHL,  0 AS G1, 0 AS U1, COUNT(*) AS V1, 0 AS HTORE1, 0 AS GTORE1 "+from+" where HEIMTORE "+(home?"<":">")+" GASTTORE ");
		sql.append(whereClause);
		sql.append(")");
		try{
		ResultSet rs = DBManager.instance().getAdapter().executePreparedQuery(getPreparedStatement(sql.toString()));
		if(rs == null){
			HOLogger.instance().log(MatchesOverviewQuery.class, sql.toString());
		}

		if(rs.next()){
			row.setCount(rs.getInt("A1"));
			row.setWin(rs.getInt("G"));
			row.setDraw(rs.getInt("U"));
			row.setLoss(rs.getInt("V"));
			row.setHomeGoals(rs.getInt(home ? "HEIMTORE" : "GASTTORE"));
			row.setAwayGoals(rs.getInt(home ? "GASTTORE" : "HEIMTORE"));
		}
		} catch(Exception e){
			HOLogger.instance().log(MatchesOverviewQuery.class,e);
		}
	}

}
