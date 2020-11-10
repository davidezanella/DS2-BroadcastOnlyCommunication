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
	private List<Perturbation> processedPerturbations = new ArrayList<>(); // used for log purposes
	private String id;

	public Relay(ContinuousSpace<Object> space, Grid<Object> grid, String id) {
		this.space = space;
		this.grid = grid;
		this.id = id;
	}

	public void onSense(Perturbation p) {
		this.processedPerturbations.add(p);
		processPerturbation(p);
	}
	
	public abstract void processPerturbation(Perturbation p);

	public void forwardPerturbation(Perturbation p) {
		Utils.createNewPerturbation(space, grid, p.senderId, p.ref, p.val, this);
	}

	public static int nextRef(Perturbation p) {
		return p.ref + 1; // for sequence numbers
		// return hash(P) // for hash chaining
	}

	public String getRelayId() {
		return this.id;
	}

	public String getArrivedPerturbations() {
		var arrived = new ArrayList<String>();
		for (var pert : processedPerturbations) {
			arrived.add(pert.senderId + " " + pert.ref);
		}

		processedPerturbations.clear();

		return String.join(", ", arrived);
	}

	@Override
	public String toString() {
		return "Relay [id=" + id + "]";
	}
	
	public String getId() {
		return id;
	}
	
}
