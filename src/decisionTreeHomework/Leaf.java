package decisionTreeHomework;

public class Leaf<Type extends Event> {

	private Leaf<Type> output1 = null;
	private Leaf<Type> output2 = null;
	private double split;
	private int variable;
	
	private int nBackground;
	private int nSignal;
	
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
		return (double) nSignal / (double)(nSignal + nBackground);
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
	
	public void train(Data<Type> signal, Data<Type> background) {
		nSignal = signal.getEvents().size();
		nBackground = background.getEvents().size();
		
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
		// TODO set the values of variable and split here		
		// Return true if you were able to find a useful variable, and false if you were not and want to stop calculation here
		
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
		
		for(Event e : signal.getEvents()){
			for(int i = 0; i < e.getNVars(); i++){
				sigAverages[i] += e.getVars()[i];
			}
		}
		for(Event e : background.getEvents()){
			for(int i = 0; i < e.getNVars(); i++){
				bkgAverages[i] += e.getVars()[i];
			}
		}
		
		for(int i = 0; i < sigAverages.length; i++){
			sigAverages[i] /= signal.getEvents().size();
		}
		for(int i = 0; i < bkgAverages.length; i++){
			bkgAverages[i] /= background.getEvents().size();
		}
		
		double[] percentDifs = new double[sigAverages.length];
		for(int i = 0; i < percentDifs.length; i++){
			double difference = Math.max(sigAverages[i], bkgAverages[i]) - Math.min(sigAverages[i], bkgAverages[i]);
			double average = (sigAverages[i] + bkgAverages[i]) / 2;
			percentDifs[i]  = difference / average;
		}
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
//		System.out.println(Math.abs( ((double) (greater - less) / (double)((double)(greater + less)/2) ) - .5));
//		System.out.println((double) Math.min(greater, less) / (double) (greater + less));
		if((double) Math.min(greater, less) / (double) (greater + less) < .05)
			ret = false;
//		System.out.println(greater + " | " + less);
		greater = 0;
		less = 0;
		for(Event e : background.getEvents()){
			if(e.getVars()[variable] > split)
				greater++;
			else
				less++;
		}
		if((double) Math.min(greater, less) / (double) (greater + less) < .05)
			ret = false;
		
//		System.out.println(greater + " " + less);
		
//		System.out.println(getPurity());
		
		return ret;
	}

}
