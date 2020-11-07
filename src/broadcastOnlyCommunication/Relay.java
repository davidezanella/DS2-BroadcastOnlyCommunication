package broadcastOnlyCommunication;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public abstract class Relay {
	protected ContinuousSpace<Object> space;
	protected Grid<Object> grid;
	private List<PendingPerturbation> pendingPerturbations = new ArrayList<>();

	public Relay(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}

	public void onSense(Perturbation p, int missingTicks) {
		this.pendingPerturbations.add(new PendingPerturbation(missingTicks, p));
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void processPerturbations() {
		var perturbationsToProcess = new ArrayList<Perturbation>();
		for (var it = this.pendingPerturbations.iterator(); it.hasNext();) {
			var pendingPerturbation = it.next();

			pendingPerturbation.onTick();

			if (pendingPerturbation.isTimeToProcess()) {
				it.remove();
				perturbationsToProcess.add(pendingPerturbation.perturbation);
			}
		}
		perturbationsToProcess.forEach(this::processPerturbation);
	}

	public abstract void processPerturbation(Perturbation p);

	public void forwardPerturbation(Perturbation p) {
		GridPoint pt = grid.getLocation(this);

		// get all the Relays in the grid
		int extentX = grid.getDimensions().getWidth() / 2;
		int extentY = grid.getDimensions().getHeight() / 2;
		GridCellNgh<Relay> nghCreator = new GridCellNgh<Relay>(grid, pt, Relay.class, extentX, extentY);
		List<GridCell<Relay>> gridCells = nghCreator.getNeighborhood(true);

		for (GridCell<Relay> cell : gridCells) {
			for (Relay relay : cell.items()) {
				double distance = Utils.distanceBetweenPoints(pt, grid.getLocation(relay));
				int missingTicks = (int) Math.ceil(distance);
				relay.onSense(p, missingTicks);
			}
		}
	}

	public int nextRef(Perturbation p) {
		return p.ref + 1; // for sequence numbers
		// return hash(P) // for hash chaining
	}
}
