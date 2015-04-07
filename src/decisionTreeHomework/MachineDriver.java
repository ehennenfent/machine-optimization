package decisionTreeHomework;

public class MachineDriver {

	public MachineDriver() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		IOHelper<Data<Homework9Event>> foobar = new IOHelper<Data<Homework9Event>>();
		Data<Homework9Event> datum = foobar.read("data/decisionTreeData.dat");
		Data<Homework9Event> signal = foobar.read("data/signalBig.dat");
		Data<Homework9Event> background = foobar.read("data/backgroundBig.dat");
		
		DecisionTree<Homework9Event> tree = new DecisionTree<Homework9Event>();
		tree.train(signal, background);
		int counter = 0;
		for(Homework9Event e : datum.getEvents()){
			System.out.println(counter + "\t" + tree.runEvent(e));
			counter++;
		}

	}

}
