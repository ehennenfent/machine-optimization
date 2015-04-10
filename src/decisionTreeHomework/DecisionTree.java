package decisionTreeHomework;

public class DecisionTree<Type extends Event> {
	
	private Leaf<Type> headnode = new Leaf<>();
	private double weight;
	private Data<Type> badSignal = new Data<Type>();
	private Data<Type> badBackground = new Data<Type>();

	public void train(Data<Type> signal, Data<Type> background) {
		headnode.train(signal, background);
		
		// Find all the poorly classified signal and background events
		for(Type e : signal.getEvents()){
			if(runEvent(e) < .5){
				badSignal.addEvent(e);
			}
		}
		for(Type e : background.getEvents()){
			if(runEvent(e) > .5){
				badBackground.addEvent(e);
			}
		}
		// Calculate the new weight of this tree and the misclassified events
		double r = (double) (badBackground.getEvents().size() + badSignal.getEvents().size()) /
				(double) (signal.getEvents().size() + background.getEvents().size());
		weight = (1d - r) / r;
	}
	
	// Updates the weights on events passed to it
	public Data<Type> setBackgroundWeights(Data<Type> events){
		for(Type e : events.getEvents()){
			if(runEvent(e) > .5){
				e.setWeight(weight);
			}
		}
		return events;
	}
	
	public Data<Type> setSignalWeights(Data<Type> events){
		for(Type e : events.getEvents()){
			if(runEvent(e) < .5){
				e.setWeight(weight);
			}
		}
		return events;
	}
	
	public double runEvent(Type event) {
		Leaf<Type> leaf = headnode;
		
		while (!leaf.isFinal()) {
			leaf = leaf.runEvent(event);
		}
		
		return leaf.getPurity();
	}
	
	public double getWeight() {
		return weight;
	}

	public Data<Type> getBadSignal() {
		return badSignal;
	}

	public Data<Type> getBadBackground() {
		return badBackground;
	}
	
	public boolean hasBadEvents(){
		if(badSignal.getEvents().size() == 0 || badBackground.getEvents().size() == 0)
			return false;
		return true;
	}
}
