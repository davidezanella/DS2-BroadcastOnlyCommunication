package broadcastOnlyCommunication;

import java.util.HashMap;
import java.util.Map;

import repast.simphony.essentials.RepastEssentials;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class RelayI extends Relay {
	private Map<String, Integer> frontier = new HashMap<String, Integer>();

	public RelayI(ContinuousSpace<Object> space, Grid<Object> grid) {
		super(space, grid);
	}

	public void processPerturbation(Perturbation p) {
		GridPoint pt = grid.getLocation(this);
		Double tick = RepastEssentials.GetTickCount();
		System.out.println(tick + " -- Relay " + pt.getX() + " - " + pt.getY() + ": " + p.val);

		if (!frontier.containsKey(p.src)) {
			frontier.put(p.src, 0);
		}

		if (frontier.get(p.src).equals(p.ref)) {
			forwardPerturbation(p);
			frontier.put(p.src, nextRef(p));
			// observer notification of P.val goes here

		}
	}
}
