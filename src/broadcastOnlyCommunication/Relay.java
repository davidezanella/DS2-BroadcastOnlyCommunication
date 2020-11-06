package broadcastOnlyCommunication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.collections.Pair;

public abstract class Relay {
	protected ContinuousSpace<Object> space;
	protected Grid<Object> grid;
	private List<Pair<Perturbation, Integer>> waitingList = new ArrayList<Pair<Perturbation, Integer>>();

	public Relay(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}

	public void OnSense(Perturbation p, Integer missingTicks) {
		this.waitingList.add(new Pair<Perturbation, Integer>(p, missingTicks));
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void ProcessPerturbations() {	
		for (Iterator<Pair<Perturbation, Integer>> it = this.waitingList.iterator(); it.hasNext(); ) {
			Pair<Perturbation, Integer> el = it.next();
			
			// decrease the missing ticks 
			el.setSecond(el.getSecond() - 1);
			
			if(el.getSecond().equals(0)) { // the perturbation is just arrived
		        it.remove();
				
				Perturbation p = el.getFirst();
				ProcessPerturbation(p);
			}
		}
	}
	
	public abstract void ProcessPerturbation(Perturbation p);

	public void ForwardPerturbation(Perturbation p) {
		//TODO: forward the perturbation
	}

	public Integer NextRef(Perturbation p) {
		return p.ref + 1; // for sequence numbers
		// return hash(P) // for hash chaining
	}
}
