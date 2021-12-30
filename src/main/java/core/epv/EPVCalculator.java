package core.epv;


import core.util.Helper;

import java.util.HashMap;
import java.util.Map;

class EPVCalculator
{

    private static EPVCalculator epvCalculator = null;
    private static String networkStructure = "@age,@fo,@xp,@lead,@st,@gk,@pm,@ps,@wi,@de,@sc,@sp,@spec,@agg,@pop,@hon,@week:50:50:price";
    private static String weightsFilename = "prediction/epvWeights.mlp";
    private static Net neuronalNetwork = new Net(networkStructure, weightsFilename);

    private EPVCalculator () {
//    	neuronalNetwork = new Net(networkStructure, weightsFilename);
//    	System.out.println(neuronalNetwork.toString());
    }

    static EPVCalculator getInstance() {
        if (epvCalculator == null)
            epvCalculator = new EPVCalculator();
        return epvCalculator;
    }

    private void normalize (Map<String,Double> inputMap) {
    	double age = inputMap.get("age");
    	inputMap.put("age", age - 17);
    }

    final double getPrice (EPVData iepvdata, int week, double currencyRate) {
    	Map<String,Double> inputMap = new HashMap<String,Double>();
    	inputMap.put("age", iepvdata.getAge() + iepvdata.getAgeDays() / 112d);
    	inputMap.put("fo", (double) iepvdata.getForm());
    	inputMap.put("xp", (double) iepvdata.getExperience());
    	inputMap.put("lead", (double) iepvdata.getLeadership());
    	inputMap.put("st", iepvdata.getStamina());
    	inputMap.put("gk", iepvdata.getGoalKeeping());
    	inputMap.put("pm", iepvdata.getPlayMaking());
    	inputMap.put("ps", iepvdata.getPassing());
    	inputMap.put("wi", iepvdata.getWing());
    	inputMap.put("de", iepvdata.getDefense());
    	inputMap.put("sc", iepvdata.getAttack());
    	inputMap.put("sp", iepvdata.getSetPieces());
    	inputMap.put("spec", (double) iepvdata.getSpeciality());
    	inputMap.put("agg", (double) iepvdata.getAggressivity());
    	inputMap.put("pop", (double) iepvdata.getPopularity());
    	inputMap.put("hon", (double) iepvdata.getHonesty());
    	inputMap.put("week", (double) week);

    	normalize (inputMap); // Normalize

    	double val = neuronalNetwork.calculate("price", inputMap);
    	double price = Math.pow(10, val*8);
    	// Round to thousands (in euro/dollar)
    	price = Helper.round(price, -3);
    	if (price < 1000)
    		price = 1000;
    	return (price * 10/currencyRate);
    }

}
