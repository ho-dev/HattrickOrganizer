package core.epv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * ROOT Java MultiLayerPerceptron
 * 
 * This class creates a Neuronal Net using the same syntax as the
 * CERN ROOT MultiLayerPerceptron
 * 
 * You can load the weights file produced by CERN ROOT and
 * then apply an input pattern to the Network
 * 
 * It consists of several Layer with Neurons and Synapses between these Neuron
 * 
 * @author Christian Erpelding <flattermannHO@gmail.com>
 *
 */
class Net {
	private static final int INVALID = 0;
	private static final int INPUT_NORM = 1;
	private static final int OUTPUT_NORM = 2;
	private static final int NEURONS_WEIGHT = 3;
	private static final int SYNAPSES_WEIGHT= 4;

	private static final int INPUT = 0;
	private static final int HIDDEN = 1;
	private static final int OUTPUT = 2;
	
	String structure;
	String [] inputParams;
	String [] outputParams;
	ArrayList<Layer> allLayers;
	boolean classifyOutputs = false;
	
	Net (String structure) {
		this.structure = structure;
		initStruct ();
	}

	Net (String structure, String filename) {
		this (structure);
		loadWeights(filename);
	}

	double calculate (double inputPar[]) {
		return calculate(0, inputPar);
	}

	double calculate (String outvarName, double[] inputPar) {
		int index = getOutParIndex(outvarName);
		if (index >= 0)
			return calculate(index, inputPar);
		System.out.println ("Net: Output parameter name not found: "+outvarName);
		return 0;

	}
	
	public double calculate (int index, double[]inputPar) {
		if (inputPar.length == inputParams.length) {
			// set inputs
			getInputLayer().setForcedInputs(inputPar);
			
			// calc output
			return getOutputLayer().getNeuron(index).calculate();
		} 
		System.out.println ("Net: Wrong number of input parameters");
		return 0;

	}

	public double calculate (int index, Map<?, ?> inputMap) {
		double[] inputPar = new double[inputParams.length];
		Iterator<?> nameIter = inputMap.keySet().iterator();
		while (nameIter.hasNext()) {
			String curName = (String)nameIter.next();
			Double curVal = (Double)inputMap.get(curName);
			int curIndex = getInParIndex(curName);
			inputPar[curIndex] = curVal.doubleValue();
		}
		return calculate(index, inputPar);
	}
	
	public double calculate (String outvarName, Map<?, ?> inputMap) {
		int index = getOutParIndex(outvarName);
		if (index >= 0)
			return calculate(index, inputMap);
		System.out.println ("Net: Output parameter name not found: "+outvarName);
		return 0;
	}

	private int getOutParIndex (String name) {
		for (int i=0; i < outputParams.length; i++) {
			if (outputParams[i].toLowerCase(java.util.Locale.ENGLISH).equals(name.toLowerCase(java.util.Locale.ENGLISH)))
				return i;
		}
		return -1;
	}

	private int getInParIndex (String name) {
		for (int i=0; i < inputParams.length; i++) {
			if (inputParams[i].toLowerCase(java.util.Locale.ENGLISH).equals(name.toLowerCase(java.util.Locale.ENGLISH)))
				return i;
		}
		return -1;
	}

	private void initStruct () {
		String[] splitted = structure.split(":");
		if (splitted.length < 3) {
			System.out.println ("Invalid net structure");
		} else {
			allLayers = new ArrayList<Layer>();
			String inputString = splitted[0];
			setInputParams(inputString);
			addLayer (INPUT, inputParams.length);
			for (int i=1; i<splitted.length-1; i++) {
				int curHiddenNeurons = Integer.parseInt(splitted[i]);
				addLayer (HIDDEN, curHiddenNeurons);
			}
			String outputString = splitted[splitted.length-1];
			setOutputParams(outputString);
			addLayer(OUTPUT, outputParams.length);
		}
	}
	
	private Layer getLastLayer () {
		if (allLayers.size() > 0)
			return allLayers.get(allLayers.size()-1);
		return null;
	}
	
	private void addLayer (int type, int numNeurons) {
		Layer layer = new Layer();
		Layer lastLayer = getLastLayer();
		for (int curNeuronNum=0; curNeuronNum < numNeurons; curNeuronNum++) {
			Neuron curNeuron = new Neuron();
			if (lastLayer != null) {
				for (int curSynNum=0; curSynNum < lastLayer.getNeuronCount(); curSynNum++) {
					Synapse curSynapse = new Synapse(lastLayer.getNeuron(curSynNum));
					curNeuron.addSynapse(curSynapse);
				}
			}
			if (type == OUTPUT) {
				if (classifyOutputs) {
					if (numNeurons == 1)
						curNeuron.setApplyFormula(Neuron.SIGMOID);
					else
						curNeuron.setApplyFormula(Neuron.SOFTMAX);
				} else {
					curNeuron.setApplyFormula(Neuron.LINEAR);
				}
			} else if (type == HIDDEN)
				curNeuron.setApplyFormula(Neuron.SIGMOID);
			curNeuron.setActiveLayer(layer);
			layer.addNeuron(curNeuron);
		}
		allLayers.add(layer);
	}
		
