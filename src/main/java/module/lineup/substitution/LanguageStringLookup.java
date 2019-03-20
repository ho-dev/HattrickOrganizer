package module.lineup.substitution;

import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.RedCardCriteria;

public class LanguageStringLookup {

	public static String getOrderType(MatchOrderType orderType) {
		switch (orderType) {
		case NEW_BEHAVIOUR:
			return HOVerwaltung.instance().getLanguageString("subs.TypeOrder");
		case POSITION_SWAP:
			return HOVerwaltung.instance().getLanguageString("subs.TypeSwap");
		case SUBSTITUTION:
			return HOVerwaltung.instance().getLanguageString("subs.TypeSub");
		default:
			return null;
		}
	}

	public static String getBehaviour(byte id) {
		switch (id) {
		case (-1):
			return HOVerwaltung.instance().getLanguageString("subs.BehNoChange");
		case IMatchRoleID.NORMAL:
			return HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal");
		case IMatchRoleID.OFFENSIVE:
			return HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive");
		case IMatchRoleID.DEFENSIVE:
			return HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive");
		case IMatchRoleID.TOWARDS_MIDDLE:
			return HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardsmiddle");
		case IMatchRoleID.TOWARDS_WING:
			return HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardswing");
		default:
			return "UNKNOWN_BEHAVIOUR";
		}
	}

	public static String getPosition(int id) {
		switch (id) {
		case IMatchRoleID.keeper:
			return HOVerwaltung.instance().getLanguageString("subs.gk");
		case IMatchRoleID.rightBack:
			return HOVerwaltung.instance().getLanguageString("subs.rb");
		case IMatchRoleID.rightCentralDefender:
			return HOVerwaltung.instance().getLanguageString("subs.rcd");
		case IMatchRoleID.middleCentralDefender:
			return HOVerwaltung.instance().getLanguageString("subs.mcd");
		case IMatchRoleID.leftCentralDefender:
			return HOVerwaltung.instance().getLanguageString("subs.lcd");
		case IMatchRoleID.leftBack:
			return HOVerwaltung.instance().getLanguageString("subs.lb");
		case IMatchRoleID.rightWinger:
			return HOVerwaltung.instance().getLanguageString("subs.rw");
		case IMatchRoleID.rightInnerMidfield:
			return HOVerwaltung.instance().getLanguageString("subs.rim");
		case IMatchRoleID.centralInnerMidfield:
			return HOVerwaltung.instance().getLanguageString("subs.cim");
		case IMatchRoleID.leftInnerMidfield:
			return HOVerwaltung.instance().getLanguageString("subs.lim");
		case IMatchRoleID.leftWinger:
			return HOVerwaltung.instance().getLanguageString("subs.lw");
		case IMatchRoleID.rightForward:
			return HOVerwaltung.instance().getLanguageString("subs.rfw");
		case IMatchRoleID.centralForward:
			return HOVerwaltung.instance().getLanguageString("subs.cfw");
		case IMatchRoleID.leftForward:
			return HOVerwaltung.instance().getLanguageString("subs.lfw");
		case IMatchRoleID.substGK1:
			return HOVerwaltung.instance().getLanguageString("subs.subgk");
		case IMatchRoleID.substCD1:
			return HOVerwaltung.instance().getLanguageString("subs.subdef");
		case IMatchRoleID.substIM1:
			return HOVerwaltung.instance().getLanguageString("subs.submid");
		case IMatchRoleID.substWI1:
			return HOVerwaltung.instance().getLanguageString("subs.subwing");
		case IMatchRoleID.substFW1:
			return HOVerwaltung.instance().getLanguageString("subs.subfw");
		default:
			return "";
		}
	}

	public static String getStanding(GoalDiffCriteria standing) {
		switch (standing) {
		case ANY_STANDING:
			return HOVerwaltung.instance().getLanguageString("subs.GoalAny");
		case MATCH_IS_TIED:
			return HOVerwaltung.instance().getLanguageString("subs.GoalTied");
		case IN_THE_LEAD:
			return HOVerwaltung.instance().getLanguageString("subs.GoalLead");
		case DOWN:
			return HOVerwaltung.instance().getLanguageString("subs.GoalDown");
		case IN_THE_LEAD_BY_MORE_THAN_ONE:
			return HOVerwaltung.instance()
					.getLanguageString("subs.GoalLeadMT1");
		case DOWN_BY_MORE_THAN_ONE:
			return HOVerwaltung.instance()
					.getLanguageString("subs.GoalDownMT1");
		case NOT_DOWN:
			return HOVerwaltung.instance()
					.getLanguageString("subs.GoalNotDown");
		case NOT_IN_THE_LEAD:
			return HOVerwaltung.instance()
					.getLanguageString("subs.GoalNotLead");
		case IN_THE_LEAD_BY_MORE_THAN_TWO:
			return HOVerwaltung.instance()
					.getLanguageString("subs.GoalLeadMT2");
		case DOWN_BY_MORE_THAN_TWO:
			return HOVerwaltung.instance()
					.getLanguageString("subs.GoalDownMT2");
		default:
			return "";
		}
	}

	public static String getRedCard(RedCardCriteria redCardCriteria) {
		switch (redCardCriteria) {
		case IGNORE:
			return HOVerwaltung.instance().getLanguageString("subs.RedIgnore");
		case MY_PLAYER:
			return HOVerwaltung.instance().getLanguageString("subs.RedMy");
		case OPPONENT_PLAYER:
			return HOVerwaltung.instance().getLanguageString("subs.RedOpp");
		case MY_CENTRAL_DEFENDER:
			return HOVerwaltung.instance().getLanguageString("subs.RedMyCD");
		case MY_MIDFIELDER:
			return HOVerwaltung.instance().getLanguageString("subs.RedMyMF");
		case MY_FORWARD:
			return HOVerwaltung.instance().getLanguageString("subs.RedMyFW");
		case MY_WING_BACK:
			return HOVerwaltung.instance().getLanguageString("subs.RedMyWB");
		case MY_WINGER:
			return HOVerwaltung.instance().getLanguageString("subs.RedMyWI");
		case OPPONENT_CENTRAL_DEFENDER:
			return HOVerwaltung.instance().getLanguageString("subs.RedOppCD");
		case OPPONENT_MIDFIELDER:
			return HOVerwaltung.instance().getLanguageString("subs.RedOppMF");
		case OPPONENT_FORAWARD:
			return HOVerwaltung.instance().getLanguageString("subs.RedOppFW");
		case OPPONENT_WING_BACK:
			return HOVerwaltung.instance().getLanguageString("subs.RedOppWB");
		case OPPONENT_WINGER:
			return HOVerwaltung.instance().getLanguageString("subs.RedOppWi");
		default:
			return "";
		}
	}
}
