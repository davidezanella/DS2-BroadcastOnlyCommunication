package broadcastOnlyCommunication;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public abstract class Relay {
	protected ContinuousSpace<Object> space;
	protected Grid<Object> grid;
	private List<PendingPerturbation> pendingPerturbations = new ArrayList<>();
	private List<Perturbation> processedPerturbations = new ArrayList<>(); // used for log purposes
	private String id;

	public Relay(ContinuousSpace<Object> space, Grid<Object> grid, String id) {
		this.space = space;
		this.grid = grid;
		this.id = id;
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
				processedPerturbations.add(pendingPerturbation.perturbation);
			}
		}
		perturbationsToProcess.forEach(this::processPerturbation);
	}

	public abstract void processPerturbation(Perturbation p);

	public void forwardPerturbation(Perturbation p) {
		GridPoint pt = grid.getLocation(this);

		var relays = Utils.getAllRelaysInGrid(grid, pt);

		for (var relay : relays) {
			double distance = Utils.distanceBetweenPoints(pt, grid.getLocation(relay));
			int missingTicks = (int) Math.ceil(distance);
			relay.onSense(p, missingTicks);
		}
	}

	public int nextRef(Perturbation p) {
		return p.ref + 1; // for sequence numbers
		// return hash(P) // for hash chaining
	}
	
	public String getRelayId() {
		return this.id;
	}
	
	public String getArrivedPerturbations() {
		var arrived = new ArrayList<String>();
		for(var pert : processedPerturbations) {
			arrived.add(pert.src + " " + pert.ref);
		}
		
		processedPerturbations.clear();
		
		return String.join(", ", arrived);
	}
}
