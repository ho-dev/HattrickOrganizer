package module.lineup.penalties;

import core.constants.player.PlayerSkill;
import core.constants.player.PlayerSpeciality;
import core.model.player.Spieler;
import core.rating.RatingPredictionManager;

public class PenaltyTaker {

	private Spieler player;

	public PenaltyTaker(Spieler player) {
		this.player = player;
	}

	public Spieler getPlayer() {
		return this.player;
	}

	public double getAbility() {
		double ability = 0;
		double loy = (double)RatingPredictionManager.getLoyaltyHomegrownBonus(player);

		ability = getExperience() * 1.5
					+ (getSetPieces() + loy) * 0.7
					+ (getScoring() + loy) * 0.3;

		if (getPlayer().getSpezialitaet() == PlayerSpeciality.TECHNICAL) {
			ability *= 1.1;
		}
		return ability;
	}

	public double getScoring() {
		return ((double) this.player.getTorschuss())
				+ this.player.getSubskill4Pos(PlayerSkill.SCORING);
	}

	public double getSetPieces() {
		return ((double) this.player.getStandards())
				+ this.player.getSubskill4Pos(PlayerSkill.SET_PIECES);
	}

	public double getExperience() {
		return ((double) this.player.getErfahrung())
				+ this.player.getSubskill4Pos(PlayerSkill.EXPERIENCE);
	}
}
