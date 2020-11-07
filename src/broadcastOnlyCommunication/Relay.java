package broadcastOnlyCommunication;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.collections.Pair;

public abstract class Relay {
	protected ContinuousSpace<Object> space;
	protected Grid<Object> grid;
	private List<Pair<Perturbation, Integer>> waitingList = new ArrayList<Pair<Perturbation, Integer>>();

	public Relay(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}

	public void onSense(Perturbation p, Integer missingTicks) {
		this.waitingList.add(new Pair<Perturbation, Integer>(p, missingTicks));
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void processPerturbations() {
		for (Iterator<Pair<Perturbation, Integer>> it = this.waitingList.iterator(); it.hasNext();) {
			Pair<Perturbation, Integer> el = it.next();

			// decrease the missing ticks
			el.setSecond(el.getSecond() - 1);

			if (el.getSecond().equals(0)) { // the perturbation is just arrived
				it.remove();

				Perturbation p = el.getFirst();
				processPerturbation(p);
			}
		}
	}

	public abstract void processPerturbation(Perturbation p);

	public void forwardPerturbation(Perturbation p) {
		GridPoint pt = grid.getLocation(this);

		// get all the Relays in the grid
		Integer extentX = grid.getDimensions().getWidth() / 2;
		Integer extentY = grid.getDimensions().getHeight() / 2;
		GridCellNgh<Relay> nghCreator = new GridCellNgh<Relay>(grid, pt, Relay.class, extentX, extentY);
		List<GridCell<Relay>> gridCells = nghCreator.getNeighborhood(true);

		for (GridCell<Relay> cell : gridCells) {
			for (Relay relay : cell.items()) {
				Double distance = Utils.distanceBetweenPoints(pt, grid.getLocation(relay));
				Integer missingTicks = (int) Math.ceil(distance);
				relay.onSense(p, missingTicks);
			}
		}
	}

	public Integer nextRef(Perturbation p) {
		return p.ref + 1; // for sequence numbers
		// return hash(P) // for hash chaining
	}
}
