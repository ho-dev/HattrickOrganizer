package module.lineup.penalties;

import core.constants.player.PlayerSkill;
import core.model.HOVerwaltung;
import core.model.player.Player;

public class PenaltyTaker {

	private final Player player;

	public PenaltyTaker(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return this.player;
	}

	public double getAbility() {
		var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
		return ratingPredictionModel.getPlayerPenaltyStrength(player);
	}

	public double getScoring() {
		return ((double) this.player.getScoringSkill())
				+ this.player.getSub4Skill(PlayerSkill.SCORING);
	}

	public double getSetPieces() {
		return ((double) this.player.getSetPiecesSkill())
				+ this.player.getSub4Skill(PlayerSkill.SET_PIECES);
	}

	public double getExperience() {
		return ((double) this.player.getExperience())
				+ this.player.getSub4Skill(PlayerSkill.EXPERIENCE);
	}
}
