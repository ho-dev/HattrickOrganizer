package module.lineup.ratings;

import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.Ratings;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;


public final class RatingChartData {

	private Ratings ratings = HOVerwaltung.instance().getModel().getCurrentLineupTeamRecalculated().getLineup().getRatings();
	private double[] leftDefense = {};
	private double avgLeftDefense90, avgLeftDefenseET, avgCentralDefense90, avgCentralDefenseET, avgRightDefense90, avgRightDefenseET;
	private double avgLeftAttack90, avgLeftAttackET, avgCentralAttack90, avgCentralAttackET, avgRightAttack90, avgRightAttackET;
	private double avgHatStats90, avgHatStatsET, avgMidfield90, avgMidfieldET, avgLoddar90, avgLoddarET;
	private double[] centralDefense = {};
	private double[] rightDefense = {};
	private double[] hatStats = {};
	private double[] midfield = {};
	private double[] loddar = {};
	private double[] leftAttack = {};
	private double[] centralAttack = {};
	private double[] rightAttack = {};
	private Integer[] chartCaptions = null;
	private ArrayList<Double> mapKeys = null;
	private boolean ET = UserParameter.instance().RatingChartFrame_ET;
	private int RTstartIdx = 0;
	private int RTendIdx = 0;
	private Hashtable<Double, Double> mapDD;
	private Hashtable<Double, Integer> mapDI;

	public void setET(boolean extraTime) {
		ET = extraTime;
	}

	private void parsePrepare() {
		if(mapKeys == null) {
			mapKeys = new ArrayList<>(ratings.getMidfield().keySet());
			mapKeys.remove(-90d);  //remove 90' and 120'
			mapKeys.remove(-120d); //average placeholder labels
			mapKeys.sort(Collections.reverseOrder());
			RTstartIdx = mapKeys.indexOf(90d);
			RTendIdx = mapKeys.size();
		}
		if(chartCaptions == null) {
			ArrayList<Integer> captionList = new ArrayList<>();
			for(Double key : mapKeys) {
				captionList.add(key.intValue());
			}
			chartCaptions = captionList.toArray(Integer[]::new);
		}
	}

	private double[] parseDD(Hashtable<Double, Double> source) {
		parsePrepare();
		ArrayList<Double> valueList = new ArrayList<>();
		for(Double key : mapKeys) {
			valueList.add(source.get(key));
		}
		return valueList.stream().mapToDouble(Double::doubleValue).toArray();
	}

	private double[] parseDI(Hashtable<Double, Integer> source) {
		parsePrepare();
		ArrayList<Double> valueList = new ArrayList<>();
		for(Double key : mapKeys) {
			valueList.add(source.get(key).doubleValue());
		}
		return valueList.stream().mapToDouble(Double::doubleValue).toArray();
	}

	double[] getLeftDefense() {
		if(leftDefense.length == 0) {
			mapDD = ratings.getLeftDefense();
			avgLeftDefense90 = mapDD.get(-90d);
			avgLeftDefenseET = mapDD.get(-120d);
			leftDefense = parseDD(mapDD);
		}
		if(ET) return leftDefense;
		else return Arrays.copyOfRange(leftDefense, RTstartIdx, RTendIdx);
	}

	double getLeftDefenseAvg() {
		if(leftDefense.length == 0) {
			mapDD = ratings.getLeftDefense();
			avgLeftDefense90 = mapDD.get(-90d);
			avgLeftDefenseET = mapDD.get(-120d);
			leftDefense = parseDD(mapDD);
		}
		if(ET) return avgLeftDefenseET;
		else return avgLeftDefense90;
	}


	double[] getCentralDefense() {
		if(centralDefense.length == 0) {
			mapDD = ratings.getCentralDefense();
			avgCentralDefense90 = mapDD.get(-90d);
			avgCentralDefenseET = mapDD.get(-120d);
			centralDefense = parseDD(mapDD);
		}
		if(ET) return centralDefense;
		else return Arrays.copyOfRange(centralDefense, RTstartIdx, RTendIdx);
	}

	double getCentralDefenseAvg() {
		if(centralDefense.length == 0) {
			mapDD = ratings.getCentralDefense();
			avgCentralDefense90 = mapDD.get(-90d);
			avgCentralDefenseET = mapDD.get(-120d);
			centralDefense = parseDD(mapDD);
		}
		if(ET) return avgCentralDefenseET;
		else return avgCentralDefense90;
	}

	double[] getRightDefense() {
		if(rightDefense.length == 0) {
			mapDD = ratings.getRightDefense();
			avgRightDefense90 = mapDD.get(-90d);
			avgRightDefenseET = mapDD.get(-120d);
			rightDefense = parseDD(mapDD);
		}
		if(ET) return rightDefense;
		else return Arrays.copyOfRange(rightDefense, RTstartIdx, RTendIdx);
	}

	double getRightDefenseAvg() {
		if(rightDefense.length == 0) {
			mapDD = ratings.getRightDefense();
			avgRightDefense90 = mapDD.get(-90d);
			avgRightDefenseET = mapDD.get(-120d);
			rightDefense = parseDD(mapDD);
		}
		if(ET) return avgRightDefenseET;
		else return avgRightDefense90;
	}

