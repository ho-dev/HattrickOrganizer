package module.specialEvents;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.MatchEvent;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchType;
import core.model.match.Matchdetails;
import core.model.match.Weather;
import core.model.player.Player;
import module.specialEvents.filter.Filter;
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
		List<MatchRow> matchRows = new ArrayList<MatchRow>();

		MatchKurzInfo[] matches = getMatches(filter);
		if (matches != null) {
			int matchCount = 1;
			for (MatchKurzInfo matchKurzInfo : matches) {
				Matchdetails details = DBManager.instance().getMatchDetails(
						matchKurzInfo.getMatchID());
				if (!filterOutByTactic(details, filter)) {
					List<MatchRow> rows = getMatchRows(matchKurzInfo, details, filter);
					if (rows != null && !rows.isEmpty()) {
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
			int id = filter.getTactic().intValue();
			if (details.getHomeTacticType() != id && details.getGuestTacticType() != id) {
				return true;
			}
		}
		return false;
	}

	private MatchKurzInfo[] getMatches(Filter filter) {
		List<MatchRow> matchRows = new ArrayList<MatchRow>();
		StringBuilder whereClause = new StringBuilder(" WHERE ");

		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		whereClause.append(" (GastID=" + teamId + " OR HeimID=" + teamId + ") ");
		whereClause.append(" AND (Status=").append(MatchKurzInfo.FINISHED).append(")");

		if (filter.getSeasonFilterValue() != SeasonFilterValue.ALL_SEASONS) {
			Timestamp datumAb = getDatumAb(filter.getSeasonFilterValue());
			whereClause.append(" AND (MATCHDATE > '").append(this.dateformat.format(datumAb));
			whereClause.append("')");
		}

		List<Integer> matchTypes = new ArrayList<Integer>();
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
			matchTypes.add(MatchType.DIVISIONBATTLE.getId());
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
		List<MatchRow> matchLines = new ArrayList<MatchRow>();
		List<MatchEvent> highlights = getMatchHighlights(details, filter);

		if (!highlights.isEmpty() || !filter.isShowMatchesWithSEOnly()) {

			Match match = new Match();
			match.setHostingTeam(kurzInfos.getHeimName());
			match.setHostingTeamId(kurzInfos.getHeimID());
			match.setHostingTeamTactic(details.getHomeTacticType());
			match.setMatchDate(new Date(kurzInfos.getMatchDateAsTimestamp().getTime()));
			match.setMatchId(kurzInfos.getMatchID());
			match.setMatchResult(kurzInfos.getHeimTore() + " - " + kurzInfos.getGastTore());
			match.setVisitingTeam(kurzInfos.getGastName());
			match.setVisitingTeamId(kurzInfos.getGastID());
			match.setVisitingTeamTactic(details.getGuestTacticType());
			match.setWeather(Weather.getById(details.getWetterId()));
			match.setMatchType(kurzInfos.getMatchTyp());

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
		List<MatchEvent> allHighlights = details.getHighlights();
		List<MatchEvent> filteredHighlights = new ArrayList<MatchEvent>();

		for (MatchEvent highlight : allHighlights) {
			if (checkForSE(highlight, filter)) {
				filteredHighlights.add(highlight);
			}
		}

		return filteredHighlights;
	}

	public static boolean isNegativeSE(MatchEvent highlight) {
		if (highlight.isGoalEvent() || highlight.isNonGoalEvent()) {
			// Chances (miss/goal)
			if (highlight.getiMatchEventID() == MatchEvent.MatchEventID.SE_TIRED_DEFENDER_MISTAKE_STRIKER_SCORES.ordinal()
					|| highlight.getiMatchEventID() == MatchEvent.MatchEventID.SE_INEXPERIENCED_DEFENDER_CAUSES_GOAL.ordinal()
					|| highlight.getiMatchEventID() == MatchEvent.MatchEventID.SE_GOAL_UNPREDICTABLE_MISTAKE.ordinal()) {
				return true;
			}
		} else if (highlight.isNegativeSpecialtyWeatherSE()) {
			// negative Weather SE
			return true;
		}
		return false;
	}

	public static EventType getEventType(MatchEvent highlight) {
		if (highlight.isSpecialtyWeatherSE()) {return EventType.SPECIALTY_WEATHER_SE;}
		else if(highlight.isSpecialtyNonWeatherSE()) {return EventType.SPECIALTY_NON_WEATHER_SE;}
		else if(highlight.isOtherSE()) {return EventType.NON_SPECIALTY_SE;}
		else if(highlight.isCounterAttack()) {return EventType.COUNTER_ATTACK;}
		else if(highlight.isFreeKick()) {return EventType.FREEKICK;}
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
		} else if (!filter.isShowFreeKickIndirect() && eventType == EventType.IFK) {
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
			List<Player> players = new ArrayList<Player>();
			players.addAll(HOVerwaltung.instance().getModel().getAllSpieler());
			if (!filter.isShowCurrentOwnPlayersOnly()) {
				players.addAll(HOVerwaltung.instance().getModel().getAllOldSpieler());
			}

			boolean playerFound = false;
			for (Player player : players) {
				if (isInvolved(player.getSpielerID(), highlight)) {
					// player found in list of current players
					playerFound = true;
					break;
				}
			}

			if (!playerFound) {
				return false;
			}
		}

		return true;
	}


	public static String getSEText(MatchEvent highlight) {return highlight.getEventTextDescription();}

	private boolean isInvolved(int playerId, MatchEvent highlight) {
		return (playerId == highlight.getGehilfeID() || playerId == highlight.getSpielerID());
	}

	private static String findName(MatchEvent highlight) {
		if (highlight.isSpecialtyWeatherSE()) {return highlight.getSpielerName();}
		else if (highlight.isGoalEvent() || highlight.isNonGoalEvent()) {
			MatchEvent.MatchEventID me = MatchEvent.MatchEventID.values()[highlight.getiMatchEventID()];
			switch (me) {
				case SE_GOAL_UNPREDICTABLE_LONG_PASS:
				case SE_GOAL_UNPREDICTABLE_SPECIAL_ACTION:
				case SE_QUICK_RUSHES_PASSES_AND_RECEIVER_SCORES:
				case SE_GOAL_CORNER_TO_ANYONE:
				case SE_GOAL_CORNER_HEAD_SPECIALIST:
				case SE_WINGER_TO_HEAD_SPEC_SCORES:  // #137
				case SE_WINGER_TO_ANYONE_SCORES: // #138
					return highlight.getGehilfeName() + " - " + highlight.getSpielerName();
				default:
					return highlight.getSpielerName();
			}
		}
		return "?";
	}

	public static String getSpielerName(MatchEvent highlight) {
		String name = "";
		// if(highlight.getTeamID() == teamId && !isNegativeSE(highlight))
		if (highlight.getTeamID() == HOVerwaltung.instance().getModel().getBasics().getTeamId()) {
			// Our team has an SE
			if (!isNegativeSE(highlight)) {
				// positive SE (our player) -> black
				name = findName(highlight) + "|*";
			} else if (highlight.isNegativeSpecialtyWeatherSE()) {
				// negative weather SE (our player) -> red
				name = findName(highlight) + "|-";
			} else {
				// Negative SE of other Team
				// negative SE (other player helps our team) -> gray
				name = highlight.getGehilfeName() + "|#";
			}
		} else {
			// other team has an SE
			if (!highlight.isSpecialtyWeatherSE() && isNegativeSE(highlight)) {
				// negative SE (our player helps the other team) -> red
				name = highlight.getGehilfeName() + "|-";
			} else {
				// SE from other team -> gray
				name = findName(highlight) + "|#";
			}
		}
		return name;
	}

	private static Timestamp getDatumAb(SeasonFilterValue period) {
		Date date = null;
		if (period.getId() == 1) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		// Date toDay = cal.getTime();
		int week = HOVerwaltung.instance().getModel().getBasics().getSpieltag();
		int tag = cal.get(7);
		int corrTag = 0;
		if (tag != 7) {
			corrTag = tag;
		}
		cal.add(7, -corrTag);
		cal.add(3, -(week - 1));
		if (period.getId() == 2) {
			cal.add(3, -16);
		}
		date = cal.getTime();
		return new Timestamp(date.getTime());
	}

	/**
	 * Convenience method for getLangStr(key)
	 * 
	 * @param key
	 *            the key for the language string
	 * @return the string for the current language
	 */
	private static String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}
}
