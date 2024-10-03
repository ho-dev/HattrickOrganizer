package module.lineup.substitution;

import core.model.TranslationFacility;
import core.model.player.IMatchRoleID;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.RedCardCriteria;

public class LanguageStringLookup {

	public static String getOrderType(MatchOrderType orderType) {
		return switch (orderType) {
			case NEW_BEHAVIOUR -> TranslationFacility.tr("subs.TypeOrder");
			case POSITION_SWAP -> TranslationFacility.tr("subs.TypeSwap");
			case SUBSTITUTION -> TranslationFacility.tr("subs.TypeSub");
			case MAN_MARKING -> TranslationFacility.tr("subs.TypeManMarking");
			default -> null;
		};
	}

	public static String getBehaviour(byte id) {
		return switch (id) {
			case (-1) -> TranslationFacility.tr("subs.BehNoChange");
			case IMatchRoleID.NORMAL -> TranslationFacility.tr("ls.player.behaviour.normal");
			case IMatchRoleID.OFFENSIVE -> TranslationFacility.tr("ls.player.behaviour.offensive");
			case IMatchRoleID.DEFENSIVE -> TranslationFacility.tr("ls.player.behaviour.defensive");
			case IMatchRoleID.TOWARDS_MIDDLE -> TranslationFacility.tr("ls.player.behaviour.towardsmiddle");
			case IMatchRoleID.TOWARDS_WING -> TranslationFacility.tr("ls.player.behaviour.towardswing");
			default -> "UNKNOWN_BEHAVIOUR";
		};
	}

	public static String getPosition(int id) {
		return switch (id) {
			case IMatchRoleID.keeper -> TranslationFacility.tr("subs.gk");
			case IMatchRoleID.rightBack -> TranslationFacility.tr("subs.rb");
			case IMatchRoleID.rightCentralDefender -> TranslationFacility.tr("subs.rcd");
			case IMatchRoleID.middleCentralDefender -> TranslationFacility.tr("subs.mcd");
			case IMatchRoleID.leftCentralDefender -> TranslationFacility.tr("subs.lcd");
			case IMatchRoleID.leftBack -> TranslationFacility.tr("subs.lb");
			case IMatchRoleID.rightWinger -> TranslationFacility.tr("subs.rw");
			case IMatchRoleID.rightInnerMidfield -> TranslationFacility.tr("subs.rim");
			case IMatchRoleID.centralInnerMidfield -> TranslationFacility.tr("subs.cim");
			case IMatchRoleID.leftInnerMidfield -> TranslationFacility.tr("subs.lim");
			case IMatchRoleID.leftWinger -> TranslationFacility.tr("subs.lw");
			case IMatchRoleID.rightForward -> TranslationFacility.tr("subs.rfw");
			case IMatchRoleID.centralForward -> TranslationFacility.tr("subs.cfw");
			case IMatchRoleID.leftForward -> TranslationFacility.tr("subs.lfw");
			case IMatchRoleID.substGK1 -> TranslationFacility.tr("subs.subgk");
			case IMatchRoleID.substWB1 -> TranslationFacility.tr("subs.subwb");
			case IMatchRoleID.substCD1 -> TranslationFacility.tr("subs.subdef");
			case IMatchRoleID.substIM1 -> TranslationFacility.tr("subs.submid");
			case IMatchRoleID.substWI1 -> TranslationFacility.tr("subs.subwing");
			case IMatchRoleID.substFW1 -> TranslationFacility.tr("subs.subfw");
			case IMatchRoleID.substXT1 -> TranslationFacility.tr("subs.subxtra");
			default -> "";
		};
	}

	public static String getStanding(GoalDiffCriteria standing) {
		return switch (standing) {
			case ANY_STANDING -> TranslationFacility.tr("subs.GoalAny");
			case MATCH_IS_TIED -> TranslationFacility.tr("subs.GoalTied");
			case IN_THE_LEAD -> TranslationFacility.tr("subs.GoalLead");
			case DOWN -> TranslationFacility.tr("subs.GoalDown");
			case IN_THE_LEAD_BY_MORE_THAN_ONE -> TranslationFacility.tr("subs.GoalLeadMT1");
			case DOWN_BY_MORE_THAN_ONE -> TranslationFacility.tr("subs.GoalDownMT1");
			case NOT_DOWN -> TranslationFacility.tr("subs.GoalNotDown");
			case NOT_IN_THE_LEAD -> TranslationFacility.tr("subs.GoalNotLead");
			case IN_THE_LEAD_BY_MORE_THAN_TWO -> TranslationFacility.tr("subs.GoalLeadMT2");
			case DOWN_BY_MORE_THAN_TWO -> TranslationFacility.tr("subs.GoalDownMT2");
			case MATCH_IS_NOT_TIED -> TranslationFacility.tr("subs.MatchIsNotTied");
			case NOT_IN_THE_LEAD_BY_MORE_THAN_ONE -> TranslationFacility.tr("subs.GoalNotLeadMT1");
			case NOT_DOWN_BY_MORE_THAN_ONE -> TranslationFacility.tr("subs.GoalNotDownMT1");
			case NOT_IN_THE_LEAD_BY_MORE_THAN_TWO -> TranslationFacility.tr("subs.GoalNotLeadMT2");
			case NOT_DOWN_BY_MORE_THAN_TWO -> TranslationFacility.tr("subs.GoalNotDownMT2");
			default -> "";
		};
	}

	public static String getRedCard(RedCardCriteria redCardCriteria) {
		return switch (redCardCriteria) {
			case IGNORE -> TranslationFacility.tr("subs.RedIgnore");
			case MY_PLAYER -> TranslationFacility.tr("subs.RedMy");
			case OPPONENT_PLAYER -> TranslationFacility.tr("subs.RedOpp");
			case MY_CENTRAL_DEFENDER -> TranslationFacility.tr("subs.RedMyCD");
			case MY_MIDFIELDER -> TranslationFacility.tr("subs.RedMyMF");
			case MY_FORWARD -> TranslationFacility.tr("subs.RedMyFW");
			case MY_WING_BACK -> TranslationFacility.tr("subs.RedMyWB");
			case MY_WINGER -> TranslationFacility.tr("subs.RedMyWI");
			case OPPONENT_CENTRAL_DEFENDER -> TranslationFacility.tr("subs.RedOppCD");
			case OPPONENT_MIDFIELDER -> TranslationFacility.tr("subs.RedOppMF");
			case OPPONENT_FORAWARD -> TranslationFacility.tr("subs.RedOppFW");
			case OPPONENT_WING_BACK -> TranslationFacility.tr("subs.RedOppWB");
			case OPPONENT_WINGER -> TranslationFacility.tr("subs.RedOppWi");
			default -> "";
		};
	}
}
