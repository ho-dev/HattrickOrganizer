package module.lineup.penalties;

import core.constants.player.PlayerSkill;
import core.constants.player.PlayerSpeciality;
import core.model.player.Player;
import core.rating.RatingPredictionManager;

public class PenaltyTaker {

	private final Player player;

	public PenaltyTaker(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return this.player;
	}

	public double getAbility() {
		double ability;
		double loy = RatingPredictionManager.getLoyaltyEffect(player);

		ability = getExperience() * 1.5
					+ (getSetPieces() + loy) * 0.7
					+ (getScoring() + loy) * 0.3;

		if (getPlayer().getPlayerSpecialty() == PlayerSpeciality.TECHNICAL) {
			ability *= 1.1;
		}
		return ability;
	}

	public double getScoring() {
		return ((double) this.player.getSCskill())
				+ this.player.getSub4Skill(PlayerSkill.SCORING);
	}

	public double getSetPieces() {
		return ((double) this.player.getSPskill())
				+ this.player.getSub4Skill(PlayerSkill.SET_PIECES);
	}

	public double getExperience() {
		return ((double) this.player.getExperience())
				+ this.player.getSub4Skill(PlayerSkill.EXPERIENCE);
	}
}
