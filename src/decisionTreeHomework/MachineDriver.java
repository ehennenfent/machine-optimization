package decisionTreeHomework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MachineDriver {

	public MachineDriver() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
//		levelOne();
//		levelTwo();
		levelThree();
	}
	
	public static void levelOne(){
		IOHelper<Data<Homework9Event>> foobar = new IOHelper<Data<Homework9Event>>();
		Data<Homework9Event> datum = foobar.read("data/decisionTreeData.dat");
		Data<Homework9Event> signal = foobar.read("data/signalBig.dat");
		Data<Homework9Event> background = foobar.read("data/backgroundBig.dat");
		for(Event e : signal.getEvents())
			e.setWeight(1d);
		for(Event e : background.getEvents())
			e.setWeight(1d);
		Leaf<Homework9Event> decision = new Leaf<Homework9Event>(); // We'll just use the chooseVariable method from a leaf here.
		decision.levelOneChooseVariable(signal, background);

		int counter = 0;
		for(Homework9Event e : datum.getEvents()){
			e.setWeight(1d);
			System.out.println(counter + "\t" + decision.checkEvent(e));
			counter++;
		}
	}
	
	public static void levelTwo(){
		IOHelper<Data<Homework9Event>> foobar = new IOHelper<Data<Homework9Event>>();
		Data<Homework9Event> datum = foobar.read("data/decisionTreeData.dat");
		Data<Homework9Event> signal = foobar.read("data/signalBig.dat");
		Data<Homework9Event> background = foobar.read("data/backgroundBig.dat");
		for(Event e : signal.getEvents())
			e.setWeight(1d);
		for(Event e : background.getEvents())
			e.setWeight(1d);
		DecisionTree<Homework9Event> tree = new DecisionTree<Homework9Event>();
		tree.train(signal, background);
		int counter = 0;
		for(Homework9Event e : datum.getEvents()){
			System.out.println(counter + "\t" + tree.runEvent(e));
			counter++;
		}
	}
	
	public static void levelThree(){
		IOHelper<Data<Homework9Event>> foobar = new IOHelper<Data<Homework9Event>>();
		Data<Homework9Event> datum = foobar.read("data/decisionTreeData.dat");
		Data<Homework9Event> signal = foobar.read("data/signalBig.dat");
		for(Event e : signal.getEvents())
			e.setWeight(1d);
		Data<Homework9Event> background = foobar.read("data/backgroundBig.dat");
		for(Event e : background.getEvents())
			e.setWeight(1d);
		List<DecisionTree<Homework9Event>> orchard = new ArrayList<DecisionTree<Homework9Event>>();
		
		DecisionTree<Homework9Event> tree = new DecisionTree<Homework9Event>();
		tree.train(signal, background);
		orchard.add(tree);
		int treeCounter = 0;
		// Keep creating more trees as long as there are misclassified events. Let's not get out of hand though.
		while(orchard.get(treeCounter).hasBadEvents() && treeCounter < 10){
			DecisionTree<Homework9Event> oldTree = orchard.get(treeCounter);
			DecisionTree<Homework9Event> newTree = new DecisionTree<Homework9Event>();
//			newTree.train(oldTree.getBadSignal(), oldTree.getBadBackground());
			newTree.train(oldTree.setSignalWeights(signal), oldTree.setBackgroundWeights(background));
			orchard.add(newTree);
			treeCounter++;
		}
		orchard.remove(orchard.size() - 1);
		
		List<Double> results = new ArrayList<Double>();
		for(Homework9Event e : datum.getEvents()){
			results.add(eventClassification(e,orchard));
		}
		double scalar = 1/(Collections.max(results) - Collections.min(results));
		int eventCounter = 0;
		for(Double d : results){
			d *= scalar; // Scale our results from 0 to 1.
			System.out.println(eventCounter + "\t" + d);
			eventCounter++;
		}
		
	}
	
	/**
	 * Classifies an event by taking the weighted sum of the results throughout the forest.
	 * @param event
	 * @param forest
	 * @return
	 */
	public static double eventClassification(Homework9Event event, List<DecisionTree<Homework9Event>> forest){
		double sum = 0;
		for(DecisionTree<Homework9Event> tree : forest){
			sum += (Math.log(tree.getWeight()) * tree.runEvent(event));
		}
		return sum;
	}

}
