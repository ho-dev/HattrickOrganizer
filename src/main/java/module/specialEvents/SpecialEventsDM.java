package module.specialEvents;

import core.db.DBManager;
//import core.db.MatchesOverviewQuery;
import core.model.HOVerwaltung;
import core.model.match.MatchEvent;
import core.model.match.MatchKurzInfo;
import core.model.enums.MatchType;
import core.model.match.Matchdetails;
import core.model.match.Weather;
import core.model.player.Player;
import core.util.HOLogger;
import module.specialEvents.filter.Filter;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SpecialEventsDM {

	private final DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	List<MatchRow> getRows(Filter filter) {
		List<MatchRow> matchRows = new ArrayList<>();

		MatchKurzInfo[] matches = getMatches(filter);
		if (matches != null) {
			int matchCount = 1;
			for (MatchKurzInfo matchKurzInfo : matches) {
				Matchdetails details = matchKurzInfo.getMatchdetails();
				if (!filterOutByTactic(details, filter)) {
					List<MatchRow> rows = getMatchRows(matchKurzInfo, details, filter);
					if (!rows.isEmpty()) {
						for (MatchRow row : rows) {
							row.setMatchCount(matchCount);
						}
						matchRows.addAll(rows);
						matchCount++;
					}
				}
			}
		}

		return matchRows;
	}

	private boolean filterOutByTactic(Matchdetails details, Filter filter) {
		if (filter.getTactic() != null) {
			int id = filter.getTactic();
			return details.getHomeTacticType() != id && details.getGuestTacticType() != id;
		}
		return false;
	}

	private MatchKurzInfo[] getMatches(Filter filter) {
		List<MatchRow> matchRows = new ArrayList<>();
		StringBuilder whereClause = new StringBuilder(" WHERE ");

		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		whereClause.append(" (GastID=").append(teamId).append(" OR HeimID=").append(teamId).append(") ");
		whereClause.append(" AND (Status=").append(MatchKurzInfo.FINISHED).append(")");

		if (filter.getSeasonFilterValue() != SeasonFilterValue.ALL_SEASONS) {
			Timestamp datumAb = getMatchDateFrom(filter.getSeasonFilterValue());
			whereClause.append(" AND (MATCHDATE > '").append(this.dateformat.format(datumAb));
			whereClause.append("')");
		}

		List<Integer> matchTypes = new ArrayList<>();
		if (filter.isShowFriendlies()) {
			matchTypes.add(MatchType.FRIENDLYNORMAL.getId());
			matchTypes.add(MatchType.FRIENDLYCUPRULES.getId());
			matchTypes.add(MatchType.INTFRIENDLYCUPRULES.getId());
			matchTypes.add(MatchType.INTFRIENDLYNORMAL.getId());
		}
		if (filter.isShowCup()) {
			matchTypes.add(MatchType.CUP.getId());
		}
		if (filter.isShowLeague()) {
			matchTypes.add(MatchType.LEAGUE.getId());
		}
		if (filter.isShowMasters()) {
			matchTypes.add(MatchType.MASTERS.getId());
		}
		if (filter.isShowTournament()) {
			matchTypes.add(MatchType.TOURNAMENTGROUP.getId());
			matchTypes.add(MatchType.TOURNAMENTPLAYOFF.getId());
		}
		if (filter.isShowRelegation()) {
			matchTypes.add(MatchType.QUALIFICATION.getId());
		}

		if (matchTypes.size() > 0) {
			whereClause.append(" AND (MatchTyp IN (");
			for (Integer id : matchTypes) {
				whereClause.append(id).append(',');
			}
			// remove last ','
			whereClause.deleteCharAt(whereClause.length() - 1);
			whereClause.append("))");
		} else {
			// NO matches at all
			return null;
		}
		whereClause.append(" ORDER BY MatchDate DESC");

		return DBManager.instance().getMatchesKurzInfo(whereClause.toString());
	}

	private List<MatchRow> getMatchRows(MatchKurzInfo kurzInfos, Matchdetails details, Filter filter) {
		List<MatchRow> matchLines = new ArrayList<>();
		List<MatchEvent> highlights = getMatchHighlights(details, filter);

		if (!highlights.isEmpty() || !filter.isShowMatchesWithSEOnly()) {

			Match match = new Match();
			match.setHostingTeam(kurzInfos.getHomeTeamName());
			match.setHostingTeamId(kurzInfos.getHomeTeamID());
			match.setHostingTeamTactic(details.getHomeTacticType());
			match.setMatchDate(kurzInfos.getMatchSchedule());
			match.setMatchId(kurzInfos.getMatchID());
			match.setMatchResult(kurzInfos.getHomeTeamGoals() + " - " + kurzInfos.getGuestTeamGoals());
			match.setVisitingTeam(kurzInfos.getGuestTeamName());
			match.setVisitingTeamId(kurzInfos.getGuestTeamID());
			match.setVisitingTeamTactic(details.getGuestTacticType());
			match.setWeather(Weather.getById(details.getWetterId()));
			match.setMatchType(kurzInfos.getMatchType());

			boolean isFirst = true;
			MatchRow matchRow = new MatchRow();
			matchRow.setMatch(match);
			matchRow.setMatchHeaderLine(true);
			matchLines.add(matchRow);

			for (MatchEvent highlight : highlights) {
				if (!isFirst) {
					matchRow = new MatchRow();
					matchRow.setMatch(match);
					matchRow.setMatchHeaderLine(false);
					matchLines.add(matchRow);
				}
				matchRow.setMatchHighlight(highlight);
				isFirst = false;
			}
		}
		return matchLines;
	}

	private List<MatchEvent> getMatchHighlights(Matchdetails details, Filter filter) {
		List<MatchEvent> filteredHighlights = new ArrayList<>();
		List<MatchEvent> allHighlights = details.downloadHighlightsIfMissing();
		if ( allHighlights != null) {
			for (MatchEvent highlight : allHighlights) {
				if (checkForSE(highlight, filter)) {
					filteredHighlights.add(highlight);
				}
			}
		}
		else {
			HOLogger.instance().debug(this.getClass(), "keine Match-Highlights");
		}
		return filteredHighlights;
	}

	@Nullable
	public static EventType getEventType(MatchEvent highlight) {
		if (highlight.isSpecialtyWeatherSE()) {return EventType.SPECIALTY_WEATHER_SE;}
		else if(highlight.isSpecialtyNonWeatherSE()) {return EventType.SPECIALTY_NON_WEATHER_SE;}
		else if(highlight.isOtherSE()) {return EventType.NON_SPECIALTY_SE;}
		else if(highlight.isCounterAttack()) {return EventType.COUNTER_ATTACK;}
		else if(highlight.isFreeKick()) {return EventType.FREEKICK;}
		else if(highlight.isManMarking()) {return EventType.MANMARKING;}
		else if(highlight.isPenalty()) {return EventType.PENALTY;}
		else if(highlight.isLongShot()) {return EventType.LONGSHOT;}
		else {return null;}
	}

	private boolean checkForSE(MatchEvent highlight, Filter filter) {
		EventType eventType = getEventType(highlight);
		if (eventType == null) {
			return false;
		} else if (!filter.isShowSpecialitySE() && eventType == EventType.SPECIALTY_NON_WEATHER_SE) {
			return false;
		} else if (!filter.isShowWeatherSE() && eventType == EventType.SPECIALTY_WEATHER_SE) {
			return false;
		} else if (!filter.isShowCounterAttack() && eventType == EventType.COUNTER_ATTACK) {
			return false;
		} else if (!filter.isShowFreeKick() && eventType == EventType.FREEKICK) {
			return false;
		} else if (!filter.isShowPenalty() && eventType == EventType.PENALTY) {
			return false;
		} else if (!filter.isShowManMarking() && eventType == EventType.MANMARKING) {
			return false;
		} else if (!filter.isShowLongShot() && eventType == EventType.LONGSHOT) {
			return false;
		}

		if (filter.getPlayerId() != null) {
			if (!isInvolved(filter.getPlayerId(), highlight)) {
				return false;
			}
		}

		if (filter.isShowOwnPlayersOnly()) {
			List<Player> players = new ArrayList<>(HOVerwaltung.instance().getModel().getCurrentPlayers());
			if (!filter.isShowCurrentOwnPlayersOnly()) {
				players.addAll(HOVerwaltung.instance().getModel().getFormerPlayers());
			}

			boolean playerFound = false;
			for (Player player : players) {
				if (isInvolved(player.getPlayerID(), highlight)) {
					// player found in list of current players
					playerFound = true;
					break;
				}
			}

			return playerFound;
		}

		return true;
	}


	public static String getSEText(MatchEvent highlight) {
		return String.format("(%s) %s", highlight.getiMatchEventID(), highlight.getEventTextDescription());
	}

	private boolean isInvolved(int playerId, MatchEvent highlight) {
		return (playerId == highlight.getAssistingPlayerId() || playerId == highlight.getPlayerId());
	}

	private static String findName(MatchEvent highlight) {
		if (highlight.isSpecialtyWeatherSE()) {return highlight.getPlayerName();}
		else if (highlight.isGoalEvent() || highlight.isNonGoalEvent()) {
			return switch (highlight.getMatchEventID()) {
				case SE_GOAL_UNPREDICTABLE_LONG_PASS, SE_GOAL_UNPREDICTABLE_SPECIAL_ACTION, SE_QUICK_RUSHES_PASSES_AND_RECEIVER_SCORES, SE_GOAL_CORNER_TO_ANYONE,
						SE_GOAL_CORNER_HEAD_SPECIALIST, SE_WINGER_TO_HEAD_SPEC_SCORES, SE_WINGER_TO_ANYONE_SCORES -> highlight.getAssistingPlayerName() + " - " + highlight.getPlayerName();
				default -> highlight.getPlayerName();
			};}
			else if (highlight.isManMarking()) {
				return highlight.getPlayerName() + " -> " + highlight.getAssistingPlayerName();
			}
		return "?";
	}

	public static String getSpielerName(MatchEvent highlight) {
		String name;
		// if(highlight.getTeamID() == teamId && !isNegativeSE(highlight))
		if (highlight.getTeamID() == HOVerwaltung.instance().getModel().getBasics().getTeamId()) {
			name = findName(highlight) + "|*";} // -> black
		else {
			name = findName(highlight) + "|-"; // -> red
		}
		return name;
	}

	private static Timestamp getMatchDateFrom(SeasonFilterValue period) {
		Date date;
		if (period.getId() == 1) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		int week = HOVerwaltung.instance().getModel().getBasics().getSpieltag();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int dayCorrection = 0;
		if (dayOfWeek != 7) {
			dayCorrection = dayOfWeek;
		}
		cal.add(Calendar.DAY_OF_WEEK, -dayCorrection);
		cal.add(Calendar.WEEK_OF_YEAR, -(week - 1));
		if (period.getId() == 2) {
			cal.add(Calendar.WEEK_OF_YEAR, -16);
		}
		date = cal.getTime();
		return new Timestamp(date.getTime());
	}
}
