package module.lineup.substitution;

import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.RedCardCriteria;

public class LanguageStringLookup {

	public static String getOrderType(MatchOrderType orderType) {
		return switch (orderType) {
			case NEW_BEHAVIOUR -> HOVerwaltung.instance().getLanguageString("subs.TypeOrder");
			case POSITION_SWAP -> HOVerwaltung.instance().getLanguageString("subs.TypeSwap");
			case SUBSTITUTION -> HOVerwaltung.instance().getLanguageString("subs.TypeSub");
			case MAN_MARKING -> HOVerwaltung.instance().getLanguageString("subs.TypeManMarking");
			default -> null;
		};
	}

	public static String getBehaviour(byte id) {
		return switch (id) {
			case (-1) -> HOVerwaltung.instance().getLanguageString("subs.BehNoChange");
			case IMatchRoleID.NORMAL -> HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal");
			case IMatchRoleID.OFFENSIVE -> HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive");
			case IMatchRoleID.DEFENSIVE -> HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive");
			case IMatchRoleID.TOWARDS_MIDDLE -> HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardsmiddle");
			case IMatchRoleID.TOWARDS_WING -> HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardswing");
			default -> "UNKNOWN_BEHAVIOUR";
		};
	}

	public static String getPosition(int id) {
		return switch (id) {
			case IMatchRoleID.keeper -> HOVerwaltung.instance().getLanguageString("subs.gk");
			case IMatchRoleID.rightBack -> HOVerwaltung.instance().getLanguageString("subs.rb");
			case IMatchRoleID.rightCentralDefender -> HOVerwaltung.instance().getLanguageString("subs.rcd");
			case IMatchRoleID.middleCentralDefender -> HOVerwaltung.instance().getLanguageString("subs.mcd");
			case IMatchRoleID.leftCentralDefender -> HOVerwaltung.instance().getLanguageString("subs.lcd");
			case IMatchRoleID.leftBack -> HOVerwaltung.instance().getLanguageString("subs.lb");
			case IMatchRoleID.rightWinger -> HOVerwaltung.instance().getLanguageString("subs.rw");
			case IMatchRoleID.rightInnerMidfield -> HOVerwaltung.instance().getLanguageString("subs.rim");
			case IMatchRoleID.centralInnerMidfield -> HOVerwaltung.instance().getLanguageString("subs.cim");
			case IMatchRoleID.leftInnerMidfield -> HOVerwaltung.instance().getLanguageString("subs.lim");
			case IMatchRoleID.leftWinger -> HOVerwaltung.instance().getLanguageString("subs.lw");
			case IMatchRoleID.rightForward -> HOVerwaltung.instance().getLanguageString("subs.rfw");
			case IMatchRoleID.centralForward -> HOVerwaltung.instance().getLanguageString("subs.cfw");
			case IMatchRoleID.leftForward -> HOVerwaltung.instance().getLanguageString("subs.lfw");
			case IMatchRoleID.substGK1 -> HOVerwaltung.instance().getLanguageString("subs.subgk");
			case IMatchRoleID.substWB1 -> HOVerwaltung.instance().getLanguageString("subs.subwb");
			case IMatchRoleID.substCD1 -> HOVerwaltung.instance().getLanguageString("subs.subdef");
			case IMatchRoleID.substIM1 -> HOVerwaltung.instance().getLanguageString("subs.submid");
			case IMatchRoleID.substWI1 -> HOVerwaltung.instance().getLanguageString("subs.subwing");
			case IMatchRoleID.substFW1 -> HOVerwaltung.instance().getLanguageString("subs.subfw");
			case IMatchRoleID.substXT1 -> HOVerwaltung.instance().getLanguageString("subs.subxtra");
			default -> "";
		};
	}

	public static String getStanding(GoalDiffCriteria standing) {
		return switch (standing) {
			case ANY_STANDING -> HOVerwaltung.instance().getLanguageString("subs.GoalAny");
			case MATCH_IS_TIED -> HOVerwaltung.instance().getLanguageString("subs.GoalTied");
			case IN_THE_LEAD -> HOVerwaltung.instance().getLanguageString("subs.GoalLead");
			case DOWN -> HOVerwaltung.instance().getLanguageString("subs.GoalDown");
			case IN_THE_LEAD_BY_MORE_THAN_ONE -> HOVerwaltung.instance().getLanguageString("subs.GoalLeadMT1");
			case DOWN_BY_MORE_THAN_ONE -> HOVerwaltung.instance().getLanguageString("subs.GoalDownMT1");
			case NOT_DOWN -> HOVerwaltung.instance().getLanguageString("subs.GoalNotDown");
			case NOT_IN_THE_LEAD -> HOVerwaltung.instance().getLanguageString("subs.GoalNotLead");
			case IN_THE_LEAD_BY_MORE_THAN_TWO -> HOVerwaltung.instance().getLanguageString("subs.GoalLeadMT2");
			case DOWN_BY_MORE_THAN_TWO -> HOVerwaltung.instance().getLanguageString("subs.GoalDownMT2");
			case MATCH_IS_NOT_TIED -> HOVerwaltung.instance().getLanguageString("subs.MatchIsNotTied");
			case NOT_IN_THE_LEAD_BY_MORE_THAN_ONE -> HOVerwaltung.instance().getLanguageString("subs.GoalNotLeadMT1");
			case NOT_DOWN_BY_MORE_THAN_ONE -> HOVerwaltung.instance().getLanguageString("subs.GoalNotDownMT1");
			case NOT_IN_THE_LEAD_BY_MORE_THAN_TWO -> HOVerwaltung.instance().getLanguageString("subs.GoalNotLeadMT2");
			case NOT_DOWN_BY_MORE_THAN_TWO -> HOVerwaltung.instance().getLanguageString("subs.GoalNotDownMT2");
			default -> "";
		};
	}

	public static String getRedCard(RedCardCriteria redCardCriteria) {
		return switch (redCardCriteria) {
			case IGNORE -> HOVerwaltung.instance().getLanguageString("subs.RedIgnore");
			case MY_PLAYER -> HOVerwaltung.instance().getLanguageString("subs.RedMy");
			case OPPONENT_PLAYER -> HOVerwaltung.instance().getLanguageString("subs.RedOpp");
			case MY_CENTRAL_DEFENDER -> HOVerwaltung.instance().getLanguageString("subs.RedMyCD");
			case MY_MIDFIELDER -> HOVerwaltung.instance().getLanguageString("subs.RedMyMF");
			case MY_FORWARD -> HOVerwaltung.instance().getLanguageString("subs.RedMyFW");
			case MY_WING_BACK -> HOVerwaltung.instance().getLanguageString("subs.RedMyWB");
			case MY_WINGER -> HOVerwaltung.instance().getLanguageString("subs.RedMyWI");
			case OPPONENT_CENTRAL_DEFENDER -> HOVerwaltung.instance().getLanguageString("subs.RedOppCD");
			case OPPONENT_MIDFIELDER -> HOVerwaltung.instance().getLanguageString("subs.RedOppMF");
			case OPPONENT_FORAWARD -> HOVerwaltung.instance().getLanguageString("subs.RedOppFW");
			case OPPONENT_WING_BACK -> HOVerwaltung.instance().getLanguageString("subs.RedOppWB");
			case OPPONENT_WINGER -> HOVerwaltung.instance().getLanguageString("subs.RedOppWi");
			default -> "";
		};
	}
}
