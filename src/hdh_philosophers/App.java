package hdh_philosophers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App {
	List<Chopstick> chopsticks;
	Random stateRandom ;
	public static void main(String[] args) {
		new App();

	}
	public App() {
		this.chopsticks = new ArrayList<Chopstick>();
		for(int i=0;i<5;i++) {
			chopsticks.add(new Chopstick(i));
		}
		for(int j=0; j<4; j++) {
			new Philosophers(j, chopsticks.get(j+1), chopsticks.get(j),0).start();
			
		}
		
		new Philosophers(4, chopsticks.get(0), chopsticks.get(4), 0).start();
		
		
	}

}
