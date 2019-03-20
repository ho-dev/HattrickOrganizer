package module.specialEvents;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.IMatchHighlight;
import core.model.match.MatchHighlight;
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
		List<MatchHighlight> highlights = getMatchHighlights(details, filter);

		if (!highlights.isEmpty() || !filter.isShowMatchesWithSEOnly()) {
			// the matchline
			Match match = new Match();
			match.setHostingTeam(kurzInfos.getHeimName());
			match.setHostingTeamId(kurzInfos.getHeimID());
			match.setHostingTeamTactic(details.getHomeTacticType());
			match.setMatchDate(new Date(kurzInfos.getMatchDateAsTimestamp().getTime()));
			match.setMatchId(kurzInfos.getMatchID());
			match.setMatchResult(String.valueOf(kurzInfos.getHeimTore()) + " - "
					+ String.valueOf(kurzInfos.getGastTore()));
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

			for (MatchHighlight highlight : highlights) {
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

	private List<MatchHighlight> getMatchHighlights(Matchdetails details, Filter filter) {
		List<MatchHighlight> allHighlights = details.getHighlights();
		List<MatchHighlight> filteredHighlights = new ArrayList<MatchHighlight>();

		for (MatchHighlight highlight : allHighlights) {
			if (checkForSE(highlight, filter)) {
				filteredHighlights.add(highlight);
			}
		}

		return filteredHighlights;
	}

	public static boolean isNegativeSE(MatchHighlight highlight) {
		if (highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_ERFOLGREICH
				|| highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_FEHLGESCHLAGEN) {
			// Chances (miss/goal)
			if (highlight.getHighlightSubTyp() == IMatchHighlight.HIGHLIGHT_SUB_SCHLECHTE_KONDITION_BALLVERLUST_TOR
					|| highlight.getHighlightSubTyp() == IMatchHighlight.HIGHLIGHT_SUB_UNERFAHREN_TOR
					|| highlight.getHighlightSubTyp() == IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_BALLVERLUST_TOR) {
				return true;
			}
		} else if (isNegativeWeatherSE(highlight)) {
			// negative Weather SE
			return true;
		}
		return false;
	}

	public static EventType getEventType(MatchHighlight highlight) {
		if (isWeatherSE(highlight)) {
			return EventType.WEATHERSE;
		} else if (highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_ERFOLGREICH
				|| highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_FEHLGESCHLAGEN) {
			switch (highlight.getHighlightSubTyp()) {
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_VORLAGE_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_ABGEFANGEN_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_BALL_ERKAEMPFT_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_BALLVERLUST_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_PASS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_SCHLECHTE_KONDITION_BALLVERLUST_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_ECKBALL_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_ECKBALL_KOPFTOR:
			case IMatchHighlight.HIGHLIGHT_SUB_ERFAHRENER_ANGREIFER_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNERFAHREN_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_QUERPASS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_AUSSERGEWOEHNLICHER_PASS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_TECHNIKER_ANGREIFER_TOR:
				return EventType.SPECIALTYSE;
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_EINS:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_ZWEI:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_DREI:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_VIER:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_FUENF:
				return EventType.COUNTER;
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_2:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_3:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_4:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_5:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_6:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_7:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_8:
				return EventType.FREEKICK;
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_2:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_3:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_4:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_5:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_6:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_7:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_8:
				return EventType.PENALTY;
			case IMatchHighlight.HIGHLIGHT_SUB_INDIRECT_FREEKICK_1:
			case IMatchHighlight.HIGHLIGHT_SUB_INDIRECT_FREEKICK_2:
				return EventType.IFK;
			case IMatchHighlight.HIGHLIGHT_SUB_LONGHSHOT_1:
			case IMatchHighlight.HIGHLIGHT_SUB_WEITSCHUSS_TOR:
				return EventType.LONGSHOT;
			}
		}
		return null;
	}

	private boolean checkForSE(MatchHighlight highlight, Filter filter) {
		EventType eventType = getEventType(highlight);
		if (eventType == null) {
			return false;
		} else if (!filter.isShowSpecialitySE() && eventType == EventType.SPECIALTYSE) {
			return false;
		} else if (!filter.isShowWeatherSE() && eventType == EventType.WEATHERSE) {
			return false;
		} else if (!filter.isShowCounterAttack() && eventType == EventType.COUNTER) {
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

	public static boolean isWeatherSE(MatchHighlight highlight) {
		return (isPositiveWeatherSE(highlight) || isNegativeWeatherSE(highlight));
	}

	public static boolean isPositiveWeatherSE(MatchHighlight highlight) {
		if (highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_SPEZIAL) {
			if (highlight.getHighlightSubTyp() == IMatchHighlight.HIGHLIGHT_SUB_PLAYER_POWERFUL_RAINY
					|| highlight.getHighlightSubTyp() == IMatchHighlight.HIGHLIGHT_SUB_PLAYER_TECHNICAL_SUNNY) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNegativeWeatherSE(MatchHighlight highlight) {
		if (highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_SPEZIAL) {
			if (highlight.getHighlightSubTyp() == IMatchHighlight.HIGHLIGHT_SUB_PLAYER_TECHNICAL_RAINY
					|| highlight.getHighlightSubTyp() == IMatchHighlight.HIGHLIGHT_SUB_PLAYER_POWERFUL_SUNNY
					|| highlight.getHighlightSubTyp() == IMatchHighlight.HIGHLIGHT_SUB_PLAYER_QUICK_RAINY
					|| highlight.getHighlightSubTyp() == IMatchHighlight.HIGHLIGHT_SUB_PLAYER_QUICK_SUNNY) {
				return true;
			}
		}
		return false;
	}

	public static String getSEText(MatchHighlight highlight) {

		if (isWeatherSE(highlight)) {
			switch (highlight.getHighlightSubTyp()) {
			case IMatchHighlight.HIGHLIGHT_SUB_PLAYER_TECHNICAL_RAINY:
				return getLangStr("WEATHER_TECHNICAL_RAINY");
			case IMatchHighlight.HIGHLIGHT_SUB_PLAYER_POWERFUL_RAINY:
				return getLangStr("WEATHER_POWERFUL_RAINY");
			case IMatchHighlight.HIGHLIGHT_SUB_PLAYER_TECHNICAL_SUNNY:
				return getLangStr("WEATHER_TECHNICAL_SUNNY");
			case IMatchHighlight.HIGHLIGHT_SUB_PLAYER_POWERFUL_SUNNY:
				return getLangStr("WEATHER_POWERFUL_SUNNY");
			case IMatchHighlight.HIGHLIGHT_SUB_PLAYER_QUICK_RAINY:
				return getLangStr("WEATHER_QUICK_RAINY");
			case IMatchHighlight.HIGHLIGHT_SUB_PLAYER_QUICK_SUNNY:
				return getLangStr("WEATHER_QUICK_SUNNY");
			}
		} else if (highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_ERFOLGREICH
				|| highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_FEHLGESCHLAGEN) {
			// Non-weather SE
			switch (highlight.getHighlightSubTyp()) {
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_VORLAGE_TOR:
				return getLangStr("UNVORHERSEHBAR_PASS_VORLAGE_TOR");
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_ABGEFANGEN_TOR:
				return getLangStr("UNVORHERSEHBAR_PASS_ABGEFANGEN_TOR");
			case IMatchHighlight.HIGHLIGHT_SUB_WEITSCHUSS_TOR:
				return getLangStr("WEITSCHUSS_TOR");
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_BALL_ERKAEMPFT_TOR:
				return getLangStr("UNVORHERSEHBAR_BALL_ERKAEMPFT_TOR");
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_BALLVERLUST_TOR:
				return getLangStr("UNVORHERSEHBAR_BALLVERLUST_TOR");
			case IMatchHighlight.HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_TOR:
				return getLangStr("SCHNELLER_ANGREIFER_TOR");
			case IMatchHighlight.HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_PASS_TOR:
				return getLangStr("SCHNELLER_ANGREIFER_PASS_TOR");
			case IMatchHighlight.HIGHLIGHT_SUB_SCHLECHTE_KONDITION_BALLVERLUST_TOR:
				return getLangStr("SCHLECHTE_KONDITION_BALLVERLUST_TOR");
			case IMatchHighlight.HIGHLIGHT_SUB_ECKBALL_TOR:
				return getLangStr("ECKBALL_TOR");
			case IMatchHighlight.HIGHLIGHT_SUB_ECKBALL_KOPFTOR:
				return getLangStr("ECKBALL_KOPFTOR");
			case IMatchHighlight.HIGHLIGHT_SUB_ERFAHRENER_ANGREIFER_TOR:
				return getLangStr("ERFAHRENER_ANGREIFER_TOR");
			case IMatchHighlight.HIGHLIGHT_SUB_UNERFAHREN_TOR:
				return getLangStr("UNERFAHREN_TOR");
			case IMatchHighlight.HIGHLIGHT_SUB_QUERPASS_TOR:
				return getLangStr("QUERPASS_TOR");
			case IMatchHighlight.HIGHLIGHT_SUB_AUSSERGEWOEHNLICHER_PASS_TOR:
				return getLangStr("AUSSERGEWOEHNLICHER_PASS_TOR");
			case IMatchHighlight.HIGHLIGHT_SUB_TECHNIKER_ANGREIFER_TOR:
				return getLangStr("TECHNIKER_ANGREIFER_TOR");
			case IMatchHighlight.HIGHLIGHT_SUB_QUICK_RUSH_STOPPED_BY_DEF:
				return getLangStr("QUICK_RUSH_STOPPED_BY_DEF");
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_EINS:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_ZWEI:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_DREI:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_VIER:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_FUENF:
				return getLangStr("ls.match.event.counter-attack");
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_2:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_3:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_4:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_5:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_6:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_7:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_8:
				return getLangStr("highlight_freekick");
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_2:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_3:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_4:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_5:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_6:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_7:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_8:
				return getLangStr("highlight_penalty");
			case IMatchHighlight.HIGHLIGHT_SUB_INDIRECT_FREEKICK_1:
			case IMatchHighlight.HIGHLIGHT_SUB_INDIRECT_FREEKICK_2:
				return getLangStr("IFK");
			case IMatchHighlight.HIGHLIGHT_SUB_LONGHSHOT_1:
				return getLangStr("ls.match.event.longshot");
			}

		}
		return "unknown";
	}

	private boolean isInvolved(int playerId, MatchHighlight highlight) {
		if (isWeatherSE(highlight)) {
			return playerId == highlight.getSpielerID();
		} else if (highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_ERFOLGREICH
				|| highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_FEHLGESCHLAGEN) {
			switch (highlight.getHighlightSubTyp()) {
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_VORLAGE_TOR:
				return (playerId == highlight.getGehilfeID() || playerId == highlight
						.getSpielerID());
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_ABGEFANGEN_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_WEITSCHUSS_TOR:
				return playerId == highlight.getSpielerID();
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_BALL_ERKAEMPFT_TOR:
				return (playerId == highlight.getGehilfeID() || playerId == highlight
						.getSpielerID());
			case IMatchHighlight.HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_TOR:
				return playerId == highlight.getSpielerID();
			case IMatchHighlight.HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_PASS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_ECKBALL_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_ECKBALL_KOPFTOR:
				return (playerId == highlight.getGehilfeID() || playerId == highlight
						.getSpielerID());
			case IMatchHighlight.HIGHLIGHT_SUB_ERFAHRENER_ANGREIFER_TOR:
				return playerId == highlight.getSpielerID();
			case IMatchHighlight.HIGHLIGHT_SUB_QUERPASS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_AUSSERGEWOEHNLICHER_PASS_TOR:
				return (playerId == highlight.getGehilfeID() || playerId == highlight
						.getSpielerID());
			case IMatchHighlight.HIGHLIGHT_SUB_TECHNIKER_ANGREIFER_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_EINS:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_ZWEI:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_DREI:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_VIER:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_FUENF:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_2:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_3:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_4:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_5:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_6:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_7:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_8:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_2:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_3:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_4:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_5:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_6:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_7:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_8:
			case IMatchHighlight.HIGHLIGHT_SUB_INDIRECT_FREEKICK_1:
			case IMatchHighlight.HIGHLIGHT_SUB_INDIRECT_FREEKICK_2:
			case IMatchHighlight.HIGHLIGHT_SUB_LONGHSHOT_1:
				return playerId == highlight.getSpielerID();
			}
		}
		return false;
	}

	private static String findName(MatchHighlight highlight) {
		if (isWeatherSE(highlight)) {
			return highlight.getSpielerName();
		} else if (highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_ERFOLGREICH
				|| highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_FEHLGESCHLAGEN) {
			switch (highlight.getHighlightSubTyp()) {
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_VORLAGE_TOR:
				return highlight.getGehilfeName() + " - " + highlight.getSpielerName();
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_ABGEFANGEN_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_WEITSCHUSS_TOR:
				return highlight.getSpielerName();
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_BALL_ERKAEMPFT_TOR:
				return highlight.getGehilfeName() + " - " + highlight.getSpielerName();
			case IMatchHighlight.HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_TOR:
				return highlight.getSpielerName();
			case IMatchHighlight.HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_PASS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_ECKBALL_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_ECKBALL_KOPFTOR:
				return highlight.getGehilfeName() + " - " + highlight.getSpielerName();
			case IMatchHighlight.HIGHLIGHT_SUB_ERFAHRENER_ANGREIFER_TOR:
				return highlight.getSpielerName();
			case IMatchHighlight.HIGHLIGHT_SUB_QUERPASS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_AUSSERGEWOEHNLICHER_PASS_TOR:
				return highlight.getGehilfeName() + " - " + highlight.getSpielerName();
			case IMatchHighlight.HIGHLIGHT_SUB_TECHNIKER_ANGREIFER_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_EINS:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_ZWEI:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_DREI:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_VIER:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_FUENF:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_2:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_3:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_4:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_5:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_6:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_7:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_8:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_2:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_3:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_4:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_5:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_6:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_7:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_8:
			case IMatchHighlight.HIGHLIGHT_SUB_INDIRECT_FREEKICK_1:
			case IMatchHighlight.HIGHLIGHT_SUB_INDIRECT_FREEKICK_2:
			case IMatchHighlight.HIGHLIGHT_SUB_LONGHSHOT_1:
				return highlight.getSpielerName();
			}
		}
		return "?";
	}

	public static String getSpielerName(MatchHighlight highlight) {
		String name = "";
		// if(highlight.getTeamID() == teamId && !isNegativeSE(highlight))
		if (highlight.getTeamID() == HOVerwaltung.instance().getModel().getBasics().getTeamId()) {
			// Our team has an SE
			if (!isNegativeSE(highlight)) {
				// positive SE (our player) -> black
				name = findName(highlight) + "|*";
			} else if (isNegativeWeatherSE(highlight)) {
				// negative weather SE (our player) -> red
				name = findName(highlight) + "|-";
			} else {
				// Negative SE of other Team
				// negative SE (other player helps our team) -> gray
				name = highlight.getGehilfeName() + "|#";
			}
		} else {
			// other team has an SE

			if (!isWeatherSE(highlight) && isNegativeSE(highlight)) {
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
