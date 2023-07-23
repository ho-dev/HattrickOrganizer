package module.lineup.substitution.plausibility;

import core.model.HOVerwaltung;
import core.model.player.Player;
import module.lineup.Lineup;
import module.lineup.assistant.LineupAssistant;
import module.lineup.substitution.LanguageStringLookup;
import module.lineup.substitution.model.Substitution;

import static module.lineup.substitution.model.MatchOrderType.*;

public class PlausibilityCheck {

	public static Problem checkForProblem(Lineup lineup, Substitution substitution) {
		int subjectPlayerID = substitution.getSubjectPlayerID();
		int objectPlayerID = substitution.getObjectPlayerID();

		if (subjectPlayerID < -1) {
			return Error.PLAYEROUT_NOT_REAL; 
		}
		
		// check if there is a subjectPlayer
		if (subjectPlayerID <= 0 || LineupAssistant.isPlayerEnabledForLineup(subjectPlayerID)) {
			return switch (substitution.getOrderType()) {
				case SUBSTITUTION -> Error.SUBSTITUTION_PLAYER_MISSING;
				case POSITION_SWAP -> Error.POSITIONSWAP_PLAYER_MISSING;
				case NEW_BEHAVIOUR -> Error.NEWBEHAVIOUR_PLAYER_MISSING;
				case MAN_MARKING -> Error.MANMARKING_PLAYER_MISSING;
			};
		}

		if ( substitution.getOrderType() != MAN_MARKING) {
			// check if there is a objectPlayerID (not relevant for NEW_BEHAVIOUR)
			if (substitution.getOrderType() != NEW_BEHAVIOUR) {
				if (objectPlayerID < -1) {
					return Error.PLAYERIN_NOT_REAL;
				}

				if (objectPlayerID <= 0 || LineupAssistant.isPlayerEnabledForLineup(objectPlayerID)) {
					if (substitution.getOrderType() == SUBSTITUTION) {
						return Error.SUBSTITUTION_PLAYER_MISSING;
					} else if (substitution.getOrderType() == POSITION_SWAP) {
						return Error.POSITIONSWAP_PLAYER_MISSING;
					}
				}
			}

			// check if players in lineup.
			//  - for NEW_BEHAVIOUR, there is only one player involved
			//  - for MAN_MARKING, the playerIn is member of the opponent team
			if (substitution.getOrderType() != NEW_BEHAVIOUR
					&& substitution.getOrderType() != MAN_MARKING
					&& !lineup.isPlayerInLineup(objectPlayerID)) {
				return Error.PLAYERIN_NOT_IN_LINEUP;
			} else if (!lineup.isPlayerInLineup(subjectPlayerID)) {
				return Error.PLAYEROUT_NOT_IN_LINEUP;
			}
		}
		else {
			//TODO: MAN_MARKING - check if playerIn is in opponent team
		}

		return null;
	}

	public static String getComment(Problem problem, Substitution substitution) {
		if (problem instanceof Error) {
			switch ((Error) problem) {
			case PLAYERIN_NOT_IN_LINEUP:
			case PLAYERIN_NOT_REAL:
				return HOVerwaltung.instance().getLanguageString(problem.getLanguageKey(),
						getPlayerIn(substitution).getFullName());
			case PLAYEROUT_NOT_IN_LINEUP:
			case PLAYEROUT_NOT_REAL:
				return HOVerwaltung.instance().getLanguageString(problem.getLanguageKey(),
						getPlayerOut(substitution).getFullName());
			default:
				return HOVerwaltung.instance().getLanguageString(problem.getLanguageKey());
			}
		} else if (problem instanceof Uncertainty) {
			switch ((Uncertainty) problem) {
			case SAME_TACTIC:
				return HOVerwaltung.instance().getLanguageString(problem.getLanguageKey(),
						getPlayerOut(substitution).getFullName(),
						LanguageStringLookup.getBehaviour(substitution.getBehaviour()));
			}
		}

		return null;
	}

	private static Player getPlayerIn(Substitution substitution) {
		return HOVerwaltung.instance().getModel().getCurrentPlayer(substitution.getObjectPlayerID());
	}

	private static Player getPlayerOut(Substitution substitution) {
		return HOVerwaltung.instance().getModel().getCurrentPlayer(substitution.getSubjectPlayerID());
	}
}
