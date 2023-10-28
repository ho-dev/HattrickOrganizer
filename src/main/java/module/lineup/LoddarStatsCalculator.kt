package module.lineup;

public class LoddarStatsCalculator {

	private double midfieldRating;
	private double rightDefenseRating;
	private double centralDefenseRating;
	private double leftDefenseRating;
	private double rightAttackRating;
	private double centralAttackRating;
	private double leftAttackRating;
	private int tacticType;
	private float tacticLevelAimAow;
	private float tacticLevelCounter;

	public float calculate() {

		// Version 3.1 formulars (gives values between 1....80),
		// http://oeufi-foed.doesel.ch/LoddarStats/LoddarStats-Inside-3.htm
		float Abwehrstaerke;
		float Angriffsstaerke;
		float MFF;
		float KK;
		float KZG;
		float KAG;
		float AG;
		float AF;
		float CA;
		float AoW;
		float AiM;

		// constants
		// float MFS=0.2f, VF=0.45f, ZG=0.5f, KG=0.25f;
		// updated to V3.2
		final float MFS = 0.0f;

		// constants
		// float MFS=0.2f, VF=0.45f, ZG=0.5f, KG=0.25f;
		// updated to V3.2
		final float VF = 0.47f;

		// constants
		// float MFS=0.2f, VF=0.45f, ZG=0.5f, KG=0.25f;
		// updated to V3.2
		float ZG = 0.37f;

		// constants
		// float MFS=0.2f, VF=0.45f, ZG=0.5f, KG=0.25f;
		// updated to V3.2
		final float KG = 0.25f;

		MFF = MFS + ((1.0f - MFS) * HQ(midfieldRating));

		AG = (1.0f - ZG) / 2.0f;
		Abwehrstaerke = VF
				* ((ZG * HQ(centralDefenseRating)) + (AG * (HQ(leftDefenseRating) + HQ(rightDefenseRating))));

		// AiM or AoW or CA?
		if (tacticType == core.model.match.IMatchDetails.TAKTIK_MIDDLE) {
			AiM = (float) Math.min(tacticLevelAimAow, 20.0d);
			KZG = ZG + (((0.2f * (AiM - 1.0f)) / 19.0f) + 0.2f);
			KK = 0.0f;
		} else if (tacticType == core.model.match.IMatchDetails.TAKTIK_WINGS) {
			AoW = (float) Math.min(tacticLevelAimAow, 20.0d);
			KZG = ZG - (((0.2f * (AoW - 1.0f)) / 19.0f) + 0.2f);
			KK = 0.0f;
		} else if (tacticType == core.model.match.IMatchDetails.TAKTIK_KONTER) {
			CA = (float) Math.min(tacticLevelCounter, 20.0d);
			KK = KG * 2.0f * (CA / (CA + 20.0f));
			KZG = ZG;
		} else {
			KZG = ZG;
			KK = 0.0f;
		}

		KAG = (1.0f - KZG) / 2.0f;
		AF = 1.0f - VF;
		Angriffsstaerke = (AF + KK)
				* ((KZG * HQ(centralAttackRating)) + (KAG * (HQ(leftAttackRating) + HQ(rightAttackRating))));

		return 80.0f * (MFF * (Abwehrstaerke + Angriffsstaerke));
	}

	public void setRatings(double midfieldRating, double rightDefenseRating,
			double centralDefenseRating, double leftDefenseRating,
			double rightAttackRating, double centralAttackRating,
			double leftAttackRating) {
		this.midfieldRating = midfieldRating;
		this.rightDefenseRating = rightDefenseRating;
		this.centralDefenseRating = centralDefenseRating;
		this.leftDefenseRating = leftDefenseRating;
		this.rightAttackRating = rightAttackRating;
		this.centralAttackRating = centralAttackRating;
		this.leftAttackRating = leftAttackRating;
	}

	public final float HQ(double x) {
		// first convert to original HT rating (1...80)
		float htRating = (float) Lineup.HTfloat2int(x);

		// and now LoddarStats Hattrick-Quality function (?)
		double result = (2.0f * htRating) / (htRating + 80.0f);
		return (float) result;
	}

	public void setTactics(int tacticType, float tacticLevelAimAow,
			float tacticLevelCounter) {
		this.tacticType = tacticType;
		this.tacticLevelAimAow = tacticLevelAimAow;
		this.tacticLevelCounter = tacticLevelCounter;
	}

}