	private void setInputParams (String s) {
		// Remove normalization symbols
		s = s.replaceAll("@", "");
		String[] splitted = s.split(",");
		this.inputParams = splitted;
	}

	private void setOutputParams (String s) {
		if (s.indexOf("!") > -1)
			classifyOutputs = true;
		else
			classifyOutputs = false;
		// Remove classification symbol '!'
		s = s.replaceAll("!", "");
		// Remove normalization symbols
		s = s.replaceAll("@", "");
		String[] splitted = s.split(",");
		this.outputParams = splitted;
	}

	
	private int getTypeForHeader (String s) {
		if (s.equals(("#input normalization")))
			return INPUT_NORM;
		else if (s.equals(("#output normalization")))
			return OUTPUT_NORM;
		else if (s.equals(("#neurons weights")))
			return NEURONS_WEIGHT;
		else if (s.equals(("#synapses weights")))
			return SYNAPSES_WEIGHT;
		else
			return INVALID;
	}

	
	public void loadWeights (String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			int num = 0;
			int whatToRead = INVALID;
			while (br.ready()) {
				String line = br.readLine();
				// Trim
				line = line.trim();
				if (line.indexOf("#") == 0) {
//					if (whatToRead != INVALID)
//						System.out.println ("Read "+num+" lines for type "+whatToRead);
					num = 0;
					whatToRead = getTypeForHeader(line);
				}
				else if (whatToRead != INVALID) {
					switch (whatToRead) {
					case INPUT_NORM:
						getInputLayer().setNormalization(num, line);
						break;
					case OUTPUT_NORM:
						getOutputLayer().setNormalization(num, line);
						break;
					case NEURONS_WEIGHT:
						setNeuronWeight (num, line);
						break;
					case SYNAPSES_WEIGHT:
						setSynapseWeight (num, line);
						break;
					}
					num++;
				}
			}
//			if (whatToRead != INVALID)
//				System.out.println ("Read "+num+" lines for type "+whatToRead);
		} catch (FileNotFoundException e) {
			System.out.println ("File not found: "+filename);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void setNeuronWeight (int no, String s) {
		double weight = Double.parseDouble(s);
		setNeuronWeight(no, weight);
	}
	
	private void setNeuronWeight (int no, double weight) {
		ArrayList<?> neuronList = getNeuronList();
		((Neuron)neuronList.get(no)).setWeight(weight);
	}

	private void setSynapseWeight (int no, String s) {
		double weight = Double.parseDouble(s);
		setSynapseWeight (no, weight);
	}

	private void setSynapseWeight (int no, double weight) {
		ArrayList<?> synapseList = getSynapseList();
		((Synapse)synapseList.get(no)).setWeight(weight);
	}
	
	private ArrayList<Neuron> getNeuronList () {
		ArrayList<Neuron> retList = new ArrayList<Neuron>();
		for (int i=0; i < getLayerCount(); i++) {
			retList.addAll (getLayer(i).getNeuronList());
		}
		return retList;
	}

	private ArrayList<Synapse> getSynapseList () {
		ArrayList<Synapse> retList = new ArrayList<Synapse>();
		for (int i=0; i < getLayerCount(); i++) {
			retList.addAll (getLayer(i).getSynapseList());
		}
		return retList;
	}

	private Layer getInputLayer () {
		return allLayers.get(0);
	}

	private Layer getOutputLayer () {
		return allLayers.get(allLayers.size()-1);
	}

	private int getHiddenLayerCount () {
		return allLayers.size() -2;
	}

	private int getNeuronCount () {
		return getNeuronList().size();
	}

	private int getSynapseCount () {
		return getSynapseList().size();
	}

	private int getLayerCount () {
		return allLayers.size();
	}
	
	private Layer getLayer (int i) {
		return allLayers.get(i);
	}

	/**
	 * Dump this network
	 */
	@Override
	public String toString() {
		String retVal = "";
		retVal += "Network Dump\n";
		retVal += "# Layers: " + allLayers.size()+ "\n";
		retVal += "# Neurons: " + getNeuronCount()+ "\n";
		retVal += "# Synapses: " + getSynapseCount()+ "\n";
		retVal += "Structure:\n";
		retVal += "\t" + structure + "\n";
		retVal += "InputParams:\n";
		for (int i=0; i<inputParams.length; i++)
			retVal += "\t" + i + "\t" + inputParams[i] + "\n";
		retVal += "OutputParams:" + "\n";
		for (int i=0; i<outputParams.length; i++)
			retVal += "\t" + i + "\t" + outputParams[i] + "\n";
		retVal += "#Hidden Layers: "+getHiddenLayerCount() + "\n";
		for (int i=0; i<getLayerCount(); i++)
			retVal += "\t" + i + "\t" + getLayer(i).toString() + "\n";
		return retVal;
	}
}
