package module.lineup.ratings;

import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.rating.RatingPredictionModel;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import static core.rating.RatingPredictionModel.RatingSector.*;

public final class RatingChartData {

	class RatingChartValues {

		double[] values;
		double average90Minutes;
		double average120Minutes;

		protected RatingChartValues(){}
		public RatingChartValues(RatingPredictionModel.RatingSector ratingSector){
			var ret = new ArrayList<Double>();
			var hoMOdel = HOVerwaltung.instance().getModel();
			var lineup = hoMOdel.getCurrentLineup();
			var ratingPredictionModel = hoMOdel.getRatingPredictionModel();
			for (var minute : getMinutes()){
				ret.add(ratingPredictionModel.getRating(lineup, ratingSector, minute));
			}
			values = ret.stream().mapToDouble(Double::doubleValue).toArray();
			average90Minutes = ratingPredictionModel.getAverageRating(lineup, ratingSector, 90);
			average120Minutes = ratingPredictionModel.getAverageRating(lineup, ratingSector, 120);
		}

		public double[] getValues(boolean isExtraTime) {
			if ( isExtraTime) return values;
			else return Arrays.copyOfRange(values, RTStartIdx, RTEndIdx);
		}
		public double getAverage(boolean isExtraTime){
			if (isExtraTime) return average120Minutes;
			else return average90Minutes;
		}
	}

	class HatStatsRatingChartValues extends RatingChartValues{
		public HatStatsRatingChartValues(){
			var values = new ArrayList<Double>();
			var hoMOdel = HOVerwaltung.instance().getModel();
			var lineup = hoMOdel.getCurrentLineup();
			var ratingPredictionModel = hoMOdel.getRatingPredictionModel();
			for (var minute : getMinutes()){
				values.add(ratingPredictionModel.getHatStats(lineup,  minute));
			}
			this.values = values.stream().mapToDouble(Double::doubleValue).toArray();
			this.average90Minutes = ratingPredictionModel.getAverage90HatStats(lineup);
			this.average120Minutes = ratingPredictionModel.getAverage120HatStats(lineup);
		}
	}
	class LoddarStatsRatingChartValues extends RatingChartValues{
		public LoddarStatsRatingChartValues(){
			var values = new ArrayList<Double>();
			var hoMOdel = HOVerwaltung.instance().getModel();
			var lineup = hoMOdel.getCurrentLineup();
			var ratingPredictionModel = hoMOdel.getRatingPredictionModel();
			for (var minute : getMinutes()){
				values.add(ratingPredictionModel.getLoddarStats(lineup,  minute));
			}
			this.values = values.stream().mapToDouble(Double::doubleValue).toArray();
			this.average90Minutes = ratingPredictionModel.getAverage90LoddarStats(lineup);
			this.average120Minutes = ratingPredictionModel.getAverage120LoddarStats(lineup);
		}
	}

	private RatingChartValues leftDefence;
	private RatingChartValues centralDefence;
	private RatingChartValues rightDefence;
	private RatingChartValues midfield;
	private RatingChartValues leftAttack;
	private RatingChartValues centralAttack;
	private RatingChartValues rightAttack;
	private HatStatsRatingChartValues hatStats;
	private LoddarStatsRatingChartValues loddarStats;
	private Integer[] chartCaptions = null;
	private ArrayList<Integer> mapKeys = null;
	private boolean isExtraTime = UserParameter.instance().RatingChartFrame_ET;
	private int RTStartIdx = 0;
	private int RTEndIdx = 0;

	public void setExtraTime(boolean extraTime) {
		isExtraTime = extraTime;
	}

	private ArrayList<Integer> getMinutes() {
		if(mapKeys == null) {
			var hoMOdel = HOVerwaltung.instance().getModel();
			var lineup = hoMOdel.getCurrentLineup();
			var ratingChangeMinutes = RatingPredictionModel.getRatingChangeMinutes(lineup, isExtraTime?120:90);
			mapKeys = new ArrayList<>(ratingChangeMinutes);
			mapKeys.sort(Collections.reverseOrder());
			RTStartIdx = mapKeys.indexOf(90);
			RTEndIdx = mapKeys.size();
		}
		if(chartCaptions == null) {
            ArrayList<Integer> captionList = new ArrayList<>(mapKeys);
			chartCaptions = captionList.toArray(Integer[]::new);
		}
		return mapKeys;
	}

