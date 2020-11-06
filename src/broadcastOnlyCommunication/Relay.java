package broadcastOnlyCommunication;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.collections.Pair;

public class Relay {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private Map<String, Integer> frontier = new HashMap<String, Integer>();
	private PriorityBlockingQueue<Pair<Perturbation, Integer>> waitingQueue = new PriorityBlockingQueue<Pair<Perturbation, Integer>>(
			10, new WaitingPerturbationComparator());

	public Relay(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}

	public void OnSense(Perturbation p, Integer missingTicks) {
		this.waitingQueue.add(new Pair<Perturbation, Integer>(p, missingTicks));
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void ProcessPerturbations() {	
		for (Pair<Perturbation, Integer> el : this.waitingQueue) {
			// decrease the missing ticks 
			el.setSecond(el.getSecond() - 1);
			
			if(el.getSecond().equals(0)) { // the perturbation is just arrived
				this.waitingQueue.remove(el);
				
				Perturbation p = el.getFirst();
				GridPoint pt = grid.getLocation(this);
				Double tick = RepastEssentials.GetTickCount();
				System.out.println(tick + " -- Relay " + pt.getX() + " - " + pt.getY() + ": " + p.val);
				
				if (!frontier.containsKey(p.src)) {
					frontier.put(p.src, 0);
				}

				if (frontier.get(p.src).equals(p.ref)) {
					ForwardPerturbation(p);
					frontier.put(p.src, NextRef(p));
					// observer notification of P.val goes here
					
				}
			}
		}
	}

	public void ForwardPerturbation(Perturbation p) {
		//TODO: forward the perturbation
	}

	public Integer NextRef(Perturbation p) {
		return p.ref + 1; // for sequence numbers
		// return hash(P) // for hash chaining
	}

	class WaitingPerturbationComparator implements Comparator<Pair<Perturbation, Integer>> {
		public int compare(Pair<Perturbation, Integer> s1, Pair<Perturbation, Integer> s2) {
			return s1.getSecond().compareTo(s2.getSecond());
		}
	}
}
