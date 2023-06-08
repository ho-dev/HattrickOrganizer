package module.lineup.substitution;

import core.datatype.CBItem;
import core.model.HOVerwaltung;
import module.lineup.Lineup;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.ht.HattrickManager;
import module.teamAnalyzer.vo.PlayerInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static core.model.player.IMatchRoleID.aFieldAndSubsMatchRoleID;

public class SubstitutionDataProvider {

	public static Map<Integer, PlayerPositionItem> getFieldAndSubPlayerPosition() {
		LinkedHashMap<Integer, PlayerPositionItem> positionMap = new LinkedHashMap<>();
		Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();

		for (Integer i : aFieldAndSubsMatchRoleID) {
			var player = lineup.getPlayerByPositionID(i);
			if (player != null) {
				positionMap.put(i,	new PlayerPositionItem(i, lineup.getPlayerByPositionID(i)));
			}
		}
		return positionMap;
	}


	public static List<PlayerPositionItem> getFieldPositions(List<Integer> aMatchRoleID , boolean includeEmptyPositions) {
		List<PlayerPositionItem> playerItems = new ArrayList<>();

		Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
		for (Integer i : aMatchRoleID) {
			var player = lineup.getPlayerByPositionID(i);
			if (player != null || includeEmptyPositions) {
				playerItems.add(new PlayerPositionItem(i, player));
			}
		}
		return playerItems;
	}

	public static List<PlayerPositionItem> getFieldPositions(int start, int end, boolean includeEmptyPositions) {
		List<PlayerPositionItem> playerItems = new ArrayList<>();

		Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
		for (int i = start; i <= end; i++) {
			var player = lineup.getPlayerByPositionID(i);
			if (player != null || includeEmptyPositions) {
				playerItems.add(new PlayerPositionItem(i, player));
			}
		}
		return playerItems;
	}

	/**
	 * Returns an {@link CBItem} array with all standings which can be chosen
	 * for a substitution.
	 *
	 * @return an array with standing items
	 */
	public static CBItem[] getStandingItems() {
		var values = GoalDiffCriteria.values();
		CBItem[] ret = new CBItem[values.length];
		int i = 0;
		for (var goalDifferenceCriteria : GoalDiffCriteria.values()){
			ret[i++] = new CBItem(LanguageStringLookup.getStanding(goalDifferenceCriteria), goalDifferenceCriteria.getId());
		}
		return ret;
	}

	/**
	 * Returns an {@link CBItem} array with all red card events which can be
	 * chosen for a substitution.
	 *
	 * @return an array with red card items
	 */
	public static CBItem[] getRedCardItems() {
		return new CBItem[]{
				new CBItem(HOVerwaltung.instance().getLanguageString("subs.RedIgnore"), -1),
				new CBItem(HOVerwaltung.instance().getLanguageString("subs.RedMy"), 1),
				new CBItem(HOVerwaltung.instance().getLanguageString("subs.RedOpp"), 2),
				new CBItem(HOVerwaltung.instance().getLanguageString("subs.RedMyCD"), 11),
				new CBItem(HOVerwaltung.instance().getLanguageString("subs.RedMyMF"), 12),
				new CBItem(HOVerwaltung.instance().getLanguageString("subs.RedMyFW"), 13),
				new CBItem(HOVerwaltung.instance().getLanguageString("subs.RedMyWB"), 14),
				new CBItem(HOVerwaltung.instance().getLanguageString("subs.RedMyWI"), 15),
				new CBItem(HOVerwaltung.instance().getLanguageString("subs.RedOppCD"), 21),
				new CBItem(HOVerwaltung.instance().getLanguageString("subs.RedOppMF"), 22),
				new CBItem(HOVerwaltung.instance().getLanguageString("subs.RedOppFW"), 23),
				new CBItem(HOVerwaltung.instance().getLanguageString("subs.RedOppWB"), 24),
				new CBItem(HOVerwaltung.instance().getLanguageString("subs.RedOppWi"), 25), };
	}

	/**
	 * Returns an {@link CBItem} array with all behaviours which can be chosen
	 * for a substitution.
	 *
	 * @return a list of behaviour items
	 */
	public static List<CBItem> getBehaviourItems(boolean withInheritItem) {
		List<CBItem> behaviourValues = new ArrayList<>();
		HOVerwaltung hov = HOVerwaltung.instance();
		if (withInheritItem) {
			behaviourValues.add(new CBItem(hov.getLanguageString("subs.BehNoChange"), -1));
		}
		behaviourValues.add(new CBItem(hov.getLanguageString("ls.player.behaviour.normal"), 0));
		behaviourValues.add(new CBItem(hov.getLanguageString("ls.player.behaviour.offensive"), 1));
		behaviourValues.add(new CBItem(hov.getLanguageString("ls.player.behaviour.defensive"), 2));
		behaviourValues.add(new CBItem(hov.getLanguageString("ls.player.behaviour.towardsmiddle"), 3));
		behaviourValues.add(new CBItem(hov.getLanguageString("ls.player.behaviour.towardswing"), 4));
		return behaviourValues;
	}

	public static List<PlayerInfo> getOpponentPlayers() {
		int teamId = SystemManager.getActiveTeamId();
		return  HattrickManager.downloadPlayers(teamId);
	}
}
