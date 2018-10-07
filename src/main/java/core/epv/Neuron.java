package core.epv;

import java.util.ArrayList;

class Neuron {
	public static final int OFF = 0;
	public static final int LINEAR = 1;
	public static final int SIGMOID = 2;
	public static final int TANH = 3;
	public static final int GAUSS = 4;
	public static final int SOFTMAX = 5;
	public static final int EXTERNAL = 6;

	private double weight = 0;
	private double forcedInput = 0;
	private double delta = 0;
	private double multi = 1;
//	private boolean applyFormula = true;

	private int applyFormula = SIGMOID;
	private Layer activeLayer;

	private ArrayList<Synapse> synapses;

	Neuron () {
		synapses = new ArrayList<Synapse>();
	}

	void setActiveLayer (Layer activeLayer) {
		this.activeLayer = activeLayer;
	}

	void setApplyFormula(int formula) {
		this.applyFormula = formula;
	}

	void setForcedInput (double input) {
		this.forcedInput = input;
	}

	void addSynapse (Synapse synapse) {
		synapses.add(synapse);
	}

	private double getInput () {
		double retVal = weight;
		for (int i=0; i < getSynapseCount(); i++) {
			Synapse curSynapse = getSynapse(i);
			Neuron curFromNeuron = curSynapse.getFromNeuron();
			retVal += curFromNeuron.calculate() * curSynapse.getWeight();
		}
		return retVal;
	}

	double calculate () {
		double retVal = 0;
		if (getSynapseCount() == 0) {
			// Input Node -> get back unchanged input
			return (forcedInput-delta)/multi;
		}
		double input = getInput();
		switch (applyFormula) {
		case OFF:
			retVal = 0;
			break;
		case SIGMOID:
			retVal = sigmoid(input);
			break;
		case TANH:
			retVal = tanh(input);
			break;
		case LINEAR:
			// Use unchanged value
			retVal = input;
//			System.out.println ("unchanged output="+input);
			break;
		case GAUSS:
			retVal = gauss(input);
			break;
		case SOFTMAX:
			double normalization = 0;
			for (int i=0; i < activeLayer.getNeuronCount(); i++) {
				normalization += Math.exp(activeLayer.getNeuron(i).calculate());
			}
			if (normalization > 0)
				retVal = Math.exp(input)/normalization;
			else
				retVal = 1/activeLayer.getNeuronCount();
			break;
		case EXTERNAL:
			break;
		}
		return retVal * multi + delta;
	}

	private double gauss(double val) {
		return Math.exp(-val * val);
	}

	private double sigmoid(double val) {
		return (1/(1+Math.exp(-val)));
	}

	private static final double tanh(double x) { // Math.tanh is available since JDK 1.5 only
		double y= Math.exp(x);
		y*= y;
		return (y-1)/(y+1);
	}

	void setWeight (double weight) {
		this.weight = weight;
	}

	Synapse getSynapse (int num) {
		return synapses.get(num);
	}

	int getSynapseCount () {
		return synapses.size();
	}

	ArrayList<Synapse> getSynapseList () {
		return synapses;
	}

	void setNormalization (double delta, double multi) {
		this.delta = delta;
		this.multi = multi;
	}

	@Override
	public String toString () {
		String retVal = "";
		retVal += "[Neuron: ";
		if (delta > 0)
			retVal += "delta="+delta+", ";
		if (multi != 1)
			retVal += "multi="+multi+", ";
		retVal += "weight="+weight + " ";
		for (int i=0; i<getSynapseCount(); i++) {
			retVal += getSynapse(i).toString();
			if (i<getSynapseCount()-1)
				retVal += ", ";
		}
		retVal += "]";
		return retVal;
	}
}
