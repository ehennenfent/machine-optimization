package decisionTreeHomework;

import java.util.List;

public class Leaf<Type extends Event> {

	private Leaf<Type> output1 = null;
	private Leaf<Type> output2 = null;
	private double split;
	private int variable;
	
	private double nBackground;
	private double nSignal;
	
	public Leaf() {
		this(0, 0);
	}
	
	public Leaf(int variable, double split) {
		this.variable = variable;
		this.split = split;
	}
	
	public boolean isFinal() {
		return output1 == null || output2 == null;
	}
	
	public double getPurity() {
		return nSignal / (nSignal + nBackground);
	}
	
	public Leaf<Type> getLeftBranch() {
		return output1;
	}
	
	public Leaf<Type> getRightBranch() {
		return output2;
	}
	
	public Leaf<Type> runEvent(Type event) {
		if (isFinal()) {
			return null;
		}
		
		if (event.getVars()[variable] <= split) {
			return output1;
		} else {
			return output2;
		}
	}
	
	// Used only in level one
	public int checkEvent(Type event) {
		if (event.getVars()[variable] <= split) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public void train(Data<Type> signal, Data<Type> background) {
		nSignal = getWeightedSize(signal.getEvents());
		nBackground = getWeightedSize(background.getEvents());
		
		boolean branch = chooseVariable(signal, background);
	
		if (branch) {
			output1 = new Leaf<Type>();
			output2 = new Leaf<Type>();
			
			Data<Type> signalLeft = new Data<>();
			Data<Type> signalRight = new Data<>();
			Data<Type> backgroundLeft = new Data<>();
			Data<Type> backgroundRight = new Data<>();
			
			for (Type event : signal.getEvents()) {
				if (runEvent(event) == output1) {
					signalLeft.addEvent(event);
				} else {
					signalRight.addEvent(event);
				}
			}
			
			for (Type event : background.getEvents()) {
				if (runEvent(event) == output1) {
					backgroundLeft.addEvent(event);
				} else {
					backgroundRight.addEvent(event);
				}
			}
			
			output1.train(signalLeft, backgroundLeft);
			output2.train(signalRight, backgroundRight);
		}		
	}
	
	private boolean chooseVariable(Data<Type> signal, Data<Type> background) {
		/* For all the variables in each set, figure out which one has the most significant difference.
		 * If that difference is greater than some percentage, store it in variable and make 
		 * split the average of the two averages, then return true. If not, mark this leaf as final
		 * and return false.
		 */
		boolean ret = true;
		
		Event sigEvent = signal.getEvents().get(0);
		Event noiseEvent = background.getEvents().get(0);
		
		double[] sigAverages = new double[sigEvent.getNVars()];
		double[] bkgAverages = new double[noiseEvent.getNVars()];
		
		//Find the weighted sum of each variable over all the events.
		for(Event e : signal.getEvents()){
			for(int i = 0; i < e.getNVars(); i++){
				sigAverages[i] += e.getVars()[i]*e.getWeight();
			}
		}
		for(Event e : background.getEvents()){
			for(int i = 0; i < e.getNVars(); i++){
				bkgAverages[i] += e.getVars()[i]*e.getWeight();
			}
		}
		// Divide the weighted sums by the weighted size.
		for(int i = 0; i < sigAverages.length; i++){
			sigAverages[i] /= getWeightedSize(signal.getEvents());
		}
		for(int i = 0; i < bkgAverages.length; i++){
			bkgAverages[i] /= getWeightedSize(background.getEvents());
		}
		// Find the percent difference between the average value of each variable.
		double[] percentDifs = new double[sigAverages.length];
		for(int i = 0; i < percentDifs.length; i++){
			double difference = Math.max(sigAverages[i], bkgAverages[i]) - Math.min(sigAverages[i], bkgAverages[i]);
			double average = (sigAverages[i] + bkgAverages[i]) / 2;
			percentDifs[i]  = difference / average;
		}
		// Find the most significant percent difference.
		double maxdif = 0;
		for(int i = 0; i < percentDifs.length; i++){
			if(percentDifs[i] > maxdif){
				maxdif = percentDifs[i];
				variable = i;
				split = (sigAverages[i] + bkgAverages[i]) / 2;
			}
		}
		
		// If, after picking a variable, it doesn't sort either the signal or background very well, we return false
		// and say this is the final leaf.
		int greater = 0;
		int less = 0;
		for(Event e : signal.getEvents()){
			if(e.getVars()[variable] > split)
				greater++;
			else
				less++;
		}
		if((double) Math.min(greater, less) / (double) (greater + less) < .05)
			ret = false;
		greater = 0;
		less = 0;
		for(Event e : background.getEvents()){
			if(e.getVars()[variable] > split)
				greater++;
			else
				less++;
		}
		// If the fraction of events sorted is less than 5%
		if((double) Math.min(greater, less) / (double) (greater + less) < .05)
			ret = false;
		
		return ret;
	}
	
	// Private methods will stay private. Wouldn't want a monk to cut me open.
	public void levelOneChooseVariable(Data<Type> signal, Data<Type> background){
		chooseVariable(signal, background);
	}
	
	private double getWeightedSize(List<Type> events){
		double sum = 0;
		for (Type e : events)
			sum+= e.getWeight();
		return sum;
	}
	
	

}
