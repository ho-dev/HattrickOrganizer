package module.lineup.ratings;

import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.Ratings;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;


public final class RatingChartData {

	private Ratings ratings = HOVerwaltung.instance().getModel().getLineup().getRatings();
	private double[] leftDefense = {};
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
			mapKeys = new ArrayList(ratings.getMidfield().keySet());
			mapKeys.remove(-90d);  //remove 90' and 120'
			mapKeys.remove(-120d); //average placeholder labels
			Collections.sort(mapKeys, Collections.reverseOrder());
			RTstartIdx = mapKeys.indexOf(90d);
			RTendIdx = mapKeys.size();
		}
		if(chartCaptions == null) {
			ArrayList<Integer> captionList = new ArrayList();
			for(Double key : mapKeys) {
				captionList.add(key.intValue());
			}
			chartCaptions = captionList.stream().toArray(Integer[]::new);
		}
	}

	private double[] parseDD(Hashtable<Double, Double> source) {
		parsePrepare();
		ArrayList<Double> valueList = new ArrayList();
		for(Double key : mapKeys) {
			valueList.add(source.get(key));
		}
		return valueList.stream().mapToDouble(Double::doubleValue).toArray();
	}

	private double[] parseDI(Hashtable<Double, Integer> source) {
		parsePrepare();
		ArrayList<Double> valueList = new ArrayList();
		for(Double key : mapKeys) {
			valueList.add(source.get(key).doubleValue());
		}
		return valueList.stream().mapToDouble(Double::doubleValue).toArray();
	}

	double[] getLeftDefense() {
		if(leftDefense.length == 0) {
			mapDD = ratings.getLeftDefense();
			leftDefense = parseDD(mapDD);
		}
		if(ET) return leftDefense;
		else return Arrays.copyOfRange(leftDefense, RTstartIdx, RTendIdx);
	}

	double[] getCentralDefense() {
		if(centralDefense.length == 0) {
			mapDD = ratings.getCentralDefense();
			centralDefense = parseDD(mapDD);
		}
		if(ET) return centralDefense;
		else return Arrays.copyOfRange(centralDefense, RTstartIdx, RTendIdx);
	}

	double[] getRightDefense() {
		if(rightDefense.length == 0) {
			mapDD = ratings.getRightDefense();
			rightDefense = parseDD(mapDD);
		}
		if(ET) return rightDefense;
		else return Arrays.copyOfRange(rightDefense, RTstartIdx, RTendIdx);
	}

	double[] getHatStats() {
		if(hatStats.length == 0) {
			mapDI = ratings.getHatStats();
			hatStats = parseDI(mapDI);
		}
		if(ET) return hatStats;
		else return Arrays.copyOfRange(hatStats, RTstartIdx, RTendIdx);
	}

	double[] getMidfield() {
		if(midfield.length == 0) {
			mapDD = ratings.getMidfield();
			midfield = parseDD(mapDD);
		}
		if(ET) return midfield;
		else return Arrays.copyOfRange(midfield, RTstartIdx, RTendIdx);
	}

	double[] getLoddar() {
		if(loddar.length == 0) {
			mapDD = ratings.getLoddarStat();
			loddar = parseDD(mapDD);
		}
		if(ET) return loddar;
		else return Arrays.copyOfRange(loddar, RTstartIdx, RTendIdx);
	}

	double[] getLeftAttack() {
		if(leftAttack.length == 0) {
			mapDD = ratings.getLeftAttack();
			leftAttack = parseDD(mapDD);
		}
		if(ET) return leftAttack;
		else return Arrays.copyOfRange(leftAttack, RTstartIdx, RTendIdx);
	}

	double[] getCentralAttack() {
		if(centralAttack.length == 0) {
			mapDD = ratings.getCentralAttack();
			centralAttack = parseDD(mapDD);
		}
		if(ET) return centralAttack;
		else return Arrays.copyOfRange(centralAttack, RTstartIdx, RTendIdx);
	}

	double[] getRightAttack() {
		if(rightAttack.length == 0) {
			mapDD = ratings.getRightAttack();
			rightAttack = parseDD(mapDD);
		}
		if(ET) return rightAttack;
		else return Arrays.copyOfRange(rightAttack, RTstartIdx, RTendIdx);
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