	double[] getLeftDefence() {
		if(leftDefence == null) {
			leftDefence = new RatingChartValues(Defence_Left);
		}
		return leftDefence.getValues(isExtraTime);
	}

	double getLeftDefenceAvg() {
		if(leftDefence == null) {
			leftDefence = new RatingChartValues(Defence_Left);
		}
		return leftDefence.getAverage(isExtraTime);
	}

	double[] getCentralDefence() {
		if(centralDefence == null) {
			centralDefence = new RatingChartValues(Defence_Central);
		}
		return centralDefence.getValues(isExtraTime);
	}

	double getCentralDefenceAvg() {
		if(centralDefence == null) {
			centralDefence = new RatingChartValues(Defence_Central);
		}
		return centralDefence.getAverage(isExtraTime);
	}

	double[] getRightDefence() {
		if(rightDefence  == null) {
			rightDefence = new RatingChartValues(Defence_Right);
		}
		return rightDefence.getValues(isExtraTime);
	}

	double getRightDefenceAvg() {
		if(rightDefence == null) {
			rightDefence = new RatingChartValues(Defence_Right);
		}
		return rightDefence.getAverage(isExtraTime);
	}
	double[] getMidfield() {
		if(midfield == null) {
			midfield = new RatingChartValues(Midfield);
		}
		return midfield.getValues(isExtraTime);
	}

	double getMidfieldAvg() {
		if(midfield == null) {
			midfield = new RatingChartValues(Midfield);
		}
		return midfield.getAverage(isExtraTime);
	}

	double[] getLeftAttack() {
		if(leftAttack == null) {
			leftAttack = new RatingChartValues(Attack_Left);
		}
		return leftAttack.getValues(isExtraTime);
	}

	double getLeftAttackAvg() {
		if(leftAttack == null) {
			leftAttack = new RatingChartValues(Attack_Left);
		}
		return leftAttack.getAverage(isExtraTime);
	}

	double[] getCentralAttack() {
		if(centralAttack == null) {
			centralAttack = new RatingChartValues(Attack_Central);
		}
		return centralAttack.getValues(isExtraTime);
	}

	double getCentralAttackAvg() {
		if(centralAttack == null) {
			centralAttack = new RatingChartValues(Attack_Central);
		}
		return centralAttack.getAverage(isExtraTime);
	}

	double[] getRightAttack() {
		if(rightAttack == null) {
			rightAttack = new RatingChartValues(Attack_Right);
		}
		return rightAttack.getValues(isExtraTime);
	}

	double getRightAttackAvg() {
		if(rightAttack == null) {
			rightAttack = new RatingChartValues(Attack_Right);
		}
		return rightAttack.getAverage(isExtraTime);
	}

	double[] getHatStats() {
		if(hatStats == null) {
			hatStats = new HatStatsRatingChartValues();
		}
		return hatStats.getValues(isExtraTime);
	}

	double getHatStatsAvg() {
		if(hatStats == null) {
			hatStats = new HatStatsRatingChartValues();
		}
		return hatStats.getAverage(isExtraTime);
	}

	double[] getLoddar() {
		if(loddarStats==null) {
			loddarStats = new LoddarStatsRatingChartValues();
		}
		return loddarStats.getValues(isExtraTime);
	}

	double getLoddarAvg() {
		if(loddarStats==null) {
			loddarStats = new LoddarStatsRatingChartValues();
		}
		return loddarStats.getAverage(isExtraTime);
	}

	ArrayList<Integer> getCaptions() {
		if(chartCaptions == null) {
			getMinutes();
		}
		ArrayList<Integer> result = new ArrayList<>();
		if(isExtraTime) {
			Collections.addAll(result, chartCaptions);
		}
		else {
			Collections.addAll(result, Arrays.copyOfRange(chartCaptions, RTStartIdx, RTEndIdx));
		}
		return result;
	}

}
