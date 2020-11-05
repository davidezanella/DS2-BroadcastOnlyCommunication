package broadcastOnlyCommunication;

import java.util.HashMap;
import java.util.Map;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Relay {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private Map<String, Integer> frontier = new HashMap<String, Integer>();

	public Relay(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}

	public void OnSense(Perturbation p) {
		if(!frontier.containsKey(p.src)) {
			frontier.put(p.src, 0);
		}
		
		if (frontier.get(p.src).equals(p.ref)) {
			ForwardPerturbation(p);
			frontier.put(p.src, NextRef(p));
			//  observer  notification  of P.val  goes  here	
		}
	}
	
	public void ForwardPerturbation(Perturbation p) {
		
	}
	
	public Integer NextRef(Perturbation p) {
		return p.ref + 1;     // for  sequence  numbers
		//return  hash(P)       // for  hash  chaining
	}
}