	double[] getHatStats() {
		if(hatStats.length == 0) {
			mapDI = ratings.getHatStats();
			avgHatStats90 = mapDI.get(-90d);
			avgHatStatsET = mapDI.get(-120d);
			hatStats = parseDI(mapDI);
		}
		if(ET) return hatStats;
		else return Arrays.copyOfRange(hatStats, RTstartIdx, RTendIdx);
	}

	double getHatStatsAvg() {
		if(hatStats.length == 0) {
			mapDI = ratings.getHatStats();
			avgHatStats90 = mapDI.get(-90d);
			avgHatStatsET = mapDI.get(-120d);
			hatStats = parseDI(mapDI);
		}
		if(ET) return avgHatStatsET;
		else return avgHatStats90;
	}

	double[] getMidfield() {
		if(midfield.length == 0) {
			mapDD = ratings.getMidfield();
			avgMidfield90 = mapDD.get(-90d);
			avgMidfieldET = mapDD.get(-120d);
			midfield = parseDD(mapDD);
		}
		if(ET) return midfield;
		else return Arrays.copyOfRange(midfield, RTstartIdx, RTendIdx);
	}

	double getMidfieldAvg() {
		if(midfield.length == 0) {
			mapDD = ratings.getMidfield();
			avgMidfield90 = mapDD.get(-90d);
			avgMidfieldET = mapDD.get(-120d);
			midfield = parseDD(mapDD);
		}
		if(ET) return avgMidfieldET;
		else return avgMidfield90;
	}

	double[] getLoddar() {
		if(loddar.length == 0) {
			mapDD = ratings.getLoddarStat();
			avgLoddar90 = mapDD.get(-90d);
			avgLoddarET = mapDD.get(-120d);
			loddar = parseDD(mapDD);
		}
		if(ET) return loddar;
		else return Arrays.copyOfRange(loddar, RTstartIdx, RTendIdx);
	}

	double getLoddarAvg() {
		if(loddar.length == 0) {
			mapDD = ratings.getMidfield();
			avgLoddar90 = mapDD.get(-90d);
			avgLoddarET = mapDD.get(-120d);
			loddar = parseDD(mapDD);
		}
		if(ET) return avgLoddarET;
		else return avgLoddar90;
	}

	double[] getLeftAttack() {
		if(leftAttack.length == 0) {
			mapDD = ratings.getLeftAttack();
			avgLeftAttack90 = mapDD.get(-90d);
			avgLeftAttackET = mapDD.get(-120d);
			leftAttack = parseDD(mapDD);
		}
		if(ET) return leftAttack;
		else return Arrays.copyOfRange(leftAttack, RTstartIdx, RTendIdx);
	}

	double getLeftAttackAvg() {
		if(leftAttack.length == 0) {
			mapDD = ratings.getLeftAttack();
			avgLeftAttack90 = mapDD.get(-90d);
			avgLeftAttackET = mapDD.get(-120d);
			leftAttack = parseDD(mapDD);
		}
		if(ET) return avgLeftAttackET;
		else return avgLeftAttack90;
	}

	double[] getCentralAttack() {
		if(centralAttack.length == 0) {
			mapDD = ratings.getCentralAttack();
			avgCentralAttack90 = mapDD.get(-90d);
			avgCentralAttackET = mapDD.get(-120d);
			centralAttack = parseDD(mapDD);
		}
		if(ET) return centralAttack;
		else return Arrays.copyOfRange(centralAttack, RTstartIdx, RTendIdx);
	}

	double getCentralAttackAvg() {
		if(centralAttack.length == 0) {
			mapDD = ratings.getCentralAttack();
			avgCentralAttack90 = mapDD.get(-90d);
			avgCentralAttackET = mapDD.get(-120d);
			centralAttack = parseDD(mapDD);
		}
		if(ET) return avgCentralAttackET;
		else return avgCentralAttack90;
	}

	double[] getRightAttack() {
		if(rightAttack.length == 0) {
			mapDD = ratings.getRightAttack();
			avgRightAttack90 = mapDD.get(-90d);
			avgRightAttackET = mapDD.get(-120d);
			rightAttack = parseDD(mapDD);
		}
		if(ET) return rightAttack;
		else return Arrays.copyOfRange(rightAttack, RTstartIdx, RTendIdx);
	}

	double getRightAttackAvg() {
		if(rightAttack.length == 0) {
			mapDD = ratings.getRightAttack();
			avgRightAttack90 = mapDD.get(-90d);
			avgRightAttackET = mapDD.get(-120d);
			rightAttack = parseDD(mapDD);
		}
		if(ET) return avgRightAttackET;
		else return avgRightAttack90;
	}

	ArrayList<Integer> getCaptions() {
		if(chartCaptions == null) {
			parsePrepare();
		}
		ArrayList<Integer> result = new ArrayList<>();
		if(ET) {
			Collections.addAll(result, chartCaptions);
		}
		else {
			Collections.addAll(result, Arrays.copyOfRange(chartCaptions, RTstartIdx, RTendIdx));
		}
		return result;
	}

}
