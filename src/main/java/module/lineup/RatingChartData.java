package module.lineup;

import core.model.HOVerwaltung;
import core.model.Ratings;

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
	private ArrayList<Double> mapKeys = null;
	private String[] chartCaptions = null;
	private Hashtable<Double, Double> mapDD;
	private Hashtable<Double, Integer> mapDI;

	private void parsePrepare() {
		if(mapKeys == null) {
			mapKeys = new ArrayList(ratings.getMidfield().keySet());
			mapKeys.remove(-90d);  //remove 90' and 120'
			mapKeys.remove(-120d); //average placeholder labels
			Collections.sort(mapKeys, Collections.reverseOrder());
		}
		if(chartCaptions == null) {
			ArrayList<String> captionList = new ArrayList();
			for(Double key : mapKeys) {
				captionList.add(String.valueOf(key.intValue()));
			}
			chartCaptions = captionList.stream().toArray(String[]::new);
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
		if(leftDefense.length != 0) return leftDefense;
		mapDD = ratings.getLeftDefense();
		leftDefense = parseDD(mapDD);
		return leftDefense;
	}

	double[] getCentralDefense() {
		if(centralDefense.length != 0) return centralDefense;
		mapDD = ratings.getCentralDefense();
		centralDefense = parseDD(mapDD);
		return centralDefense;
	}

	double[] getRightDefense() {
		if(rightDefense.length != 0) return rightDefense;
		mapDD = ratings.getRightDefense();
		rightDefense = parseDD(mapDD);
		return rightDefense;
	}

	double[] getHatStats() {
		if(hatStats.length != 0) return hatStats;
		mapDI = ratings.getHatStats();
		hatStats = parseDI(mapDI);
		return hatStats;
	}

	double[] getMidfield() {
		if(midfield.length != 0) return midfield;
		mapDD = ratings.getMidfield();
		midfield = parseDD(mapDD);
		return midfield;
	}

	double[] getLoddar() {
		if(loddar.length != 0) return loddar;
		mapDD = ratings.getLoddarStat();
		loddar = parseDD(mapDD);
		return loddar;
	}

	double[] getLeftAttack() {
		if(leftAttack.length != 0) return leftAttack;
		mapDD = ratings.getLeftAttack();
		leftAttack = parseDD(mapDD);
		return leftAttack;
	}

	double[] getCentralAttack() {
		if(centralAttack.length != 0) return centralAttack;
		mapDD = ratings.getCentralAttack();
		centralAttack = parseDD(mapDD);
		return centralAttack;
	}

	double[] getRightAttack() {
		if(rightAttack.length != 0) return rightAttack;
		mapDD = ratings.getRightAttack();
		rightAttack = parseDD(mapDD);
		return rightAttack;
	}

	String[] getCaptions() {
		if(chartCaptions != null) return chartCaptions;
		parsePrepare();
		return chartCaptions;
	}
}
