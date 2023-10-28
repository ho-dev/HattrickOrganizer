package core.epv;

class Synapse {
	private double weight = 0;
	Neuron fromNeuron;
	
	Synapse (Neuron fromNeuron) {
		this.fromNeuron = fromNeuron;
	}
	
	void setWeight (double weight) {
		this.weight = weight;
	}

	double getWeight () {
		return this.weight;
	}
	
	Neuron getFromNeuron() {
		return fromNeuron;
	}

	@Override
	public String toString () {
		return "[SynWeight="+weight+"]";
	}
}
